package de.tum.in.cindy3dplugin.jogl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Hashtable;
import java.util.logging.Level;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import org.apache.commons.math.geometry.Vector3D;

import de.tum.in.cindy3dplugin.AppearanceState;
import de.tum.in.cindy3dplugin.Cindy3DViewer;
import de.tum.in.cindy3dplugin.LightInfo;
import de.tum.in.cindy3dplugin.jogl.RenderHints.RenderMode;
import de.tum.in.cindy3dplugin.jogl.primitives.Circle;
import de.tum.in.cindy3dplugin.jogl.primitives.Line;
import de.tum.in.cindy3dplugin.jogl.primitives.Line.LineType;
import de.tum.in.cindy3dplugin.jogl.primitives.renderers.fixedfunc.FixedfuncPrimitiveRendererFactory;
import de.tum.in.cindy3dplugin.jogl.primitives.renderers.shader.ShaderPrimitiveRendererFactory;
import de.tum.in.cindy3dplugin.jogl.primitives.Mesh;
import de.tum.in.cindy3dplugin.jogl.primitives.Point;
import de.tum.in.cindy3dplugin.jogl.primitives.Polygon;
import de.tum.in.cindy3dplugin.jogl.primitives.Scene;
import de.tum.in.cindy3dplugin.jogl.renderers.DefaultRenderer;
import de.tum.in.cindy3dplugin.jogl.renderers.JOGLRenderer;
import de.tum.in.cindy3dplugin.jogl.renderers.SupersampledFBORenderer;

