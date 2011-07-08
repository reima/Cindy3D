package de.tum.in.cindy3dplugin.jogl;

import java.awt.Color;

public abstract class Primitive {

	Color color;

	public Primitive(Color color, double opacity) {
		this.color = new Color(color.getRed(), color.getGreen(), color
				.getBlue(), (int) (opacity * 255));
	}

	public boolean isOpaque() {
		return (color.getAlpha() == 255);
	}
}
