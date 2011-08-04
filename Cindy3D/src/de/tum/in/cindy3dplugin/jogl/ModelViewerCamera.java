package de.tum.in.cindy3dplugin.jogl;

import org.apache.commons.math.geometry.Rotation;
import org.apache.commons.math.geometry.RotationOrder;
import org.apache.commons.math.geometry.Vector3D;
import org.apache.commons.math.linear.LUDecompositionImpl;
import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;

public class ModelViewerCamera {
	private static final double ROTATE_SENSITIVITY = 0.01;
	private static final double PAN_SENSITIVITY = 0.05;
	
	private Rotation rotation;
	private Vector3D position;
	private Vector3D lookAt;
	private Vector3D up;
	private RealMatrix transform;
	
	private double zNear, zFar;
	private double fieldOfView; // In degrees
	private double aspectRatio;
	private RealMatrix perspectiveTransform;
	
	private double lodFactor;
	
	private Plane[] clippingPlanes = new Plane[6];
	
	public ModelViewerCamera() {
		rotation = Rotation.IDENTITY;
		position = new Vector3D(0, 0, 1);
		lookAt = new Vector3D(0, 0, 0);
		up = new Vector3D(0, 1, 0);
		transform = MatrixUtils.createRealMatrix(4, 4);
		updateTransform();
		
		zNear = 0.1;
		zFar = 100.0;
		fieldOfView = 45.0;
		aspectRatio = 4.0/3.0;
		perspectiveTransform = MatrixUtils.createRealMatrix(4, 4);
		updatePerspectiveTransform();		
	}
	
	private void updatePerspectiveTransform() {
		double f = 1.0/Math.tan(Math.toRadians(fieldOfView)*0.5);
		double nearMinusFar = zNear - zFar;
		perspectiveTransform.setEntry(0, 0, f/aspectRatio);
		perspectiveTransform.setEntry(1, 1, f);
		perspectiveTransform.setEntry(2, 2, (zFar + zNear)
				/ nearMinusFar);
		perspectiveTransform.setEntry(2, 3, 2 * zFar * zNear
				/ nearMinusFar);
		perspectiveTransform.setEntry(3, 2, -1);
	}

	protected void updateTransform() {
		// Lookat
		Vector3D forward = lookAt.subtract(position);
		Rotation lookAtRotation = new Rotation(up, forward, Vector3D.PLUS_J,
				Vector3D.MINUS_K);
		RealMatrix lookAtMatrix = MatrixUtils.createRealIdentityMatrix(4);
		lookAtMatrix.setSubMatrix(lookAtRotation.getMatrix(), 0, 0);
		lookAtMatrix.setColumn(
				3,
				new double[] { -position.getX(), -position.getY(),
						-position.getZ(), 1 });
		
		// Model rotation
		RealMatrix modelRotationMatrix = MatrixUtils
				.createRealIdentityMatrix(4);
		modelRotationMatrix.setSubMatrix(rotation.getMatrix(), 0, 0);
		
		transform = lookAtMatrix.multiply(modelRotationMatrix);
	}
	
	public void lookAt(Vector3D position, Vector3D lookAt, Vector3D up) {
		this.position = position;
		this.up = up;
		this.lookAt = lookAt;
		updateTransform();
	}

	public RealMatrix getTransform() {
		return transform;
	}
	
	public void setPerspective(double fieldOfView, int width, int height, double zNear, double zFar) {
		if (height <= 0)
			height = 1;
		this.aspectRatio = (double) width / height;
		this.fieldOfView = fieldOfView;
		this.zNear = zNear;
		this.zFar = zFar;
		updatePerspectiveTransform();
		
		lodFactor = 2.0 * Math.tan(0.5 * fieldOfView / aspectRatio)
				/ ((double) height); 
	}
	
	public RealMatrix getPerspectiveTransform() {
		return perspectiveTransform;
	}
	
	public Rotation getRotation() {
		return rotation;
	}

