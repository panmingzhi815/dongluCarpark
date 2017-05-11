package com.donglu.carpark.service.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.server.CarparkServerConfig;
import com.donglu.carpark.service.PlateSubmitServiceI;
import com.donglu.carpark.util.CarparkDataBaseUtil;
import com.donglu.carpark.util.CarparkUtils;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.util.ThreadUtil;
import com.google.common.io.Files;

public class PlateSubmitServiceImpl implements PlateSubmitServiceI {
	Logger logger = LoggerFactory.getLogger(getClass());
	
	private ExecutorService plateSubmitThreadPool;

	@Override
	public void submitPlate(final String plateNO, final Date date, final byte[] image,  final SingleCarparkDevice device) {
		if (plateSubmitThreadPool==null) {
			plateSubmitThreadPool = Executors.newFixedThreadPool(2, ThreadUtil.createThreadFactory("车牌报送任务"));
		}
		Runnable runnable = new Runnable() {
			public void run() {
				try {
					logger.info("准备对车牌{}进行车牌报送",plateNO);
					SingleCarparkCarpark carpark = device.getCarpark();
					String folder = "D:\\Pic\\" + StrUtil.formatDate(date, "yyyyMMdd");
					String inOutType = device.getInType().indexOf("进口") > -1 ? "01" : "11";
					String imageId = StrUtil.formatDate(date, "yyyyMMddHHmmss") + device.getIdentifire() + carpark.getCode() + inOutType;
					String imageFileName = imageId + ".jpg";
					saveImage(folder, imageFileName, image);
					String sql = "INSERT INTO [platepark].[dbo].[plateinfo] ([CPHM] ,[CPLX] ,[TGSJ],[TCCID],[JCKLX],[JCKBH],[TPZS],[TPID1],[CPCD1],[SFBC]) "
							+ "VALUES ('{}','0','{}','{}','{}','{}','1','{}',{},0);";
					String inOrOut = inOutType.equals("01") ? "0" : "1";
					sql = CarparkUtils.formatString(sql, plateNO, StrUtil.formatDateTime(date), carpark.getCode(), inOrOut, device.getIdentifire(), imageId, image.length);
					logger.info("报送数据插入脚本：{}",sql);
					boolean executeSQL = CarparkDataBaseUtil.executeSQL(sql, "platepark", CarparkServerConfig.getInstance());
					logger.info("对车牌{}进行车牌报送,结果：{}",plateNO,executeSQL);
					if (!executeSQL) {
						plateSubmitThreadPool.submit(this);
					}
				} catch (Throwable e) {
					logger.error("车牌报送保存数据时发生错误",e);
				}
			}
		};
		plateSubmitThreadPool.submit(runnable);
	}

	private void saveImage(String folder, String imageFileName, byte[] image) throws IOException {
		File f=new File(folder);
		File imgFile=new File(folder+"\\"+imageFileName);
		if (!f.isDirectory()) {
			Files.createParentDirs(imgFile);
		}
		if (!imgFile.exists()) {
			imgFile.createNewFile();
		}
		saveImg(imgFile.getPath(), image);
	}
	public void saveImg(String filePath, byte[] data) {
		try(FileOutputStream fos = new FileOutputStream(filePath);
			BufferedOutputStream bos = new BufferedOutputStream(fos);){
			bos.write(data);
			bos.flush();
		}catch (Exception e){
			logger.error("保存图片失败:{}",filePath,e);
		}
	}
	
	public static void main(String[] args) {
		ExecutorService service = Executors.newFixedThreadPool(2);
		for (int j = 0; j < 100; j++) {
			for (int i = 0; i < 4; i++) {
				String s=String.format("第%s第%s个",j,i);
				Runnable runnable = new Runnable() {
					public void run() {
						String sql = "INSERT INTO [platepark].[dbo].[plateinfo] ([CPHM] ,[CPLX] ,[TGSJ],[TCCID],[JCKLX],[JCKBH],[TPZS],[TPID1],[CPCD1],[SFBC]) VALUES ('浙A2NM69','0','2017-04-27 11:48:15','101A0582','1','02','1','2017042711481502101A058211',227704,0);";
						CarparkServerConfig instance = CarparkServerConfig.getInstance();
						instance.setDbServerPassword("1");
						boolean executeSQL = CarparkDataBaseUtil.executeSQL(sql, "platepark", instance);
						System.out.println(Thread.currentThread().getName() + "="+s+"=" + executeSQL);
					}
				};
				service.submit(runnable);
			}
			System.out.println("添加数据："+j);
		}
	}
}
