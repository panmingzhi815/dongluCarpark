package com.donglu.carpark.service;

import java.io.File;
import java.util.Date;
import java.util.List;

import com.dongluhitec.card.domain.db.singlecarpark.YellowUser;

public interface SettingService {
	List<File> getServerChildFiles(String file);
	boolean createServerDirectory(String path);
	boolean createServerFile(String path);
	boolean backupDataBase(String filePath);
	int restoreDataBase(String filePath);
	
	public void initCarpark();
	
	YellowUser findYellowUser(String plateNO);
	Long saveYellowUser(YellowUser yu);
	Long deleteYellowUser(YellowUser yu);
	
	List<YellowUser> findYellowUser(int s,int max,String plateNO,Date start,Date end);
}
