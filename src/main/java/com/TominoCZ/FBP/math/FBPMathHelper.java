package com.TominoCZ.FBP.math;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.math.MathHelper;

public class FBPMathHelper {
	static List<double[]> newVec = new ArrayList();

	static double[] cube;

	static double sinAngleX;
	static double sinAngleY;
	static double sinAngleZ;

	static double cosAngleX;
	static double cosAngleY;
	static double cosAngleZ;

	static float radsX;
	static float radsY;
	static float radsZ;

	static double[] d = new double[3];
	
	public static List<double[]> rotateCubeXYZ(double AngleX, double AngleY, double AngleZ, double halfSize) {
		cube = new double[] { -halfSize, -halfSize, halfSize, -halfSize, halfSize, halfSize, halfSize, halfSize,
				halfSize, halfSize, -halfSize, halfSize, halfSize, -halfSize, -halfSize, halfSize, halfSize, -halfSize,
				-halfSize, halfSize, -halfSize, -halfSize, -halfSize, -halfSize, -halfSize, -halfSize, -halfSize,
				-halfSize, halfSize, -halfSize, -halfSize, halfSize, halfSize, -halfSize, -halfSize, halfSize, halfSize,
				-halfSize, halfSize, halfSize, halfSize, halfSize, halfSize, halfSize, -halfSize, halfSize, -halfSize,
				-halfSize, halfSize, halfSize, -halfSize, halfSize, halfSize, halfSize, -halfSize, halfSize, halfSize,
				-halfSize, halfSize, -halfSize, -halfSize, -halfSize, -halfSize, -halfSize, -halfSize, halfSize,
				halfSize, -halfSize, halfSize, halfSize, -halfSize, -halfSize };

		radsX = (float) Math.toRadians(AngleX);
		radsY = (float) Math.toRadians(AngleY);
		radsZ = (float) Math.toRadians(AngleZ);

		sinAngleX = MathHelper.sin(radsX);
		sinAngleY = MathHelper.sin(radsY);
		sinAngleZ = MathHelper.sin(radsZ);

		cosAngleX = MathHelper.cos(radsX);
		cosAngleY = MathHelper.cos(radsY);
		cosAngleZ = MathHelper.cos(radsZ);

		newVec.clear();

		for (int i = 0; i < 72; i += 3) {
			d = new double[] { cube[i], cube[i + 1] * cosAngleX - cube[i + 2] * sinAngleX,
					cube[i + 1] * sinAngleX + cube[i + 2] * cosAngleX };

			d = new double[] { d[0] * cosAngleY + d[2] * sinAngleY, d[1], d[0] * sinAngleY - d[2] * cosAngleY };

			d = new double[] { d[0] * cosAngleZ - d[1] * sinAngleZ, d[0] * sinAngleZ + d[1] * cosAngleZ, d[2] };

			newVec.add(d);
		}
 
		return newVec;
	}

	public static double round(double d, int decimals) {
		int i = (int) Math.round(d * Math.pow(10, decimals));
		return ((double) i) / Math.pow(10, decimals);
	}
}