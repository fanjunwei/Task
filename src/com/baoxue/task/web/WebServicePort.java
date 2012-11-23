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
		Log.d("tttt", updateinfo.getUpdatePackageNames().size() + "");
		return updateinfo;

	}

}
