package com.baoxue.task.web;

public class ResTaskItem {
	public final static String CMD_UPDATE_PACKAGE = "updatePackage";
	public final static String CMD_DELETE_PACKAGE = "deletePackage";
	private String command;
	private String packageName;
	private int versionCode;
	private String url;
	private boolean forcesUpdate;

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean getForcesUpdate() {
		return forcesUpdate;
	}

	public void setForcesUpdate(boolean forcesUpdate) {
		this.forcesUpdate = forcesUpdate;
	}

}
