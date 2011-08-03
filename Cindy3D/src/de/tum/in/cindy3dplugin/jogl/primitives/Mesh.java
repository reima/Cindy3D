package de.tum.in.cindy3dplugin.jogl.primitives;

import java.awt.Color;

import org.apache.commons.math.geometry.Vector3D;

import de.tum.in.cindy3dplugin.Cindy3DViewer.MeshTopology;
import de.tum.in.cindy3dplugin.Cindy3DViewer.NormalType;
import de.tum.in.cindy3dplugin.jogl.Util;

public class Mesh extends Primitive {
	private static int meshCounter = 0;

	private int gridWidth, gridHeight;

	private double[][] positions;
	private double[][] normals;

	private NormalType normalType;

	private int identifier;
	private MeshTopology topology;

	private int gridXMax, gridYMax;
	private int faceCount;

	public Mesh(int height, int width, double[][] positions,
			double[][] normals, Color color, int shininess, double alpha,
			MeshTopology topology) {
		super(color, shininess, alpha);
		init(width, height, positions, normals,
				normals == null ? NormalType.PER_VERTEX : NormalType.PER_FACE,
				topology);
		if (normals == null) {
			computeNormals();
		}
	}

	public Mesh(int height, int width, double[][] positions,
			NormalType normalType, Color color, int shininess, double alpha,
			MeshTopology topology) {
		super(color, shininess, alpha);
		init(width, height, positions, null, normalType, topology);
		computeNormals();
	}

	private void init(int width, int height, double[][] positions,
			double[][] normals, NormalType normalType, MeshTopology topology) {
		this.gridWidth = width;
		this.gridHeight = height;
		this.positions = positions;
		this.normals = normals;
		this.normalType = normalType;
		this.topology = topology;
		this.identifier = meshCounter++;

		switch (this.topology) {
		case OPEN:
			gridXMax = gridWidth - 1;
			gridYMax = gridHeight - 1;
			break;
		case CLOSE_X:
			gridXMax = gridWidth;
			gridYMax = gridHeight - 1;
			break;
		case CLOSE_Y:
			gridXMax = gridWidth - 1;
			gridYMax = gridHeight;
			break;
		case CLOSE_XY:
			gridXMax = gridWidth;
			gridYMax = gridHeight;
			break;
		default:
			return;
		}

		faceCount = gridXMax * gridYMax * 2;
	}

	public double[][] getPositions() {
		return positions;
	}

	public double[][] getNormals() {
		return normals;
	}

	public NormalType getNormalType() {
		return normalType;
	}

	public int getIdentifier() {
		return identifier;
	}

	public int getFaceCount() {
		return faceCount;
	}

	public int getGridXMax() {
		return gridXMax;
	}

	public int getGridYMax() {
		return gridYMax;
	}

	public int getGridWidth() {
		return gridWidth;
	}

	public int getGridHeight() {
		return gridHeight;
	}

	public int getVertexIndex(int x, int y) {
		return y * gridWidth + x;
	}

	private void computeNormals() {
		normals = null;

		Vector3D[] faceNormals = new Vector3D[faceCount];
		Vector3D[] vertexNormals = null;

		if (normalType == NormalType.PER_VERTEX) {
			vertexNormals = new Vector3D[gridWidth * gridHeight];
			for (int i = 0; i < vertexNormals.length; ++i) {
				vertexNormals[i] = Vector3D.ZERO;
			}
		}

		Vector3D[] positions = new Vector3D[gridWidth * gridHeight];
		for (int i = 0; i < positions.length; ++i) {
			positions[i] = new Vector3D(this.positions[i][0],
					this.positions[i][1], this.positions[i][2]);
		}

		// Iterate over all grid cells (each of which consists of two faces)
		int faceIndex = 0;
		for (int gridY = 0; gridY < gridYMax; ++gridY) {
			for (int gridX = 0; gridX < gridXMax; ++gridX) {
				/*
				 *    v1----v2
				 *    |    / |
				 *    |f1 /  |
				 *    |  / f2|
				 *    | /    |
				 *    v3----v4
				 */
				int gridXPlus1 = (gridX + 1) % gridWidth;
				int gridYPlus1 = (gridY + 1) % gridHeight;

				int v1Index = getVertexIndex(gridX, gridY);
				int v2Index = getVertexIndex(gridXPlus1, gridY);
				int v3Index = getVertexIndex(gridX, gridYPlus1);
				int v4Index = getVertexIndex(gridXPlus1, gridYPlus1);

				Vector3D v1 = positions[v1Index];
				Vector3D v2 = positions[v2Index];
				Vector3D v3 = positions[v3Index];
				Vector3D v4 = positions[v4Index];

				int f1Index = faceIndex++;
				int f2Index = faceIndex++;

				faceNormals[f1Index] = Vector3D.crossProduct(v2.subtract(v1),
						v3.subtract(v1)).normalize();
				faceNormals[f2Index] = Vector3D.crossProduct(v3.subtract(v4),
						v2.subtract(v4)).normalize();

				if (normalType == NormalType.PER_VERTEX) {
					vertexNormals[v1Index] = vertexNormals[v1Index]
							.add(faceNormals[f1Index]);
					vertexNormals[v2Index] = vertexNormals[v2Index].add(
							faceNormals[f1Index]).add(faceNormals[f2Index]);
					vertexNormals[v3Index] = vertexNormals[v3Index].add(
							faceNormals[f1Index]).add(faceNormals[f2Index]);
					vertexNormals[v4Index] = vertexNormals[v4Index]
							.add(faceNormals[f2Index]);
				}
			}
		}

		if (normalType == NormalType.PER_VERTEX) {
			normals = new double[gridWidth * gridHeight][3];
			for (int i = 0; i < vertexNormals.length; ++i) {
				normals[i] = Util.vectorToDoubleArray(vertexNormals[i]
						.normalize());
			}
		} else if (normalType == NormalType.PER_FACE) {
			normals = new double[faceCount][3];
			for (int i = 0; i < normals.length; ++i) {
				normals[i] = Util.vectorToDoubleArray(faceNormals[i]);
			}
		}
	}
}
