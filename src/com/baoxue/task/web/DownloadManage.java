package com.baoxue.task.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;

import com.baoxue.task.db.dbHelper;

public class DownloadManage {

	final static int MSG_ADD_DOWNLOAD = 0;

	final static int MSG_DATA_CHANGE = 2;

	final Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_ADD_DOWNLOAD:
				DownloadReqData data = (DownloadReqData) msg.obj;
				DownloadItem it = new DownloadItem();
				it.setName(data.name);
				it.setURL(data.url);
				downloadQueue.add(it);
				datamap.put(data.url, it);
				it.addDownloadRecord();
				BeginDownload();
				break;

			case MSG_DATA_CHANGE:
				break;
			}

		}

	};

	private static DownloadManage instance = null;
	public static final String DATA_TABLE_NAME = "download_list";
	private boolean cancelDownlaodFlag = false;
	// List<DownloadItem> complateList = new ArrayList<DownloadItem>();
	List<DownloadItem> downloadingList = new ArrayList<DownloadItem>();
	Queue<DownloadItem> downloadQueue = new LinkedList<DownloadItem>();
	Map<String, DownloadItem> datamap = new HashMap<String, DownloadItem>();
	int downloadCount = 0;
	Thread downloadThread = null;
	private boolean hasRunCheck = false;
	boolean has_init_history = false;

	private DownloadManage() {

	}

	public void InitDownloadHistory() {
		if (!has_init_history) {
			has_init_history = true;
			boolean has_download_history = false;
			SQLiteDatabase db = dbHelper.getInstance().getWritableDatabase();
			Cursor c = db.rawQuery("select * from " + DATA_TABLE_NAME, null);
			try {
				while (c.moveToNext()) {
					has_download_history = true;
					DownloadItem it = new DownloadItem(c);
					downloadQueue.add(it);
					datamap.put(it.getURL(), it);
				}
			} finally {
				if (c != null)
					c.close();
			}
			if (has_download_history)
				BeginDownload();
		}
	}

	public static DownloadManage getDownload() {
		if (instance == null) {
			instance = new DownloadManage();
		}
		return instance;
	}

	public List<DownloadItem> getDownloadingList() {
		return downloadingList;
	}

	final static int MAX_DOWNLOAD_COONT = 3;

	public synchronized int AddDownloadFile(DownloadReqData data) {
		if (datamap.containsKey(data.url)) {
			return 1;
		} else {
			Message msg = mHandler.obtainMessage(MSG_ADD_DOWNLOAD, data);
			mHandler.sendMessage(msg);
			return 0;
		}
	}

	private void loop() {
		if (!hasRunCheck) {
			hasRunCheck = true;
			boolean hasDataChanged = false;
			int totalLength = 0;
			// int complateLength = 0;
			downloadCount = 0;
			for (int i = 0; i < downloadingList.size(); i++) {
				DownloadItem item = downloadingList.get(i);
				if (cancelDownlaodFlag) {
					item.CancelDownload();
					continue;
				}
				switch (item.getCurrState()) {
				case DownloadItem.STATE_COMPLATE:

					// complateList.add(item);
					downloadingList.remove(item);
					i--;
					notifyDataChanged();
					break;
				case DownloadItem.STATE_CANCEL:
					String itemID = item.getURL();
					datamap.remove(itemID);
					downloadingList.remove(item);
					i--;
					notifyDataChanged();
					hasDataChanged = true;
					// lastTime = 0;
					break;
				case DownloadItem.STATE_DOWNLOADING:
					downloadCount++;
					totalLength += item.getLength();
					// complateLength += item.getComplateLen();
					if (item.isDataChanged()) {
						item.setDataChanged(false);
						hasDataChanged = true;
						item.updataComplateLength();
					}
					item.CheckTimeOut();
					break;
				}

			}

			if (downloadCount < MAX_DOWNLOAD_COONT) {
				for (int i = 0; i < downloadingList.size(); i++) {

					DownloadItem item = downloadingList.get(i);
					if (item.getCurrState() == DownloadItem.STATE_NONE
							|| item.getCurrState() == DownloadItem.STATE_TO_DOWNLOAD) {
						item.Download();
						downloadCount++;
					}
					if (downloadCount >= MAX_DOWNLOAD_COONT) {
						break;
					}
				}
			}

			while (downloadCount < MAX_DOWNLOAD_COONT
					&& downloadQueue.size() > 0) {

				DownloadItem item = downloadQueue.remove();

				if (item.getCurrState() == DownloadItem.STATE_NONE
						|| item.getCurrState() == DownloadItem.STATE_TO_DOWNLOAD) {
					item.Download();
					downloadingList.add(item);
					downloadCount++;
				}
			}

			cancelDownlaodFlag = false;
			if (hasDataChanged) {
				notifyDataChanged();
			}

			hasRunCheck = false;
		}
	}

	public void BeginDownload() {
		if (downloadThread == null) {
			downloadThread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						while (downloadingList.size() > 0
								|| downloadQueue.size() > 0
						) {
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
						downloadThread = null;
					}
				}
			});
			downloadThread.start();
		}
	}

	public void CancelDownlaod() {
		cancelDownlaodFlag = true;
	}


	public void notifyDataChanged() {
		mHandler.sendEmptyMessage(MSG_DATA_CHANGE);
	}
}
