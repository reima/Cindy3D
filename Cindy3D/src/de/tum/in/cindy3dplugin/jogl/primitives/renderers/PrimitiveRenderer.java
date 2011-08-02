package de.tum.in.cindy3dplugin.jogl.primitives.renderers;

import java.util.Collection;

import javax.media.opengl.GL;

import de.tum.in.cindy3dplugin.jogl.primitives.Primitive;
import de.tum.in.cindy3dplugin.jogl.renderers.JOGLRenderState;

public abstract class PrimitiveRenderer<T extends Primitive> {
	public abstract boolean loadShader(GL gl);

	public abstract void dispose(GL gl);
	
	public abstract void preRender(JOGLRenderState jrs);
	public abstract void postRender(JOGLRenderState jrs);
	protected abstract void render(JOGLRenderState jrs, T primitive);
	
	public void render(JOGLRenderState jrs, Collection<T> c) {
		if (c.isEmpty())
			return;
		
		preRender(jrs);
		for (T primitive : c)
			if (primitive.isOpaque() == jrs.renderOpaque)
				render(jrs, primitive);
		postRender(jrs);
	}
}
