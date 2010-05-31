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
}
