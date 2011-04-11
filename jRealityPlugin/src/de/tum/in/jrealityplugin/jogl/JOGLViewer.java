package de.tum.in.jrealityplugin.jogl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import org.apache.commons.math.geometry.Vector3D;

import de.tum.in.jrealityplugin.AppearanceState;
import de.tum.in.jrealityplugin.Cindy3DViewer;
import de.tum.in.jrealityplugin.jogl.Line.LineType;

public class JOGLViewer implements Cindy3DViewer, GLEventListener,
		MouseListener, MouseMotionListener, MouseWheelListener {
	private JFrame frame;
	private GLCanvas canvas;
	private GLU glu = new GLU();
	
	private float[] backgroundColor = { 0.0f, 0.0f, 0.0f, 1.0f }; 

	private ArrayList<Point> points = new ArrayList<Point>();
	private ArrayList<Circle> circles = new ArrayList<Circle>();
	private ArrayList<Line> lines = new ArrayList<Line>();
	private ArrayList<Polygon> polygons = new ArrayList<Polygon>();

	private Logger log;
	private FileHandler fh;
	private boolean mouseDown;
	private double[] mousePosition = new double[2];

	private PointRenderer pointRenderer = new PointRenderer();
	private CircleRenderer circleRenderer = new CircleRenderer();
	private LineRenderer lineRenderer = new LineRenderer();
	private PolygonRenderer polygonRenderer = new PolygonRenderer();
	
	private ModelViewerCamera camera = new ModelViewerCamera();
	private double camDistance = 5.0;

	public JOGLViewer() {
		try {
			fh = new FileHandler("C:\\tmp\\cindy.log", false);
			log = Logger.getLogger("log");
			log.addHandler(fh);
			log.setLevel(Level.ALL);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
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

		try {
			GLProfile.initSingleton(true);
			GLProfile profile = GLProfile.getDefault();
			GLCapabilities caps = new GLCapabilities(profile);
			canvas = new GLCanvas(caps);
			canvas.addGLEventListener(this);
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
		points.clear();
		circles.clear();
		lines.clear();
		polygons.clear();
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
	public void addPoint(double x, double y, double z,
			AppearanceState appearance) {
		// log.info("addPoint(" + x + "," + y + "," + z + ")");
		points.add(new Point(x, y, z, appearance.getSize(), appearance
				.getColor()));
	}

	@Override
	public void addCircle(double cx, double cy, double cz, double nx,
			double ny, double nz, double radius, AppearanceState appearance) {
		log.info("addCircle(" + cx + "," + cy + "," + cz + "," + nx + "," + ny
				+ "," + nz + "," + radius + ")");
		circles.add(new Circle(cx, cy, cz, nx, ny, nz, radius, appearance
				.getColor()));
	}

	@Override
	public void shutdown() {
		log.info("shutdown()");
		frame.dispose();
		frame = null;
	}

	@Override
	public void addSegment(double x1, double y1, double z1, double x2,
			double y2, double z2, AppearanceState appearance) {
		log.info("addSegment(" + x1 + "," + y1 + "," + z1 + "," + x2 + "," + y2
				+ "," + z2 + ")");
		
		addPoint(x1, y1, z1, appearance);
		addPoint(x2, y2, z2, appearance);
		
		lines.add(new Line(x1, y1, z1, x2, y2, z2, appearance.getSize()*0.05,
				appearance.getColor(), LineType.SEGMENT));
	}

	@Override
	public void addLine(double x1, double y1, double z1, double x2, double y2,
			double z2, AppearanceState appearance) {
		log.info("addLine(" + x1 + "," + y1 + "," + z1 + "," + x2 + "," + y2
				+ "," + z2 + ")");
		lines.add(new Line(x1, y1, z1, x2, y2, z2, appearance.getSize()*0.05,
				appearance.getColor(), LineType.LINE));
	}

	@Override
	public void addRay(double x1, double y1, double z1, double x2, double y2,
			double z2, AppearanceState appearance) {
		log.info("addRay(" + x1 + "," + y1 + "," + z1 + "," + x2 + "," + y2
				+ "," + z2 + ")");
		
		addPoint(x1, y1, z1, appearance);
		
		lines.add(new Line(x1, y1, z1, x2, y2, z2, appearance.getSize()*0.05,
				appearance.getColor(), LineType.RAY));
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
		
		polygons.add(new Polygon(vertices, normals, appearance.getColor()));
	}
	
	@Override
	public void addLineStrip(double[][] vertices, AppearanceState appearance,
			boolean closed) {
		for (int i = 1; i < vertices.length; ++i) {
			lines.add(new Line(vertices[i - 1][0], vertices[i - 1][1],
					vertices[i - 1][2], vertices[i][0], vertices[i][1],
					vertices[i][2], appearance.getSize() * 0.05, appearance
							.getColor(), LineType.SEGMENT));
			points.add(new Point(vertices[i][0], vertices[i][1],
					vertices[i][2], appearance.getSize(), appearance
							.getColor()));
		}
		points.add(new Point(vertices[0][0], vertices[0][1], vertices[0][2],
				appearance.getSize(), appearance.getColor()));
		if (closed)
			lines.add(new Line(vertices[vertices.length - 1][0],
					vertices[vertices.length - 1][1],
					vertices[vertices.length - 1][2], vertices[0][0],
					vertices[0][1], vertices[0][2],
					appearance.getSize() * 0.05, appearance.getColor(),
					LineType.SEGMENT));
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		// log.info("display()");

		GL2 gl = drawable.getGL().getGL2();
		gl.glClearColor(backgroundColor[0], backgroundColor[1],
				backgroundColor[2], backgroundColor[3]);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		camera.lookAt(new Vector3D(0.0, 0.0, camDistance), Vector3D.ZERO,
				Vector3D.PLUS_J);
		gl.glMultMatrixf(Util.matrixToFloatArrayTransposed(camera.getTransform()), 0);
		
		JOGLRenderState jrs = new JOGLRenderState(gl, camera);

		pointRenderer.render(jrs, points);
		circleRenderer.render(jrs, circles);
		lineRenderer.render(jrs, lines);
		polygonRenderer.render(jrs, polygons);

		// gl.glFlush();
		// drawable.swapBuffers();
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		pointRenderer.dispose(drawable.getGL());
		circleRenderer.dispose(drawable.getGL());
		lineRenderer.dispose(drawable.getGL());
		polygonRenderer.dispose(drawable.getGL());
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		log.info("init()");

		try {
			// drawable.setGL(new DebugGL2(drawable.getGL().getGL2()));
			GL2 gl = drawable.getGL().getGL2();
			gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			gl.glLoadIdentity();

			gl.glEnable(GL2.GL_DEPTH_TEST);

			gl.glEnable(GL2.GL_LIGHTING);
			gl.glEnable(GL2.GL_LIGHT0);
			gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, new float[] { 10.0f,
					10.0f, 0.0f, 1.0f }, 0);

			gl.glEnable(GL2.GL_LIGHT1);
			gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, new float[] { 0.0f,
					0.0f, 0.0f, 1.0f }, 0);

			if (!pointRenderer.init(gl))
				log.severe("Point renderer initialization failed");
			if (!circleRenderer.init(gl))
				log.severe("Circle renderer initialization failed");
			if (!lineRenderer.init(gl))
				log.severe("Line renderer initialization failed");
			if (!polygonRenderer.init(gl))
				log.severe("Polygon renderer initialization failed");
		} catch (GLException e) {
			// TODO Auto-generated catch block
			log.log(Level.SEVERE, e.toString(), e);
		}
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		log.info("reshape(" + x + "," + y + "," + width + "," + height + ")");
		GL2 gl = drawable.getGL().getGL2();
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		if (height <= 0)
			height = 1;
		double aspect = (double) width / height;
		glu.gluPerspective(60.0, aspect, 0.01, 1000.0);
		display(drawable);
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
		if (e.getButton() == MouseEvent.BUTTON1) {
			mouseDown = true;
			mousePosition[0] = e.getX();
			mousePosition[1] = e.getY();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			mouseDown = false;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (!mouseDown)
			return;
		
		camera.mouseDragged(e.getX() - mousePosition[0],
				e.getY() - mousePosition[1]);

		mousePosition[0] = e.getX();
		mousePosition[1] = e.getY();
		canvas.display();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getWheelRotation() > 0) {
			camDistance *= 1.1;
		} else {
			camDistance /= 1.1;
		}
		canvas.display();
	}

	@Override
	public void setBackgroundColor(Color color) {
		color.getRGBComponents(backgroundColor);
	}
}
