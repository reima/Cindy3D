package de.tum.in.cindy3dplugin.jogl;

import javax.media.opengl.GL;

public class JOGLRenderState {
	enum CullMode {
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
