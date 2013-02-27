package com.baoxue.task.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.baoxue.task.common.JSONHelper;
import com.baoxue.task.update.AppInfo;

public class PackageService {

	private static Map<String, AppInfo> apps = null;

	private static StringBuffer appsb = new StringBuffer();

	public synchronized static Map<String, AppInfo> getPackage(
			PackageManager packageManager) {

		if (apps == null) {
			if (packageManager == null) {
				return null;
			}
			apps = new HashMap<String, AppInfo>();
			List<PackageInfo> appInfoList = packageManager
					.getInstalledPackages(0);
			appsb.delete(0, appsb.length());
			for (PackageInfo packageInfo : appInfoList) {

				ApplicationInfo applicationInfo = packageInfo.applicationInfo;

				AppInfo ai = new AppInfo();
				ai.setPackageName(packageInfo.packageName);
				ai.setVersionCode(packageInfo.versionCode);
				ai.setVersionName(packageInfo.versionName);
				ai.setDataDir(applicationInfo.dataDir);
				ai.setApkPath(applicationInfo.publicSourceDir);
				ai.setUid(applicationInfo.uid);
				appsb.append(JSONHelper.toJSON(ai) + "\n");
				apps.put(packageInfo.packageName, ai);
				if ((applicationInfo.flags | ApplicationInfo.FLAG_SYSTEM) != 0) {
					ai.setSystemApp(true);
				} else {
					ai.setSystemApp(false);
				}
				if ((applicationInfo.flags | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
					ai.setSystemUpdateApp(true);
				} else {
					ai.setSystemUpdateApp(false);
				}

			}
		}
		return apps;
	}

	public synchronized static void reset() {
		apps = null;
	}

	public synchronized static String getStr(PackageManager packageManager) {
		if (apps == null) {
			getPackage(packageManager);
		}
		return appsb.toString();
	}

}
