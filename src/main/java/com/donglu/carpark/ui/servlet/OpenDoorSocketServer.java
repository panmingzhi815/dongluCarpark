package com.donglu.carpark.ui.servlet;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.ui.CarparkMainPresenter;
import com.donglu.carpark.ui.view.message.MessageUtil;
import com.donglu.carpark.util.ExecutorsUtils;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.util.StrUtil;

public class OpenDoorSocketServer {
	final static Logger LOGGER=LoggerFactory.getLogger(OpenDoorSocketServer.class);
	private CarparkMainPresenter presenter;
	private int port;
	private ServerSocket serverSocket;

	public OpenDoorSocketServer(CarparkMainPresenter presenter,int port) {
		super();
		this.presenter = presenter;
		this.port = port;
	}
	public void start() {
		try {
			serverSocket = new ServerSocket(port);
			ExecutorsUtils.scheduleWithFixedDelay(new Runnable() {
				@Override
				public void run() {
					try {
						Socket client = serverSocket.accept();
						byte[] bs = new byte[64];
						String ip = client.getInetAddress().getHostAddress();
						int read = client.getInputStream().read(bs);
						client.getOutputStream().write(new byte[] {1});
						client.close();
						String msg = new String(bs,0,read);
						LOGGER.info("获取到遥控开闸信号：{}-{}",ip,msg);
						String[] split = msg.split("-");
						if(split.length>2) {
							return;
						}
						Map<String, SingleCarparkDevice> map = presenter.getModel().getMapIpToDevice();
						
						for (SingleCarparkDevice device : map.values()) {
							String linkAddress = device.getLinkAddress();
							if(StrUtil.isEmpty(linkAddress)) {
								continue;
							}
							String[] split2 = linkAddress.split(":");
							if(ip.equals(split2[0])) {
//								if(presenter.getModel().getMapOpenDoor().getOrDefault(device.getIp(),false)) {
//									LOGGER.info("正在开闸，忽略本次操作");
//									return;
//								}
								presenter.handOpenDoor(device.getIp(),false,"遥控开闸",null);
								return;
							}
						}
					} catch (Exception e) {
						
					}
				}
			}, 50, 50, TimeUnit.MILLISECONDS, "开闸记录上传服务器");
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						serverSocket.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}));
		} catch (IOException e) {
			MessageUtil.info("遥控记录服务启动失败,请检查端口10002是否占用！");
		}
	}
}
