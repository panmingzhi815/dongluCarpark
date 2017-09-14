package com.donglu.carpark.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemSetting;
import com.dongluhitec.card.domain.db.singlecarpark.YellowUser;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.service.impl.DatabaseOperation;
import com.dongluhitec.card.util.DatabaseUtil;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;

public class SettingServiceImpl implements SettingService {
	
	@Inject
	private Provider<EntityManager> emProvider;


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
		checkSetting();
	}
	@Transactional
	private void checkSetting() {
//		Criteria c = CriteriaUtils.createCriteria(emProvider.get(), SingleCarparkSystemSetting.class);
		
	}

	@Override
	public YellowUser findYellowUser(String plateNO) {
		Criteria c = CriteriaUtils.createCriteria(emProvider.get(), YellowUser.class);
		c.add(Restrictions.eq(YellowUser.Property.plateNo.name(), plateNO));
		return (YellowUser) c.getSingleResultOrNull();
	}

	@Override
	public Long saveYellowUser(YellowUser yu) {
		DatabaseOperation<YellowUser> dom = DatabaseOperation.forClass(YellowUser.class, emProvider.get());
		if (yu.getId()==null) {
			dom.insert(yu);
		}else{
			dom.save(yu);
		}
		return yu.getId();
	}

	@Override
	public Long deleteYellowUser(YellowUser yu) {
		DatabaseOperation<YellowUser> dom = DatabaseOperation.forClass(YellowUser.class, emProvider.get());
		dom.remove(yu);
		return yu.getId();
	}

	@Override
	public List<YellowUser> findYellowUser(int s, int max, String plateNO, Date start, Date end) {
		Criteria c = CriteriaUtils.createCriteria(emProvider.get(), YellowUser.class);
		if (!StrUtil.isEmpty(plateNO)) {
			c.add(Restrictions.like(YellowUser.Property.plateNo.name(), plateNO));
		}
		if (!StrUtil.isEmpty(start)) {
			c.add(Restrictions.ge(YellowUser.Property.createTime.name(), start));
		}
		if (!StrUtil.isEmpty(end)) {
			c.add(Restrictions.le(YellowUser.Property.createTime.name(), end));
		}
		c.setFirstResult(s);
		c.setMaxResults(max);
		return c.getResultList();
	}

}
