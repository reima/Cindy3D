package de.tum.in.cindy3dplugin.jogl.primitives.renderers.fixedfunc;

import de.tum.in.cindy3dplugin.jogl.primitives.Circle;
import de.tum.in.cindy3dplugin.jogl.primitives.Line;
import de.tum.in.cindy3dplugin.jogl.primitives.Mesh;
import de.tum.in.cindy3dplugin.jogl.primitives.Point;
import de.tum.in.cindy3dplugin.jogl.primitives.Polygon;
import de.tum.in.cindy3dplugin.jogl.primitives.renderers.PrimitiveRenderer;
import de.tum.in.cindy3dplugin.jogl.primitives.renderers.PrimitiveRendererFactory;

public class FixedfuncPrimitiveRendererFactory implements
		PrimitiveRendererFactory {

	@Override
	public PrimitiveRenderer<Circle> createCircleRenderer() {
		return new DummyRenderer<Circle>();
	}

	@Override
	public PrimitiveRenderer<Line> createLineRenderer() {
		return new LineRenderer();
	}

	@Override
	public PrimitiveRenderer<Mesh> createMeshRenderer() {
		return new MeshRenderer();
	}

	@Override
	public PrimitiveRenderer<Point> createPointRenderer() {
		return new PointRenderer();
	}

	@Override
	public PrimitiveRenderer<Polygon> createPolygonRenderer() {
		return new PolygonRenderer();
	}

}
