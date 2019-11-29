package com.donglu.carpark.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.swt.custom.CTabItem;
import org.joda.time.DateTime;

import com.donglu.carpark.ui.MyHashMap1;
import com.donglu.carpark.ui.common.App;
import com.donglu.carpark.ui.task.CarInTask;
import com.donglu.carpark.ui.task.CarOutTask;
import com.donglu.carpark.util.MyHashMap;
import com.donglu.carpark.util.MyMapCache;
import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.db.singlecarpark.CarCheckHistory;
import com.dongluhitec.card.domain.db.singlecarpark.CarTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkCarType;
import com.dongluhitec.card.domain.db.singlecarpark.DeviceVoiceTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDeviceVoice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkStoreFreeHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkVisitor;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.google.inject.Singleton;

@Singleton
public class CarparkMainModel extends DomainObject {

	// 保存设备的进出口信息
	private final Map<String, String> mapDeviceType = new MyHashMap<>();
	// 保存设备的进出口信息
	private final Map<String, String> mapCameraLastImage = new MyHashMap<>();
	// 保存界面对应的设备信息
	private final Map<CTabItem, String> mapDeviceTabItem = Maps.newHashMap();
	// 保存设备对应的界面信息
	private final Map<String,CTabItem> mapIpToTabItem = Maps.newHashMap();
	// 保存设备的信息
	private final Map<String, SingleCarparkDevice> mapIpToDevice = Maps.newHashMap();
	// 保存设置信息
	private final Map<SystemSettingTypeEnum, String> mapSystemSetting = Maps.newHashMap();
	// 保存车牌最近的处理时间
	private final Map<String, Date> mapPlateNoDate = new MyMapCache<>(600 * 1000, 5);
	//保存开门信息
	private final Map<String, Boolean> mapOpenDoor = Maps.newHashMap();
	// 保存最近的手动拍照时间
	private final Map<String, Date> mapHandPhotograph = Maps.newHashMap();
	//保存进场任务双摄像头信息
	private final Map<String, CarInTask> mapInTwoCameraTask = Maps.newHashMap();
	//保存出场任务双摄像头信息
	private final Map<String, CarOutTask> mapOutTwoCameraTask = Maps.newHashMap();
	//保存双摄像头信息
	private final Map<String, Boolean> mapIsTwoChanel = Maps.newHashMap();
	//保存监控口设备信息
	private final Map<String, List<SingleCarparkDevice>> mapTypeDevices = Maps.newHashMap();
	// 保存临时收费车辆类型
	private final Map<String, CarparkCarType> mapTempCharge = Maps.newHashMap();;
	// 保存双摄像头处理任务
	private final Map<String, Timer> mapTwoChanelTimer = new HashMap<>();

	private final Map<String, CarInTask> mapInCheck = new MyMapCache<>(600000, 5);
	private final Map<String, CarOutTask> mapOutCheck = new MyMapCache<>(600000, 1);
	//保存声音信息
	private final Map<DeviceVoiceTypeEnum, SingleCarparkDeviceVoice> mapVoice = new HashMap<>();
	//保存设备使用状态信息
	private final Map<String, Boolean> mapIpToDeviceStatus=new HashMap<>();
	
	private final Map<String, Integer> mapHCameraPlayHandle=new HashMap<>();
	//保存待出场信息
	private final List<CarOutTask> listOutTask=new ArrayList<>();
	private long lastCarOutTime=0;
	
	public final Lock inOutLock=new ReentrantLock();

	/**
	 * 
	 */
	private static final long serialVersionUID = -5774120750950259375L;
	// 进口显示信息
	private String inShowPlateNO;
	private String inShowTime;
	private String inShowMeg;
	private byte[] inShowSmallImg;
	private byte[] inShowBigImg;
	private String inBigImageName;

	// 出口显示信息
	private String outShowPlateNO;
	private String outShowTime;
	private String outShowMeg;
	private byte[] outShowSmallImg;
	private byte[] outShowBigImg;
	private String outBigImageName;
	private boolean outPlateNOEditable = false;

