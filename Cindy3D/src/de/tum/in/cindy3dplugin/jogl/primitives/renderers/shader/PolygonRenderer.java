package de.tum.in.cindy3dplugin.jogl.primitives.renderers.shader;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.opengl.util.glsl.ShaderProgram;

import de.tum.in.cindy3dplugin.jogl.Util;
import de.tum.in.cindy3dplugin.jogl.primitives.Polygon;
import de.tum.in.cindy3dplugin.jogl.primitives.renderers.PrimitiveRenderer;
import de.tum.in.cindy3dplugin.jogl.renderers.JOGLRenderState;

public class PolygonRenderer extends PrimitiveRenderer<Polygon> {
	private ShaderProgram program = null;

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
		
		program = Util.loadShaderProgram(gl2, "polygon.vert", "polygon.frag");
		if (program == null) {
			return false;
		}

		return true;
	}
	
	@Override
	public void dispose(GL gl) {
		if (program != null) {
			program.destroy(gl.getGL2());
		}
	}
	
	@Override
	protected void preRender(JOGLRenderState jrs) {
		GL2 gl2 = jrs.gl.getGL2();
		gl2.glUseProgram(program.program());
	}

	@Override
	protected void render(JOGLRenderState jrs, Polygon polygon) {
		GL2 gl2 = jrs.gl.getGL2();

		gl2.glBegin(GL2.GL_POLYGON);
		for (int i = 0; i < polygon.getPositions().length; ++i) {
			gl2.glNormal3dv(Util.vectorToDoubleArray(polygon.getNormals()[i]), 0);
			gl2.glVertex3dv(Util.vectorToDoubleArray(polygon.getPositions()[i]), 0);
		}
		gl2.glEnd();
	}

	@Override
	protected void postRender(JOGLRenderState jrs) {
		GL2 gl2 = jrs.gl.getGL2();
		gl2.glUseProgram(0);
	}
}
