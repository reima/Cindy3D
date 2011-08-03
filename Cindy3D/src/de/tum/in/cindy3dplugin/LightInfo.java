package de.tum.in.cindy3dplugin;

import java.awt.Color;

public class LightInfo {
	public enum LightType {
		POINT_LIGHT,
		DIRECTIONAL_LIGHT,
		SPOT_LIGHT,
	}
	
	public Color ambient = null;
	public Color diffuse = null;
	public Color specular = null;
	public double[] position = null;
	public double[] direction = null;
	
	public LightType type;
}
