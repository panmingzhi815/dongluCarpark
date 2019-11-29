package com.donglu.carpark.service.background.impl;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Properties;

import lombok.Data;

@Data
public class GuiyangSendInfoConfig implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8710168087805946672L;
	
	public static final String configFileName = "GuiyangSendInfoConfig.properties";
	public static GuiyangSendInfoConfig instance;
	
	private boolean enable;
	private String url="";
	private String csId;
	private String parkAppId;
	private String key;
	
	
	private GuiyangSendInfoConfig(){}
	
	public static GuiyangSendInfoConfig getInstance(){
		if(instance == null){
			instance = new GuiyangSendInfoConfig();
		}
		instance.loadConfig();
		return instance;
	}
	
	public void saveConfig(){
		try (FileOutputStream fos = new FileOutputStream(configFileName, false);PrintWriter out = new PrintWriter(fos);){
			out.println(String.format("#贵阳车牌报送配置  %s", new Date()));
			out.println("#是否开启车牌报送");
			out.println(String.format("%s=%s", "enable", enable));
			out.println("#上传地址");
			out.println(String.format("%s=%s", "url", url));
			out.println("#服务商在平台注册的编号");
			out.println(String.format("%s=%s", "csId", csId));
			out.println("#停车场appId,由平台统一分配");
			out.println(String.format("%s=%s", "parkAppId", parkAppId));
			out.println("#加密key,由平台统一分配");
			out.println(String.format("%s=%s", "key", key));
			out.println();
			

			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadConfig() {
		Path path = Paths.get(configFileName);
		if(!Files.exists(path)){
			try {
				Files.createFile(path);
				saveConfig();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        try (FileInputStream fileInputStream = new FileInputStream(configFileName);){
        	Properties preferenceStore = new Properties();
            preferenceStore.load(fileInputStream);
            
            this.enable = Boolean.valueOf(preferenceStore.getProperty("enable", "false"));
            this.url = preferenceStore.getProperty("url", "http://127.0.0.1:8081");
            
            this.csId = preferenceStore.getProperty("csId", "");
            this.parkAppId = preferenceStore.getProperty("parkAppId", "");
            this.key = preferenceStore.getProperty("key", "");
        } catch (IOException e) {
            e.printStackTrace();
        } 
	}

	public static void main(String[] args) {
		Path parent = Paths.get(System.getProperty("user.dir")).getParent().getParent();
		System.out.println(parent.toString() + File.separator + "database" + File.separator);
	}
}
