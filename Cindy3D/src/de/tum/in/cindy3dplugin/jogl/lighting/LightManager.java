package de.tum.in.cindy3dplugin.jogl.lighting;

import javax.media.opengl.GL2;

import de.tum.in.cindy3dplugin.Cindy3DViewer;
import de.tum.in.cindy3dplugin.LightModificationInfo;
import de.tum.in.cindy3dplugin.LightModificationInfo.LightType;
import de.tum.in.cindy3dplugin.jogl.Util;

/**
 * Manages all light sources.
 * 
 */
public class LightManager {
	/**
	 * Indicates if light source properties have changed. In this case, the
	 * shader code should be recompiled to update to current light setting.
	 */
	boolean lightSettingChanged = false;

	/**
	 * Stores all available light sources. The number of lights is limited to
	 * {@value de.tum.in.cindy3dplugin.Cindy3DViewer#MAX_LIGHTS}.
	 */
	Light[] lights = new Light[Cindy3DViewer.MAX_LIGHTS];

	/**
	 * Creates a new light manager.
	 * 
	 * The new light manager defines a default point light at position (0, 0,
	 * 0).
	 */
	public LightManager() {
		LightModificationInfo info = new LightModificationInfo(
				LightType.POINT_LIGHT);
		info.setPosition(new double[] { 0, 0, 0 });
		setLight(0, info);
	}

	/**
	 * Creates a new light or changes or replaces an existing light source.
	 * 
	 * Setting new light source properties results in one of the following three
	 * cases:
	 * 
	 * <ol>
	 * <li>If there is no light source yet at light index <code>light</code>, a
	 * new light source with properties defined by <code>info</code> is created.
	 * <li>If there is already a light source specified at light index
	 * <code>light</code> and the existing light source type and the type
	 * defined in <code>info</code> differ, the existing light source is
	 * replaced and overwritten by a new light source with properties defined in
	 * <code>info</code>.
	 * <li>If the light type specified in <code>info</code> is the same as the
	 * type of the existing light source, the properties of the existing light
	 * source are changed according to the properties defined in
	 * <code>info</code>
	 * </ol>
	 * 
	 * @param light
	 *            light index. <code>light</code> specifies the light index
	 *            ranging from 0 (inclusive) to
	 *            {@value de.tum.in.cindy3dplugin.Cindy3DViewer#MAX_LIGHTS}
	 *            (exclusive).
	 * @param info
	 *            light properties to be set
	 */
	public void setLight(int light, LightModificationInfo info) {
		if (lights[light] == null || lights[light].getType() != info.getType()) {
			lightSettingChanged = true;

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
			lightSettingChanged = true;
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

	/**
	 * Method to build up a string that is inserted into GLSL shader code.
	 * 
	 * The string inserted into GLSL shading section causes the shading to
	 * consider all the lights defined in the manager.
	 * 
	 * @return GLSL code
	 */
	public String getShaderFillIn() {
		String str = "vec3 eye = -normalize(ecPoint);\n";
		for (int i = 0; i < Cindy3DViewer.MAX_LIGHTS; ++i) {
			if (lights[i] != null && lights[i].isEnabled()) {
				str += lights[i].getShaderFillIn(i) + "\n";
			}
		}
		return str;
	}

	/**
	 * @return <code>true</code> if at least one property of a light source has
	 *         changed since the last reset, <code>false</code> otherwise
	 */
	public boolean hasLightSettingChanged() {
		return lightSettingChanged;
	}

	/*
	 * TODO: Find method name
	 */
	public void wurstMitBrot() {
		this.lightSettingChanged = false;
	}

	/**
	 * Sets all parameters and states needed for rendering for each existing
	 * light source.
	 * 
	 * @see Light#setGLState(GL2, int)
	 * 
	 * @param gl
	 *            GL handle
	 */
	public void setGLState(GL2 gl) {
		// Viewer assumed to be at (0,0,0) in eye coordinates
		gl.glLightModeli(GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, 1);
		
		for (int i = 0; i < Cindy3DViewer.MAX_LIGHTS; ++i) {
			if (lights[i] != null) {
				lights[i].setGLState(gl, GL2.GL_LIGHT0 + i);
			}
		}
	}

	/**
	 * Disables the specified light.
	 * 
	 * @param light
	 *            light index. <code>light</code> specifies the light index
	 *            ranging from 0 (inclusive) to
	 *            {@value de.tum.in.cindy3dplugin.Cindy3DViewer#MAX_LIGHTS}
	 *            (exclusive).
	 */
	public void disableLight(int light) {
		if (!lights[light].isEnabled()) {
			return;
		}
		
		lights[light].setEnabled(false);
		lightSettingChanged = true;
	}

}
