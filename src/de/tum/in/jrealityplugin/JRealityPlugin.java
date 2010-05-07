package de.tum.in.jrealityplugin;

import de.cinderella.api.cs.CindyScript;
import de.cinderella.api.cs.CindyScriptPlugin;

/**
 * Implementation of the plugin interface
 */
public class JRealityPlugin extends CindyScriptPlugin {
    private Cindy3DViewer cindy3d;
    
    public JRealityPlugin() {
        cindy3d = new JRealityViewer();
    }

    /* (non-Javadoc)
     * @see de.cinderella.api.cs.CindyScriptPlugin#getAuthor()
     */
    @Override
    public String getAuthor() {
        return "Jan Sommer und Matthias Reitinger";
    }

    /* (non-Javadoc)
     * @see de.cinderella.api.cs.CindyScriptPlugin#getName()
     */
    @Override
    public String getName() {
        return "jReality for Cinderella";
    }

    /**
     * Squares the given number
     * @param x
     * @return The square of x
     */
    @CindyScript("square")
    public double square(double x) {
        return x*x;
    }
    
    /**
     * Prepares drawing of 3D objects.
     * Must be called before any 3D drawing function.
     * TODO: List these functions
     */
    @CindyScript("begin3d")
    public void begin3d() {
        cindy3d.begin3d();        
    }
        
    /**
     * Finalizes the drawing of 3D objects.
     * Displays all objects drawn since the last call to <code>begin3d</code>. 
     */
    @CindyScript("end3d")
    public void end3d() {
        cindy3d.end3d();
    }
    
}
