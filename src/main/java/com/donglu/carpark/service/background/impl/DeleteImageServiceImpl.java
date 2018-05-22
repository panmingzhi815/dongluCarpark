package com.donglu.carpark.service.background.impl;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
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
		super(Scheduler.newFixedDelaySchedule(5, 60 * 24, TimeUnit.SECONDS), "自动删除图片");
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
	List<File> listNotDeleteFiles=new ArrayList<>();
	/**
	 * 锁定进场
	 */
	private void loadInHistoryImage() {
		try {
    		List<SingleCarparkInOutHistory> findByCondition = sp.getCarparkInOutService().findCarInHistorys(Integer.MAX_VALUE);
    		for (SingleCarparkInOutHistory inOut : findByCondition) {
    			String imaFileName =clientImageSavePath+inOut.getBigImg();
				File file = new File(imaFileName);
				listNotDeleteFiles.add(file);
    		}
		} catch (Exception e) {
			log.debug("锁定进场车辆图片时发生错误:{}"+e);
		}
		
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
		SingleCarparkSystemSetting ss2 = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.图片保存多少天.name());
		int saveMonth = Integer.valueOf(ss2 == null ? SystemSettingTypeEnum.图片保存多少天.getDefaultValue() : ss2.getSettingValue());
		log.info("图片保存多少天设置为{}", saveMonth);
		String imgSavePath = (String) CarparkFileUtils.readObject(ConstUtil.CLIENT_IMAGE_SAVE_FILE_PATH);
		if (!StrUtil.isEmpty(imgSavePath)) {
			clientImageSavePath = imgSavePath + "/img/";
		}
		Date d = new Date();
		DateTime deleteTime = new DateTime(d).minusDays(saveMonth + 1);
		File file=new File(clientImageSavePath);
		List<File> listWaitDeleteFile=new ArrayList<>();
		for (File file2 : file.listFiles()) {
			if (!file2.isDirectory()) {
				continue;
			}
			for (File file3 : file2.listFiles()) {
				if (!file3.isDirectory()) {
					continue;
				}
				File[] listFiles = file3.listFiles();
				for (File file4 : listFiles) {
					String s=file2.getName()+file3.getName()+file4.getName();
//					System.out.println(s+"==="+file4);
					try {
						if (Integer.valueOf(s)>Integer.valueOf(deleteTime.toString("yyyyMMdd"))) {
							continue;
						}
						listWaitDeleteFile.addAll(getDirFiles(file4));
					} catch (Exception e) {
						
					}
				}
			}
		}
		System.out.println(listWaitDeleteFile.size());
		if (listWaitDeleteFile.size()<=0) {
			return;
		}
		loadInHistoryImage();
		for (int i = 0;true; i++){
			List<SingleCarparkInOutHistory> list = sp.getCarparkInOutService().findHistoryByIn(i*50, 50, null, null, null, null);
			for (SingleCarparkInOutHistory inOut : list) {
    			String imaFileName =clientImageSavePath+inOut.getBigImg();
				File f = new File(imaFileName);
				listWaitDeleteFile.remove(f);
    		}
			if (list.size()<50) {
				break;
			}
		}
		for (File f : listWaitDeleteFile) {
			f.delete();
		}
	}
	
	public static void main(String[] args) {
		File file = new File("D:\\img\\2018\\04\\23");
		List<File> list = getDirFiles(file);
		System.out.println(list.size());
	}
	
	/**
	 * @param file
	 * @return 
	 */
	public static List<File> getDirFiles(File file) {
		List<File> list = new ArrayList<>();
		getDirFiles(list,file);
		return list;
	}
	/**
	 * @param list 
	 * @param file
	 * @return
	 */
	public static void getDirFiles(List<File> list, File file) {
		for (File f : file.listFiles()) {
			if (f.isDirectory()) {
				getDirFiles(list, f);
			}else{
				System.out.println(f.getName());
				list.add(f);
			}
		}
	}
}
