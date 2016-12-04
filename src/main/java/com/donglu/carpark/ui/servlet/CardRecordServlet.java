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
import com.donglu.carpark.ui.task.CarOutTask;
import com.dongluhitec.card.domain.db.singlecarpark.MachTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCard;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
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
		if (!Boolean.valueOf(model.getMapSystemSetting().get(SystemSettingTypeEnum.启用卡片支持))) {
			logger.info("没有启用卡片支持，不处理：{} 上传的卡片记录",ip);
			return;
		}
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
		if (device == null) {
			return;
		}
		if (device.getMachType().equals(MachTypeEnum.P)) {
			logger.info("设备匹配模式为：{} 不处理刷卡信息",device.getMachType());
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
			if (device.getInOrOut().equals("进口")) {
				cardIn(device, serialNumber, user, plateNos);
			}else{
				if (device.getMachType().equals(MachTypeEnum.PAC)) {
					CarOutTask carOutTask = null;
					Date date = new Date();
					String plateNO = null;
					for (String string : plateNos) {
						plateNO = string;
						carOutTask = model.getMapPlateToOutTask().remove(string);
						if (carOutTask != null
								&& StrUtil.countTime(carOutTask.getDate(), date, TimeUnit.MILLISECONDS) < Integer.valueOf(model.getMapSystemSetting().get(SystemSettingTypeEnum.车牌卡片共用时允许识别间隔)) * 1000) {
							break;
						}
						carOutTask = null;
					}
					if (carOutTask == null) {
						logger.info("没有找到卡片:{} 对应车牌:{} 的信息，等待车牌识别", serialNumber, plateNO);
						model.getMapCardEventTime().put(serialNumber, date);
						return;
					}
					logger.info("找到卡片:{} 对应车牌:{}　的识别信息，准备放行",serialNumber,carOutTask.getEditPlateNo());
					carOutTask.setCardSerialNumber(serialNumber);
					carOutTask.setUser(user);
					carOutTask.checkUserAndOut(false);
				}else if(device.getMachType().equals(MachTypeEnum.POC)||device.getMachType().equals(MachTypeEnum.C)){
					SingleCarparkInOutHistory cch = new SingleCarparkInOutHistory();
					cch.setPlateNo(user.getPlateNo());
					cch.setCardSerialNumber(serialNumber);
					cch.setUser(user);
					model.getMapDeviceToCard().put(device.getIp(), cch);
					presenter.handPhotograph(device.getIp());
				}
			}
		} catch (Exception e) {
			logger.error("处理刷卡记录时发生错误" + e, e);
		}
	}

	/**
	 * 刷卡进入
	 * @param device
	 * @param serialNumber
	 * @param user
	 * @param plateNos
	 * @throws Exception
	 */
	public void cardIn(SingleCarparkDevice device, String serialNumber, SingleCarparkUser user, String[] plateNos) throws Exception {
		if (device.getMachType().equals(MachTypeEnum.PAC)) {
			CarInTask carInTask = null;
			Date date = new Date();
			String plateNO = null;
			for (String string : plateNos) {
				plateNO = string;
				carInTask = model.getMapPlateToInTask().remove(string);
				if (carInTask != null
						&& StrUtil.countTime(carInTask.getDate(), date, TimeUnit.MILLISECONDS) < Integer.valueOf(model.getMapSystemSetting().get(SystemSettingTypeEnum.车牌卡片共用时允许识别间隔)) * 1000) {
					break;
				}
				carInTask = null;
			}
			if (carInTask == null) {
				logger.info("没有找到卡片:{} 对应车牌:{} 的信息，等待车牌识别", serialNumber, plateNO);
				model.getMapCardEventTime().put(serialNumber, date);
				return;
			}
			carInTask.setCardSerialNumber(serialNumber);
			carInTask.initInOutHistory();
			carInTask.checkUser(false);
		}else if(device.getMachType().equals(MachTypeEnum.POC)||device.getMachType().equals(MachTypeEnum.C)){
			SingleCarparkInOutHistory cch = new SingleCarparkInOutHistory();
			cch.setCardSerialNumber(serialNumber);
			cch.setUser(user);
			model.getMapDeviceToCard().put(device.getIp(), cch);
			presenter.handPhotograph(device.getIp());
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
