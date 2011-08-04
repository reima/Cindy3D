package de.tum.in.cindy3dplugin.jogl.primitives.renderers;

import java.util.HashMap;

import javax.media.opengl.GL;

import de.tum.in.cindy3dplugin.jogl.primitives.Mesh;

public abstract class MeshRendererBase extends PrimitiveRenderer<Mesh> {
	protected HashMap<Integer, MeshBuffer> meshBuffers = null;
	
	@Override
	public boolean loadShader(GL gl) {
		meshBuffers = new HashMap<Integer, MeshBuffer>();
		return true;
	}
}
