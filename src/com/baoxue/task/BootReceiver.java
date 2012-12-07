package com.baoxue.task;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.baoxue.task.common.Utility;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("Task", "boot action=" + intent.getAction());
		Utility.setAlarm(context, 60 * 1000);
	}

}
