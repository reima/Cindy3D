package de.tum.in.jrealityplugin.jogl;

import java.util.Collection;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

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
	public void render(GL gl, Collection<Line> lines) {
		if (lines.isEmpty())
			return;

		GL2 gl2 = gl.getGL2();

		double mV[] = new double[16];
		gl2.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, mV, 0);
		Matrix4d modelView = new Matrix4d(mV);
		modelView.transpose();

		double pr[] = new double[16];
		gl2.glGetDoublev(GL2.GL_PROJECTION_MATRIX, pr, 0);
		Matrix4d invProjection = new Matrix4d(pr);
		invProjection.transpose();
		invProjection.invert();

		Vector4d[] f = new Vector4d[] { new Vector4d(-1, -1, 1, 1),
				new Vector4d(-1, -1, -1, 1), new Vector4d(-1, 1, -1, 1),
				new Vector4d(1, 1, -1, 1), new Vector4d(1, 1, 1, 1),
				new Vector4d(1, -1, 1, 1), };

		for (int i = 0; i < 6; ++i) {
			invProjection.transform(f[i]);
			f[i].scale(1.0 / f[i].w);
		}

		Vector3d[] frustumVertices = new Vector3d[6];
		for (int i = 0; i < 6; ++i)
			frustumVertices[i] = new Vector3d(f[i].x, f[i].y, f[i].z);

		Vector3d[] frustumNormals = new Vector3d[6];
		double[] frustumOrigin = new double[6];

		Vector3d v1 = new Vector3d(), v2 = new Vector3d();
		for (int i = 0; i < 6; ++i) {
			v1.sub(frustumVertices[(i + 1) % 6], frustumVertices[i]);
			v2.sub(frustumVertices[(i + 2) % 6], frustumVertices[i]);
			frustumNormals[i] = new Vector3d();
			frustumNormals[i].cross(v1, v2);
			frustumOrigin[i] = -frustumNormals[i].dot(frustumVertices[i]);
		}

		Point3d p1, p2;
		Vector3d direction = new Vector3d();
		double cylinderLength;

		gl2.glUseProgram(program.program());
		for (Line l : lines) {
			p1 = new Point3d(l.p1);
			p2 = new Point3d(l.p2);

			modelView.transform(p1);
			modelView.transform(p2);

			direction.sub(p2, p1);

			cylinderLength = direction.length();
			direction.scale(1.0 / cylinderLength);

			gl2.glUniform3f(originLoc, (float) p1.x, (float) p1.y,
							(float) p1.z);
			gl2.glUniform3f(directionLoc, (float) direction.x,
					(float) direction.y, (float) direction.z);

			if (l.lineType != LineType.SEGMENT) {
				double min = Double.MAX_VALUE;
				double max = Double.MIN_VALUE;
				for (int i = 0; i < 6; ++i) {
					double lambda = linePlaneIntersection(new Vector3d(p1),
							direction, frustumNormals[i], frustumOrigin[i]);
					if (lambda == Double.MAX_VALUE)
						continue;
					else {
						min = Math.min(min, lambda);
						max = Math.max(max, lambda);
					}
				}

				cylinderLength = 0;
				p2 = new Point3d(direction);
				p2.scale(max);
				p2.add(p1);
				if (l.lineType == LineType.LINE) {
					cylinderLength = -1;
					v1 = direction;
					v1.scale(min);
					p1.add(v1);
				}
			}

			double dist = Math.max(p1.distance(p2), 2.0 * l.radius) / 2.0;
			
			Vector3d axis = new Vector3d();
			axis.sub(p1, p2);
			
			p1.scale(0.5);
			p2.scale(0.5);
			
			Vector3d avg = new Vector3d();
			avg.add(p1, p2);

			Matrix4d cylinder = new Matrix4d();

			cylinder.set(avg);
			
			Matrix4d fromTo = new Matrix4d();
			
			fromTo.set(Util.rotateFromTo(new Vector3d(1,0,0), axis));
			
			Matrix4d scale = new Matrix4d();
			scale.m00 = dist;
			scale.m11 = l.radius;
			scale.m22 = l.radius;
			scale.m33 = 1;
			
			cylinder.mul(fromTo);
			cylinder.mul(scale);
			
			gl2.glUniformMatrix4fv(transformLoc, 1, true,
					Util.matrix4dToFloatArray(cylinder), 0);

			gl2.glUniform1f(lengthLoc, (float)cylinderLength);
			gl2.glUniform1f(radiusLoc, (float) l.radius);
			gl2.glUniform3fv(colorLoc, 1, l.color.getColorComponents(null), 0);
			// // gl2.glFlush();
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

	private double linePlaneIntersection(Vector3d p1, Vector3d direction,
			Vector3d normal, double distance) {

		double denom = direction.dot(normal);
		if (Math.abs(denom) < 10E-8)
			return Double.MAX_VALUE;
		
		double lambda = -(p1.dot(normal) + distance) / denom;
		return lambda;
	}

}
