package com.donglu.carpark.server;

import org.eclipse.jetty.servlet.ServletHandler;

import com.donglu.carpark.server.servlet.CarPayServlet;
import com.donglu.carpark.server.servlet.CarparkDeviceServlet;
import com.donglu.carpark.server.servlet.CarparkHttpServiceServlet;
import com.donglu.carpark.server.servlet.CarparkServlet;
import com.donglu.carpark.server.servlet.ImageUploadServlet;
import com.donglu.carpark.server.servlet.InOutServlet;
import com.donglu.carpark.server.servlet.IpmsServlet;
import com.donglu.carpark.server.servlet.PlateSubmitServlet;
import com.donglu.carpark.server.servlet.ServerServlet;
import com.donglu.carpark.server.servlet.ShanghaiYunCarparkServlet;
import com.donglu.carpark.server.servlet.StoreServiceServlet;
import com.donglu.carpark.server.servlet.StoreServlet;
import com.donglu.carpark.server.servlet.UpdatePositionServlet;
import com.donglu.carpark.server.servlet.UserServlet;
import com.donglu.carpark.service.background.StoreServerLinkServiceI;
import com.donglu.carpark.service.impl.UploadServiceImpl;
import com.donglu.carpark.ui.servlet.WebSocketServer;
import com.dongluhitec.card.server.ServerUtil;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class CarparkDBServer {
	@Inject
	private Provider<UserServlet> userServlerProvider;
	@Inject
	private Provider<CarparkServlet> carparkServletProvider;
	@Inject
	private Provider<InOutServlet> inOutServletProvider;
	@Inject
	private Provider<ImageUploadServlet> imageServletProvider;
	@Inject
	private Provider<StoreServlet> storeServletProvider;
	@Inject
	private Provider<ServerServlet> serverServletProvider;
	@Inject
	private Provider<StoreServiceServlet> storeServiceServletProvider;
	@Inject
	private Provider<PlateSubmitServlet> plateSubmitServletProvider;
	@Inject
	private Provider<UpdatePositionServlet> updatePositionServletProvider;
	@Inject
	private Provider<UploadServiceImpl> uploadServiceProvider;
	@Inject
	private Provider<StoreServerLinkServiceI> storeServerLinkServiceProvider;
	@Inject
	private Provider<IpmsServlet> ipmsServletProvider;
	@Inject
	private Provider<CarPayServlet> carPayServletProvider;
	@Inject
	private Provider<ShanghaiYunCarparkServlet> shanghaiYunCarparkServletProvider;
	
	@Inject
	private Provider<CarparkHttpServiceServlet> carparkHttpServiceServletProvider;
	@Inject
	private Provider<CarparkDeviceServlet> carparkDeviceServletProvider;
	
	public void startDbServlet(ServletHandler handler){
		ServerUtil.startServlet("/user/*", handler, userServlerProvider);
		ServerUtil.startServlet("/carpark/*", handler, carparkServletProvider);
		ServerUtil.startServlet("/inout/*", handler, inOutServletProvider);
		ServerUtil.startServlet("/storeservice/*", handler, storeServiceServletProvider);
		ServerUtil.startServlet("/carparkImage/*", handler, imageServletProvider);
		ServerUtil.startServlet("/store/*", handler, storeServletProvider);
		ServerUtil.startServlet("/server/*", handler, serverServletProvider);
		ServerUtil.startServlet("/plateSubmit/*", handler, plateSubmitServletProvider);
		ServerUtil.startServlet("/positionUpdate/*", handler, updatePositionServletProvider);
		ServerUtil.startServlet("/upload/*", handler, uploadServiceProvider);
		ServerUtil.startServlet("/ipms/*", handler, ipmsServletProvider);
		ServerUtil.startServlet("/carPay/*", handler, carPayServletProvider);
		ServerUtil.startServlet("/shanghaiYunCarpark/*", handler, shanghaiYunCarparkServletProvider);
		ServerUtil.startServlet("/carparkDeviceService/*", handler, carparkDeviceServletProvider);
		
		if (System.getProperty("startHttpService", "false").equals("true")) {
			ServerUtil.startServlet("/carparkHttpService/*", handler, carparkHttpServiceServletProvider);
		}
    	new WebSocketServer(16666).start();	
	}
	
	public void startBackgroudService(){
		storeServerLinkServiceProvider.get().startAsync();
	}
}
