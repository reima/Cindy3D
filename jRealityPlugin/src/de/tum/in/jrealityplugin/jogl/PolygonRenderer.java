package de.tum.in.jrealityplugin.jogl;

import java.util.Collection;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

public class PolygonRenderer extends Renderer<Polygon> {

	private ShaderProgram program = null;
	
	private int colorLoc;
	
	@Override
	public void dispose(GL gl) {
		if (program != null)
			program.destroy(gl.getGL2());
	}

	@Override
	public boolean init(GL gl) {
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
	public void render(GL gl, Collection<Polygon> polygons) {
		if (polygons.isEmpty())
			return;

		GL2 gl2 = gl.getGL2();

		gl2.glUseProgram(program.program());
		for (Polygon p : polygons) {
			gl2.glUniform3fv(colorLoc, 1, p.color.getColorComponents(null), 0);

			gl2.glBegin(GL2.GL_POLYGON);
			for (int i = 0; i < p.positions.length; ++i) {
				gl2.glNormal3d(p.normals[i].x, p.normals[i].y,
						p.normals[i].z);
				gl2.glVertex3d(p.positions[i].x, p.positions[i].y,
						p.positions[i].z);
			}
			gl2.glEnd();
		}
		gl2.glUseProgram(0);
	}

}
