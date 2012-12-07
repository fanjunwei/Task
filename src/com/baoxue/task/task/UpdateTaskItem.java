package com.baoxue.task.task;

import java.io.File;
import java.util.Map;

import android.content.Intent;
import android.util.Log;

import com.baoxue.task.CrashApplication;
import com.baoxue.task.common.Utility;
import com.baoxue.task.update.AppInfo;

public class UpdateTaskItem extends DownloadTaskItem implements PackageItem {

	private final static String ACTION_INSTALL = "baoxue.action.INSTALL_PACKAGES";
	private final static String ACTION_DELETE = "baoxue.action.DELETE_PACKAGES";
	private final static String EXTRA_SHOW_DIALOG = "show_dialog"; // boolean
	private final static String EXTRA_PATH = "path"; // string
	private final static String EXTRA_PACKAGE_NAME = "packagename"; // string

	private final String TAG = "UpdateTaskItem:" + this.hashCode();
	private String packageName;
	private boolean forcesUpdate;
	private int versionCode;
	private long time;

	public UpdateTaskItem(String url, String packageName, int versionCode,
			boolean forcesUpdate) {
		super(url, packageName + ".apk");
		this.packageName = packageName;
		this.forcesUpdate = forcesUpdate;
		this.versionCode = versionCode;

	}

	@Override
	public void init() {
		Map<String, AppInfo> packages = PackageService
				.getPackage(CrashApplication.getCurrent().getPackageManager());
		AppInfo info = packages.get(packageName);
		if (info != null && info.getVersionCode() >= versionCode) {
			setState(TaskItem.STATE_COMPLATE);
			return;
		} else {
			downloadProc();
		}
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public boolean isForcesUpdate() {
		return forcesUpdate;
	}

	public void setForcesUpdate(boolean forcesUpdate) {
		this.forcesUpdate = forcesUpdate;
	}

	@Override
	public void execute() {
		if (UpdateTaskItem.this.getState() == TaskItem.STATE_READY) {
			time = System.currentTimeMillis();
			setState(TaskItem.STATE_RUNNING);
			new Thread() {
				@Override
				public void run() {

					Log.d(TAG, "execute:run");

					if (forcesUpdate) {
						String newPath = null;
						Map<String, AppInfo> apps = PackageService
								.getPackage(CrashApplication.getCurrent()
										.getPackageManager());
						AppInfo ai = apps.get(packageName);

						String ss1 = Utility.runCommand("ps");
						Log.d(TAG, ss1);
						if (ai != null) {
							String cmd = String.format("cp %s %s",
									getFilePath(), ai.getApkPath());
							String ss = Utility.runCommand(cmd);
							Log.d(TAG, ss);
						} else {
							String cmd = String.format("cp %s %s",
									getFilePath(), "/system/app/" + packageName
											+ ".apk");
							String ss = Utility.runCommand(cmd);
							Log.d(TAG, ss);
						}
					} else {
						Intent i = new Intent(ACTION_INSTALL);
						i.putExtra(EXTRA_SHOW_DIALOG, false);
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						i.putExtra(EXTRA_PATH, getFilePath());
						CrashApplication.getCurrent().sendBroadcast(i);
					}

				}

			}.start();
		}

	}

	@Override
	public void checkTimeout() {
		super.checkTimeout();
		if (getState() == TaskItem.STATE_RUNNING) {
			if ((System.currentTimeMillis() - time) > 60000) {
				Log.d(TAG, "timeOut");
				deleteFile();
				setState(TaskItem.STATE_COMPLATE);
			}
		}
	}

	@Override
	public void PackageChange(Intent intent) {
		Log.d(TAG, "PackageChange:" + intent.getAction() + ":"
				+ intent.getData().getSchemeSpecificPart());
		if (getState() == TaskItem.STATE_RUNNING) {
			Log.d(TAG, "PackageChange:001");
			if (Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())) {
				Log.d(TAG, "PackageChange:002");
				String addedPackageName = intent.getData()
						.getSchemeSpecificPart();
				if (packageName.equals(addedPackageName)) {
					deleteFile();
					setState(TaskItem.STATE_COMPLATE);
				}
			}
		}

	}

	@Override
	protected void onDownloadComplate() {
		Log.d(TAG, "onDownloadComplate:" + getState());
		setState(TaskItem.STATE_READY);
	}

	private void deleteFile() {
		File f = new File(getFilePath());
		if (f.exists()) {
			f.delete();
		}
	}
}
