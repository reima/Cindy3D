package de.jreality.jogl.shader;

import static de.jreality.shader.CommonAttributes.DIFFUSE_COLOR;
import static de.jreality.shader.CommonAttributes.POINT_DIFFUSE_COLOR_DEFAULT;
import static de.jreality.shader.CommonAttributes.POINT_RADIUS;
import static de.jreality.shader.CommonAttributes.POINT_RADIUS_DEFAULT;

import java.awt.Color;

import javax.media.opengl.GL;

import de.jreality.geometry.GeometryUtility;
import de.jreality.jogl.JOGLRenderer;
import de.jreality.jogl.JOGLRenderingState;
import de.jreality.math.Matrix;
import de.jreality.math.MatrixBuilder;
import de.jreality.math.Rn;
import de.jreality.scene.Geometry;
import de.jreality.scene.PointSet;
import de.jreality.scene.data.Attribute;
import de.jreality.scene.data.DataList;
import de.jreality.scene.data.DoubleArray;
import de.jreality.shader.EffectiveAppearance;
import de.jreality.shader.ShaderUtility;

public class CirclePointShader extends AbstractPrimitiveShader implements
		PointShader {
	de.jreality.shader.CirclePointShader templateShader;
	GlslShaderProgram program = null;
	Color diffuseColor;
	double[] diffuseColorAsDouble = new double[3];
	
	public CirclePointShader(de.jreality.shader.CirclePointShader orig)	{
		templateShader = orig;
	}

	public CirclePointShader() {
	}
	
	public void setFromEffectiveAppearance(EffectiveAppearance eap, String name) {
		super.setFromEffectiveAppearance(eap, name);
//		pointRadius = eap.getAttribute(ShaderUtility.nameSpace(name,POINT_RADIUS),POINT_RADIUS_DEFAULT);
		diffuseColor = (Color) eap.getAttribute(ShaderUtility.nameSpace(name,DIFFUSE_COLOR), POINT_DIFFUSE_COLOR_DEFAULT);
		float[] diffuseColorAsFloat = diffuseColor.getRGBComponents(null);
		for (int i = 0; i < 3; ++i) {
			diffuseColorAsDouble[i] = diffuseColorAsFloat[i];
		}
	}
	
	public boolean providesProxyGeometry() {
		//return true;
		return false;
	}
	
	public void render(JOGLRenderingState jrs) {
		JOGLRenderer jr = jrs.renderer;
		GL gl = jr.globalGL;
		
		if (program == null) {
			try {
				program = new GlslShaderProgram(gl,
						getClass().getResourceAsStream("/de/tum/in/jrealityplugin/resources/shader/circle.vert"),
						getClass().getResourceAsStream("/de/tum/in/jrealityplugin/resources/shader/circle.frag")
				);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}
		
		Geometry original = jrs.currentGeometry;
		
		PointSet ps = (PointSet) original;
		DataList centers = ps.getVertexAttributes(Attribute.COORDINATES);
		if (centers == null)
			return;
		
		DataList normals = ps.getVertexAttributes(Attribute.NORMALS);
		if (normals == null)
			return;
		
		DataList radiiList = ps.getVertexAttributes(Attribute.RELATIVE_RADII);
		if (radiiList == null)
			return;
		DoubleArray radii = radiiList.toDoubleArray();
		
		DataList colors = ps.getVertexAttributes(Attribute.COLORS);
		
//		float[] diffuseColorAsFloat = diffuseColor.getRGBColorComponents(null);
//		gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, diffuseColorAsFloat, 0);
		
		DoubleArray colorArray = new DoubleArray(diffuseColorAsDouble);
		
		for (int i = 0; i < centers.size(); ++i) {
			DoubleArray centerArray = centers.item(i).toDoubleArray();
			DoubleArray normalArray = normals.item(i).toDoubleArray();
			if (colors != null) colorArray = colors.item(i).toDoubleArray();
			double radius = radii.getValueAt(i);
			program.bind(gl);
			gl.glUniform3f(program.getUniformLocation(gl, "circleCenter"),
					(float)centerArray.getValueAt(0), (float)centerArray.getValueAt(1), (float)centerArray.getValueAt(2));
			gl.glUniform3f(program.getUniformLocation(gl, "circleNormal"),
					(float)normalArray.getValueAt(0), (float)normalArray.getValueAt(1), (float)normalArray.getValueAt(2));
			gl.glUniform1f(program.getUniformLocation(gl, "circleRadiusSq"), (float)(radius*radius));
			gl.glUniform3f(program.getUniformLocation(gl, "circleColor"),
					(float)colorArray.getValueAt(0), (float)colorArray.getValueAt(1), (float)colorArray.getValueAt(2));
			
			Matrix transform = new Matrix();
			MatrixBuilder.euclidean().translate(centerArray.toDoubleArray(null))
				.rotateFromTo(new double[]{0,0,1}, normalArray.toDoubleArray(null))
				.scale(new double[] {radius, radius, 1}).assignTo(transform);
			
			gl.glUniformMatrix4fv(program.getUniformLocation(gl, "circleTransform"),
					1, true, Rn.convertDoubleToFloatArray(transform.getArray()), 0);
			
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
