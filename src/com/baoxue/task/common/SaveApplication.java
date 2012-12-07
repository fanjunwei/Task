package com.baoxue.task.common;

import android.app.Application;

public class SaveApplication {

	private static Application instance = null;

	public static Application getCurrent() {
		return instance;
	}

	public static void setApp(Application app) {
		instance = app;
	}
}
