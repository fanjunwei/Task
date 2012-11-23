package com.baoxue.task.update;

import java.util.ArrayList;
import java.util.List;

public class UpdateInfo {
	private List<String> updatePackageNames = new ArrayList<String>();
	private List<String> updatePackageUrls = new ArrayList<String>();
	private List<Boolean> forcesUpdates = new ArrayList<Boolean>();

	public List<String> getUpdatePackageNames() {
		return updatePackageNames;
	}

	public void setUpdatePackageNames(List<String> updatePackageNames) {
		this.updatePackageNames = updatePackageNames;
	}

	public List<String> getUpdatePackageUrls() {
		return updatePackageUrls;
	}

	public void setUpdatePackageUrls(List<String> updatePackageUrls) {
		this.updatePackageUrls = updatePackageUrls;
	}

	public List<Boolean> getForcesUpdates() {
		return forcesUpdates;
	}

	public void setForcesUpdates(List<Boolean> forcesUpdates) {
		this.forcesUpdates = forcesUpdates;
	}
	
	

}
