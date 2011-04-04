package de.tum.in.reitinge.test;

import java.awt.Color;

import de.jreality.geometry.IndexedLineSetFactory;
import de.jreality.geometry.PointSetFactory;
import de.jreality.geometry.QuadMeshFactory;
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.content.ContentLoader;
import de.jreality.plugin.content.ContentTools;
import de.jreality.plugin.content.DirectContent;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.data.Attribute;
import de.jreality.scene.data.IntArray;
import de.jreality.scene.data.IntArrayArray;
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
		IndexedLineSetFactory ilsf = new IndexedLineSetFactory();
		ilsf.setVertexCount(2*2);
		ilsf.setVertexCoordinates(new double[] {
				-2, -2, 0,
				 2, -2, 0,
				 2,  2, 0,
				-2,  2, 0
		});
		//psf.setVertexRelativeRadii(new double[] {
		//		1, 2, 4, 8
		//});
		ilsf.setVertexColors(new Color[] {
				Color.red, Color.green, Color.blue, Color.yellow
		});
		ilsf.setEdgeCount(4);
		ilsf.setEdgeIndices(new int[]{0,1,1,2,2,3,3,0});
		ilsf.setEdgeAttribute(Attribute.attributeForName("lineType"),new IntArray(new int[]{0,1,2,0}));
		ilsf.update();
		quad.setGeometry(ilsf.getGeometry());
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
		
		SceneGraphComponent circles = SceneGraphUtility.createFullSceneGraphComponent("circles");
		PointSetFactory psf = new PointSetFactory();
		psf.setVertexCount(2);
		psf.setVertexCoordinates(new double[] { 0, 0, 0, 0, 2, 0 });
		psf.setVertexNormals(new double[] { 0, 1, 0, 0, 0, 1 });
		psf.setVertexRelativeRadii(new double[] { 1, 0.5 });
		psf.setVertexColors(new Color[] { Color.red, Color.blue });
		psf.update();
		circles.setGeometry(psf.getGeometry());
		dgs = ShaderUtility.createDefaultGeometryShader(circles.getAppearance(), true);
		dgs.setShowLines(false);
		dgs.setShowPoints(true);
		dgs.setShowFaces(false);
		dgs.createPointShader("circle");
		cmp.addChild(circles);
		
		
		double[][][] vertices = new double[][][] {{{0,0,0},{10,0,0}},{{0,0,10},{10,0,10}}};
		
		SceneGraphComponent mesh = SceneGraphUtility.createFullSceneGraphComponent("mesh");
		QuadMeshFactory qmf = new QuadMeshFactory();

		qmf.setVLineCount(2);
		qmf.setULineCount(2);
		
		qmf.setClosedInUDirection(false);
		qmf.setClosedInVDirection(false);
		qmf.setVertexCoordinates(vertices);
		qmf.setGenerateFaceNormals(true);
		qmf.setGenerateTextureCoordinates(false);
		qmf.setGenerateEdgesFromFaces(true);
		qmf.setEdgeFromQuadMesh(true);

		qmf.update();
		
		mesh.setGeometry(qmf.getGeometry());
		
		dgs = ShaderUtility.createDefaultGeometryShader(mesh.getAppearance(), true);
		dgs.setShowLines(true);
		dgs.setShowPoints(true);
		dgs.setShowFaces(true);
		dgs.createPointShader("my");
		dgs.createLineShader("my");
		
		cmp.addChild(mesh);

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