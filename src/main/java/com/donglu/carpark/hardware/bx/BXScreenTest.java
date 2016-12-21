package com.donglu.carpark.hardware.bx;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import onbon.bx05.Bx5GEnv;
import onbon.bx05.Bx5GScreenClient;
import onbon.bx05.area.DateStyle;
import onbon.bx05.area.DateTimeBxArea;
import onbon.bx05.area.TextCaptionBxArea;
import onbon.bx05.area.TimeStyle;
import onbon.bx05.area.WeekStyle;
import onbon.bx05.area.page.TextBxPage;
import onbon.bx05.cmd.dyn7.DynamicBxAreaRule;
import onbon.bx05.file.ProgramBxFile;
import onbon.bx05.utils.DisplayStyleFactory;
import onbon.bx05.utils.TextBinary.Alignment;

public class BXScreenTest{
	

	private static TextCaptionBxArea areaMainInfo;
	

	public static void main(String[] args) throws Exception {
		BXScreenTest c=new BXScreenTest();
		c.setPlateControlStatus(true);
//		c.setWillInPlate(new ArrayList<>());
		c.init(0);
		c.sendPosition(0, "172.16.1.232", 0);
		Thread.sleep(10000000);
	}
	private Map<String, Bx5GScreenClient> mapScreen=new HashMap<>();
	
	
	private List<String> listWaitInPlate=Arrays.asList("陕G12345","陕G12346");
	private boolean plateControlSetting;
	private DynamicBxAreaRule dynRule;
	private DynamicBxAreaRule dynRule1;

	public void init() throws Exception {
		Bx5GEnv.initial("log.properties", 15000);
		ScheduledExecutorService newSingleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
		newSingleThreadScheduledExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					Set<String> keySet = mapScreen.keySet();
					if (keySet.isEmpty()) {
						return;
					}
					for (String ip : keySet) {
						Bx5GScreenClient screen = mapScreen.get(ip);
						if (mapScreen.get(ip)==null) {
							return;
						}
						showMainInfo(screen);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 5, 15, TimeUnit.SECONDS);
	}

	protected void showMainInfo(Bx5GScreenClient screen) throws Exception {
		long currentTimeMillis = System.currentTimeMillis();
		screen.deleteAllDynamic();
		TextCaptionBxArea area = new TextCaptionBxArea(0, 32, 160, 30, screen.getProfile());
		area.setFrameShow(false);
		if (listWaitInPlate.isEmpty()) {
			String string = " 0 2 4 6 8 双号通行";
			if (plateControlSetting) {
				string = " 1 3 5 7 9 单号通行";
			}
			TextBxPage p = new TextBxPage(string, new Font("宋体", Font.PLAIN, 14), Color.green, Color.black);
			p.setDisplayStyle(DisplayStyleFactory.getStyle(1));
			p.setVerticalAlignment(Alignment.CENTER); // 垂直置中
			p.setHorizontalAlignment(Alignment.CENTER);
			area.addPage(p);
		}
		
		for (String string : listWaitInPlate) {
			TextBxPage page1 = new TextBxPage(" "+string+" 等待通行", new Font("宋体", Font.PLAIN, 12), Color.green, Color.black);
			page1.setDisplayStyle(DisplayStyleFactory.getStyle(5));
			page1.setSpeed(4);
			page1.setStayTime(500);
			page1.setVerticalAlignment(Alignment.CENTER); // 垂直置中
			area.addPage(page1);
		}
		
//		writeButtomTxtArea(screen);
		screen.writeDynamic(dynRule, area);
		System.out.println("更新主页信息花费时间===="+(System.currentTimeMillis()-currentTimeMillis));
	}
	
