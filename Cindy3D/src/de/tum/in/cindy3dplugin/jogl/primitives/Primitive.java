package de.tum.in.cindy3dplugin.jogl.primitives;

import java.awt.Color;

public abstract class Primitive {
	public Color color;
	public double shininess;

	public Primitive(Color color, double shininess, double alpha) {
		this.color = new Color(color.getRed(), color.getGreen(), color
				.getBlue(), (int) (alpha * 255));
		this.shininess = shininess;
	}

	public boolean isOpaque() {
		return (color.getAlpha() == 255);
	}
}
