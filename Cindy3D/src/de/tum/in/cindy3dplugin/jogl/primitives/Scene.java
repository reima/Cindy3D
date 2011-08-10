package de.tum.in.cindy3dplugin.jogl.primitives;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a scene, storing and handling geometry objects and well as global
 * scene dependend parameters.
 */
public class Scene {
	/**
	 * Background color of the scene. Default value is white.
	 */
	private Color backgroundColor = Color.white;
	/**
	 * List storing all points and spheres in the scene.
	 */
	private ArrayList<Sphere> spheres = new ArrayList<Sphere>();
	/**
	 * List storing all circles in the scene.
	 */
	private ArrayList<Circle> circles = new ArrayList<Circle>();
	/**
	 * List storing all lines, rays and line segments in the scene.
	 */
	private ArrayList<Line> lines = new ArrayList<Line>();
	/**
	 * List storing all polygons in the scene.
	 */
	private ArrayList<Polygon> polygons = new ArrayList<Polygon>();
	/**
	 * List storing all meshes in the scene.
	 */
	private ArrayList<Mesh> meshes = new ArrayList<Mesh>();

	/**
	 * Sets scene background color.
	 * 
	 * @param backgroundColor
	 *            new background color
	 */
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	/**
	 * @return current scene background color
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * @return list of spheres currently in the scene
	 */
	public Collection<Sphere> getSpheres() {
		return spheres;
	}

	/**
	 * @return list of circles and points currently in the scene
	 */
	public Collection<Circle> getCircles() {
		return circles;
	}

	/**
	 * @return list of lines, rays and line segments currently in the scene
	 */
	public Collection<Line> getLines() {
		return lines;
	}

	/**
	 * @return list of polygons currently in the scene
	 */
	public Collection<Polygon> getPolygons() {
		return polygons;
	}

	/**
	 * @return list of meshes currently in the scene
	 */
	public Collection<Mesh> getMeshes() {
		return meshes;
	}

	/**
	 * Cleans the scene by removing all geometry objects.
	 */
	public void clear() {
		spheres.clear();
		circles.clear();
		lines.clear();
		polygons.clear();
		meshes.clear();
	}

	/**
	 * Add a sphere to the scene.
	 * 
	 * @param sphere
	 *            sphere to be added
	 */
	public void addSphere(Sphere sphere) {
		spheres.add(sphere);
	}

	/**
	 * Adds a new circle to the scene.
	 * 
	 * @param circle
	 *            circle to be added
	 */
	public void addCircle(Circle circle) {
		circles.add(circle);
	}

	/**
	 * Adds a new line, ray or line segment to the scene.
	 * 
	 * @param line
	 *            line, ray or line segment to be added
	 */
	public void addLine(Line line) {
		lines.add(line);
	}

	/**
	 * Adds a new polygon to the scene.
	 * 
	 * @param polygon
	 *            polygon to be added
	 */
	public void addPolygon(Polygon polygon) {
		polygons.add(polygon);
	}

	/**
	 * Adds a new mesh to the scene.
	 * 
	 * @param mesh
	 *            mesh to be added
	 */
	public void addMesh(Mesh mesh) {
		meshes.add(mesh);
	}
}