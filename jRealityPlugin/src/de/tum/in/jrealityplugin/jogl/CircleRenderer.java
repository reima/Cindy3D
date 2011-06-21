package de.tum.in.jrealityplugin.jogl;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.apache.commons.math.geometry.Rotation;
import org.apache.commons.math.geometry.Vector3D;
import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealMatrix;

import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

public class CircleRenderer extends PrimitiveRenderer<Circle> {
	private ShaderProgram program = null;
	private int centerLoc;
	private int radiusSqLoc;
	private int normalLoc;
	private int colorLoc;
	private int transformLoc;

	@Override
	public boolean init(GL gl) {
		GL2 gl2 = gl.getGL2();

		program = new ShaderProgram();
		ShaderCode vertexShader = Util.loadShader(GL2.GL_VERTEX_SHADER,
				"circle.vert");
		if (!vertexShader.compile(gl2))
			return false;
		ShaderCode fragmentShader = Util.loadShader(GL2.GL_FRAGMENT_SHADER,
				"circle.frag");
		if (!fragmentShader.compile(gl2))
			return false;

		if (!program.add(vertexShader))
			return false;
		if (!program.add(fragmentShader))
			return false;
		if (!program.link(gl.getGL2(), null))
			return false;

		transformLoc = gl2.glGetUniformLocation(program.program(),
				"circleTransform");
		centerLoc = gl2.glGetUniformLocation(program.program(), "circleCenter");
		radiusSqLoc = gl2.glGetUniformLocation(program.program(),
				"circleRadiusSq");
		normalLoc = gl2.glGetUniformLocation(program.program(), "circleNormal");
		colorLoc = gl2.glGetUniformLocation(program.program(), "circleColor");

		return true;
	}

	@Override
	public void dispose(GL gl) {
		if (program != null)
			program.destroy(gl.getGL2());
	}

	@Override
	public void postRender(JOGLRenderState jrs) {
		GL2 gl2 = jrs.gl.getGL2();
		gl2.glUseProgram(0);
	}

	@Override
	public void preRender(JOGLRenderState jrs) {
		GL2 gl2 = jrs.gl.getGL2();
		gl2.glUseProgram(program.program());
	}

	@Override
	protected void render(JOGLRenderState jrs, Circle circle) {
		GL2 gl2 = jrs.gl.getGL2();
		gl2.glUniform3f(centerLoc, (float) circle.centerX,
				(float) circle.centerY, (float) circle.centerZ);
		gl2.glUniform3f(normalLoc, (float) circle.normalX,
				(float) circle.normalY, (float) circle.normalZ);
		gl2.glUniform1f(radiusSqLoc, (float) (circle.radius * circle.radius));
		gl2.glUniform4fv(colorLoc, 1, circle.color.getComponents(null), 0);

		RealMatrix transform = MatrixUtils.createRealIdentityMatrix(4);
		transform.setColumn(3, new double[] { circle.centerX, circle.centerY,
				circle.centerZ, 1 });

		Rotation rotation = new Rotation(Vector3D.PLUS_K, new Vector3D(
				circle.normalX, circle.normalY, circle.normalZ));
		RealMatrix rotationMatrix = MatrixUtils.createRealIdentityMatrix(4);
		rotationMatrix.setSubMatrix(rotation.getMatrix(), 0, 0);

		RealMatrix scaleMatrix = MatrixUtils
				.createRealDiagonalMatrix(new double[] { circle.radius,
						circle.radius, circle.radius, 1 });

		transform = transform.multiply(rotationMatrix).multiply(scaleMatrix);

		gl2.glUniformMatrix4fv(transformLoc, 1, true, Util
				.matrixToFloatArray(transform), 0);

		// gl2.glFlush();
		gl2.glBegin(GL2.GL_QUADS);
			gl2.glVertex2f(-1, -1);
			gl2.glVertex2f(1, -1);
			gl2.glVertex2f(1, 1);
			gl2.glVertex2f(-1, 1);
		gl2.glEnd();
	}

}
