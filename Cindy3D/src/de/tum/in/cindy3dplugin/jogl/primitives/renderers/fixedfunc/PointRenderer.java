package de.tum.in.cindy3dplugin.jogl.primitives.renderers.fixedfunc;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import de.tum.in.cindy3dplugin.jogl.Util;
import de.tum.in.cindy3dplugin.jogl.primitives.Point;
import de.tum.in.cindy3dplugin.jogl.primitives.renderers.PrimitiveRenderer;
import de.tum.in.cindy3dplugin.jogl.renderers.JOGLRenderState;
import de.tum.in.cindy3dplugin.jogl.renderers.JOGLRenderState.CullMode;

public class PointRenderer extends PrimitiveRenderer<Point> {
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
			int stacks = lod + 2;
			int slices = 2 * stacks;
			
			vertexCount[lod] = 2 + slices * (stacks - 1);
			faceCount[lod] = 2 * slices + // stacks at poles, triangles
					(stacks - 2) * slices * 2; // inner stacks loops, quads (2
											   // triangles)
			DoubleBuffer vertices = DoubleBuffer.allocate(vertexCount[lod] * 3);
			IntBuffer indices = IntBuffer.allocate(faceCount[lod] * 3);
			
			/*
			 * Generate vertices
			 */
			double[] vertex = new double[3];
			
			vertex[0] = vertex[1] = 0;
			vertex[2] = -1;
			vertices.put(vertex);
			
			for (int stack = 1; stack < stacks; ++stack) {
				double latitude = (((double) stack) / stacks - 0.5) * Math.PI;
				for (int slice = 0; slice < slices; ++slice) {
					double longitude = ((double) slice) / slices * Math.PI * 2;
					vertex[0] = Math.cos(longitude)*Math.cos(latitude);
					vertex[1] = Math.sin(longitude)*Math.cos(latitude);
					vertex[2] = Math.sin(latitude);
					vertices.put(vertex);
				}
			}
			
			vertex[0] = vertex[1] = 0;
			vertex[2] = 1;
			vertices.put(vertex);
			
			vertices.flip();
			gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, vertexBuffer[lod]);
			gl2.glBufferData(GL2.GL_ARRAY_BUFFER, vertices.capacity()
					* Util.SIZEOF_DOUBLE, vertices, GL2.GL_STATIC_DRAW);

			/*
			 * Generate indices
			 */
			int stackOffset = 0;		// First vertex of current stack
			int nextStackOffset = 1;	// First vertex of next stack
			
			// South pole
			for (int slice = 0; slice < slices; ++slice) {
				indices.put(stackOffset);
				indices.put(nextStackOffset + (slice + 1) % slices);
				indices.put(nextStackOffset + slice);
			}
			
			for (int stack = 1; stack < stacks - 1; ++stack) {
				stackOffset = nextStackOffset;
				nextStackOffset += slices;
				for (int slice = 0; slice < slices; ++slice) {
					indices.put(stackOffset     + slice);
					indices.put(nextStackOffset + (slice + 1) % slices);
					indices.put(nextStackOffset + slice);
					
					indices.put(stackOffset     + slice);
					indices.put(stackOffset     + (slice + 1) % slices);
					indices.put(nextStackOffset + (slice + 1) % slices);
				}
			}
			
			// North pole
			for (int slice = 0; slice < slices; ++slice) {
				indices.put(nextStackOffset + slices);
				indices.put(nextStackOffset + slice);
				indices.put(nextStackOffset + (slice + 1) % slices);
			}

			indices.flip();
			gl2.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, indexBuffer[lod]);
			gl2.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, indices.capacity()
					* Util.SIZEOF_INT, indices, GL2.GL_STATIC_DRAW);
			
			faceCount[lod] = indices.limit() / 3;
			
			/*
			 * Calculate longest edge
			 */
			maxEdgeLength[lod] = 0;
			// TODO: Could perhaps be optimized (longest diagonal)
			for (int i = 0; i < indices.limit(); i += 3) {
				int i0 = indices.get(i);
				int i1 = indices.get(i + 1);
				int i2 = indices.get(i + 2);
				double[] v0 = new double[3];
				vertices.position(i0*3);
				vertices.get(v0, 0, 3);
				double[] v1 = new double[3];
				vertices.position(i1*3);
				vertices.get(v1, 0, 3);
				double[] v2 = new double[3];
				vertices.position(i2*3);
				vertices.get(v2, 0, 3);
				
				maxEdgeLength[lod] = Math.max(
						maxEdgeLength[lod],
						Math.sqrt(Math.pow(v0[0] - v1[0], 2)
								+ Math.pow(v0[1] - v1[1], 2)
								+ Math.pow(v0[2] - v1[2], 2)));
				maxEdgeLength[lod] = Math.max(
						maxEdgeLength[lod],
						Math.sqrt(Math.pow(v1[0] - v2[0], 2)
								+ Math.pow(v1[1] - v2[1], 2)
								+ Math.pow(v1[2] - v2[2], 2)));
				maxEdgeLength[lod] = Math.max(
						maxEdgeLength[lod],
						Math.sqrt(Math.pow(v2[0] - v0[0], 2)
								+ Math.pow(v2[1] - v0[1], 2)
								+ Math.pow(v2[2] - v0[2], 2)));
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
		gl2.glEnable(GL2.GL_NORMALIZE);
		gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl2.glEnableClientState(GL2.GL_NORMAL_ARRAY);
		if (jrs.cullMode == CullMode.CULL_FRONT) {
			gl2.glEnable(GL2.GL_CULL_FACE);
			gl2.glCullFace(GL2.GL_FRONT);
		} else if (jrs.cullMode == CullMode.CULL_BACK) {
			gl2.glEnable(GL2.GL_CULL_FACE);
			gl2.glCullFace(GL2.GL_BACK);
		}
	}

	@Override
	public void postRender(JOGLRenderState jrs) {
		GL2 gl2 = jrs.gl.getGL2();
		gl2.glDisable(GL2.GL_NORMALIZE);
		gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		gl2.glDisableClientState(GL2.GL_NORMAL_ARRAY);
	}

	@Override
	protected void render(JOGLRenderState jrs, Point point) {
		GL2 gl = jrs.gl.getGL2();

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glTranslated(point.x, point.y, point.z);
		gl.glScaled(point.size, point.size, point.size);
		
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vertexBuffer[4]);
		gl.glVertexPointer(3, GL2.GL_DOUBLE, 3 * Util.SIZEOF_DOUBLE, 0);
		gl.glNormalPointer(GL2.GL_DOUBLE, 3 * Util.SIZEOF_DOUBLE, 0);
		gl.glColorPointer(3, GL2.GL_DOUBLE, 3 * Util.SIZEOF_DOUBLE, 0);
		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, indexBuffer[4]);
		gl.glDrawElements(GL2.GL_TRIANGLES, faceCount[4] * 3,
				GL2.GL_UNSIGNED_INT, 0);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		gl.glPopMatrix();
	}
}
