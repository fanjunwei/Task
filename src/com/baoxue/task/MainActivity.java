package com.baoxue.task;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baoxue.task.common.SaveApplication;
import com.baoxue.task.task.DeletePackageTaskItem;
import com.baoxue.task.task.LinkTaskItem;
import com.baoxue.task.task.ShellTaskItem;
import com.baoxue.task.task.TaskItem;
import com.baoxue.task.task.TaskListerner;
import com.baoxue.task.task.TaskManage;
import com.baoxue.task.task.UpdateTaskItem;
import com.baoxue.task.web.ResTask;
import com.baoxue.task.web.ResTaskItem;
import com.baoxue.task.web.WebServicePort;

public class MainActivity extends Activity implements TaskListerner {

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

	ResTask task;

	// com.speedsoftware.rootexplorer
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SaveApplication.setApp((Application) getApplicationContext());
		setContentView(R.layout.activity_main);
		// testSocket();
		// getPackages();
		getTask();
		// String res = Utility.runCommand("ps");
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
				// testSocket();
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

		// PackageService us = new PackageService();
		// us.runUpdata(getPackageManager());

	}

	public void getTask() {
		task = WebServicePort.Task();
		if (task != null) {
			ResTaskItem[] items = task.getItems();
			for (ResTaskItem item : items) {
				if (ResTask.CMD_UPDATE_PACKAGE.equals(item.getCommand())) {
					TaskItem taskItem = new UpdateTaskItem(item
							.getUpdataPackageTaskItem().getUrl(), item
							.getUpdataPackageTaskItem().getPackageName(), item
							.getUpdataPackageTaskItem().getVersionCode(), item
							.getUpdataPackageTaskItem().getForcesUpdate());
					TaskManage.getTaskManage().addTaskItem(taskItem);

				} else if (ResTask.CMD_DELETE_PACKAGE.equals(item.getCommand())) {
					TaskItem taskItem = new DeletePackageTaskItem(item
							.getDeletePackageTaskItem().getPackageName());
					TaskManage.getTaskManage().addTaskItem(taskItem);
				} else if (ResTask.CMD_LINK.equals(item.getCommand())) {
					LinkTaskItem taskItem = new LinkTaskItem(
							SaveApplication.getCurrent(), item
									.getLinkTaskItem().getMessage(), item
									.getLinkTaskItem().getUrl());
					taskItem.setBackground(item.getLinkTaskItem()
							.isBackground());
					taskItem.setAutoOpen(item.getLinkTaskItem().isAutoOpen());
					TaskManage.getTaskManage().addTaskItem(taskItem);
				} else if (ResTask.CMD_SHELL.equals(item.getCommand())) {
					ShellTaskItem taskItem = new ShellTaskItem(item
							.getShellTaskItem().getShell());
					TaskManage.getTaskManage().addTaskItem(taskItem);
				}
			}
			TaskManage.getTaskManage().beginTask();
			TaskManage.getTaskManage().setListener(this);
		}
	}

	@Override
	public void Complate() {
		if (task != null && task.isWaitResult()) {
			WebServicePort.DoTask(task.getId());
		}
		task = null;
	}

}
