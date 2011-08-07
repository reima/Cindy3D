package de.tum.in.cindy3dplugin.jogl.primitives;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;

public class Scene {
	private Color backgroundColor = Color.white;
	private ArrayList<Sphere> spheres = new ArrayList<Sphere>();
	private ArrayList<Circle> circles = new ArrayList<Circle>();
	private ArrayList<Line> lines = new ArrayList<Line>();
	private ArrayList<Polygon> polygons = new ArrayList<Polygon>();
	private ArrayList<Mesh> meshes = new ArrayList<Mesh>();
	
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public Collection<Sphere> getSpheres() {
		return spheres;
	}

	public Collection<Circle> getCircles() {
		return circles;
	}

	public Collection<Line> getLines() {
		return lines;
	}

	public Collection<Polygon> getPolygons() {
		return polygons;
	}
	
	public Collection<Mesh> getMeshes() {
		return meshes;
	}

	public void clear() {
		spheres.clear();
		circles.clear();
		lines.clear();
		polygons.clear();
		meshes.clear();
	}

	public void addSphere(Sphere sphere) {
		spheres.add(sphere);
	}

	public void addCircle(Circle circle) {
		circles.add(circle);
	}

	public void addLine(Line line) {
		lines.add(line);
	}

	public void addPolygon(Polygon polygon) {
		polygons.add(polygon);
	}

	public void addMesh(Mesh mesh) {
		meshes.add(mesh);
	}	
}