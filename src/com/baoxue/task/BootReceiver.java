package com.baoxue.task;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.baoxue.task.common.SaveApplication;
import com.baoxue.task.common.Utility;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		SaveApplication.setApp((Application) context.getApplicationContext());
		Log.d("Task", "boot action=" + intent.getAction());
		Utility.setAlarm(context, 60 * 1000);
	}

}
