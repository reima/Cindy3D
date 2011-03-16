package de.tum.in.reitinge.test;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import de.jreality.geometry.FrameFieldType;
import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.geometry.IndexedLineSetFactory;
import de.jreality.geometry.Primitives;
import de.jreality.geometry.TubeUtility;
import de.jreality.math.Matrix;
import de.jreality.math.MatrixBuilder;
import de.jreality.math.Rn;
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.content.ContentTools;
import de.jreality.scene.Appearance;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultLineShader;
import de.jreality.shader.GlslProgram;
import de.jreality.shader.LineShader;
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
		
		double p1[] = new double[] {1.5, 0, 1.5};
		double p2[] = new double[] {2.5, 0, 2.5};
		float radius = 0.025f;
		
		SceneGraphComponent quad = SceneGraphUtility.createFullSceneGraphComponent("quad");
		quad.setGeometry(Primitives.texturedQuadrilateral(new double[] {
				-1, -1, 0,
				 1, -1, 0,
				 1,  1, 0,
				-1,  1, 0
		}));
		
		Appearance app = quad.getAppearance();
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
		sphereProg.setUniform("sphereCenter", p1);
		sphereProg.setUniform("sphereRadius", radius);
		
		cmp.addChild(quad);
		
		/////
		
		SceneGraphComponent quad2 = SceneGraphUtility.createFullSceneGraphComponent("quad2");
		quad2.setGeometry(Primitives.texturedQuadrilateral(new double[] {
				-1, -1, 0,
				 1, -1, 0,
				 1,  1, 0,
				-1,  1, 0
		}));
		
		Appearance app2 = quad2.getAppearance();
		DefaultGeometryShader dgs2 =
			ShaderUtility.createDefaultGeometryShader(app2, true);
		dgs2.setShowLines(false);
		dgs2.setShowPoints(false);
		dgs2.createPolygonShader("glsl");
		GlslProgram sphereProg2 = null;
		try {
			sphereProg2 = new GlslProgram(app2, "polygonShader",   
					Input.getInput(new File("./shader/sphere.vert")),
					Input.getInput(new File("./shader/sphere.frag"))
			    );
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (sphereProg2 == null) {
			System.exit(-1);
		}
		sphereProg2.setUniform("sphereCenter", p2);
		sphereProg2.setUniform("sphereRadius", radius);
		
		cmp.addChild(quad2);
		
		SceneGraphComponent box = SceneGraphUtility.createFullSceneGraphComponent("box");		
		IndexedFaceSetFactory ifsf = new IndexedFaceSetFactory();
		
		double[][] vertices = new double[][] {
				{-1,-1,-1},
				{-1,-1, 1},
				{-1, 1,-1},
				{-1, 1, 1},
				{ 1,-1,-1},
				{ 1,-1, 1},
				{ 1, 1,-1},
				{ 1, 1, 1},
		};
		
		float dist = Math.max((float)Rn.euclideanDistance(p1, p2), 2*radius)/2.0f;
		
		double[] p = new double[]{0,0,0};
		double[] avg = new double[3];
		Rn.average(avg, new double[][]{p1,p2});
		Matrix m = new Matrix();
		MatrixBuilder.euclidean().translate(p, avg).rotateFromTo(new double[]{-dist/2.0f,0,0}, p1).
		scale(new double[]{dist, radius, radius})
								 .assignTo(m);
		
		double[][] transVertices = new double[8][3];
		for (int i=0; i<8; ++i)
			Rn.matrixTimesVector(transVertices[i], m.getArray(), vertices[i]);
		int[][] faceIndices = new int[][] {
				{0, 1, 3, 2},
				{5, 4, 6, 7},
				{5, 1, 0, 4},
				{3, 7, 6, 2},
				{7, 3, 1, 5},
				{0, 2, 6, 4},
		};
		
		ifsf.setVertexCount(transVertices.length);
		ifsf.setVertexCoordinates(transVertices);
		ifsf.setFaceCount(faceIndices.length);
		ifsf.setFaceIndices(faceIndices);
		ifsf.setGenerateEdgesFromFaces(false);
		ifsf.setGenerateFaceNormals(true);
		ifsf.update();

		box.setGeometry(ifsf.getGeometry());
		
		Appearance appBox = box.getAppearance();
		
		DefaultGeometryShader dgsBox =
			ShaderUtility.createDefaultGeometryShader(appBox, true);
		dgsBox.setShowLines(false);
		dgsBox.setShowPoints(false);
		dgsBox.createPolygonShader("glsl");
		GlslProgram cylinderProg = null;
		try {
			cylinderProg = new GlslProgram(appBox, "polygonShader",
					Input.getInput(new File("./shader/cylinder.vert")),
					Input.getInput(new File("./shader/cylinder.frag"))
				);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (cylinderProg == null) {
			System.exit(-1);
		}
		
		cylinderProg.setUniform("cylinderPoint1", p1);
		cylinderProg.setUniform("cylinderPoint2", p2);
		cylinderProg.setUniform("cylinderRadius", radius);
		
		cmp.addChild(box);
		
		SceneGraphComponent lines = SceneGraphUtility.createFullSceneGraphComponent("lines");
		IndexedLineSetFactory ilsf = new IndexedLineSetFactory();
		ilsf.setVertexCount(2);
		ilsf.setVertexCoordinates(new double[] {-2,-2,0, 2,2,0});
		ilsf.setEdgeCount(1);
		ilsf.setEdgeIndices(new int[] {0,1});
		ilsf.setEdgeRelativeRadii(new double[] {10});
		ilsf.update();
		lines.setGeometry(ilsf.getGeometry());
		
		DefaultGeometryShader dgs3 = ShaderUtility.createDefaultGeometryShader(lines.getAppearance(), true);
		DefaultLineShader ls = (DefaultLineShader) dgs3.createLineShader("default");
		ls.setCrossSection(TubeUtility.diamondCrossSection);
		
		//cmp.addChild(lines);
		
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