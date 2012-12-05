package com.baoxue.task.task;

import android.util.Log;

import com.baoxue.task.common.Utility;

public class ShellTaskItem extends TaskItem {

	private String shell;
	private long time;

	private final String TAG = "ShellTaskItem:" + this.hashCode();

	public ShellTaskItem(String shell) {
		this.shell = shell;
	}

	@Override
	public void init() {
		setState(TaskItem.STATE_READY);
	}

	@Override
	public void execute() {
		setState(TaskItem.STATE_RUNNING);
		time = System.currentTimeMillis();
		new Thread() {

			@Override
			public void run() {
				String ss = Utility.runCommand(shell);
				Log.d(TAG, ss);
				setState(TaskItem.STATE_COMPLATE);
			}

		}.start();
	}

	@Override
	public void cancel() {
		setState(TaskItem.STATE_CANCEL);
	}

	@Override
	public void checkTimeout() {
		if (getState() == TaskItem.STATE_RUNNING) {
			if ((System.currentTimeMillis() - time) > 60000) {
				Log.d(TAG, "timeOut");
				setState(TaskItem.STATE_COMPLATE);
			}
		}
	}

	@Override
	public int getId() {
		String str = "link" + shell;
		return str.hashCode();
	}

}
