package de.tum.in.cindy3dplugin.jogl.primitives.renderers.fixedfunc;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.apache.commons.math.geometry.Vector3D;
import org.apache.commons.math.linear.RealMatrix;

import de.tum.in.cindy3dplugin.jogl.Util;
import de.tum.in.cindy3dplugin.jogl.primitives.Line;
import de.tum.in.cindy3dplugin.jogl.primitives.renderers.LineRendererBase;
import de.tum.in.cindy3dplugin.jogl.renderers.JOGLRenderState;

public class LineRenderer extends LineRendererBase {
	private static final int LOD_COUNT = 8;
	
	private LODMesh[] meshes = new LODMesh[LOD_COUNT];
	
	private LODMesh createMesh(GL gl, int loops, int slices) {
		GL2 gl2 = gl.getGL2();
		
		
		int vertexCount = (loops+1) * slices;
		int faceCount = 2 * loops * slices;
		
		LODMesh mesh = new LODMesh(3, vertexCount, faceCount);
		
		/*
		 * Generate vertices
		 */
		double[] vertex = new double[3];
		
		for (int loop = 0; loop < loops+1; ++loop) {
			double zValue = 2.0 * ((double) loop) / loops;
			for (int slice = 0; slice < slices; ++slice) {
				double angle = ((double) slice) / slices * Math.PI * 2;
				vertex[0] = Math.cos(angle);
				vertex[1] = Math.sin(angle);
				vertex[2] = zValue;
				mesh.putVertex(vertex);
			}
		}
		
		/*
		 * Generate indices
		 */
		
		int loopOffset = 0;
		int nextLoopOffset = slices;
		
		for (int loop = 0;  loop < loops; ++loop) {
			for (int slice = 0; slice < slices; ++slice) {
//				mesh.putFace(loopOffset + slice, 
//							 loopOffset + (slice + 1) % slices,
//							 nextLoopOffset + slice);
				mesh.putFace(nextLoopOffset + slice,
						loopOffset + (slice + 1) % slices,
						loopOffset + slice);
				
//				mesh.putFace(nextLoopOffset + slice,
//							 loopOffset + (slice + 1) % slices,
//							 nextLoopOffset + (slice + 1) % slices);
				mesh.putFace(nextLoopOffset + (slice + 1) % slices,
						loopOffset + (slice + 1) % slices,
						nextLoopOffset + slice);
			}
			loopOffset = nextLoopOffset;
			nextLoopOffset += slices;
		}
		
		mesh.finish(gl2);
		
		return mesh;
	}
	
	@Override
	public boolean init(GL gl) {
		for (int lod = 0; lod < LOD_COUNT; ++lod) {
			int slices = 2 * lod + 1;
			int loops = 4 * slices;
			meshes[lod] = createMesh(gl, loops, slices);
		}

		return true;
	}

	@Override
	public void dispose(GL gl) {
		GL2 gl2 = gl.getGL2();
		for (LODMesh mesh : meshes) {
			mesh.dispose(gl2);
		}
	}

	@Override
	public void preRender(JOGLRenderState jrs) {
		GL2 gl2 = jrs.gl.getGL2();
		gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl2.glEnable(GL2.GL_NORMALIZE);
		gl2.glDisable(GL2.GL_CULL_FACE);
	}

	@Override
	public void postRender(JOGLRenderState jrs) {
		GL2 gl2 = jrs.gl.getGL2();
		gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		gl2.glDisable(GL2.GL_NORMALIZE);
	}

	@Override
	protected void render(JOGLRenderState jrs, Line line) {
		GL2 gl2 = jrs.gl.getGL2();
		// Get the model view matrix
		RealMatrix modelView = jrs.camera.getTransform();

		// All computations are made in camera space, so first transform the two
		// points of the line into camera space by multiplying with the
		// modelview matrix
		Vector3D p1 = Util.transformPoint(modelView, line.getFirstPoint());
		Vector3D p2 = Util.transformPoint(modelView, line.getSecondPoint());

		Endpoints endPoints = clipLineAtFrustum(jrs.camera, p1, p2,
				line.lineType);
		
		double distance = Math.min(Util.transformPoint(jrs.camera.getTransform(),
				endPoints.p1).getNorm()
				- line.radius,
				Util.transformPoint(jrs.camera.getTransform(),
						endPoints.p2).getNorm()
						- line.radius);
		
		double allowedWorldSpaceError = jrs.camera.getWorldSpaceError(
				jrs.renderHints.getAllowedScreenSpaceError(), distance);
		
		double obbLength = Vector3D.distance(endPoints.p1, endPoints.p2) / 2.0;
		
		LODMesh mesh = meshes[LOD_COUNT - 1];
		int lod;
		for (lod = 0; lod < LOD_COUNT; ++lod) {
			if (meshes[lod].isSufficient(obbLength,
					allowedWorldSpaceError)) {
				mesh = meshes[lod];
				break;
			}
		}

		// After shifting the end points of the ray/line to the maximal visible
		// positions, the size and orientation for the OBB is needed
		RealMatrix cylinder = buildOBBTransform(endPoints, line.radius);
		
		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glPushMatrix();
		gl2.glLoadMatrixf(Util.matrixToFloatArrayTransposed(cylinder), 0);
		// Rotate cylinder's main axis to x-axis
		gl2.glRotated(90.0, 0.0, 1.0, 0.0);
		// Center cylinder on origin
		gl2.glTranslated(0, 0, -1);
		
		mesh.render(gl2);
		
		//gl2.glCallList(displayList);
		gl2.glPopMatrix();
	}
}
