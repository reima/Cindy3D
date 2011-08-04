package de.tum.in.cindy3dplugin.jogl.primitives.renderers;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL2;

import de.tum.in.cindy3dplugin.Cindy3DViewer.NormalType;
import de.tum.in.cindy3dplugin.jogl.Util;
import de.tum.in.cindy3dplugin.jogl.primitives.Mesh;

public class MeshBuffer {
	private int vertexBuffer;
	private int indexBuffer;

	private boolean hasIndexBuffer;

	private int indexCount;
	private int vertexCount;

	public MeshBuffer(GL2 gl2, Mesh m) {
		createBuffers(gl2, m);
	}

	private void createBuffers(GL2 gl2, Mesh m) {
		double[][] positions = m.getPositions();
		double[][] normals = m.getNormals();
		
		if (m.getNormalType() == NormalType.PER_VERTEX) {
			vertexCount = m.getGridWidth() * m.getGridHeight();
			indexCount = m.getFaceCount() * 3;
			hasIndexBuffer = true;
		} else {
			vertexCount = m.getFaceCount() * 3;
			indexCount = 0;
			hasIndexBuffer = false;
		}

		// Position (3 doubles) + normal (3 doubles)
		DoubleBuffer vertices = DoubleBuffer.allocate(vertexCount * (3 + 3));

		IntBuffer indices = null;
		if (m.getNormalType() == NormalType.PER_VERTEX) {
			indices = IntBuffer.allocate(indexCount);
			for (int i = 0; i < vertexCount; ++i) {
				vertices.put(positions[i]);
				vertices.put(normals[i]);
			}
		}

		// Iterate over all grid cells (each of which consists of two faces)
		int faceIndex = 0;
		for (int gridY = 0; gridY < m.getGridYMax(); ++gridY) {
			for (int gridX = 0; gridX < m.getGridXMax(); ++gridX) {
				/*
				 *    v1----v2
				 *    |    / |
				 *    |f1 /  |
				 *    |  / f2|
				 *    | /    |
				 *    v3----v4
				 */
				int gridXPlus1 = (gridX + 1) % m.getGridWidth();
				int gridYPlus1 = (gridY + 1) % m.getGridHeight();

				int v1Index = m.getVertexIndex(gridX,      gridY);
				int v2Index = m.getVertexIndex(gridXPlus1, gridY);
				int v3Index = m.getVertexIndex(gridX,      gridYPlus1);
				int v4Index = m.getVertexIndex(gridXPlus1, gridYPlus1);

				if (m.getNormalType() == NormalType.PER_VERTEX) {
					// f1
					indices.put(v1Index);
					indices.put(v2Index);
					indices.put(v3Index);

					// f2
					indices.put(v2Index);
					indices.put(v4Index);
					indices.put(v3Index);
				} else {
					// f1
					int f1Index = faceIndex++;
					vertices.put(positions[v1Index]);
					vertices.put(normals[f1Index]);
					vertices.put(positions[v2Index]);
					vertices.put(normals[f1Index]);
					vertices.put(positions[v3Index]);
					vertices.put(normals[f1Index]);

					// f2
					int f2Index = faceIndex++;
					vertices.put(positions[v2Index]);
					vertices.put(normals[f2Index]);
					vertices.put(positions[v4Index]);
					vertices.put(normals[f2Index]);
					vertices.put(positions[v3Index]);
					vertices.put(normals[f2Index]);
				}
			}
		}

		vertices.flip();
		int tmp[] = new int[1];
		gl2.glGenBuffers(1, tmp, 0);
		vertexBuffer = tmp[0];
		gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, vertexBuffer);
		gl2.glBufferData(GL2.GL_ARRAY_BUFFER, vertices.capacity()
				* Util.SIZEOF_DOUBLE, vertices, GL2.GL_STATIC_DRAW);
		gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);

		if (hasIndexBuffer) {
			indices.flip();
			gl2.glGenBuffers(1, tmp, 0);
			indexBuffer = tmp[0];
			gl2.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
			gl2.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, indices.capacity()
					* Util.SIZEOF_INT, indices, GL2.GL_STATIC_DRAW);
			gl2.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);
		} else {
			indexBuffer = 0;
		}
	}
	
	public void render(GL2 gl2) {
		gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, vertexBuffer);
		gl2.glVertexPointer(3, GL2.GL_DOUBLE, 6 * 8, 0);
		gl2.glNormalPointer(GL2.GL_DOUBLE, 6 * 8, 3 * 8);

		if (hasIndexBuffer) {
			gl2.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
			gl2.glDrawElements(GL2.GL_TRIANGLES, indexCount,
					GL2.GL_UNSIGNED_INT, 0);
		} else {
			gl2.glDrawArrays(GL2.GL_TRIANGLES, 0, vertexCount);
		}
	}

	public void dispose(GL2 gl2) {
		gl2.glDeleteBuffers(2, new int[] { vertexBuffer, indexBuffer }, 0);
	}
}
