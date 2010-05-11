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
}
