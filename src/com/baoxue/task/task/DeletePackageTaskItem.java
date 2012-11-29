package com.baoxue.task.task;

import java.util.Map;

import com.baoxue.task.CrashApplication;
import com.baoxue.task.common.Utility;
import com.baoxue.task.update.AppInfo;

public class DeletePackageTaskItem extends TaskItem {

	String packageName;

	public DeletePackageTaskItem(String packageName) {

	}

	@Override
	public void init() {
		setState(TaskItem.STATE_READY);
	}

	@Override
	public void execute() {
		setState(TaskItem.STATE_RUNNING);
		Map<String, AppInfo> apps = PackageService.getPackage(CrashApplication
				.getCurrent().getPackageManager());
		AppInfo appInfo = apps.get(packageName);
		String apkPath = appInfo.getApkPath();
		String dataPath = appInfo.getDataDir();
		String cmd = String.format("rm %s", apkPath);
		Utility.runCommand(cmd);
		cmd = String.format("rm -r %s", dataPath);
		Utility.runCommand(cmd);
		PackageService.reset();
		setState(TaskItem.STATE_COMPLATE);
	}

	@Override
	public void cancel() {
		setState(TaskItem.STATE_CANCEL);
	}

	@Override
	public void checkTimeout() {

	}

	@Override
	public int getId() {
		String str = "delete" + packageName;
		return str.hashCode();
	}

}
