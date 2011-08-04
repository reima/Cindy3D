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
		transform.setColumn(3, new double[] { circle.center.getX(),
				circle.center.getY(), circle.center.getZ(), 1 });

		Rotation rotation = new Rotation(Vector3D.PLUS_K, new Vector3D(
				circle.normal.getX(), circle.normal.getY(),
				circle.normal.getZ()));
		RealMatrix rotationMatrix = MatrixUtils.createRealIdentityMatrix(4);
		rotationMatrix.setSubMatrix(rotation.getMatrix(), 0, 0);

		RealMatrix scaleMatrix = MatrixUtils
				.createRealDiagonalMatrix(new double[] { circle.radius,
						circle.radius, circle.radius, 1 });

		transform = transform.multiply(rotationMatrix).multiply(scaleMatrix);

		return Util.matrixToFloatArray(transform);
	}
}
