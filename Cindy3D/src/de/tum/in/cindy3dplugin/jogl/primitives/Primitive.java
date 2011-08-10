package de.tum.in.cindy3dplugin.jogl.primitives;

import de.tum.in.cindy3dplugin.jogl.Material;

/**
 * Abstract base class for all primitive classes.
 */
public abstract class Primitive {
	/**
	 * Material used for rendering the primitive
	 */
	private Material material;
	
	/**
	 * Constructs a new primitive.
	 * 
	 * @param material
	 *            material of the primitive
	 */
	public Primitive(Material material) {
		this.material = material;
	}
	
	/**
	 * @return primitive material
	 */
	public Material getMaterial() {
		return material;
	}
	
	/**
	 * @return <code>true</code> iff the primitive is opaque
	 */
	public boolean isOpaque() {
		return (material.getColor().getAlpha() == 255);
	}
}
