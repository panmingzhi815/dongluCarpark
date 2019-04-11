package com.donglu.carpark.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.criteria4jpa.Criteria;
import org.criteria4jpa.CriteriaUtils;
import org.criteria4jpa.criterion.Restrictions;

import com.donglu.carpark.server.CarparkServerConfig;
import com.donglu.carpark.service.SettingService;
import com.donglu.carpark.util.CarparkUtils;
import com.dongluhitec.card.domain.db.singlecarpark.DeviceVoiceTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDeviceVoice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemSetting;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.UploadHistory;
import com.dongluhitec.card.service.impl.DatabaseOperation;
import com.dongluhitec.card.util.DatabaseUtil;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.google.inject.persist.UnitOfWork;

public class SettingServiceImpl implements SettingService {
	
	@Inject
	private Provider<EntityManager> emProvider;
	@Inject
	private UnitOfWork unitOfWork;

	@Override
	public List<File> getServerChildFiles(String fileName) {
		List<File> list=new ArrayList<>();
		if (fileName==null) {
			File[] listRoots = File.listRoots();
			for (File file2 : listRoots) {
				list.add(file2);
			}
		}else{
			File file = new File(fileName);
			if (file.isDirectory()) {
				File[] listFiles = file.listFiles();
				if (listFiles!=null) {
					for (File file2 : listFiles) {
						if (!file2.isDirectory()) {
							if (file2.toString().indexOf(".bak")<=-1) {
								continue;
							}
						}
						list.add(file2);
					}
				}
			}
		}
		list.sort(new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				if (o1.isDirectory()&&!o2.isDirectory()) {
					return -1;
				}else if(!o1.isDirectory()&&o2.isDirectory()){
					return 1;
				}
				return o1.toString().compareTo(o2.toString());
			}
		});
		return list;
	}

	@Override
	public boolean backupDataBase(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			try {
				Files.createParentDirs(file);
				file.createNewFile();
			} catch (IOException e) {
				return false;
			}
		}
		CarparkServerConfig ccc = CarparkServerConfig.getInstance();
		boolean executeSQL = CarparkUtils.backupDateBase(filePath, ccc.getDbServerIp(), ccc.getDbServerPort(), ccc.getDbServerUsername(), ccc.getDbServerPassword());
		return executeSQL;
	}

	@Override
	public int restoreDataBase(String filePath) {
		File f = new File(filePath);
		if (!f.exists()) {
			return 0;
		}
		CarparkServerConfig ccc = CarparkServerConfig.getInstance();
		String onlineSql = "ALTER DATABASE carpark SET ONLINE WITH ROLLBACK IMMEDIATE";
		String restoreSql = "USE master ALTER DATABASE carpark SET OFFLINE WITH ROLLBACK IMMEDIATE;RESTORE DATABASE carpark FROM disk = '" + filePath + "' WITH REPLACE;";
		boolean executeSQL = DatabaseUtil.executeSQL(ccc.getDbServerIp(), ccc.getDbServerPort(), "master", ccc.getDbServerUsername(), ccc.getDbServerPassword(), restoreSql, "SQLSERVER 2008");
		boolean executeSQL2 = DatabaseUtil.executeSQL(ccc.getDbServerIp(), ccc.getDbServerPort(), "master", ccc.getDbServerUsername(), ccc.getDbServerPassword(), onlineSql, "SQLSERVER 2008");
		if (!executeSQL && !executeSQL2) {
			if (!executeSQL) {
				return 1;
			}
			if (!executeSQL2) {
				return 2;
			}
		}
		return 99;
	}

	@Override
	public boolean createServerDirectory(String path) {
		File file = new File(path);
		if (!file.exists()) {
			try {
				Files.createParentDirs(file);
			} catch (IOException e) {
				return false;
			}
			file.mkdirs();
		}
		return true;
	}

	@Override
	public boolean createServerFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			try {
				Files.createParentDirs(file);
			} catch (IOException e) {
				return false;
			}
			try {
				file.createNewFile();
			} catch (IOException e) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void initCarpark() {
		
	}
	private void checkSetting() {
		Criteria c = CriteriaUtils.createCriteria(emProvider.get(), SingleCarparkSystemSetting.class);
		List<SingleCarparkSystemSetting> resultList = c.getResultList();
		List<SystemSettingTypeEnum> list = new ArrayList<>();
		for (SystemSettingTypeEnum systemSettingTypeEnum2 : SystemSettingTypeEnum.values()) {
			list.add(systemSettingTypeEnum2);
		}
		for (SingleCarparkSystemSetting singleCarparkSystemSetting : resultList) {
			try {
				SystemSettingTypeEnum valueOf = SystemSettingTypeEnum.valueOf(singleCarparkSystemSetting.getSettingKey());
				list.remove(list.indexOf(valueOf));
			} catch (Exception e) {
				
			}
		}
		DatabaseOperation<SingleCarparkSystemSetting> dom = DatabaseOperation.forClass(SingleCarparkSystemSetting.class, emProvider.get());
		
		for (SystemSettingTypeEnum systemSettingTypeEnum : list) {
			SingleCarparkSystemSetting ss=new SingleCarparkSystemSetting();
			ss.setSettingKey(systemSettingTypeEnum.name());
			ss.setSettingValue(systemSettingTypeEnum.getDefaultValue());
			dom.insert(ss);
		}
	}

	@Override
	public Date getServerDate() {
		return new Date();
	}

	@Transactional
	@Override
	public void initData() {
		checkSetting();
		checkVoice();	
	}

	private void checkVoice() {
		Criteria c = CriteriaUtils.createCriteria(emProvider.get(), SingleCarparkDeviceVoice.class);
		c.add(Restrictions.in(SingleCarparkDeviceVoice.Property.type.name(), DeviceVoiceTypeEnum.values()));
		List<SingleCarparkDeviceVoice> resultList = c.getResultList();
		List<DeviceVoiceTypeEnum> list = new ArrayList<>();
		for (DeviceVoiceTypeEnum deviceVoiceTypeEnum2 : DeviceVoiceTypeEnum.values()) {
			list.add(deviceVoiceTypeEnum2);
		}
		for (SingleCarparkDeviceVoice singleCarparkDeviceVoice : resultList) {
			list.remove(singleCarparkDeviceVoice.getType());
		}
		DatabaseOperation<SingleCarparkDeviceVoice> dom = DatabaseOperation.forClass(SingleCarparkDeviceVoice.class, emProvider.get());
		for (DeviceVoiceTypeEnum deviceVoiceTypeEnum : list) {
			SingleCarparkDeviceVoice dv=new SingleCarparkDeviceVoice();
			dv.setType(deviceVoiceTypeEnum);
			dv.setContent(deviceVoiceTypeEnum.getContent());
			dv.setVolume(deviceVoiceTypeEnum.getVolume());
			dom.insert(dv);
		}
	}

	@Override
	public List<UploadHistory> findUploadHistory(int start, int max, String type, int processState) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emProvider.get(), UploadHistory.class);
			c.add(Restrictions.eq("processState", processState));
			c.add(Restrictions.eq("type", type));
			c.setFirstResult(start);
			c.setMaxResults(max);
			return c.getResultList();
		} finally {
			unitOfWork.end();
		}
	}
	
	@Transactional
	@Override
	public Long updateUploadHistory(Long id, int processState) {
		UploadHistory history = emProvider.get().getReference(UploadHistory.class, id);
		history.setProcessState(processState);
		return id;
	}
	
	@Transactional
	@Override
	public Long saveUploadHistory(UploadHistory history) {
		DatabaseOperation<UploadHistory> dom = DatabaseOperation.forClass(UploadHistory.class, emProvider.get());
		if (history.getId()==null) {
			dom.insert(history);
		}else {
			dom.save(history);
		}
		return history.getId();
	}

}
