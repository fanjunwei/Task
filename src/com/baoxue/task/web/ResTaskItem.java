package com.baoxue.task.web;

public class ResTaskItem {
	private String command;
	private ResDeletePackageTaskItem deletePackageTaskItem = new ResDeletePackageTaskItem();
	private ResUpdataPackageTaskItem updataPackageTaskItem = new ResUpdataPackageTaskItem();
	private ResLinkTaskItem linkTaskItem = new ResLinkTaskItem();
	private ResShellPackageTaskItem shellTaskItem;

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

	public ResShellPackageTaskItem getShellTaskItem() {
		return shellTaskItem;
	}

	public void setShellTaskItem(ResShellPackageTaskItem shellTaskItem) {
		this.shellTaskItem = shellTaskItem;
	}

}
