package de.tum.in.cindy3dplugin.jogl.primitives.renderers.fixedfunc;

import javax.media.opengl.GL;

import de.tum.in.cindy3dplugin.jogl.primitives.Primitive;
import de.tum.in.cindy3dplugin.jogl.primitives.renderers.PrimitiveRenderer;
import de.tum.in.cindy3dplugin.jogl.renderers.JOGLRenderState;

public class DummyRenderer<T extends Primitive> extends PrimitiveRenderer<T> {
	@Override
	public boolean loadShader(GL gl) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void dispose(GL gl) {
		// TODO Auto-generated method stub
	}

	@Override
	public void preRender(JOGLRenderState jrs) {
		// TODO Auto-generated method stub
	}

	@Override
	public void postRender(JOGLRenderState jrs) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void render(JOGLRenderState jrs, T primitive) {
		// TODO Auto-generated method stub
	}

}
