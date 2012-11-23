package com.baoxue.task.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.baoxue.task.CrashApplication;

public class Utility {
	public static int FourBytesToInt(byte Bytes[]) {
		return FourBytesToInt(Bytes, 0);
	}

	public static int FourBytesToInt(byte Bytes[], int startIndex) {
		int a = Bytes[startIndex] & 0xff;
		int b = Bytes[startIndex + 1] & 0xff;
		int c = Bytes[startIndex + 2] & 0xff;
		int d = Bytes[startIndex + 3] & 0xff;
		return a | b << 8 | c << 16 | d << 24;
	}

	public static byte[] intToByte(int i) {
		byte[] result = new byte[4];
		result[0] = (byte) (i & 0xff);
		result[1] = (byte) ((i & 0xff00) >> 8);
		result[2] = (byte) ((i & 0xff0000) >> 16);
		result[3] = (byte) ((i & 0xff000000) >> 24);
		return result;

	}

	public static String formatLength(long length) {
		java.text.NumberFormat formater = java.text.DecimalFormat.getInstance();
		formater.setMaximumFractionDigits(2);
		formater.setMinimumFractionDigits(0);
		
		if (length > 1024 * 1024) {
			float value = ((float) length) / (1024 * 1024);
			return formater.format(value) + "MB";
		} else if (length > 1024) {
			float value = ((float) length) / 1024;
			return formater.format(value) + "KB";
		} else {
			return String.valueOf(length) + "B";
		}
	}

	//private static Activity lastRootActivity = null;

	public static int getVersionCode() {
		Context mContext = CrashApplication.getCurrent();
		PackageInfo inf;
		try {
			inf = mContext.getPackageManager().getPackageInfo(
					mContext.getPackageName(), 0);
			return inf.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static String getVersionName() {
		Context mContext = CrashApplication.getCurrent();
		PackageInfo inf;
		try {
			inf = mContext.getPackageManager().getPackageInfo(
					mContext.getPackageName(), 0);
			return inf.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
