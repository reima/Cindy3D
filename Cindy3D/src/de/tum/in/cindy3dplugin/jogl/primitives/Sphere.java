package de.tum.in.cindy3dplugin.jogl.primitives;

import java.awt.Color;

import org.apache.commons.math.geometry.Vector3D;

/**
 * Sphere primitive.
 */
public class Sphere extends Primitive {
	/**
	 * Center point of the sphere
	 */
	private Vector3D center;
	/**
	 * Radius of the sphere
	 */
	private double radius;

	/**
	 * Constructs a new sphere.
	 * 
	 * @param x
	 *            x coordinate of center
	 * @param y
	 *            y coordinate of center
	 * @param z
	 *            z coordinate of center
	 * @param radius
	 *            radius
	 * @param color
	 *            color
	 * @param shininess
	 *            shininess
	 * @param alpha
	 *            alpha value
	 */
	public Sphere(double x, double y, double z, double radius, Color color,
			double shininess, double alpha) {
		super(color, shininess, alpha);
		this.center = new Vector3D(x, y, z);
		this.radius = radius;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return center.toString();
	}

	/**
	 * @return center of the sphere
	 */
	public Vector3D getCenter() {
		return center;
	}

	/**
	 * @return radius of the sphere
	 */
	public double getRadius() {
		return radius;
	}
}