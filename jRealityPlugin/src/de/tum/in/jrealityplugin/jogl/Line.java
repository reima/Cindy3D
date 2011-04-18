package de.tum.in.jrealityplugin.jogl;

import java.awt.Color;

import org.apache.commons.math.geometry.Vector3D;

public class Line extends Primitive{
	
	public enum LineType {
		SEGMENT,
		RAY,
		LINE
	}
	
	Vector3D p1;
	Vector3D p2;
	double radius;
	LineType lineType;

	public Line(double x1, double y1, double z1,
				double x2, double y2, double z2,
				double radius, Color color, LineType lineType) {
		
		super(color, 1);
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
