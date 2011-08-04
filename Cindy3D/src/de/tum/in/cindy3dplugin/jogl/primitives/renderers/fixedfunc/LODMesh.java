package de.tum.in.cindy3dplugin.jogl.primitives.renderers.fixedfunc;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL2;

import de.tum.in.cindy3dplugin.jogl.Util;

public class LODMesh {
	private int vertexBuffer;
	private int indexBuffer;
	private int faceCount;
	private int vertexSize;
	private double maxEdgeLength;
	
	private DoubleBuffer vertices;
	private IntBuffer indices;
	
	public LODMesh(int vertexSize, int vertexCount, int faceCount) {
		this.vertexSize = vertexSize;
		this.faceCount = faceCount;
		this.vertices = DoubleBuffer.allocate(vertexCount * vertexSize);
		this.indices = IntBuffer.allocate(faceCount * 3);
		this.maxEdgeLength = 0;
	}
	
	public void putVertex(double[] vertex) {
		vertices.put(vertex);
	}
	
	public void putFace(int i0, int i1, int i2) {
		indices.put(i0);
		indices.put(i1);
		indices.put(i2);
	}
	
	public void finish(GL2 gl2) {
		int[] tmp = new int[2];
		gl2.glGenBuffers(2, tmp, 0);
		vertexBuffer = tmp[0];
		indexBuffer = tmp[1];

		vertices.flip();
		gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, vertexBuffer);
		gl2.glBufferData(GL2.GL_ARRAY_BUFFER, vertices.capacity()
				* Util.SIZEOF_DOUBLE, vertices, GL2.GL_STATIC_DRAW);
		gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);

		indices.flip();
		gl2.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
		gl2.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, indices.capacity()
				* Util.SIZEOF_INT, indices, GL2.GL_STATIC_DRAW);
		gl2.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);

		calculateMaxEdgeLength();

		vertices = null;
		indices = null;
	}

	private void calculateMaxEdgeLength() {
		maxEdgeLength = 0;
		for (int i = 0; i < indices.limit(); i += 3) {
			int i0 = indices.get(i);
			int i1 = indices.get(i + 1);
			int i2 = indices.get(i + 2);
			double[][] v = new double[3][vertexSize];
			vertices.position(i0 * vertexSize);
			vertices.get(v[0], 0, vertexSize);
			vertices.position(i1 * vertexSize);
			vertices.get(v[1], 0, vertexSize);
			vertices.position(i2 * vertexSize);
			vertices.get(v[2], 0, vertexSize);

			for (int j = 0; j < 3; ++j) {
				double edgeLengthSq = 0;
				double[] v0 = v[j];
				double[] v1 = v[(j + 1) % 3];
				for (int k = 0; k < vertexSize; ++k) {
					edgeLengthSq += Math.pow(v0[k] - v1[k], 2);
				}
				maxEdgeLength = Math.max(maxEdgeLength,
						Math.sqrt(edgeLengthSq));
			}
		}
	}

	public void dispose(GL2 gl2) {
		gl2.glDeleteBuffers(2, new int[] { vertexBuffer, indexBuffer }, 0);
	}

	public void render(GL2 gl2) {
		gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, vertexBuffer);
		gl2.glVertexPointer(vertexSize, GL2.GL_DOUBLE, vertexSize
				* Util.SIZEOF_DOUBLE, 0);
		gl2.glNormalPointer(GL2.GL_DOUBLE, vertexSize * Util.SIZEOF_DOUBLE,
				0);
		gl2.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
		gl2.glDrawElements(GL2.GL_TRIANGLES, faceCount * 3,
				GL2.GL_UNSIGNED_INT, 0);
		gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
		gl2.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
}
