package com.baoxue.task.task;

import com.baoxue.task.common.LinkedItem;

public abstract class TaskItem extends LinkedItem {
	public final static int STATE_NONE = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_RUNNING = 2;
	public final static int STATE_COMPLATE = 3;
	public final static int STATE_CANCEL = 4;

	public final static int STATE_ERROR = -1;

	private int state = STATE_NONE;
	private TaskListener listener;

	public int getState() {
		return state;
	}

	public abstract void init();

	public abstract void execute();

	public abstract void cancel();

	public abstract int getId();

	public abstract void checkTimeout();

	protected void setState(int state) {
		this.state = state;
		if (listener != null) {
			listener.StateChanged(state, this);
		}
	}

	public TaskListener getListener() {
		return listener;
	}

	public void setListener(TaskListener listener) {
		this.listener = listener;
	}

}
