package de.tum.in.jrealityplugin.jogl;

import org.apache.commons.math.linear.RealMatrix;

public class Util {
	public static float[] matrixToFloatArray(RealMatrix m) {
		int rows = m.getRowDimension();
		int cols = m.getColumnDimension();
		
		float[] result = new float[rows*cols];
		double[][] data = m.getData();
		int offset = 0;
		for (int row = 0; row < rows; ++row) {
			for (int col = 0; col < cols; ++col, ++offset) {
				result[offset] = (float) data[row][col];
			}
		}
		
		return result;
	}
}
