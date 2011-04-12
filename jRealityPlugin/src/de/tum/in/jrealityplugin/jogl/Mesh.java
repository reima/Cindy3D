package de.tum.in.jrealityplugin.jogl;

import java.awt.Color;

import org.apache.commons.math.geometry.Vector3D;

public class Mesh {
	
	public enum MeshNormalType {
		MANUAL,
		PERVERTEX,
		PERFACE,
	}
	
	int n, m;
	
	Color color;
	double[][] vertices;
	double[][] normals;
	
	MeshNormalType type;
	
	private static int meshCounter = 0;
	
	int identifier;
	
	public Mesh(int m, int n, double[][] vertices, double[][] normals,
			Color color) {
		this.n = n;
		this.m = m;
		if (normals != null)
			type = MeshNormalType.MANUAL;
		else
			type = MeshNormalType.PERFACE;
		identifier = meshCounter++;
		this.vertices = vertices;
		this.normals = normals;
		this.color = color;
		computeNormals();
	}
	
	public Mesh(int m, int n, double[][] vertices, MeshNormalType type,
			Color color) {
		this.n = n;
		this.m = m;
		type = (type == MeshNormalType.MANUAL) ? MeshNormalType.PERFACE : type;
		identifier = meshCounter++;
		this.vertices = vertices;
		this.normals = null;
		this.color = color;
		computeNormals();
	}
	
	private void computeNormals() {
		if (type == MeshNormalType.MANUAL)
			return;
		
		normals = null;

		Vector3D[] perFace = new Vector3D[(m-1)*(n-1)*2];

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

		if (type == MeshNormalType.PERFACE) {
			normals = new double[(m-1)*(n-1)*2][3];
			for (int i=0; i<normals.length; ++i)
				normals[i] = Util.vectorToDoubleArray(perFace[i]);
			return;
		}

		normals = new double[m * n][3];

		Vector3D[] perVertex = new Vector3D[m * n];
		for (int i = 0; i < perVertex.length; ++i)
			perVertex[i] = new Vector3D(0, 0, 0);

		for (int i = 0; i < m - 1; ++i) {
			for (int j = 0; j < n - 1; ++j) {
				perVertex[i * n + j] = perVertex[i * n + j].add(perFace[2 * (i
						* (n - 1) + j)]);
				perVertex[i * n + j + 1] = perVertex[i * n + j]
						.add(perFace[2 * (i * (n - 1) + j)]);
				perVertex[(i + 1) * n + j + 1] = perVertex[i * n + j]
						.add(perFace[2 * (i * (n - 1) + j)]);

				perVertex[i * n + j] = perVertex[i * n + j].add(perFace[2 * (i
						* (n - 1) + j) + 1]);
				perVertex[(i + 1) * n + j + 1] = perVertex[i * n + j]
						.add(perFace[2 * (i * (n - 1) + j) + 1]);
				perVertex[(i + 1) * n + j] = perVertex[i * n + j]
						.add(perFace[2 * (i * (n - 1) + j) + 1]);
			}
		}

		for (int i = 0; i < perVertex.length; ++i) {
			perVertex[i].normalize();
			normals[i] = Util.vectorToDoubleArray(perVertex[i]);
		}
	}
}
