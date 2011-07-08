package de.tum.in.cindy3dplugin.jogl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import org.apache.commons.math.geometry.Vector3D;

import de.tum.in.cindy3dplugin.AppearanceState;
import de.tum.in.cindy3dplugin.Cindy3DViewer;
import de.tum.in.cindy3dplugin.jogl.Line.LineType;

public class JOGLViewer implements Cindy3DViewer, MouseListener,
		MouseMotionListener, MouseWheelListener {
	private static final double POINT_SCALE = 0.05;
	private static final boolean FILE_LOGGING = false;
	
	private JFrame frame;
	private GLCanvas canvas;
	
	private JOGLRenderer renderer;

	private Scene scene = new Scene();
	private ModelViewerCamera camera = new ModelViewerCamera();
	
	private Logger log;
	private double[] mousePosition = new double[2];
	
	private boolean drawPending = false;

	public JOGLViewer() {
		try {
			log = Logger.getLogger("log");
			if (FILE_LOGGING) {
				FileHandler fh = new FileHandler("C:\\tmp\\cindy.log", false);
				fh.setFormatter(new SimpleFormatter());
				log.addHandler(fh);
			}
			log.setLevel(Level.ALL);
			log.log(Level.INFO, "Log started");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		frame = new JFrame("Cindy3D (JOGL)");
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		camera.lookAt(new Vector3D(0, 0, 5), Vector3D.ZERO, Vector3D.PLUS_J);

		try {
			GLProfile.initSingleton(true);
			GLProfile profile = GLProfile.getDefault();
			GLCapabilities caps = new GLCapabilities(profile);
			canvas = new GLCanvas(caps);
			//renderer = new DefaultRenderer(scene, camera);
			//renderer = new SupersampledRenderer(scene, camera);
			renderer = new SupersampledFBORenderer(scene, camera);
			canvas.addGLEventListener(renderer);
			canvas.addMouseListener(this);
			canvas.addMouseMotionListener(this);
			canvas.addMouseWheelListener(this);
			canvas.setSize(640, 480);

			frame.add(canvas, BorderLayout.CENTER);
			frame.pack();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.log(Level.SEVERE, e.toString(), e);
		}
	}

	@Override
	public void begin() {
		log.info("begin()");
		scene.clear();
	}

	@Override
	public void end() {
		log.info("end()");

		try {
			if (!frame.isVisible())
				frame.setVisible(true);
			canvas.display();
		} catch (Exception e) {
			log.log(Level.SEVERE, e.toString(), e);
		}
	}

	@Override
	public void shutdown() {
		log.info("shutdown()");
		frame.dispose();
		frame = null;
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
				appearance.getColor(), 1));
	}

	@Override
	public void addCircle(double cx, double cy, double cz, double nx,
			double ny, double nz, double radius, AppearanceState appearance) {
		log.info("addCircle(" + cx + "," + cy + "," + cz + "," + nx + "," + ny
				+ "," + nz + "," + radius + ")");
		scene.addCircle(new Circle(cx, cy, cz, nx, ny, nz, radius, appearance
				.getColor()));
	}

	@Override
	public void addSegment(double x1, double y1, double z1, double x2,
			double y2, double z2, AppearanceState appearance) {
		log.info("addSegment(" + x1 + "," + y1 + "," + z1 + "," + x2 + "," + y2
				+ "," + z2 + ")");
		
		addPoint(x1, y1, z1, appearance);
		addPoint(x2, y2, z2, appearance);
		
		scene.addLine(new Line(x1, y1, z1, x2, y2, z2, appearance.getSize()
				* POINT_SCALE, appearance.getColor(), LineType.SEGMENT));
	}

	@Override
	public void addLine(double x1, double y1, double z1, double x2, double y2,
			double z2, AppearanceState appearance) {
		log.info("addLine(" + x1 + "," + y1 + "," + z1 + "," + x2 + "," + y2
				+ "," + z2 + ")");
		scene.addLine(new Line(x1, y1, z1, x2, y2, z2, appearance.getSize()
				* POINT_SCALE, appearance.getColor(), LineType.LINE));
	}

	@Override
	public void addRay(double x1, double y1, double z1, double x2, double y2,
			double z2, AppearanceState appearance) {
		log.info("addRay(" + x1 + "," + y1 + "," + z1 + "," + x2 + "," + y2
				+ "," + z2 + ")");
		
		addPoint(x1, y1, z1, appearance);
		
		scene.addLine(new Line(x1, y1, z1, x2, y2, z2, appearance.getSize()
				* POINT_SCALE, appearance.getColor(), LineType.RAY));
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
//		log.info(str);
		
		scene.addPolygon(new Polygon(vertices, normals, appearance.getColor(),
				appearance.getOpacity()));
	}

	@Override
	public void addLineStrip(double[][] vertices, AppearanceState appearance,
			boolean closed) {
		for (int i = 1; i < vertices.length; ++i) {
			scene.addLine(new Line(vertices[i - 1][0], vertices[i - 1][1],
					vertices[i - 1][2], vertices[i][0], vertices[i][1],
					vertices[i][2], appearance.getSize() * POINT_SCALE,
					appearance.getColor(), LineType.SEGMENT));
			scene.addPoint(new Point(vertices[i][0], vertices[i][1],
					vertices[i][2], appearance.getSize() * POINT_SCALE,
					appearance.getColor(), 1.0));
		}
		scene.addPoint(new Point(vertices[0][0], vertices[0][1],
				vertices[0][2], appearance.getSize() * POINT_SCALE, appearance
						.getColor(), 1.0));
		if (closed) {
			scene.addLine(new Line(vertices[vertices.length - 1][0],
					vertices[vertices.length - 1][1],
					vertices[vertices.length - 1][2], vertices[0][0],
					vertices[0][1], vertices[0][2], appearance.getSize()
							* POINT_SCALE, appearance.getColor(),
					LineType.SEGMENT));
		}
	}

	@Override
	public void addMesh(int rows, int columns, double[][] vertices,
			double[][] normals, MeshTopology topology, AppearanceState appearance) {
		scene.addMesh(new Mesh(rows, columns, vertices, normals, appearance
				.getColor(), appearance.getOpacity(), topology));
	}
	
	@Override
	public void addMesh(int rows, int columns, double[][] vertices,
			boolean perVertexNormals, MeshTopology topology, AppearanceState appearance) {
		scene.addMesh(new Mesh(rows, columns, vertices, perVertexNormals,
				appearance.getColor(), appearance.getOpacity(),
				topology));
	}
	
	@Override
	public void addSphere(double cx, double cy, double cz, double radius,
			AppearanceState appearance)
	{
		scene.addPoint(new Point(cx, cy, cz, radius, appearance
				.getColor(), appearance.getOpacity()));
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
		camera.setPerspective(camera.getFieldOfView(), camera.getAspectRatio(),
				near, far);
	}
}
