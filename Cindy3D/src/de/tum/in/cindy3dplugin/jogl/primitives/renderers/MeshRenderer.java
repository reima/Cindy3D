package de.tum.in.cindy3dplugin.jogl.primitives.renderers;

import java.util.HashMap;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

import de.tum.in.cindy3dplugin.jogl.Util;
import de.tum.in.cindy3dplugin.jogl.primitives.Mesh;
import de.tum.in.cindy3dplugin.jogl.renderers.JOGLRenderState;

public class MeshRenderer extends PrimitiveRenderer<Mesh> {

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
			mb = (MeshBuffer) it.next();
			mb.dispose(gl.getGL2());
		}
	}

	@Override
	public boolean init(GL gl) {
		meshBuffers = new HashMap<Integer, MeshBuffer>();

		GL2 gl2 = gl.getGL2();

		program = new ShaderProgram();
		ShaderCode vertexShader = Util.loadShader(GL2.GL_VERTEX_SHADER,
				"polygon.vert");
		if (!vertexShader.compile(gl2))
			return false;
		ShaderCode fragmentShader = Util.loadShader(GL2.GL_FRAGMENT_SHADER,
				"polygon.frag");
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
	public void postRender(JOGLRenderState jrs) {
		GL2 gl2 = jrs.gl.getGL2();
		gl2.glUseProgram(0);
		gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		gl2.glDisableClientState(GL2.GL_NORMAL_ARRAY);
	}

	@Override
	public void preRender(JOGLRenderState jrs) {
		GL2 gl2 = jrs.gl.getGL2();
		gl2.glUseProgram(program.program());
		gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl2.glEnableClientState(GL2.GL_NORMAL_ARRAY);

		gl2.glUseProgram(program.program());
	}

	@Override
	protected void render(JOGLRenderState jrs, Mesh m) {
		GL2 gl2 = jrs.gl.getGL2();
		MeshBuffer mb;
		mb = meshBuffers.get(m.identifier);
		if (mb == null) {
			mb = new MeshBuffer(jrs.gl.getGL2(), m);
			meshBuffers.put(m.identifier, mb);
		}

		gl2.glUniform4fv(colorLoc, 1, m.color.getComponents(null), 0);

		gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, mb.vertexBuffer);

		gl2.glVertexPointer(3, GL2.GL_DOUBLE, 6 * 8, 0);
		gl2.glNormalPointer(GL2.GL_DOUBLE, 6 * 8, 3 * 8);

		if (mb.hasIndexBuffer) {
			gl2.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, mb.indexBuffer);
			gl2.glPointSize(5);
			gl2.glDrawElements(GL2.GL_TRIANGLES, mb.indexCount,
					GL2.GL_UNSIGNED_INT, 0);
		} else {
			//gl2.glDrawArrays(GL2.GL_TRIANGLES, 0, (m.m - 1)*(m.n - 1) * 2 * 3);
			gl2.glDrawArrays(GL2.GL_TRIANGLES, 0, mb.vertexCount);
		}
	}
}
