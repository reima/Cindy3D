package de.tum.in.jrealityplugin.jogl;

import java.net.URL;
import java.util.Collection;

import javax.media.opengl.GL;

import com.jogamp.opengl.util.glsl.ShaderCode;

public abstract class Renderer<T> {
	public abstract boolean init(GL gl);
	public abstract void render(GL gl, Collection<T> c);
	public abstract void dispose(GL gl);
	
	protected ShaderCode loadShader(int type, URL path) {
		StringBuffer buffer = new StringBuffer();
		ShaderCode.readShaderSource(getClass().getClassLoader(), "", path,
				buffer);
		ShaderCode shader = new ShaderCode(type, 1,
				new String[][] { { buffer.toString() } });
		return shader;
	}
}
