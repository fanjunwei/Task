package com.baoxue.task;

import com.baoxue.task.store.storeHelper;

import android.app.Application;

public class CrashApplication extends Application {

	private static Application instance = null;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		storeHelper.createCache();

	}

	public static Application getCurrent() {
		return instance;
	}

}
