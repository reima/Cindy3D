package de.tum.in.jrealityplugin.jogl;

import java.net.URL;
import java.util.Collection;

import javax.media.opengl.GL;

import com.jogamp.opengl.util.glsl.ShaderCode;

public abstract class PrimitiveRenderer<T extends Primitive> {
	public abstract boolean init(GL gl);

	public abstract void dispose(GL gl);
	
	public abstract void preRender(JOGLRenderState jrs);
	public abstract void postRender(JOGLRenderState jrs);
	protected abstract void render(JOGLRenderState jrs, T primitive);
	
	
	public void render(JOGLRenderState jrs, Collection<T> c) {
		if (c.isEmpty())
			return;
		
		preRender(jrs);
		for (T primitive : c)
			render(jrs, primitive);		
		postRender(jrs);
	}
	
	protected ShaderCode loadShader(int type, URL path) {
		StringBuffer buffer = new StringBuffer();
		ShaderCode.readShaderSource(getClass().getClassLoader(), "", path,
				buffer);
		ShaderCode shader = new ShaderCode(type, 1,
				new String[][] { { buffer.toString() } });
		return shader;
	}
}
