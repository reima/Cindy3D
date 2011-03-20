package de.tum.in.jrealityplugin;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.geometry.IndexedLineSetFactory;
import de.jreality.geometry.PointSetFactory;
import de.jreality.math.Rn;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.Camera;
import de.jreality.scene.tool.Tool;
import de.jreality.tools.ClickWheelCameraZoomTool;
import de.jreality.tools.DraggingTool;
import de.jreality.tools.RotateTool;
import de.jreality.ui.viewerapp.ViewerApp;
import de.jreality.util.CameraUtility;

@SuppressWarnings("deprecation")
public class JRealityViewer implements Cindy3DViewer {
	private ViewerApp viewer;
	
	private Camera camera;

	private SceneGraphComponent sceneRoot;

	// Point resources
	private SceneGraphComponent scenePoints;
	private PointSetFactory psf;

	private ArrayList<double[]> pointCoordinates;
	private ArrayList<Color> pointColors;
	private ArrayList<Double> pointSizes;

	// Line resources
	private SceneGraphComponent sceneLines;
	private IndexedLineSetFactory ilsf;

	private ArrayList<double[]> lineCoordinates;
	private ArrayList<Integer> lineIndices;
	private ArrayList<Double> lineSizes;
	private ArrayList<Color> lineColors;
	
	// Polygon resources
	private SceneGraphComponent scenePolygons;
	private IndexedFaceSetFactory ifsf;
	
	private ArrayList<double[][]> polygonVertices;
	private ArrayList<Color> polygonColors;
	private int polygonTotalVertexCount;

	public JRealityViewer() {
		psf = new PointSetFactory();
		pointCoordinates = new ArrayList<double[]>();
		pointColors = new ArrayList<Color>();
		pointSizes = new ArrayList<Double>();

		ilsf = new IndexedLineSetFactory();
		lineCoordinates = new ArrayList<double[]>();
		lineIndices = new ArrayList<Integer>();
		lineSizes = new ArrayList<Double>();
		lineColors = new ArrayList<Color>();
		
		ifsf = new IndexedFaceSetFactory();
		polygonVertices = new ArrayList<double[][]>();
		polygonColors = new ArrayList<Color>();
		polygonTotalVertexCount = 0;

		sceneRoot = new SceneGraphComponent("root");
		
		// TODO: Set custom appearances for these components
		scenePoints = new SceneGraphComponent("points");
		scenePoints.setGeometry(psf.getGeometry());

		sceneLines = new SceneGraphComponent("lines");
		sceneLines.setGeometry(ilsf.getGeometry());
		
		scenePolygons = new SceneGraphComponent("polygons");
		scenePolygons.setGeometry(ifsf.getGeometry());

		sceneRoot.addChild(scenePoints);
		sceneRoot.addChild(sceneLines);
		sceneRoot.addChild(scenePolygons);

		viewer = new ViewerApp(sceneRoot);
		viewer.setAttachBeanShell(false);
		viewer.setAttachNavigator(true);
		viewer.setShowMenu(true);
		viewer.setBackgroundColor(Color.BLACK);

		// Replace default tools with custom tools
		List<SceneGraphComponent> components =
			new LinkedList<SceneGraphComponent>(
					viewer.getSceneRoot().getChildComponents());
		for (SceneGraphComponent component : components) {
			if (component.getName().equals("scene")) {
				List<Tool> tools = new LinkedList<Tool>(component.getTools());
				for (Tool tool : tools) {
					component.removeTool(tool);
				}
				// Rotation
				RotateTool rotateTool = new RotateTool();
				rotateTool.setFixOrigin(true);
				component.addTool(rotateTool);
				// Dragging
				DraggingTool draggingTool = new DraggingTool();
				component.addTool(draggingTool);
				// Zooming
				ClickWheelCameraZoomTool clickWheelCameraZoomTool =
					new ClickWheelCameraZoomTool();
				component.addTool(clickWheelCameraZoomTool);
				break;
			}
		}
		
		List<Tool> tools = new LinkedList<Tool>(viewer.getSceneRoot().getTools());
		for (Tool tool : tools) {
			viewer.getSceneRoot().removeTool(tool);
		}
		
		// Set camera near and far plane
		camera = CameraUtility.getCamera(viewer.getCurrentViewer());
		camera.setNear(0.1);
		camera.setFar(1000.0);
		
		viewer.update();
		viewer.getFrame().setSize(600, 600);
	}

	/* (non-Javadoc)
	 * @see de.tum.in.jrealityplugin.Cindy3DViewer#begin()
	 */
	@Override
	public void begin() {
		clearPoints();
		clearLines();
		clearPolygons();
	}

	/* (non-Javadoc)
	 * @see de.tum.in.jrealityplugin.Cindy3DViewer#end()
	 */
	@Override
	public void end() {
		updatePoints();
		updateLines();
		updatePolygons();

		viewer.getFrame().setVisible(true);
	}

	/* (non-Javadoc)
	 * @see de.tum.in.jrealityplugin.Cindy3DViewer#addPoint(double, double, double)
	 */
	@Override
	public void addPoint(double x, double y, double z,
						 AppearanceState appearance) {
		pointCoordinates.add(new double[] { x, y, z });
		pointColors.add(appearance.getColor());
		pointSizes.add(appearance.getSize());
	}

	@Override
	public void addSegment(double x1, double y1, double z1, double x2, double y2,
			double z2, AppearanceState appearance) {
		lineCoordinates.add(new double[] { x1, y1, z1 });
		lineCoordinates.add(new double[] { x2, y2, z2 });
		lineColors.add(appearance.getColor());
		lineIndices.add(lineCoordinates.size() - 2);
		lineIndices.add(lineCoordinates.size() - 1);
		lineSizes.add(appearance.getSize());
	}
	
