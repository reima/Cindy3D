package de.tum.in.jrealityplugin.jogl;

import java.awt.Color;

import javax.vecmath.Point3d;

public class Line {
	
	public enum LineType {
		SEGMENT,
		RAY,
		LINE
	}
	
	Point3d p1;
	Point3d p2;
	double radius;
	Color color;
	LineType lineType;

	public Line(double x1, double y1, double z1,
				double x2, double y2, double z2,
				double radius, Color color, LineType lineType) {
		
		p1 = new Point3d(x1, y1, z1);
		p2 = new Point3d(x2, y2, z2);
		this.radius = radius;
		this.color = color;
		this.lineType = lineType;
	}

	@Override
	public String toString() {
		return "[" + p1.x + "," + p1.y + "," + p1.z + "] - ["
			+ p2.x + "," + p2.y + "," + p2.z + "]";
	}
}
