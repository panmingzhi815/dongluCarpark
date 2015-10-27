package com.donglu.carpark.ui;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Properties;

public class CarparkClientConfig implements Serializable{
	
	public final String configFileName = "CarparkClientConfig.properties";
	public static CarparkClientConfig instance;

	private String dbServerIp;
	private String dbServerPort;
	private String dbServerUsername;
	private String dbServerPassword;
	
	private String dbServerType;
	
	private CarparkClientConfig(){}
	
	public static CarparkClientConfig getInstance(){
		if(instance == null){
			instance = new CarparkClientConfig();
		}
		instance.loadConfig();
		return instance;
	}
	
	public void saveConfig(){
		try (FileOutputStream fos = new FileOutputStream(configFileName, false);PrintWriter out = new PrintWriter(fos);){
			out.println(String.format("#服务器ip配置  %s", new Date()));

			out.println("#业务底层数据库ip");
			out.println(String.format("%s=%s", "db.dbServerIp", getDbServerIp()));
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
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        try (FileInputStream fileInputStream = new FileInputStream(configFileName);){
        	Properties preferenceStore = new Properties();
            preferenceStore.load(fileInputStream);
            
            this.dbServerIp = preferenceStore.getProperty("db.dbServerIp", "127.0.0.1");
        } catch (IOException e) {
            e.printStackTrace();
        } 
	}

	public String getDbServerIp() {
		return dbServerIp;
	}
	public void setDbServerIp(String dbServerIp) {
		this.dbServerIp = dbServerIp;
		saveConfig();
	}
	

	public static void main(String[] args) {
		Path parent = Paths.get(System.getProperty("user.dir")).getParent().getParent();
		System.out.println(parent.toString() + File.separator + "database" + File.separator);
	}

	public String getDbServerPort() {
		return dbServerPort;
	}

	public void setDbServerPort(String dbServerPort) {
		this.dbServerPort = dbServerPort;
	}

	public String getDbServerUsername() {
		return dbServerUsername;
	}

	public void setDbServerUsername(String dbServerUsername) {
		this.dbServerUsername = dbServerUsername;
	}

	public String getDbServerPassword() {
		return dbServerPassword;
	}

	public void setDbServerPassword(String dbServerPassword) {
		this.dbServerPassword = dbServerPassword;
	}
	public String getDbServerDriver() {
		switch (this.dbServerType) {
		case "SQLSERVER2008":
			return "net.sourceforge.jtds.jdbc.Driver";
		case "MYSQL":
			return "com.mysql.jdbc.Driver";
		default:
			return null;
		}
	}
	public String getDbServerURL() {
		switch (this.dbServerType) {
		case "SQLSERVER2008":
			return String.format("jdbc:jtds:sqlserver://%s:%s/carpark", this.dbServerIp,this.dbServerPort);
		case "MYSQL":
			return String.format("jdbc:mysql://%s:%s/carpark", this.dbServerIp,this.dbServerPort);
		default:
			return null;
		}
	}

	public String getDbServerType() {
		return dbServerType;
	}

	public void setDbServerType(String dbServerType) {
		this.dbServerType = dbServerType;
	}
}
