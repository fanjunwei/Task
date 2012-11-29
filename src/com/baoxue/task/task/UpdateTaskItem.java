package com.baoxue.task.task;

import java.io.File;
import java.util.Map;

import android.content.Intent;

import com.baoxue.task.CrashApplication;
import com.baoxue.task.common.Utility;
import com.baoxue.task.update.AppInfo;

public class UpdateTaskItem extends DownloadTaskItem implements TaskListener {

	private final static String ACTION_INSTALL = "baoxue.action.INSTALL_PACKAGES";
	private final static String ACTION_DELETE = "baoxue.action.DELETE_PACKAGES";
	private final static String EXTRA_SHOW_DIALOG = "show_dialog"; // boolean
	private final static String EXTRA_PATH = "path"; // stirng
	private final static String EXTRA_PACKAGE_NAME = "packagename"; // stirng

	private String packageName;
	private boolean forcesUpdate;

	public UpdateTaskItem(String url, String packageName, boolean forcesUpdate) {
		super(url, packageName + ".apk");
		this.packageName = packageName;
		this.forcesUpdate = forcesUpdate;
		setListener(this);

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
	public void StateChanged(int state, TaskItem sender) {
		if (state == DownloadTaskItem.STATE_DOWNLOAD_COMPLATE) {
			setState(TaskItem.STATE_READY);
		}

	}

	@Override
	public void execute() {
		if (getState() == TaskItem.STATE_READY) {
			setState(TaskItem.STATE_RUNNING);
			if (forcesUpdate) {
				Map<String, AppInfo> apps = PackageService
						.getPackage(CrashApplication.getCurrent()
								.getPackageManager());
				AppInfo ai = apps.get(packageName);
				if (ai != null) {
					String cmd = String.format("mv %s %s", getFilePath(),
							ai.getApkPath());
					Utility.runCommand(cmd);
				} else {
					String cmd = String.format("mv %s %s", getFilePath(),
							"/system/app/" + packageName + ".apk");
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
			setState(TaskItem.STATE_COMPLATE);
		}

	}

}
