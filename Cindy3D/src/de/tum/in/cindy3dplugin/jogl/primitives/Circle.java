package de.tum.in.cindy3dplugin.jogl.primitives;

import java.awt.Color;

import org.apache.commons.math.geometry.Vector3D;

public class Circle extends Primitive {
	public Vector3D center;
	public Vector3D normal;
	public double radius;
	
	public Circle(double centerX, double centerY, double centerZ,
			double normalX, double normalY, double normalZ, double radius,
			Color color, double shininess, double alpha) {
		super(color, shininess, alpha);
		
		center = new Vector3D(centerX, centerY, centerZ);
		normal = new Vector3D(normalX, normalY, normalZ);
		this.radius = radius;
	}
}