	@Override
	public void addLine(double x1, double y1, double z1, double x2, double y2,
			double z2, AppearanceState appearance) {
		double direction[] = {
				x2 - x1,
				y2 - y1,
				z2 - z1
		};
		Rn.setEuclideanNorm(direction, 1000, direction);
		addSegment(x1 + direction[0], y1 + direction[1], z1 + direction[2],
				x1 - direction[0], y1 - direction[1], z1 - direction[2],
				appearance);
	}

	@Override
	public void addRay(double x1, double y1, double z1, double x2, double y2,
			double z2, AppearanceState appearance) {
		double direction[] = {
				x2 - x1,
				y2 - y1,
				z2 - z1
		};
		Rn.setEuclideanNorm(direction, 1000, direction);
		addSegment(x1, y1, z1,
				x1 + direction[0], y1 + direction[1], z1 + direction[2],
				appearance);
	}

	@Override
	public void addPolygon(double[][] vertices, AppearanceState appearance) {
		polygonVertices.add(vertices);
		polygonColors.add(appearance.getColor());
		polygonTotalVertexCount += vertices.length;
	}

	/* (non-Javadoc)
	 * @see de.tum.in.jrealityplugin.Cindy3DViewer#shutdown()
	 */
	@Override
	public void shutdown() {
		viewer.getFrame().setVisible(false);
	}

	/**
	 * Deletes all point primitives from the internal data structures
	 */
	private void clearPoints() {
		pointCoordinates.clear();
		pointColors.clear();
		pointSizes.clear();
	}

	/**
	 * Transfers internal point data to jReality
	 */
	private void updatePoints() {
		if (pointCoordinates.size() == 0)
			return;
		psf.setVertexCount(pointCoordinates.size());
		psf.setVertexCoordinates(pointCoordinates.toArray(new double[0][0]));
		psf.setVertexColors(pointColors.toArray(new Color[0]));

		double[] sizesArray = new double[pointSizes.size()];
		for (int i = 0; i < pointSizes.size(); ++i)
			sizesArray[i] = pointSizes.get(i);

		psf.setVertexRelativeRadii(sizesArray);
		psf.update();
	}

	/**
	 * Deletes all line primitives from the internal data structures
	 */
	private void clearLines() {
		lineCoordinates.clear();
		lineIndices.clear();
		lineSizes.clear();
		lineColors.clear();
	}

	/**
	 * Transfers internal line data to jReality
	 */
	private void updateLines() {
		if (lineCoordinates.size() == 0)
			return;
		ilsf.setVertexCount(lineCoordinates.size());
		ilsf.setVertexCoordinates(lineCoordinates.toArray(new double[0][0]));

		Color[] pointColorsArray = new Color[lineColors.size()*2];
		for (int i = 0; i < lineColors.size(); i++) {
			pointColorsArray[2*i] = lineColors.get(i);
			pointColorsArray[2*i+1] = lineColors.get(i);
		}
		ilsf.setVertexColors(pointColorsArray);
		pointColorsArray = null;

		double[] pointSizesArray = new double[lineSizes.size()*2];
		for (int i = 0; i < lineColors.size(); i++) {
			pointSizesArray[2*i] = lineSizes.get(i);
			pointSizesArray[2*i+1] = lineSizes.get(i);
		}
		ilsf.setVertexRelativeRadii(pointSizesArray);

		ilsf.setEdgeCount(lineIndices.size() / 2);
		ilsf.setEdgeColors(lineColors.toArray(new Color[0]));

		int[] indicesArray = new int[lineIndices.size()];
		for (int i = 0; i < lineIndices.size(); ++i)
			indicesArray[i] = lineIndices.get(i);
		ilsf.setEdgeIndices(indicesArray);

		double[] sizesArray = new double[lineSizes.size()];
		for (int i = 0; i < lineSizes.size(); ++i)
			sizesArray[i] = lineSizes.get(i);
		ilsf.setEdgeRelativeRadii(sizesArray);

		ilsf.update();
	}

	/**
	 * Deletes all polygons form the interal data structures
	 */
	private void clearPolygons() {
		polygonVertices.clear();
		polygonColors.clear();
		polygonTotalVertexCount = 0;
	}

	/**
	 * Transfers internal polygon data to jReality
	 */
	private void updatePolygons() {
		if (polygonTotalVertexCount == 0)
			return;
		
		int faceCount = polygonVertices.size();
		double[][] vertices = new double[polygonTotalVertexCount][3];
		int[][] faceIndices = new int[faceCount][];
		int vertexId = 0;
		for (int faceId = 0; faceId < faceCount; ++faceId) {
			int faceVertexId = 0;
			double[][] faceVertices = polygonVertices.get(faceId);
			int[] indices = new int[faceVertices.length]; 
			for (double[] vertex : faceVertices) {
				vertices[vertexId] = vertex;
				indices[faceVertexId] = vertexId;
				++vertexId;
				++faceVertexId;
			}
			faceIndices[faceId] = indices;
		}

		ifsf.setVertexCount(polygonTotalVertexCount);
		ifsf.setVertexCoordinates(vertices);
		ifsf.setFaceCount(polygonVertices.size());
		ifsf.setFaceIndices(faceIndices);
		ifsf.setFaceColors(polygonColors.toArray(new Color[0]));
		ifsf.setLineCount(0);
		ifsf.setGenerateEdgesFromFaces(false);
		ifsf.setGenerateFaceNormals(true);
		
		ifsf.update();
	}

}