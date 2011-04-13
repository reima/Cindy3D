package de.tum.in.reitinge.test;

import java.awt.Color;

import de.tum.in.jrealityplugin.AppearanceState;
import de.tum.in.jrealityplugin.Cindy3DViewer;
import de.tum.in.jrealityplugin.jogl.JOGLViewer;

public class JOGLViewerTest {
	public static void main(String[] args) {
		JOGLViewer viewer = new JOGLViewer();
		
		viewer.setBackgroundColor(Color.white);
		
		viewer.begin();
//		colorSpiral(viewer);
//		circles(viewer);
//		lines(viewer);
		icosahedron(viewer);
		viewer.end();
	}
	
	public static void colorSpiral(Cindy3DViewer viewer) {
		AppearanceState appearance = new AppearanceState(Color.red, 1.0);
		
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
						Math.sin(frac * 2 * Math.PI) * rad, (frac - 0.5)
								* height, appearance);
			}
		}
	}
	
	public static void circles(Cindy3DViewer viewer) {
		AppearanceState appearance = new AppearanceState(Color.red, 1.0);
		
		for (int i = -5; i <= 5; ++i) {
			for (int j = -5; j <= 5; ++j) {
				appearance.setColor(Color.red);
				viewer.addCircle(i * 2, j * 2, 0, 1, 0, 0, 1, appearance);
				appearance.setColor(Color.green);
				viewer.addCircle(i * 2, j * 2, 0, 0, 1, 0, 1, appearance);
				appearance.setColor(Color.blue);
				viewer.addCircle(i * 2, j * 2, 0, 0, 0, 1, 1, appearance);
			}
		}
	}
	
	public static void lines(Cindy3DViewer viewer) {
		AppearanceState appearance = new AppearanceState(Color.red, 1.0);
		
		viewer.addSegment(-10, 0, 0, 8, 0, -0.5, appearance);
		viewer.addLine(0, 2, 0, 5, 4, 0, appearance);
		viewer.addRay(0, 4, 0, 5, -2, 2, appearance);
		
		double r = 1;
		int n = 750;
		double[][] vertices = new double[n][3];
		for (int i=0; i<n; ++i) {
			vertices[i][2] = 0;
			vertices[i][0] = r*Math.sin(i*2*Math.PI/n);
			vertices[i][1] = r*Math.cos(i*2*Math.PI/n);
		}
		
		//viewer.addCircle(0, 0, 0.5, 0, 0, 1, r, appearance);
		
		//viewer.addPolygon(vertices, null, appearance);
		
//		viewer.addMesh(2, 3, new double[][] { { 0, 0, 0 }, { 1, 1, 0 },
//				{ 2, 0, 0 }, { 0, 0, 1 }, { 1, 1, 1 }, { 2, 0, 1 } },
//				new double[][] { { -1, 1, 0 }, { 0, 1, 0 }, { 1, 1, 0 },
//						{ -1, 1, 1 }, { 0, 1, 0 }, { 1, 1, 0 } }, appearance);
		
		appearance.setColor(Color.GREEN);
		//viewer.addLineStrip(vertices, appearance, true);
	}

	public static void icosahedron(JOGLViewer viewer) {
		AppearanceState appearance = new AppearanceState(Color.yellow, 1.0);
		double golden = (1.0 + Math.sqrt(5)) / 2.0;
		
		double verts[][] = {
				{       0,       1,  golden },
				{       0,      -1,  golden },
				{       0,       1, -golden },
				{       0,      -1, -golden },
				{  golden,       0,       1 },
				{  golden,       0,      -1 },
				{ -golden,       0,       1 },
				{ -golden,       0,      -1 },
				{       1,  golden,       0 },
				{      -1,  golden,       0 },
				{       1, -golden,       0 },
				{      -1, -golden,       0 },
		};
		int[][] edges = {
				{ 0, 1 }, { 2, 3 }, { 4, 5 }, { 6, 7 }, { 8, 9 }, { 10, 11 },
				{ 0, 8 }, { 2, 8 }, { 4, 0 }, { 6, 0 }, { 8, 4 }, { 10, 4 },
				{ 0, 9 }, { 2, 9 }, { 4, 1 }, { 6, 1 }, { 8, 5 }, { 10, 5 },
				{ 1, 10 }, { 3, 10 }, { 5, 2 }, { 7, 2 }, { 9, 6 }, { 11, 6 },
				{ 1, 11 }, { 3, 11 }, { 5, 3 }, { 7, 3 }, { 9, 7 }, { 11, 7 },
		};
		
		for (int[] edge : edges) {
			double vert1[] = verts[edge[0]];
			double vert2[] = verts[edge[1]];
			viewer.addSegment(vert1[0], vert1[1], vert1[2], vert2[0], vert2[1],
					vert2[2], appearance);
		}
	}
}
