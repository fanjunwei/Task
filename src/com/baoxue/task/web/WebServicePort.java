package com.baoxue.task.web;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baoxue.task.common.JSONHelper;
import com.baoxue.task.common.Zip;
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

	public static ResTask DoTask(String taskId, String result) {

		if (taskId != null && !"".equals(taskId)) {
			try {
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("taskId", taskId);

				parameters.put("resultZipBase64",
						Zip.zipBase64compress(result.getBytes("utf-8")));

				String res = WebService.getInstance().CallFuncJson("do_task",
						parameters);
				ResTask taskItems = JSONHelper.parseObject(res, ResTask.class);
				return taskItems;
			} catch (UnsupportedEncodingException e) {
			}
		}
		return null;
	}
}
