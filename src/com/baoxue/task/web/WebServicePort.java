package com.baoxue.task.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.baoxue.task.common.JSONHelper;
import com.baoxue.task.update.UpdateInfo;

public class WebServicePort {

	public static UpdateInfo Update(List<String> packages,
			List<Integer> versionCodes) {

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("packages", packages);
		parameters.put("versionCodes", versionCodes);
		String res = WebService.getInstance()
				.CallFuncJson("updata", parameters);
		UpdateInfo updateinfo = JSONHelper.parseObject(res, UpdateInfo.class);
		return updateinfo;

	}

	public static ResTask Task() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		String res = WebService.getInstance().CallFuncJson("task", parameters);
		ResTask taskItems = JSONHelper.parseObject(res, ResTask.class);
		return taskItems;

	}

	public static ResTask DoTask(String taskId) {

		if (taskId != null && !"".equals(taskId)) {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("taskId", taskId);
			String res = WebService.getInstance().CallFuncJson("do_task",
					parameters);
			ResTask taskItems = JSONHelper.parseObject(res, ResTask.class);
			return taskItems;
		} else {
			return null;
		}

	}
}
