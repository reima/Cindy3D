package de.tum.in.jrealityplugin;

import java.awt.Color;


/** 
 * Stores one set of appearance attributes for scene objects 
 */
public class AppearanceState {

	private Color color;
	private double size;
	
	/**
	 * Create a new Appearance State with a certain color and size
	 * @param color Point color
	 * @param size Point size
	 */
	public AppearanceState(Color color, double size) {
		this.color = color;
		this.size = size;
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
			
}
