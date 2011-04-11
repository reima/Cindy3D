package de.tum.in.jrealityplugin.jogl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

public class MeshRenderer extends Renderer<Mesh> {
	
	private HashMap<Integer, MeshBuffer> meshBuffers;

	private ShaderProgram program = null;

	private int colorLoc;

	@Override
	public void dispose(GL gl) {
		if (program != null)
			program.destroy(gl.getGL2());
		
		Iterator<MeshBuffer> it = meshBuffers.values().iterator();
		
		MeshBuffer mb;
		while (it.hasNext()) {
			mb = (MeshBuffer)it.next();
			mb.dispose(gl.getGL2());
		}
	}

	@Override
	public boolean init(GL gl) {
		meshBuffers = new HashMap<Integer, MeshBuffer>();
		
		GL2 gl2 = gl.getGL2();

		program = new ShaderProgram();
		ShaderCode vertexShader = loadShader(
				GL2.GL_VERTEX_SHADER,
				getClass()
						.getResource(
								"/de/tum/in/jrealityplugin/resources/shader/polygon.vert"));
		if (!vertexShader.compile(gl2))
			return false;
		ShaderCode fragmentShader = loadShader(
				GL2.GL_FRAGMENT_SHADER,
				getClass()
						.getResource(
								"/de/tum/in/jrealityplugin/resources/shader/polygon.frag"));
		if (!fragmentShader.compile(gl2))
			return false;

		if (!program.add(vertexShader))
			return false;
		if (!program.add(fragmentShader))
			return false;
		if (!program.link(gl.getGL2(), null))
			return false;

		colorLoc = gl2.glGetUniformLocation(program.program(), "polygonColor");

		return true;
	}

	@Override
	public void render(JOGLRenderState jrs, Collection<Mesh> meshes) {
		
		if (meshes.isEmpty())
			return;
		
		GL2 gl2 = jrs.gl.getGL2();
		gl2.glUseProgram(program.program());
		MeshBuffer mb;
		
		gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		
		gl2.glUseProgram(program.program());
		
		for (Mesh m : meshes)
		{
			mb = meshBuffers.get(m.identifier);
			if (mb == null) {
				mb = new MeshBuffer(jrs.gl.getGL2(), m);
				meshBuffers.put(m.identifier, mb);
			}
			
			gl2.glUniform3fv(colorLoc, 1, m.color.getColorComponents(null), 0);
			
			gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, mb.vertexBuffer);
			gl2.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, mb.indexBuffer);
			
			gl2.glDrawElements(GL2.GL_QUADS, m.m*m.n, GL2.GL_INT, 0);
		}
		gl2.glUseProgram(0);
		gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);
	}

}