	private boolean inCheckIsClick = false;

	String userName;
	String workTime;
	int totalSlot;
	String totalSlotTooltip;
	int hoursSlot;
	int monthSlot;
	float totalCharge;
	float totalFree;
	float totalOnline;

	String plateNo;
	String carUser;
	String carType;
	String inTime;
	String outTime;
	String totalTime;
	float shouldMony;
	float real;

	String ip;

	boolean btnClick = false;
	boolean inCheckClick = false;
	boolean outCheckClick = false;
	boolean handSearch = false;

	boolean selectCarType = false;
	boolean comboCarTypeEnable = false;
	boolean comboCarTypeFocos = false;

	String carparkCarType;
	private String currentTime;

	// 收费时的临时信息
	SingleCarparkInOutHistory chargeHistory;
	SingleCarparkDevice chargeDevice;
	Map<Long, SingleCarparkInOutHistory> childCarparkInOut;

	// 查询时的临时信息
	String searchPlateNo;
	String searchBigImage;
	String searchSmallImage;

	Boolean disContinue;

	private SingleCarparkCarpark carpark;
	private String carTypeEnum;

	private List<SingleCarparkStoreFreeHistory> stroeFrees;

	private SingleCarparkUser user;

	private String outPlateNOColor;

	private List<SingleCarparkInOutHistory> inHistorys = new ArrayList<SingleCarparkInOutHistory>();

	private List<SingleCarparkInOutHistory> outHistorys = new ArrayList<SingleCarparkInOutHistory>();
	private SingleCarparkInOutHistory inHistorySelect;
	private SingleCarparkInOutHistory outHistorySelect;

	private String handPlateNO; // 手动入场车牌

	private CTabItem selectTabSelect;
	private Float chargedMoney = 0F;
	// 停车场进场时间
	private Date plateInTime = new Date();

	private SingleCarparkCarpark searchCarpark;

