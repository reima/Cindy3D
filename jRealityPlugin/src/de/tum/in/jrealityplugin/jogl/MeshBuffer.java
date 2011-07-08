package de.tum.in.jrealityplugin.jogl;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL2;

import de.tum.in.jrealityplugin.jogl.Mesh.MeshTopology;

public class MeshBuffer {
	int vertexBuffer;
	int indexBuffer;

	boolean hasIndexBuffer;
	
	int indexCount;
	int vertexCount;

	public MeshBuffer(GL2 gl2, Mesh m) {
		if (m.perVertexNormals)
			createBuffersPerVertex(gl2, m);
		else
			createBuffersPerFace(gl2, m);
	}

	private void createBuffersPerFace(GL2 gl2, Mesh m) {
		int faceCount = (m.m - 1) * (m.n - 1) * 2;
		
		if (m.topology != MeshTopology.OPEN)
			faceCount += 2*(m.m-1);
		
		if (m.topology == MeshTopology.BOTHSIDED)
			faceCount += 2*m.n;

		int sizeofDouble = 8;

		int[] buffers = new int[1];
		gl2.glGenBuffers(1, buffers, 0);
		vertexBuffer = buffers[0];
		
		vertexCount = faceCount * 6;

		DoubleBuffer vertices = DoubleBuffer.allocate(faceCount * 36);

		for (int i = 0; i < m.m - 1; ++i) {
			for (int j = 0; j < m.n - 1; ++j) {

				int[] vertexIDs = new int[] { i * m.n + j,
											  i * m.n + j + 1,
											  (i + 1) * m.n + j + 1, 
											  i * m.n + j,
											  (i + 1) * m.n + j + 1,
											  (i + 1) * m.n + j };

				int normalID = 2 * (i * (m.n - 1) + j);

				for (int k = 0; k < 6; ++k) {
					vertices.put(m.vertices[vertexIDs[k]]);
					if (k < 3)
						vertices.put(m.normals[normalID]);
					else
						vertices.put(m.normals[normalID+1]);
				}
			}
		}
		
		if (m.topology != MeshTopology.OPEN) {
			for (int i = 0; i < m.m - 1; ++i) {
				int[] vertexIDs = new int[] { i * m.n + m.m - 1,
											  i * m.n,
											  (i + 1) * m.n,
											  i * m.n + m.m - 1,
											  (i + 1) * m.n,
											  (i + 1) * m.n + m.m - 1 };
				
				int normalID = (m.m-1)*(m.n-1)*2+2*i;
				
				for (int k=0; k<6; ++k) {
					vertices.put(m.vertices[vertexIDs[k]]);
					if (k < 3)
						vertices.put(m.normals[normalID]);
					else
						vertices.put(m.normals[normalID+1]);
				}
			}
		}
		
		if (m.topology == MeshTopology.BOTHSIDED) {
			for (int i=0; i<m.n-1; ++i) {
				int[] vertexIDs = new int[] { m.n*(m.m-1)+i,
											  m.n*(m.m-1)+i+1,
											  i+1,
											  m.n*(m.m-1)+i,
											  i+1,
											  i};
				
				int normalID = (m.m-1)*(m.n-1)*2+2*(m.m-1)+2*i;
				
				for (int k=0; k<6; ++k) {
					vertices.put(m.vertices[vertexIDs[k]]);
					if (k < 3)
						vertices.put(m.normals[normalID]);
					else
						vertices.put(m.normals[normalID+1]);
				}
			}
			
			int[] vertexIDs = new int[] {m.n*m.m-1,
										 m.n*(m.m-1),
										 0,
										 m.n*m.m-1,
										 0,
										 m.n-1};
			
			int normalID = (m.m-1)*(m.n-1)*2+2*(m.m-1)+2*(m.n-1);
			for (int k=0; k<6; ++k) {
				vertices.put(m.vertices[vertexIDs[k]]);
				if (k<3)
					vertices.put(m.normals[normalID]);
				else
					vertices.put(m.normals[normalID+1]);
			}
		}
		
		vertices.flip();

		gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, vertexBuffer);

		gl2.glBufferData(GL2.GL_ARRAY_BUFFER, vertices.capacity()
				* sizeofDouble, vertices, GL2.GL_STATIC_DRAW);

		hasIndexBuffer = false;

		gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
	}

	private void createBuffersPerVertex(GL2 gl2, Mesh m) {
		int sizeofDouble = 8;
		int sizeofInt = 4;
		int faceCount = (m.m - 1) * (m.n - 1) * 2;
		
		if (m.topology != MeshTopology.OPEN)
			faceCount += (m.m-1)*2;
		if (m.topology == MeshTopology.BOTHSIDED)
			faceCount += m.n*2;
		
		indexCount = faceCount * 3;
		vertexCount = m.vertices.length;

		IntBuffer buffers = IntBuffer.allocate(2);

		gl2.glGenBuffers(2, buffers);

		vertexBuffer = buffers.get(0);
		indexBuffer = buffers.get(1);

		gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, vertexBuffer);

		DoubleBuffer vertices = DoubleBuffer
				.allocate(m.vertices.length * 3 * 2);
		for (int i = 0; i < m.vertices.length; ++i) {
			vertices.put(m.vertices[i]);
			vertices.put(m.normals[i]);
		}

		vertices.flip();

		gl2.glBufferData(GL2.GL_ARRAY_BUFFER, vertices.capacity()
				* sizeofDouble, vertices, GL2.GL_STATIC_DRAW);

		gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);

		gl2.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, indexBuffer);

		IntBuffer indices = IntBuffer.allocate(faceCount * 3);

		for (int i = 0; i < m.m - 1; ++i) {
			for (int j = 0; j < m.n - 1; ++j) {
				// Vertices of first triangle in quad
				indices.put(i * m.n + j);
				indices.put(i * m.n + j + 1);
				indices.put((i + 1) * m.n + j + 1);

				// Vertices of second triangle in quad
				indices.put(i * m.n + j);
				indices.put((i + 1) * m.n + j + 1);
				indices.put((i + 1) * m.n + j);
			}
		}

		if (m.topology != MeshTopology.OPEN) {
			for (int i = 0; i < m.m-1; ++i) {
				indices.put(i*m.n + m.m - 1);
				indices.put(i*m.n);
				indices.put((i+1)*m.n);
				
				indices.put(i*m.n + m.m-1);
				indices.put((i+1)*m.n);
				indices.put((i+1)*m.n + m.m-1);
			}
		}
		
		if (m.topology == MeshTopology.BOTHSIDED) {
			for (int i=0; i<m.n-1; ++i) {
				indices.put(m.n*(m.m-1)+i);
				indices.put(m.n*(m.m-1)+i+1);
				indices.put(i+1);
				
				indices.put(m.n*(m.m-1)+i);
				indices.put(i+1);
				indices.put(i);
			}
			
			indices.put(m.n*m.m-1);
			indices.put(m.n*(m.m-1));
			indices.put(0);
			
			indices.put(m.n*m.m-1);
			indices.put(0);
			indices.put(m.n-1);
		}

		indices.flip();
		
		System.out.println("CAPACITY: " + indices.capacity());

		gl2.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, indices.capacity()
				* sizeofInt, indices, GL2.GL_STATIC_DRAW);

		gl2.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);

		hasIndexBuffer = true;
	}

	public void dispose(GL2 gl2) {
		gl2.glDeleteBuffers(1, new int[] { vertexBuffer }, 0);
		gl2.glDeleteBuffers(1, new int[] { indexBuffer }, 0);
	}
}
