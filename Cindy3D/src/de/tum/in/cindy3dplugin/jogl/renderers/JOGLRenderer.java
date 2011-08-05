package de.tum.in.cindy3dplugin.jogl.renderers;

import javax.media.opengl.GLEventListener;

import de.tum.in.cindy3dplugin.jogl.ModelViewerCamera;
import de.tum.in.cindy3dplugin.jogl.RenderHints;
import de.tum.in.cindy3dplugin.jogl.lighting.LightManager;
import de.tum.in.cindy3dplugin.jogl.primitives.Scene;

public abstract class JOGLRenderer implements GLEventListener {
	protected Scene scene;
	protected ModelViewerCamera camera;
	protected LightManager lightManager;
	
	protected RenderHints renderHints;
	
	public JOGLRenderer(RenderHints renderHints, Scene scene,
			ModelViewerCamera camera, LightManager lightManager) {
		this.renderHints = renderHints;
		this.scene = scene;
		this.camera = camera;
		this.lightManager = lightManager;
	}
	
	public RenderHints getRenderHints() {
		return renderHints;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}

	public Scene getScene() {
		return scene;
	}

	public void setCamera(ModelViewerCamera camera) {
		this.camera = camera;
	}

	public ModelViewerCamera getCamera() {
		return camera;
	}
	
	public LightManager getLightManager() {
		return lightManager;
	}
}
