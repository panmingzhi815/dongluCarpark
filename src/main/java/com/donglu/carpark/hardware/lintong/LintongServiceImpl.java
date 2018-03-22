package com.donglu.carpark.hardware.lintong;

import java.util.Date;

import org.eclipse.swt.widgets.Shell;

import com.donglu.carpark.ui.view.message.MessageUtil;
import com.dongluhitec.card.domain.util.StrUtil;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

public class LintongServiceImpl implements LintongService {
	
	private LintongApi api;

	public LintongServiceImpl() {
		NativeLibrary.addSearchPath("IgetMes", System.getProperty("user.dir")+"/native/lintong");
		api = (LintongApi) Native.loadLibrary("IgetMes", LintongApi.class);
	}

	@Override
	public Date getInDateByPlate(String plate) {
		byte[] bs=new byte[32];
		try {
			int getCarTimesFunc = api.GetCarTimesFunc(plate.getBytes("GBK"), bs);
			if (getCarTimesFunc>0) {
				MessageUtil.info("获取车牌：" + plate + " 的扫描时间是发生错误，错误码：" + getCarTimesFunc);
			}
			return StrUtil.parseDateTime(new String(bs).trim());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
