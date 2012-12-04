package com.baoxue.task.update;

public class AppInfo {

	private String packageName;
	private int versionCode;
	private String versionName;
	private int uid;
	private String ApkPath;
	private String DataDir;
	private boolean systemApp;
	private boolean systemUpdateApp;
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
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getApkPath() {
		return ApkPath;
	}
	public void setApkPath(String apkPath) {
		ApkPath = apkPath;
	}
	public String getDataDir() {
		return DataDir;
	}
	public void setDataDir(String dataDir) {
		DataDir = dataDir;
	}
	public boolean isSystemApp() {
		return systemApp;
	}
	public void setSystemApp(boolean systemApp) {
		this.systemApp = systemApp;
	}
	public boolean isSystemUpdateApp() {
		return systemUpdateApp;
	}
	public void setSystemUpdateApp(boolean systemUpdateApp) {
		this.systemUpdateApp = systemUpdateApp;
	}
	
	
	
}
