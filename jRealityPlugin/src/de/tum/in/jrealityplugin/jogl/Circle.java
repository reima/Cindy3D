package de.tum.in.jrealityplugin.jogl;

import java.awt.Color;

public class Circle extends Primitive {
	double centerX, centerY, centerZ;
	double normalX, normalY, normalZ;
	double radius;
	
	public Circle(double centerX, double centerY, double centerZ,
			double normalX, double normalY, double normalZ, double radius,
			Color color) {
		super(color, 0.99);
		this.centerX = centerX;
		this.centerY = centerY;
		this.centerZ = centerZ;
		this.normalX = normalX;
		this.normalY = normalY;
		this.normalZ = normalZ;
		this.radius = radius;
	}
}
