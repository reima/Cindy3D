package de.tum.in.cindy3dplugin;

import java.awt.Color;

/**
 * Stores one set of appearance attributes for scene objects.
 */
public class AppearanceState implements Cloneable {
	/**
	 * Color
	 */
	private Color color;
	/**
	 * Shininess
	 */
	private double shininess;
	/**
	 * Size
	 */
	private double size;
	/**
	 * Alpha
	 */
	private double alpha;

	/**
	 * Creates a new appearance state with the given attributes.
	 * 
	 * @param color
	 *            color
	 * @param shininess
	 *            shininess
	 * @param size
	 *            size
	 * @param alpha
	 *            alpha
	 */
	public AppearanceState(Color color, double shininess, double size, double alpha) {
		setColor(color);
		setShininess(shininess);
		setSize(size);
		setAlpha(alpha);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public AppearanceState clone() {
		return new AppearanceState(color, shininess, size, alpha);
	}

	/**
	 * Sets the color of the appearance.
	 * 
	 * @param color
	 *            new color
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Gets the color of the appearance.
	 * 
	 * @return color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Sets the shininess of the appearance.
	 * 
	 * @param shininess
	 *            New shininess
	 */
	public void setShininess(double shininess) {
		this.shininess = shininess;
	}

	/**
	 * Gets the shininess of the appearance.
	 * 
	 * @return shininess
	 */
	public double getShininess() {
		return shininess;
	}

	/**
	 * Sets the size of the appearance.
	 * 
	 * @param size
	 *            new size
	 */
	public void setSize(double size) {
		this.size = size;
	}

	/**
	 * Gets the size of the appearance.
	 * 
	 * @return size
	 */
	public double getSize() {
		return size;
	}

	/**
	 * Sets the alpha value of the appearance.
	 * 
	 * @param alpha
	 *            new alpha
	 */
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	/**
	 * Gets the alpha value of the appearance.
	 * 
	 * @return alpha
	 */
	public double getAlpha() {
		return alpha;
	}
}
