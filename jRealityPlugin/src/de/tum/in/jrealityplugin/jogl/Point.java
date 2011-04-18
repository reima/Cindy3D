package de.tum.in.jrealityplugin.jogl;

import java.awt.Color;

class Point extends Primitive {
	double x, y, z;
	double size;
	Color color;

	public Point(double x, double y, double z, double size, Color color) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.size = size;
		this.color = color;
	}

	@Override
	public String toString() {
		return "[" + x + "," + y + "," + z + "]";
	}
}