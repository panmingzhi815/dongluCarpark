package com.dongluhitec.card.domain.db.singlecarpark.haiyu;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.util.StrUtil;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by xiaopan on 2016/8/27.
 */
@Entity
public class CarparkRecordHistory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    private String userName;
    private String plateNO;
    private String inTime;
    private String inDevice;
    private String outTime;
    private String outDevice;
    private Float shouldMoney;
    private Float factMoney;
    private String carparkName;
    private Long hid;
    private String inImage;
    private String outImage;
    private String carType;
    
    private String userType;
    private Long carparkId;
    
    private String inDeviceId;
	private String outDeviceId;
	
	private String plateColor;
	
	private Integer leftSlot=0;

    @Embedded
    private HistoryDetail historyDetail = new HistoryDetail();

	private Integer chargedType;

    public CarparkRecordHistory() {
    }

    public CarparkRecordHistory(SingleCarparkInOutHistory carparkInOutHistory,UpdateEnum updateEnum){
        this.userName = StrUtil.isEmptyToString(carparkInOutHistory.getUserName(),"");
        this.plateNO = StrUtil.isEmptyToString(carparkInOutHistory.getPlateNo(), "");
        this.inTime = StrUtil.isEmptyToString(StrUtil.formatDateTime(carparkInOutHistory.getInTime()), "");
        this.outTime = StrUtil.isEmptyToString(StrUtil.formatDateTime(carparkInOutHistory.getOutTime()),"");
        this.inDevice = StrUtil.isEmptyToString(carparkInOutHistory.getInDevice(), "");
        this.outDevice = StrUtil.isEmptyToString(carparkInOutHistory.getOutDevice(), "");
        this.shouldMoney = carparkInOutHistory.getShouldMoney() == null ? 0F : carparkInOutHistory.getShouldMoney();
        this.factMoney = carparkInOutHistory.getFactMoney() == null ? 0F : carparkInOutHistory.getFactMoney();
        this.carparkName = StrUtil.isEmptyToString(carparkInOutHistory.getCarparkName(), "");
        hid=carparkInOutHistory.getId();
        inImage=carparkInOutHistory.getBigImg();
        outImage=carparkInOutHistory.getOutBigImg();
        carType=carparkInOutHistory.getCarType();
        userType=carparkInOutHistory.getUserType();
        carparkId=carparkInOutHistory.getCarparkId();
        this.historyDetail.setUpdateState(updateEnum);
        inDeviceId=carparkInOutHistory.getInDeviceId();
        outDeviceId=carparkInOutHistory.getOutDeviceId();
        chargedType = carparkInOutHistory.getChargedType();
        setLeftSlot(carparkInOutHistory.getLeftSlot());
    	setPlateColor(carparkInOutHistory.getPlateColor());
    }
    public SingleCarparkInOutHistory getHistory(){
    	SingleCarparkInOutHistory carparkInOutHistory=new SingleCarparkInOutHistory();
    	carparkInOutHistory.setUserName(userName);
    	carparkInOutHistory.setPlateNo(plateNO);
    	carparkInOutHistory.setInTime(StrUtil.parseDateTime(inTime));
    	carparkInOutHistory.setOutTime(StrUtil.parseDateTime(outTime));
    	carparkInOutHistory.setInDevice(inDevice);
    	carparkInOutHistory.setOutDevice(outDevice);
    	carparkInOutHistory.setShouldMoney(shouldMoney);
    	carparkInOutHistory.setFactMoney(factMoney);
    	carparkInOutHistory.setCarparkName(carparkName);
    	carparkInOutHistory.setId(hid);
    	carparkInOutHistory.setBigImg(inImage);
    	carparkInOutHistory.setOutBigImg(outImage);
    	carparkInOutHistory.setCarType(carType);
    	carparkInOutHistory.setUserType(userType);
    	carparkInOutHistory.setCarparkId(carparkId);
    	carparkInOutHistory.setInDeviceId(inDeviceId);
    	carparkInOutHistory.setOutDeviceId(outDeviceId);
    	carparkInOutHistory.setLeftSlot(getLeftSlot());
    	carparkInOutHistory.setPlateColor(getPlateColor());
    	carparkInOutHistory.setChargedType(chargedType);
		return carparkInOutHistory;
    }

    @PrePersist
    public void prePersist(){
        this.historyDetail.setUpdateTime(new Date());
        this.historyDetail.setProcessTime(null);
        this.historyDetail.setProcessState(ProcessEnum.未处理);
    }

    @PreUpdate
    public void preUpdate(){
        this.historyDetail.setProcessTime(new Date());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPlateNO() {
        return plateNO;
    }

    public void setPlateNO(String plateNO) {
        this.plateNO = plateNO;
    }

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public String getInDevice() {
        return inDevice;
    }

    public void setInDevice(String inDevice) {
        this.inDevice = inDevice;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    public String getOutDevice() {
        return outDevice;
    }

    public void setOutDevice(String outDevice) {
        this.outDevice = outDevice;
    }

    public Float getShouldMoney() {
        return shouldMoney;
    }

    public void setShouldMoney(Float shouldMoney) {
        this.shouldMoney = shouldMoney;
    }

    public Float getFactMoney() {
        return factMoney;
    }

    public void setFactMoney(Float factMoney) {
        this.factMoney = factMoney;
    }

    public String getCarparkName() {
        return carparkName;
    }

    public void setCarparkName(String carparkName) {
        this.carparkName = carparkName;
    }

    public HistoryDetail getHistoryDetail() {
        return historyDetail;
    }

    public void setHistoryDetail(HistoryDetail historyDetail) {
        this.historyDetail = historyDetail;
    }

	public Long getHid() {
		return hid;
	}

	public void setHid(Long hid) {
		this.hid = hid;
	}

	public String getInImage() {
		return inImage;
	}

	public void setInImage(String inImage) {
		this.inImage = inImage;
	}

	public String getOutImage() {
		return outImage;
	}

	public void setOutImage(String outImage) {
		this.outImage = outImage;
	}

	public String getCarType() {
		return carType;
	}

	public void setCarType(String carType) {
		this.carType = carType;
	}

	public String getUserType() {
		if (userType==null) {
			return "小车";
		}
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public Long getCarparkId() {
		return carparkId;
	}

	public void setCarparkId(Long carparkId) {
		this.carparkId = carparkId;
	}

	public String getInDeviceId() {
		return inDeviceId;
	}

	public void setInDeviceId(String inDeviceId) {
		this.inDeviceId = inDeviceId;
	}

	public String getOutDeviceId() {
		return outDeviceId;
	}

	public void setOutDeviceId(String outDeviceId) {
		this.outDeviceId = outDeviceId;
	}

	public String getPlateColor() {
		return plateColor;
	}

	public void setPlateColor(String plateColor) {
		this.plateColor = plateColor;
		//firePropertyChange("plateColor", null, null);
	}

	public Integer getLeftSlot() {
		return leftSlot;
	}

	public void setLeftSlot(Integer leftSlot) {
		this.leftSlot = leftSlot;
		//firePropertyChange("leftSlot", null, null);
	}
}
