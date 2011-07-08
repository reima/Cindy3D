package de.tum.in.cindy3dplugin;

import java.awt.Color;

public interface Cindy3DViewer {
	enum MeshTopology {
		OPEN,
		CLOSE_X,
		CLOSE_Y,
		CLOSE_XY
	};
	
	/**
	 * Marks the beginning of a new scene
	 */
	void begin();
	/**
	 * Finishes and displays the scene
	 */
	void end();
	/**
	 * Adds a new point to the current scene
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param appearance
	 */
	void addPoint(double x, double y, double z, AppearanceState appearance);
	
	void addCircle(double cx, double cy, double cz,
			       double nx, double ny, double nz, double radius,
			       AppearanceState appearance);
	
	void addSphere(double cx, double cy, double cz, double radius,
			AppearanceState appearance);
	/**
	 * Shuts down the viewer
	 */
	void shutdown();
	void addSegment(double x1, double y1, double z1,
					double x2, double y2, double z2,
					AppearanceState appearance);
	void addLine(double x1, double y1, double z1,
				 double x2, double y2, double z2,
				 AppearanceState appearance);
	void addRay(double x1, double y1, double z1,
				double x2, double y2, double z2,
				AppearanceState appearance);
	void addLineStrip(double[][] vertices, AppearanceState appearance, boolean closed);
	void addPolygon(double[][] vertices, double[][] normals,
			AppearanceState appearance);
	void addMesh(int rows, int columns, double[][] vertices,
			double[][] normals, MeshTopology topology, AppearanceState appearance);
	void addMesh(int rows, int columns, double[][] vertices,
			boolean perVertexNormals, MeshTopology topology, AppearanceState appearance);
	void setBackgroundColor(Color color);
	void setDepthRange(double near, double far);
}
