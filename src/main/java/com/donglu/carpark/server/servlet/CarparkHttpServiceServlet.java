package com.donglu.carpark.server.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.dongluhitec.card.domain.db.singlecarpark.CarPayHistory;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkCarType;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkChargeStandard;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemSetting;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkVisitor;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;


public class CarparkHttpServiceServlet extends HttpServlet {
	private Logger log=LoggerFactory.getLogger(CarparkHttpServiceServlet.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = -2486848099999025170L;
	
	private CarparkDatabaseServiceProvider sp;
	String key="";
	
	@Inject
	public CarparkHttpServiceServlet(CarparkDatabaseServiceProvider sp) {
		this.sp = sp;
		SingleCarparkSystemSetting systemSetting = sp.getCarparkService().findSystemSettingByKey("HTTP对外服务签名秘钥");
		key=systemSetting==null?SystemSettingTypeEnum.HTTP对外服务签名秘钥.getDefaultValue():systemSetting.getSettingValue();
		
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pathInfo = req.getPathInfo();
		log.info("接收到请求pathInfo={}",pathInfo);
		if (!checkSign(req,resp)) {
			return;
		}
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
		case "/historySearch":
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
		case "/countPayInfo":
			countPayInfo(req,resp);
			break;
		case "/pushPay":
			pushPay(req,resp);
			break;
		default:
			writeMsg(resp, -1, "未知的方法"+pathInfo);
			break;
		}
	}
	public boolean checkSign(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		if (key==null||key.trim().isEmpty()) {
			return true;
		}
		List<String> collect = req.getParameterMap().keySet().stream().sorted((e1,e2)->e1.compareTo(e2)).collect(Collectors.toList());
		collect.remove("sign");
		if (collect.isEmpty()) {
			return true;
		}
		String sign = req.getParameter("sign");
		if (sign==null) {
			writeMsg(resp, -2, "签名错误");
			return false;
		}
		StringBuffer sb=new StringBuffer();
		for (String name : collect) {
			if ("sign".equals(name)) {
				continue;
			}
			String p = req.getParameter(name);
			if (p!=null) {
				sb.append("&");
				sb.append(name);
				sb.append("=");
				sb.append(p);
			}
		}
		sb.append("&key="+key);
		sb.deleteCharAt(0);
		try {
			String md5 = md5(sb.toString());
			log.info("签名数据：{} 结果：{}",sb,md5);
			if (sign.equals(md5)) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		writeMsg(resp, -2, "签名错误");
		return false;
	}
	
	private void pushPay(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String hid = req.getParameter("id");
		String shouldMoney = req.getParameter("shouldMoney");
		String factMoney = req.getParameter("factMoney");
		String freeMoney = req.getParameter("freeMoney");
		String paidTime = req.getParameter("paidTime");
		String delayOutTime = req.getParameter("delayOutTime");
		if (hid==null||shouldMoney==null||factMoney==null||freeMoney==null||paidTime==null) {
			writeMsg(resp, -1, "缺少必传参数");
			return;
		}
		SingleCarparkInOutHistory ioh = sp.getCarparkInOutService().findInOutById(Long.valueOf(hid));
		if (ioh==null) {
			writeMsg(resp, -1, String.format("id为%s的记录不存在", hid));
			return;
		}
		ioh.setShouldMoney(Float.valueOf(shouldMoney));
		ioh.setFactMoney(Float.valueOf(factMoney));
		ioh.setFreeMoney(Float.valueOf(freeMoney));
		
		CarPayHistory carPayHistory = new CarPayHistory(ioh);
		carPayHistory.setPayTime(StrUtil.parse(paidTime, "yyyyMMddHHmmss"));
		sp.getCarPayService().saveCarPayHistory(carPayHistory);
		writeMsg(resp, 0, "操作成功");
	}
	private void countPayInfo(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			String plate = req.getParameter("plate");
			String parkId = req.getParameter("parkId");
			if (plate==null||parkId==null) {
				writeMsg(resp, -1, "错误的参数");
				return;
			}
			SingleCarparkCarpark carpark=sp.getCarparkService().findCarparkById(Long.valueOf(parkId));
			if (carpark==null) {
				writeMsg(resp, -1, "停车场不存在");
				return;
			}
			List<SingleCarparkInOutHistory> findByNoOut = sp.getCarparkInOutService().findByNoOut(plate,carpark);
			if (findByNoOut.isEmpty()) {
				writeMsg(resp, -1, "停车记录不存在或已出场");
				return;
			}
			List<CarparkCarType> carparkCarTypeList = sp.getCarparkService().getCarparkCarTypeList();
			Map<String, CarparkCarType> map = carparkCarTypeList.stream().collect(Collectors.toMap(e->e.getName(), e1->e1));
			
			Long carType=Optional.ofNullable(map.get("小车")).orElse(carparkCarTypeList.get(0)).getId();
			SingleCarparkInOutHistory ioh = findByNoOut.get(0);
			Date endTime = new Date();
			float f = sp.getCarparkService().calculateTempCharge(carpark.getId(), carType, ioh.getInTime(), endTime);
			List<CarPayHistory> list = sp.getCarPayService().findCarPayHistoryByHistoryId(ioh.getId());
			double cashCost=0;
			double couponValue=0;
			for (CarPayHistory carPayHistory : list) {
				cashCost += carPayHistory.getCashCost();
				couponValue += carPayHistory.getCouponValue();
			}
			JSONObject jo=new JSONObject();
			jo.put("plate", plate);
			jo.put("parkId", parkId);
			jo.put("inTime", StrUtil.formatDateTime(ioh.getInTime()));
			jo.put("countTime", StrUtil.formatDateTime(new Date()));
			double money = f-cashCost-couponValue;
			jo.put("money", money<0?0:money);
			jo.put("totalPaidMoney", cashCost);
			jo.put("totalFreeMoney", couponValue);
			jo.put("id", ioh.getId());
			writeMsg(resp, 0, "计费成功",jo);
		} catch (Exception e) {
			writeMsg(resp, -1, "计费时发生错误"+e);
		}
	}
	private void visitorHandle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			String type = req.getParameter("type");
			String plate = req.getParameter("plate");
			if (type==null||plate==null) {
				writeMsg(resp, -1, "未传必传的参数");
				return;
			}
			String valid = req.getParameter("endTime");
			String startTime = req.getParameter("startTime");
			String times = req.getParameter("times");
			String parkId = req.getParameter("parkId");
			String name = req.getParameter("name");
			String tel = req.getParameter("tel");
			switch (type) {
			case "update":
				List<SingleCarparkVisitor> list = sp.getCarparkService().findVisitorByLike(0, 10, null, plate);
				if (list.isEmpty()) {
					SingleCarparkCarpark carpark = null;
					if (parkId!=null) {
						try {
							carpark = sp.getCarparkService().findCarparkById(Long.valueOf(parkId));
						} catch (Exception e) {
							
						}
						if (carpark==null) {
							carpark=sp.getCarparkService().findCarparkByYunIdentifier(parkId);
						}
					}
					SingleCarparkVisitor v = new SingleCarparkVisitor();
					v.setPlateNO(plate);
					v.setValidTo(StrUtil.parse(valid, "yyyyMMddHHmmss"));
					v.setStartTime(StrUtil.parse(startTime, "yyyyMMddHHmmss"));
					if (!StrUtil.isEmpty(times)) {
						v.setAllIn(Integer.valueOf(times));
					}
					v.setCarpark(carpark);
					v.setStatus(SingleCarparkVisitor.VisitorStatus.可用.name());
					v.setName(name);
					v.setTelephone(tel);
					sp.getCarparkService().saveVisitor(v);
					writeMsg(resp, 0, "添加成功");
				}else {
					for (SingleCarparkVisitor v : list) {
						v.setPlateNO(plate);
						v.setValidTo(StrUtil.parse(valid, "yyyyMMddHHmmss"));
						v.setStartTime(StrUtil.parse(startTime, "yyyyMMddHHmmss"));
						if (!StrUtil.isEmpty(times)) {
							v.setAllIn(Integer.valueOf(times));
						}
						v.setStatus(SingleCarparkVisitor.VisitorStatus.可用.name());
						v.setName(name);
						v.setTelephone(tel);
						sp.getCarparkService().saveVisitor(v);
					}
					writeMsg(resp, 0, "更新成功");
				}
				break;
			case "delete":
				list = sp.getCarparkService().findVisitorByLike(0, 10, null, plate);
				for (SingleCarparkVisitor singleCarparkVisitor : list) {
					sp.getCarparkService().deleteVisitor(singleCarparkVisitor);
				}
				writeMsg(resp, 0, "删除成功");
				break;
			default:
				writeMsg(resp, 1, "错误的type");
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			writeMsg(resp, 1, e.getMessage());
		}
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
		log.info("返回消息：{}",string);
		os.write(string.getBytes("UTF-8"));
		os.flush();
	}
	private void writeMsg(HttpServletResponse resp, int code, String msg,Object result) throws IOException {
		ServletOutputStream os = resp.getOutputStream();
		JSONObject jo = new JSONObject();
		jo.put("code", code);
		jo.put("result", result);
		jo.put("msg", msg);
		String string = jo.toString();
		log.info("返回消息：{}",string);
		os.write(string.getBytes("UTF-8"));
		os.flush();
	}
	public static void main(String[] args) throws Exception {
//		testCarpark();
		URL url=new URL("http://47.92.24.201:8899/carparkHttpService/countPayInfo");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setDoInput(true);
//		JSONObject json = new JSONObject();
//		json.put("id", "93");
//		json.put("plateNo", "粤BD021W");
//		json.put("name", "hjx");
//		json.put("address", "宝安");
//		json.put("telephone", "133");
//		json.put("validTo", "2018-05-15 23:59:59");
//		json.put("carparkId", 1);
//		OutputStream os = conn.getOutputStream();
//		System.out.println(URLEncoder.encode(json+"", "UTF-8"));
//		os.write(("user="+json).getBytes());
//		os.flush();
//		InputStream is = conn.getInputStream();
//		byte[] b = new byte[1024];
//		is.read(b);
//		System.out.println(new String(b).trim());
		
		TreeMap<String, Object> map = new TreeMap<>();
		map.put("plate", "粤BD022W");
		map.put("parkId", 1);
		StringBuffer sb=new StringBuffer();
		Iterator<String> iterator = map.keySet().iterator();
		while(iterator.hasNext()) {
			String next = iterator.next();
			sb.append("&");
			sb.append(next);
			sb.append("=");
			sb.append(map.get(next));
		}
		sb.deleteCharAt(0);
		String string = sb.toString()+"&key=123";
		System.out.println("带加密数据："+string);
		String md5 = md5(string);
		System.out.println("加密后数据：{}"+md5);
		
		OutputStream os = conn.getOutputStream();
		String p = sb.toString()+"&sign="+md5;
		System.out.println("param=="+p);
		os.write(p.getBytes("UTF-8"));
		os.flush();
		InputStream is = conn.getInputStream();
		byte[] b = new byte[1024];
		is.read(b);
		System.out.println(new String(b).trim());
		
		
		System.out.println(md5("delayOutTime=20&factMoney=291.0&freeMoney=0&id=1&paidTime=20200414123048&shouldMoney=291.0&key=123"));
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
	/**
	 * 利用MD5进行加密
	 * 
	 * @param str 待加密的字符串
	 * @return 加密后的字符串
	 * @throws NoSuchAlgorithmException
	 *             没有这种产生消息摘要的算法
	 * @throws UnsupportedEncodingException
	 */
	public static String md5(String str) throws Exception {
		 System.out.println(str);
		// 确定计算方法
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		// 加密后的字符串
		byte[] digest = md5.digest(str.getBytes("utf-8"));
		String s = "";
		for (byte b : digest) {
			s += padStart(Integer.toHexString(b & 0xff), 2, '0');
		}
		return s;
	}

	private static String padStart(String string, int minLength, char padChar) {
		if (string.length() >= minLength) {
			return string;
		}
		StringBuilder sb = new StringBuilder(minLength);
		for (int i = string.length(); i < minLength; i++) {
			sb.append(padChar);
		}
		sb.append(string);
		return sb.toString();
	}
	
}
