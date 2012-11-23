package com.baoxue.task.web;

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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.baoxue.task.common.Utility;
import com.baoxue.task.db.dbHelper;
import com.baoxue.task.store.storeHelper;

public class DownloadItem {
	private final String TAG = "DownloadItem";
	public final static int STATE_NONE = 0;
	public final static int STATE_DOWNLOADING = 3;
	public final static int STATE_TO_DOWNLOAD = 4;
	public final static int STATE_PAUSE = 5;
	public final static int STATE_CANCEL = 6;
	public final static int STATE_COMPLATE = 7;

	public final static int STATE_ERROR = -1;
	public final static int STATE_ERROR_NOT_FINDOUT = -2;
	public final static int STATE_ERROR_OUT_OF_STORE = -3;

	private int currState = STATE_NONE;

	private String name;
	private String URL;
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

	private void reset() {
		ETag = null;
		Length = 0;
		complateLen = 0;
		dataChanged = true;
	}

	public DownloadItem() {
	}

	public DownloadItem(Cursor c) {

	}

	public int getCurrState() {
		return currState;
	}

	private void setCurrState(int state) {
		currState = state;
		updataData();
	}

	public boolean isDataChanged() {
		return dataChanged;
	}

	public void setDataChanged(boolean dataChanged) {
		this.dataChanged = dataChanged;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	Thread loadDataThread = null;
	Object loadDataLock = new Object();

	public void Download() {
		setCurrState(STATE_DOWNLOADING);
		downloadProc();

	}

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

	public void CheckTimeOut() {
		if (System.currentTimeMillis() - newtime > TimeOut) {
			if (currState == STATE_DOWNLOADING) {
				Download();
			} else if (currState == STATE_ERROR_OUT_OF_STORE) {
				if (getSDFreeSpace() > Length) {
					Download();
				}
			}
		}
	}

	private void downloadProc() {
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
						byte[] testhead = new byte[3];
						newtime = System.currentTimeMillis();

						while ((readLength = downloadStream.read(b, 0, 5120)) > 0
								&& getComplateLen() < getLength()
								&& currState == STATE_DOWNLOADING) {
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
										&& currState == STATE_DOWNLOADING) {
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
		WebService ws = WebService.getInstance();

		setCurrState(STATE_COMPLATE);

		DownloadManage dm = DownloadManage.getDownload();
		dm.notifyDataChanged();

	}

	public void CancelDownload() {
		synchronized (DOWNLOAD_LOCK) {
			try {
				delDownlaodData();
				if (fileoutput != null) {
					fileoutput.close();
					fileoutput = null;
				}
				if (FilePath != null && !(currState == STATE_COMPLATE)) {
					File file = new File(FilePath);
					if (file.exists()) {
						file.delete();
					}
				}
				setCurrState(STATE_CANCEL);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private long lastGetSpeedTime = 0;
	private long lastGetComplateLength = 0;
	private long lastDownloadSpeed = 0;

	public String getSpeed() {
		if (currState == STATE_COMPLATE || currState == STATE_CANCEL) {
			return "";
		} else if (currState == STATE_PAUSE) {
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
		getDB().delete(DownloadManage.DATA_TABLE_NAME, "url=?",
				new String[] { URL });
		ContentValues cv = new ContentValues();
		cv.put("name", URL);
		cv.put("url", URL);
		cv.put("file_path", FilePath);
		cv.put("length", Length);
		cv.put("complate_len", complateLen);
		cv.put("curr_state", currState);
		cv.put("ETag", ETag);
		getDB().insert(DownloadManage.DATA_TABLE_NAME, null, cv);
	}

	public void updataData() {
		ContentValues cv = new ContentValues();
		cv.put("url", URL);
		cv.put("file_path", FilePath);
		cv.put("length", Length);
		cv.put("complate_len", complateLen);
		cv.put("curr_state", currState);
		getDB().update(DownloadManage.DATA_TABLE_NAME, cv, "url=?",
				new String[] { URL });
	}

	public void updataComplateLength() {
		ContentValues cv = new ContentValues();
		cv.put("complate_len", complateLen);
		getDB().update(DownloadManage.DATA_TABLE_NAME, cv, "url=?",
				new String[] { URL });
		getSpeed();
	}

	private void delDownlaodData() {
		getDB().delete(DownloadManage.DATA_TABLE_NAME, "url=?",
				new String[] { URL });
	}
}
