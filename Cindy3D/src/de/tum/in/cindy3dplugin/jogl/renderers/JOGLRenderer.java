package de.tum.in.cindy3dplugin.jogl.renderers;

import javax.media.opengl.GLEventListener;

import de.tum.in.cindy3dplugin.jogl.ModelViewerCamera;
import de.tum.in.cindy3dplugin.jogl.primitives.Scene;

public abstract class JOGLRenderer implements GLEventListener {
	protected Scene scene;
	protected ModelViewerCamera camera;
	
	public JOGLRenderer(Scene scene, ModelViewerCamera camera) {
		this.scene = scene;
		this.camera = camera;
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
}