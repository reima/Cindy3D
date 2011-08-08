package de.tum.in.cindy3dplugin.jogl.primitives;

import java.awt.Color;

import org.apache.commons.math.geometry.Vector3D;

/**
 * Line primitive.
 */
public class Line extends Primitive {
	/**
	 * Line type
	 */
	public enum LineType {
		/**
		 * Line segment
		 */
		SEGMENT,
		/**
		 * Ray (half-line)
		 */
		RAY,
		/**
		 * Line
		 */
		LINE
	}

	/**
	 * First point on line
	 */
	private Vector3D p1;
	/**
	 * Second point on line
	 */
	private Vector3D p2;
	/**
	 * Radius of tube representing the line
	 */
	public double radius;
	/**
	 * Line type
	 */
	public LineType lineType;

	/**
	 * Constructs a new line.
	 * 
	 * @param x1
	 *            x component of first point on line
	 * @param y1
	 *            y component of first point on line
	 * @param z1
	 *            z component of first point on line
	 * @param x2
	 *            x component of second point on line
	 * @param y2
	 *            y component of second point on line
	 * @param z2
	 *            z component of second point on line
	 * @param radius
	 *            radius of tube representing the line
	 * @param color
	 *            color
	 * @param shininess
	 *            shininess
	 * @param lineType
	 *            line type
	 */
	public Line(double x1, double y1, double z1, double x2, double y2,
			double z2, double radius, Color color, double shininess,
			LineType lineType) {
		super(color, shininess, 1);
		p1 = new Vector3D(x1, y1, z1);
		p2 = new Vector3D(x2, y2, z2);
		this.radius = radius;
		this.lineType = lineType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return p1.toString() + " - " + p2.toString();
	}

	/**
	 * @return first point on the line
	 */
	public Vector3D getFirstPoint() {
		return p1;
	}
	
	/**
	 * @return second point on the line
	 */
	public Vector3D getSecondPoint() {
		return p2;
	}
}
