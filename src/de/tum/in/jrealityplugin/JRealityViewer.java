package de.tum.in.jrealityplugin;

import java.awt.Color;
import java.util.ArrayList;

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

	public JRealityViewer() {
		pointCoordinates = new ArrayList<double[]>();
		pointColors = new ArrayList<Color>();
		pointSizes = new ArrayList<Double>();

		psf = new PointSetFactory();

		sceneRoot = new SceneGraphComponent("root");

		scenePoints = new SceneGraphComponent("points");
		scenePoints.setGeometry(psf.getGeometry());

		sceneRoot.addChild(scenePoints);

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
	}

	/* (non-Javadoc)
	 * @see de.tum.in.jrealityplugin.Cindy3DViewer#end()
	 */
	@Override
	public void end() {
		updatePoints();

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
		for (int i=0; i<pointSizes.size(); ++i)
			sizesArray[i] = pointSizes.get(i);

		psf.setVertexRelativeRadii(sizesArray);
		psf.update();
	}

}
