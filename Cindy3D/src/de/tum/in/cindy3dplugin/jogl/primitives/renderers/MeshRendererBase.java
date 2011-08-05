package de.tum.in.cindy3dplugin.jogl.primitives.renderers;

import java.util.HashMap;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import de.tum.in.cindy3dplugin.jogl.primitives.Mesh;
import de.tum.in.cindy3dplugin.jogl.renderers.JOGLRenderState;

public abstract class MeshRendererBase extends PrimitiveRenderer<Mesh> {
	protected HashMap<Integer, MeshBuffer> meshBuffers = null;
	
	@Override
	public boolean init(GL gl) {
		meshBuffers = new HashMap<Integer, MeshBuffer>();
		return true;
	}
	
	@Override
	public void dispose(GL gl) {
		Iterator<MeshBuffer> it = meshBuffers.values().iterator();

		MeshBuffer mb;
		while (it.hasNext()) {
			mb = it.next();
			mb.dispose(gl.getGL2());
		}
	}
	

	@Override
	public void preRender(JOGLRenderState jrs) {
		GL2 gl2 = jrs.gl.getGL2();
		gl2.glDisable(GL2.GL_CULL_FACE);
		gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl2.glEnableClientState(GL2.GL_NORMAL_ARRAY);
	}

	@Override
	public void postRender(JOGLRenderState jrs) {
		GL2 gl2 = jrs.gl.getGL2();
		gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		gl2.glDisableClientState(GL2.GL_NORMAL_ARRAY);
	}

	protected MeshBuffer getMeshBuffer(GL gl, Mesh m) {
		MeshBuffer mb;
		mb = meshBuffers.get(m.getIdentifier());
		if (mb == null) {
			mb = new MeshBuffer(gl.getGL2(), m);
			meshBuffers.put(m.getIdentifier(), mb);
		}
		return mb;
	}
	
	@Override
	protected void render(JOGLRenderState jrs, Mesh m) {
		getMeshBuffer(jrs.gl, m).render(jrs.gl);
	}
}
