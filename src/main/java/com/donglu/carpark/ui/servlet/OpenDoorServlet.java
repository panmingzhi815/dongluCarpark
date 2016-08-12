package com.donglu.carpark.ui.servlet;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.ui.CarparkMainPresenter;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;

public class OpenDoorServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2258667355504265934L;
	private static final Logger logger = LoggerFactory.getLogger(OpenDoorServlet.class);
	private CarparkMainPresenter presenter;
	private CarparkMainModel model;
	private static Map<String, SingleCarparkDevice> mapControlIpToDevice = new HashMap<String, SingleCarparkDevice>();

	public OpenDoorServlet(CarparkMainPresenter presenter) {
		this.presenter = presenter;
		model = presenter.getModel();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String ip = getRemoteAddress(req);
		logger.info("控制器：{}获得设备遥控开闸信号",ip);
		SingleCarparkDevice device = mapControlIpToDevice.get(ip);
		if (device==null) {
			for (SingleCarparkDevice singleCarparkDevice : model.getMapIpToDevice().values()) {
				if (singleCarparkDevice.getLinkAddress().contains(ip)) {
					device=singleCarparkDevice;
					break;
				}
			}
		}
		if (device==null) {
			return;
		}
		model.getMapOpenDoor().put(device.getIp(),true);
		presenter.handPhotograph(device.getIp());
		return;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

	public String getRemoteAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public String getMACAddress(String ip) {
		String str = "";
		String macAddress = "";
		try {
			Process p = Runtime.getRuntime().exec("nbtstat -A " + ip);
			InputStreamReader ir = new InputStreamReader(p.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			for (int i = 1; i < 100; i++) {
				str = input.readLine();
				if (str != null) {
					if (str.indexOf("MAC Address") > 1) {
						macAddress = str.substring(str.indexOf("MAC Address") + 14, str.length());
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
		return macAddress;
	}

}
