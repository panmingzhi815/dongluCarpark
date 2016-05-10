package com.donglu.carpark.ui.task;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.CarparkMainPresenter;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.util.StrUtil;

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
		logger.debug("生成车牌：{}的抓拍图片位置", plateNO);
		String imageSavefolder = StrUtil.formatDate(date, "yyyy/MM/dd/HH");
		String fileName = StrUtil.formatDate(date, "yyyyMMddHHmmssSSS");
		bigImgFileName = fileName + "_" + plateNO + "_big.jpg";
		smallImgFileName = fileName + "_" + plateNO + "_small.jpg";
		smallImgFileName = imageSavefolder + "/" + smallImgFileName;
		bigImgFileName=imageSavefolder + "/" + bigImgFileName;
		logger.info("生成车牌：{}的抓拍图片位置：{}--{}", smallImgFileName,bigImgFileName);
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
