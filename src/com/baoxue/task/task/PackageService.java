package com.baoxue.task.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.baoxue.task.update.AppInfo;

public class PackageService {

	private static Map<String, AppInfo> apps = new HashMap<String, AppInfo>();

	public static Map<String, AppInfo> getPackage(PackageManager packageManager) {
		if (apps == null) {
			List<PackageInfo> appInfoList = packageManager
					.getInstalledPackages(0);
			for (PackageInfo packageInfo : appInfoList) {

				ApplicationInfo applicationInfo = packageInfo.applicationInfo;
				AppInfo ai = new AppInfo();
				ai.setPackageName(packageInfo.packageName);
				ai.setVersionCode(packageInfo.versionCode);
				ai.setVersionName(packageInfo.versionName);
				ai.setDataDir(applicationInfo.dataDir);
				ai.setApkPath(applicationInfo.publicSourceDir);
				ai.setUid(applicationInfo.uid);
				apps.put(packageInfo.packageName, ai);

			}
		}
		return apps;
	}

	public static void reset() {
		apps = null;
	}

}
