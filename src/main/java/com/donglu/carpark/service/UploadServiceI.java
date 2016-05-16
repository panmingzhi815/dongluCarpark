package com.donglu.carpark.service;

import java.util.List;

public interface UploadServiceI {
	public String HaveNewVersion(String version);
	public List<Object> upload(String mac,boolean isOpen);
}
