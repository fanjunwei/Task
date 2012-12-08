package com.baoxue.task.task;

import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.baoxue.task.R;
import com.baoxue.task.common.Utility;

public class LinkTaskItem extends TaskItem {

	private String url;
	private String message;
	private boolean background;
	private boolean autoOpen;
	private long time;
	Context context;
	private final String TAG = "LinkTaskItem:" + this.hashCode();

	public LinkTaskItem(String id, Context context, String message, String url) {
		super(id);
		this.message = message;
		this.url = url;
		this.context = context;

	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isBackground() {
		return background;
	}

	public void setBackground(boolean background) {
		this.background = background;
	}

	public boolean isAutoOpen() {
		return autoOpen;
	}

	public void setAutoOpen(boolean autoOpen) {
		this.autoOpen = autoOpen;
	}

	@Override
	public void init() {
		setState(TaskItem.STATE_READY);
	}

	private void showNotification() {

		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification n = new Notification(R.drawable.ic_launcher, message,
				System.currentTimeMillis());
		n.flags = Notification.FLAG_AUTO_CANCEL;
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		// PendingIntent
		PendingIntent contentIntent = PendingIntent.getActivity(context,
				R.string.app_name, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		n.setLatestEventInfo(context, message, message, contentIntent);
		nm.notify(R.string.app_name, n);

	}

	@Override
	public void execute() {
		setState(TaskItem.STATE_RUNNING);
		time = System.currentTimeMillis();
		new Thread() {

			@Override
			public void run() {
				HttpURLConnection conn = null;
				if (background) {
					try {
						URL u = new URL(url);
						conn = (HttpURLConnection) u.openConnection();
						if (conn.getResponseCode() == 200) {
							setState(TaskItem.STATE_COMPLATE);
						} else {
							setState(TaskItem.STATE_ERROR);
						}
					} catch (Throwable e) {
						setState(TaskItem.STATE_ERROR);
					} finally {
						if (conn != null)
							conn.disconnect();
					}
				} else if (autoOpen) {
					Utility.openUrl(url);
					setState(TaskItem.STATE_COMPLATE);
				} else {
					showNotification();
					setState(TaskItem.STATE_COMPLATE);
				}
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
		return "link:url=" + url;
	}

}
