package com.donglu.carpark.hardware.bx;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dongluhitec.card.util.ThreadUtil;
import com.sun.jna.Native;

public class BXScreenServiceImpl implements BXScreenService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static final String LED_X_SER_TW = "BX_IV";
	private BXJNA jna;
	private Map<Integer, List<String>> mapScreenInfo=new HashMap<>();
	private Map<Integer, Integer> mapAreaSize=new HashMap<>();
	private Map<Integer, String> mapScreen=new HashMap<>();
	private ExecutorService fixedThreadPool;
	//车位信息
	private int position=0;
	//保存最后车牌显示时间
	private Map<String, Date> mapIpToLastPlateShowDate=new HashMap<>();
	//保存屏幕序号
	private Map<String, Integer> mapIpToScreenNo=new HashMap<>();
	private Date initDate=new Date();
	//节目类型
	private int programType=3;
	
	@Override
	public boolean sendContent(int identitifire, String ip, String content) {
		return false;
	}

	@Override
	public boolean sendPosition(int identitifire, String ip, int position) {
		mapIpToScreenNo.put(ip, identitifire);
		this.position = position;
		Date lastPlateShowDate=mapIpToLastPlateShowDate.getOrDefault(ip, initDate);
		if (System.currentTimeMillis()-lastPlateShowDate.getTime()<15000) {
			sendPosition(identitifire, ip);
		}
		return false;
	}

	@Override
	public boolean sendPlateNO(int identitifire, String ip, String plateNO,boolean isTrue) {
		mapIpToScreenNo.put(ip, identitifire);
		mapIpToLastPlateShowDate.put(ip, new Date());
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
					if (programType!=1) {
						jna.DeleteScreenProgram(identitifire, 0);
						jna.AddScreenProgram(identitifire, 0, 0, 65535, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 23, 59);
						programType=1;
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
						String string = screenTextList.get(size-1-i);
						int deleteScreenProgramAreaBmpTextFile = jna.DeleteScreenProgramAreaBmpTextFile(identitifire, 0, i, 0);
						System.out.println((System.currentTimeMillis() - currentTimeMillis) + "===deleteScreenProgramAreaBmpTextFile===" + deleteScreenProgramAreaBmpTextFile);
						
						int nFontColor = 255;
						if (isTrue) {
							nFontColor= BXScreenConfig.getInstance().getTrueColor();
							string=string+"    准予放行";
						}else{
							nFontColor= BXScreenConfig.getInstance().getFalseColor();
							string=string+"    禁止通行";
						}
						int result = jna.AddScreenProgramAreaBmpTextText(identitifire, 0, i, string, 1, 0, "宋体", 10, 0, nFontColor, 0x01, 10, 10);
						System.out.println((System.currentTimeMillis() - currentTimeMillis) + "===addScreenProgramAreaBmpTextText===" + result);
					}
					int result = jna.SendScreenInfo(identitifire, BXJNA.SEND_CMD_SENDALLPROGRAM, 0);
					System.out.println((System.currentTimeMillis() - currentTimeMillis) + "===sendScreenInfo===" + result);
					mapIpToLastPlateShowDate.put(ip, new Date());
				} catch (Exception e) {
					logger.info("发送车牌信息时发生错误");
				}
			}
		};
		fixedThreadPool.submit(runnable);
	}


	protected void addScreenProgramBmpTextArea(int identifire,int areaSize) {
		int nY = 0;
		nY = areaSize * 16;
		System.out.println("nY===" + nY);
		int result = jna.AddScreenProgramBmpTextArea(identifire, 1, 0, nY, 160, 16);
		System.out.println("addScreenProgramBmpTextArea===" + result);
	}

	protected void addScreenProgram(int nScreenNo) {
		jna.AddScreenProgram(nScreenNo, 0, 0, 65535, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 23, 59);
	}

	private void initAnkangCityScreen(int nScreenNo) {
		if (programType!=0) {
			jna.DeleteScreenProgram(nScreenNo, 0);
			jna.AddScreenProgram(nScreenNo, 0, 0, 65535, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 23, 59);
			jna.AddScreenProgramTimeArea(nScreenNo, 0, 12, 0, 141, 16);
			jna.AddScreenProgramTimeAreaFile(nScreenNo, 0, 0, null, "宋体", 0, 0, 10, 0, 0, 0, 0, 255, 1, 0, 255, 1, 0, 255, 0, 4, 255, 1);
			jna.AddScreenProgramTimeArea(nScreenNo, 0, 53, 16, 54, 16);
			jna.AddScreenProgramTimeAreaFile(nScreenNo, 0, 1, null, "宋体", 0, 0, 10, 0, 0, 0, 0, 255, 0, 0, 255, 0, 0, 255, 1, 4, 255, 1);
			jna.AddScreenProgramBmpTextArea(nScreenNo, 0, 0, 32, 160, 16);
			jna.AddScreenProgramBmpTextArea(nScreenNo, 0, 0, 48, 160, 16);
			jna.AddScreenProgramAreaBmpTextText(nScreenNo, 0, 2, "安康市人民政府", 1, 1, "宋体", 10, 0, 255, 0x01, 10, 10);
			programType=0;
		}
		jna.AddScreenProgramAreaBmpTextText(nScreenNo, 0, 2, "车位:"+position, 1, 1, "宋体", 10, 0, 255, 0x01, 10, 10);
	}

	protected int addScreen(int identitifire, String ip) {
		System.out.println("添加屏幕："+identitifire+"--"+ip);
		int nScreenType = BXScreenConfig.getInstance().getnScreenType();
		int result = jna.AddScreen(0x0274, identitifire, 2, 160, 96, nScreenType, 0, 0, 0, 0, 0, 0, null, 57600, ip, 5005, 0, 0, null, null, null, 0, null, null, null, 0, null, 0, null, System.getProperty("user.dir")+File.separator+"screenConfig.ini");
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
		ScheduledExecutorService newSingleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor(ThreadUtil.createThreadFactory("发送车位到BX屏幕"));
		newSingleThreadScheduledExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					Set<String> keySet = mapIpToScreenNo.keySet();
					if (keySet.isEmpty()) {
						return;
					}
					for (String ip : keySet) {
						Date lastPlateShowDate=mapIpToLastPlateShowDate.getOrDefault(ip, new Date());
						if (System.currentTimeMillis()-lastPlateShowDate.getTime()<15000)  {
							return;
						}
						sendPosition(mapIpToScreenNo.get(ip), ip);
					}
				} catch (Exception e) {
					logger.info("发送车位时发生错误");
				}
			}
		}, 5, 5, TimeUnit.SECONDS);
		int initialize = jna.Initialize(handle, pCallBack);
		System.out.println(handle+"=initialize==="+initialize);
		return initialize==0;
	}

	protected void sendPosition(int integer, String ip) {
		initAnkangCityScreen(integer);
		jna.SendScreenInfo(integer, BXJNA.SEND_CMD_SENDALLPROGRAM, 0);
	}
}
