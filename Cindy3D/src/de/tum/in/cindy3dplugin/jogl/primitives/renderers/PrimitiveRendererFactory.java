package de.tum.in.cindy3dplugin.jogl.primitives.renderers;

import de.tum.in.cindy3dplugin.jogl.primitives.*;

public interface PrimitiveRendererFactory {
	public PrimitiveRenderer<Circle> createCircleRenderer();
	public PrimitiveRenderer<Line> createLineRenderer();
	public PrimitiveRenderer<Mesh> createMeshRenderer();
	public PrimitiveRenderer<Point> createPointRenderer();
	public PrimitiveRenderer<Polygon> createPolygonRenderer();
}
