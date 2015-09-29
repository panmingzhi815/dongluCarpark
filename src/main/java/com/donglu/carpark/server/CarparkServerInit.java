package com.donglu.carpark.server;

import com.dongluhitec.card.blservice.DatabaseServiceProvider;
import com.dongluhitec.card.service.DbServiceConfigurator;
import com.dongluhitec.card.service.impl.LocalVMServiceProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

import java.io.File;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CarparkServerInit {

	public boolean checkLink() {
		CarparkServerConfig instance = CarparkServerConfig.getInstance();
		try {
			Socket s = new Socket(instance.getDbServerIp(),Integer.valueOf(instance.getDbServerPort()));
			s.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean checkDatabaseExist() {
		CarparkServerConfig instance = CarparkServerConfig.getInstance();
		String driver = instance.getDbServerDriver();
		String url = instance.getDbServerURL();
		String username = instance.getDbServerUsername();
		String password = instance.getDbServerPassword();
		
		Connection connection = getConnection(driver, url, username, password);
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

	public boolean createTable(){
		try {
			Injector injector = Guice.createInjector(new AbstractModule() {
				@Override
				protected void configure() {
					this.bindConstant().annotatedWith(Names.named("HBM2DDL")).to("create");
					bind(DbServiceConfigurator.class).toInstance(new DbServiceConfigurator());
					bind(DatabaseServiceProvider.class).to(LocalVMServiceProvider.class);
				}
			});
			DatabaseServiceProvider serviceProvider = injector.getInstance(DatabaseServiceProvider.class);
			serviceProvider.start();
			serviceProvider.getSettingService().initSystemData();
			serviceProvider.stop();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean createDatabase() {
		CarparkServerConfig instance = CarparkServerConfig.getInstance();
		try(Connection connection = getConnection(instance.getDbServerDriver(), instance.getDbDefaultServerURL(), instance.getDbServerUsername(), instance.getDbServerPassword());
			Statement statement = connection.createStatement())
		{
			String databaseFolder = Paths.get(System.getProperty("user.dir")).getParent().getParent().toString() + File.separator + "database" + File.separator;
			if(!Files.exists(Paths.get(databaseFolder))){
				Files.createDirectory(Paths.get(databaseFolder));
			}
			String createDatabaseSql = instance.getDbCreateSql(databaseFolder);
			statement.execute(createDatabaseSql);
			return true;
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}

	public Connection getConnection(String driver,String url,String username,String password) {
		try {
			Class.forName(driver);
			Connection connection = DriverManager.getConnection(url, username,password);
			return connection;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