	/**
	 * @param screen
	 * @return
	 * @throws IOException
	 */
	public ProgramBxFile createProgram1(Bx5GScreenClient screen) throws IOException {
		DateTimeBxArea dtArea = new DateTimeBxArea(1, 0, 158, 16, screen.getProfile());
		dtArea.setFont(new Font("宋体", Font.PLAIN, 14));
		// 设置颜色
		dtArea.setColor(Color.green);
		// 多行显示还是单行显示
		dtArea.setMultiline(false);
		//
		// 年月日的显示方式
		// 如果不显示，则设置为 null
		dtArea.setDateStyle(DateStyle.YYYY_MM_DD_3);
		dtArea.setTimeStyle(TimeStyle.HH_MM_2);
		dtArea.setWeekStyle(null);

		DateTimeBxArea dtArea1 = new DateTimeBxArea(60, 16, 39, 16, screen.getProfile());
		dtArea1.setFont(new Font("宋体", Font.PLAIN, 16));
		// 设置颜色
		dtArea1.setColor(Color.green);
		// 多行显示还是单行显示
		dtArea1.setMultiline(false);
		//
		// 年月日的显示方式
		// 如果不显示，则设置为 null
		dtArea1.setDateStyle(null);
		dtArea1.setTimeStyle(null);
		dtArea1.setWeekStyle(WeekStyle.CHINESE);

//		// 增加文本頁
		String string = " 0 2 4 6 8  双号通行";
		if (plateControlSetting) {
			string = " 1 3 5 7 9  双号通行";
		}
		TextBxPage p = new TextBxPage(string, new Font("宋体", Font.PLAIN, 14), Color.green, Color.black);
		p.setDisplayStyle(DisplayStyleFactory.getStyle(1));
		p.setVerticalAlignment(Alignment.CENTER); // 垂直置中
		p.setHorizontalAlignment(Alignment.CENTER);
		

		areaMainInfo = new TextCaptionBxArea(0, 32, 160, 32, screen.getProfile());
		areaMainInfo.setFrameShow(false);
		
		
		TextCaptionBxArea areaMainInfo = new TextCaptionBxArea(0, 32, 160, 32, screen.getProfile());
		areaMainInfo.setFrameShow(false);
		areaMainInfo.addPage(p);

		// 增加文本頁
		TextBxPage page = new TextBxPage("安康市人民政府", new Font("宋体", Font.PLAIN, 14), Color.red, Color.black);
		page.setDisplayStyle(DisplayStyleFactory.getStyle(1));
		page.setVerticalAlignment(Alignment.CENTER); // 垂直置中

		// 增加圖文區
		TextCaptionBxArea area = new TextCaptionBxArea(24, 64, 112, 32, screen.getProfile());
		area.setFrameShow(false);
		area.setFrameStyle(2);
		area.loadFrameImage(6);
		area.addPage(page);
		// 建立節目
		ProgramBxFile program = new ProgramBxFile("P000", screen.getProfile());
		program.setProgramTimeSpan(65535);
		program.addArea(dtArea);
		program.addArea(dtArea1);
//		program.addArea(areaMainInfo);
		program.addArea(area);
		dynRule = new DynamicBxAreaRule(0, (byte)0, (byte)0, 0);
		dynRule.addProgram("P000");
		dynRule1 = new DynamicBxAreaRule(1, (byte)0, (byte)0, 0);
		dynRule1.addProgram("P000");
		return program;
	}
	
	public boolean sendPosition(int identitifire, String ip, int position) {
		try {
			Bx5GScreenClient screen = initScreen(ip);
			showMainInfo(screen);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	private Bx5GScreenClient initScreen(String ip) throws Exception{
		Bx5GScreenClient screen = mapScreen.get(ip);
		if (screen == null) {
			screen = new Bx5GScreenClient("MyScreen");
			if (!screen.connect(ip, 5005)) {
				System.out.println("connect failed");
				return null;
			}
			mapScreen.put(ip, screen);
			screen.deletePrograms();
			screen.deleteAllDynamic();
			ProgramBxFile bxFile = createProgram1(screen);
			screen.writeProgram(bxFile);
			writeButtomTxtArea(screen);
//			screen.writeProgram(bxFile2);
		}
		return screen;
	}

	/**
	 * @param screen
	 * @throws IOException
	 */
	public void writeButtomTxtArea(Bx5GScreenClient screen) throws IOException {
		// 增加文本頁
		TextBxPage page = new TextBxPage("安康市人民政府", new Font("宋体", Font.PLAIN, 14), Color.red, Color.black);
		page.setDisplayStyle(DisplayStyleFactory.getStyle(1));
		page.setVerticalAlignment(Alignment.CENTER); // 垂直置中

		// 增加圖文區
		TextCaptionBxArea area1 = new TextCaptionBxArea(24, 64, 112, 32, screen.getProfile());
		area1.setFrameShow(false);
		area1.setFrameStyle(2);
		area1.loadFrameImage(6);
		area1.addPage(page);
		screen.writeDynamic(dynRule1, area1);
	}
	public boolean init(int handle) {
		try {
			init();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	public void setPlateControlStatus(boolean plateControlSetting) {
		this.plateControlSetting = plateControlSetting;
	}
	public void setWillInPlate(List<String> willInPlate) {
		listWaitInPlate=willInPlate;
	}
	
}
