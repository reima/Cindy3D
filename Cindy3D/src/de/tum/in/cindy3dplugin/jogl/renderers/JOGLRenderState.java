package de.tum.in.cindy3dplugin.jogl.renderers;

import javax.media.opengl.GL;

import de.tum.in.cindy3dplugin.jogl.ModelViewerCamera;

public class JOGLRenderState {
	public enum CullMode {
		CULL_NONE,
		CULL_FRONT,
		CULL_BACK
	}
	
	public GL gl;
	public ModelViewerCamera camera;
	public boolean renderOpaque;
	public CullMode cullMode;
	
	public JOGLRenderState(GL gl, ModelViewerCamera camera,
			boolean renderOpaque, CullMode cullMode) {
		this.gl = gl;
		this.camera = camera;
		this.renderOpaque = renderOpaque;
		this.cullMode = cullMode;
	}
}
