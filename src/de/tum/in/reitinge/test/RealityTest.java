package de.tum.in.reitinge.test;

import java.io.File;
import java.io.IOException;

import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.geometry.IndexedLineSetFactory;
import de.jreality.geometry.Primitives;
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.content.ContentTools;
import de.jreality.scene.Appearance;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.GlslProgram;
import de.jreality.shader.RenderingHintsShader;
import de.jreality.shader.ShaderUtility;
import de.jreality.tools.ClickWheelCameraZoomTool;
import de.jreality.util.CameraUtility;
import de.jreality.util.Input;
import de.jreality.util.SceneGraphUtility;

public class RealityTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SceneGraphComponent cmp = SceneGraphUtility.createFullSceneGraphComponent("root");
		cmp.addTool(new ClickWheelCameraZoomTool());
		
		SceneGraphComponent box = SceneGraphUtility.createFullSceneGraphComponent("box");

		box.setGeometry(Primitives.box(1, 1, 1, false));
		
		Appearance app = box.getAppearance();
		DefaultGeometryShader dgs =
			ShaderUtility.createDefaultGeometryShader(app, true);
		dgs.setShowLines(false);
		dgs.setShowPoints(false);
		dgs.createPolygonShader("glsl");
		GlslProgram sphereProg = null;
		try {
			sphereProg = new GlslProgram(app, "polygonShader",   
					Input.getInput(new File("./shader/sphere.vert")),
					Input.getInput(new File("./shader/sphere.frag"))
			    );
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (sphereProg == null) {
			System.exit(-1);
		}
		sphereProg.setUniform("center", new double[]{0,0,0});
		sphereProg.setUniform("radius", .5);
		
		cmp.addChild(box);
		
		SceneGraphComponent lines = new SceneGraphComponent();
		IndexedLineSetFactory ilsf = new IndexedLineSetFactory();
		ilsf.setVertexCount(2);
		ilsf.setVertexCoordinates(new double[] {-2,-2,0, 2,2,0});
		ilsf.setEdgeCount(1);
		ilsf.setEdgeIndices(new int[] {0,1});
		ilsf.setEdgeRelativeRadii(new double[] {10});
		ilsf.update();
		lines.setGeometry(ilsf.getGeometry());
		
		cmp.addChild(lines);
		
		JRViewer v = new JRViewer();
		v.setContent(cmp);
		//v.registerPlugin(new DirectContent());
		v.registerPlugin(new ContentTools());
		//v.registerPlugin(new ContentLoader());
		v.addBasicUI();
		v.setShowPanelSlots(false, true, false, false);
		v.startup();
		
		CameraUtility.getCamera(v.getViewer()).setFar(1000.0);
	}

}