	// 判断车队是否开启
	private Boolean isOpenFleet = false;
	private SingleCarparkVisitor visitor;
	
	
	private final Cache<String, SingleCarparkInOutHistory> mapWaitInOutHistory=CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).build();
	private final Cache<String, String> plateColorCache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).build();
	private final Cache<String, Integer> plateOverSpeedSizeCache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).build();
	
	private final Map<String,App> mapInOutWindow=new HashMap<>();
	
	private final List<CarCheckHistory> carChecks=new ArrayList<>();
	private CarCheckHistory carCheck=null;
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
		if (pcs != null)
			pcs.firePropertyChange("userName", null, null);
	}

	public String getWorkTime() {
		return workTime;
	}

	public void setWorkTime(Date workTime) {
		this.workTime = StrUtil.formatDate(workTime, "yyyy-MM-dd HH:mm:ss");
		if (pcs != null)
			pcs.firePropertyChange("workTime", null, null);
	}

	public String getPlateNo() {
		return plateNo;
	}

	public void setPlateNo(String plateNo) {
		this.plateNo = plateNo;
		if (pcs != null)
			pcs.firePropertyChange("plateNo", null, null);
	}

	public String getCarUser() {
		return carUser;
	}

	public void setCarUser(String carUser) {
		this.carUser = carUser;
		if (pcs != null)
			pcs.firePropertyChange("carUser", null, null);
	}

	public String getCarType() {
		return carType;
	}

	public void setCarType(String carType) {
		this.carType = carType;
		if (pcs != null)
			pcs.firePropertyChange("carType", null, null);
	}

	public String getInTime() {
		return inTime;
	}

	public void setInTime(Date inTime) {
		this.inTime = StrUtil.formatDate(inTime, "yyyy-MM-dd HH:mm:ss");
		if (pcs != null)
			pcs.firePropertyChange("inTime", null, null);
	}

	public String getOutTime() {
		return outTime;
	}

	public void setOutTime(Date outTime) {
		this.outTime = StrUtil.formatDate(outTime, "yyyy-MM-dd HH:mm:ss");
		if (pcs != null)
			pcs.firePropertyChange("outTime", null, null);
	}

	public String getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(String totalTime) {
		this.totalTime = totalTime;
		if (pcs != null)
			pcs.firePropertyChange("totalTime", null, null);
	}

	public float getShouldMony() {
		return shouldMony;
	}

	public void setShouldMony(float shouldMony) {
		this.shouldMony = shouldMony;
		if (pcs != null)
			pcs.firePropertyChange("shouldMony", null, null);
	}

	public float getReal() {
		return real;
	}

	public void setReal(float real) {
		this.real = real;
		if (pcs != null)
			pcs.firePropertyChange("real", null, null);
	}

	public int getTotalSlot() {
		return totalSlot;
	}

	public void setTotalSlot(int totalSlot) {
		this.totalSlot = totalSlot;
		if (pcs != null)
			pcs.firePropertyChange("totalSlot", null, null);
	}

	public int getHoursSlot() {
		return hoursSlot;
	}

	public void setHoursSlot(int hoursSlot) {
		this.hoursSlot = hoursSlot;
		if (pcs != null)
			pcs.firePropertyChange("hoursSlot", null, null);
	}

	public int getMonthSlot() {
		return monthSlot;
	}

	public void setMonthSlot(int monthSlot) {
		this.monthSlot = monthSlot;
		if (pcs != null)
			pcs.firePropertyChange("monthSlot", null, null);
	}

	public float getTotalCharge() {
		return totalCharge;
	}

	public void setTotalCharge(float totalCharge) {
		this.totalCharge = totalCharge;
		if (pcs != null)
			pcs.firePropertyChange("totalCharge", null, null);
	}

	public float getTotalFree() {
		return totalFree;
	}

	public void setTotalFree(float totalFree) {
		this.totalFree = totalFree;
		if (pcs != null)
			pcs.firePropertyChange("totalFree", null, null);
	}

	public boolean isBtnClick() {
		return btnClick;
	}

	public void setBtnClick(boolean btnClick) {
		this.btnClick = btnClick;
		if (pcs != null)
			pcs.firePropertyChange("btnClick", null, null);
	}
	public String getInShowPlateNO() {
		return inShowPlateNO;
	}

	public void setInShowPlateNO(String inShowPlateNO) {
		this.inShowPlateNO = inShowPlateNO;
		if (pcs != null)
			pcs.firePropertyChange("inShowPlateNO", null, null);
	}

	public String getInShowTime() {
		return inShowTime;
	}

	public void setInShowTime(String inShowTime) {
		this.inShowTime = inShowTime;
		if (pcs != null)
			pcs.firePropertyChange("inShowTime", null, null);
	}

	public String getInShowMeg() {
		return inShowMeg;
	}

	public void setInShowMeg(String inShowMeg) {
		this.inShowMeg = inShowMeg;
		if (pcs != null)
			pcs.firePropertyChange("inShowMeg", null, null);
	}

	public byte[] getInShowSmallImg() {
		return inShowSmallImg;
	}

	public void setInShowSmallImg(byte[] inShowSmallImg) {
		this.inShowSmallImg = inShowSmallImg;
		if (pcs != null)
			pcs.firePropertyChange("inShowSmallImg", null, null);
	}

	public byte[] getInShowBigImg() {
		return inShowBigImg;
	}

	public void setInShowBigImg(byte[] inShowBigImg) {
		this.inShowBigImg = inShowBigImg;
		if (pcs != null)
			pcs.firePropertyChange("inShowBigImg", null, null);
	}

	public String getOutShowPlateNO() {
		return outShowPlateNO;
	}

	public void setOutShowPlateNO(String outShowPlateNO) {
		this.outShowPlateNO = outShowPlateNO;
		if (pcs != null)
			pcs.firePropertyChange("outShowPlateNO", null, null);
	}

	public String getOutShowTime() {
		return outShowTime;
	}

	public void setOutShowTime(String outShowTime) {
		this.outShowTime = outShowTime;
		if (pcs != null)
			pcs.firePropertyChange("outShowTime", null, null);
	}

	public String getOutShowMeg() {
		return outShowMeg;
	}

	public void setOutShowMeg(String outShowMeg) {
		this.outShowMeg = outShowMeg;
		if (pcs != null)
			pcs.firePropertyChange("outShowMeg", null, null);
	}

	public byte[] getOutShowSmallImg() {
		return outShowSmallImg;
	}

	public void setOutShowSmallImg(byte[] outShowSmallImg) {
		this.outShowSmallImg = outShowSmallImg;
		if (pcs != null)
			pcs.firePropertyChange("outShowSmallImg", null, null);
	}

	public byte[] getOutShowBigImg() {
		return outShowBigImg;
	}

	public void setOutShowBigImg(byte[] outShowBigImg) {
		this.outShowBigImg = outShowBigImg;
		if (pcs != null)
			pcs.firePropertyChange("outShowBigImg", null, null);
	}

	public void clear() {
		setPlateNo(null);
		setInTime(null);
		setOutTime(null);
		setBtnClick(false);
		setShouldMony(0);
		setReal(0);
		setCarType(null);
	}

	public boolean isInCheckClick() {
		return inCheckClick;
	}

	public void setInCheckClick(boolean inCheckClick) {
		this.inCheckClick = inCheckClick;
		if (pcs != null)
			pcs.firePropertyChange("inCheckClick", null, null);
	}

	public boolean isOutCheckClick() {
		return outCheckClick;
	}

	public void setOutCheckClick(boolean outCheckClick) {
		this.outCheckClick = outCheckClick;
		if (pcs != null)
			pcs.firePropertyChange("outCheckClick", null, null);
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
		if (pcs != null)
			pcs.firePropertyChange("ip", null, null);
	}

	public boolean isHandSearch() {
		return handSearch;
	}

	public void setHandSearch(boolean handSearch) {
		this.handSearch = handSearch;
		if (pcs != null)
			pcs.firePropertyChange("handSearch", null, null);
	}

	public boolean isSelectCarType() {
		return selectCarType;
	}

	public void setSelectCarType(boolean selectCarType) {
		this.selectCarType = selectCarType;
		if (pcs != null)
			pcs.firePropertyChange("selectCarType", null, null);
	}

	public void setCarparkCarType(String carparkCarType) {
		this.carparkCarType = carparkCarType;
		if (pcs != null)
			pcs.firePropertyChange("carparkCarType", null, null);
	}

	public String getCarparkCarType() {
		return carparkCarType;
	}

	public boolean isComboCarTypeEnable() {
		return comboCarTypeEnable;
	}

	public void setComboCarTypeEnable(boolean comboCarTypeEnable) {
		this.comboCarTypeEnable = comboCarTypeEnable;
		if (pcs != null)
			pcs.firePropertyChange("comboCarTypeEnable", null, null);
	}

	public void setCurrentTime(String currentTime) {
		this.currentTime = currentTime;
		if (pcs != null)
			pcs.firePropertyChange("currentTime", null, null);
	}

	public String getCurrentTime() {
		return currentTime;
	}

	public boolean isOutPlateNOEditable() {
		return outPlateNOEditable;
	}

	public void setOutPlateNOEditable(boolean outPlateNOEditable) {
		this.outPlateNOEditable = outPlateNOEditable;
		if (pcs != null)
			pcs.firePropertyChange("outPlateNOEditable", null, null);
	}

	public SingleCarparkCarpark getCarpark() {
		return carpark;
	}

	public void setCarpark(SingleCarparkCarpark carpark) {
		this.carpark = carpark;
		if (pcs != null)
			pcs.firePropertyChange("carpark", null, null);
	}

	public boolean isComboCarTypeFocos() {
		return comboCarTypeFocos;
	}

	public void setComboCarTypeFocos(boolean comboCarTypeFocos) {
		this.comboCarTypeFocos = comboCarTypeFocos;
		if (pcs != null)
			pcs.firePropertyChange("comboCarTypeFocos", null, null);
	}

	public void setCartypeEnum(String carTypeEnum) {
		this.carTypeEnum = carTypeEnum;
	}

	public String getCarTypeEnum() {
		return carTypeEnum;
	}

	public SingleCarparkInOutHistory getChargeHistory() {
		return chargeHistory;
	}

	public void setChargeHistory(SingleCarparkInOutHistory chargeHistory) {
		this.chargeHistory = chargeHistory;
	}

	public SingleCarparkDevice getChargeDevice() {
		return chargeDevice;
	}

	public void setChargeDevice(SingleCarparkDevice chargeDevice) {
		this.chargeDevice = chargeDevice;
	}

	public String getSearchPlateNo() {
		return searchPlateNo;
	}

	public void setSearchPlateNo(String searchPlateNo) {
		this.searchPlateNo = searchPlateNo;
	}

	public String getSearchBigImage() {
		return searchBigImage;
	}

	public void setSearchBigImage(String searchBigImage) {
		this.searchBigImage = searchBigImage;
	}

	public String getSearchSmallImage() {
		return searchSmallImage;
	}

	public void setSearchSmallImage(String searchSmallImage) {
		this.searchSmallImage = searchSmallImage;
	}

	public Boolean getDisContinue() {
		return disContinue;
	}

	public void setDisContinue(Boolean disContinue) {
		this.disContinue = disContinue;
	}

	public Map<Long, SingleCarparkInOutHistory> getChildCarparkInOut() {
		return childCarparkInOut;
	}

	public void setChildCarparkInOut(Map<Long, SingleCarparkInOutHistory> childCarparkInOut) {
		this.childCarparkInOut = childCarparkInOut;
	}

	public void setStroeFrees(List<SingleCarparkStoreFreeHistory> stroeFrees) {
		this.stroeFrees = stroeFrees;
	}

	public List<SingleCarparkStoreFreeHistory> getStroeFrees() {
		return stroeFrees;
	}

	public void setUser(SingleCarparkUser user) {
		this.user = user;
	}

	public SingleCarparkUser getUser() {
		return user;
	}

	public void setOutPlateNOColor(String outPlateNOColor) {
		this.outPlateNOColor = outPlateNOColor;
	}

	public String getOutPlateNOColor() {
		return outPlateNOColor;
	}

	public List<SingleCarparkInOutHistory> getInHistorys() {
		return inHistorys;
	}

	public void setInHistorys(List<SingleCarparkInOutHistory> inHistorys) {
		this.inHistorys = inHistorys;
		if (pcs != null)
			pcs.firePropertyChange("inHistorys", null, null);
	}

	public void addInHistorys(SingleCarparkInOutHistory inHistory) {
		if (StrUtil.isEmpty(inHistory)) {
			return;
		}
		if (this.inHistorys.size() > 50) {
			this.inHistorys.remove(0);
		}
		if (this.inHistorys.contains(inHistory)) {
			this.inHistorys.remove(inHistory);
			if (pcs != null)
				pcs.firePropertyChange("inHistorys", null, null);
		}
		this.inHistorys.add(inHistory);
		if (pcs != null)
			pcs.firePropertyChange("inHistorys", null, null);
	}

	public List<SingleCarparkInOutHistory> getOutHistorys() {
		return outHistorys;
	}

	public void setOutHistorys(List<SingleCarparkInOutHistory> outHistorys) {
		this.outHistorys = outHistorys;
		if (pcs != null)
			pcs.firePropertyChange("outHistorys", null, null);
	}

	public void addOutHistorys(SingleCarparkInOutHistory inHistory) {
		if (StrUtil.isEmpty(inHistory)) {
			return;
		}
		if (this.outHistorys.size() > 50) {
			this.outHistorys.remove(0);
		}
		this.inHistorys.add(inHistory);
		if (pcs != null)
			pcs.firePropertyChange("outHistorys", null, null);
	}

	public SingleCarparkInOutHistory getInHistorySelect() {
		return inHistorySelect;
	}

	public void setInHistorySelect(SingleCarparkInOutHistory inHistorySelect) {
		this.inHistorySelect = inHistorySelect;
		if (pcs != null)
			pcs.firePropertyChange("inHistorySelect", null, null);
	}

	public SingleCarparkInOutHistory getOutHistorySelect() {
		return outHistorySelect;
	}

	public void setOutHistorySelect(SingleCarparkInOutHistory outHistorySelect) {
		this.outHistorySelect = outHistorySelect;
		if (pcs != null)
			pcs.firePropertyChange("outHistorySelect", null, null);
	}

	public String getHandPlateNO() {
		return handPlateNO;
	}

	public void setHandPlateNO(String handPlateNO) {
		this.handPlateNO = handPlateNO;
		if (pcs != null)
			pcs.firePropertyChange("handPlateNO", null, null);
	}

	public void setSelectTabSelect(CTabItem selectTabSelect) {
		this.selectTabSelect = selectTabSelect;
	}

	public CTabItem getSelectTabSelect() {
		return selectTabSelect;
	}

	public boolean isInCheckIsClick() {
		return inCheckIsClick;
	}

	public void setInCheckIsClick(boolean inCheckIsClick) {
		this.inCheckIsClick = inCheckIsClick;
	}

	public Float getChargedMoney() {
		return chargedMoney;
	}

	public void setChargedMoney(Float chargedMoney) {
		this.chargedMoney = chargedMoney;
		if (pcs != null)
			pcs.firePropertyChange("chargedMoney", null, null);
	}

	public Date getPlateInTime() {
		return plateInTime;
	}

	public synchronized void setPlateInTime(Date plateInTime, int second) {
		this.plateInTime = new DateTime(plateInTime).plusSeconds(second).toDate();
	}

	public void setSearchCarpark(SingleCarparkCarpark searchCarpark) {
		this.searchCarpark = searchCarpark;
		firePropertyChange("searchCarpark", null, null);
	}

	public SingleCarparkCarpark getSearchCarpark() {
		return searchCarpark;
	}

	public Map<String, CarInTask> getMapInCheck() {
		return mapInCheck;
	}

	public Map<DeviceVoiceTypeEnum, SingleCarparkDeviceVoice> getMapVoice() {
		return mapVoice;
	}

	public void setIsOpenFleet(Boolean isOpenFleet) {
		this.isOpenFleet = isOpenFleet;
	}

	public Boolean getIsOpenFleet() {
		return isOpenFleet;
	}

	public Map<String, String> getMapDeviceType() {
		return mapDeviceType;
	}

	public Map<String, String> getMapCameraLastImage() {
		return mapCameraLastImage;
	}

	public Map<CTabItem, String> getMapDeviceTabItem() {
		return mapDeviceTabItem;
	}

	public Map<String, SingleCarparkDevice> getMapIpToDevice() {
		return mapIpToDevice;
	}

	public Map<SystemSettingTypeEnum, String> getMapSystemSetting() {
		return mapSystemSetting;
	}

	public Map<String, Date> getMapPlateNoDate() {
		return mapPlateNoDate;
	}

	public Map<String, Boolean> getMapOpenDoor() {
		return mapOpenDoor;
	}

	public Map<String, Date> getMapHandPhotograph() {
		return mapHandPhotograph;
	}

	public Map<String, CarInTask> getMapInTwoCameraTask() {
		return mapInTwoCameraTask;
	}

	public Map<String, CarOutTask> getMapOutTwoCameraTask() {
		return mapOutTwoCameraTask;
	}

	public Map<String, Boolean> getMapIsTwoChanel() {
		return mapIsTwoChanel;
	}

	public Map<String, List<SingleCarparkDevice>> getMapTypeDevices() {
		return mapTypeDevices;
	}

	public Map<String, CarparkCarType> getMapTempCharge() {
		return mapTempCharge;
	}

	public Map<String, Timer> getMapTwoChanelTimer() {
		return mapTwoChanelTimer;
	}

	public String getTotalSlotTooltip() {
		return totalSlotTooltip;
	}

	public void setTotalSlotTooltip(String totalSlotTooltip) {
		this.totalSlotTooltip = totalSlotTooltip;
		firePropertyChange("totalSlotTooltip", null, null);
	}

	public String getInBigImageName() {
		return inBigImageName;
	}

	public void setInBigImageName(String inBigImageName) {
		this.inBigImageName = inBigImageName;
		firePropertyChange("inBigImageName", null, null);
	}

	public String getOutBigImageName() {
		return outBigImageName;
	}

	public void setOutBigImageName(String outBigImageName) {
		this.outBigImageName = outBigImageName;
		firePropertyChange("outBigImageName", null, null);
	}

	public List<CarOutTask> getListOutTask() {
		return listOutTask;
	}

	public long getLastCarOutTime() {
		return lastCarOutTime;
	}

	public void setLastCarOutTime(long lastCarOutTime) {
		this.lastCarOutTime = lastCarOutTime;
	}

	public Map<String, CarOutTask> getMapOutCheck() {
		return mapOutCheck;
	}

	public Map<String, Boolean> getMapIpToDeviceStatus() {
		return mapIpToDeviceStatus;
	}

	public Map<String, CTabItem> getMapIpToTabItem() {
		return mapIpToTabItem;
	}

	public Lock getInOutLock() {
		return inOutLock;
	}

	public Map<String, Integer> getMapHCameraPlayHandle() {
		return mapHCameraPlayHandle;
	}

	public void setVisitor(SingleCarparkVisitor visitor) {
		this.visitor=visitor;
	}

	public SingleCarparkVisitor getVisitor() {
		return visitor;
	}

	public Map<String, SingleCarparkInOutHistory> getMapWaitInOutHistory() {
		return new MyHashMap1<>(mapWaitInOutHistory.asMap());
//		return mapWaitInOutHistory.asMap();
	}

	public Cache<String, String> getPlateColorCache() {
		return plateColorCache;
	}

	public boolean equalsSetting(SystemSettingTypeEnum systemSettingTypeEnum, String string) {
		return mapSystemSetting.get(systemSettingTypeEnum).equals(string);
	}

	public boolean booleanSetting(SystemSettingTypeEnum systemSettingTypeEnum) {
		return Boolean.valueOf(mapSystemSetting.get(systemSettingTypeEnum));
	}
	
	public void printStack() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		for (int i = 0; i < stackTrace.length-2; i++) {
			StackTraceElement element = stackTrace[i];
			System.out.println(element.getClassName()+"=="+element.getMethodName()+"=="+element.getLineNumber());
		}
	}

	public int intSetting(SystemSettingTypeEnum systemSettingTypeEnum) {
		return Integer.valueOf(mapSystemSetting.get(systemSettingTypeEnum));
	}

	public Cache<String, Integer> getPlateOverSpeedSizeCache() {
		return plateOverSpeedSizeCache;
	}

	public float getTotalOnline() {
		return totalOnline;
	}

	public void setTotalOnline(float totalOnline) {
		this.totalOnline = totalOnline;
		firePropertyChange("totalOnline", null, null);
	}

	public List<CarCheckHistory> getCarChecks() {
		return carChecks;
	}

	public void addCarChecks(List<CarCheckHistory> carChecks) {
		this.carChecks.addAll(0, carChecks);
		if (this.carChecks.size()>200) {
			this.carChecks.remove(this.carChecks.size()-1);
		}
		firePropertyChange("carChecks", null, null);
	}

	public CarCheckHistory getCarCheck() {
		return carCheck;
	}

	public void setCarCheck(CarCheckHistory carCheck) {
		this.carCheck = carCheck;
		firePropertyChange("carCheck", null, null);
	}

	public void removeCarCheck(CarCheckHistory carCheck) {
		this.carChecks.remove(carCheck);
		firePropertyChange("carChecks", null, null);
	}

	public void setCarChecks(List<CarCheckHistory> carChecks) {
		this.carChecks.clear();
		this.carChecks.addAll(carChecks);
		firePropertyChange("carChecks", null, null);
	}

	public Map<String, App> getMapInOutWindow() {
		return mapInOutWindow;
	}
}
