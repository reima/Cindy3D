package de.tum.in.cindy3dplugin.jogl.lighting;

import javax.media.opengl.GL2;

import org.apache.commons.math.geometry.Vector3D;

import de.tum.in.cindy3dplugin.jogl.lighting.LightManager.LightType;

public class DirectionalLight extends Light {
	
	private Vector3D direction = new Vector3D(0,-1,0);

	public void setDirection(Vector3D direction) {
		this.direction = direction;
	}

	@Override
	public void setGLState(GL2 gl, int light) {
		super.setGLState(gl, light);
		
		gl.glLightfv(light, GL2.GL_POSITION,
				new float[]{(float) direction.getX(), (float) direction.getY(),
				(float) direction.getZ(), 0.0f}, 0);
	}

	@Override
	public String getShaderFillIn(int light) {
		return "directionalLight(" + light +", normal);";
	}

	@Override
	public LightType getType() {
		return LightType.DIRECTIONAL_LIGHT;
	}
}
