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

import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.util.ThreadUtil;
import com.sun.jna.Native;

public class BXScreenServiceImpl implements BXScreenService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static final String LED_X_SER_TW = "BX_IV";
	private BXJNA jna;
	private Map<Integer, List<String>> mapScreenInfo=new HashMap<>();
	private Map<String, Boolean> mapPlateStatus=new HashMap<>();
	private Map<Integer, Integer> mapAreaSize=new HashMap<>();
	private ExecutorService fixedThreadPool;
	
	private List<String> listWaitInPlate=new ArrayList<>();
	//车位信息
	private int position=0;
	//保存最后车牌显示时间
	private Map<String, Date> mapIpToLastPlateShowDate=new HashMap<>();
	//保存屏幕序号
	private Map<String, Integer> mapIpToScreenNo=new HashMap<>();
	private Date initDate=new Date();
	//节目类型
	private int programType=3;
	private boolean plateControlSetting;
	
	private int totalSize=0;
	
	@Override
	public boolean sendContent(int identitifire, String ip, String content) {
		return false;
	}

	@Override
	public boolean sendPosition(int identitifire, String ip, int position) {
		if (mapIpToScreenNo.get(ip)==null) {
			addScreen(identitifire, ip);
			mapIpToScreenNo.put(ip, identitifire);
		}
		this.position = position;
		return false;
	}

	@Override
	public boolean sendPlateNO(int identitifire, String ip, String plateNO,boolean isTrue) {
		if (mapIpToScreenNo.get(ip)==null) {
			addScreen(identitifire, ip);
			mapIpToScreenNo.put(ip, identitifire);
		}
		mapIpToLastPlateShowDate.put(ip, new Date());
		List<String> list = mapScreenInfo.getOrDefault(identitifire, new ArrayList<>());
		list.add(plateNO);
		if (list.size()>2) {
			String remove = list.remove(0);
			mapPlateStatus.remove(remove);
		}
		mapScreenInfo.put(identitifire, list);
		mapPlateStatus.put(plateNO, isTrue);
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
					long currentTimeMillis = System.currentTimeMillis();
					createPlateShowProgram(identitifire);
					int result = sendInfo(identitifire);
					System.out.println("fixedThreadPool===="+(System.currentTimeMillis() - currentTimeMillis) + "===sendScreenInfo===" + result);
					mapIpToLastPlateShowDate.put(ip, new Date());
				} catch (Exception e) {
					logger.info("发送车牌信息时发生错误");
				}
			}

			/**
			 * 创建车牌显示节目
			 * @param screenNo
			 */
			public void createPlateShowProgram(final int screenNo) {
				if (programType!=1) {
					//创建车辆实时状态显示节目
					jna.DeleteScreenProgram(screenNo, 0);
					jna.AddScreenProgram(screenNo, 0, 10, 65535, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 23, 59);
					int result = jna.AddScreenProgramBmpTextArea(1, 0, 30, 0, 100, 16);
					result = jna.AddScreenProgramBmpTextArea(1, 0, 0, 80, 160, 16);
					result = jna.AddScreenProgramBmpTextArea(1, 0, 0, 16, 160, 32);
					result = jna.AddScreenProgramBmpTextArea(1, 0, 0, 48, 160, 32);
					jna.AddScreenProgramAreaBmpTextText(1, 0, 0, "车辆实时状态", 1, 1, "宋体", 12, 0, 255, 1, 1, 1);
//						jna.AddScreenProgramAreaBmpTextText(1, 0, 2, " 陕G12345  准予通行", 1, 1, "宋体", 12, 0, 65280, 1, 1, 1);
//						jna.AddScreenProgramAreaBmpTextText(1, 0, 3, " 陕G12345  禁止通行", 1, 1, "宋体", 12, 0, 255, 1, 1, 1);
					jna.AddScreenProgramAreaBmpTextText(1, 0, 1, " 0 2 4 6 8 双号通行", 1, 1, "宋体", 12, 0, 255, 1, 1, 1);
					programType=1;
				}
				List<String> screenTextList = mapScreenInfo.get(screenNo);
				Integer areaSize = mapAreaSize.getOrDefault(screenNo, 0);

				mapAreaSize.put(screenNo, areaSize);
				int size = screenTextList.size();
				for (int i = 0; i < size; i++) {
					String string = screenTextList.get(size-1-i);
					int deleteScreenProgramAreaBmpTextFile = jna.DeleteScreenProgramAreaBmpTextFile(screenNo, 0, i+2, 0);
//						System.out.println((System.currentTimeMillis() - currentTimeMillis) + "===deleteScreenProgramAreaBmpTextFile===" + deleteScreenProgramAreaBmpTextFile);
					
					int nFontColor = 255;
					if (mapPlateStatus.get(string)) {
						nFontColor= BXScreenConfig.getInstance().getTrueColor();
						string=" "+string+" 准予放行";
					}else{
						nFontColor= BXScreenConfig.getInstance().getFalseColor();
						string=" "+string+" 禁止通行";
					}
					int result = jna.AddScreenProgramAreaBmpTextText(screenNo, 0, i+2, string, 1, 0, "宋体", 12, 0, nFontColor, 0x01, 10, 10);
//						System.out.println((System.currentTimeMillis() - currentTimeMillis) + "===addScreenProgramAreaBmpTextText===" + result);
				}
				if (size==1) {
					int deleteScreenProgramAreaBmpTextFile = jna.DeleteScreenProgramAreaBmpTextFile(screenNo, 0, 3, 0);
//						System.out.println((System.currentTimeMillis() - currentTimeMillis) + "===deleteScreenProgramAreaBmpTextFile===" + deleteScreenProgramAreaBmpTextFile);
					
					int nFontColor = 255;
					String string="";
					int result = jna.AddScreenProgramAreaBmpTextText(screenNo, 0, 3, string, 1, 0, "宋体", 12, 0, nFontColor, 0x01, 10, 10);
//						System.out.println((System.currentTimeMillis() - currentTimeMillis) + "===addScreenProgramAreaBmpTextText===" + result);
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
			int screenProgramIndex=0;
			int result=0;
			result=jna.DeleteScreenProgram(nScreenNo, 0);
			result=jna.AddScreenProgram(nScreenNo, 0, 10, 65535, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 23, 59);
			System.out.println("AddScreenProgram==="+result);
			result=jna.AddScreenProgramTimeArea(nScreenNo, screenProgramIndex, 1, 0, 158, 16);
			System.out.println("AddScreenProgramTimeArea==="+result);
			result=jna.AddScreenProgramTimeArea(nScreenNo, screenProgramIndex, 60, 16, 39, 16);
			System.out.println("AddScreenProgramTimeArea==="+result);
			result=jna.AddScreenProgramBmpTextArea(nScreenNo, screenProgramIndex, 24, 64, 112, 32);
			System.out.println("AddScreenProgramBmpTextArea==="+result);
			result=jna.AddScreenProgramBmpTextArea(nScreenNo, screenProgramIndex, 0, 32, 160, 32);
			System.out.println("AddScreenProgramBmpTextArea==="+result);
			jna.AddScreenProgramTimeAreaFile(nScreenNo, screenProgramIndex, 0, null, "宋体", 0, 1, 10, 0, 0, 0, 0, 65280, 1, 0, 65280, 0, 0, 65280, 1, 0, 65280, 0);
			jna.AddScreenProgramTimeAreaFile(nScreenNo, screenProgramIndex, 1, null, "宋体", 0, 1, 10, 0, 0, 0, 0, 65280, 0, 0, 65280, 1, 0, 65280, 0, 0, 65280, 0);
//			jna.AddScreenProgramAreaBmpTextText(1, screenProgramIndex, 2, " 陕G12345 等待通行", 0, 1, "宋体", 12, 0, 65280,  0x05, 4, 10);
//			jna.AddScreenProgramAreaBmpTextText(1, screenProgramIndex, 2, " 陕G12346 等待通行", 0, 1, "宋体", 12, 0, 65280,  0x05, 4, 10);
			jna.AddScreenProgramAreaBmpTextText(nScreenNo, screenProgramIndex, 2, "安康市人民政府", 0, 1, "宋体", 12, 0, 255,  0x01, 4, 8);
			programType=0;
		}
		jna.DeleteScreenProgramArea(nScreenNo, 0, 3);
		int result=jna.AddScreenProgramBmpTextArea(nScreenNo, 0, 0, 32, 160, 32);
		System.out.println(listWaitInPlate+"AddScreenProgramBmpTextArea==="+result);
		if (StrUtil.isEmpty(listWaitInPlate)) {
			String pText = " 0 2 4 6 8 双号通行";
			if (plateControlSetting) {
				pText = " 1 3 5 7 9 单号通行";
			}
			jna.AddScreenProgramAreaBmpTextText(nScreenNo, 0, 3, pText, 0, 1, "宋体", 12, 0, 65280,  0x01, 4, 65525);
		}else{
			for (String string : listWaitInPlate) {
				jna.AddScreenProgramAreaBmpTextText(nScreenNo, 0, 3, " "+string+" 等待通行", 0, 1, "宋体", 12, 0, 65280,  0x05, 4, 10);
			}
		}
	}

	protected int addScreen(int identitifire, String ip) {
		System.out.println("添加屏幕："+identitifire+"--"+ip);
		int nScreenType = BXScreenConfig.getInstance().getnScreenType();
		int result = jna.AddScreen(0x0154, identitifire, 2, 160, 96, nScreenType, 1, 1, 0, 0, 0, 0, null, 57600, ip, 5005, 0, 0, null, null, null, 0, null, null, null, 0, null, 0, null, System.getProperty("user.dir")+File.separator+"screenConfig.ini");
		System.out.println(nScreenType+"==AddScreen==="+result);
		return result;
	}



	/**
	 * 
	 */
	@Override
	public boolean init(int handle) {
		mapIpToScreenNo.clear();
		jna = (BXJNA) Native.loadLibrary(LED_X_SER_TW, BXJNA.class);
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
						if (mapIpToScreenNo.get(ip)==null) {
							return;
						}
						Date lastPlateShowDate=mapIpToLastPlateShowDate.getOrDefault(ip,initDate);
						if (System.currentTimeMillis()-lastPlateShowDate.getTime()<15000)  {
							return;
						}
						sendPosition(0, ip);
					}
				} catch (Exception e) {
					logger.info("发送平时屏时发生错误");
				}
			}
		}, 5, 5, TimeUnit.SECONDS);
		int initialize = jna.Initialize(handle, null);
		return initialize==0;
	}

	protected void sendPosition(int integer, String ip) {
		initAnkangCityScreen(integer);
		int sendScreenInfo = sendInfo(integer);
		if (sendScreenInfo!=0) {
			deleteScreen(integer);
			addScreen(integer, ip);
			programType=9;
			initAnkangCityScreen(integer);
			sendInfo(integer);
		}
	}

	/**
	 * @param integer
	 * @return
	 */
	public synchronized int sendInfo(int integer) {
		totalSize++;
		long currentTimeMillis = System.currentTimeMillis();
		int sendScreenInfo = jna.SendScreenInfo(integer, BXJNA.SEND_CMD_SENDALLPROGRAM, 0);
		logger.info("第{}次发送消息到bx屏幕花费时间：{} 结果为：{} ",totalSize,(System.currentTimeMillis()-currentTimeMillis),sendScreenInfo);
		return sendScreenInfo;
	}

	private void deleteScreen(int integer) {
		jna.DeleteScreen(integer);
	}

	@Override
	public void setPlateControlStatus(boolean plateControlSetting) {
		this.plateControlSetting = plateControlSetting;
	}

	@Override
	public void setWillInPlate(List<String> willInPlate) {
		this.listWaitInPlate = willInPlate;
	}
}