public class JOGLViewer implements Cindy3DViewer, MouseListener,
		MouseMotionListener, MouseWheelListener {
	private static final double POINT_SCALE = 0.05;
	
	boolean standalone;
	private Container container;
	private GLCanvas canvas = null;
	
	private JOGLRenderer renderer;

	private Scene scene = new Scene();
	private ModelViewerCamera camera = new ModelViewerCamera();
	
	private double[] mousePosition = new double[2];
	
	private boolean drawPending = false;

	private final RenderHints[] qualityHints = new RenderHints[] {
		new RenderHints(RenderMode.FIXED_FUNCTION_PIPELINE, 1, 1),
		new RenderHints(RenderMode.FIXED_FUNCTION_PIPELINE, 2, 1),
		new RenderHints(RenderMode.FIXED_FUNCTION_PIPELINE, 4, 1),
		new RenderHints(RenderMode.FIXED_FUNCTION_PIPELINE, 8, 1),
		new RenderHints(RenderMode.PROGRAMMABLE_PIPELINE, 1, 1),
		new RenderHints(RenderMode.PROGRAMMABLE_PIPELINE, 2, 1),
		new RenderHints(RenderMode.PROGRAMMABLE_PIPELINE, 3, 1),
		new RenderHints(RenderMode.PROGRAMMABLE_PIPELINE, 4, 1),
		new RenderHints(RenderMode.PROGRAMMABLE_PIPELINE, 8, 1),
	};
	
	private RenderHints renderHints = null;
	private RenderHints requestedRenderHints = null; 
	
	public JOGLViewer() {
		this(null);
	}

	public JOGLViewer(Container container) {
		Util.initLogger();
		Util.setupGluegenClassLoading();
		
		if (container == null) {
			standalone = true;
			JFrame frame = new JFrame("Cindy3D (JOGL)");
			frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			frame.setLayout(new BorderLayout());
			frame.setSize(640, 480);
			this.container = frame;
		} else {
			standalone = false;
			this.container = container;
		}

		camera.lookAt(new Vector3D(0, 0, 5), Vector3D.ZERO, Vector3D.PLUS_J);

		try {
			GLProfile.initSingleton(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Util.logger.log(Level.SEVERE, e.toString(), e);
		}
		
		applyHints(qualityHints[4]);
	}

	private void applyHints(RenderHints hints) {
		if (renderHints != null && renderHints.equals(hints)) {
			return;
		}
		
		renderHints = hints;
		
		try {
			GLProfile profile = GLProfile.getDefault();
			GLCapabilities caps = new GLCapabilities(profile);
			
			if (hints.getRenderMode() == RenderMode.FIXED_FUNCTION_PIPELINE) {
				if (hints.getSamplingRate() > 1) {
					caps.setSampleBuffers(true);
					caps.setNumSamples(hints.getSamplingRate());
				}
				renderer = new DefaultRenderer(scene, camera,
						new FixedfuncPrimitiveRendererFactory());
			} else {
				if (hints.getSamplingRate() == 1) {
					renderer = new DefaultRenderer(scene, camera,
							new ShaderPrimitiveRendererFactory());
				} else {
					renderer = new SupersampledFBORenderer(scene, camera,
							hints.getSamplingRate(),
							new ShaderPrimitiveRendererFactory());
				}
			}
			
			if (canvas != null) {
				canvas.destroy();
				this.container.remove(canvas);
			}
			
			canvas = new GLCanvas(caps);
			canvas.addGLEventListener(renderer);
			canvas.addMouseListener(this);
			canvas.addMouseMotionListener(this);
			canvas.addMouseWheelListener(this);
			canvas.setSize(this.container.getSize());
			this.container.add(canvas, BorderLayout.CENTER);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Util.logger.log(Level.SEVERE, e.toString(), e);
		}
	}

	@Override
	public void begin() {
		Util.logger.info("begin()");
		
		if (requestedRenderHints != null) {
			applyHints(requestedRenderHints);
		}
		scene.clear();
	}

	@Override
	public void end() {
		Util.logger.info("end()");

		try {
			if (!container.isVisible()) {
				container.setVisible(true);
			}
			canvas.display();
		} catch (Exception e) {
			Util.logger.log(Level.SEVERE, e.toString(), e);
		}
	}

	@Override
	public void shutdown() {
		Util.logger.info("shutdown()");
		if (standalone && container instanceof JFrame) {
			((JFrame)container).dispose();
		}
		container = null;
	}

	@Override
	public void setBackgroundColor(Color color) {
		scene.setBackgroundColor(color);
	}

	@Override
	public void addPoint(double x, double y, double z,
			AppearanceState appearance) {
		// log.info("addPoint(" + x + "," + y + "," + z + ")");
		scene.addPoint(new Point(x, y, z, appearance.getSize() * POINT_SCALE,
				appearance.getColor(), appearance.getShininess(), 1));
	}

	@Override
	public void addCircle(double cx, double cy, double cz, double nx,
			double ny, double nz, double radius, AppearanceState appearance) {
		Util.logger.info("addCircle(" + cx + "," + cy + "," + cz + "," + nx + "," + ny
				+ "," + nz + "," + radius + ")");
		scene.addCircle(new Circle(cx, cy, cz, nx, ny, nz, radius, appearance
				.getColor(), appearance.getShininess(), appearance.getAlpha()));
	}

	@Override
	public void addSegment(double x1, double y1, double z1, double x2,
			double y2, double z2, AppearanceState appearance) {
		Util.logger.info("addSegment(" + x1 + "," + y1 + "," + z1 + "," + x2 + "," + y2
				+ "," + z2 + ")");
		
		addPoint(x1, y1, z1, appearance);
		addPoint(x2, y2, z2, appearance);
		
		scene.addLine(new Line(x1, y1, z1, x2, y2, z2, appearance.getSize()
				* POINT_SCALE, appearance.getColor(), appearance.getShininess(),
				LineType.SEGMENT));
	}

	@Override
	public void addLine(double x1, double y1, double z1, double x2, double y2,
			double z2, AppearanceState appearance) {
		Util.logger.info("addLine(" + x1 + "," + y1 + "," + z1 + "," + x2 + "," + y2
				+ "," + z2 + ")");
		scene.addLine(new Line(x1, y1, z1, x2, y2, z2, appearance.getSize()
				* POINT_SCALE, appearance.getColor(), appearance.getShininess(),
				LineType.LINE));
	}

	@Override
	public void addRay(double x1, double y1, double z1, double x2, double y2,
			double z2, AppearanceState appearance) {
		Util.logger.info("addRay(" + x1 + "," + y1 + "," + z1 + "," + x2 + "," + y2
				+ "," + z2 + ")");
		
		addPoint(x1, y1, z1, appearance);
		
		scene.addLine(new Line(x1, y1, z1, x2, y2, z2, appearance.getSize()
				* POINT_SCALE, appearance.getColor(), appearance.getShininess(),
				LineType.RAY));
	}

	@Override
	public void addPolygon(double[][] vertices, double[][] normals,
			AppearanceState appearance) {
//		String str = "addPolygon(";
//		for (int i = 0; i < vertices.length; ++i) {
//			if (i != 0)
//				str += ", ";
//			str += "[" + vertices[i][0] + "," + vertices[i][1] + ","
//					+ vertices[i][2] + "]";
//		}
//		str += ")";
//		Util.logger.info(str);
		
		scene.addPolygon(new Polygon(vertices, normals, appearance.getColor(),
				appearance.getShininess(), appearance.getAlpha()));
	}

	@Override
	public void addLineStrip(double[][] vertices, AppearanceState appearance,
			boolean closed) {
		for (int i = 1; i < vertices.length; ++i) {
			scene.addLine(new Line(vertices[i - 1][0], vertices[i - 1][1],
					vertices[i - 1][2], vertices[i][0], vertices[i][1],
					vertices[i][2], appearance.getSize() * POINT_SCALE,
					appearance.getColor(), appearance.getShininess(),
					LineType.SEGMENT));
			scene.addPoint(new Point(vertices[i][0], vertices[i][1],
					vertices[i][2], appearance.getSize() * POINT_SCALE,
					appearance.getColor(), appearance.getShininess() , 1.0));
		}
		scene.addPoint(new Point(vertices[0][0], vertices[0][1],
				vertices[0][2], appearance.getSize() * POINT_SCALE, appearance
						.getColor(), appearance.getShininess(), 1.0));
		if (closed) {
			scene.addLine(new Line(vertices[vertices.length - 1][0],
					vertices[vertices.length - 1][1],
					vertices[vertices.length - 1][2], vertices[0][0],
					vertices[0][1], vertices[0][2], appearance.getSize()
							* POINT_SCALE, appearance.getColor(),
					appearance.getShininess(), LineType.SEGMENT));
		}
	}

	@Override
	public void addMesh(int rows, int columns, double[][] vertices,
			double[][] normals, MeshTopology topology, AppearanceState appearance) {
		scene.addMesh(new Mesh(rows, columns, vertices, normals, appearance
				.getColor(), appearance.getShininess(), appearance.getAlpha(), topology));
	}
	
	@Override
	public void addMesh(int rows, int columns, double[][] vertices,
			NormalType normalType, MeshTopology topology, AppearanceState appearance) {
		scene.addMesh(new Mesh(rows, columns, vertices, normalType,
				appearance.getColor(), appearance.getShininess(), appearance.getAlpha(),
				topology));
	}
	
	@Override
	public void addSphere(double cx, double cy, double cz, double radius,
			AppearanceState appearance)
	{
		scene.addPoint(new Point(cx, cy, cz, radius, appearance
				.getColor(), appearance.getShininess(), appearance.getAlpha()));
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		mousePosition[0] = e.getX();
		mousePosition[1] = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
			if (e.isControlDown()) {
				camera.mouseDragged2(e.getX() - mousePosition[0],
						e.getY() - mousePosition[1]);
			} else {
				camera.mouseDragged1(e.getX() - mousePosition[0],
						e.getY() - mousePosition[1]);
			}
			drawLater();
		} else if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
			drawLater();
		}

		mousePosition[0] = e.getX();
		mousePosition[1] = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		camera.mouseWheelMoved(e.getWheelRotation());
		drawLater();
	}
	
	private void drawLater() {
		if (!drawPending) {
			drawPending = true;
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					canvas.display();
					drawPending = false;
				}
			});
		}
	}

	@Override
	public void setDepthRange(double near, double far) {
		camera.setPerspective(camera.getFieldOfView(), canvas.getWidth(),
				canvas.getHeight(), near, far);
	}

	@Override
	public void setLight(int light, LightInfo info) {
		renderer.getLightManager().setLight(light, info);
	}

	@Override
	public void disableLight(int light) {
		renderer.getLightManager().disableLight(light);
	}

	
	/**
	 * supported hints:
	 * - quality, range [0,8]
	 * - renderMode, "fixedfunction" or "programmable"
	 * - samplingRate, range [1,oo[
	 * 
	 * quality selects from a fixed set of render hints, which can be modified
	 * by specifying renderMode and samplingRate. Providing a valid render mode
	 * resets the sampling rate to 1. 
	 */
	@Override
	public void setRenderHints(Hashtable<String, Object> hintsMap) {
		
		requestedRenderHints = renderHints.clone();

		Object value;
		value = hintsMap.get("quality");
		if (value instanceof Double) {
			int quality = ((Double) value).intValue();
			quality = Math.max(0, Math.min(quality, qualityHints.length - 1));
			requestedRenderHints = qualityHints[quality];
		}

		value = hintsMap.get("renderMode");
		if (value instanceof String) {
			String renderMode = (String) value;
			if (renderMode.equals("fixedfunction")) {
				requestedRenderHints.setSamplingRate(1);
				requestedRenderHints
						.setRenderMode(RenderMode.FIXED_FUNCTION_PIPELINE);
			} else if (renderMode.equals("programmable")) {
				requestedRenderHints.setSamplingRate(1);
				requestedRenderHints
						.setRenderMode(RenderMode.PROGRAMMABLE_PIPELINE);
			}
		}

		value = hintsMap.get("samplingRate");
		if (value instanceof Double) {
			requestedRenderHints.setSamplingRate(((Double) value).intValue());
		}
	}
}
