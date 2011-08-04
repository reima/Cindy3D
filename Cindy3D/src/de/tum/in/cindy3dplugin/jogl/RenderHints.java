package de.tum.in.cindy3dplugin.jogl;

public class RenderHints {
	enum RenderMode {
		FIXED_FUNCTION_PIPELINE,
		PROGRAMMABLE_PIPELINE,
	}

	private int samplingRate = 1;
	private RenderMode renderMode;
	private double allowedScreenSpaceError;

	public RenderHints(RenderMode renderMode, int samplingRate,
			double allowedScreenSpaceError) {
		this.renderMode = renderMode;
		this.samplingRate = samplingRate;
		this.allowedScreenSpaceError = allowedScreenSpaceError;
	}

	public void setRenderMode(RenderMode renderMode) {
		this.renderMode = renderMode;
	}
	
	public RenderMode getRenderMode() {
		return renderMode;
	}
	
	public void setSamplingRate(int samplingRate) {
		this.samplingRate = samplingRate;
	}
	
	public int getSamplingRate() {
		return samplingRate;
	}
	
	public void setAllowedScreenSpaceError(double allowedScreenSpaceError) {
		this.allowedScreenSpaceError = allowedScreenSpaceError;
	}
	
	public double getAllowedScreenSpaceError() {
		return allowedScreenSpaceError;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof RenderHints)) {
			return false;
		}
		RenderHints hints = (RenderHints)obj;
		if (samplingRate != hints.getSamplingRate() ||
			renderMode != hints.getRenderMode()) {
			return false;
		}
		return true;
	}
	
	public RenderHints clone() {
		return new RenderHints(renderMode, samplingRate,
				allowedScreenSpaceError);
	}
}