	public void setRotation(Rotation rotation) {
		this.rotation = rotation;
		updateTransform();
	}

	public Vector3D getPosition() {
		return position;
	}

	public void setPosition(Vector3D position) {
		this.position = position;
		updateTransform();
	}

	public Vector3D getLookAt() {
		return lookAt;
	}

	public void setLookAt(Vector3D lookAt) {
		this.lookAt = lookAt;
		updateTransform();
	}

	public Vector3D getUp() {
		return up;
	}

	public void setUp(Vector3D up) {
		this.up = up;
		updateTransform();
	}
	
	public double getFieldOfView() {
		return fieldOfView;
	}
	
	public double getAspectRatio() {
		return aspectRatio;
	}
	
	public double getZNear() {
		return zNear;
	}
	
	public double getZFar() {
		return zFar;
	}

	public void mouseDragged1(double dx, double dy) {
		rotation = new Rotation(RotationOrder.XYZ, dy * ROTATE_SENSITIVITY, dx
				* ROTATE_SENSITIVITY, 0).applyTo(rotation);

		updateTransform();
	}

	public void mouseDragged2(double dx, double dy) {
		Vector3D forward = lookAt.subtract(position).normalize();
		Vector3D right = Vector3D.crossProduct(forward, up).normalize();
		
		Vector3D movement = new Vector3D(0, 0, 0)
			.add(-dx * PAN_SENSITIVITY, right)
			.add(dy * PAN_SENSITIVITY, up);
		
		position = position.add(movement);
		lookAt = lookAt.add(movement);
		
		updateTransform();
	}

	public void mouseWheelMoved(int wheelRotation) {
		if (wheelRotation == 0) return;
		Vector3D lookAtToPosition = position.subtract(lookAt);
		Vector3D newPosition;
		if (wheelRotation > 0) {
			newPosition = lookAt.add(1.1, lookAtToPosition);
		} else {
			newPosition = lookAt.add(1.0/1.1, lookAtToPosition);
		}
		setPosition(newPosition);
	}

	public Plane[] buildClippingPlanes() {
		// Get the inverse of the projection matrix
		RealMatrix invProjection = new LUDecompositionImpl(perspectiveTransform)
				.getSolver().getInverse();
		
		// Coordinates of the normalized view frustum
		RealVector[] f = new RealVector[] {
				MatrixUtils.createRealVector(new double[] { -1, -1,  1, 1 }),
				MatrixUtils.createRealVector(new double[] { -1, -1, -1, 1 }),
				MatrixUtils.createRealVector(new double[] { -1,  1, -1, 1 }),
				MatrixUtils.createRealVector(new double[] {  1,  1, -1, 1 }),
				MatrixUtils.createRealVector(new double[] {  1,  1,  1, 1 }),
				MatrixUtils.createRealVector(new double[] {  1, -1,  1, 1 })
		};

		// Transform view frustum into camera space
		for (int i = 0; i < 6; ++i) {
			f[i] = invProjection.operate(f[i]);
			f[i].mapDivideToSelf(f[i].getEntry(3));
		}

		Vector3D[] frustumVertices = new Vector3D[6];
		for (int i = 0; i < 6; ++i) {
			frustumVertices[i] = new Vector3D(f[i].getEntry(0),
					f[i].getEntry(1), f[i].getEntry(2));
		}

		for (int i = 0; i < 6; ++i) {
			Vector3D v1 = frustumVertices[(i + 1) % 6]
					.subtract(frustumVertices[i]);
			Vector3D v2 = frustumVertices[(i + 2) % 6]
					.subtract(frustumVertices[i]);

			clippingPlanes[i] = new Plane(Vector3D.crossProduct(v1, v2)
					.normalize(), frustumVertices[i]);
		}
		
		return getClippingPlanes();
	}
	
	public Plane[] getClippingPlanes() {
		return clippingPlanes;
	}
	
	public double getWorldSpaceError(float screenSpaceError, float distance) {
		return lodFactor * screenSpaceError * distance;
	}
}
