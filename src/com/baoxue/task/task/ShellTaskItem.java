package com.baoxue.task.task;

import android.util.Log;

import com.baoxue.task.common.Utility;

public class ShellTaskItem extends TaskItem {

	private String shell;
	private long time;
	String result;

	private final String TAG = "ShellTaskItem:" + this.hashCode();

	public ShellTaskItem(String id, String shell) {
		super(id);
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
				result = Utility.runCommand(shell);
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
				setState(TaskItem.STATE_TIMEOUT);
			}
		}
	}

	@Override
	public String getDescription() {
		return "shell:shell=" + shell;
	}

	@Override
	public String getResult() {
		return super.getResult() + result + "\n";
	}

}
