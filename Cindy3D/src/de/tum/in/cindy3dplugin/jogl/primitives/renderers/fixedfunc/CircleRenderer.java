package de.tum.in.cindy3dplugin.jogl.primitives.renderers.fixedfunc;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import de.tum.in.cindy3dplugin.jogl.Util;
import de.tum.in.cindy3dplugin.jogl.primitives.Circle;
import de.tum.in.cindy3dplugin.jogl.primitives.renderers.CircleRendererBase;
import de.tum.in.cindy3dplugin.jogl.renderers.JOGLRenderState;

public class CircleRenderer extends CircleRendererBase {
	private static final int LOD_COUNT = 8;
	
	private int[] vertexBuffer = new int[LOD_COUNT];
	private int[] indexBuffer = new int[LOD_COUNT];
	private int[] vertexCount = new int[LOD_COUNT];
	private int[] faceCount = new int[LOD_COUNT];
	private double[] maxEdgeLength = new double[LOD_COUNT];

	@Override
	public boolean loadShader(GL gl) {
		GL2 gl2 = gl.getGL2();
		
		gl2.glGenBuffers(LOD_COUNT, vertexBuffer, 0);
		gl2.glGenBuffers(LOD_COUNT, indexBuffer, 0);
		
		for (int lod = 0; lod < LOD_COUNT; ++lod) {
			int loops = lod + 1;
			int slices = 4 * loops;
			
			vertexCount[lod] = 1 + slices * loops;
			faceCount[lod] = slices + // inner loop, triangles
					(loops - 1) * slices * 2; // outer loops, quads (2
												// triangles)
			DoubleBuffer vertices = DoubleBuffer.allocate(vertexCount[lod] * 2);
			IntBuffer indices = IntBuffer.allocate(faceCount[lod] * 3);
			
			/*
			 * Generate vertices
			 */
			double[] vertex = new double[2];
			vertex[0] = vertex[1];
			vertices.put(vertex);
			
			for (int loop = 0; loop < loops; ++loop) {
				double radius = ((double) (loop + 1)) / loops;
				for (int slice = 0; slice < slices; ++slice) {
					double angle = ((double) slice) / slices * Math.PI * 2;
					vertex[0] = Math.cos(angle) * radius;
					vertex[1] = Math.sin(angle) * radius;
					vertices.put(vertex);
				}
			}
			
			vertices.flip();
			gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, vertexBuffer[lod]);
			gl2.glBufferData(GL2.GL_ARRAY_BUFFER, vertices.capacity()
					* Util.SIZEOF_DOUBLE, vertices, GL2.GL_STATIC_DRAW);

			/*
			 * Generate indices
			 */
			// Inner loop
			for (int slice = 0; slice < slices; ++slice) {
				indices.put(0);
				indices.put(1 + slice);
				indices.put(1 + (slice + 1) % slices);
			}
			// Outer loops
			int loopOffset = 1; // First vertex of current loop
			int nextLoopOffset = loopOffset + slices; // First vertex of next loop
			for (int loop = 1; loop < loops; ++loop) {
				for (int slice = 0; slice < slices; ++slice) {
					indices.put(loopOffset     + slice);
					indices.put(nextLoopOffset + slice);
					indices.put(nextLoopOffset + (slice + 1) % slices);
					
					indices.put(loopOffset     + slice);
					indices.put(nextLoopOffset + (slice + 1) % slices);
					indices.put(loopOffset     + (slice + 1) % slices);
				}
				loopOffset = nextLoopOffset;
				nextLoopOffset += slices;
			}
			
			indices.flip();
			gl2.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, indexBuffer[lod]);
			gl2.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, indices.capacity()
					* Util.SIZEOF_INT, indices, GL2.GL_STATIC_DRAW);
			
			/*
			 * Calculate longest edge
			 */
			maxEdgeLength[lod] = 0;
			// TODO: Could perhaps be optimized (longest diagonal)
			for (int i = 0; i < indices.limit(); i += 3) {
				int i0 = indices.get(i);
				int i1 = indices.get(i + 1);
				int i2 = indices.get(i + 2);
				double[] v0 = new double[2];
				vertices.position(i0*2);
				vertices.get(v0, 0, 2);
				double[] v1 = new double[2];
				vertices.position(i1*2);
				vertices.get(v1, 0, 2);
				double[] v2 = new double[2];
				vertices.position(i2*2);
				vertices.get(v2, 0, 2);
				
				maxEdgeLength[lod] = Math.max(
						maxEdgeLength[lod],
						Math.sqrt(Math.pow(v0[0] - v1[0], 2)
								+ Math.pow(v0[1] - v1[1], 2)));
				maxEdgeLength[lod] = Math.max(
						maxEdgeLength[lod],
						Math.sqrt(Math.pow(v1[0] - v2[0], 2)
								+ Math.pow(v1[1] - v2[1], 2)));
				maxEdgeLength[lod] = Math.max(
						maxEdgeLength[lod],
						Math.sqrt(Math.pow(v2[0] - v0[0], 2)
								+ Math.pow(v2[1] - v0[1], 2)));
			}
		}
		
		gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
		gl2.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);

		return true;
	}

	@Override
	public void dispose(GL gl) {
		GL2 gl2 = gl.getGL2();
		gl2.glDeleteBuffers(LOD_COUNT, vertexBuffer, 0);
		gl2.glDeleteBuffers(LOD_COUNT, indexBuffer, 0);
	}

	@Override
	public void preRender(JOGLRenderState jrs) {
		GL2 gl2 = jrs.gl.getGL2();
		gl2.glDisable(GL2.GL_CULL_FACE);
		gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		//gl2.glEnableClientState(GL2.GL_NORMAL_ARRAY);
	}

	@Override
	public void postRender(JOGLRenderState jrs) {
		GL2 gl2 = jrs.gl.getGL2();
		gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		//gl2.glDisableClientState(GL2.GL_NORMAL_ARRAY);
	}

	@Override
	protected void render(JOGLRenderState jrs, Circle circle) {
		GL2 gl = jrs.gl.getGL2();
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glMultTransposeMatrixf(buildTransform(circle), 0);
		
		gl.glNormal3d(0, 0, 1);
		
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vertexBuffer[3]);
		gl.glVertexPointer(2, GL2.GL_DOUBLE, 2 * Util.SIZEOF_DOUBLE, 0);
		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, indexBuffer[3]);
		gl.glDrawElements(GL2.GL_TRIANGLES, faceCount[3] * 3, GL2.GL_UNSIGNED_INT, 0);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		gl.glPopMatrix();
	}
}
