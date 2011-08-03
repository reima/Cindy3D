package de.tum.in.cindy3dplugin.jogl.primitives.renderers.shader;

import java.util.HashMap;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

import de.tum.in.cindy3dplugin.jogl.Util;
import de.tum.in.cindy3dplugin.jogl.primitives.Mesh;
import de.tum.in.cindy3dplugin.jogl.primitives.renderers.PrimitiveRenderer;
import de.tum.in.cindy3dplugin.jogl.renderers.JOGLRenderState;

public class MeshRenderer extends PrimitiveRenderer<Mesh> {
	private HashMap<Integer, MeshBuffer> meshBuffers;

	private ShaderProgram program = null;

	@Override
	public void dispose(GL gl) {
		if (program != null)
			program.destroy(gl.getGL2());

		Iterator<MeshBuffer> it = meshBuffers.values().iterator();

		MeshBuffer mb;
		while (it.hasNext()) {
			mb = it.next();
			mb.dispose(gl.getGL2());
		}
	}

	@Override
	public boolean loadShader(GL gl) {
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
		mb = meshBuffers.get(m.getIdentifier());
		if (mb == null) {
			mb = new MeshBuffer(jrs.gl.getGL2(), m);
			meshBuffers.put(m.getIdentifier(), mb);
		}

		mb.render(gl2);
	}
}
