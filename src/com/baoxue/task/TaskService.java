package com.baoxue.task;

import com.baoxue.task.task.DeletePackageTaskItem;
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
				if (ResTaskItem.CMD_UPDATE_PACKAGE.equals(item.getCommand())) {
					TaskItem taskItem = new UpdateTaskItem(item.getUrl(),
							item.getPackageName(), item.getForcesUpdate());
					TaskManage.getTaskManage().addTaskItem(taskItem);

				} else if (ResTaskItem.CMD_DELETE_PACKAGE.equals(item
						.getCommand())) {
					TaskItem taskItem = new DeletePackageTaskItem(
							item.getPackageName());
					TaskManage.getTaskManage().addTaskItem(taskItem);
				}
			}
			TaskManage.getTaskManage().setListener(this);
			TaskManage.getTaskManage().beginTask();
		}
	}

	@Override
	public void Complate() {

		if (task != null) {
			WebServicePort.DoTask(task.getId());
		}

	}

}
