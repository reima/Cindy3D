package de.tum.in.cindy3dplugin.jogl.lighting;

import javax.media.opengl.GL2;

import org.apache.commons.math.geometry.Vector3D;

public class LightManager {
	
	public static final int MAX_LIGHTS = 8;
	
	public enum LightType {
		POINT_LIGHT,
		DIRECTIONAL_LIGHT,
		SPOT_LIGHT,
	}
	
	boolean compileShader = false;
	
	Light[] lights = new Light[MAX_LIGHTS];
	
	public LightManager() {
		LightInfo info = new LightInfo();
		info.position = new Vector3D(0,0,0);
		info.type = LightType.POINT_LIGHT;
		setLight(0, info);
	}
	
	public void setLight(int light, LightInfo info) {
		
		if (lights[light] == null || lights[light].getType() != info.type) {
			compileShader = true;
			
			switch (info.type)
			{
				case POINT_LIGHT:
					lights[light] = new PointLight();					
					break;
				case DIRECTIONAL_LIGHT:
					lights[light] = new DirectionalLight();
					break;
				case SPOT_LIGHT:
					lights[light] = new SpotLight();
					break;
			}
		}
		
		if (!lights[light].isEnabled()) {
			lights[light].setEnabled(true);
			compileShader = true;
		}
		
		if (info.ambient != null) {
			lights[light].setAmbientColor(info.ambient);
		}
		if (info.diffuse != null) {
			lights[light].setDiffuseColor(info.diffuse);
		}
		if (info.specular != null) {
			lights[light].setSpecularColor(info.specular);
		}

		switch (info.type) {
			case POINT_LIGHT:
				PointLight pLight = (PointLight)lights[light];
				
				if (info.position != null) {
					pLight.setPosition(info.position);
				}
				break;
			case DIRECTIONAL_LIGHT:
				DirectionalLight dLight = (DirectionalLight)lights[light];
				if (info.direction != null) {
					dLight.setDirection(info.direction);
				}
				break;
			case SPOT_LIGHT:
				break;
		}
	}
	
	public String getShaderFillIn() {
		String str = "vec3 eye = -normalize(ecPoint);\n";
		for (int i=0; i<MAX_LIGHTS; ++i) {
			if (lights[i] != null && lights[i].isEnabled()) {
				str += lights[i].getShaderFillIn(i) + "\n";
			}
		}
		return str;
	}
	
	public boolean getCompileShader() {
		return compileShader;
	}

	public void setCompileShader(boolean compileShader) {
		this.compileShader = compileShader;
	}
	
	public void setGLState(GL2 gl) {
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		
		// Viewer assumed to be at (0,0,0) in eye coordinates
		gl.glLightModeli(GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, 1);
		
		gl.glLoadIdentity();
		for (int i=0; i<MAX_LIGHTS; ++i) {
			if (lights[i] != null) {
				lights[i].setGLState(gl, GL2.GL_LIGHT0 + i);
			}
		}
		gl.glPopMatrix();
	}

	public void disableLight(int light) {
		if (!lights[light].isEnabled()) {
			return;
		}
		
		lights[light].setEnabled(false);
		compileShader = true;
	}

}
