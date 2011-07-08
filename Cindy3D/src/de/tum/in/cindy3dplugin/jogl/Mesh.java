package de.tum.in.cindy3dplugin.jogl;

import java.awt.Color;

import org.apache.commons.math.geometry.Vector3D;

import de.tum.in.cindy3dplugin.Cindy3DViewer.MeshTopology;

public class Mesh extends Primitive {
	int n, m;

	double[][] vertices;
	double[][] normals;

	boolean perVertexNormals;

	private static int meshCounter = 0;

	int identifier;
	MeshTopology topology;

	public Mesh(int m, int n, double[][] vertices, double[][] normals,
			Color color, double opacity, MeshTopology topology) {
		super(color, opacity);
		this.n = n;
		this.m = m;
		this.topology = topology;
		perVertexNormals = (normals != null);
		identifier = meshCounter++;
		this.vertices = vertices;
		this.normals = normals;
		if (normals == null)
			computeNormals();
	}

	public Mesh(int m, int n, double[][] vertices, boolean perVertexNormals,
			Color color, double opacity, MeshTopology topology) {
		super(color, opacity);
		this.n = n;
		this.m = m;
		this.topology = topology;
		this.perVertexNormals = perVertexNormals;
		identifier = meshCounter++;
		this.vertices = vertices;
		this.normals = null;
		computeNormals();
	}

	private void computeNormals() {
		normals = null;

		int faceNormalCount = (m - 1) * (n - 1) * 2;

		if (topology != MeshTopology.OPEN)
			faceNormalCount += (m - 1) * 2;
		if (topology == MeshTopology.TWO_SIDED)
			faceNormalCount += n*2;

		Vector3D[] perFace = new Vector3D[faceNormalCount];

		Vector3D[] positions = new Vector3D[m * n];
		for (int i = 0; i < m * n; ++i)
			positions[i] = new Vector3D(vertices[i][0], vertices[i][1],
					vertices[i][2]);

		for (int i = 0; i < m - 1; ++i) {
			for (int j = 0; j < n - 1; ++j) {
				Vector3D v1 = positions[i * n + j + 1].subtract(positions[i * n
						+ j]);
				Vector3D v2 = positions[(i + 1) * n + j + 1]
						.subtract(positions[i * n + j]);
				Vector3D v3 = positions[(i + 1) * n + j].subtract(positions[i
						* n + j]);

				perFace[2 * (i * (n - 1) + j) + 0] = Vector3D.crossProduct(v1,
						v2);
				perFace[2 * (i * (n - 1) + j) + 0].normalize();
				perFace[2 * (i * (n - 1) + j) + 1] = Vector3D.crossProduct(v2,
						v3);
				perFace[2 * (i * (n - 1) + j) + 1].normalize();
			}
		}		  
		  
		if (topology != MeshTopology.OPEN) {
			int offset = (m-1)*(n-1)*2;
			for (int i = 0; i < m - 1; ++i) {
				Vector3D v1 = positions[i * n]
						.subtract(positions[i * n + n - 1]);
				Vector3D v2 = positions[(i + 1) * n].subtract(positions[i * n
						+ n - 1]);
				Vector3D v3 = positions[(i + 1) * n + n - 1]
						.subtract(positions[i * n + n - 1]);

				perFace[offset + 2 * i] = Vector3D.crossProduct(
						v1, v2);
				perFace[offset + 2 * i].normalize();
				perFace[offset + 2 * i + 1] = Vector3D
						.crossProduct(v2, v3);
				perFace[offset + 2 * i + 1].normalize();
			}
		}
		
		if (topology == MeshTopology.TWO_SIDED) {
			int offset = (m-1)*(n-1)*2+2*(m-1);
			for (int i=0; i<n-1;++i) {
				Vector3D v1 = positions[n*(m-1)+i+1].subtract(positions[n*(m-1)+i]);
				Vector3D v2 = positions[i+1].subtract(positions[n*(m-1)+i]);
				Vector3D v3 = positions[i].subtract(positions[n*(m-1)+i]);
				
				perFace[offset+2*i] = Vector3D.crossProduct(
						v1, v2);
				perFace[offset+2*i].normalize();
				perFace[offset+2*i + 1] = Vector3D
						.crossProduct(v2, v3);
				perFace[offset+2*i + 1].normalize();
			}
				 
			Vector3D v1 = positions[m*(m-1)].subtract(positions[m*n-1]);
			Vector3D v2 = positions[0].subtract(positions[m*n-1]);
			Vector3D v3 = positions[m-1].subtract(positions[m*n-1]);
			
			offset += 2*(n-1);
			
			perFace[offset] = Vector3D.crossProduct(
					v1, v2);
			perFace[offset].normalize();
			perFace[offset + 1] = Vector3D
					.crossProduct(v2, v3);
			perFace[offset + 1].normalize();
			
		}

		if (!perVertexNormals) {
			normals = new double[faceNormalCount][3];
			for (int i = 0; i < normals.length; ++i)
				normals[i] = Util.vectorToDoubleArray(perFace[i]);
			return;
		}

		Vector3D[] perVertex = new Vector3D[m * n];
		for (int i = 0; i < perVertex.length; ++i)
			perVertex[i] = new Vector3D(0, 0, 0);

		for (int i = 0; i < m - 1; ++i) {
			for (int j = 0; j < n - 1; ++j) {
				perVertex[i * n + j] = perVertex[i * n + j].add(perFace[2 * (i
						* (n - 1) + j)]);
				perVertex[i * n + j + 1] = perVertex[i * n + j + 1]
						.add(perFace[2 * (i * (n - 1) + j)]);
				perVertex[(i + 1) * n + j + 1] = perVertex[(i + 1) * n + j + 1]
						.add(perFace[2 * (i * (n - 1) + j)]);

				perVertex[i * n + j] = perVertex[i * n + j].add(perFace[2 * (i
						* (n - 1) + j) + 1]);
				perVertex[(i + 1) * n + j + 1] = perVertex[(i + 1) * n + j + 1]
						.add(perFace[2 * (i * (n - 1) + j) + 1]);
				perVertex[(i + 1) * n + j] = perVertex[(i + 1) * n + j]
						.add(perFace[2 * (i * (n - 1) + j) + 1]);
			}
		}

		normals = new double[m * n][3];
		for (int i = 0; i < perVertex.length; ++i) {
			perVertex[i].normalize();
			normals[i] = Util.vectorToDoubleArray(perVertex[i]);
		}
	}
}
