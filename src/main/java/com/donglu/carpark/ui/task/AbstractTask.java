package com.donglu.carpark.ui.task;

import static org.hamcrest.CoreMatchers.instanceOf;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.CarparkMainPresenter;
import com.donglu.carpark.ui.view.message.MessageUtil;
import com.donglu.carpark.util.CarparkUtils;
import com.dongluhitec.card.domain.db.singlecarpark.Holiday;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
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
			if (e.getCause() instanceof ConnectException) {
				MessageUtil.info(type+"错误","服务器连接失败,请检查网络！",60000);
			}else{
				MessageUtil.info(type+"错误",e.getMessage(),60000);
			}
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
	
	public boolean checkInTime() {
		File file = new File("临时车限时.txt");
		if (file.exists()) {
			try {
				String s = null;
				List<String> list = Files.readAllLines(file.toPath());
				logger.info("获取到临时车限时设置：{}",list);
				if (StrUtil.isEmpty(list)) {
					return false;
				}
				if (list.size()==1) {
					Files.write(file.toPath(), Arrays.asList(list.get(0),"00:00-00:00;00:00-00:00"));
					list.add("00:00-00:00;00:00-00:00");
				}
				s=list.get(0);
				Holiday holiday = sp.getCarparkService().findHolidayByDate(date);
				if (holiday!=null) {
					s=list.get(1);
				}
				
				String[] times = s.split(";");
				for (String string : times) {
					if (StrUtil.isEmpty(string)) {
						continue;
					}
					String[] split = string.split("-");
					String[] split2 = split[0].split(":");
					String[] split3 = split[1].split(":");
					if(new DateTime(date).withTime(Integer.valueOf(split2[0]), Integer.valueOf(split2[1]), 0, 0).isBeforeNow()&&new DateTime(date).withTime(Integer.valueOf(split3[0]), Integer.valueOf(split3[1]), 0, 0).isAfterNow()) {
						return true;
					}
				}
			} catch (Exception e) {
				
			}
		}else {
			try {
				Files.createFile(file.toPath());
				Files.write(file.toPath(), Arrays.asList("00:00-00:00;00:00-00:00","00:00-00:00;00:00-00:00"));
			} catch (Exception e) {
				
			}
		}
		return false;
	}
	/**
	 * 特殊车辆检测自动放行
	 * @return
	 */
	public boolean checkSpecialCar() {
		if (model.booleanSetting(SystemSettingTypeEnum.特殊车辆自动放行)) {
			if (!StrUtil.isEmpty(plateNO)) {
				return plateNO.matches(model.getMapSystemSetting().get(SystemSettingTypeEnum.特殊车辆车牌类型));
			}
		}
		return false;
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
