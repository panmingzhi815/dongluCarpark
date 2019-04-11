package com.donglu.carpark.service;

import java.io.File;
import java.util.Date;
import java.util.List;

import com.dongluhitec.card.domain.db.singlecarpark.UploadHistory;

public interface SettingService {
	List<File> getServerChildFiles(String file);
	boolean createServerDirectory(String path);
	boolean createServerFile(String path);
	boolean backupDataBase(String filePath);
	int restoreDataBase(String filePath);
	
	public void initCarpark();
	Date getServerDate();
	void initData();
	List<UploadHistory> findUploadHistory(int start, int max, String type,int processState);
	Long updateUploadHistory(Long id,int processState);
	Long saveUploadHistory(UploadHistory history);
	
}
