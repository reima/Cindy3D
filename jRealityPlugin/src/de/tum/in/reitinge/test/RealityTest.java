package de.tum.in.reitinge.test;

import java.awt.Color;

import de.jreality.geometry.IndexedLineSetFactory;
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.content.ContentLoader;
import de.jreality.plugin.content.ContentTools;
import de.jreality.plugin.content.DirectContent;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.MyLineShader;
import de.jreality.shader.MyPointShader;
import de.jreality.shader.ShaderUtility;
import de.jreality.tools.ClickWheelCameraZoomTool;
import de.jreality.util.CameraUtility;
import de.jreality.util.SceneGraphUtility;

public class RealityTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SceneGraphComponent cmp = SceneGraphUtility.createFullSceneGraphComponent("root");
		
//		try {
//			SceneGraphComponent content = Readers.read(Input.getInput("C:/Users/reima/Downloads/bunny.obj"));
//			DefaultGeometryShader dgs =
//				ShaderUtility.createDefaultGeometryShader(content.getAppearance(), true);
//			dgs.setShowLines(false);
//			dgs.setShowPoints(true);
//			dgs.setShowFaces(true);
//			dgs.createPointShader("my");
//			cmp.addChild(content);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		SceneGraphComponent quad = SceneGraphUtility.createFullSceneGraphComponent("quad");
		IndexedLineSetFactory psf = new IndexedLineSetFactory();
		psf.setVertexCount(2*2);
		psf.setVertexCoordinates(new double[] {
				-1, -1, 0,
				 1, -1, 0,
				 1,  1, 0,
				-1,  1, 0
		});
		//psf.setVertexRelativeRadii(new double[] {
		//		1, 2, 4, 8
		//});
		psf.setVertexColors(new Color[] {
				Color.red, Color.green, Color.blue, Color.yellow
		});
		psf.setEdgeCount(4);
		psf.setEdgeIndices(new int[]{0,1,1,2,2,3,3,0});
		psf.update();
		quad.setGeometry(psf.getGeometry());
		DefaultGeometryShader dgs =
			ShaderUtility.createDefaultGeometryShader(quad.getAppearance(), true);
		dgs.setShowLines(true);
		dgs.setShowPoints(true);
		dgs.setShowFaces(true);
		MyPointShader mps = (MyPointShader) dgs.createPointShader("my");
		mps.setPointRadius(0.05);
		MyLineShader ls = (MyLineShader)dgs.createLineShader("my");
		ls.setLineType(2);
		cmp.addChild(quad);

		JRViewer v = new JRViewer();
		v.setContent(cmp);
		v.registerPlugin(new DirectContent());
		v.registerPlugin(new ContentTools());
		v.registerPlugin(new ContentLoader());
		v.addBasicUI();
		v.setShowPanelSlots(false, true, false, false);
		v.startup();
		
		CameraUtility.getCamera(v.getViewer()).setFar(1000.0);
	}

}