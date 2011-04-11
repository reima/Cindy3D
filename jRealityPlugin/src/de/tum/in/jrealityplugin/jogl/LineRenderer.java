package de.tum.in.jrealityplugin.jogl;

import java.util.Arrays;
import java.util.Collection;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.apache.commons.math.geometry.Rotation;
import org.apache.commons.math.geometry.Vector3D;
import org.apache.commons.math.linear.LUDecompositionImpl;
import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;

import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

import de.tum.in.jrealityplugin.jogl.Line.LineType;

public class LineRenderer extends Renderer<Line> {

	private ShaderProgram program = null;

	private int transformLoc;
	private int originLoc;
	private int directionLoc;
	private int radiusLoc;
	private int colorLoc;
	private int lengthLoc;

	@Override
	public void dispose(GL gl) {
		if (program != null)
			program.destroy(gl.getGL2());
	}

	@Override
	public boolean init(GL gl) {
		GL2 gl2 = gl.getGL2();

		program = new ShaderProgram();
		ShaderCode vertexShader = loadShader(
				GL2.GL_VERTEX_SHADER,
				getClass()
						.getResource(
								"/de/tum/in/jrealityplugin/resources/shader/cylinder.vert"));
		if (!vertexShader.compile(gl2))
			return false;
		ShaderCode fragmentShader = loadShader(
				GL2.GL_FRAGMENT_SHADER,
				getClass()
						.getResource(
								"/de/tum/in/jrealityplugin/resources/shader/cylinder.frag"));
		if (!fragmentShader.compile(gl2))
			return false;

		if (!program.add(vertexShader))
			return false;
		if (!program.add(fragmentShader))
			return false;
		if (!program.link(gl.getGL2(), null))
			return false;

		transformLoc = gl2.glGetUniformLocation(program.program(),
				"cylinderTransform");
		originLoc = gl2
				.glGetUniformLocation(program.program(), "cylinderPoint");
		directionLoc = gl2.glGetUniformLocation(program.program(),
				"cylinderDirection");
		radiusLoc = gl2.glGetUniformLocation(program.program(),
				"cylinderRadius");
		colorLoc = gl2.glGetUniformLocation(program.program(), "cylinderColor");
		lengthLoc = gl2.glGetUniformLocation(program.program(),
				"cylinderLength");

		return true;
	}

