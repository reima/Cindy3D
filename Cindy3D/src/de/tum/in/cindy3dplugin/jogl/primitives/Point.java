package de.tum.in.cindy3dplugin.jogl.primitives;

import java.awt.Color;

import org.apache.commons.math.geometry.Vector3D;

public class Point extends Primitive {
	public Vector3D center;
	public double size;

	public Point(double x, double y, double z, double size, Color color,
			int shininess, double alpha) {
		super(color, shininess, alpha);
		this.center = new Vector3D(x, y, z);
		this.size = size;
	}

	@Override
	public String toString() {
		return center.toString();
	}
}