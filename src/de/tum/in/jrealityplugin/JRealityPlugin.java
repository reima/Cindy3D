package de.tum.in.jrealityplugin;

import java.util.ArrayList;

import de.cinderella.api.cs.CindyScript;
import de.cinderella.api.cs.CindyScriptPlugin;

/**
 * Implementation of the plugin interface
 */
public class JRealityPlugin extends CindyScriptPlugin {
	private Cindy3DViewer cindy3d = null;

	public JRealityPlugin() {
	}

	@Override
	public void register() {
		if (cindy3d == null)
			cindy3d = new JRealityViewer();
	}

	@Override
	public void unregister() {
		if (cindy3d != null)
			cindy3d.shutdown();
		cindy3d = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.cinderella.api.cs.CindyScriptPlugin#getAuthor()
	 */
	@Override
	public String getAuthor() {
		return "Jan Sommer und Matthias Reitinger";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.cinderella.api.cs.CindyScriptPlugin#getName()
	 */
	@Override
	public String getName() {
		return "jReality for Cinderella";
	}

	/**
	 * Squares the given number
	 * 
	 * @param x
	 * @return The square of x
	 */
	@CindyScript("square")
	public double square(double x) {
		return x * x;
	}

	/**
	 * Prepares drawing of 3D objects. Must be called before any 3D drawing
	 * function. TODO: List these functions
	 */
	@CindyScript("begin3d")
	public void begin3d() {
		cindy3d.begin();
	}

	/**
	 * Finalizes the drawing of 3D objects. Displays all objects drawn since the
	 * last call to <code>begin3d</code>.
	 */
	@CindyScript("end3d")
	public void end3d() {
		cindy3d.end();
	}

	/**
	 * Draws a point in 3D space
	 * @param vec Euclidean coordinates of the point
	 */
	@CindyScript("draw3d")
	public void draw3d(ArrayList<Double> vec) {
		if (vec.size() != 3)
			return;

		cindy3d.addPoint(vec.get(0), vec.get(1), vec.get(2));
	}

}
