package de.tum.in.cindy3dplugin.jogl.primitives.renderers.shader;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

import de.tum.in.cindy3dplugin.jogl.Util;
import de.tum.in.cindy3dplugin.jogl.primitives.Polygon;
import de.tum.in.cindy3dplugin.jogl.primitives.renderers.PrimitiveRenderer;
import de.tum.in.cindy3dplugin.jogl.renderers.JOGLRenderState;

public class PolygonRenderer extends PrimitiveRenderer<Polygon> {
	private ShaderProgram program = null;

	@Override
	public void dispose(GL gl) {
		if (program != null)
			program.destroy(gl.getGL2());
	}

	@Override
	public boolean loadShader(GL gl) {
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
	public void preRender(JOGLRenderState jrs) {
		GL2 gl2 = jrs.gl.getGL2();
		gl2.glUseProgram(program.program());
	}

	@Override
	protected void render(JOGLRenderState jrs, Polygon polygon) {
		GL2 gl2 = jrs.gl.getGL2();

		Util.setMaterial(jrs.gl, polygon.color, polygon.shininess);

		gl2.glBegin(GL2.GL_POLYGON);
		for (int i = 0; i < polygon.positions.length; ++i) {
			gl2.glNormal3d(polygon.normals[i].getX(),
					polygon.normals[i].getY(), polygon.normals[i].getZ());
			gl2.glVertex3d(polygon.positions[i].getX(), polygon.positions[i]
					.getY(), polygon.positions[i].getZ());
		}
		gl2.glEnd();
	}

	@Override
	public void postRender(JOGLRenderState jrs) {
		GL2 gl2 = jrs.gl.getGL2();
		gl2.glUseProgram(0);		
	}
}
