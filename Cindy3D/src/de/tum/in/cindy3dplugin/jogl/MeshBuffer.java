package de.tum.in.cindy3dplugin.jogl;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL2;

public class MeshBuffer {
	private static final int SIZEOF_DOUBLE = 8;
	private static final int SIZEOF_INT = 4;
	
	int vertexBuffer;
	int indexBuffer;

	boolean hasIndexBuffer;
	
	int indexCount;
	int vertexCount;

	public MeshBuffer(GL2 gl2, Mesh m) {
		createBuffers(gl2, m);
	}

	private void createBuffers(GL2 gl2, Mesh m) {
		if (m.perVertexNormals) {
			vertexCount = m.gridWidth * m.gridHeight;
			indexCount = m.faceCount * 3;
			hasIndexBuffer = true;
		} else {
			vertexCount = m.faceCount * 3;
			indexCount = 0;
			hasIndexBuffer = false;
		}

		DoubleBuffer vertices = DoubleBuffer.allocate(vertexCount * 3 * 2);

		IntBuffer indices = null;
		if (m.perVertexNormals) {
			indices = IntBuffer.allocate(indexCount);
			for (int i = 0; i < vertexCount; ++i) {
				vertices.put(m.positions[i]);
				vertices.put(m.normals[i]);
			}
		}
		
		// Iterate over all grid cells (each of which consists of two faces)
		int faceIndex = 0;
		for (int gridY = 0; gridY < m.gridYMax; ++gridY) {
			for (int gridX = 0; gridX < m.gridXMax; ++gridX) {
				/*
				 *    v1----v2
				 *    |    / |
				 *    |f1 /  |
				 *    |  / f2|
				 *    | /    |
				 *    v3----v4
				 */
				int gridXPlus1 = (gridX + 1) % m.gridWidth;
				int gridYPlus1 = (gridY + 1) % m.gridHeight;
				
				int v1Index = m.getVertexIndex(gridX,      gridY);
				int v2Index = m.getVertexIndex(gridXPlus1, gridY);
				int v3Index = m.getVertexIndex(gridX,      gridYPlus1);
				int v4Index = m.getVertexIndex(gridXPlus1, gridYPlus1);
				
				if (m.perVertexNormals) {
					// f1
					indices.put(v1Index);
					indices.put(v2Index);
					indices.put(v3Index);
					
					// f2
					indices.put(v2Index);
					indices.put(v3Index);
					indices.put(v4Index);
				} else {
					// f1
					int f1Index = faceIndex++;
					vertices.put(m.positions[v1Index]);
					vertices.put(m.normals[f1Index]);
					vertices.put(m.positions[v2Index]);
					vertices.put(m.normals[f1Index]);
					vertices.put(m.positions[v3Index]);
					vertices.put(m.normals[f1Index]);
					
					// f2
					int f2Index = faceIndex++;
					vertices.put(m.positions[v2Index]);
					vertices.put(m.normals[f2Index]);
					vertices.put(m.positions[v3Index]);
					vertices.put(m.normals[f2Index]);
					vertices.put(m.positions[v4Index]);
					vertices.put(m.normals[f2Index]);
				}
			}
		}
		
		int tmp[] = new int[1];
		
		vertices.flip();
		gl2.glGenBuffers(1, tmp, 0);
		vertexBuffer = tmp[0];
		gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, vertexBuffer);
		gl2.glBufferData(GL2.GL_ARRAY_BUFFER, vertices.capacity()
				* SIZEOF_DOUBLE, vertices, GL2.GL_STATIC_DRAW);
		gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
		
		if (hasIndexBuffer) {
			indices.flip();
			gl2.glGenBuffers(1, tmp, 0);
			indexBuffer = tmp[0];
			gl2.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
			gl2.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, indices.capacity()
					* SIZEOF_INT, indices, GL2.GL_STATIC_DRAW);
			gl2.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);
		} else {
			indexBuffer = 0;
		}
	}

	public void dispose(GL2 gl2) {
		gl2.glDeleteBuffers(2, new int[] { vertexBuffer, indexBuffer }, 0);
	}
}
