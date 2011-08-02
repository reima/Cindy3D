package de.tum.in.cindy3dplugin.jogl.primitives;

import java.awt.Color;

public class Circle extends Primitive {
	public double centerX, centerY, centerZ;
	public double normalX, normalY, normalZ;
	public double radius;
	
	public Circle(double centerX, double centerY, double centerZ,
			double normalX, double normalY, double normalZ, double radius,
			Color color, double shininess, double alpha) {
		super(color, shininess, alpha);
		this.centerX = centerX;
		this.centerY = centerY;
		this.centerZ = centerZ;
		this.normalX = normalX;
		this.normalY = normalY;
		this.normalZ = normalZ;
		this.radius = radius;
	}
}
