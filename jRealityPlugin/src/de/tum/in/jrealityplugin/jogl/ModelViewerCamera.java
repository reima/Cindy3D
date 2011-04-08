package de.tum.in.jrealityplugin.jogl;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

public class ModelViewerCamera {
	private static final double ROTATE_SENSITIVITY = 0.01;
	
	private Quat4d rotation;
	private Point3d position;
	private Point3d lookAt;
	private Vector3d up;
	
	private Matrix4d transform;
	
	public ModelViewerCamera() {
		rotation = new Quat4d(1, 0, 0, 0);
		position = new Point3d(0, 0, 1);
		lookAt = new Point3d(0, 0, 0);
		up = new Vector3d(0, 1, 0);
		transform = new Matrix4d();
		updateTransform();
	}
	
	protected void updateTransform() {
		// Lookat
		Vector3d forward = new Vector3d();
		forward.sub(lookAt, position);
		forward.normalize();
		Vector3d right = new Vector3d();
		right.cross(forward, up);
		Vector3d up2 = new Vector3d();
		up2.cross(right, forward);

		transform.set(new double[] {
				right.x,    right.y,    right.z,    0,
				up2.x,      up2.y,      up2.z,      0,
				-forward.x, -forward.y, -forward.z, 0,
				0,          0,          0,          1
		});
		
		// Translation	
		Matrix4d translation = new Matrix4d();
		translation.setIdentity();
		translation.setColumn(3, -position.x, -position.y, -position.z, 1);
		transform.mul(translation);
		
		// Model rotation
		Matrix4d rot = new Matrix4d();
		rot.set(rotation);
		transform.mul(rot);
	}
	
	public void lookAt(Point3d position, Point3d lookAt, Vector3d up) {
		this.position = position;
		this.up = up;
		this.lookAt = lookAt;
		updateTransform();
	}

	public Matrix4d getTransform() {
		return transform;
	}
	
	public Quat4d getRotation() {
		return rotation;
	}

	public void setRotation(Quat4d rotation) {
		this.rotation = rotation;
		updateTransform();
	}

	public Point3d getPosition() {
		return position;
	}

	public void setPosition(Point3d position) {
		this.position = position;
		updateTransform();
	}

	public Point3d getLookAt() {
		return lookAt;
	}

	public void setLookAt(Point3d lookAt) {
		this.lookAt = lookAt;
		updateTransform();
	}

	public Vector3d getUp() {
		return up;
	}

	public void setUp(Vector3d up) {
		this.up = up;
		updateTransform();
	}

	public void mouseDragged(double dx, double dy) {
		Matrix3d m = new Matrix3d();
		Quat4d rotationInv = new Quat4d();
		rotationInv.inverse(rotation);
		m.set(rotationInv);
		
		Vector3d x = new Vector3d(1, 0, 0);
		Vector3d y = new Vector3d(0, 1, 0);
		m.transform(x);
		m.transform(y);

		Quat4d rotX = new Quat4d();
		rotX.set(new AxisAngle4d(x, dy*ROTATE_SENSITIVITY));
		Quat4d rotY = new Quat4d();
		rotY.set(new AxisAngle4d(y, dx*ROTATE_SENSITIVITY));
		rotation.mul(rotX);
		rotation.mul(rotY);
		
		updateTransform();
	}
}
