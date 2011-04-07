package de.tum.in.jrealityplugin.jogl;

import java.util.Collection;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import com.jogamp.opengl.util.PMVMatrix;
import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

import de.jreality.jogl.JOGLRenderer;
import de.jreality.jogl.shader.GlslShaderProgram;
import de.jreality.math.Matrix;
import de.jreality.math.MatrixBuilder;
import de.jreality.math.Rn;
import de.jreality.scene.Geometry;
import de.jreality.scene.PointSet;
import de.jreality.scene.data.Attribute;
import de.jreality.scene.data.DataList;
import de.jreality.scene.data.DoubleArray;

public class CircleRenderer extends Renderer<Circle> {
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
		ShaderCode vertexShader = loadShader(
				GL2.GL_VERTEX_SHADER,
				getClass()
						.getResource(
								"/de/tum/in/jrealityplugin/resources/shader/circle.vert"));
		if (!vertexShader.compile(gl2))
			return false;
		ShaderCode fragmentShader = loadShader(
				GL2.GL_FRAGMENT_SHADER,
				getClass()
						.getResource(
								"/de/tum/in/jrealityplugin/resources/shader/circle.frag"));
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
	public void render(GL gl, Collection<Circle> circles) {
		if (circles.isEmpty())
			return;

		GL2 gl2 = gl.getGL2();

		gl2.glUseProgram(program.program());
		for (Circle c : circles) {
			gl2.glUniform3f(centerLoc, (float) c.centerX, (float) c.centerY,
					(float) c.centerZ);
			gl2.glUniform3f(normalLoc, (float) c.normalX, (float) c.normalY,
					(float) c.normalZ);
			gl2.glUniform1f(radiusSqLoc, (float) (c.radius * c.radius));
			gl2.glUniform3fv(colorLoc, 1, c.color.getColorComponents(null), 0);

			Matrix4d transform = new Matrix4d();
			transform.set(new Vector3d(c.centerX, c.centerY, c.centerZ));
			Matrix4d rotation = new Matrix4d();
			rotation.set(Util.rotateFromTo(new Vector3d(0, 0, 1), new Vector3d(
					c.normalX, c.normalY, c.normalZ)));
			transform.mul(rotation);
			Matrix4d scale = new Matrix4d();
			scale.set(c.radius);
			transform.mul(scale);

			gl2.glUniformMatrix4fv(transformLoc, 1, true,
					Util.matrix4dToFloatArray(transform), 0);

			// uniform mat4 circleTransform;
			// gl2.glFlush();
			gl2.glBegin(GL2.GL_QUADS);
			gl2.glVertex2f(-1, -1);
			gl2.glVertex2f(1, -1);
			gl2.glVertex2f(1, 1);
			gl2.glVertex2f(-1, 1);
			gl2.glEnd();
		}
		gl2.glUseProgram(0);
	}

	@Override
	public void dispose(GL gl) {
		if (program != null)
			program.destroy(gl.getGL2());
	}

}
