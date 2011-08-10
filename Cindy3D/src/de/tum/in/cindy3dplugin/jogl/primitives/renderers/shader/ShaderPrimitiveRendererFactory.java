package de.tum.in.cindy3dplugin.jogl.primitives.renderers.shader;

import de.tum.in.cindy3dplugin.jogl.primitives.Circle;
import de.tum.in.cindy3dplugin.jogl.primitives.Line;
import de.tum.in.cindy3dplugin.jogl.primitives.Mesh;
import de.tum.in.cindy3dplugin.jogl.primitives.Sphere;
import de.tum.in.cindy3dplugin.jogl.primitives.Polygon;
import de.tum.in.cindy3dplugin.jogl.primitives.renderers.PrimitiveRenderer;
import de.tum.in.cindy3dplugin.jogl.primitives.renderers.PrimitiveRendererFactory;

/**
 * Primitive renderer factory for advanced shading renderers.
 * 
 * This specialization of the PrimtiveRendererFactory factory generates
 * primitive renderers that use shaders and more advanced shading techniques.
 */
public class ShaderPrimitiveRendererFactory implements PrimitiveRendererFactory {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.cindy3dplugin.jogl.primitives.renderers.PrimitiveRendererFactory
	 * #createCircleRenderer()
	 */
	@Override
	public PrimitiveRenderer<Circle> createCircleRenderer() {
		return new CircleRenderer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.cindy3dplugin.jogl.primitives.renderers.PrimitiveRendererFactory
	 * #createLineRenderer()
	 */
	@Override
	public PrimitiveRenderer<Line> createLineRenderer() {
		return new LineRenderer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.cindy3dplugin.jogl.primitives.renderers.PrimitiveRendererFactory
	 * #createMeshRenderer()
	 */
	@Override
	public PrimitiveRenderer<Mesh> createMeshRenderer() {
		return new MeshRenderer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.cindy3dplugin.jogl.primitives.renderers.PrimitiveRendererFactory
	 * #createSphereRenderer()
	 */
	@Override
	public PrimitiveRenderer<Sphere> createSphereRenderer() {
		return new SphereRenderer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.cindy3dplugin.jogl.primitives.renderers.PrimitiveRendererFactory
	 * #createPolyonRenderer()
	 */
	@Override
	public PrimitiveRenderer<Polygon> createPolygonRenderer() {
		return new PolygonRenderer();
	}
}
