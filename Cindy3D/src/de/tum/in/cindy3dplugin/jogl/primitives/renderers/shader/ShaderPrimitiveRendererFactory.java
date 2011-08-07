package de.tum.in.cindy3dplugin.jogl.primitives.renderers.shader;

import de.tum.in.cindy3dplugin.jogl.primitives.Circle;
import de.tum.in.cindy3dplugin.jogl.primitives.Line;
import de.tum.in.cindy3dplugin.jogl.primitives.Mesh;
import de.tum.in.cindy3dplugin.jogl.primitives.Sphere;
import de.tum.in.cindy3dplugin.jogl.primitives.Polygon;
import de.tum.in.cindy3dplugin.jogl.primitives.renderers.PrimitiveRenderer;
import de.tum.in.cindy3dplugin.jogl.primitives.renderers.PrimitiveRendererFactory;

public class ShaderPrimitiveRendererFactory implements PrimitiveRendererFactory {

	@Override
	public PrimitiveRenderer<Circle> createCircleRenderer() {
		return new CircleRenderer();
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
	public PrimitiveRenderer<Sphere> createSphereRenderer() {
		return new SphereRenderer();
	}

	@Override
	public PrimitiveRenderer<Polygon> createPolygonRenderer() {
		return new PolygonRenderer();
	}

}
