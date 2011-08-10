package de.tum.in.cindy3dplugin.jogl.primitives.renderers.shader;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.opengl.util.glsl.ShaderProgram;

import de.tum.in.cindy3dplugin.jogl.Util;
import de.tum.in.cindy3dplugin.jogl.primitives.Circle;
import de.tum.in.cindy3dplugin.jogl.primitives.renderers.CircleRendererBase;
import de.tum.in.cindy3dplugin.jogl.renderers.JOGLRenderState;

/**
 * Circle renderer using shaders for rendering circles.
 * 
 * Using shaders results in per fragment shading and lighting. Circles has a
 * perfectly smooth form as a quad as proxy bounding geometry is rendered
 * followed by per fragment raycasting.
 */
public class CircleRenderer extends CircleRendererBase {
	/**
	 * Shader program for the circle shaders
	 */
	private ShaderProgram program = null;
	/**
	 * Shader variable id for the circle center position
	 */
	private int centerLoc;
	/**
	 * Shader variable id for the circle radius
	 */
	private int radiusSqLoc;
	/**
	 * Shader variable id for the circle orientation / normal
	 */
	private int normalLoc;
	/**
	 * Shader variable id for the circle transformation matrix
	 */
	private int transformLoc;

	/* (non-Javadoc)
	 * @see de.tum.in.cindy3dplugin.jogl.primitives.renderers.PrimitiveRenderer#init(javax.media.opengl.GL)
	 */
	@Override
	public boolean init(GL gl) {
		return reloadShaders(gl);
	}

	/* (non-Javadoc)
	 * @see de.tum.in.cindy3dplugin.jogl.primitives.renderers.PrimitiveRenderer#reloadShaders(javax.media.opengl.GL)
	 */
	@Override
	public boolean reloadShaders(GL gl) {
		GL2 gl2 = gl.getGL2();

		if (program != null) {
			program.destroy(gl2);
		}

		program = Util.loadShaderProgram(gl2, "circle.vert", "circle.frag");
		if (program == null) {
			return false;
		}

		transformLoc = gl2.glGetUniformLocation(program.program(),
				"circleTransform");
		centerLoc = gl2.glGetUniformLocation(program.program(), "circleCenter");
		radiusSqLoc = gl2.glGetUniformLocation(program.program(),
				"circleRadiusSq");
		normalLoc = gl2.glGetUniformLocation(program.program(), "circleNormal");

		return true;
	}

	/* (non-Javadoc)
	 * @see de.tum.in.cindy3dplugin.jogl.primitives.renderers.PrimitiveRenderer#dispose(javax.media.opengl.GL)
	 */
	@Override
	public void dispose(GL gl) {
		if (program != null) {
			program.destroy(gl.getGL2());
		}
	}

	/* (non-Javadoc)
	 * @see de.tum.in.cindy3dplugin.jogl.primitives.renderers.PrimitiveRenderer#postRender(de.tum.in.cindy3dplugin.jogl.renderers.JOGLRenderState)
	 */
	@Override
	protected void postRender(JOGLRenderState jrs) {
		GL2 gl2 = jrs.gl.getGL2();
		gl2.glUseProgram(0);
	}

	/* (non-Javadoc)
	 * @see de.tum.in.cindy3dplugin.jogl.primitives.renderers.PrimitiveRenderer#preRender(de.tum.in.cindy3dplugin.jogl.renderers.JOGLRenderState)
	 */
	@Override
	protected void preRender(JOGLRenderState jrs) {
		GL2 gl2 = jrs.gl.getGL2();
		gl2.glUseProgram(program.program());
	}

	/* (non-Javadoc)
	 * @see de.tum.in.cindy3dplugin.jogl.primitives.renderers.PrimitiveRenderer#render(de.tum.in.cindy3dplugin.jogl.renderers.JOGLRenderState, de.tum.in.cindy3dplugin.jogl.primitives.Primitive)
	 */
	@Override
	protected void render(JOGLRenderState jrs, Circle circle) {
		GL2 gl2 = jrs.gl.getGL2();
		gl2.glUniform3fv(centerLoc, 1,
				Util.vectorToFloatArray(circle.getCenter()), 0);
		gl2.glUniform3fv(normalLoc, 1,
				Util.vectorToFloatArray(circle.getNormal()), 0);
		gl2.glUniform1f(radiusSqLoc, (float) Math.pow(circle.getRadius(), 2.0));

		gl2.glUniformMatrix4fv(transformLoc, 1, true, buildTransform(circle), 0);

		gl2.glBegin(GL2.GL_QUADS);
		gl2.glVertex2f(-1, -1);
		gl2.glVertex2f(1, -1);
		gl2.glVertex2f(1, 1);
		gl2.glVertex2f(-1, 1);
		gl2.glEnd();
	}
}
