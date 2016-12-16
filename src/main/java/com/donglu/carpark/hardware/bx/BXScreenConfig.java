package com.donglu.carpark.hardware.bx;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Properties;

public class BXScreenConfig implements Serializable{
	
	public final String configFileName = "BXScreenConfig.properties";
	public static BXScreenConfig instance;
	private int nScreenType=2;
	
	private int trueColor=65280;
	private int falseColor=255;
	private int plateShowTime=15;
	private BXScreenConfig(){}
	
	public static BXScreenConfig getInstance(){
		if(instance == null){
			instance = new BXScreenConfig();
		}
		instance.loadConfig();
		return instance;
	}
	
	public void saveConfig(){
		try (FileOutputStream fos = new FileOutputStream(configFileName, false);PrintWriter out = new PrintWriter(fos);){
			out.println(String.format("#服务器ip配置  %s", new Date()));

			out.println("#显示屏类型；1：单基色；2：双基色；3：双基色；注意：该显示屏类型只有BX-4MC支持；同时该型号控制器不支持其它显示屏类型。4：全彩色；注意：该显示屏类型只有BX-5Q系列支持；同时该型号控制器不支持其它显示屏类型。5：双基色灰度；注意：该显示屏类型只有BX-5QS支持；同时该型号控制器不支持其它显示屏类型");
			out.println(String.format("%s=%s", "nScreenType", getnScreenType()));
			out.println();
			out.println("#有效车牌颜色 红255 绿65280 黄65535");
			out.println(String.format("%s=%s", "trueColor", getTrueColor()));
			out.println();
			out.println("#无效车牌颜色 红255 绿65280 黄65535");
			out.println(String.format("%s=%s", "falseColor", getFalseColor()));
			out.println();
			out.println("#车牌显示停留时间(秒)");
			out.println(String.format("%s=%s", "falseColor", getFalseColor()));
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
            
            this.nScreenType = Integer.valueOf(preferenceStore.getProperty("nScreenType", "1"));
            this.trueColor = Integer.valueOf(preferenceStore.getProperty("trueColor", "255"));
            this.falseColor = Integer.valueOf(preferenceStore.getProperty("falseColor", "255"));
            this.plateShowTime = Integer.valueOf(preferenceStore.getProperty("plateShowTime", "15"));
        } catch (IOException e) {
            e.printStackTrace();
        } 
	}

	public int getnScreenType() {
		return nScreenType;
	}

	public void setnScreenType(int nScreenType) {
		this.nScreenType=nScreenType;
		saveConfig();
	}

	public int getTrueColor() {
		return trueColor;
	}

	public void setTrueColor(int trueColor) {
		this.trueColor = trueColor;
		saveConfig();
	}

	public int getFalseColor() {
		return falseColor;
	}

	public void setFalseColor(int falseColor) {
		this.falseColor = falseColor;
		saveConfig();
	}

	public int getPlateShowTime() {
		return plateShowTime;
	}

	public void setPlateShowTime(int plateShowTime) {
		this.plateShowTime = plateShowTime;
		saveConfig();
	}
}
