package de.tum.in.jrealityplugin.jogl;

import java.awt.Color;

class Point extends Primitive {
	double x, y, z;
	double size;

	public Point(double x, double y, double z, double size, Color color) {
		super(color, 1.0);
		this.x = x;
		this.y = y;
		this.z = z;
		this.size = size;
	}

	@Override
	public String toString() {
		return "[" + x + "," + y + "," + z + "]";
	}
}