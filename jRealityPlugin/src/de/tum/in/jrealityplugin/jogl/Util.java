package de.tum.in.jrealityplugin.jogl;

import org.apache.commons.math.geometry.Vector3D;
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

	public static float[] matrixToFloatArrayTransposed(RealMatrix m) {
		int rows = m.getRowDimension();
		int cols = m.getColumnDimension();
		
		float[] result = new float[rows*cols];
		double[][] data = m.getData();
		int offset = 0;
		for (int row = 0; row < rows; ++row) {
			for (int col = 0; col < cols; ++col, ++offset) {
				result[offset] = (float) data[col][row];
			}
		}
		
		return result;
	}
	
	public static double[] vectorToDoubleArray(Vector3D v) {
		return new double[] {v.getX(), v.getY(), v.getZ()};
	}
}
