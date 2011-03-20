package de.tum.in.reitinge.test;

import java.awt.BorderLayout;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.swing.JFrame;
import javax.swing.JPanel;

import de.jreality.jogl.shader.GlslShaderProgram;

public class JOGLTest extends JPanel implements GLEventListener {
	GlslShaderProgram program = null;
	
	public JOGLTest() {
		GLCanvas canvas = new GLCanvas();
		setLayout(new BorderLayout());
		add(canvas, BorderLayout.CENTER);
		canvas.addGLEventListener(this);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	      final int WINDOW_WIDTH = 640;
	      final int WINDOW_HEIGHT = 480;
	      final String WINDOW_TITLE = "JOGL Program Template";

	      JFrame frame = new JFrame();
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
		
		program.bind(gl);
		
		gl.glUniform1f(program.getUniformLocation(gl, "sphereRadius"), 1.0f);
		gl.glUniform3f(program.getUniformLocation(gl, "sphereCenter"), 0.0f, 0.0f, 0.0f);
		
		gl.glBegin(GL.GL_QUADS);
			gl.glVertex2f(-1.0f, -1.0f);
			gl.glVertex2f(1.0f, -1.0f);
			gl.glVertex2f(1.0f, 1.0f);
			gl.glVertex2f(-1.0f, 1.0f);
		gl.glEnd();
		
		program.unbind(gl);
	}

	@Override
	public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL gl = drawable.getGL();		
		try {
			program = new GlslShaderProgram(gl, "./shader/sphere.vert", "./shader/sphere.frag");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3,
			int arg4) {
		// TODO Auto-generated method stub
		
	}
}
