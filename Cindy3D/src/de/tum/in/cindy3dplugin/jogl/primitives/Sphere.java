package de.tum.in.cindy3dplugin.jogl.primitives;

import java.awt.Color;

import org.apache.commons.math.geometry.Vector3D;

public class Sphere extends Primitive {
	public Vector3D center;
	public double radius;

	public Sphere(double x, double y, double z, double radius, Color color,
			double shininess, double alpha) {
		super(color, shininess, alpha);
		this.center = new Vector3D(x, y, z);
		this.radius = radius;
	}

	@Override
	public String toString() {
		return center.toString();
	}
}