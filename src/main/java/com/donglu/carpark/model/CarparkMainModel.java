package com.donglu.carpark.model;

import java.util.Date;

import org.eclipse.swt.graphics.Image;

import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.db.singlecarpark.CarTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.util.StrUtil;

public class CarparkMainModel extends DomainObject{
	//进口显示信息
	private String inShowPlateNO;
	private String inShowTime;
	private String inShowMeg;
	private Image inShowSmallImg;
	private Image inShowBigImg;
	
	//出口显示信息
	private String outShowPlateNO;
	private String outShowTime;
	private String outShowMeg;
	private Image outShowSmallImg;
	private Image outShowBigImg;
	private boolean outPlateNOEditable=false;
	
	
	String userName;
	String workTime;
	int totalSlot;
	int hoursSlot;
	int monthSlot;
	float totalCharge;
	float totalFree;
	
	
	String plateNo;
	String carUser;
	String carType;
	String inTime;
	String outTime;
	String totalTime;
	float shouldMony;
	float real;
	
	SingleCarparkInOutHistory history;
	
	String ip;
	
	boolean btnClick=false;
	boolean inCheckClick=false;
	boolean outCheckClick=false;
	boolean handSearch=false;
	
	boolean selectCarType=false;
	boolean comboCarTypeEnable=false;
	boolean comboCarTypeFocos=false;
	
	
	String carparkCarType;
	private String currentTime;
	
	//收费时的临时信息
	SingleCarparkInOutHistory chargeHistory;
	SingleCarparkDevice chargeDevice;
	
	//查询时的临时信息
	String searchPlateNo;
	String searchBigImage;
	String searchSmallImage;
	
	Boolean disContinue;
	
	private SingleCarparkCarpark carpark;
	private CarTypeEnum carTypeEnum;
	
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
	public SingleCarparkInOutHistory getHistory() {
		return history;
	}
	public void setHistory(SingleCarparkInOutHistory history) {
		this.history = history;
		if (pcs != null)
			pcs.firePropertyChange("history", null, null);
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
	public Image getInShowSmallImg() {
		return inShowSmallImg;
	}
	public void setInShowSmallImg(Image inShowSmallImg) {
		this.inShowSmallImg = inShowSmallImg;
		if (pcs != null)
			pcs.firePropertyChange("inShowSmallImg", null, null);
	}
	public Image getInShowBigImg() {
		return inShowBigImg;
	}
	public void setInShowBigImg(Image inShowBigImg) {
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
	public Image getOutShowSmallImg() {
		return outShowSmallImg;
	}
	public void setOutShowSmallImg(Image outShowSmallImg) {
		this.outShowSmallImg = outShowSmallImg;
		if (pcs != null)
			pcs.firePropertyChange("outShowSmallImg", null, null);
	}
	public Image getOutShowBigImg() {
		return outShowBigImg;
	}
	public void setOutShowBigImg(Image outShowBigImg) {
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
		this.carparkCarType=carparkCarType;
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
		this.currentTime=currentTime;
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
		// TODO 自动生成的方法存根
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
	public void setCartypeEnum(CarTypeEnum carTypeEnum) {
		this.carTypeEnum=carTypeEnum;
	}
	public CarTypeEnum getCarTypeEnum() {
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

}
