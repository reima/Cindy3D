package de.tum.in.cindy3dplugin.jogl.lighting;

import java.awt.Color;

import org.apache.commons.math.geometry.Vector3D;

import de.tum.in.cindy3dplugin.jogl.lighting.LightManager.LightType;

public class LightInfo {
	
	public Color ambient = null;
	public Color diffuse = null;
	public Color specular = null;
	public Vector3D position = null;
	public Vector3D direction = null;
	
	public LightType type;
}
