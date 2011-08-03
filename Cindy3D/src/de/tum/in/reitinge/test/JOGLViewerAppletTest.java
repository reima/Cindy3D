package de.tum.in.reitinge.test;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;

import de.tum.in.cindy3dplugin.AppearanceState;
import de.tum.in.cindy3dplugin.Cindy3DViewer;
import de.tum.in.cindy3dplugin.Cindy3DViewer.MeshTopology;
import de.tum.in.cindy3dplugin.Cindy3DViewer.NormalType;
import de.tum.in.cindy3dplugin.jogl.JOGLViewer;

public class JOGLViewerAppletTest extends Applet {
	private static final long serialVersionUID = 743076563164770653L;
	
	@Override
	public void init() {
		setLayout(new BorderLayout());
		JOGLViewer viewer = new JOGLViewer(this);
		viewer.begin();
		enneper(viewer, true);
		viewer.end();
	}
	
	@Override
	public void start() {
	}
	
	@Override
	public void stop() {
	}
	
	public static void enneper(Cindy3DViewer viewer, boolean dots) {
		AppearanceState app = new AppearanceState(Color.white, 60, 1, 1);
		
		viewer.setBackgroundColor(Color.black);
		int rr = 30;
		int ss = 30;
		
		double[][] vertices = new double[(2*rr+1)*(2*ss+1)][3];
		
		int i = 0;
		for (int ii = -rr; ii <= rr; ++ii) {
			double u = ii/20.0;
			for (int jj = -ss; jj <= ss; ++jj, ++i) {
				double v = jj/20.0;
				double x = u - u*u*u/3.0 + u*v*v;
				double y = v - v*v*v/3.0 + v*u*u;
				double z = u*u - v*v;
				vertices[i][0] = x;
				vertices[i][1] = y;
				vertices[i][2] = z;
			}
		}
		
		if (dots) {
			app.setSize(0.6);
			app.setColor(new Color(0.9f, 0.1f, 0.1f));
			for (double[] vertex : vertices) {
				viewer.addPoint(vertex[0], vertex[1], vertex[2], app);
			}
		}
		
		app.setAlpha(0.8);
		app.setColor(new Color(0.5f, 0.5f, 1.0f));
		viewer.addMesh(2*rr+1, 2*ss+1, vertices, NormalType.PER_VERTEX, MeshTopology.OPEN, app);
	}
}
