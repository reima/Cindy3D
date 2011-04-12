package de.tum.in.jrealityplugin.jogl;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL2;

public class MeshBuffer {
	int vertexBuffer;
	int indexBuffer;
	
	boolean hasIndexBuffer;

	public MeshBuffer(GL2 gl2, Mesh m) {
		IntBuffer buffers = IntBuffer.allocate(2);
		gl2.glGenBuffers(2, buffers);

		vertexBuffer = buffers.get(0);
		indexBuffer = buffers.get(1);
		
		// TODO: create buffers matching the normal type and indexing type

		gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, vertexBuffer);

		double[] flatBuffer = new double[m.vertices.length *3*2];

		for (int i = 0; i < m.vertices.length; ++i) {
			//System.arraycopy(m.vertices[i], 0, flatBuffer, i * 3, 3);
			System.arraycopy(m.vertices[i], 0, flatBuffer, i*6, 3);
			System.arraycopy(m.normals[i], 0, flatBuffer, i*6+3, 3);
		}
		DoubleBuffer b = DoubleBuffer.wrap(flatBuffer);

		gl2.glBufferData(GL2.GL_ARRAY_BUFFER, m.m * m.n * 3 * 8 * 2, b,
				GL2.GL_STATIC_DRAW);

		gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);

		gl2.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, indexBuffer);

		int[] indices = new int[(m.m - 1) * (m.n - 1) * 4];

		for (int i = 0; i < m.m - 1; ++i) {
			for (int j = 0; j < m.n - 1; ++j) {
				indices[4 * (i * (m.n-1) + j) + 0] = i * m.n + j;
				indices[4 * (i * (m.n-1) + j) + 1] = i * m.n + j + 1;
				indices[4 * (i * (m.n-1) + j) + 2] = (i + 1) * m.n + j + 1;
				indices[4 * (i * (m.n-1) + j) + 3] = (i + 1) * m.n + j;
			}
		}
		IntBuffer intbuffer = IntBuffer.wrap(indices);
		gl2.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, (m.m - 1) * (m.n - 1) * 4
				* 4, intbuffer, GL2.GL_STATIC_DRAW);

		gl2.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);

	}
	
	public void dispose(GL2 gl2) {
		gl2.glDeleteBuffers(1, new int[]{vertexBuffer}, 0);
		gl2.glDeleteBuffers(1, new int[]{indexBuffer}, 0);
	}
}
