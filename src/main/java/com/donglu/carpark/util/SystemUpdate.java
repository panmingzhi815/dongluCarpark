package com.donglu.carpark.util;

import com.donglu.carpark.server.CarparkServerConfig;
import com.dongluhitec.card.service.database.SQLParser;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class SystemUpdate{

	public Connection getconnConnection(CarparkServerConfig carparkServerConfig) {
		String url = carparkServerConfig.getDbServerURL();
		String driver = carparkServerConfig.getDbServerDriver();
		String user = carparkServerConfig.getDbServerUsername();
		String password = carparkServerConfig.getDbServerPassword();
		
		try {
			Class.forName(driver);
			return DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void close(Connection connection, Statement statement, ResultSet resultSet) {
		if(connection != null){
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}	
		if(statement != null){
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(resultSet != null){
			try {
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public String getDatabaseVersion() {
		String result = "";
		String sql = "select settingValue from UserSetting where settingKey='Database_Version'";
		
		Connection connection = getconnConnection(CarparkServerConfig.getInstance());
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			resultSet.next();
			result = resultSet.getString(1);
		} catch (SQLException e) {
			System.out.println("select database version error ************");
			System.out.println(e.getMessage());
			System.out.println("select database version error ************");
		}finally{
			close(connection, statement, resultSet);
		}
		return result;
	}

	public boolean systemUpdate(String databaseVersion, String clientVersion) {
		boolean result = false;
		
		String folderPath = System.getProperty("user.dir") + "/sqlScript/"+CarparkServerConfig.getInstance().getDbServerType()+"/";
//		String folderPath = this.getClass().getClassLoader().getResource("sqlScript/"+getAppConfigurator().getDatabasePersistenceName() + "/").getPath();
		System.out.println(folderPath);
		List<String> allSqlString = new ArrayList<String>();
		List<String> versionFiles = getUpdateVersionFileList(folderPath, databaseVersion, clientVersion);
		for (String fileName : versionFiles) {
			SQLParser parser = new SQLParser();
			allSqlString.addAll(parser.createQueries(folderPath+fileName));
		}
		
		if(allSqlString.isEmpty()){
			System.out.println("systemUpdate database version info ************");
			System.out.println("any update sql had be execute......");
			System.out.println("systemUpdate database version info ************");
			return true;
		}
		
		Connection connection = null;
		Statement statement = null;
		
		try {
			connection = getconnConnection(CarparkServerConfig.getInstance());
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			for (String sql : allSqlString) {
				statement.addBatch(sql);
                System.out.println(sql);
            }
			statement.executeBatch();
			connection.commit();
			result = true;
		} catch (Exception e) {
			try {connection.rollback();} catch (SQLException e1) {}
			System.out.println("system update error *******");
			System.out.println(e.getMessage());
			System.out.println("system update error *******");
		}finally{
			close(connection, statement, null);
		}
		return result;
	}
	
	public List<String> getUpdateVersionFileList(String folderPath,String databaseVersion, String clientVersion){
		File file = new File(folderPath);
		
		if(!file.exists()){
			file.mkdirs();
		}
		File[] listFiles = file.listFiles();
		List<String> fileNameList = new ArrayList<String>();
		for (File f : listFiles) {
			fileNameList.add(f.getName());
		}
		
		Collections.sort(fileNameList,new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		int indexOf = fileNameList.indexOf(databaseVersion + ".sql");
		int indexOf2 = fileNameList.indexOf(clientVersion + ".sql");
		if(indexOf <0 || indexOf2 < 0){
			return Collections.emptyList();
		}
		return fileNameList.subList(indexOf + 1, indexOf2 + 1);
	}

}
