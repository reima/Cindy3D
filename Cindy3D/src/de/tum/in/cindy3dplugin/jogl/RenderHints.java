package de.tum.in.cindy3dplugin.jogl;

/**
 * Properties that define the quality of the rendering. The different properties
 * that can be defined are
 * <ol>
 * <li>Render mode, which specifies how primitives
 * are rendered
 * <li>Sampling rate, which speficies the rate of multi- or
 * supersampling
 * <li>Screen space error, which defines the maximal error in
 * screen that is acceptable for level of detail rendering
 * </ol>
 */
public class RenderHints {
	/**
	 * All different types how to render primitives.
	 */
	public enum RenderMode {
		/**
		 * Everything should be rendered using the fixed function pipeline.
		 */
		FIXED_FUNCTION_PIPELINE,
		/**
		 * Everything should be rendered using shaders and the programmable
		 * rendering pipeline.
		 */
		PROGRAMMABLE_PIPELINE,
	}

	/**
	 * Rate for either multi or supersampling.
	 */
	private int samplingRate = 1;
	/**
	 * Defines which render mode is set
	 */
	private RenderMode renderMode;
	/**
	 * Maximum screen space error that is allowed if rendering using diferent
	 * level of detail meshes is performed.
	 */
	private double allowedScreenSpaceError;

	/**
	 * Constructs new render hints object with given parameters.
	 * 
	 * @param renderMode
	 *            render mode
	 * @param samplingRate
	 *            sampling rate
	 * @param allowedScreenSpaceError
	 *            allowed screen space error
	 */
	public RenderHints(RenderMode renderMode, int samplingRate,
			double allowedScreenSpaceError) {
		this.renderMode = renderMode;
		this.samplingRate = samplingRate;
		this.allowedScreenSpaceError = allowedScreenSpaceError;
	}

	/**
	 * Set render mode.
	 * 
	 * @param renderMode
	 *            new render mode
	 */
	public void setRenderMode(RenderMode renderMode) {
		this.renderMode = renderMode;
	}

	/**
	 * @return render mode
	 */
	public RenderMode getRenderMode() {
		return renderMode;
	}

	/**
	 * Set sampling rate.
	 * 
	 * @param samplingRate
	 *            new sampling rate
	 */
	public void setSamplingRate(int samplingRate) {
		this.samplingRate = samplingRate;
	}

	/**
	 * @return sampling rate
	 */
	public int getSamplingRate() {
		return samplingRate;
	}

	/**
	 * Set allowed screen space error
	 * 
	 * @param allowedScreenSpaceError
	 *            new allowed screen space error
	 */
	public void setAllowedScreenSpaceError(double allowedScreenSpaceError) {
		this.allowedScreenSpaceError = allowedScreenSpaceError;
	}

	/**
	 * @return allowed screen space error
	 */
	public double getAllowedScreenSpaceError() {
		return allowedScreenSpaceError;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof RenderHints)) {
			return false;
		}
		RenderHints hints = (RenderHints) obj;
		if (samplingRate != hints.getSamplingRate()
				|| renderMode != hints.getRenderMode()) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public RenderHints clone() {
		return new RenderHints(renderMode, samplingRate,
				allowedScreenSpaceError);
	}
}
