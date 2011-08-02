package de.tum.in.cindy3dplugin.jogl.primitives;

import java.awt.Color;

public abstract class Primitive {
	public Color color;
	public int shininess;

	public Primitive(Color color, int shininess, double alpha) {
		this.color = new Color(color.getRed(), color.getGreen(), color
				.getBlue(), (int) (alpha * 255));
		this.shininess = shininess;
	}

	public boolean isOpaque() {
		return (color.getAlpha() == 255);
	}
}
