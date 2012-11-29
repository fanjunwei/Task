package com.baoxue.task.task;

import java.util.LinkedList;
import java.util.Queue;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;

import com.baoxue.task.common.LinkedItem;
import com.baoxue.task.db.dbHelper;

public class TaskManage {

	private static TaskManage instance = null;
	public static final String DATA_TABLE_NAME = "download_list";
	private boolean cancelAllTaskFlag = false;
	LinkedItem taskDoingListHeader = new TaskItemHeader();
	Queue<TaskItem> taskQueue = new LinkedList<TaskItem>();
	SparseArray<TaskItem> datamap = new SparseArray<TaskItem>();
	int downloadCount = 0;
	final static int MAX_DOWNLOAD_COONT = 3;
	Thread taskThread = null;
	private boolean hasRunCheck = false;
	boolean has_init_history = false;

	private TaskManage() {

	}

	public void InitTaskHistory() {
		if (!has_init_history) {
			has_init_history = true;
			boolean has_task_history = false;
			SQLiteDatabase db = dbHelper.getInstance().getWritableDatabase();
			Cursor c = db.rawQuery("select * from " + DATA_TABLE_NAME, null);
			try {
				while (c.moveToNext()) {
					// has_download_history = true;
					// DownloadTaskItem it = new DownloadTaskItem(c);
					// taskQueue.add(it);
					// datamap.put(it.getId(), it);
				}
			} finally {
				if (c != null)
					c.close();
			}
			if (has_task_history)
				beginTask();
		}
	}

	public static TaskManage getTaskManage() {
		if (instance == null) {
			instance = new TaskManage();
		}
		return instance;
	}

	public synchronized int AddTaskItem(TaskItem taskItem) {
		if (datamap.get(taskItem.getId()) != null) {
			return 1;
		} else {
			taskQueue.add(taskItem);
			datamap.put(taskItem.getId(), taskItem);
			beginTask();
			return 0;
		}
	}

	private void loop() {
		if (!hasRunCheck) {
			hasRunCheck = true;
			downloadCount = 0;
			TaskItem lastItem = null;
			for (TaskItem item = (TaskItem) taskDoingListHeader.getNext(); item != null; item = (TaskItem) item
					.getNext()) {

				if (cancelAllTaskFlag) {
					item.cancel();
					continue;
				}
				switch (item.getState()) {

				case TaskItem.STATE_COMPLATE:
					item.remove();

					break;
				case DownloadTaskItem.STATE_CANCEL:
					datamap.remove(item.getId());
					item.remove();
					break;

				case DownloadTaskItem.STATE_DOWNLOADING:
					if (item instanceof DownloadTaskItem) {
						downloadCount++;
					}
					lastItem = item;
					break;
				default:
					lastItem = item;
					item.checkTimeout();
					break;
				}

			}
			if (taskDoingListHeader.getNext() != null
					&& ((TaskItem) taskDoingListHeader.getNext()).getState() == TaskItem.STATE_READY) {
				TaskItem item = (TaskItem) taskDoingListHeader.getNext();
				item.execute();
			}
			while (downloadCount < MAX_DOWNLOAD_COONT && taskQueue.size() > 0) {
				TaskItem item = taskQueue.remove();
				if (item.getState() == TaskItem.STATE_NONE) {
					item.init();
					if (item instanceof DownloadTaskItem) {
						downloadCount++;
					}
					if (lastItem != null) {
						lastItem.add(item);
					} else {
						taskDoingListHeader.add(item);
					}
				}

			}
			cancelAllTaskFlag = false;
			hasRunCheck = false;
		}
	}

	public void beginTask() {
		if (taskThread == null) {
			taskThread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						while (taskDoingListHeader.getNext() != null
								|| taskQueue.size() > 0) {
							if (!hasRunCheck) {
								loop();
							}
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					} finally {
						taskThread = null;
					}
				}
			});
			taskThread.start();
		}
	}

	public void CancelTask() {
		cancelAllTaskFlag = true;
	}

}
