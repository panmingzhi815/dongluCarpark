package com.donglu.carpark.server;

import com.google.common.base.Preconditions;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: wudong
 * Date: 11/08/13
 * Time: 21:16
 * To change this template use File | Settings | File Templates.
 */
public class CarparkDbServiceConfigurator {
	private static Logger LOGGER = LoggerFactory.getLogger(CarparkDbServiceConfigurator.class);
	
    public static final String DbServiceConfiguratorName = "CarparkServerConfig.properties";

    public static final String KEY_DB_PERSISTENCE = "db.databaseType";
    public static final String KEY_IP_PERSISTENCE = "db.databaseIp";
    public static final String KEY_PORT_PERSISTENCE = "db.databasePort";
    public static final String KEY_DB_USER = "db.databaseUsername";
    public static final String KEY_DB_PASSWORD = "db.databasePassword";


    private Properties preferenceStore = new Properties();

    public CarparkDbServiceConfigurator() {
        load(DbServiceConfiguratorName);
    }

    public boolean load(String file) {
        this.preferenceStore.clear();

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            preferenceStore.load(fileInputStream);
        } catch (IOException e) {
        	LOGGER.debug("未找到配置文件{}",file);
            return false;
        } finally {
            if (fileInputStream != null)
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                }
        }
        return true;
    }

    public String getDatabasePersistenceName() {
        String property = preferenceStore.getProperty(KEY_DB_PERSISTENCE);
        Preconditions.checkState(property != null, "No DB Persistence name is set!");
        return property;
    }
    
    public String getDatabaseIP() {
        String property = preferenceStore.getProperty(KEY_IP_PERSISTENCE);
        Preconditions.checkState(property != null, "No DB IP name is set!");
        return property;
    }
    
    public String getDatabasePORT() {
        String property = preferenceStore.getProperty(KEY_PORT_PERSISTENCE);
        Preconditions.checkState(property != null, "No DB PORT name is set!");
        return property;
    }

    public String getDatabaseURL() {
    	switch (getDatabasePersistenceName()) {
		case "SQLSERVER2008":
			return String.format("jdbc:jtds:sqlserver://%s:%s/onecard", getDatabaseIP(),getDatabasePORT());
		case "MYSQL":
			return String.format("jdbc:mysql://%s:%s/onecard", getDatabaseIP(),getDatabasePORT());
		default:
			return null;
		}
    }

    public String getDatabaseDriver() {
    	switch (getDatabasePersistenceName()) {
		case "SQLSERVER2008":
			return "net.sourceforge.jtds.jdbc.Driver";
		case "MYSQL":
			return "com.mysql.jdbc.Driver";
		default:
			return null;
		}
    }


    public String getDatabaseUser() {
        String property = preferenceStore.getProperty(KEY_DB_USER);
        return property;
    }

    public String getDatabasePassword() {
        String property = preferenceStore.getProperty(KEY_DB_PASSWORD);
        return property;
    }

}
