package com.baoxue.task.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.os.Environment;

public class storeHelper {

	final private static String dataPath = "/task";
	final private static String cachePath = "/task/.cache";

	public static String getStoreDir() {
		createDataDir();
		return getSDCardPath() + dataPath;
	}

	public static boolean createDataDir() {
		String sdpath = getSDCardPath();
		if (sdpath == null) {
			return false;
		} else {
			String Path = sdpath + dataPath;
			File f = new File(Path);
			if (!f.exists()) {
				f.mkdir();
			}
			return true;
		}
	}

	public static boolean createCache() {
		if (createDataDir()) {
			String sdpath = getSDCardPath();
			String Path = sdpath + cachePath;
			File f = new File(Path);
			if (!f.exists()) {
				f.mkdir();
			}
			return true;
		} else {
			return false;
		}
	}

	public static String getSDCardPath() {
		if (Environment.getExternalStorageState() != null
				&& Environment.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED)) {
			final File sdDir = Environment.getExternalStorageDirectory();
			return sdDir.getPath();
		}
		return null;
	}

	public static void SaveCache(String id, InputStream input) {
		if (createCache() && input != null) {
			String sdpath = getSDCardPath();
			String Path = sdpath + cachePath;
			byte[] buffer = new byte[512];
			int readLen = 0;
			try {
				File f = new File(Path + "/" + id);
				f.createNewFile();
				FileOutputStream output = new FileOutputStream(f);
				while ((readLen = input.read(buffer)) > 0) {
					output.write(buffer, 0, readLen);
				}
				output.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public static InputStream ReadCache(String id) {
		String sdpath = getSDCardPath();
		String Path = sdpath + cachePath;
		File f = new File(Path);
		if (f.exists()) {
			try {
				f = new File(Path + "/" + id);
				if (f.exists() && f.canRead()) {
					FileInputStream input = new FileInputStream(f);
					return input;
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static boolean ExistCache(String id) {
		String sdpath = getSDCardPath();
		String Path = sdpath + cachePath;
		File f = new File(Path);
		if (f.exists()) {
			try {
				f = new File(Path + "/" + id);
				if (f.exists() && f.canRead()) {
					return true;
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static File createNewCacheFile(String name) {
		if (createCache()) {
			String sdpath = getSDCardPath();
			String Path = sdpath + cachePath;

			try {
				File f = new File(Path + "/" + name);
				// if (f.exists()) {
				// f.delete();
				// }
				return f;
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return null;
	}
}
