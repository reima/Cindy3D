package de.tum.in.cindy3dplugin.jogl.primitives;

import java.awt.Color;

import org.apache.commons.math.geometry.Vector3D;

public class Line extends Primitive {
	public enum LineType {
		SEGMENT,
		RAY,
		LINE
	}

	public Vector3D p1;
	public Vector3D p2;
	public double radius;
	public LineType lineType;

	public Line(double x1, double y1, double z1,
				double x2, double y2, double z2,
				double radius, Color color, int shininess,
				LineType lineType) {
		
		super(color, shininess, 1);
		p1 = new Vector3D(x1, y1, z1);
		p2 = new Vector3D(x2, y2, z2);
		this.radius = radius;
		this.lineType = lineType;
	}

	@Override
	public String toString() {
		return p1.toString() + " - " + p2.toString();
	}
}
