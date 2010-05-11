package de.tum.in.jrealityplugin;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Stack;

import de.cinderella.api.cs.CindyScript;
import de.cinderella.api.cs.CindyScriptPlugin;

/**
 * Implementation of the plugin interface
 */
public class JRealityPlugin extends CindyScriptPlugin {
	private Cindy3DViewer cindy3d = null;
	
	/**
	 * Stack of saved point appearances
	 * @see JRealityPlugin#gsave3d()
	 * @see JRealityPlugin#grestore3d()
	 */
	Stack<AppearanceState> pointAppearanceStack;
	/**
	 * The current point appearance
	 */
	AppearanceState pointAppearance;
	

	public JRealityPlugin() {
		pointAppearanceStack = new Stack<AppearanceState>();
		pointAppearance = new AppearanceState(Color.RED, 1);
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

		cindy3d.addPoint(vec.get(0), vec.get(1), vec.get(2), pointAppearance);
	}

	/**
	 * Pushes the current appearance on the appearance stack
	 * @see JRealityPlugin#grestore3d()
	 */
	@CindyScript("gsave3d")
	public void gsave3d() {
		pointAppearanceStack.push(new AppearanceState(pointAppearance));
	}

	/**
	 * Removes the top element of the appearance stack and replaces the current
	 * appearance with it
	 * @see JRealityPlugin#gsave3d()
	 */
	@CindyScript("grestore3d")
	public void grestore3d() {
		if (pointAppearanceStack.isEmpty())
			return;
		pointAppearance = pointAppearanceStack.pop();
	}

	/**
	 * Set point color state
	 * @param vec Color vector
	 */
	@CindyScript("pointcolor3d")
	public void pointcolor3d(ArrayList<Double> vec) {
		if (vec.size() != 3)
			return;
		pointAppearance.setColor(new Color(
				(float)Math.max(0, Math.min(1, vec.get(0))),
				(float)Math.max(0, Math.min(1, vec.get(1))),
				(float)Math.max(0, Math.min(1, vec.get(2)))));
	}
	
	/**
	 * Set point size state
	 * @param size Point size
	 */
	@CindyScript("pointsize3d")
	public void pointsize3d(double size) {
		if (size <= 0)
			return;
		pointAppearance.setSize(size);
	}
}
