package de.tum.in.jrealityplugin.jogl;

import java.awt.Color;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class Polygon {

	Point3d positions[];
	Vector3d normals[];

	Color color;

	public Polygon(double[][] positions, double[][] normals,
			Color color) {

		this.positions = new Point3d[positions.length];
		this.normals = new Vector3d[positions.length];

		for (int i = 0; i < positions.length; ++i)
			this.positions[i] = new Point3d(positions[i][0],positions[i][1],positions[i][2]);
		if (normals != null)
			for (int i = 0; i < positions.length; ++i)
				this.normals[i] = new Vector3d(normals[i][0], normals[i][1], normals[i][2]);
		else {
			Vector3d v1 = new Vector3d();
			Vector3d v2 = new Vector3d();

			v1.sub(this.positions[2], this.positions[0]);
			v2.sub(this.positions[1], this.positions[0]);

			for (int i = 0; i < this.normals.length; ++i) {
				this.normals[i] = new Vector3d();
				this.normals[i].cross(v2, v1);
				this.normals[i].normalize();
			}
		}
		this.color = color;
	}

	@Override
	public String toString() {
		String str = "[";
		for (int i = 0; i < positions.length; ++i)
			str += "[" + positions[i].x + "," + positions[i].y + ","
					+ positions[i].z + "]";
		str += "]";
		return str;
	}
}
