package de.tum.in.jrealityplugin;

import java.awt.Color;


/** 
 * Stores one set of appearance attributes for scene objects 
 */
public class AppearanceState {

	private Color color;
	private double size;
	private double opacity;
	
	/**
	 * Create a new appearance state with a certain color and size
	 * @param color Point color
	 * @param size Point size
	 */
	public AppearanceState(Color color, double size, double opacity) {
		this.color = color;
		this.size = size;
		this.opacity = opacity;
	}
	
	/**
	 * Create a new appearance state and initialize it from another state
	 * @param state Appearance state to initialize from
	 */
	public AppearanceState(AppearanceState state) {
	    this(state.color, state.size, state.opacity);
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
	 * @param opacity Opacity to set
	 */
	public void setOpacity(double opacity) {
		this.opacity = opacity;
	}
	
	/**
	 * @return The opacity
	 */
	public double getOpacity() {
		return opacity;
	}
			
}
