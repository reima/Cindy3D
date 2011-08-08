package de.tum.in.cindy3dplugin.jogl.lighting;

import javax.media.opengl.GL2;

import de.tum.in.cindy3dplugin.Cindy3DViewer;
import de.tum.in.cindy3dplugin.LightModificationInfo;
import de.tum.in.cindy3dplugin.LightModificationInfo.LightType;
import de.tum.in.cindy3dplugin.jogl.Util;

public class LightManager {
	boolean compileShader = false;

	Light[] lights = new Light[Cindy3DViewer.MAX_LIGHTS];

	public LightManager() {
		LightModificationInfo info = new LightModificationInfo(
				LightType.POINT_LIGHT);
		info.setPosition(new double[] { 0, 0, 0 });
		setLight(0, info);
	}

	public void setLight(int light, LightModificationInfo info) {
		if (lights[light] == null || lights[light].getType() != info.getType()) {
			compileShader = true;

			switch (info.getType()) {
			case POINT_LIGHT:
				lights[light] = new PointLight();
				break;
			case DIRECTIONAL_LIGHT:
				lights[light] = new DirectionalLight();
				break;
			case SPOT_LIGHT:
				lights[light] = new SpotLight();
				break;
			default:
				return;
			}
		}

		if (!lights[light].isEnabled()) {
			lights[light].setEnabled(true);
			compileShader = true;
		}

		if (info.hasAmbient()) {
			lights[light].setAmbientColor(info.getAmbient());
		}
		if (info.hasDiffuse()) {
			lights[light].setDiffuseColor(info.getDiffuse());
		}
		if (info.hasSpecular()) {
			lights[light].setSpecularColor(info.getSpecular());
		}
		if (info.hasFrame()) {
			lights[light].setLightFrame(info.getFrame());
		}

		switch (info.getType()) {
		case POINT_LIGHT:
			PointLight pointLight = (PointLight) lights[light];
			if (info.hasPosition()) {
				pointLight.setPosition(Util.toVector(info.getPosition()));
			}
			break;
		case DIRECTIONAL_LIGHT:
			DirectionalLight directionalLight = (DirectionalLight) lights[light];
			if (info.hasDirection()) {
				directionalLight
						.setDirection(Util.toVector(info.getDirection()));
			}
			break;
		case SPOT_LIGHT:
			break;
		}
	}

	public String getShaderFillIn() {
		String str = "vec3 eye = -normalize(ecPoint);\n";
		for (int i = 0; i < Cindy3DViewer.MAX_LIGHTS; ++i) {
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
		// Viewer assumed to be at (0,0,0) in eye coordinates
		gl.glLightModeli(GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, 1);
		
		for (int i = 0; i < Cindy3DViewer.MAX_LIGHTS; ++i) {
			if (lights[i] != null) {
				lights[i].setGLState(gl, GL2.GL_LIGHT0 + i);
			}
		}
	}

	public void disableLight(int light) {
		if (!lights[light].isEnabled()) {
			return;
		}
		
		lights[light].setEnabled(false);
		compileShader = true;
	}

}
