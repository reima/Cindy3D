package de.tum.in.jrealityplugin.jogl;

import java.awt.Color;

public abstract class Primitive {

	Color color;
	double opacity;
	
	public boolean isTransparent() {
		return (opacity != 1.0);
	}
}
