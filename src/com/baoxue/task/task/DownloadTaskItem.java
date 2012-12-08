package com.baoxue.task.task;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.baoxue.task.common.Utility;
import com.baoxue.task.db.dbHelper;
import com.baoxue.task.store.storeHelper;

public abstract class DownloadTaskItem extends TaskItem {
	private final String TAG = "DownloadTaskItem";

	public final static int STATE_DOWNLOADING = 0x0300;
	public final static int STATE_DOWNLOAD_PAUSE = 0x0500;
	public final static int STATE_DOWNLOAD_COMPLATE = 0x0700;

	public final static int STATE_DOWNLOAD_ERROR_NOT_FINDOUT = -20;
	public final static int STATE_DOWNLOAD_ERROR_OUT_OF_STORE = -30;

	private String ETag = null;
	private String FilePath = null;
	private int Length;
	private int complateLen;
	private boolean dataChanged = false;
	private long newtime;
	private Thread mainThread;
	private static final Object DOWNLOAD_LOCK = new Object();
	private final static int TimeOut = 60000;
	RandomAccessFile fileoutput = null;

	private String url;
	private String name;

	private void reset() {
		ETag = null;
		Length = 0;
		complateLen = 0;
		dataChanged = true;
	}

	public DownloadTaskItem(String id, String url, String name) {
		super(id);
		this.url = url;
		this.name = name;

	}

	protected abstract void onDownloadComplate();

	public boolean isDataChanged() {
		return dataChanged;
	}

	public void setDataChanged(boolean dataChanged) {
		this.dataChanged = dataChanged;
	}

	public String getURL() {
		return url;
	}

	public String getFilePath() {
		return FilePath;
	}

	public int getLength() {
		return Length;
	}

	public int getComplateLen() {
		return complateLen;
	}

