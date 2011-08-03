package de.tum.in.cindy3dplugin.jogl.primitives.renderers.fixedfunc;


import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import de.tum.in.cindy3dplugin.jogl.Util;
import de.tum.in.cindy3dplugin.jogl.primitives.Polygon;
import de.tum.in.cindy3dplugin.jogl.primitives.renderers.PrimitiveRenderer;
import de.tum.in.cindy3dplugin.jogl.renderers.JOGLRenderState;

public class PolygonRenderer extends PrimitiveRenderer<Polygon> {

	@Override
	public boolean loadShader(GL gl) {
		return true;
	}

	@Override
	public void dispose(GL gl) {
	}

	@Override
	public void preRender(JOGLRenderState jrs) {
		GL2 gl = jrs.gl.getGL2();
		gl.glEnable(GL2.GL_NORMALIZE);
		
		gl.glDisable(GL2.GL_CULL_FACE);
	}

	@Override
	public void postRender(JOGLRenderState jrs) {
	}

	@Override
	protected void render(JOGLRenderState jrs, Polygon polygon) {
		GL2 gl2 = jrs.gl.getGL2();

		Util.setMaterial(jrs.gl, polygon.color, polygon.shininess);

		gl2.glBegin(GL2.GL_POLYGON);
		for (int i = 0; i < polygon.positions.length; ++i) {
			gl2.glNormal3d(polygon.normals[i].getX(),
					polygon.normals[i].getY(), polygon.normals[i].getZ());
			gl2.glVertex3d(polygon.positions[i].getX(), polygon.positions[i]
					.getY(), polygon.positions[i].getZ());
		}
		gl2.glEnd();
	}
}
