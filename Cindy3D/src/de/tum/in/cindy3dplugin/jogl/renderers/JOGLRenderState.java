package de.tum.in.cindy3dplugin.jogl.renderers;

import javax.media.opengl.GL;

import de.tum.in.cindy3dplugin.jogl.ModelViewerCamera;
import de.tum.in.cindy3dplugin.jogl.RenderHints;

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
	public RenderHints renderHints;

	public JOGLRenderState(GL gl, ModelViewerCamera camera,
			boolean renderOpaque, CullMode cullMode, RenderHints renderHints) {
		this.gl = gl;
		this.camera = camera;
		this.renderOpaque = renderOpaque;
		this.cullMode = cullMode;
		this.renderHints = renderHints;
	}
}
