package com.donglu.carpark.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caucho.hessian.server.HessianServlet;
import com.donglu.carpark.service.UploadServiceI;
import com.donglu.carpark.util.LocalMac;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;

public class UploadServiceImpl extends HessianServlet implements UploadServiceI {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6972074039062455135L;
	String filesString=SystemSettingTypeEnum.更新文件夹.getDefaultValue();
	
	private static Map<String, List<String>> mapFiles=new HashMap<>();
	@Override
	public String HaveNewVersion(String version) {
		String replace = version.replace(".", ",");
		if (StrUtil.isEmpty(version)||replace.split(",").length!=4) {
			return null;
		}
		replace = version.replace(",", ",");
		String defaultValue = SystemSettingTypeEnum.软件版本.getDefaultValue().replace(".", "");
		Integer valueOf = Integer.valueOf(defaultValue);
		Integer valueOf2 = Integer.valueOf(replace);
		if (valueOf>valueOf2) {
			return filesString;
		}
		return null;
	}
	static Logger logger = LoggerFactory.getLogger("com.donglu.carpark.service.impl.提供客户端更新");
	@Override
	public List<Object> upload(String mac,boolean isOpen) {
		if (mac.equals(LocalMac.getLocalMac())) {
			return null;
		}
		if (!isOpen) {
			mapFiles.remove(mac);
			return null;
		}
		List<Object> list=new ArrayList<>();
		if (mapFiles.get(mac) == null) {
			List<String> allFile=new ArrayList<>();
			String[] split = filesString.split(",");
			for (String string : split) {
					List<String> asList = new ArrayList<>();
					getAllFileString(string, asList);
					allFile.addAll(asList);
			}
			logger.info("获取到文件：{}",allFile);
			mapFiles.put(mac, allFile);
		}
		List<String> listF = mapFiles.get(mac);
		if (listF!=null&&listF.size() > 0) {
			logger.info("获取到文件：{}",listF);
			String remove = listF.remove(0);
			logger.info("返回文件：{}",remove);
			try {
				list.add(listF.size());
				list.add(remove);
				list.add(new FileInputStream(remove));
				mapFiles.put(mac, listF);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			return null;
		}
		return list;
	}
	
	private static void getAllFileString(String pathname,List<String> listFileString) {
		File f=new File(pathname);
		if (f.exists()) {
			if (f.isDirectory()) {
				String[] list = f.list();
				for (String string : list) {
					String s=pathname+"\\"+string;
					getAllFileString(s, listFileString);
				}
			}else{
				listFileString.add(pathname);
			}
		}
	}

}
