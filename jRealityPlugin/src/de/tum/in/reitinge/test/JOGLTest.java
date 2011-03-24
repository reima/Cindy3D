package de.tum.in.reitinge.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLDrawable;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.sun.opengl.util.GLUT;

import de.jreality.jogl.shader.GlslShaderProgram;
import de.jreality.soft.NewDoublePolygonRasterizer;

public class JOGLTest extends JPanel implements GLEventListener,
						MouseListener, MouseMotionListener, MouseWheelListener {
	private static final long serialVersionUID = 1L;

	GlslShaderProgram program = null;
	
	private GLAutoDrawable drawable = null;
	private GLU glu = new GLU();
	private GLUT glut = new GLUT();
	
	private boolean mouseDown = false;
	private float mouse[] = {0,0};
	private float rotate[] = {0,0};
	private float zoom = 0;
	
	private boolean first = true;
	
	public JOGLTest() {
		GLCanvas canvas = new GLCanvas(new GLCapabilities());
		setLayout(new BorderLayout());
		add(canvas, BorderLayout.CENTER);
		canvas.addGLEventListener(this);
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		canvas.addMouseWheelListener(this);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	      final int WINDOW_WIDTH = 800;
	      final int WINDOW_HEIGHT = 600;
	      final String WINDOW_TITLE = "JOGL Program Template";

	      JFrame frame = new JFrame();
	      frame.setBackground(Color.blue);
	      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	      final JOGLTest joglMain = new JOGLTest();
	      frame.setContentPane(joglMain);
	      frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
	      frame.setVisible(true);
	      frame.setTitle(WINDOW_TITLE);
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL gl = drawable.getGL();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
		gl.glColor3f(0, 1, 0);
		
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glPushMatrix();
		
		gl.glTranslatef(0, 0, -zoom);
		gl.glRotatef(rotate[0], 0.0f, 1.0f, 0.0f);
		gl.glRotatef(rotate[1], 1.0f, 0.0f, 0.0f);
		
		gl.glEnable(GL.GL_BLEND);
		gl.glDepthMask(false);
		drawTransparent(gl);
		gl.glDepthMask(true);
		
		gl.glDisable(GL.GL_BLEND);
		
		drawOpaque(gl);
		
		gl.glEnable(GL.GL_BLEND);
		gl.glDepthMask(false);
		drawTransparent(gl);
		gl.glDepthMask(true);
		
		gl.glColorMask(false, false, false, false);
		drawOpaque(gl);
		drawTransparent(gl);
		gl.glColorMask(true, true, true, true);
		
		gl.glEnable(GL.GL_BLEND);
		gl.glDepthMask(false);
		drawTransparent(gl);
		gl.glDepthMask(true);

		drawOpaque(gl);

//		gl.glDepthMask(false);
//		gl.glEnable(GL.GL_BLEND);
//		drawTransparent(gl);
//		gl.glDepthMask(true);
		
//		gl.glDisable(GL.GL_BLEND);
//		
////		gl.glColorMask(false, false, false, false);
//		drawOpaque(gl);
////		gl.glColorMask(true, true, true, true);
//		
//		gl.glDepthMask(false);
//		gl.glEnable(GL.GL_BLEND);
//		drawTransparent(gl);
//		gl.glDepthMask(true);
//		
//		gl.glDisable(GL.GL_BLEND);
		
		//drawOpaque(gl);

//		program.bind(gl);		
//		gl.glUniform1f(program.getUniformLocation(gl, "sphereRadius"), 0.5f);
//		gl.glUniform3f(program.getUniformLocation(gl, "sphereCenter"), 0.0f, 0.0f, 0.0f);
//		gl.glUniform3f(program.getUniformLocation(gl, "sphereColor"), 1.0f, 0.0f, 0.0f);
//		program.unbind(gl);
		
		gl.glPopMatrix();
		
		gl.glFlush();
	}

	private void drawOpaque(GL gl) {
//		gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
//		gl.glBegin(GL.GL_QUADS);
//			gl.glVertex3f(-1.0f, -1.0f,1.0f);
//			gl.glVertex3f(1.0f, -1.0f,1.0f);
//			gl.glVertex3f(1.0f, 1.0f,1.0f);
//			gl.glVertex3f(-1.0f, 1.0f,1.0f);
//		gl.glEnd();
		
//		gl.glTranslated(0, 0, 7.5);
//			gl.glColor4f(1, 0, 0, 1);
//			glut.glutSolidTorus(1, 2, 32, 32);
//		gl.glTranslated(0, 0, -7.5);

	}

	private void drawTransparent(GL gl) {
		
		int steps = 128;
		double step = 2.0*Math.PI / steps;
		int j;
		
		double shift[][] = {{0,0,0},{1,-0.5,-1.3},{3,0.5,1.6},{2,0,3}};
		
		for (int k=0; k<1; ++k) {
			for (int i=0; i<steps; ++i) {
				j = i%8;
				gl.glColor4f(j/4, (j%4)/2, j%2, 0.02f);
				
				double dir[] = {Math.cos(i*step), Math.sin(i*step)};
				gl.glBegin(GL.GL_QUADS);
					gl.glVertex3d(dir[0]*0.5+shift[k][0], -1.0f+shift[k][1], dir[1]*0.5+shift[k][2]);
					gl.glVertex3d(dir[0]*2+shift[k][0], -1.0f+shift[k][1], dir[1]*2+shift[k][2]);
					gl.glVertex3d(dir[0]*2+shift[k][0],  1.0f+shift[k][1], dir[1]*2+shift[k][2]);
					gl.glVertex3d(dir[0]*0.5+shift[k][0],  1.0f+shift[k][1], dir[1]*0.5+shift[k][2]);
				gl.glEnd();
			}
		}
		
//		gl.glBegin(GL.GL_QUADS);
//			gl.glVertex3d(-1, -1,)
//		gl.glEnd();
		
//		gl.glColor4d(1, 0, 0, 0.1);
//		glut.glutSolidSphere(1, 32, 32);
		
//		for (int i=0; i<8; ++i) {
//			if (i == 4) {
//				gl.glTranslated(0, 0, 1.5);
//				continue;
//			}
//			gl.glColor4f(i/4, (i%4)/2, i%2, 0.1f);
//			gl.glTranslated(0, 0, 1.5);
//			glut.glutSolidTorus(1, 2, 32, 32);
//		}
//		gl.glTranslated(0, 0, -8*1.5);
	}

	@Override
	public void displayChanged(GLAutoDrawable drawable,
			boolean modeChanged, boolean arg2) {
		System.out.println("displayChanged() called");
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		
		System.out.println("init() called");
		this.drawable = drawable;
		GL gl = drawable.getGL();
		
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		//gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_DST_ALPHA);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);
		
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		glu.gluLookAt(0, 0, 5, 0, 0, 0, 0, 1, 0);
		
		
		try {
			program = new GlslShaderProgram(gl,
					getClass().getResourceAsStream("/de/tum/in/jrealityplugin/resources/shader/sphere.vert"),
					getClass().getResourceAsStream("/de/tum/in/jrealityplugin/resources/shader/sphere.frag")
			);		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y,
												 int width, int height) {
		System.out.println("reshape() called");
		if (height <=0)
			height = 1;
		GL gl = drawable.getGL();
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(45.0f, (float)width/(float)height, 0.1f, 500.0f);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
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
		if (e.getButton() == MouseEvent.BUTTON3) {
			mouseDown = true;
			mouse[0] = e.getX();
			mouse[1] = e.getY();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3)
			mouseDown = false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (mouseDown) {
			rotate[0] += e.getX() - mouse[0];
			rotate[1] += e.getY() - mouse[1];
			
			mouse[0] = e.getX();
			mouse[1] = e.getY();
			
			drawable.display();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		zoom += e.getWheelRotation();
		drawable.display();
	}
}
