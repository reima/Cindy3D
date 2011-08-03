package de.tum.in.cindy3dplugin.jogl.lighting;

import de.tum.in.cindy3dplugin.LightInfo.LightType;

public class SpotLight extends Light {
	
	@Override
	public String getShaderFillIn(int light) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LightType getType() {
		return LightType.SPOT_LIGHT;
	}

}
