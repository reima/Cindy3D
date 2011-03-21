package de.jreality.shader;

public interface MyLineShader extends DefaultLineShader {
	public final static int LINE_TYPE_DEFAULT = 0;
	
	public abstract void setLineType(int lineType);
	public abstract int getLineType();
}
