package de.jreality.jogl.shader;

import static de.jreality.shader.CommonAttributes.DIFFUSE_COLOR;

import java.awt.Color;

import javax.media.opengl.GL;

import de.jreality.jogl.JOGLRenderer;
import de.jreality.jogl.JOGLRenderingState;
import de.jreality.math.Matrix;
import de.jreality.math.MatrixBuilder;
import de.jreality.math.Pn;
import de.jreality.math.Rn;
import de.jreality.scene.Geometry;
import de.jreality.scene.IndexedLineSet;
import de.jreality.scene.data.Attribute;
import de.jreality.scene.data.DataList;
import de.jreality.shader.CommonAttributes;
import de.jreality.shader.DefaultLineShader;
import de.jreality.shader.EffectiveAppearance;
import de.jreality.shader.ShaderUtility;

public class MyLineShader extends AbstractPrimitiveShader implements LineShader {
	private DefaultLineShader templateShader;
	private double tubeRadius;
	private GlslShaderProgram program = null;
	private Color diffuseColor = java.awt.Color.BLUE;
	private double[] diffuseColorAsDouble = new double[3];
	
	public MyLineShader(de.jreality.shader.DefaultLineShader orig)	{
		templateShader = orig;
	}
	
	public MyLineShader()	{
	}
	
	public void setFromEffectiveAppearance(EffectiveAppearance eap, String name)	{
		super.setFromEffectiveAppearance(eap, name);
		tubeRadius = eap.getAttribute(ShaderUtility.nameSpace(name,CommonAttributes.TUBE_RADIUS),CommonAttributes.TUBE_RADIUS_DEFAULT);
		diffuseColor = (Color) eap.getAttribute(ShaderUtility.nameSpace(name,DIFFUSE_COLOR),CommonAttributes.LINE_DIFFUSE_COLOR_DEFAULT);
		float[] diffuseColorAsFloat = diffuseColor.getRGBComponents(null);
		for (int i = 0; i < 3; ++i) {
			diffuseColorAsDouble[i] = diffuseColorAsFloat[i];
		}
	}
	
	public boolean providesProxyGeometry() {
		//return true;
		return false;
	}
	
	private static double linePlaneIntersection(double[] origin,
			double[] direction,
			double[] plane) {
		double denom = Rn.innerProduct(direction, plane);
		if (Math.abs(denom) < 10E-8)
			return Double.MAX_VALUE;
		
		double lambda = -(Rn.innerProduct(origin, plane)+plane[3])/ denom;
		return lambda;
	}
	
