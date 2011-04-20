package de.tum.in.jrealityplugin.jogl;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLException;

public class DefaultRenderer extends JOGLRenderer {
	private PointRenderer pointRenderer = new PointRenderer();
	private CircleRenderer circleRenderer = new CircleRenderer();
	private LineRenderer lineRenderer = new LineRenderer();
	private PolygonRenderer polygonRenderer = new PolygonRenderer();
	private MeshRenderer meshRenderer = new MeshRenderer();
	
	private Logger log;
	
	public DefaultRenderer(Scene scene, ModelViewerCamera camera) {
		super(scene, camera);
		
		log = Logger.getLogger("log");
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		log.info("init()");
		
		try {
			// drawable.setGL(new DebugGL2(drawable.getGL().getGL2()));
			GL2 gl = drawable.getGL().getGL2();
			gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			gl.glLoadIdentity();
	
			gl.glEnable(GL2.GL_DEPTH_TEST);
			gl.glDepthFunc(GL2.GL_LEQUAL);
			gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
	
			gl.glEnable(GL2.GL_LIGHTING);
			gl.glEnable(GL2.GL_LIGHT0);
			gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, new float[] { 0.0f,
					0.0f, 0.0f, 1.0f }, 0);
	
			//gl.glEnable(GL2.GL_LIGHT1);
			//gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, new float[] { 0.0f,
			//		0.0f, 0.0f, 1.0f }, 0);
	
			if (!pointRenderer.init(gl))
				log.severe("Point renderer initialization failed");
			if (!circleRenderer.init(gl))
				log.severe("Circle renderer initialization failed");
			if (!lineRenderer.init(gl))
				log.severe("Line renderer initialization failed");
			if (!polygonRenderer.init(gl))
				log.severe("Polygon renderer initialization failed");
			if (!meshRenderer.init(gl))
				log.severe("Mesh renderer initialization failed");
		} catch (GLException e) {
			// TODO Auto-generated catch block
			log.log(Level.SEVERE, e.toString(), e);
		}
		
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		log.info("reshape(" + x + "," + y + "," + width + "," + height + ")");
		GL2 gl = drawable.getGL().getGL2();
		if (height <= 0)
			height = 1;
		double aspect = (double) width / height;
		camera.setPerspective(60.0, aspect, 0.01, 1000.0);
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadMatrixf(Util.matrixToFloatArrayTransposed(camera
				.getPerspectiveTransform()), 0);
		
