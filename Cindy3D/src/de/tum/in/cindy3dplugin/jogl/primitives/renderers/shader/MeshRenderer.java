package de.tum.in.cindy3dplugin.jogl.primitives.renderers.shader;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

import de.tum.in.cindy3dplugin.jogl.Util;
import de.tum.in.cindy3dplugin.jogl.primitives.renderers.MeshRendererBase;
import de.tum.in.cindy3dplugin.jogl.renderers.JOGLRenderState;

public class MeshRenderer extends MeshRendererBase {
	private ShaderProgram program = null;

	@Override
	public boolean init(GL gl) {
		return super.init(gl) && reloadShaders(gl);
	}

	@Override
	public boolean reloadShaders(GL gl) {
		GL2 gl2 = gl.getGL2();
		
		if (program != null) {
			program.destroy(gl2);
		}

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
	public void dispose(GL gl) {
		super.dispose(gl);
		if (program != null) {
			program.destroy(gl.getGL2());
		}
	}

	@Override
	public void preRender(JOGLRenderState jrs) {
		super.preRender(jrs);
		jrs.gl.getGL2().glUseProgram(program.program());
	}

	@Override
	public void postRender(JOGLRenderState jrs) {
		super.postRender(jrs);
		jrs.gl.getGL2().glUseProgram(0);
	}
}
