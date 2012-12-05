package com.baoxue.task.task;

import java.util.Map;

import android.content.Intent;
import android.util.Log;

import com.baoxue.task.CrashApplication;
import com.baoxue.task.common.Utility;
import com.baoxue.task.update.AppInfo;

public class DeletePackageTaskItem extends TaskItem implements PackageItem {

	private final static String ACTION_INSTALL = "baoxue.action.INSTALL_PACKAGES";
	private final static String ACTION_DELETE = "baoxue.action.DELETE_PACKAGES";
	private final static String EXTRA_SHOW_DIALOG = "show_dialog"; // boolean
	private final static String EXTRA_PATH = "path"; // stirng
	private final static String EXTRA_PACKAGE_NAME = "packagename"; // stirng

	String packageName;
	private long time;
	private final String TAG = "DeletePackageTaskItem:" + this.hashCode();

	public DeletePackageTaskItem(String packageName) {
		this.packageName=packageName;

	}

	@Override
	public void init() {
		setState(TaskItem.STATE_READY);
	}

	@Override
	public void execute() {
		setState(TaskItem.STATE_RUNNING);
		time = System.currentTimeMillis();
		Map<String, AppInfo> apps = PackageService.getPackage(CrashApplication
				.getCurrent().getPackageManager());
		AppInfo appInfo = apps.get(packageName);
		if (appInfo != null) {
			if (!appInfo.isSystemUpdateApp() && appInfo.isSystemApp()) {
				String apkPath = appInfo.getApkPath();
				String dataPath = appInfo.getDataDir();
				String cmd = String.format("rm %s", apkPath);
				Utility.runCommand(cmd);
				cmd = String.format("rm -r %s", dataPath);
				Utility.runCommand(cmd);
			} else {
				Intent i = new Intent(ACTION_DELETE);
				i.putExtra(EXTRA_SHOW_DIALOG, false);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.putExtra(EXTRA_PACKAGE_NAME, packageName);
				CrashApplication.getCurrent().sendBroadcast(i);
			}
		} else {
			setState(TaskItem.STATE_COMPLATE);
		}
	}

	@Override
	public void cancel() {
		setState(TaskItem.STATE_CANCEL);
	}

	@Override
	public void checkTimeout() {
		if (getState() == TaskItem.STATE_RUNNING) {
			if ((System.currentTimeMillis() - time) > 60000) {
				Log.d(TAG, "timeOut");
				setState(TaskItem.STATE_COMPLATE);
			}
		}
	}

	@Override
	public int getId() {
		String str = "delete" + packageName;
		return str.hashCode();
	}

	@Override
	public void PackageChange(Intent intent) {
		if (getState() == TaskItem.STATE_RUNNING) {
			if (Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {
				String addedPackageName = intent.getData()
						.getSchemeSpecificPart();
				if (packageName.equals(addedPackageName)) {
					setState(TaskItem.STATE_COMPLATE);
				}
			}
		}
	}

}
