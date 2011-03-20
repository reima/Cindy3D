package de.jreality.jogl.shader;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;

import de.jreality.jogl.JOGLRenderingState;

public class GlslShaderProgram {
	private int program;
	
	public GlslShaderProgram(GL gl) {
		program = gl.glCreateProgram();
	}
	
	public GlslShaderProgram(GL gl, String vertexShaderPath, String fragmentShaderPath) throws Exception {
		GlslShader vert = new GlslShader(GL.GL_VERTEX_SHADER, new File(vertexShaderPath));
		vert.compile(gl);
		if (!vert.isValid(gl)) throw new Exception("vertex shader: " + vert.getInfoLog(gl));
		
		GlslShader frag = new GlslShader(GL.GL_FRAGMENT_SHADER, new File(fragmentShaderPath));
		frag.compile(gl);
		if (!frag.isValid(gl)) throw new Exception("fragment shader: " + frag.getInfoLog(gl));
		
		program = gl.glCreateProgram();		
		attachShader(gl, frag);
		attachShader(gl, vert);		
		link(gl);
		
		if (!isValid(gl)) throw new Exception(getInfoLog(gl));
	}
	
	public void dispose(GL gl) {
		gl.glDeleteProgram(program);
	}
	
	public void attachShader(GL gl, GlslShader shader) {
		gl.glAttachShader(program, shader.getName());
	}
	
	public void link(GL gl) {
		gl.glLinkProgram(program);
	}
	
	public boolean isValid(GL gl) {
		int[] result = new int[1];
		gl.glGetProgramiv(program, GL.GL_LINK_STATUS, IntBuffer.wrap(result));
		return result[0] == GL.GL_TRUE;
	}

	public void bind(GL gl) {
		gl.glUseProgram(program);
	}
	
	public void unbind(GL gl) {
		gl.glUseProgram(0);
	}
	
	public int getName() {
		return program;
	}
	
	public String getInfoLog(GL gl) {
		int[] length = new int[1];
		gl.glGetProgramiv(program, GL.GL_INFO_LOG_LENGTH, IntBuffer.wrap(length));
		byte[] log = new byte[length[0]];
		int[] dummy = new int[1];
		gl.glGetProgramInfoLog(program, length[0], IntBuffer.wrap(dummy), ByteBuffer.wrap(log));
		return new String(log);
	}
	
	public int getUniformLocation(GL gl, String name) {
		return gl.glGetUniformLocation(program, name);
	}
}
