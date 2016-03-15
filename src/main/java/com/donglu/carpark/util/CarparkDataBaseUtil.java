package com.donglu.carpark.util;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.server.CarparkServerConfig;


public class CarparkDataBaseUtil {


    public final static Logger LOGGER = LoggerFactory.getLogger(CarparkDataBaseUtil.class);
    public final static String SQLSERVER2008 = "SQLSERVER2008";
    public final static String SQLSERVER2005 = "SQLSERVER2005";
    public final static String MYSQL = "MYSQL";

    public static boolean checkPortAvailable(String ip,String port){
        LOGGER.debug("检查主机{}端口{}是否可用",ip,port);
        try (Socket s = new Socket()){
            s.connect(new InetSocketAddress(ip, Integer.valueOf(port)), 500);
            return s.isConnected();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查数据库是否可用
     * @param ip 数据库ip
     * @param port 数据库端口
     * @param databaseName 数据库名称
     * @param username 用户名
     * @param password 密码
     * @param type 数据库类型
     * @return
     */
    public static boolean checkoutDatabaseAvailable(String ip,String port,String databaseName,String username,String password,String type){
        LOGGER.debug("检查数据库是否可用");
        String driver = getDriverStr(type);
        String url = getUrlStr(type,ip,port,databaseName);
        try(Connection connection = getConnection(driver, url, username, password)){
            return connection != null;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 执行sql语句
     * @param ip 数据库ip
     * @param port 数据库端口
     * @param databaseName 数据库名称
     * @param username 用户名
     * @param password 密码
     * @param sql 执行的sql语句
     * @param type 数据库类型
     * @return
     */
    public static boolean executeSQL(String ip,String port,String databaseName,String username,String password,String sql,String type){
        LOGGER.debug("执行数据库sql:{}",sql);

        String driver = getDriverStr(type);
        String url = getUrlStr(type, ip, port, databaseName);
        Connection conn = getConnection(driver, url, username, password);
        if(conn == null){
            return false;
        }

        try(Connection connection = conn;
            Statement statement = connection.createStatement()
        ){
            statement.execute(sql);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 获取数据库连接
     * @param driver 数据库驱动名称
     * @param url 数据库连接地址
     * @param username 用户名
     * @param password 密码
     * @return
     */
    private static Connection getConnection(String driver,String url,String username,String password) {
        LOGGER.debug("获取数据库连接: \n driver={} \n url={} \n username={} \n password={}",driver,url,username,password);
        try {
            Class.forName(driver);
            Connection connection;
            connection = DriverManager.getConnection(url, username, password);
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取数据库驱动名称
     * @param type 数据库类型
     * @return
     */
    public static String getDriverStr(String type){
        switch (type){
            case SQLSERVER2008:
                return "net.sourceforge.jtds.jdbc.Driver";
            case SQLSERVER2005:
                return "net.sourceforge.jtds.jdbc.Driver";
            case MYSQL:
                return "com.mysql.jdbc.Driver";
            default:
                throw new RuntimeException("未定义此类型的数据库：" + type);
        }
    }

    /**
     * 获取数据库连接地址
     * @param type 数据库类型
     * @param ip 数据库ip
     * @param port 数据库端口
     * @param databaseName 数据库名称
     * @return
     */
    public static String getUrlStr(String type,String ip,String port,String databaseName){
        switch (type){
            case SQLSERVER2008:
                return String.format("jdbc:jtds:sqlserver://%s:%s/%s", ip,port,databaseName);
            case SQLSERVER2005:
                return String.format("jdbc:jtds:sqlserver://%s:%s/%s", ip,port,databaseName);
            case MYSQL:
                return String.format("jdbc:mysql://%s:%s/%s", ip,port,databaseName);
            default:
                throw new RuntimeException("未定义此类型的数据库：" + type);
        }
    }

    /**
     * 生成默认创建数据库的sql语句
     * @param type 数据库类型
     * @param databaseFolder 数据库创建目录
     * @return
     */
    public static String getDefaultCreateDatabaseSql(String type,String databaseFolder){
    	String mdfFilePath = databaseFolder + "carpark.mdf";
    	String ldfFilePath = databaseFolder + "carpark.ldf";
    	switch (type) {
        case SQLSERVER2008:
            return "IF NOT EXISTS(SELECT * FROM sysDatabases WHERE name='carpark') CREATE DATABASE carpark ON PRIMARY (NAME= carpark_data, FILENAME='"+mdfFilePath+"', SIZE=10, FILEGROWTH= 10%) LOG ON (NAME=carpark_log, FILENAME='"+ldfFilePath+"', SIZE=10, FILEGROWTH= 10% )";
        case SQLSERVER2005:
            return "IF NOT EXISTS(SELECT * FROM sysDatabases WHERE name='carpark') CREATE DATABASE carpark ON PRIMARY (NAME= carpark_data, FILENAME='"+mdfFilePath+"', SIZE=10, FILEGROWTH= 10%) LOG ON (NAME=carpark_log, FILENAME='"+ldfFilePath+"', SIZE=10, FILEGROWTH= 10% )";
        case MYSQL:
            return "CREATE DATABASE IF NOT EXISTS carpark CHARACTER SET 'utf8'";
        default:
            return null;
    }
    }

    /**
     * 获取原生存在的数据库名称
     * @param type 数据库类型
     * @return
     */
    public static String getOriginalDatabaseName(String type){
        switch (type){
            case SQLSERVER2008:
                return "master";
            case SQLSERVER2005:
                return "master";
            case MYSQL:
                return "mysql";
            default:
                throw new RuntimeException("未定义此类型的数据库：" + type);
        }
    }

	public static boolean executeSQL(String sql, String databaseName, CarparkServerConfig cf) {
		
		return executeSQL(cf.getDbServerIp(), cf.getDbServerPort(), databaseName, cf.getDbServerUsername(), cf.getDbServerPassword(), sql, cf.getDbServerType());
	}


}
