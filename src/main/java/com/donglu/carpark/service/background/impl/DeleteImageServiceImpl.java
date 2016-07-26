package com.donglu.carpark.service.background.impl;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.background.AbstractCarparkBackgroundService;
import com.donglu.carpark.service.background.DeleteImageServiceI;
import com.donglu.carpark.util.CarparkFileUtils;
import com.donglu.carpark.util.CarparkUtils;
import com.donglu.carpark.util.ConstUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemSetting;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class DeleteImageServiceImpl extends AbstractCarparkBackgroundService implements DeleteImageServiceI {
	public DeleteImageServiceImpl() {
		super(Scheduler.newFixedDelaySchedule(5, 60 * 24, TimeUnit.MINUTES), "自动删除图片");
	}
	@Inject
	private CarparkDatabaseServiceProvider sp;
	private String clientImageSavePath=System.getProperty("user.dir") + "/img/";


	@Override
	protected void run() {
		SingleCarparkSystemSetting ss1 = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.是否自动删除图片.name());
		if (StrUtil.isEmpty(ss1) || ss1.getSettingValue().equals("false")) {
			log.info("是否自动删除图片设置为{}", StrUtil.isEmpty(ss1)?null:ss1.getSettingValue());
			return;
		}
		clientDeleteImage();
		if (CarparkUtils.checkServerIsLocal()) {
			serverDeleteImage();
		}
	}
	Map<String, FileLock> mapFileImgToLock=new HashMap<>();
	Map<String, RandomAccessFile> mapFileRaf=new HashMap<>();
	/**
	 * 锁定进场
	 */
	private void lockInHistoryImage() {
		List<SingleCarparkInOutHistory> findByCondition = sp.getCarparkInOutService().findCarInHistorys(Integer.MAX_VALUE);
		for (SingleCarparkInOutHistory inOut : findByCondition) {
			String imaFileName =clientImageSavePath+inOut.getBigImg();
			try {
				RandomAccessFile raf = new RandomAccessFile(new File(imaFileName), "rw");
				FileChannel channel = raf.getChannel();
				FileLock tryLock = channel.tryLock();
				mapFileImgToLock.put(imaFileName, tryLock);
				mapFileRaf.put(imaFileName, raf);
			} catch (Exception e) {
				log.error("锁定进场车辆图片时发生错误",e);
			}
		}
		
	}
	private void unLockInHistoryImage(){
		for (String imgName : mapFileImgToLock.keySet()) {
			try {
				FileLock fileLock = mapFileImgToLock.get(imgName);
				fileLock.release();
				mapFileRaf.get(imgName).close();
			} catch (IOException e) {
				log.error("解锁进场车辆图片时发生错误",e);
			}
		}
		mapFileImgToLock.clear();
		mapFileRaf.clear();
	}

	/**
	 * 服务器删除图片
	 */
	private void serverDeleteImage() {
		String serverImageSavePath=System.getProperty("user.dir") + "/img/";
		SingleCarparkSystemSetting findSystemSettingByKey = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.图片保存位置.name());
		if (!StrUtil.isEmpty(findSystemSettingByKey)&&!StrUtil.isEmpty(findSystemSettingByKey.getSettingValue())) {
			serverImageSavePath=findSystemSettingByKey.getSettingValue()+"/img/";
		}
		if (clientImageSavePath!=null&&!clientImageSavePath.equals(serverImageSavePath)) {
			clientImageSavePath=serverImageSavePath;
			clientDeleteImage();
		}
	}

	/**
	 * 删除图片
	 */
	private void clientDeleteImage() {
		lockInHistoryImage();
		SingleCarparkSystemSetting ss2 = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.图片保存多少天.name());
		int saveMonth = Integer.valueOf(ss2 == null ? SystemSettingTypeEnum.图片保存多少天.getDefaultValue() : ss2.getSettingValue());
		log.info("图片保存多少天设置为{}", saveMonth);
		String imgSavePath = (String) CarparkFileUtils.readObject(ConstUtil.CLIENT_IMAGE_SAVE_FILE_PATH);
		if (!StrUtil.isEmpty(imgSavePath)) {
			clientImageSavePath = imgSavePath + "/img/";
		}
		Date d = new Date();
		DateTime deleteTime = new DateTime(d).minusDays(saveMonth + 1);
		String nowMonth=deleteTime.toString("MM");
		String day = deleteTime.toString("dd");
		String month = deleteTime.toString("MM");
		int year = deleteTime.getYear();
		int nowYear=deleteTime.getYear();
		File file;
		while (true) {
			String pathname = clientImageSavePath + year + "/" + month;
			log.info("检测文件夹{}是否存在", pathname);
			file = new File(pathname);
			if (file.isDirectory()) {
				log.info("文件夹{}存在,准备删除文件夹", pathname);
				if (month.equals(nowMonth)) {
					for (File f : file.listFiles()) {
						try {
							if (Integer.valueOf(f.getName())<=Integer.valueOf(day)) {
								CarparkUtils.deleteDir(f);
							}
						} catch (NumberFormatException e) {
							continue;
						}
					}
				}else{
					CarparkUtils.deleteDir(file);
				}
			} else {
				while (true) {
					String pathname1 = clientImageSavePath + (year);
					log.info("检测文件夹{}是否存在", pathname1);
					file = new File(pathname1);
					if (file.isDirectory()) {
						log.info("文件夹{}存在,准备删除文件夹", pathname1);
						if (year==nowYear) {
							for (File f : file.listFiles()) {
								try {
									if (Integer.valueOf(f.getName())<=Integer.valueOf(month)) {
										CarparkUtils.deleteDir(f);
									}
								} catch (NumberFormatException e) {
									continue;
								}
							}
						}else{
							CarparkUtils.deleteDir(file);
						}
					}else{
						log.info("文件夹{}不存在,退出任务", pathname1);
						break;
					}
					year-=1;
				}
				break;
			}
			month=deleteTime.minusMonths(1).toString("MM");
		}
		unLockInHistoryImage();
	}
}
