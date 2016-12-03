package com.donglu.carpark.ui.servlet;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.CarparkMainPresenter;
import com.donglu.carpark.ui.task.CarInTask;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCard;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;

public class CardRecordServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2258667355504265934L;
	private static final Logger logger = LoggerFactory.getLogger(CardRecordServlet.class);
	private CarparkMainPresenter presenter;
	private CarparkMainModel model;
	private CarparkDatabaseServiceProvider sp;
	static Map<String, SingleCarparkDevice> mapControlIpToDevice = OpenDoorServlet.mapControlIpToDevice;

	public CardRecordServlet(CarparkMainPresenter presenter) {
		this.presenter = presenter;
		model = presenter.getModel();
		sp = presenter.getSp();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String ip = getRemoteAddress(req);
		logger.info("控制器：{}获得刷卡记录上传", ip);
		SingleCarparkDevice device = mapControlIpToDevice.get(ip);
		if (device == null) {
			for (SingleCarparkDevice singleCarparkDevice : model.getMapIpToDevice().values()) {
				if (singleCarparkDevice.getLinkAddress().contains(ip)) {
					device = singleCarparkDevice;
					mapControlIpToDevice.put(ip, device);
					break;
				}
			}
		}
		if (device == null || device.getInOrOut().equals("出口")) {
			return;
		}
		try {
			byte[] bs = new byte[17];
			req.getInputStream().read(bs);
			String serialNumber = new String(bs).trim();
			List<SingleCarparkCard> list = sp.getCarparkUserService().findSingleCarparkCardBySearch(0, Integer.MAX_VALUE, serialNumber, null);
			if (StrUtil.isEmpty(list)) {
				logger.error("在系统中没有找到卡片[{}]", serialNumber);
				return;
			}
			SingleCarparkCard card = list.get(0);
			SingleCarparkUser user = card.getUser();
			String[] plateNos = user.getPlateNo().split(",");
			CarInTask carInTask = null;
			Date date = new Date();
			String plateNO=null;
			for (String string : plateNos) {
				plateNO=string;
				carInTask = model.getMapPlateToInTask().remove(string);
				if (carInTask != null
						&& StrUtil.countTime(carInTask.getDate(), date, TimeUnit.MILLISECONDS) < Integer.valueOf(model.getMapSystemSetting().get(SystemSettingTypeEnum.车牌卡片共用时允许识别间隔)) * 1000) {
					break;
				}
				carInTask=null;
			}
			if (carInTask == null) {
				logger.info("没有找到卡片:{} 对应车牌:{} 的信息，等待车牌识别",serialNumber,plateNO);
				model.getMapCardEventTime().put(serialNumber, date);
				return;
			}
			carInTask.setCardSerialNumber(serialNumber);
			carInTask.initInOutHistory();
			carInTask.checkUser(false);
			return;
		} catch (Exception e) {
			logger.error("处理刷卡记录时发生错误" + e, e);
		}
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
