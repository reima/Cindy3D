package de.tum.in.jrealityplugin;

import java.awt.Color;
import java.util.ArrayList;

import de.jreality.geometry.IndexedLineSetFactory;
import de.jreality.geometry.PointSetFactory;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.ui.viewerapp.ViewerApp;

@SuppressWarnings("deprecation")
public class JRealityViewer implements Cindy3DViewer {
	private ViewerApp viewer;

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

		sceneRoot = new SceneGraphComponent("root");

		scenePoints = new SceneGraphComponent("points");
		scenePoints.setGeometry(psf.getGeometry());

		sceneLines = new SceneGraphComponent("lines");
		sceneLines.setGeometry(ilsf.getGeometry());

		sceneRoot.addChild(scenePoints);
		sceneRoot.addChild(sceneLines);

		viewer = new ViewerApp(sceneRoot);
		viewer.setAttachBeanShell(false);
		viewer.setAttachNavigator(true);
		viewer.setShowMenu(true);
		viewer.setBackgroundColor(Color.BLACK);
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
	}

	/* (non-Javadoc)
	 * @see de.tum.in.jrealityplugin.Cindy3DViewer#end()
	 */
	@Override
	public void end() {
		updatePoints();
		updateLines();

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
	protected void clearPoints() {
		pointCoordinates.clear();
		pointColors.clear();
		pointSizes.clear();
	}

	/**
	 * Transfers internal point data to jReality
	 */
	protected void updatePoints() {
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

}
