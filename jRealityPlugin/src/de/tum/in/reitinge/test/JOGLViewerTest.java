package de.tum.in.reitinge.test;

import java.awt.Color;

import de.tum.in.jrealityplugin.AppearanceState;
import de.tum.in.jrealityplugin.JOGLViewer;

public class JOGLViewerTest {
	public static void main(String[] args) {
		JOGLViewer viewer = new JOGLViewer();
		AppearanceState appearance = new AppearanceState(Color.red, 1.0);
		viewer.begin();
		double height = 2;
		double radius = 1;
		int segments = 100;
		int rings = 20;

		for (int segment = 1; segment <= segments; ++segment) {
			double frac = (double) segment / segments;
			appearance.setColor(new Color(Color.HSBtoRGB((float) frac, 1, 1)));
			for (int ring = 1; ring <= rings; ++ring) {
				double rad = (double) ring / rings * radius;
				viewer.addPoint(Math.cos(frac * 2 * Math.PI) * rad,
						Math.sin(frac * 2 * Math.PI) * rad, frac * height,
						appearance);
			}
		}
		viewer.end();
	}
}
