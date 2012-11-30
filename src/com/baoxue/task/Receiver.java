package com.baoxue.task;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Receiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("Task", "getTask");
		Intent i = new Intent();
		i.setClass(context, TaskService.class);
		context.startService(i);
	}

}
