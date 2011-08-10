package de.tum.in.cindy3dplugin.jogl.primitives.renderers;

import de.tum.in.cindy3dplugin.jogl.primitives.*;

/**
 * Primitive renderer factory.
 * 
 * Creates instances of renderers for different primitives.
 */
public interface PrimitiveRendererFactory {
	/**
	 * @return new circle renderer instance
	 */
	public PrimitiveRenderer<Circle> createCircleRenderer();
	/**
	 * @return new line, ray and line segment renderer instance
	 */
	public PrimitiveRenderer<Line> createLineRenderer();
	/**
	 * @return new mesh renderer instance
	 */
	public PrimitiveRenderer<Mesh> createMeshRenderer();
	/**
	 * @return new sphere renderer instance
	 */
	public PrimitiveRenderer<Sphere> createSphereRenderer();
	/**
	 * 
	 * @return new polygon renderer instance
	 */
	public PrimitiveRenderer<Polygon> createPolygonRenderer();
}
