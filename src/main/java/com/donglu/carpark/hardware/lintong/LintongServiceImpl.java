package com.donglu.carpark.hardware.lintong;

import java.util.Date;

import org.eclipse.swt.widgets.Shell;

import com.donglu.carpark.ui.view.message.MessageUtil;
import com.dongluhitec.card.domain.util.StrUtil;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

public class LintongServiceImpl implements LintongService {
	
	public static String charsetName = "GBK";
	public static int LINTONG_RETURN_SIZE = 20;
	private LintongApi api;

	public LintongServiceImpl() {
		NativeLibrary.addSearchPath("IgetMes", System.getProperty("user.dir")+"/native/lintong");
		api = (LintongApi) Native.loadLibrary("IgetMes", LintongApi.class);
	}

	@Override
	public Date getInDateByPlate(String plate) {
		byte[] bs=new byte[LINTONG_RETURN_SIZE];
		try {
			int getCarTimesFunc = api.GetCarTimesFunc(plate, bs);
			if (getCarTimesFunc>0) {
				String msg="接口运行错误";
				switch (getCarTimesFunc) {
				case 1:
					msg="连接失败";
					break;
				case 2:
					msg="没有记录";
					break;
				default:
					break;
				}
				MessageUtil.info("获取车牌：" + plate + " 的扫描时间是发生错误，错误码：" + getCarTimesFunc+"-"+msg,60000);
			}
			return StrUtil.parseDateTime(new String(bs).trim());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void deletePlate(String plate){
		api.DelCarInfoFunc(plate);
	}
	
	public static void main(String[] args) throws Exception {
		new Shell().open();
		MessageUtil.info("====dads");
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(200000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

}
