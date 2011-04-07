package de.tum.in.jrealityplugin;

public interface Cindy3DViewer {
	/**
	 * Marks the beginning of a new scene
	 */
	void begin();
	/**
	 * Finishes and displays the scene
	 */
	void end();
	/**
	 * Adds a new point to the current scene
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param appearance
	 */
	void addPoint(double x, double y, double z, AppearanceState appearance);
	
	void addCircle(double cx, double cy, double cz,
			       double nx, double ny, double nz, double radius,
			       AppearanceState appearance);
	/**
	 * Shuts down the viewer
	 */
	void shutdown();
	void addSegment(double x1, double y1, double z1,
					double x2, double y2, double z2,
					AppearanceState appearance);
	void addLine(double x1, double y1, double z1,
				 double x2, double y2, double z2,
				 AppearanceState appearance);
	void addRay(double x1, double y1, double z1,
				double x2, double y2, double z2,
				AppearanceState appearance);
	void addPolygon(double[][] vertices, AppearanceState appearance);
	
	void addMesh(double[][][] vertices, double[][][] normals,
			AppearanceState appearance);
}
