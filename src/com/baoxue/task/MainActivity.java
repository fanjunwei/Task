package com.baoxue.task;

import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Intent;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
	}
}
