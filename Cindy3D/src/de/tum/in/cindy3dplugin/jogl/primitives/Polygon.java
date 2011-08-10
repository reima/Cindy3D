package de.tum.in.cindy3dplugin.jogl.primitives;

import org.apache.commons.math.geometry.Vector3D;

import de.tum.in.cindy3dplugin.jogl.Material;

/**
 * Polygon primitive.
 */
public class Polygon extends Primitive {
	/**
	 * Vertex positions
	 */
	private Vector3D positions[];
	/**
	 * Vertex normals
	 */
	private Vector3D normals[];

	/**
	 * Constructs a new polygon.
	 * 
	 * If <strong>normals</strong> is null, the vector orthogonal to the plane
	 * defined by the first three vertices is assumed as a common normal for all
	 * vertices.
	 * 
	 * @param positions
	 *            vertex positions
	 * @param normals
	 *            normal positions
	 * @param material
	 *            material
	 */
	public Polygon(double[][] positions, double[][] normals, Material material) {
		super(material);
		this.positions = new Vector3D[positions.length];
		this.normals = new Vector3D[positions.length];

		for (int i = 0; i < positions.length; ++i) {
			this.positions[i] = new Vector3D(positions[i][0], positions[i][1],
					positions[i][2]);
		}

		if (normals != null) {
			for (int i = 0; i < positions.length; ++i) {
				this.normals[i] = new Vector3D(normals[i][0], normals[i][1],
						normals[i][2]);
			}
		} else {
			Vector3D v1 = this.positions[2].subtract(this.positions[0]);
			Vector3D v2 = this.positions[1].subtract(this.positions[0]);
			Vector3D normal = Vector3D.crossProduct(v2, v1).normalize();

			for (int i = 0; i < this.normals.length; ++i) {
				this.normals[i] = normal;
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String str = "[";
		for (int i = 0; i < positions.length; ++i)
			str += positions[i].toString();
		str += "]";
		return str;
	}

	/**
	 * @return vertex positions of the polygon
	 */
	public Vector3D[] getPositions() {
		return positions;
	}

	/**
	 * @return vertex normals of the polygon
	 */
	public Vector3D[] getNormals() {
		return normals;
	}
}
