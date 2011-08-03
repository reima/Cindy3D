package de.tum.in.cindy3dplugin.jogl.primitives.renderers;

import org.apache.commons.math.geometry.Rotation;
import org.apache.commons.math.geometry.Vector3D;
import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealMatrix;

import de.tum.in.cindy3dplugin.jogl.ModelViewerCamera;
import de.tum.in.cindy3dplugin.jogl.Plane;
import de.tum.in.cindy3dplugin.jogl.primitives.Line;
import de.tum.in.cindy3dplugin.jogl.primitives.Line.LineType;

public abstract class LineRendererBase extends PrimitiveRenderer<Line> {

	protected static class Endpoints {
		public Vector3D p1;
		public Vector3D p2;

		public Endpoints(Vector3D p1, Vector3D p2) {
			this.p1 = p1;
			this.p2 = p2;
		}
	}

	protected static Endpoints clipLineAtFrustum(ModelViewerCamera camera,
			Vector3D p1, Vector3D p2, LineType lineType) {

		// Compute orientation of the cylinder and its length, assuming
		// a line segment is about to be drawn
		Vector3D direction = p2.subtract(p1).normalize();
		// direction = direction.normalize();

		Plane[] planes = camera.buildClippingPlanes();

		// In case, no line segment should be drawn, a ray or line is drawn
		// So the intersection points with the view frustum are computed
		if (lineType != LineType.SEGMENT) {
			double min = Double.MAX_VALUE;
			double max = Double.MIN_VALUE;
			for (int i = 0; i < 6; ++i) {
				double lambda = planes[i].intersectRay(p1, direction);
				if (lambda == Double.MAX_VALUE)
					continue;
				else {
					min = Math.min(min, lambda);
					max = Math.max(max, lambda);
				}
			}

			// For each line or ray, the second point is to be shifted to
			// infinity. As we can only see the ray/line until it leaves
			// the view frustum, the point is shifted to the
			// ray/line-frustum intersection, with maximum distance to p1
			p2 = new Vector3D(1, p1, max, direction);
			if (lineType == LineType.LINE) {
				// In case we want to draw a line, the first point should
				// be shifted to infinity as well, here, it is shifted to
				// the minimum intersection point
				p1 = new Vector3D(1, p1, min, direction);
			}
		}

		return new Endpoints(p1, p2);
	}

	protected static RealMatrix buildOBBTransform(Endpoints endPoints,
			double radius) {

		// After shifting the end points of the ray/line to the maximal
		// visible positions, the size and orientation fo the OBB is needed

		// Length of the OBB
		double dist = Vector3D.distance(endPoints.p1, endPoints.p2) / 2.0;
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
		// scaling the OBB, then rotating it and finaling translating it
		// into the final the line fitting position
		RealMatrix cylinder = translationMatrix.multiply(rotationMatrix)
				.multiply(scaleMatrix);

		return cylinder;
	}
}
