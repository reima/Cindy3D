package de.tum.in.jrealityplugin;

import java.awt.Color;

import de.jreality.geometry.PointSetFactory;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.ui.viewerapp.ViewerApp;

@SuppressWarnings("deprecation")
public class JRealityViewer implements Cindy3DViewer {
    private PointSetFactory psf;
    private SceneGraphComponent sceneRoot;
    private ViewerApp viewer;
    
    public JRealityViewer() {
        psf = new PointSetFactory();

        sceneRoot = new SceneGraphComponent();
        sceneRoot.setGeometry(psf.getGeometry());

        viewer = new ViewerApp(sceneRoot);
        viewer.setAttachBeanShell(false);
        viewer.setAttachNavigator(true);
        viewer.setShowMenu(true);
        viewer.setBackgroundColor(Color.BLACK);
        viewer.update();

        viewer.getFrame().setSize(600, 600);
    }

    @Override
    public void begin() {
    }

    @Override
    public void end() {
        viewer.getFrame().setVisible(true);
    }

    @Override
    public void addPoint(double x, double y, double z) {
        psf.setVertexCount(1);
        psf.setVertexCoordinates(new double[] { x, y, z });
        psf.update();
    }

    @Override
    public void shutdown() {
        viewer.getFrame().setVisible(false);
    }

}
