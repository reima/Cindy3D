package de.tum.in.jrealityplugin;

import de.jreality.plugin.JRViewer;

public class JRealityViewer implements Cindy3DViewer {
    private JRViewer viewer;
    
    public JRealityViewer() {
        viewer = new JRViewer();
    }

    @Override
    public void begin3d() {
    }

    @Override
    public void end3d() {
        viewer.startup();
    }

}
