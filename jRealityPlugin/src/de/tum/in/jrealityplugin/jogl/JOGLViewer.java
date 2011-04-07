package de.tum.in.jrealityplugin.jogl;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.net.URL;
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

import com.jogamp.opengl.util.glsl.ShaderCode;

import de.tum.in.jrealityplugin.AppearanceState;
import de.tum.in.jrealityplugin.Cindy3DViewer;

public class JOGLViewer implements Cindy3DViewer, GLEventListener,
		MouseListener, MouseMotionListener, MouseWheelListener {
	private JFrame frame;
	private GLCanvas canvas;
	private GLU glu = new GLU();

	// private static final int FLOAT_SIZE = 4;

	private ArrayList<Point> points = new ArrayList<Point>();
	// private FloatBuffer pointBuffer;

	private Logger log;
	private FileHandler fh;
	private boolean mouseDown;
	private double[] mousePosition = new double[2];
	private double[] rotation = new double[2];

	private PointRenderer pointRenderer = new PointRenderer();
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
	}

	@Override
	public void end() {
		log.info("end()");
		updatePoints();

		try {
			if (!frame.isVisible())
				frame.setVisible(true);
			canvas.display();
		} catch (Exception e) {
			log.log(Level.SEVERE, e.toString(), e);
		}
	}

	private void updatePoints() {
		// int requestedSize = points.size() * 6;
		// if (pointBuffer == null || pointBuffer.capacity() < requestedSize) {
		// log.info("Buffer allocation");
		// pointBuffer = ByteBuffer.allocateDirect(requestedSize * FLOAT_SIZE)
		// .order(ByteOrder.nativeOrder()).asFloatBuffer();
		// } else {
		// pointBuffer.rewind();
		// }
		// for (Point p : points) {
		// pointBuffer.put((float) p.x);
		// pointBuffer.put((float) p.y);
		// pointBuffer.put((float) p.z);
		// pointBuffer.put(p.color.getRGBColorComponents(null));
		// }
		// pointBuffer.rewind();
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
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	@Override
	public void addLine(double x1, double y1, double z1, double x2, double y2,
			double z2, AppearanceState appearance) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addRay(double x1, double y1, double z1, double x2, double y2,
			double z2, AppearanceState appearance) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addPolygon(double[][] vertices, AppearanceState appearance) {
		// TODO Auto-generated method stub

	}

	@Override
	public void display(GLAutoDrawable drawable) {
		// log.info("display()");

		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		glu.gluLookAt(0.0, 0.0, camDistance, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);
		gl.glRotated(rotation[0], 0.0, 1.0, 0.0);
		gl.glRotated(rotation[1], 1.0, 0.0, 0.0);

		pointRenderer.render(gl, points);

		// gl.glFlush();
		// drawable.swapBuffers();
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		pointRenderer.dispose(drawable.getGL());
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
			gl.glClearColor(0.65625f, 0.6875f, 0.75f, 0.0f);

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
		glu.gluPerspective(60.0, aspect, 0.01, 100.0);
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
		rotation[0] += (e.getX() - mousePosition[0]);
		rotation[1] += (e.getY() - mousePosition[1]);
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

}