	public void render(JOGLRenderingState jrs) {
		JOGLRenderer jr = jrs.renderer;
		GL gl = jr.globalGL;
		
		if (program == null) {
			try {
				program = new GlslShaderProgram(gl,
						getClass().getResourceAsStream("/de/tum/in/jrealityplugin/resources/shader/cylinder.vert"),
						getClass().getResourceAsStream("/de/tum/in/jrealityplugin/resources/shader/cylinder.frag")
				);				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}
		
		Geometry original = jrs.currentGeometry;
		
//		jrs.currentGeometry = null;
//		polygonShader.render(jrs);
//		jrs.currentGeometry = original;
		
		IndexedLineSet ls = (IndexedLineSet) original;
		DataList vertices = ls.getVertexAttributes(Attribute.COORDINATES);
		if (vertices == null)
			return;
		
		DataList indices = ls.getEdgeAttributes(Attribute.INDICES);
		if (indices == null)
			return;
		//DataList colors = ps.getVertexAttributes(Attribute.COLORS);
		
		//float[] diffuseColorAsFloat = diffuseColor.getRGBColorComponents(null);
		//gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, diffuseColorAsFloat, 0);
		
		//DoubleArray colorArray = new DoubleArray(diffuseColorAsDouble);
		
		//if (colors != null) {
		//	colorArray = colors.item(i).toDoubleArray();
		//}
		
		Matrix m = new Matrix(jrs.cameraToNDC);
		m.invert();
		
		double[][] f = new double[][] {		
				{-1, -1,  1, 1},
				{-1, -1, -1, 1},
				{-1,  1, -1, 1},
				{ 1,  1, -1, 1},		
				{ 1,  1,  1, 1},	
				{ 1, -1,  1, 1},
		};
		
		for (int i=0; i<6; ++i) {
			m.transformVector(f[i]);
			Pn.dehomogenize(f[i], f[i]);
			
			//System.out.println(f[i][0] + ", " + f[i][1] + ", " + f[i][2]);
		}
		

		
		double[][] planes = new double[6][4];
		
		for (int i=0; i<6; ++i) {
			Rn.crossProduct(planes[i], 
					Rn.subtract(null, f[(i+1)%6], f[i]),
					Rn.subtract(null, f[(i+2)%6], f[i]));
			planes[i][3] = 0;
			planes[i][3] = -Rn.innerProduct(planes[i], f[i]);
			
			//System.out.println(Rn.toString(planes[i]));
		}
		
		double[] mTmp = new double[16];
		jrs.currentPath.getMatrix(mTmp);
		Matrix modelToWorld = new Matrix(mTmp);
		
		Matrix worldToCamera = new Matrix(jrs.worldToCamera);
		modelToWorld.multiplyOnLeft(worldToCamera);
		
		boolean ray = true;

		for (int i = 0; i < indices.size(); ++i) {
			int[] ind = indices.item(i).toIntArray(null);
			
			for (int k = 1; k < ind.length; ++k) {
				
				program.bind(gl);
				double[] coord1 = vertices.item(ind[k-1]).toDoubleArray(null);
				double[] coord2 = vertices.item(ind[k]).toDoubleArray(null);

				modelToWorld.transformVector(coord1);
				modelToWorld.transformVector(coord2);
				
				double[] direction = new double[3];
				Rn.subtract(direction, coord2, coord1);
				double min = Double.MAX_VALUE;
				double max = Double.MIN_VALUE;
				for (int j=0; j<6; ++j) {
					double lambda = linePlaneIntersection(coord1, direction, planes[j]);
					if (lambda == Double.MAX_VALUE)
						continue;
					else {
						min = Math.min(min, lambda);
						max = Math.max(max, lambda);
					}
				}
				
				for (int j=0; j<3; ++j) {
					coord2[j] = coord1[j] + max*direction[j];
					if (!ray)
						coord1[j] = coord1[j] + min*direction[j];
				}
				
				double radius = 0.025f;
				double dist = Math.max(Rn.euclideanDistance(coord1, coord2), 2*radius)/2.0;
				
				double[] p = new double[]{0,0,0};
				double[] avg = new double[3];
				double[] axis = new double[3];
				Rn.average(avg, new double[][]{coord1,coord2});
				Rn.subtract(axis, coord1, coord2);
				Matrix cylinder = new Matrix();
				MatrixBuilder.euclidean().translate(p, avg).rotateFromTo(new double[]{1,0,0}, axis).
				scale(new double[]{dist, radius, radius})
										 .assignTo(cylinder);
								
				gl.glUniform3f(program.getUniformLocation(gl, "cylinderPoint1"),
						(float)coord1[0], (float)coord1[1], (float)coord1[2]);
				gl.glUniform3f(program.getUniformLocation(gl, "cylinderPoint2"),
						(float)coord2[0], (float)coord2[1], (float)coord2[2]);
				
//				gl.glUniform3f(program.getUniformLocation(gl, "sphereColor"),
//						(float)colorArray.getValueAt(0), (float)colorArray.getValueAt(1), (float)colorArray.getValueAt(2));

				gl.glUniform1f(program.getUniformLocation(gl, "cylinderRadius"), (float)radius);
				
				gl.glUniformMatrix4fv(program.getUniformLocation(gl, "cylinderTransform"),
						1, true, Rn.convertDoubleToFloatArray(cylinder.getArray()), 0);
				
				gl.glBegin(GL.GL_QUADS);
					gl.glVertex3d(-1, -1, -1);
					gl.glVertex3d(-1, -1,  1);
					gl.glVertex3d(-1,  1,  1);
					gl.glVertex3d(-1,  1, -1);
					
					gl.glVertex3d( 1, -1,  1);
					gl.glVertex3d( 1, -1, -1);
					gl.glVertex3d( 1,  1, -1);
					gl.glVertex3d( 1,  1,  1);
					
					gl.glVertex3d( 1, -1,  1);
					gl.glVertex3d(-1, -1,  1);
					gl.glVertex3d(-1, -1, -1);
					gl.glVertex3d( 1, -1, -1);
					
					gl.glVertex3d(-1,  1,  1);
					gl.glVertex3d( 1,  1,  1);
					gl.glVertex3d( 1,  1, -1);
					gl.glVertex3d(-1,  1, -1);
					
					gl.glVertex3d( 1,  1,  1);
					gl.glVertex3d(-1,  1,  1);
					gl.glVertex3d(-1, -1,  1);
					gl.glVertex3d( 1, -1,  1);
					
					gl.glVertex3d(-1, -1, -1);
					gl.glVertex3d(-1,  1, -1);
					gl.glVertex3d( 1,  1, -1);
					gl.glVertex3d( 1, -1, -1);
				gl.glEnd();

				program.unbind(gl);
			}
		}
	}


}
