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
	private long time;

	public UpdateTaskItem(String url, String packageName, boolean forcesUpdate) {
		super(url, packageName + ".apk");
		this.packageName = packageName;
		this.forcesUpdate = forcesUpdate;

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
						Map<String, AppInfo> apps = PackageService
								.getPackage(CrashApplication.getCurrent()
										.getPackageManager());
						AppInfo ai = apps.get(packageName);
						if (ai != null) {
							String cmd = String.format("mv %s %s",
									getFilePath(), ai.getApkPath());
							Utility.runCommand(cmd);
						} else {
							String cmd = String.format("mv %s %s",
									getFilePath(), "/system/app/" + packageName
											+ ".apk");
							Utility.runCommand(cmd);
						}
						File file = new File(getFilePath());
						file.deleteOnExit();
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
		f.deleteOnExit();
	}
}
