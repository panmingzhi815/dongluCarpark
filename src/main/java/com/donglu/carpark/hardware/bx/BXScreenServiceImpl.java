package com.donglu.carpark.hardware.bx;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.jna.Native;

public class BXScreenServiceImpl implements BXScreenService {
	private static final String LED_X_SER_TW = "BX_IV";
	private BXJNA jna;
	private Map<Integer, List<String>> mapScreenInfo=new HashMap<>();
	private Map<Integer, Integer> mapAreaSize=new HashMap<>();
	private Map<Integer, String> mapScreen=new HashMap<>();
	private ExecutorService fixedThreadPool;
	List<Runnable> listTask=new ArrayList<>();	
	
	@Override
	public boolean sendContent(int identitifire, String ip, String content) {
		return false;
	}

	@Override
	public boolean sendPosition(int identitifire, String ip, int position) {
		return false;
	}

	@Override
	public boolean sendPlateNO(int identitifire, String ip, String plateNO,boolean isTrue) {
		List<String> list = mapScreenInfo.getOrDefault(identitifire, new ArrayList<>());
		list.add(plateNO);
		if (list.size()>6) {
			list.remove(0);
		}
		mapScreenInfo.put(identitifire, list);
		sendInfoToScreen(identitifire,ip,isTrue);
		return true;
	}

	private void sendInfoToScreen(int identitifire, String ip, boolean isTrue) {
		sendScreenInfo(identitifire,ip,isTrue);
	}
	
	protected void sendScreenInfo(final int identitifire, final String ip, boolean isTrue) {
		if (fixedThreadPool==null) {
			fixedThreadPool = Executors.newFixedThreadPool(1);
		}
		Runnable runnable = new Runnable() {
			public void run() {
				try {
					String string2 = mapScreen.get(identitifire);
					if (string2 == null) {
						String ipAddr = ip.split(":")[0];
						int addScreen = addScreen(identitifire, ipAddr);
						if (addScreen != 0) {
							System.out.println("添加屏幕失败");
							return;
						}
						mapScreen.put(identitifire, ip);
					}
					List<String> screenTextList = mapScreenInfo.get(identitifire);
					Integer areaSize = mapAreaSize.getOrDefault(identitifire, 0);

					long currentTimeMillis = System.currentTimeMillis();
					for (; areaSize < screenTextList.size(); areaSize++) {
						addScreenProgramBmpTextArea(identitifire,areaSize);
					}
					mapAreaSize.put(identitifire, areaSize);
					int size = screenTextList.size();
					for (int i = 0; i < size; i++) {
						String string = screenTextList.get(i);
						int deleteScreenProgramAreaBmpTextFile = jna.DeleteScreenProgramAreaBmpTextFile(identitifire, 0, i, 0);
						System.out.println((System.currentTimeMillis() - currentTimeMillis) + "===deleteScreenProgramAreaBmpTextFile===" + deleteScreenProgramAreaBmpTextFile);
						
						int nFontColor = 255;
						if (isTrue) {
							nFontColor= BXScreenConfig.getInstance().getTrueColor();
						}else{
							nFontColor= BXScreenConfig.getInstance().getFalseColor();
						}
						int result = jna.AddScreenProgramAreaBmpTextText(identitifire, 0, i, string, 1, 0, "微软雅黑", 10, 0, nFontColor, 0x01, 10, 10);
						System.out.println((System.currentTimeMillis() - currentTimeMillis) + "===addScreenProgramAreaBmpTextText===" + result);
					}
					int result = jna.SendScreenInfo(identitifire, BXJNA.SEND_CMD_SENDALLPROGRAM, 0);
					System.out.println((System.currentTimeMillis() - currentTimeMillis) + "===sendScreenInfo===" + result);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		fixedThreadPool.submit(runnable);
	}


	protected void addScreenProgramBmpTextArea(int identifire,int areaSize) {
		int nY = 0;
		nY = areaSize * 16;
		System.out.println("nY===" + nY);
		int result = jna.AddScreenProgramBmpTextArea(identifire, 0, 0, nY, 160, 16);
		System.out.println("addScreenProgramBmpTextArea===" + result);
	}

	protected void addScreenProgram(int nScreenNo) {
		int result = jna.AddScreenProgram(nScreenNo, 0, 0, 65535, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 23, 59);
		System.out.println("addScreenProgram==="+result);
	}

	protected int addScreen(int identitifire, String ip) {
		System.out.println("添加屏幕："+identitifire+"--"+ip);
		int nScreenType = BXScreenConfig.getInstance().getnScreenType();
		int result = jna.AddScreen(0x0274, identitifire, 2, 160, 96, nScreenType, 0, 0, 0, 0, 0, 0, null, 57600, ip, 5005, 0, 0, null, null, null, 0, null, null, null, 0, null, 0, null, System.getProperty("user.dir")+File.separator+"screenConfig.ini");
		
		addScreenProgram(identitifire);
		return result;
	}



	/**
	 * 
	 */
	@Override
	public boolean init(int handle) {
		jna = (BXJNA) Native.loadLibrary(LED_X_SER_TW, BXJNA.class);
		BXCallBack pCallBack=new BXCallBack() {
			@Override
			public void invoke(String szMessagge, Integer nProgress) {
				System.out.println(System.currentTimeMillis()+"===pCallBack==="+szMessagge+"===="+nProgress);
			}
		};
		
		int initialize = jna.Initialize(handle, pCallBack);
		System.out.println(handle+"=initialize==="+initialize);
		return initialize==0;
	}

}
