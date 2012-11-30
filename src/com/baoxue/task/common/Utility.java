package com.baoxue.task.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;

import com.baoxue.task.CrashApplication;
import com.baoxue.task.Receiver;
import com.baoxue.task.db.Profile;

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

	// private static Activity lastRootActivity = null;

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

	public static int readHttp(InputStream input, byte[] buffer, int offset,
			int length) {
		int readlen = 0;
		int total = 0;
		if (length > 0) {
			try {
				while ((readlen = input.read(buffer, offset, length)) > 0
						&& length > 0) {
					length -= readlen;
					offset += readlen;
					total += readlen;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return total;
	}

	public static byte[] readBlock(InputStream input) {
		byte[] lenByte = new byte[4];
		byte[] buffer = null;
		int bytes = 0;

		readHttp(input, lenByte, 0, 4);
		bytes = Utility.FourBytesToInt(lenByte);
		if (bytes > 0) {
			buffer = new byte[bytes];
			readHttp(input, buffer, 0, bytes);
		}
		return buffer;

	}

	public static String runCommand(String cmd) {

		LocalSocket s = null;
		LocalSocketAddress l;
		s = new LocalSocket();
		l = new LocalSocketAddress("task",
				LocalSocketAddress.Namespace.RESERVED);
		byte[] lineBuffer = null;
		StringBuffer sb = new StringBuffer();
		try {
			s.connect(l);
			OutputStream out = s.getOutputStream();

			byte[] b = cmd.getBytes();
			out.write((int) b.length);
			out.write(b);
			byte[] end = new byte[] { (byte) 0xff };

			out.write(end);

			InputStream input = s.getInputStream();

			while ((lineBuffer = readBlock(input)) != null) {
				sb.append(new String(lineBuffer, "utf-8"));
			}
			input.close();
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (s != null)
					s.close();
			} catch (IOException e) {
			}
		}
		return sb.toString();
	}

	public static String getDeviceId() {
		String id = Profile.getProfile().getValue("deviceID");
		if (id == null) {
			id = UUID.randomUUID().toString();
			Profile.getProfile().setValue("deviceID", id);
		}
		return id;
	}

	public static void setAlarm(Context context, long intervalMillis) {

		PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(
				context, Receiver.class), 0);

		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		am.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(),
				intervalMillis, pi);

	}
}
