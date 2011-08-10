package de.tum.in.cindy3dplugin.jogl.primitives.renderers;

import de.tum.in.cindy3dplugin.jogl.primitives.*;

/**
 * Primitive renderer factory interface.
 * 
 * Interface for different kinds of factories producing renderers for different
 * kinds of primitives.
 * 
 */
public interface PrimitiveRendererFactory {
	/**
	 * @return circle renderer
	 */
	public PrimitiveRenderer<Circle> createCircleRenderer();
	/**
	 * @return line, ray and line segment renderer
	 */
	public PrimitiveRenderer<Line> createLineRenderer();
	/**
	 * @return mesh renderer
	 */
	public PrimitiveRenderer<Mesh> createMeshRenderer();
	/**
	 * @return sphere renderer
	 */
	public PrimitiveRenderer<Sphere> createSphereRenderer();
	/**
	 * 
	 * @return polygon renderer
	 */
	public PrimitiveRenderer<Polygon> createPolygonRenderer();
}
