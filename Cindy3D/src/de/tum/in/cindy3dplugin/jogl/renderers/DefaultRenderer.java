package de.tum.in.cindy3dplugin.jogl.renderers;

import java.util.logging.Level;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLException;

import de.tum.in.cindy3dplugin.jogl.ModelViewerCamera;
import de.tum.in.cindy3dplugin.jogl.RenderHints;
import de.tum.in.cindy3dplugin.jogl.Util;
import de.tum.in.cindy3dplugin.jogl.lighting.LightManager;
import de.tum.in.cindy3dplugin.jogl.primitives.Circle;
import de.tum.in.cindy3dplugin.jogl.primitives.Line;
import de.tum.in.cindy3dplugin.jogl.primitives.Mesh;
import de.tum.in.cindy3dplugin.jogl.primitives.Sphere;
import de.tum.in.cindy3dplugin.jogl.primitives.Polygon;
import de.tum.in.cindy3dplugin.jogl.primitives.Scene;
import de.tum.in.cindy3dplugin.jogl.primitives.renderers.PrimitiveRenderer;
import de.tum.in.cindy3dplugin.jogl.primitives.renderers.PrimitiveRendererFactory;
import de.tum.in.cindy3dplugin.jogl.renderers.JOGLRenderState.CullMode;

public class DefaultRenderer extends JOGLRenderer {
	private PrimitiveRenderer<Sphere> sphereRenderer;
	private PrimitiveRenderer<Circle> circleRenderer;
	private PrimitiveRenderer<Line> lineRenderer;
	private PrimitiveRenderer<Polygon> polygonRenderer;
	private PrimitiveRenderer<Mesh> meshRenderer;
	
	public DefaultRenderer(RenderHints renderHints, Scene scene,
			ModelViewerCamera camera, LightManager lightManager,
			PrimitiveRendererFactory prf) {
		super(renderHints, scene, camera, lightManager);
		sphereRenderer = prf.createSphereRenderer();
		circleRenderer = prf.createCircleRenderer();
		lineRenderer = prf.createLineRenderer();
		polygonRenderer = prf.createPolygonRenderer();
		meshRenderer = prf.createMeshRenderer();
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		Util.logger.info("init()");
		
		try {
			// drawable.setGL(new DebugGL2(drawable.getGL().getGL2()));
			GL2 gl = drawable.getGL().getGL2();
			
			Util.logger.info("Chosen caps: " + drawable.getChosenGLCapabilities());
			Util.logger.info("GL_VENDOR: " + gl.glGetString(GL2.GL_VENDOR));
			Util.logger.info("GL_RENDERER: " + gl.glGetString(GL2.GL_RENDERER));
			Util.logger.info("GL_VERSION: " + gl.glGetString(GL2.GL_VERSION));
			
			gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			gl.glLoadIdentity();
	
			gl.glEnable(GL2.GL_DEPTH_TEST);
			gl.glDepthFunc(GL2.GL_LEQUAL);
			gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
			
			gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, new float[] { 0.5f,
					0.5f, 0.5f, 0.5f }, 0);
			gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, new float[] { 0.2f,
					0.2f, 0.2f, 0.2f }, 0);
			gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, 60.0f);
	
			gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL2.GL_TRUE);
			gl.glEnable(GL2.GL_LIGHTING);
			
			if (!sphereRenderer.init(gl))
				Util.logger.severe("Point renderer initialization failed");
			if (!circleRenderer.init(gl))
				Util.logger.severe("Circle renderer initialization failed");
			if (!lineRenderer.init(gl))
				Util.logger.severe("Line renderer initialization failed");
			if (!polygonRenderer.init(gl))
				Util.logger.severe("Polygon renderer initialization failed");
			if (!meshRenderer.init(gl))
				Util.logger.severe("Mesh renderer initialization failed");
		} catch (GLException e) {
			// TODO Auto-generated catch block
			Util.logger.log(Level.SEVERE, e.toString(), e);
		}
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		Util.logger.info("reshape(" + x + "," + y + "," + width + "," + height + ")");
		GL2 gl = drawable.getGL().getGL2();
		camera.setPerspective(camera.getFieldOfView(), width, height, camera
				.getZNear(), camera.getZFar());
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadMatrixf(Util.matrixToFloatArrayTransposed(camera
				.getPerspectiveTransform()), 0);
		
		display(drawable);
	}
	
	private void renderPrimitives(JOGLRenderState jrs) {
		if (!jrs.renderOpaque) {
			jrs.cullMode = CullMode.CULL_FRONT;
			sphereRenderer.render(jrs, scene.getSpheres());
			jrs.cullMode = CullMode.CULL_NONE;
		}
		
		circleRenderer.render(jrs, scene.getCircles());
		lineRenderer.render(jrs, scene.getLines());
		polygonRenderer.render(jrs, scene.getPolygons());
		meshRenderer.render(jrs, scene.getMeshes());

		jrs.cullMode = CullMode.CULL_BACK;
		sphereRenderer.render(jrs, scene.getSpheres());
		jrs.cullMode = CullMode.CULL_NONE;
	}

	@Override
	public void display(GLAutoDrawable drawable) {
//		Util.logger.info("display()");

		GL2 gl = drawable.getGL().getGL2();
		float[] backgroundColor = new float[4];
		scene.getBackgroundColor().getRGBComponents(backgroundColor);
		gl.glClearColor(backgroundColor[0], backgroundColor[1],
				backgroundColor[2], backgroundColor[3]);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadMatrixf(
				Util.matrixToFloatArrayTransposed(camera.getTransform()), 0);
			
		JOGLRenderState jrs = new JOGLRenderState(gl, camera, true,
				CullMode.CULL_NONE, renderHints);

		if (lightManager.getCompileShader()) {
			Util.setShaderLightFillIn(lightManager.getShaderFillIn());
			
			if (!sphereRenderer.reloadShaders(gl))
				Util.logger.severe("Point renderer shader loading failed");
			if (!circleRenderer.reloadShaders(gl))
				Util.logger.severe("Circle renderer shader loading failed");
			if (!lineRenderer.reloadShaders(gl))
				Util.logger.severe("Line renderer shader loading failed");
			if (!polygonRenderer.reloadShaders(gl))
				Util.logger.severe("Polygon renderer shader loading failed");
			if (!meshRenderer.reloadShaders(gl))
				Util.logger.severe("Mesh renderer shader loading failed");
			
			lightManager.setCompileShader(false);
		}
		
		lightManager.setGLState(gl);
		
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
		jrs.renderOpaque = false;
		renderPrimitives(jrs);
		renderPrimitives(jrs);
		gl.glDepthMask(true);
		gl.glDisable(GL2.GL_BLEND);
		
		//gl.glColorMask(false, false, false, false);
		jrs.renderOpaque = true;
		renderPrimitives(jrs);
//		gl.glEnable(GL2.GL_BLEND);
//		renderPrimitives(jrs, false);
//		gl.glDisable(GL2.GL_BLEND);
		//gl.glColorMask(true, true, true, true);
		
//		gl.glColorMask(false, false, false, false);
//		renderPrimitives(jrs, false);
//		gl.glColorMask(true, true, true, true);
		
		
		gl.glEnable(GL2.GL_BLEND);
		//gl.glDepthMask(false);
		jrs.renderOpaque = false;
		renderPrimitives(jrs);
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
		sphereRenderer.dispose(drawable.getGL());
		circleRenderer.dispose(drawable.getGL());
		lineRenderer.dispose(drawable.getGL());
		polygonRenderer.dispose(drawable.getGL());
		meshRenderer.dispose(drawable.getGL());
	}
}
