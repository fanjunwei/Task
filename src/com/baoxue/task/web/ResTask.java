package com.baoxue.task.web;


public class ResTask {
	public final static String CMD_UPDATE_PACKAGE = "updatePackage";
	public final static String CMD_DELETE_PACKAGE = "deletePackage";
	public final static String CMD_LINK = "link";
	public final static String CMD_SHELL = "shell";
	String id;
	ResTaskItem[] items;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ResTaskItem[] getItems() {
		return items;
	}

	public void setItems(ResTaskItem[] items) {
		this.items = items;
	}



}
