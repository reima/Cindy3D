package de.jreality.jogl.shader;

import static de.jreality.shader.CommonAttributes.DIFFUSE_COLOR;
import static de.jreality.shader.CommonAttributes.POINT_DIFFUSE_COLOR_DEFAULT;
import static de.jreality.shader.CommonAttributes.POINT_RADIUS;
import static de.jreality.shader.CommonAttributes.POINT_RADIUS_DEFAULT;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.media.opengl.GL;

import de.jreality.geometry.GeometryUtility;
import de.jreality.jogl.JOGLRenderer;
import de.jreality.jogl.JOGLRenderingState;
import de.jreality.scene.Geometry;
import de.jreality.scene.PointSet;
import de.jreality.scene.data.Attribute;
import de.jreality.scene.data.DataList;
import de.jreality.scene.data.DoubleArray;
import de.jreality.shader.EffectiveAppearance;
import de.jreality.shader.ShaderUtility;

public class MyPointShader extends AbstractPrimitiveShader implements
		PointShader {
	de.jreality.shader.MyPointShader templateShader;
	double pointRadius;
	Color diffuseColor = java.awt.Color.RED;
	PolygonShader polygonShader = null;
	GlslShaderProgram program = null;
	double[] diffuseColorAsDouble = new double[3];
	
	public MyPointShader(de.jreality.shader.MyPointShader orig)	{
		templateShader = orig;
	}

	public MyPointShader() {
	}

	public void setFromEffectiveAppearance(EffectiveAppearance eap, String name) {
		super.setFromEffectiveAppearance(eap, name);
		if (templateShader != null)  {
			polygonShader = DefaultGeometryShader.createFrom(templateShader.getPolygonShader());
			polygonShader.setFromEffectiveAppearance(eap, name+".polygonShader");
		}
		else polygonShader = (PolygonShader) ShaderLookup.getShaderAttr(eap, name, "polygonShader");
		pointRadius = eap.getAttribute(ShaderUtility.nameSpace(name,POINT_RADIUS),POINT_RADIUS_DEFAULT);
		diffuseColor = (Color) eap.getAttribute(ShaderUtility.nameSpace(name,DIFFUSE_COLOR), POINT_DIFFUSE_COLOR_DEFAULT);
		float[] diffuseColorAsFloat = diffuseColor.getRGBComponents(null);
		for (int i = 0; i < 3; ++i) {
			diffuseColorAsDouble[i] = diffuseColorAsFloat[i];
		}
	}

	public void postRender(JOGLRenderingState jrs) {
		//polygonShader.postRender(jrs);
	}
	
	public boolean providesProxyGeometry() {
		//return true;
		return false;
	}
	
	public int proxyGeometryFor(JOGLRenderingState jrs) {
//		JOGLRenderer jr = jrs.renderer;
//		GL gl = jr.globalGL;
//		Geometry original = jrs.currentGeometry;
//		PointSet ps = (PointSet) original;
//		DataList vertices = ps.getVertexAttributes(Attribute.COORDINATES);
//		if (vertices == null)	
//			return -1; //throw new IllegalStateException("No vertex coordinates for "+ps.getName());
//		DataList piDL = ps.getVertexAttributes(Attribute.INDICES);
//		IntArray vind = null;
//		if (piDL != null) vind = piDL.toIntArray();
//		int nextDL = -1;
//		if (jrs.useDisplayLists)	{
//			nextDL = gl.glGenLists(1);
//			gl.glNewList(nextDL, GL.GL_COMPILE);				
//		}
//				
//		if (jrs.useDisplayLists) gl.glEndList();
//		return nextDL;
		return -1;
	}
	
	public void render(JOGLRenderingState jrs) {
		JOGLRenderer jr = jrs.renderer;
		GL gl = jr.globalGL;
		
		if (program == null) {
			try {
				program = new GlslShaderProgram(gl, "./shader/sphere.vert", "./shader/sphere.frag");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Geometry original = jrs.currentGeometry;
		
//		jrs.currentGeometry = null;
//		polygonShader.render(jrs);
//		jrs.currentGeometry = original;
		
		PointSet ps = (PointSet) original;
		DataList vertices = ps.getVertexAttributes(Attribute.COORDINATES);
		int vertexLength = GeometryUtility.getVectorLength(vertices);
		DataList colors = ps.getVertexAttributes(Attribute.COLORS);
		
		float[] diffuseColorAsFloat = diffuseColor.getRGBColorComponents(null);
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, diffuseColorAsFloat, 0);
		
		DoubleArray colorArray = new DoubleArray(diffuseColorAsDouble);
		
		for (int i = 0; i < vertices.size(); ++i) {
			DoubleArray coordArray = vertices.item(i).toDoubleArray();
			if (colors != null) {
				colorArray = colors.item(i).toDoubleArray();
			}
			program.bind(gl);
			gl.glUniform3f(program.getUniformLocation(gl, "sphereCenter"),
					(float)coordArray.getValueAt(0), (float)coordArray.getValueAt(1), (float)coordArray.getValueAt(2));
			gl.glUniform3f(program.getUniformLocation(gl, "sphereColor"),
					(float)colorArray.getValueAt(0), (float)colorArray.getValueAt(1), (float)colorArray.getValueAt(2));
			gl.glUniform1f(program.getUniformLocation(gl, "sphereRadius"), (float)pointRadius);
			gl.glBegin(GL.GL_QUADS);
				gl.glVertex2d(-1, -1);
				gl.glVertex2d( 1, -1);
				gl.glVertex2d( 1,  1);
				gl.glVertex2d(-1,  1);
			gl.glEnd();
			program.unbind(gl);
		}
	}

	public Color getDiffuseColor() {
		// TODO Auto-generated method stub
		return null;
	}
}
