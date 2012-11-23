package com.baoxue.task;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baoxue.task.update.AppInfo;
import com.baoxue.task.update.UpdateInfo;
import com.baoxue.task.web.DownloadManage;
import com.baoxue.task.web.DownloadReqData;
import com.baoxue.task.web.WebServicePort;

public class MainActivity extends Activity {

	private final static String ACTION_INSTALL = "baoxue.action.INSTALL_PACKAGES";
	private final static String ACTION_DELETE = "baoxue.action.DELETE_PACKAGES";
	private final static String EXTRA_SHOW_DIALOG = "show_dialog"; // boolean
	private final static String EXTRA_PATH = "path"; // stirng
	private final static String EXTRA_PACKAGE_NAME = "packagename"; // stirng

	Button btn_install_ui;
	Button btn_install_no_ui;

	Button btn_uninstall_ui;
	Button btn_uninstall_no_ui;
	Button send;
	Button packages;

	TextView text;

	// com.speedsoftware.rootexplorer
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btn_install_ui = (Button) findViewById(R.id.btn_install_ui);
		btn_install_no_ui = (Button) findViewById(R.id.btn_install_no_ui);

		btn_uninstall_ui = (Button) findViewById(R.id.btn_uninstall_ui);
		btn_uninstall_no_ui = (Button) findViewById(R.id.btn_uninstall_no_ui);

		send = (Button) findViewById(R.id.send);
		packages = (Button) findViewById(R.id.packages);

		text = (TextView) findViewById(R.id.text);

		btn_install_ui.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(ACTION_INSTALL);
				i.putExtra(EXTRA_SHOW_DIALOG, true);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.putExtra(EXTRA_PATH, "/sdcard/root_explorer.apk");
				sendBroadcast(i);
			}
		});
		btn_install_no_ui.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(ACTION_INSTALL);
				i.putExtra(EXTRA_SHOW_DIALOG, false);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.putExtra(EXTRA_PATH, "/sdcard/root_explorer.apk");
				sendBroadcast(i);

			}
		});

		btn_uninstall_ui.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(ACTION_DELETE);
				i.putExtra(EXTRA_SHOW_DIALOG, true);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.putExtra(EXTRA_PACKAGE_NAME, "com.youba.WeatherForecast");
				sendBroadcast(i);
			}
		});
		btn_uninstall_no_ui.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(ACTION_DELETE);
				i.putExtra(EXTRA_SHOW_DIALOG, false);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.putExtra(EXTRA_PACKAGE_NAME, "com.youba.WeatherForecast");
				sendBroadcast(i);

			}
		});
		send.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				LocalSocket s = null;
				LocalSocketAddress l;
				s = new LocalSocket();
				l = new LocalSocketAddress("task",
						LocalSocketAddress.Namespace.RESERVED);
				try {
					s.connect(l);
					OutputStream out = s.getOutputStream();
					String str = "my data";
					byte[] b = str.getBytes();
					out.write((int) b.length);
					out.write(b);
					out.close();

				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (s != null)
							s.close();
					} catch (IOException e) {
					}
				}

			}
		});

		packages.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				getPackages();

			}
		});
	}

	public void getPackages() {
		List<String> sbPackages = new ArrayList<String>();
		List<Integer> sbVersions = new ArrayList<Integer>();
		Map<String, AppInfo> apps = new HashMap<String, AppInfo>();
		PackageManager packageManager = getPackageManager();

		List<PackageInfo> list = packageManager.getInstalledPackages(0);

		for (PackageInfo packageInfo : list) {

			ApplicationInfo applicationInfo = packageInfo.applicationInfo;
			AppInfo ai = new AppInfo();
			sbPackages.add(packageInfo.packageName);
			sbVersions.add(packageInfo.versionCode);

			ai.setPackageName(packageInfo.packageName);
			ai.setVersionCode(packageInfo.versionCode);
			ai.setVersionName(packageInfo.versionName);
			ai.setDataDir(applicationInfo.dataDir);
			ai.setApkPath(applicationInfo.publicSourceDir);
			ai.setUid(applicationInfo.uid);

			apps.put(packageInfo.packageName, ai);

		}

		UpdateInfo info = WebServicePort.Update(sbPackages, sbVersions);
		if (info != null && info.getUpdatePackageUrls() != null
				&& info.getUpdatePackageUrls().size() > 0) {
			for (int i = 0; i < info.getUpdatePackageUrls().size(); i++) {
				DownloadReqData req = new DownloadReqData();
				req.name = info.getUpdatePackageNames().get(i) + ".apk";
				req.url = info.getUpdatePackageUrls().get(i);
				DownloadManage.getDownload().AddDownloadFile(req);
			}
		}

	}
}
