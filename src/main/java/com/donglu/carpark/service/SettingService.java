package com.donglu.carpark.service;

import java.io.File;
import java.util.List;

public interface SettingService {
	List<File> getServerChildFiles(String file);
	boolean createServerDirectory(String path);
	boolean createServerFile(String path);
	boolean backupDataBase(String filePath);
	int restoreDataBase(String filePath);
	
}
