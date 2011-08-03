package de.tum.in.cindy3dplugin.jogl;

import org.apache.commons.math.geometry.Vector3D;

public class Plane {
	private Vector3D normal;
	private double distance;
	
	public Plane(Vector3D normal, Vector3D position) {
		this(normal, -Vector3D.dotProduct(normal, position));
	}
	
	public Plane(Vector3D normal, double distance) {
		this.normal = normal;
		this.distance = distance;
	}

	public double intersectRay(Vector3D rayOrigin, Vector3D rayDirection) {
		double denom = Vector3D.dotProduct(rayDirection, normal);
		if (Math.abs(denom) < 10E-8)
			return Double.MAX_VALUE;
		double lambda = -(Vector3D.dotProduct(rayOrigin, normal) + distance)
				/ denom;
		return lambda;
	}
}
