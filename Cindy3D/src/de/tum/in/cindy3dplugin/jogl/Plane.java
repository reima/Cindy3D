package de.tum.in.cindy3dplugin.jogl;

import org.apache.commons.math.geometry.Vector3D;

/**
 * Plane in three-dimensional space.
 */
public class Plane {
	/**
	 * Plane normal
	 */
	private Vector3D normal;
	/**
	 * Distance of plane from origin
	 */
	private double distance;
	
	/**
	 * Constructs a new plane.
	 * 
	 * @param normal
	 * 			plane normal
	 * @param position
	 * 			coordinates of a point on plane
	 */
	public Plane(Vector3D normal, Vector3D position) {
		this(normal, -Vector3D.dotProduct(normal, position));
	}
	
	/**
	 * Constructs a new plane.
	 * 
	 * @param normal
	 * 			plane normal
	 * @param distance
	 * 			distance of plane from origin
	 */
	public Plane(Vector3D normal, double distance) {
		this.normal = normal;
		this.distance = distance;
	}

	/**
	 * Computes the intersection between a ray and the plane.
	 * 
	 * @param rayOrigin
	 * 			coordinates of the ray origin
	 * @param rayDirection
	 * 			ray direction as a vector
	 * @return
	 * 			distance from origin in given direction to intersection point
	 */
	public double intersectRay(Vector3D rayOrigin, Vector3D rayDirection) {
		double denom = Vector3D.dotProduct(rayDirection, normal);
		// Ray parallel to plane, so no intersection point is found
		if (Math.abs(denom) < 10E-8) {
			return Double.MAX_VALUE;
		}
		double lambda = -(Vector3D.dotProduct(rayOrigin, normal) + distance)
				/ denom;
		return lambda;
	}

	/**
	 * Computes signed distance between a point and the plane.
	 * 
	 * @param point
	 * 			point coordinates
	 * @return
	 * 			signed distance from point to plane
	 */
	public double distance(Vector3D point) {
		return Vector3D.dotProduct(normal, point) + distance;
	}
}
