package de.jreality.jogl.shader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;

import de.jreality.util.Input;

public class GlslShader {
	private int shader;
	private int type;
	private String source;

	public GlslShader(int type, String source) {
		this.type = type;
		this.source = source;
	}
	
	public GlslShader(int type, File file) throws IOException {
		this.type = type;
		this.source = readFile(file);
	}
	
	private static String readFile(File file) throws IOException {
		byte[] buffer = new byte[(int)file.length()];
		FileInputStream fis = new FileInputStream(file);
		fis.read(buffer);
		return new String(buffer);
	}
	
	public void compile(GL gl) {
		shader = gl.glCreateShader(type);		
		gl.glShaderSource(shader, 1, new String[] {source}, null, 0);
		gl.glCompileShader(shader);
	}
	
	public void dispose(GL gl) {
		gl.glDeleteShader(shader);
	}
	
	public boolean isValid(GL gl) {
		int[] result = new int[1];
		gl.glGetShaderiv(shader, GL.GL_COMPILE_STATUS, IntBuffer.wrap(result));
		return result[0] == GL.GL_TRUE;
	}
	
	public String getInfoLog(GL gl) {
		int[] length = new int[1];
		gl.glGetShaderiv(shader, GL.GL_INFO_LOG_LENGTH, IntBuffer.wrap(length));
		byte[] log = new byte[length[0]];
		int[] dummy = new int[1];
		gl.glGetShaderInfoLog(shader, length[0], IntBuffer.wrap(dummy), ByteBuffer.wrap(log));
		return new String(log);
	}
	
	public int getName() {
		return shader;
	}
}
