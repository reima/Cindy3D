package de.tum.in.cindy3dplugin.jogl.primitives;

import java.awt.Color;

public class Point extends Primitive {
	public double x;
	public double y;
	public double z;
	public double size;

	public Point(double x, double y, double z, double size, Color color,
			double opacity) {
		super(color, opacity);
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