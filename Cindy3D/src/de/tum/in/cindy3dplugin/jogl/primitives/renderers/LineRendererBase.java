package de.tum.in.cindy3dplugin.jogl.primitives.renderers;

import org.apache.commons.math.geometry.Rotation;
import org.apache.commons.math.geometry.Vector3D;
import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealMatrix;

import de.tum.in.cindy3dplugin.jogl.ModelViewerCamera;
import de.tum.in.cindy3dplugin.jogl.Plane;
import de.tum.in.cindy3dplugin.jogl.primitives.Line;
import de.tum.in.cindy3dplugin.jogl.primitives.Line.LineType;

/**
 * Base class for different kinds of line, ray and line segments renderers.
 * 
 * This intermediate class contains methods needed by more than one specialized
 * line renderer.
 */
public abstract class LineRendererBase extends PrimitiveRenderer<Line> {
	/**
	 * Internal class representing the two end points of a line segment.
	 */
	protected static class Endpoints {
		/**
		 * First end point.
		 */
		public Vector3D p1;
		/**
		 * Second end point.
		 */
		public Vector3D p2;

		/**
		 * Constructs a new Endpoint instance with the two given end points.
		 * 
		 * @param p1
		 *            first end point
		 * @param p2
		 *            second end point
		 */
		public Endpoints(Vector3D p1, Vector3D p2) {
			this.p1 = p1;
			this.p2 = p2;
		}
	}
	
	/**
	 * Clips a given line or ray at the camera view frustum. As lines and rays
	 * have infinite length, they are clipped against the camera view frustum,
	 * which results in a finite line segment representing the potentially
	 * visible part of the line or ray.
	 * <ol>
	 * <li>A line is represented by two points <code>p1</code> and
	 * <code>p2</code>.
	 * <li>A ray is represented by its starting point <code>p1</code> and
	 * another point <code>p2</code>.
	 * <li>Line segments are not clipped even if they are not or only partially
	 * in the view frustum.
	 * </ol>
	 * 
	 * @param camera
	 *            current camera containing the view frustum
	 * @param p1
	 *            first point on line or ray starting point
	 * @param p2
	 *            second point on line or ray
	 * @param radius
	 *            radius of the cylinder representing the line
	 * @param lineType
	 *            line type
	 * @return end points of the potentially visible segment of the line. If
	 *         nothing is visible, both end points are NaN.
	 */
	protected static Endpoints clipLineAtFrustum(ModelViewerCamera camera,
			Vector3D p1, Vector3D p2, double radius, LineType lineType) {
		// Compute direction for intersection tests
		Vector3D direction = p2.subtract(p1).normalize();

		Plane[] planes = camera.getClippingPlanes();

		// In case no line segment should be drawn, a ray or line is drawn.
		// So the intersection points with the view frustum are computed.
		if (lineType != LineType.SEGMENT) {
			double min = Double.MAX_VALUE;
			double max = -Double.MAX_VALUE;

			// Intersect ray with frustum planes shifted by the line radius.
			// In this way we can ensure that when we later draw a line segment
			// as a cylinder with the given radius, that it is fully visible.
			for (int i = 0; i < 6; ++i) {
				double lambda = planes[i].intersectRayWithShift(p1, direction,
						radius);
				if (lambda == Double.MAX_VALUE) {
					continue;
				}

				Vector3D p = p1.add(lambda, direction);

				boolean inFrustum = true;
				for (int j = 0; j < 6; ++j) {
					if (j == i) {
						continue;
					}

					if (planes[j].distance(p) > radius) {
						inFrustum = false;
						break;
					}
				}

				if (inFrustum) {
					min = Math.min(min, lambda);
					max = Math.max(max, lambda);
				}
			}

			// The ray or line doesn't intersect the camera frustum at all, so
			// nothing is visible. Signal this by setting both end points to
			// NaN.
			if (min == Double.MAX_VALUE) {
				p1 = p2 = Vector3D.NaN;
			}

			// Set second end point to frustum intersection with the maximum
			// signed distance to p1.
			p2 = new Vector3D(1, p1, max, direction);
			if (lineType == LineType.LINE) {
				// In case we want to draw a line, set the first end point to
				// the frustrum intersection with minimim signed distance to p2.
				p1 = new Vector3D(1, p1, min, direction);
			}
		}

		return new Endpoints(p1, p2);
	}

	/**
	 * Computes a transformation matrix transforming a standard axis-aligned
	 * bounding box (AABB) into an oriented bounding box (OBB). The standard
	 * AABB box is defined by its minimum (-1, -1, -1) and its maximum (1, 1,
	 * 1). The main axis of the OBB is defined by the line containing both
	 * points of <code>endPoints</code>. The distance between these two end
	 * points also implies the OBB size on this axis. The two other axes are not
	 * further defined except that they are perpendicular to the main axis and
	 * perpendicular to each other to build a regular box. As a line is
	 * represented as a tube, the box size on these two axes is the tube radius
	 * <code>radius</code> times 2.
	 * 
	 * @param endPoints
	 *            end points of the line segment, defining the main orientation
	 *            of the OBB
	 * @param radius
	 *            radius of the tube representing the line segment
	 * @return transformation matrix
	 */
	protected static RealMatrix buildOBBTransform(Endpoints endPoints,
			double radius) {
		// Length of the OBB
		double dist = 0.5 * Vector3D.distance(endPoints.p1, endPoints.p2);
		Vector3D direction = endPoints.p2.subtract(endPoints.p1).normalize();

		// Midpoint of OBB
		Vector3D avg = new Vector3D(0.5, endPoints.p1, 0.5, endPoints.p2);

		// Translate OBB's center to origin
		RealMatrix translationMatrix = MatrixUtils.createRealIdentityMatrix(4);
		translationMatrix.setColumn(3, new double[] { avg.getX(), avg.getY(),
				avg.getZ(), 1 });

		// Rotate x-axis to OBB main direction
		Rotation rotation = new Rotation(Vector3D.PLUS_I, direction);
		RealMatrix rotationMatrix = MatrixUtils.createRealIdentityMatrix(4);
		rotationMatrix.setSubMatrix(rotation.getMatrix(), 0, 0);

		// Scale OBB to fit the size of the segment/ray/line
		RealMatrix scaleMatrix = MatrixUtils
				.createRealDiagonalMatrix(new double[] { dist, radius, radius,
						1 });

		// Compose the final transformation matrix for [-1,1]^3 by first
		// scaling the OBB, then rotating it and finally translating it
		// into the line fitting position
		RealMatrix cylinder = translationMatrix.multiply(rotationMatrix)
				.multiply(scaleMatrix);

		return cylinder;
	}
}
