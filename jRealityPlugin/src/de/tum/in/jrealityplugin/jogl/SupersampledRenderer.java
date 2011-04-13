package de.tum.in.jrealityplugin.jogl;

import javax.media.opengl.DebugGL2;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLPbuffer;

import com.jogamp.opengl.util.texture.Texture;

public class SupersampledRenderer extends JOGLRenderer {
	private GLPbuffer pbuffer = null;
	private Texture pbufferTexture = null;
	private int width;
	private int height;
	private int superSampleFactor = 2;
	
	private class PBufferRenderer extends DefaultRenderer {
		public PBufferRenderer(Scene scene, ModelViewerCamera camera) {
			super(scene, camera);
		}
		
		@Override
		public void init(GLAutoDrawable drawable) {
			super.init(drawable);
			GL2 gl = drawable.getGL().getGL2();
			pbufferTexture.bind();
			gl.glCopyTexImage2D(pbufferTexture.getTarget(), 0, GL2.GL_RGBA,
					0, 0, drawable.getWidth(), drawable.getHeight(), 0);
			// TODO: Find out why reshape doesn't get called automatically
			reshape(drawable, 0, 0, drawable.getWidth(), drawable.getHeight());
		}
		
		@Override
		public void display(GLAutoDrawable drawable) {			
			super.display(drawable);
			GL2 gl = drawable.getGL().getGL2();
			pbufferTexture.bind();
			gl.glCopyTexSubImage2D(pbufferTexture.getTarget(), 0, 0, 0, 0, 0,
					drawable.getWidth(), drawable.getHeight());
			gl.glGenerateMipmap(pbufferTexture.getTarget());
		}
	}
	
	public SupersampledRenderer(Scene scene, ModelViewerCamera camera) {
		super(scene, camera);
		superSampleFactor = 4;
		width = 1;
		height = 1;
	}

	public int getSuperSampleFactor() {
		return superSampleFactor;
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		drawable.setGL(new DebugGL2(drawable.getGL().getGL2()));
		GL2 gl = drawable.getGL().getGL2();
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		resizePBuffer(drawable);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		this.width = width;
		this.height = height;
		
		if (pbufferTexture == null) {
			pbufferTexture = new Texture(GL2.GL_TEXTURE_2D);
			pbufferTexture.setTexParameteri(GL2.GL_TEXTURE_MIN_FILTER,
					GL2.GL_LINEAR_MIPMAP_LINEAR);
			pbufferTexture.setTexParameteri(GL2.GL_TEXTURE_MAG_FILTER,
					GL2.GL_LINEAR);
		}
		
		// TODO: Fix exception at resize
		resizePBuffer(drawable);
		
		display(drawable);
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		pbuffer.display();
		
		GL2 gl = drawable.getGL().getGL2();

		pbufferTexture.enable();
		pbufferTexture.bind();
        
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2d(0, 0); gl.glVertex3d(-1, -1, 0);
		gl.glTexCoord2d(1, 0); gl.glVertex3d( 1, -1, 0);
		gl.glTexCoord2d(1, 1); gl.glVertex3d( 1,  1, 0);
		gl.glTexCoord2d(0, 1); gl.glVertex3d(-1,  1, 0);
		gl.glEnd();
		
		pbufferTexture.disable();
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		pbufferTexture.destroy(drawable.getGL());
		pbuffer.destroy();
	}

	private void resizePBuffer(GLAutoDrawable drawable) {
		// TODO: Avoid reallocation of all resources at resize
		// (move resources to main context)
		if (pbuffer != null) {
			pbuffer.destroy();
			pbuffer = null;
		}
		GLCapabilities caps = new GLCapabilities(null);
		caps.setDoubleBuffered(false);
		caps.setPBuffer(true);

		pbuffer = GLDrawableFactory.getFactory(drawable.getGLProfile())
				.createGLPbuffer(null, caps, null, width * superSampleFactor,
						height * superSampleFactor, drawable.getContext());
		pbuffer.addGLEventListener(new PBufferRenderer(scene, camera));
	}
}
