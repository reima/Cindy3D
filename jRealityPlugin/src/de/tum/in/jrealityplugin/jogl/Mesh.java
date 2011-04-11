package de.tum.in.jrealityplugin.jogl;

import java.awt.Color;

public class Mesh {
	
	int n, m;
	
	Color color;
	double[][] vertices;
	
	private static int meshCounter = 0;
	
	int identifier;
	
	public Mesh(int n, int m, double[][] vertices) {
		
		identifier = meshCounter++;
		this.vertices = vertices;
	}
}
