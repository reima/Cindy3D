package de.tum.in.cindy3dplugin.jogl.primitives.renderers.fixedfunc;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import org.apache.commons.math.geometry.Vector3D;
import org.apache.commons.math.linear.RealMatrix;

import de.tum.in.cindy3dplugin.jogl.Util;
import de.tum.in.cindy3dplugin.jogl.primitives.Line;
import de.tum.in.cindy3dplugin.jogl.primitives.renderers.LineRendererBase;
import de.tum.in.cindy3dplugin.jogl.renderers.JOGLRenderState;

public class LineRenderer extends LineRendererBase {
	private int displayList; // TODO: different LOD levels
	
	@Override
	public boolean loadShader(GL gl) {
		GL2 gl2 = gl.getGL2();
		displayList = gl2.glGenLists(1);
		
		GLU glu = new GLU();
		GLUquadric q = glu.gluNewQuadric();
		glu.gluQuadricNormals(q, GLU.GLU_OUTSIDE);
		
		gl2.glNewList(displayList, GL2.GL_COMPILE);
		glu.gluCylinder(q, 1.0, 1.0, 2.0, 8, 8);
		gl2.glEndList();
		glu.gluDeleteQuadric(q);
		return true;
	}

	@Override
	public void dispose(GL gl) {
		// TODO Auto-generated method stub
	}

	@Override
	public void preRender(JOGLRenderState jrs) {
		jrs.gl.glEnable(GL2.GL_NORMALIZE);
		jrs.gl.glDisable(GL2.GL_CULL_FACE);
	}

	@Override
	public void postRender(JOGLRenderState jrs) {
		jrs.gl.glDisable(GL2.GL_NORMALIZE);
	}

	@Override
	protected void render(JOGLRenderState jrs, Line line) {
		GL2 gl2 = jrs.gl.getGL2();
		// Get the model view matrix
		RealMatrix modelView = jrs.camera.getTransform();

		// All computations are made in camera space, so first
		// transform the two points of the line into camera space
		// by multiplying with the modelview matrix			
		Vector3D p1 = Util.transformVector(modelView, line.p1);
		Vector3D p2 = Util.transformVector(modelView, line.p2);

		Endpoints endPoints = clipLineAtFrustum(jrs.camera, p1, p2,
				line.lineType);

		// After shifting the end points of the ray/line to the maximal
		// visible positions, the size and orientation for the OBB is needed
		RealMatrix cylinder = buildOBBTransform(endPoints, line.radius);
		
		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glPushMatrix();
		gl2.glLoadMatrixf(Util.matrixToFloatArrayTransposed(cylinder), 0);
		// Rotate cylinder's main axis to x-axis
		gl2.glRotated(90.0, 0.0, 1.0, 0.0);
		// Center cylinder on origin
		gl2.glTranslated(0, 0, -1);
		gl2.glCallList(displayList);
		gl2.glPopMatrix();
	}
}