	@Override
	public void render(JOGLRenderState jrs, Collection<Line> lines) {
		if (lines.isEmpty())
			return;

		GL2 gl2 = jrs.gl.getGL2();

		RealMatrix modelView = jrs.camera.getTransform();

		double pr[] = new double[16];
		gl2.glGetDoublev(GL2.GL_PROJECTION_MATRIX, pr, 0);
		RealMatrix projection = MatrixUtils.createRealMatrix(4, 4);
		projection.setColumn(0, Arrays.copyOfRange(pr, 0, 4));
		projection.setColumn(1, Arrays.copyOfRange(pr, 4, 8));
		projection.setColumn(2, Arrays.copyOfRange(pr, 8, 12));
		projection.setColumn(3, Arrays.copyOfRange(pr, 12, 16));
		RealMatrix invProjection = new LUDecompositionImpl(projection)
				.getSolver().getInverse();
		
		RealVector[] f = new RealVector[] {
				MatrixUtils.createRealVector(new double[] { -1, -1,  1, 1 }),
				MatrixUtils.createRealVector(new double[] { -1, -1, -1, 1 }),
				MatrixUtils.createRealVector(new double[] { -1,  1, -1, 1 }),
				MatrixUtils.createRealVector(new double[] {  1,  1, -1, 1 }),
				MatrixUtils.createRealVector(new double[] {  1,  1,  1, 1 }),
				MatrixUtils.createRealVector(new double[] {  1, -1,  1, 1 })
		};

		for (int i = 0; i < 6; ++i) {
			f[i] = invProjection.operate(f[i]);
			f[i].mapDivideToSelf(f[i].getEntry(3));
		}
		
		Vector3D[] frustumVertices = new Vector3D[6];
		for (int i = 0; i < 6; ++i) {
			frustumVertices[i] = new Vector3D(f[i].getEntry(0),
					f[i].getEntry(1), f[i].getEntry(2));
		}
		
		Vector3D[] frustumNormals = new Vector3D[6];
		double[] frustumOrigin = new double[6];

		for (int i = 0; i < 6; ++i) {
			Vector3D v1 = frustumVertices[(i + 1) % 6]
					.subtract(frustumVertices[i]);
			Vector3D v2 = frustumVertices[(i + 2) % 6]
					.subtract(frustumVertices[i]);
			frustumNormals[i] = Vector3D.crossProduct(v1, v2);
			frustumOrigin[i] = -Vector3D.dotProduct(frustumNormals[i],
					frustumVertices[i]);
		}

		gl2.glUseProgram(program.program());
		for (Line l : lines) {
			double[] tmp = modelView.operate(new double[] { l.p1.getX(),
					l.p1.getY(), l.p1.getZ(), 1 });
			Vector3D p1 = new Vector3D(tmp[0], tmp[1], tmp[2]);
			tmp = modelView.operate(new double[] { l.p2.getX(), l.p2.getY(),
					l.p2.getZ(), 1 });
			Vector3D p2 = new Vector3D(tmp[0], tmp[1], tmp[2]); 
			
			Vector3D direction = p2.subtract(p1);
			double cylinderLength = direction.getNorm();
			direction = direction.normalize();

			gl2.glUniform3f(originLoc, (float) p1.getX(), (float) p1.getY(),
					(float) p1.getZ());
			gl2.glUniform3f(directionLoc, (float) direction.getX(),
					(float) direction.getY(), (float) direction.getZ());

			if (l.lineType != LineType.SEGMENT) {
				double min = Double.MAX_VALUE;
				double max = Double.MIN_VALUE;
				for (int i = 0; i < 6; ++i) {
					double lambda = linePlaneIntersection(p1, direction,
							frustumNormals[i], frustumOrigin[i]);
					if (lambda == Double.MAX_VALUE)
						continue;
					else {
						min = Math.min(min, lambda);
						max = Math.max(max, lambda);
					}
				}

				cylinderLength = 0;
				p2 = new Vector3D(1, p1, max, direction);
				if (l.lineType == LineType.LINE) {
					cylinderLength = -1;
					p1 = p1.add(min, direction);
				}
			}

			double dist = Math.max(Vector3D.distance(p1, p2), 2.0 * l.radius) / 2.0;
			
			Vector3D axis = p1.subtract(p2);
			Vector3D avg = new Vector3D(0.5, p1, 0.5, p2);
			
			RealMatrix translationMatrix = MatrixUtils
					.createRealIdentityMatrix(4);
			translationMatrix.setColumn(3,
					new double[] { avg.getX(), avg.getY(), avg.getZ(), 1 });
			
			Rotation rotation = new Rotation(Vector3D.PLUS_I, axis);
			RealMatrix rotationMatrix = MatrixUtils.createRealIdentityMatrix(4);
			rotationMatrix.setSubMatrix(rotation.getMatrix(), 0, 0);
			
			RealMatrix scaleMatrix = MatrixUtils
					.createRealDiagonalMatrix(new double[] { dist, l.radius,
							l.radius, 1 });
			
			RealMatrix cylinder = translationMatrix.multiply(rotationMatrix)
					.multiply(scaleMatrix);
			
			gl2.glUniformMatrix4fv(transformLoc, 1, true,
					Util.matrixToFloatArray(cylinder), 0);

			gl2.glUniform1f(lengthLoc, (float) cylinderLength);
			gl2.glUniform1f(radiusLoc, (float) l.radius);
			gl2.glUniform3fv(colorLoc, 1, l.color.getColorComponents(null), 0);
			//gl2.glFlush();
			gl2.glBegin(GL2.GL_QUADS);
				gl2.glVertex3d(-1, -1, -1);
				gl2.glVertex3d(-1, -1, 1);
				gl2.glVertex3d(-1, 1, 1);
				gl2.glVertex3d(-1, 1, -1);
	
				gl2.glVertex3d(1, -1, 1);
				gl2.glVertex3d(1, -1, -1);
				gl2.glVertex3d(1, 1, -1);
				gl2.glVertex3d(1, 1, 1);
	
				gl2.glVertex3d(1, -1, 1);
				gl2.glVertex3d(-1, -1, 1);
				gl2.glVertex3d(-1, -1, -1);
				gl2.glVertex3d(1, -1, -1);
	
				gl2.glVertex3d(-1, 1, 1);
				gl2.glVertex3d(1, 1, 1);
				gl2.glVertex3d(1, 1, -1);
				gl2.glVertex3d(-1, 1, -1);
	
				gl2.glVertex3d(1, 1, 1);
				gl2.glVertex3d(-1, 1, 1);
				gl2.glVertex3d(-1, -1, 1);
				gl2.glVertex3d(1, -1, 1);
	
				gl2.glVertex3d(-1, -1, -1);
				gl2.glVertex3d(-1, 1, -1);
				gl2.glVertex3d(1, 1, -1);
				gl2.glVertex3d(1, -1, -1);
			gl2.glEnd();
		}
		gl2.glUseProgram(0);
	}

	private double linePlaneIntersection(Vector3D p1, Vector3D direction,
			Vector3D normal, double distance) {
		double denom = Vector3D.dotProduct(direction, normal);
		if (Math.abs(denom) < 10E-8)
			return Double.MAX_VALUE;
		
		double lambda = -(Vector3D.dotProduct(p1, normal) + distance) / denom;
		return lambda;
	}

}
