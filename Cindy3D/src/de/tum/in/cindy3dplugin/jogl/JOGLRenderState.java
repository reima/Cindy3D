package de.tum.in.cindy3dplugin.jogl;

import javax.media.opengl.GL;

public class JOGLRenderState {
	public GL gl;
	public ModelViewerCamera camera;
	public boolean renderOpaque;
	
	public JOGLRenderState(GL gl, ModelViewerCamera camera, boolean renderOpaque) {
		this.gl = gl;
		this.camera = camera;
		this.renderOpaque = renderOpaque;
	}
}
