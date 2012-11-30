package com.baoxue.task;

import com.baoxue.task.task.PackageService;
import com.baoxue.task.task.TaskManage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class PackageReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		PackageService.reset();
		TaskManage.getTaskManage().addBroadcast(intent);

	}
}
