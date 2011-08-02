package de.tum.in.cindy3dplugin;

import java.awt.Color;


/** 
 * Stores one set of appearance attributes for scene objects 
 */
public class AppearanceState {

	private Color color;
	private double shininess;
	private double size;
	private double alpha;
	
	/**
	 * Create a new appearance state with a certain color and size
	 * @param color Point color
	 * @param size Point size
	 */
	public AppearanceState(Color color, double shininess, double size,
			double alpha) {
		this.color = color;
		this.shininess = shininess;
		this.size = size;
		this.alpha = alpha;
	}
	
	/**
	 * Create a new appearance state and initialize it from another state
	 * @param state Appearance state to initialize from
	 */
	public AppearanceState(AppearanceState state) {
	    this(state.color, state.shininess, state.size, state.alpha);
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}
	
	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}
	
	/**
	 * @param shininess the shininess to set
	 */
	public void setShininess(double shininess) {
		this.shininess = shininess;
	}
	
	/**
	 * @return the shininess
	 */
	public double getShininess() {
		return shininess;
	}
	
	/**
	 * @param size the size to set
	 */
	public void setSize(double size) {
		this.size = size;
	}
	
	/**
	 * @return the size
	 */
	public double getSize() {
		return size;
	}
	
	/**
	 * @param alpha Alpha to set
	 */
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}
	
	/**
	 * @return The alpha
	 */
	public double getAlpha() {
		return alpha;
	}
			
}
