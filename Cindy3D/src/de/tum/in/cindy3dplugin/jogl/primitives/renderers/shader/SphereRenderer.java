package de.tum.in.cindy3dplugin.jogl.primitives.renderers.shader;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.opengl.util.glsl.ShaderProgram;

import de.tum.in.cindy3dplugin.jogl.Util;
import de.tum.in.cindy3dplugin.jogl.primitives.Sphere;
import de.tum.in.cindy3dplugin.jogl.primitives.renderers.PrimitiveRenderer;
import de.tum.in.cindy3dplugin.jogl.renderers.JOGLRenderState;
import de.tum.in.cindy3dplugin.jogl.renderers.JOGLRenderState.CullMode;

/**
 * Sphere renderer using shaders for rendering spheres. Using shaders results in per fragment shading and lighting. Spheres have a
 * perfectly smooth form as a quad as proxy bounding geometry is rendered
 * followed by per fragment raycasting.
 */
public class SphereRenderer extends PrimitiveRenderer<Sphere> {
	private ShaderProgram program = null;
	private int centerLoc;
	private int radiusLoc;
	private int modeLoc;
	
	private float renderMode;

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
		
		program = Util.loadShaderProgram(gl2, "sphere.vert", "sphere.frag");
		if (program == null) {
			return false;
		}

		centerLoc = gl2.glGetUniformLocation(program.program(), "sphereCenter");
		radiusLoc = gl2.glGetUniformLocation(program.program(), "sphereRadius");
		modeLoc = gl2.glGetUniformLocation(program.program(), "sphereMode");

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
		
		if (jrs.cullMode == CullMode.CULL_FRONT) {
			renderMode = 0;
		} else if (jrs.cullMode == CullMode.CULL_BACK) {
			renderMode = 1;
		} else if (jrs.cullMode == CullMode.CULL_NONE) {
			renderMode = 2;
		}
	}

	/* (non-Javadoc)
	 * @see de.tum.in.cindy3dplugin.jogl.primitives.renderers.PrimitiveRenderer#render(de.tum.in.cindy3dplugin.jogl.renderers.JOGLRenderState, de.tum.in.cindy3dplugin.jogl.primitives.Primitive)
	 */
	@Override
	protected void render(JOGLRenderState jrs, Sphere sphere) {
		GL2 gl2 = jrs.gl.getGL2();
		gl2.glUniform3fv(centerLoc, 1,
				Util.vectorToFloatArray(sphere.getCenter()), 0);
		gl2.glUniform1f(radiusLoc, (float) sphere.getRadius());
		
		// gl2.glFlush();
		gl2.glUniform1f(modeLoc, renderMode);

		gl2.glBegin(GL2.GL_QUADS);
		gl2.glVertex2f(-1, -1);
		gl2.glVertex2f(1, -1);
		gl2.glVertex2f(1, 1);
		gl2.glVertex2f(-1, 1);
		gl2.glEnd();
//		}
//		else {
//		
//		gl2.glUniform1f(modeLoc, 1);
//		gl2.glBegin(GL2.GL_QUADS);
//		gl2.glVertex2f(-1, -1);
//		gl2.glVertex2f(1, -1);
//		gl2.glVertex2f(1, 1);
//		gl2.glVertex2f(-1, 1);
//		gl2.glEnd();
//		}
	}
}
