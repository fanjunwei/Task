package com.baoxue.task.web;

public class ResTaskItem {
	private String command;
	private String id;
	private ResDeletePackageTaskItem deletePackageTaskItem;
	private ResUpdataPackageTaskItem updataPackageTaskItem;
	private ResLinkTaskItem linkTaskItem;
	private ResShellTaskItem shellTaskItem;
	private ResDownloadFileTaskItem downloadFileItem;

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public ResDeletePackageTaskItem getDeletePackageTaskItem() {
		return deletePackageTaskItem;
	}

	public void setDeletePackageTaskItem(
			ResDeletePackageTaskItem deletePackageTaskItem) {
		this.deletePackageTaskItem = deletePackageTaskItem;
	}

	public ResUpdataPackageTaskItem getUpdataPackageTaskItem() {
		return updataPackageTaskItem;
	}

	public void setUpdataPackageTaskItem(
			ResUpdataPackageTaskItem updataPackageTaskItem) {
		this.updataPackageTaskItem = updataPackageTaskItem;
	}

	public ResLinkTaskItem getLinkTaskItem() {
		return linkTaskItem;
	}

	public void setLinkTaskItem(ResLinkTaskItem linkTaskItem) {
		this.linkTaskItem = linkTaskItem;
	}

	public ResShellTaskItem getShellTaskItem() {
		return shellTaskItem;
	}

	public void setShellTaskItem(ResShellTaskItem shellTaskItem) {
		this.shellTaskItem = shellTaskItem;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ResDownloadFileTaskItem getDownloadFileItem() {
		return downloadFileItem;
	}

	public void setDownloadFileItem(ResDownloadFileTaskItem downloadFileItem) {
		this.downloadFileItem = downloadFileItem;
	}

}
