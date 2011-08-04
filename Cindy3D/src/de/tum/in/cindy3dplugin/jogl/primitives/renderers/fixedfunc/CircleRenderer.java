package de.tum.in.cindy3dplugin.jogl.primitives.renderers.fixedfunc;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import de.tum.in.cindy3dplugin.jogl.primitives.Circle;
import de.tum.in.cindy3dplugin.jogl.primitives.renderers.CircleRendererBase;
import de.tum.in.cindy3dplugin.jogl.renderers.JOGLRenderState;

public class CircleRenderer extends CircleRendererBase {
	private int displayListBase; // Display lists for different LOD levels
	private static final int LOD_COUNT = 8;

	@Override
	public boolean loadShader(GL gl) {
		GL2 gl2 = gl.getGL2();
		displayListBase = gl2.glGenLists(8);
		GLU glu = new GLU();
		GLUquadric q = glu.gluNewQuadric();
		glu.gluQuadricNormals(q, GLU.GLU_OUTSIDE);
		for (int i = 0; i < LOD_COUNT; ++i) {
			gl2.glNewList(displayListBase + i, GL2.GL_COMPILE);
			int stacks = 2 * i;
			int slices = 2 * stacks;
			glu.gluDisk(q, 0, 1, slices, stacks);
			gl2.glEndList();
		}
		glu.gluDeleteQuadric(q);
		return true;
	}

	@Override
	public void dispose(GL gl) {
		gl.getGL2().glDeleteLists(displayListBase, LOD_COUNT);
	}

	@Override
	public void preRender(JOGLRenderState jrs) {
		GL2 gl = jrs.gl.getGL2();
		gl.glEnable(GL2.GL_NORMALIZE);
		gl.glDisable(GL2.GL_CULL_FACE);
	}

	@Override
	public void postRender(JOGLRenderState jrs) {
		jrs.gl.glDisable(GL2.GL_NORMALIZE);
	}

	@Override
	protected void render(JOGLRenderState jrs, Circle circle) {
		GL2 gl = jrs.gl.getGL2();
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();

		gl.glMultTransposeMatrixf(buildTransform(circle), 0);
		
		gl.glCallList(displayListBase + 4);
		gl.glPopMatrix();
	}
}
