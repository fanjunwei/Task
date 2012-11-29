package com.baoxue.task.web;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.baoxue.task.CrashApplication;
import com.baoxue.task.common.JSONHelper;
import com.baoxue.task.common.Utility;

/**
 * 
 * @author www.7manba.com
 * 
 */
public class WebService {
	private static WebService instance = null;
	private static String TAG = "WebService";
//	public static String _baseUrl = "http://www.baoxuetech.com";
//	private static final String _serviceURL = _baseUrl
//			+ "/android/device_service";
	
	public static String _baseUrl = "http://10.0.2.2:8080";
	private static final String _serviceURL = _baseUrl
			+ "/baoxue/device_service";

	public static String getBaseUrl() {
		return _baseUrl;
	}

	public static String getServiceUrl() {
		return _serviceURL;
	}

	List<HeaderProperty> resProperty = new ArrayList<HeaderProperty>();

	private WebService() {
		// 请使用getInstance实例化
	}

	public static String getImageUrl(String id) {
		return getBaseUrl() + "/wap/AndroidService.aspx/CartoonImg/" + id;
	}

	public static String getCartoonUrl(String fileid) {
		return getBaseUrl() + "/wap/Stream.aspx/WapCartoonFile/" + fileid;
	}

	public static WebService getInstance() {
		if (instance == null) {
			instance = new WebService();
		}
		return instance;
	}

	public String CallFuncJson(String actionName, Map<String, Object> parm) {
		String body = "";
		if (NetWorkEnable(CrashApplication.getCurrent()) == -1) {
			return null;
		}
		waitNetworkToConnected();
		try {
			String url = _serviceURL + "/" + actionName + ".action?version="
					+ Utility.getVersionCode() + "&deviceVersion="
					+ "F5_BXT_01" + "&deviceId=" + Utility.getDeviceId();
			// SystemProperties
			// .get("ro.custom.build.version", "unknown");
			if (parm != null) {
				body = JSONHelper.toJSON(parm);
			}
			URL u = new URL(url);
			HttpURLConnection conn = null;
			try {
				conn = (HttpURLConnection) u.openConnection();
			} catch (UnknownHostException ex) {
				conn = (HttpURLConnection) u.openConnection();
			}
			conn.setDoOutput(true);
			conn.setConnectTimeout(20000);
			conn.setReadTimeout(20000);

			for (int j = 0; j < resProperty.size(); j++) {
				HeaderProperty pro = resProperty.get(j);
				String key = pro.getKey();
				String value = pro.getValue();
				conn.setRequestProperty(key, value);
			}
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			conn.connect();
			conn.getOutputStream().write(body.getBytes());
			conn.getOutputStream().flush();
			conn.getOutputStream().close();
			int code = conn.getResponseCode();
			if (code == 200) {
				Iterator<Entry<String, List<String>>> map = conn
						.getHeaderFields().entrySet().iterator();
				while (map.hasNext()) {
					Entry<String, List<String>> item = map.next();
					String key = item.getKey();

					if (key != null && key.toLowerCase().equals("set-cookie")) {
						List<String> value = item.getValue();
						if (value != null && value.size() > 0) {
							String valueitem = value.get(0).toString();
							if (valueitem.indexOf("jsessionid") != -1) {
								HeaderProperty cookie = new HeaderProperty(
										"Cookie", valueitem);
								resProperty.add(cookie);
							}
						}
					}
				}
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(new BufferedInputStream(
								conn.getInputStream()), "utf-8"));
				StringBuilder res = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					res.append(line + "\n");
				}
				return res.toString();

			} else {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(new BufferedInputStream(
								conn.getErrorStream()), "utf-8"));
				StringBuilder res = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					res.append(line + "\n");
				}
				Log.d(TAG, res.toString());
			}

		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void readHttpData(InputStream input, byte[] data, int off,
			int length) {
		int readlength = 0;
		int pos = off;
		do {
			try {
				readlength = input.read(data, pos, length);
				length -= readlength;
				pos += readlength;
			} catch (IOException ex) {
				ex.printStackTrace();
				readlength = 0;
			}
		} while (length > 0 && readlength >= 0);
	}

	public BufferedInputStream CreateDownloadStream(String url) {
		return CreateDownloadStream(url, null);
	}

	public BufferedInputStream CreateDownloadStream(String url,
			List<HeaderProperty> header) {
		if (NetWorkEnable(CrashApplication.getCurrent()) == -1) {
			return null;
		}
		waitNetworkToConnected();
		try {
			URL u = new URL(url);
			URLConnection conn = u.openConnection();
			for (int i = 0; i < resProperty.size(); i++) {
				HeaderProperty pro = resProperty.get(i);
				String key = pro.getKey();
				String value = pro.getValue();
				conn.setRequestProperty(key, value);
			}

			if (header != null) {
				for (int i = 0; i < header.size(); i++) {
					HeaderProperty pro = header.get(i);
					String key = pro.getKey();
					String value = pro.getValue();
					conn.setRequestProperty(key, value);
				}
			}

			return new BufferedInputStream(conn.getInputStream(), 8192);

		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;

	}

	private void waitNetworkToConnected() {
		if (NetWorkEnable(CrashApplication.getCurrent()) == -1) {
			return;
		}
		while (NetWorkEnable(CrashApplication.getCurrent()) == 0) {
			try {
				URL u = new URL(getBaseUrl() + "/wap");
				URLConnection conn = u.openConnection();
				conn.getInputStream().close();
			} catch (Throwable e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static int NetWorkEnable(Context act) {

		ConnectivityManager manager = (ConnectivityManager) act
				.getApplicationContext().getSystemService(
						Context.CONNECTIVITY_SERVICE);

		if (manager == null) {
			return -1;
		}

		NetworkInfo networkinfo = manager.getActiveNetworkInfo();

		if (networkinfo == null || !networkinfo.isAvailable()) {
			return -1;
		}

		if (networkinfo.isConnected()) {
			return 1;
		} else {
			return 0;
		}
	}

	public static boolean isWifi(Context act) {
		try {
			ConnectivityManager mag = (ConnectivityManager) act
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = mag.getActiveNetworkInfo();

			int type = info.getType();
			if (type == ConnectivityManager.TYPE_WIFI) {
				return true;
			}
		} catch (Throwable t) {
		}
		return false;
	}
}
