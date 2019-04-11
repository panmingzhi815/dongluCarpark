package com.donglu.carpark.server.servlet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;
import com.google.inject.Inject;

public class ApiServlet extends AbstractHttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CarparkDatabaseServiceProvider sp;
	@Inject
	public ApiServlet(CarparkDatabaseServiceProvider sp) {
		this.sp = sp;
	}
	@RequestMapping("/login")
	public Object login(String username,String password) {
		LOGGER.info("用户：{} 登录",username);
		SingleCarparkSystemUser user = sp.getSystemUserService().findByNameAndPassword(username, password);
		if(user==null) {
			return createResult(false,1,"用户名或密码错误",null);
		}
		if(!user.getPassword().equals(password)) {
			return createResult(false,1,"用户名或密码错误",null);
		}
		return createResult(true, 0, "登录成功", null);
	}
	@RequestMapping("/openDoor")
	public Object openDoor(String ip,String userName) {
		sp.getCarparkDeviceService().openDoor(ip,userName);
		return createResult(true, 0, "已发送开闸命令", null);
	}
	@RequestMapping("/closeDoor")
	public Object closeDoor(@RequestParam("ip")String ip) {
		sp.getCarparkDeviceService().closeDoor(ip);
		return createResult(true, 0, "已发送落闸命令", null);
	}
	@RequestMapping("/getDevices")
	public Object getDeviceList() {
		List<SingleCarparkDevice> list = sp.getCarparkDeviceService().getAllDevice();
		list.sort(new Comparator<SingleCarparkDevice>() {
			@Override
			public int compare(SingleCarparkDevice o1, SingleCarparkDevice o2) {
				return o1.getIdentifire().compareTo(o2.getIdentifire());
			}
		});
		List<JSONObject> listDevices=new ArrayList<>();
		for (SingleCarparkDevice singleCarparkDevice : list) {
			JSONObject jo = new JSONObject();
			jo.put("id", singleCarparkDevice.getIdentifire());
			jo.put("name", singleCarparkDevice.getName());
			jo.put("ip", singleCarparkDevice.getIp());
			jo.put("status", singleCarparkDevice.getStatus()==0);
			listDevices.add(jo);
		}
		return listDevices;
	}
	@RequestMapping("/getDevice")
	public Object getDeviceInfo(@RequestParam("ip") String ip) {
		if (ip==null) {
			return createResult(false, -1, "ip为空",null);
		}
		List<SingleCarparkDevice> allDevice = sp.getCarparkDeviceService().getAllDevice();
		for (SingleCarparkDevice device : allDevice) {
			if (ip.equals(device.getIp())) {
				JSONObject jo = new JSONObject();
				jo.put("id", device.getIdentifire());
				jo.put("name", device.getName());
				jo.put("ip", device.getIp());
				jo.put("status", device.getStatus()==0);
				return jo;
			}
		}
		return createResult(false, -1, "设备不存在", null);
	}
}
