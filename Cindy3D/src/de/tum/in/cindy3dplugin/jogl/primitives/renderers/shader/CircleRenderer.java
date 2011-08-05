package de.tum.in.cindy3dplugin.jogl.primitives.renderers.shader;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

import de.tum.in.cindy3dplugin.jogl.Util;
import de.tum.in.cindy3dplugin.jogl.primitives.Circle;
import de.tum.in.cindy3dplugin.jogl.primitives.renderers.CircleRendererBase;
import de.tum.in.cindy3dplugin.jogl.renderers.JOGLRenderState;

public class CircleRenderer extends CircleRendererBase {
	private ShaderProgram program = null;
	private int centerLoc;
	private int radiusSqLoc;
	private int normalLoc;
	private int transformLoc;

	@Override
	public boolean init(GL gl) {
		return reloadShaders(gl);
	}

	@Override
	public boolean reloadShaders(GL gl) {
		GL2 gl2 = gl.getGL2();
		
		if (program != null) {
			program.destroy(gl2);
		}

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
		gl2.glUniform3f(centerLoc, (float) circle.center.getX(),
				(float) circle.center.getY(), (float) circle.center.getZ());
		gl2.glUniform3f(normalLoc, (float) circle.normal.getX(),
				(float) circle.normal.getY(), (float) circle.normal.getZ());
		gl2.glUniform1f(radiusSqLoc, (float) (circle.radius * circle.radius));

		gl2.glUniformMatrix4fv(transformLoc, 1, true, buildTransform(circle), 0);
		
		gl2.glBegin(GL2.GL_QUADS);
			gl2.glVertex2f(-1, -1);
			gl2.glVertex2f(1, -1);
			gl2.glVertex2f(1, 1);
			gl2.glVertex2f(-1, 1);
		gl2.glEnd();
	}
}
