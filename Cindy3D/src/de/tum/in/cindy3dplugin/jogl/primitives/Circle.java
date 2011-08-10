package de.tum.in.cindy3dplugin.jogl.primitives;

import org.apache.commons.math.geometry.Vector3D;

import de.tum.in.cindy3dplugin.jogl.Material;

/**
 * Circle primitive.
 */
public class Circle extends Primitive {
	/**
	 * Center of the circle
	 */
	private Vector3D center;
	/**
	 * Normal of the circle's plane
	 */
	private Vector3D normal;
	/**
	 * Radius of the circle
	 */
	private double radius;

	/**
	 * Constructs a new circle.
	 * 
	 * @param centerX
	 *            x coordinate of center
	 * @param centerY
	 *            y coordinate of center
	 * @param centerZ
	 *            z coordinate of center
	 * @param normalX
	 *            x component of normal vector
	 * @param normalY
	 *            y component of normal vector
	 * @param normalZ
	 *            z component of normal vector
	 * @param radius
	 *            radius
	 * @param material
	 *            material
	 */
	public Circle(double centerX, double centerY, double centerZ,
			double normalX, double normalY, double normalZ, double radius,
			Material material) {
		super(material);

		center = new Vector3D(centerX, centerY, centerZ);
		normal = new Vector3D(normalX, normalY, normalZ);
		this.radius = radius;
	}

	/**
	 * @return center of the circle
	 */
	public Vector3D getCenter() {
		return center;
	}

	/**
	 * @return normal of the circle's plane
	 */
	public Vector3D getNormal() {
		return normal;
	}

	/**
	 * @return radius of the circle
	 */
	public double getRadius() {
		return radius;
	}
}
