package com.donglu.carpark.server;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Properties;

public class CarparkServerConfig implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8710168087805946672L;
	//	public enum DBType{
//		SQLSERVER2008,SQLSERVER2005,MYSQL
//	}
	private static final String MYSQL = "MYSQL";
	public static final String SQLSERVER2008 = "SQLSERVER2008";
	private static final String SQLSERVER2005 = "SQLSERVER2005";
	public static final String CARPARK = "carpark";
	public final String configFileName = "CarparkServerConfig.properties";
	public static CarparkServerConfig instance;
	
	private boolean hwPoll;
	private boolean autoStartHWServer;
	private boolean autoStartDBServer;
	private String hwDbService;

	private String dbServerType=SQLSERVER2008;
	private String dbServerIp;
	private String dbServerPort;
	private String dbServerUsername;
	private String dbServerPassword;
	
	
	private CarparkServerConfig(){}
	
	public static CarparkServerConfig getInstance(){
		if(instance == null){
			instance = new CarparkServerConfig();
		}
		instance.loadConfig();
		return instance;
	}
	
	public void saveConfig(){
		try (FileOutputStream fos = new FileOutputStream(configFileName, false);PrintWriter out = new PrintWriter(fos);){
			out.println(String.format("#底层中间件配置  %s", new Date()));
			out.println("#硬件底层配置");
			out.println();
			out.println("#硬件底层是否轮询");
			out.println(String.format("%s=%s", "hw.hwPoll", isHwPoll()));
			out.println("#硬件底层是否自动启动");
			out.println(String.format("%s=%s", "hw.autoStartHWServer", isAutoStartHWServer()));
			out.println("#硬件底层业务数据服务地址");
			out.println(String.format("%s=%s", "hw.hwDbService", getHwDbService()));
			out.println();

			out.println("#业务底层配置");
			out.println();
			out.println("#业务底层自动启动");
			out.println(String.format("%s=%s", "db.autoStartDBServer", isAutoStartDBServer()));
			out.println("#业务底层数据库类型");
			out.println(String.format("%s=%s", "db.dbServerType", getDbServerType()));
			out.println("#业务底层数据库ip");
			out.println(String.format("%s=%s", "db.dbServerIp", getDbServerIp()));
			out.println("#业务底层数据库端口");
			out.println(String.format("%s=%s", "db.dbServerPort", getDbServerPort()));
			out.println("#业务底层数据库用户名");
			out.println(String.format("%s=%s", "db.dbServerUsername", getDbServerUsername()));
			out.println("#业务底层数据库密码");
			out.println(String.format("%s=%s", "db.dbServerPassword", getDbServerPassword()));
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
            
            this.hwPoll = Boolean.valueOf(preferenceStore.getProperty("hw.hwPoll", "true"));
            this.autoStartHWServer = Boolean.valueOf(preferenceStore.getProperty("hw.autoStartHWServer", "false"));
            this.hwDbService = preferenceStore.getProperty("hw.hwDbService", "http://127.0.0.1:8889/db/");
            
            this.autoStartDBServer = Boolean.valueOf(preferenceStore.getProperty("db.autoStartDBServer", "false"));
            this.dbServerType = preferenceStore.getProperty("db.dbServerType", SQLSERVER2008);
            this.dbServerIp = preferenceStore.getProperty("db.dbServerIp", "127.0.0.1");
            this.dbServerPort = preferenceStore.getProperty("db.dbServerPort", "1433");
            this.dbServerUsername = preferenceStore.getProperty("db.dbServerUsername", "sa");
            this.dbServerPassword = preferenceStore.getProperty("db.dbServerPassword", "a123456");
        } catch (IOException e) {
            e.printStackTrace();
        } 
	}

	public boolean isHwPoll() {
		return hwPoll;
	}
	public void setHwPoll(boolean hwPoll) {
		this.hwPoll = hwPoll;
		saveConfig();
	}
	public boolean isAutoStartHWServer() {
		return autoStartHWServer;
	}
	public void setAutoStartHWServer(boolean autoStartHWServer) {
		this.autoStartHWServer = autoStartHWServer;
		saveConfig();
	}
	public boolean isAutoStartDBServer() {
		return autoStartDBServer;
	}
	public void setAutoStartDBServer(boolean autoStartDBServer) {
		this.autoStartDBServer = autoStartDBServer;
		saveConfig();
	}
	public String getDbServerIp() {
		return dbServerIp;
	}
	public void setDbServerIp(String dbServerIp) {
		this.dbServerIp = dbServerIp;
		saveConfig();
	}
	public String getDbServerPort() {
		return dbServerPort;
	}
	public void setDbServerPort(String dbServerPort) {
		this.dbServerPort = dbServerPort;
		saveConfig();
	}
	public String getDbServerUsername() {
		return dbServerUsername;
	}
	public void setDbServerUsername(String dbServerUsername) {
		this.dbServerUsername = dbServerUsername;
		saveConfig();
	}
	public String getDbServerPassword() {
		return dbServerPassword;
	}
	public void setDbServerPassword(String dbServerPassword) {
		this.dbServerPassword = dbServerPassword;
		saveConfig();
	}

	public String getHwDbService() {
		return hwDbService;
	}

	public void setHwDbService(String hwDbService) {
		this.hwDbService = hwDbService;
		saveConfig();
	}

	public String getDbServerType() {
		return dbServerType;
	}

	public void setDbServerType(String dbServerType) {
		this.dbServerType = dbServerType;
		saveConfig();
	}

	public String getDbServerDriver() {
		switch (this.dbServerType) {
		case SQLSERVER2008:
			return "net.sourceforge.jtds.jdbc.Driver";
		case SQLSERVER2005:
			return "net.sourceforge.jtds.jdbc.Driver";
		case MYSQL:
			return "com.mysql.jdbc.Driver";
		default:
			return null;
		}
	}

	public String getDbServerURL() {
		switch (this.dbServerType) {
		case SQLSERVER2008:
			return String.format("jdbc:jtds:sqlserver://%s:%s/carpark", this.dbServerIp,this.dbServerPort);
		case SQLSERVER2005:
			return String.format("jdbc:jtds:sqlserver://%s:%s/carpark", this.dbServerIp,this.dbServerPort);
		case MYSQL:
			return String.format("jdbc:mysql://%s:%s/carpark", this.dbServerIp,this.dbServerPort);
		default:
			return null;
		}
	}
	
	public String getDbDefaultServerURL() {
		switch (this.dbServerType) {
		case SQLSERVER2008:
			return String.format("jdbc:jtds:sqlserver://%s:%s/master", this.dbServerIp,this.dbServerPort);
		case MYSQL:
			return String.format("jdbc:mysql://%s:%s/mysql", this.dbServerIp,this.dbServerPort);
		default:
			return null;
		}
	}
	
	public String getDbCreateSql(String databaseFolder) {
		switch (this.dbServerType) {
		case SQLSERVER2008:
			String mdfFilePath = databaseFolder + "carpark.mdf";
			String ldfFilePath = databaseFolder + "carpark.ldf";
			return "IF NOT EXISTS(SELECT * FROM sysDatabases WHERE name='carpark') CREATE DATABASE carpark ON PRIMARY (NAME= onecard_data, FILENAME='"+mdfFilePath+"', SIZE=10, FILEGROWTH= 10%) LOG ON (NAME=onecard_log, FILENAME='"+ldfFilePath+"', SIZE=10, FILEGROWTH= 10% )";
		case MYSQL:
			return "CREATE DATABASE IF NOT EXISTS onecard CHARACTER SET 'utf8'";
		default:
			return null;
		}
	}

	public static void main(String[] args) {
		Path parent = Paths.get(System.getProperty("user.dir")).getParent().getParent();
		System.out.println(parent.toString() + File.separator + "database" + File.separator);
	}
}