	private long getSDFreeSpace() {
		File path = Environment.getExternalStorageDirectory(); // 取得sdcard文件路径
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	Thread loadDataThread = null;
	Object loadDataLock = new Object();

	private RandomAccessFile openfile() {
		if (fileoutput == null) {
			try {
				if (FilePath == null)
					FilePath = storeHelper.getStoreDir() + "/" + name;
				File file = new File(FilePath);
				if (!file.exists()) {
					file.createNewFile();
				}
				fileoutput = new RandomAccessFile(FilePath, "rw");
				updataData();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fileoutput;
	}

	@Override
	public void checkTimeout() {
		if (System.currentTimeMillis() - newtime > TimeOut) {
			if (getState() == STATE_DOWNLOADING) {
				init();
			} else if (getState() == STATE_DOWNLOAD_ERROR_OUT_OF_STORE) {
				if (getSDFreeSpace() > Length) {
					init();
				}
			}
		}

	}

	protected void downloadProc() {
		setState(STATE_DOWNLOADING);
		new Thread() {
			public void run() {
				newtime = System.currentTimeMillis();
				BufferedInputStream downloadStream = null;
				RandomAccessFile output = null;
				try {
					mainThread = this;
					URL u = new URL(getURL());
					HttpURLConnection conn = null;
					try {
						conn = (HttpURLConnection) u.openConnection();
					} catch (UnknownHostException ex) {
						conn = (HttpURLConnection) u.openConnection();
					}
					conn.setDoOutput(true);
					conn.setConnectTimeout(20000);
					conn.setReadTimeout(20000);
					if (getLength() == 0) {
						complateLen = 0;
					}
					if (getComplateLen() > 0 && getLength() > 0) {
						conn.setRequestProperty("RANGE", "bytes="
								+ getComplateLen() + "-");
					} else {
						reset();
					}

					conn.setRequestMethod("GET");
					conn.connect();
					int code = conn.getResponseCode();
					if (code == 200 || code == 206) {
						newtime = System.currentTimeMillis();
						Iterator<Entry<String, List<String>>> map = conn
								.getHeaderFields().entrySet().iterator();
						while (map.hasNext()) {
							Entry<String, List<String>> item = map.next();
							String key = item.getKey();
							if (key != null) {
								if (key.toLowerCase().equals(
										"Content-Length".toLowerCase())) {
									List<String> value = item.getValue();
									if (value != null && value.size() > 0) {
										int valueitem = Integer.parseInt(value
												.get(0).toString());
										if (Length == 0) {
											Length = valueitem;
										} else if (Length != valueitem) {
											reset();
											conn.disconnect();
											newtime = 0;
											return;

										}
									}
								} else if (key.toLowerCase().equals(
										"ETag".toLowerCase())) {
									List<String> value = item.getValue();
									if (value != null && value.size() > 0) {
										String valueitem = value.get(0)
												.toString();
										if (ETag == null) {
											ETag = valueitem;
										} else if (!ETag.equals(valueitem)) {
											reset();
											conn.disconnect();
											newtime = 0;
											return;
										}
									}
								}
							}
						}
						downloadStream = new BufferedInputStream(
								conn.getInputStream(), 8192);

					} else {
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(new BufferedInputStream(
										conn.getErrorStream()), "utf-8"));
						StringBuilder res = new StringBuilder();
						String line;
						while ((line = reader.readLine()) != null) {
							res.append(line + "\n");
						}
						Log.e(TAG, res.toString());

						return;
					}

					// *****************************************

					if (downloadStream != null) {
						int readLength;
						byte[] b = new byte[8192];
						long currProcess = 0;
						long oldProcess = 0;

						newtime = System.currentTimeMillis();

						while ((readLength = downloadStream.read(b, 0, 5120)) > 0
								&& getComplateLen() < getLength()
								&& DownloadTaskItem.this.getState() == STATE_DOWNLOADING) {
							synchronized (DOWNLOAD_LOCK) {
								if (mainThread != this) {
									downloadStream.close();
									conn.disconnect();
									return; // 线程已超时，无需继续下载，由新线程接管下载
								}
								output = openfile();
								output.seek(complateLen);
								output.setLength(complateLen);
								if (output != null
										&& DownloadTaskItem.this.getState() == STATE_DOWNLOADING) {
									output.write(b, 0, readLength);
									complateLen += readLength;
									currProcess = complateLen * 100 / Length;
									updataComplateLength();
									if (currProcess > oldProcess) {
										oldProcess = currProcess;
										setDataChanged(true);
									}
								}
								newtime = System.currentTimeMillis();
							}
						}
						b = null;
						if (complateLen >= Length) {
							if (output != null) {
								output.close();
							}
							downloadStream.close();
							conn.disconnect();
							downloadComplate();
						} else {
							newtime = 0;// 开始新线程
						}
					}
				} catch (IOException ex) {
					newtime = 0;
				} catch (NullPointerException ex) {
					newtime = 0;
				} finally {
					if (downloadStream != null) {
						try {
							downloadStream.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}.start();
	}

	private void downloadComplate() {
		Log.d(TAG, "downloadComplate,name=" + name + "speed=" + getSpeed());
		delDownlaodData();

		setState(STATE_DOWNLOAD_COMPLATE);
		onDownloadComplate();

	}

	@Override
	public void cancel() {
		synchronized (DOWNLOAD_LOCK) {
			try {
				delDownlaodData();
				if (fileoutput != null) {
					fileoutput.close();
					fileoutput = null;
				}
				if (FilePath != null
						&& !(getState() == STATE_DOWNLOAD_COMPLATE)) {
					File file = new File(FilePath);
					if (file.exists()) {
						file.delete();
					}
				}
				setState(STATE_CANCEL);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	private long lastGetSpeedTime = 0;
	private long lastGetComplateLength = 0;
	private long lastDownloadSpeed = 0;

	public String getSpeed() {
		if (getState() == STATE_DOWNLOAD_COMPLATE || getState() == STATE_CANCEL) {
			return "";
		} else if (getState() == STATE_DOWNLOAD_PAUSE) {
			return Utility.formatLength(lastDownloadSpeed) + "/s";
		} else if (lastGetSpeedTime == 0) {
			lastGetSpeedTime = System.currentTimeMillis();
			lastGetComplateLength = complateLen;
			return "";
		} else {
			long currTime = System.currentTimeMillis();
			long diffTime = (currTime - lastGetSpeedTime) / 1000;
			long diffLen = complateLen - lastGetComplateLength;
			if ((diffTime >= 1 && diffLen > 0) || diffTime >= 5) {
				lastDownloadSpeed = diffLen / diffTime;
				lastGetSpeedTime = System.currentTimeMillis();
				lastGetComplateLength = complateLen;
			}
			return Utility.formatLength(lastDownloadSpeed) + "/s";
		}
	}

	private SQLiteDatabase db = null;

	private SQLiteDatabase getDB() {
		if (db == null) {
			db = dbHelper.getInstance().getWritableDatabase();
		}
		return db;
	}

	public void addDownloadRecord() {
		getDB().delete(TaskManage.DATA_TABLE_NAME, "url=?",
				new String[] { getURL() });
		ContentValues cv = new ContentValues();
		cv.put("name", getURL());
		cv.put("url", getURL());
		cv.put("file_path", FilePath);
		cv.put("length", Length);
		cv.put("complate_len", complateLen);
		cv.put("curr_state", getState());
		cv.put("ETag", ETag);
		getDB().insert(TaskManage.DATA_TABLE_NAME, null, cv);
	}

	public void updataData() {
		ContentValues cv = new ContentValues();
		cv.put("url", getURL());
		cv.put("file_path", FilePath);
		cv.put("length", Length);
		cv.put("complate_len", complateLen);
		cv.put("curr_state", getState());
		getDB().update(TaskManage.DATA_TABLE_NAME, cv, "url=?",
				new String[] { getURL() });
	}

	public void updataComplateLength() {
		ContentValues cv = new ContentValues();
		cv.put("complate_len", complateLen);
		getDB().update(TaskManage.DATA_TABLE_NAME, cv, "url=?",
				new String[] { getURL() });
		getSpeed();
	}

	private void delDownlaodData() {
		getDB().delete(TaskManage.DATA_TABLE_NAME, "url=?",
				new String[] { getURL() });
	}

	@Override
	public void init() {
		downloadProc();
	}
}
