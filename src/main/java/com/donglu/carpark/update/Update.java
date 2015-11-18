package com.donglu.carpark.update;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.donglu.carpark.server.CarparkServerConfig;
import com.dongluhitec.card.softupdate.AbstractUpdateMainUI;
import com.dongluhitec.card.util.DatabaseUtil;

import javafx.application.Application;
import javafx.stage.Stage;

public class Update extends AbstractUpdateMainUI {
	public static void main(String[] args) {
		Application.launch(Update.class);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		super.start(primaryStage);
		CarparkServerConfig instance = CarparkServerConfig.getInstance();
		controller.setDatabaseIp(instance.getDbServerIp());
		controller.setDatabaseName("carpark");
		controller.setDatabasePassword(instance.getDbServerPassword());
		controller.setDatabasePort(instance.getDbServerPort());;
		controller.setDatabaseUsername(instance.getDbServerUsername());
		controller.setDatabaseType(DatabaseUtil.SQLSERVER2008);
		List<String> list=new ArrayList<>();
		list.add("CarparkServerConfig.properties");
		//复制程序使用文件
		File file = new File("temp");
		if (file.isDirectory()) {
			String[] list2 = file.list();
			for (String string : list2) {
				list.add("temp/"+string);
			}
		}
		//复制导出的excel模板
		File file1 = new File("excelTemplete");
		if (file1.isDirectory()) {
			String[] list2 = file.list();
			for (String string : list2) {
				list.add("excelTemplete/"+string);
			}
		}
		controller.setCopyFileNames(list);
	}
	
}