		display(drawable);
	}
	
	private void renderPrimitives(JOGLRenderState jrs, boolean opaque) {
		pointRenderer.render(jrs, scene.getPoints(), opaque);
		circleRenderer.render(jrs, scene.getCircles(), opaque);
		lineRenderer.render(jrs, scene.getLines(), opaque);
		polygonRenderer.render(jrs, scene.getPolygons(), opaque);
		meshRenderer.render(jrs, scene.getMeshes(), opaque);
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		// log.info("display()");

		GL2 gl = drawable.getGL().getGL2();
		float[] backgroundColor = new float[4];
		scene.getBackgroundColor().getRGBComponents(backgroundColor);
		gl.glClearColor(backgroundColor[0], backgroundColor[1],
				backgroundColor[2], backgroundColor[3]);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadMatrixf(
				Util.matrixToFloatArrayTransposed(camera.getTransform()), 0);
			
		JOGLRenderState jrs = new JOGLRenderState(gl, camera);
		
		
		/*
		 * gl.glEnable(GL.GL_BLEND);
		gl.glDepthMask(false);
		drawTransparent(gl);
		gl.glDepthMask(true);
		
		gl.glDisable(GL.GL_BLEND);
		
		drawOpaque(gl);
		
		gl.glEnable(GL.GL_BLEND);
		gl.glDepthMask(false);
		drawTransparent(gl);
		gl.glDepthMask(true);
		
		gl.glColorMask(false, false, false, false);
		drawOpaque(gl);
		drawTransparent(gl);
		gl.glColorMask(true, true, true, true);
		
		gl.glEnable(GL.GL_BLEND);
		gl.glDepthMask(false);
		drawTransparent(gl);
		gl.glDepthMask(true);

		drawOpaque(gl);
		 * 
		 */
		
//		gl.glEnable(GL2.GL_BLEND);
//		gl.glDepthMask(false);
//		renderPrimitives(jrs, false);
//		gl.glDepthMask(true);
//		
		gl.glDisable(GL2.GL_BLEND);
		
		//renderPrimitives(jrs, true);
		
		gl.glEnable(GL2.GL_BLEND);
		gl.glDepthMask(false);
		renderPrimitives(jrs, false);
		renderPrimitives(jrs, false);
		gl.glDepthMask(true);
		gl.glDisable(GL2.GL_BLEND);
		
		//gl.glColorMask(false, false, false, false);
		renderPrimitives(jrs, true);
//		gl.glEnable(GL2.GL_BLEND);
//		renderPrimitives(jrs, false);
//		gl.glDisable(GL2.GL_BLEND);
		//gl.glColorMask(true, true, true, true);
		
		gl.glEnable(GL2.GL_BLEND);
		//gl.glDepthMask(false);
		renderPrimitives(jrs, false);
		//gl.glDepthMask(true);
		gl.glDisable(GL2.GL_BLEND);

		//renderPrimitives(jrs, true);
		
//		renderPrimitives(jrs, true);
//		renderPrimitives(jrs, false);

		// gl.glFlush();
		// drawable.swapBuffers();
	}
	
	/*
		gl.glColor3f(0, 1, 0);
		
		gl.glEnable(GL.GL_BLEND);
		gl.glDepthMask(false);
		drawTransparent(gl);
		gl.glDepthMask(true);
		
		gl.glDisable(GL.GL_BLEND);
		
		drawOpaque(gl);
		
		gl.glEnable(GL.GL_BLEND);
		gl.glDepthMask(false);
		drawTransparent(gl);
		gl.glDepthMask(true);
		
		gl.glColorMask(false, false, false, false);
		drawOpaque(gl);
		drawTransparent(gl);
		gl.glColorMask(true, true, true, true);
		
		gl.glEnable(GL.GL_BLEND);
		gl.glDepthMask(false);
		drawTransparent(gl);
		gl.glDepthMask(true);

		drawOpaque(gl);

//		gl.glDepthMask(false);
//		gl.glEnable(GL.GL_BLEND);
//		drawTransparent(gl);
//		gl.glDepthMask(true);
		
//		gl.glDisable(GL.GL_BLEND);
//		
////		gl.glColorMask(false, false, false, false);
//		drawOpaque(gl);
////		gl.glColorMask(true, true, true, true);
//		
//		gl.glDepthMask(false);
//		gl.glEnable(GL.GL_BLEND);
//		drawTransparent(gl);
//		gl.glDepthMask(true);
//		
//		gl.glDisable(GL.GL_BLEND);
		
		//drawOpaque(gl);

//		program.bind(gl);		
//		gl.glUniform1f(program.getUniformLocation(gl, "sphereRadius"), 0.5f);
//		gl.glUniform3f(program.getUniformLocation(gl, "sphereCenter"), 0.0f, 0.0f, 0.0f);
//		gl.glUniform3f(program.getUniformLocation(gl, "sphereColor"), 1.0f, 0.0f, 0.0f);
//		program.unbind(gl);
		
		gl.glPopMatrix();
		
		gl.glFlush();


	 */

	@Override
	public void dispose(GLAutoDrawable drawable) {
		pointRenderer.dispose(drawable.getGL());
		circleRenderer.dispose(drawable.getGL());
		lineRenderer.dispose(drawable.getGL());
		polygonRenderer.dispose(drawable.getGL());
		meshRenderer.dispose(drawable.getGL());
	}
}
