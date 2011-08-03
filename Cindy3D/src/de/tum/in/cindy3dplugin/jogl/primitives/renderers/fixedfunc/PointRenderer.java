package de.tum.in.cindy3dplugin.jogl.primitives.renderers.fixedfunc;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import de.tum.in.cindy3dplugin.jogl.primitives.Point;
import de.tum.in.cindy3dplugin.jogl.primitives.renderers.PrimitiveRenderer;
import de.tum.in.cindy3dplugin.jogl.renderers.JOGLRenderState;
import de.tum.in.cindy3dplugin.jogl.renderers.JOGLRenderState.CullMode;

public class PointRenderer extends PrimitiveRenderer<Point> {
	private int displayList; // TODO: different LOD levels
	
	@Override
	public boolean loadShader(GL gl) {
		GL2 gl2 = gl.getGL2();
		displayList = gl2.glGenLists(1);
		gl2.glNewList(displayList, GL2.GL_COMPILE);
		GLU glu = new GLU();
		GLUquadric q = glu.gluNewQuadric();
		glu.gluQuadricNormals(q, GLU.GLU_OUTSIDE);
		glu.gluSphere(q, 1, 8, 8);
		glu.gluDeleteQuadric(q);
		gl2.glEndList();
		return true;
	}

	@Override
	public void dispose(GL gl) {
		gl.getGL2().glDeleteLists(displayList, 1);
	}

	@Override
	public void preRender(JOGLRenderState jrs) {
		GL2 gl = jrs.gl.getGL2();
		gl.glEnable(GL2.GL_NORMALIZE);
		if (jrs.cullMode == CullMode.CULL_FRONT) {
			gl.glEnable(GL2.GL_CULL_FACE);
			gl.glCullFace(GL2.GL_FRONT);
		} else if (jrs.cullMode == CullMode.CULL_BACK) {
			gl.glEnable(GL2.GL_CULL_FACE);
			gl.glCullFace(GL2.GL_BACK);
		}
	}

	@Override
	public void postRender(JOGLRenderState jrs) {
		jrs.gl.glDisable(GL2.GL_NORMALIZE);
	}

	@Override
	protected void render(JOGLRenderState jrs, Point point) {
		GL2 gl = jrs.gl.getGL2();
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glTranslated(point.x, point.y, point.z);
		gl.glScaled(point.size, point.size, point.size);
		
		gl.glCallList(displayList);
		
		gl.glPopMatrix();
	}
}
