package com.baoxue.task.task;

import java.io.File;
import java.util.UUID;

import android.util.Log;

import com.baoxue.task.common.Utility;

public class DownloadFileTaskItem extends DownloadTaskItem {

	private final String TAG = "DownloadFileTaskItem:" + this.hashCode();
	private String desPath;
	private long time;
	private String result;

	public DownloadFileTaskItem(String id, String url, String desPath) {
		super(id, url, UUID.randomUUID().toString());
		this.desPath = desPath;

	}

	@Override
	public void execute() {
		if (DownloadFileTaskItem.this.getState() == TaskItem.STATE_READY) {
			time = System.currentTimeMillis();
			setState(TaskItem.STATE_RUNNING);
			new Thread() {
				@Override
				public void run() {

					Log.d(TAG, "execute:run");

					String cmd = String.format("cp %s %s", getFilePath(),
							desPath);
					String ss = Utility.runCommand(cmd);
					Log.d(TAG, ss);
					deleteFile();
					setState(TaskItem.STATE_COMPLATE);

				}

			}.start();
		}

	}

	@Override
	public void checkTimeout() {
		super.checkTimeout();
		if (getState() == TaskItem.STATE_RUNNING) {
			if ((System.currentTimeMillis() - time) > 60000) {
				Log.d(TAG, "timeOut");
				deleteFile();
				setState(TaskItem.STATE_TIMEOUT);
			}
		}
	}

	@Override
	protected void onDownloadComplate() {
		Log.d(TAG, "onDownloadComplate:" + getState());
		setState(TaskItem.STATE_READY);
	}

	private void deleteFile() {
		File f = new File(getFilePath());
		if (f.exists()) {
			f.delete();
		}
	}

	@Override
	public String getDescription() {
		return "upate:desPath=" + desPath;
	}

	@Override
	public String getResult() {
		return super.getResult() + result + "\n";
	}
}
