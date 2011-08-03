package de.tum.in.cindy3dplugin.jogl;

public class RenderHints {
	enum RenderMode {
		FIXED_FUNCTION_PIPELINE,
		PROGRAMMABLE_PIPELINE,
	}

	private int samplingRate = 1;
	private RenderMode renderMode;

	public RenderHints(RenderMode renderMode, int samplingRate) {
		this.renderMode = renderMode;
		this.samplingRate = samplingRate;
	}

	public void setRenderMode(RenderMode renderMode) {
		this.renderMode = renderMode;
	}
	
	public void setSamplingRate(int samplingRate) {
		this.samplingRate = samplingRate;
	}
	
	public int getSamplingRate() {
		return samplingRate;
	}
	
	public RenderMode getRenderMode() {
		return renderMode;
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
}
