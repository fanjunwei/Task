package com.baoxue.task.web;

public class ResLinkTaskItem {
	private String message;
	private String url;
	private boolean background;
	private boolean autoOpen;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isBackground() {
		return background;
	}

	public void setBackground(boolean background) {
		this.background = background;
	}

	public boolean isAutoOpen() {
		return autoOpen;
	}

	public void setAutoOpen(boolean autoOpen) {
		this.autoOpen = autoOpen;
	}

}
