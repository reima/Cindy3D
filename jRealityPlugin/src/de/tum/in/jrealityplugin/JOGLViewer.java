package de.tum.in.jrealityplugin;

import java.awt.BorderLayout;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import com.sun.opengl.util.BufferUtil;

public class JOGLViewer implements Cindy3DViewer, GLEventListener {
	JFrame frame;
	GLCanvas canvas;
	GLU glu = new GLU();
	
	ArrayList<Double> pointCoords = new ArrayList<Double>();
	FloatBuffer pointVertexBuffer;
	
	JOGLViewer() {
		frame = new JFrame("Cindy3D (JOGL)");
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		canvas = new GLCanvas();
		canvas.addGLEventListener(this);
		canvas.setSize(640, 480);
		
		frame.add(canvas, BorderLayout.CENTER);
		frame.pack();
	}
	
	@Override
	public void begin() {
		pointCoords.clear();
	}

	@Override
	public void end() {
		pointVertexBuffer = BufferUtil.newFloatBuffer(pointCoords.size());
		for (Double d : pointCoords) {
			pointVertexBuffer.put(d.floatValue());
		}
		pointVertexBuffer.rewind();
		
		if (!frame.isVisible()) frame.setVisible(true);
		canvas.display();
	}

	@Override
	public void addPoint(double x, double y, double z,
			AppearanceState appearance) {
		pointCoords.add(x);
		pointCoords.add(y);
		pointCoords.add(z);
	}

	@Override
	public void addCircle(double cx, double cy, double cz, double nx,
			double ny, double nz, double radius, AppearanceState appearance) {
		// TODO Auto-generated method stub

	}

	@Override
	public void shutdown() {
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
		GL gl = drawable.getGL();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslated(0, 0, 5);
		
		if (pointVertexBuffer.capacity() > 0) {
			//gl.glColor3f(0.0f, 0.0f, 0.0f);
			//gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
			//gl.glVertexPointer(3, GL.GL_FLOAT, 0, pointVertexBuffer);
			//gl.glDrawArrays(GL.GL_POINTS, 0, pointVertexBuffer.capacity()/3);
			//gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
		}
		
		//drawable.swapBuffers();
	}

	@Override
	public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL gl = drawable.getGL();
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		GL gl = drawable.getGL();
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		if (height <= 0) height = 1;
		double aspect = (double)width / height;
		glu.gluPerspective(60.0, aspect, 0.01, 100.0);
	}
}
