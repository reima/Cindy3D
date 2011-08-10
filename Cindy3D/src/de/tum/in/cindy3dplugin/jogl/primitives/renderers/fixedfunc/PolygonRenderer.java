package de.tum.in.cindy3dplugin.jogl.primitives.renderers.fixedfunc;


import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import de.tum.in.cindy3dplugin.jogl.Util;
import de.tum.in.cindy3dplugin.jogl.primitives.Polygon;
import de.tum.in.cindy3dplugin.jogl.primitives.renderers.PrimitiveRenderer;
import de.tum.in.cindy3dplugin.jogl.renderers.JOGLRenderState;

public class PolygonRenderer extends PrimitiveRenderer<Polygon> {
	@Override
	public boolean init(GL gl) {
		return true;
	}

	@Override
	public void dispose(GL gl) {
	}

	@Override
	protected void preRender(JOGLRenderState jrs) {
		GL2 gl = jrs.gl.getGL2();
		gl.glDisable(GL2.GL_CULL_FACE);
	}

	@Override
	protected void postRender(JOGLRenderState jrs) {
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
}
