package com.donglu.carpark.server.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.dongluhitec.card.domain.db.singlecarpark.CarPayHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;


public class CarparkHttpServiceServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2486848099999025170L;
	
	private CarparkDatabaseServiceProvider sp;
	
	@Inject
	public CarparkHttpServiceServlet(CarparkDatabaseServiceProvider sp) {
		this.sp = sp;
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pathInfo = req.getPathInfo();
		System.out.println("pathInfo=="+pathInfo);
		switch (pathInfo) {
		case "/carpark":
			carparkHandle(req,resp);
			break;
		case "/history":
			Long id=0l;
			String idstring = req.getParameter("id");
			if (idstring!=null) {
				id=Long.valueOf(idstring);
			} 
			SimplePropertyPreFilter filter=new SimplePropertyPreFilter();
			filter.getExcludes().addAll(Arrays.asList("remark","labelString","uuid","propertyChangeSupport","reviseInTime","stillTimeLabel","saveHistory","savePayHistory"));
			List<SingleCarparkInOutHistory> findHistoryThanId = sp.getCarparkInOutService().findHistoryThanId(id,0,100);
			String jsonString = JSON.toJSONString(findHistoryThanId,filter);
			System.out.println(jsonString);
			writeMsg(resp, 0, jsonString);
			break;
		case "/payedHistory":
			id=0l;
			idstring = req.getParameter("id");
			if (idstring!=null) {
				id=Long.valueOf(idstring);
			} 
			filter=new SimplePropertyPreFilter();
			filter.getExcludes().addAll(Arrays.asList("remark","labelString","uuid","propertyChangeSupport","reviseInTime","stillTimeLabel","saveHistory","savePayHistory"));
			List<CarPayHistory> findCarPayHistoryThanId = sp.getCarparkInOutService().findCarPayHistoryThanId(id, 0, 100);
			String jsonString2 = JSON.toJSONString(findCarPayHistoryThanId,filter);
			System.out.println(jsonString2);
			writeMsg(resp, 0, jsonString2);
			break;
		case "/user":
			userHandle(req,resp);
			break;
		case "/visitor":
			visitorHandle(req,resp);
			break;
		case "/openDoor":
			String ip = req.getParameter("ip");
			String identifier = req.getParameter("identifier");
			System.out.println(ip+"==="+identifier);
			sp.getCarparkDeviceService().openDoor(ip);
			writeMsg(resp, 0, "已发送开闸指令");
			break;
		case "historySearch":
			String plate = req.getParameter("plate");
			if (plate!=null) {
				plate=URLDecoder.decode(plate, "UTF-8");
			}
			String beginTime = req.getParameter("beginTime");
			String endTime = req.getParameter("endTime");
			Date begin=null;
			Date end=null;
			if (!StrUtil.isEmpty(beginTime)) {
				begin = StrUtil.parse(beginTime, "yyyyMMddHHmmss");
			}
			
			if (!StrUtil.isEmpty(endTime)) {
				end=StrUtil.parse(endTime, "yyyyMMddHHmmss");
			}
			String timeType = req.getParameter("timeType");
			if (StrUtil.isEmpty(timeType)) {
				timeType="0";
			}
			int page=1;
			int size=10;
			String ppage = req.getParameter("page");
			if (!StrUtil.isEmpty(ppage)) {
				page=Integer.valueOf(ppage);
			}
			String psize = req.getParameter("size");
			if (!StrUtil.isEmpty(psize)) {
				
			}
			findHistoryThanId=sp.getCarparkInOutService().findHistoryByTimeOrder((page-1)*size,size,plate,begin,end,Integer.valueOf(timeType));
			filter=new SimplePropertyPreFilter();
			filter.getExcludes().addAll(Arrays.asList("remark","labelString","uuid","propertyChangeSupport","reviseInTime","stillTimeLabel","saveHistory","savePayHistory"));
			jsonString = JSON.toJSONString(findHistoryThanId,filter);
			System.out.println(jsonString);
			writeMsg(resp, 0, jsonString);
			break;
		}
	}
	private void visitorHandle(HttpServletRequest req, HttpServletResponse resp) {
		String type = req.getParameter("type");
	}
	private void userHandle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String type = req.getParameter("type");
		String userString = req.getParameter("user");
		switch (type) {
		case "add":
			Long carparkId = JSON.parseObject(userString).getLong("carparkId");
			SingleCarparkCarpark carpark = sp.getCarparkService().findCarparkById(carparkId);
			if (carpark==null) {
				writeMsg(resp, 1, "添加失败,停车场不存在:"+carparkId);
				return;
			}
			SingleCarparkUser user = JSON.parseObject(userString, SingleCarparkUser.class);
			SingleCarparkUser oldUser = sp.getCarparkUserService().findUserByPlateNo(user.getPlateNo(), carparkId);
			if (oldUser!=null) {
				writeMsg(resp, 1, "添加失败,车牌已存在:"+oldUser.getId());
				return;
			}
			user.setCarparkSlot(1);
			user.setType("普通");
			user.setId(null);
			user.setCarpark(carpark);
			System.out.println(user);
			Long saveUser = sp.getCarparkUserService().saveUser(user);
			writeMsg(resp, 0, saveUser+"");
			break;
		case "update":
			carparkId = JSON.parseObject(userString).getLong("carparkId");
			carpark = sp.getCarparkService().findCarparkById(carparkId);
			if (carpark==null) {
				writeMsg(resp, 1, "修改失败,停车场不存在:"+carparkId);
				return;
			}
			user = JSON.parseObject(userString, SingleCarparkUser.class);
			if (user.getId()==null) {
				writeMsg(resp, 1, "修改失败,id不能为空");
				return;
			}
			oldUser = sp.getCarparkUserService().findUserById(user.getId());
			if (oldUser==null) {
				writeMsg(resp, 1, "修改失败,固定车不存在:"+user.getId());
				return;
			}
			if (oldUser.getId()!=user.getId()) {
				writeMsg(resp, 1, "修改失败,车牌已存在:"+oldUser.getId());
				return;
			}
			oldUser.setName(user.getName());
			oldUser.setAddress(user.getAddress());
			oldUser.setPlateNo(user.getPlateNo());
			oldUser.setTelephone(user.getTelephone());
			oldUser.setValidTo(user.getValidTo());
			oldUser.setCarpark(carpark);
			System.out.println(user);
			saveUser = sp.getCarparkUserService().saveUser(oldUser);
			writeMsg(resp, 0, saveUser+"");
			break;
		case "delete":
			user = JSON.parseObject(userString, SingleCarparkUser.class);
			if (user.getId()==null) {
				writeMsg(resp, 1, "删除失败,id不能为空");
				return;
			}
			Long deleteUser = sp.getCarparkUserService().deleteUser(user);
			writeMsg(resp, 0, deleteUser+"");
			break;
		default:
			break;
		}
	}
	private void carparkHandle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String type = req.getParameter("type");
		String carparkString = req.getParameter("carpark");
		switch (type) {
		case "add":
			SingleCarparkCarpark carpark = JSON.parseObject(carparkString, SingleCarparkCarpark.class);
			SingleCarparkCarpark byCode = sp.getCarparkService().findCarparkByCode(carpark.getCode());
			if (byCode!=null) {
				writeMsg(resp, 1, "添加失败,编号已存在");
				return;
			}
			carpark.setId(null);
			Long saveCarpark = sp.getCarparkService().saveCarpark(carpark);
			writeMsg(resp, 0, ""+saveCarpark);
			return;
		case "update":
			carpark = JSON.parseObject(carparkString, SingleCarparkCarpark.class);
			SingleCarparkCarpark byId = sp.getCarparkService().findCarparkById(carpark.getId());
			if (byId==null) {
				writeMsg(resp, 1, "修改失败,编号["+carpark.getId()+"]的停车场不存在");
				return;
			}
			byId.setCode(carpark.getCode());
			byId.setName(carpark.getName());
			byId.setTempNumberOfSlot(carpark.getTempNumberOfSlot());
			byId.setLeftTempNumberOfSlot(carpark.getLeftTempNumberOfSlot());
			byId.setFixNumberOfSlot(carpark.getFixNumberOfSlot());
			byId.setLeftFixNumberOfSlot(carpark.getLeftFixNumberOfSlot());
			byId.setTotalNumberOfSlot(carpark.getFixNumberOfSlot()+carpark.getTempNumberOfSlot());
			byId.setTempCarIsIn(carpark.isTempCarIsIn());
			saveCarpark = sp.getCarparkService().saveCarpark(carpark);
			writeMsg(resp, 0, ""+saveCarpark);
			break;
        case "list":
            List<SingleCarparkCarpark> carpark2 = sp.getCarparkService().findAllCarpark();
            for (SingleCarparkCarpark singleCarparkCarpark : carpark2) {
            	singleCarparkCarpark.setChilds(new ArrayList<>());
            	singleCarparkCarpark.setParent(null);
			}
            writeMsg(resp, 0, JSON.toJSONString(carpark2));
            break;
        default:
        	writeMsg(resp,99,"非法请求");
        	break;
		}
	}
	private void writeMsg(HttpServletResponse resp, int code, String msg) throws IOException {
		ServletOutputStream os = resp.getOutputStream();
		JSONObject jo = new JSONObject();
		jo.put("code", code);
		jo.put("result", msg);
		String string = jo.toString();
		System.out.println(string);
		os.write(string.getBytes("UTF-8"));
		os.flush();
	}
	public static void main(String[] args) throws Exception {
//		testCarpark();
		URL url=new URL("http://127.0.0.1:8899/carparkHttpService/user?type=delete");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setDoInput(true);
		JSONObject json = new JSONObject();
		json.put("id", "93");
		json.put("plateNo", "粤BD021W");
		json.put("name", "hjx");
		json.put("address", "宝安");
		json.put("telephone", "133");
		json.put("validTo", "2018-05-15 23:59:59");
		json.put("carparkId", 1);
		OutputStream os = conn.getOutputStream();
		System.out.println(URLEncoder.encode(json+"", "UTF-8"));
		os.write(("user="+json).getBytes());
		os.flush();
		InputStream is = conn.getInputStream();
		byte[] b = new byte[1024];
		is.read(b);
		System.out.println(new String(b).trim());
	}
	/**
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static void testCarpark() throws MalformedURLException, IOException {
		URL url=new URL("http://127.0.0.1:8899/carparkHttpService/carpark?type=add");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setDoInput(true);
		JSONObject json = new JSONObject();
		json.put("id", "1");
		json.put("fixNumberOfSlot", "100");
		json.put("tempNumberOfSlot", "200");
		json.put("leftTempNumberOfSlot", "15");
		json.put("leftFixNumberOfSlot", "25");
		json.put("code", "11");
		json.put("name", "测试");
		json.put("tempCarIsIn", true);
		OutputStream os = conn.getOutputStream();
		os.write(("carpark="+json).getBytes());
		os.flush();
		InputStream is = conn.getInputStream();
		byte[] b = new byte[1024];
		int read = is.read(b);
		System.out.println(new String(b,0,read));
	}
	
	
}
