package de.tum.in.cindy3dplugin.jogl.primitives;

import java.awt.Color;

/**
 * Abstract base class for all primitive classes.
 */
public abstract class Primitive {
	/**
	 * Color of the primitive
	 */
	private Color color;
	
	/**
	 * Shininess of the primitive
	 */
	private double shininess;

	/**
	 * Constructs a new primitive.
	 * 
	 * @param color color of the primitive
	 * @param shininess shininess of the primitive
	 * @param alpha alpha value of the primitive
	 */
	public Primitive(Color color, double shininess, double alpha) {
		this.color = new Color(color.getRed(), color.getGreen(), color
				.getBlue(), (int) (alpha * 255));
		this.shininess = shininess;
	}
	
	/**
	 * @return color of the primitive
	 */
	public Color getColor() {
		return color;
	}
	
	/**
	 * @return shininess of the primitive
	 */
	public double getShininess() {
		return shininess;
	}

	/**
	 * @return true if the primitive is totally opaque, i.e. has an alpha value
	 *         of 1
	 */
	public boolean isOpaque() {
		return (color.getAlpha() == 255);
	}
}
