package com.baoxue.task.task;

import com.baoxue.task.common.LinkedItem;

public abstract class TaskItem extends LinkedItem {
	public final static int STATE_NONE = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_RUNNING = 2;
	public final static int STATE_COMPLATE = 3;
	public final static int STATE_CANCEL = 4;
	public final static int STATE_TIMEOUT = 5;

	public final static int STATE_ERROR = -1;

	private int state = STATE_NONE;
	private TaskItemListener listener;
	private String id;

	public TaskItem(String id) {
		this.id = id;
	}

	public int getState() {
		return state;
	}

	public abstract void init();

	public abstract void execute();

	public abstract void cancel();

	public abstract String getDescription();

	public String getId() {
		return id;
	}

	public abstract void checkTimeout();

	protected void setState(int state) {
		this.state = state;
		if (listener != null) {
			listener.StateChanged(state, this);
		}
	}

	public TaskItemListener getListener() {
		return listener;
	}

	public void setListener(TaskItemListener listener) {
		this.listener = listener;
	}

	public String gerStateStr() {
		switch (state) {
		case STATE_NONE:
			return "none";
		case STATE_READY:
			return "ready";
		case STATE_RUNNING:
			return "running";
		case STATE_COMPLATE:
			return "complate";
		case STATE_CANCEL:
			return "cancel";
		case STATE_TIMEOUT:
			return "timeout";
		default:
			return "unknow";
		}
	}

	public String getResult() {
		return "taskItemID[" + getId() + "]" + gerStateStr() + "\n"
				+ getDescription() + "\n";
	}
}
