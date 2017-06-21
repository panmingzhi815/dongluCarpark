package com.donglu.carpark.ui.task;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.CarparkMainPresenter;
import com.donglu.carpark.util.CarparkUtils;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;

public abstract class AbstractTask implements Runnable {
	static final Logger logger = LoggerFactory.getLogger(AbstractTask.class);
	protected CarparkMainModel model;
	protected CarparkDatabaseServiceProvider sp;
	protected CarparkMainPresenter presenter;
	protected String ip;
	protected String plateNO;
	protected byte[] bigImage;
	protected byte[] smallImage;
	protected Float rightSize;
	// 修改的车牌
	protected String editPlateNo;
	// 车辆性质
	protected String carType = "临时车";
	// 车辆记录
	protected SingleCarparkInOutHistory cch;
	// 进场设备
	protected SingleCarparkDevice device;
	// 进场停车场
	protected SingleCarparkCarpark carpark;
	// 进场时间
	protected Date date = new Date();
	// 查找到的用户，判断是否为固定用户
	protected SingleCarparkUser user;
	// 小图片名称
	protected String smallImgFileName;
	// 大图片名称
	protected String bigImgFileName;

	protected String type = "进场";
	// 保存车牌最近的处理时间
	protected final Map<String, Date> mapPlateNoDate;

	public AbstractTask(CarparkMainModel model, CarparkDatabaseServiceProvider sp, CarparkMainPresenter presenter, String ip, String plateNO, byte[] bigImage, byte[] smallImage, Float rightSize) {
		super();
		this.model = model;
		this.sp = sp;
		this.presenter = presenter;
		this.ip = ip;
		this.plateNO = plateNO;
		this.bigImage = bigImage;
		this.smallImage = smallImage;
		this.rightSize = rightSize;
		editPlateNo=plateNO;
		device = model.getMapIpToDevice().get(ip);
		mapPlateNoDate = model.getMapPlateNoDate();
	}

	@Override
	public void run() {
		try {
			start();
		} catch (Exception e) {
			logger.error("车辆"+type+"时发生错误",e);
		}

	}

	protected abstract void start() throws Exception;
	/**
	 * 
	 */
	protected void initImgPath() {
		//图片保存文件夹
		logger.debug("准备生成车牌：{}的抓拍图片位置", plateNO);
		smallImgFileName = CarparkUtils.FormatImagePath(date,plateNO,false);
		bigImgFileName=CarparkUtils.FormatImagePath(date,plateNO,true);
		logger.debug("生成车牌：{}的抓拍图片位置：{}--{}", smallImgFileName,bigImgFileName);
		saveImage();
	}
	/**
	 * 保存图片
	 */
	public void saveImage() {
		logger.debug("开始保存车牌：{}的图片", plateNO);
		mapPlateNoDate.put(plateNO, date);
		presenter.saveImage(device,smallImgFileName,bigImgFileName,smallImage, bigImage);
	}

	public String getPlateNO() {
		return plateNO;
	}

	public void setPlateNO(String plateNO) {
		this.plateNO = plateNO;
	}

	public byte[] getBigImage() {
		return bigImage;
	}

	public void setBigImage(byte[] bigImage) {
		this.bigImage = bigImage;
	}

	public byte[] getSmallImage() {
		return smallImage;
	}

	public void setSmallImage(byte[] smallImage) {
		this.smallImage = smallImage;
	}

	public Float getRightSize() {
		return rightSize;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setRightSize(Float rightSize) {
		this.rightSize = rightSize;
	}

	public SingleCarparkUser getUser() {
		return user;
	}

	public void setUser(SingleCarparkUser user) {
		this.user = user;
	}

	public SingleCarparkInOutHistory getCch() {
		return cch;
	}

	public void setCch(SingleCarparkInOutHistory cch) {
		this.cch = cch;
	}

	public SingleCarparkDevice getDevice() {
		return device;
	}

	public CarparkDatabaseServiceProvider getSp() {
		return sp;
	}

	public String getEditPlateNo() {
		return editPlateNo;
	}

	public void setEditPlateNo(String editPlateNo) {
		this.editPlateNo = editPlateNo;
	}

	public String getSmallImgFileName() {
		return smallImgFileName;
	}

	public String getBigImgFileName() {
		return bigImgFileName;
	}
}
