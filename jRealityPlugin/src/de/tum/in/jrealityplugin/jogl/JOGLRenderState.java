package de.tum.in.jrealityplugin.jogl;

import javax.media.opengl.GL;

public class JOGLRenderState {
	public GL gl;
	public ModelViewerCamera camera;
	
	public JOGLRenderState(GL gl, ModelViewerCamera camera) {
		this.gl = gl;
		this.camera = camera;
	}
}
