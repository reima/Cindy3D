package de.tum.in.cindy3dplugin.jogl.primitives.renderers;

import org.apache.commons.math.geometry.Rotation;
import org.apache.commons.math.geometry.Vector3D;
import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealMatrix;

import de.tum.in.cindy3dplugin.jogl.Util;
import de.tum.in.cindy3dplugin.jogl.primitives.Circle;

public abstract class CircleRendererBase extends PrimitiveRenderer<Circle> {
	protected static float[] buildTransform(Circle circle) {
		RealMatrix transform = MatrixUtils.createRealIdentityMatrix(4);
		transform.setColumn(3, new double[] { circle.getCenter().getX(),
				circle.getCenter().getY(), circle.getCenter().getZ(), 1 });

		Rotation rotation = new Rotation(Vector3D.PLUS_K, circle.getNormal());
		RealMatrix rotationMatrix = MatrixUtils.createRealIdentityMatrix(4);
		rotationMatrix.setSubMatrix(rotation.getMatrix(), 0, 0);

		RealMatrix scaleMatrix = MatrixUtils
				.createRealDiagonalMatrix(new double[] { circle.getRadius(),
						circle.getRadius(), circle.getRadius(), 1 });

		transform = transform.multiply(rotationMatrix).multiply(scaleMatrix);

		return Util.matrixToFloatArray(transform);
	}
}
