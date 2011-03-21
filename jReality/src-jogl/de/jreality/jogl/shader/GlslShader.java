package de.jreality.jogl.shader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;

public class GlslShader {
	private int shader;
	private int type;
	private String source;

	public GlslShader(int type, String source) {
		this.type = type;
		this.source = source;
	}
	
	public GlslShader(int type, InputStream source) throws IOException {
		this.type = type;
		this.source = readInput(source);
	}
	
	private static String readInput(InputStream in) throws IOException {
		final char[] buffer = new char[4096];
		Reader reader = new InputStreamReader(in);
		StringBuilder builder = new StringBuilder();
		int read;
		do {
			read = reader.read(buffer);
			if (read > 0) {
				builder.append(buffer, 0, read);
			}
		} while (read >= 0);
		return builder.toString();
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
