package com.donglu.carpark.server.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.dongluhitec.card.domain.db.singlecarpark.OverSpeedCar;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkBlackUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SmsInfo;
import com.dongluhitec.card.domain.db.singlecarpark.SystemOperaLogTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;


public class CarparkOverSpeedCarServlet extends HttpServlet {
	private Logger LOGGER=LoggerFactory.getLogger(CarparkOverSpeedCarServlet.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = -2486848099999025170L;
	
	private CarparkDatabaseServiceProvider sp;
	
	private  static final Cache<String, String> cacheSetting = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();
	
	@Inject
	public CarparkOverSpeedCarServlet(CarparkDatabaseServiceProvider sp) {
		this.sp = sp;
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pathInfo = req.getPathInfo();
		LOGGER.info("pathInfo=="+pathInfo);
		switch (pathInfo) {
		case "/add":
			try {
				String CamID = req.getParameter("CamID");
				String PassTime = req.getParameter("PassTime");
				String PlaceName = req.getParameter("PlaceName");
				String CarPlate = req.getParameter("CarPlate");
				String VehicleSpeed = req.getParameter("VehicleSpeed");
				String MarkedSpeed = req.getParameter("MarkedSpeed");
				String Image = req.getParameter("Image");
				String Image1 = req.getParameter("Image1");
				String ImageURL=req.getParameter("ImageURL");
				Date time = StrUtil.parse(PassTime, "yyyyMMddHHmmssSSS");
				String key = CarPlate+StrUtil.formatDate(time, "yyyyMMddHHmm");
				if (cacheSetting.getIfPresent(key)!=null) {
					writeMsg(resp, 0, "成功");
					return;
				}
				if (StrUtil.isEmpty(CarPlate)||StrUtil.isEmpty(PassTime)||StrUtil.isEmpty(VehicleSpeed)||StrUtil.isEmpty(MarkedSpeed)) {
					writeMsg(resp, 1, "参数不能为空");
					return;
				}
				OverSpeedCar car = new OverSpeedCar();
				car.setCamId(CamID);
				car.setCurrentSpeed(Integer.valueOf(VehicleSpeed));
				car.setPlace(PlaceName);
				car.setPlate(CarPlate);
				car.setTime(time);
				
				cacheSetting.put(key, CarPlate);
				if(!StrUtil.isEmpty(Image)) {
					byte[] decode = Base64.getDecoder().decode(Image);
					String string = sp.getImageService().saveImageInServer(decode, "/img/"+StrUtil.formatDate(new Date())+"/"+System.currentTimeMillis()+".jpg");
					car.setImage(string);
				}
				if (car.getImage()==null&&ImageURL!=null) {
					byte[] decode = getImageByUrl(ImageURL);
					if (decode!=null) {
						String string = sp.getImageService().saveImageInServer(decode, "/img/" + StrUtil.formatDate(new Date()) + "/" + System.currentTimeMillis() + ".jpg");
						car.setImage(string);
					}
				}
				if(!StrUtil.isEmpty(Image1)) {
					byte[] decode = Base64.getDecoder().decode(Image1);
					String string = sp.getImageService().saveImageInServer(decode, "/img/"+StrUtil.formatDate(new Date())+"/"+System.currentTimeMillis()+".jpg");
					car.setImage2(string);
				}
				String carType="临时车";
				car.setRateLimiting(Integer.valueOf(MarkedSpeed));
				SingleCarparkUser user = sp.getCarparkUserService().findUserByPlateNo(CarPlate, null);
				if (user!=null) {
					LOGGER.info("固定车：{}-{}",user.getName(),user.getTelephone());
					carType="固定车";
					int fixCarRateLimiting=Integer.valueOf(getSystemSetting(SystemSettingTypeEnum.固定车超速速度));
					car.setRateLimiting(fixCarRateLimiting);
				}
				car.setStatus(getStatus(car));
				if (car.getTime()==null) {
					car.setTime(new Date());
				}
				car.setCarType(carType);
				sp.getCarparkInOutService().saveOverSpeedCar(car);
				countOverSpeedSize(CarPlate, user!=null, user,car);
				writeMsg(resp, 0, "成功");
			} catch (Exception e) {
				writeMsg(resp, 1, "失败,"+e);
			}
			break;
		}
	}
	
	private byte[] getImageByUrl(String imageURL) {
		try(ByteArrayOutputStream bos=new ByteArrayOutputStream()){
    		HttpURLConnection conn = (HttpURLConnection) new URL(imageURL).openConnection();
    		conn.setRequestMethod("GET");
    		conn.setDoInput(true);
    		conn.setReadTimeout(2000);
    		conn.setConnectTimeout(3000);
			InputStream is = conn.getInputStream();
			int read=-1;
			byte[] b = new byte[1024];
			while((read=is.read(b))!=-1) {
				bos.write(b,0,read);
				bos.flush();
			}
			return bos.toByteArray();
		}catch (Exception e) {
		}
		return null;
	}
	
	/**
	 * @param car
	 * @return
	 */
	public int getStatus(OverSpeedCar car) {
		if (car.getPlate().matches(getSystemSetting(SystemSettingTypeEnum.特殊车辆车牌类型))) {
			return 0;
		}
		return (car.getCurrentSpeed()>car.getRateLimiting()&&car.getRateLimiting()>0)?1:0;
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
	
	public int countOverSpeedSize(String plateNo, boolean isFixCar,SingleCarparkUser user,OverSpeedCar overSpeedCar) {
		if (booleanSetting(SystemSettingTypeEnum.启用测速系统)) {
			Map<String, Object> map=new HashMap<>();
			map.put(OverSpeedCar.Property.plate.name(), plateNo);
			String carType="临时车";
			if (isFixCar) {
				carType="固定车";
				
			}
			map.put(OverSpeedCar.Property.carType.name(), carType);
			int day=0;
			int size=0;
			int blackSize=7;
			if (isFixCar) {
				String[] split = getSystemSetting(SystemSettingTypeEnum.固定车超速自动删除).split("-");
				day=Integer.valueOf(split[0]);
				size=Integer.valueOf(split[1]);
			}else {
				String[] split = getSystemSetting(SystemSettingTypeEnum.临时车超速自动拉黑).split("-");
				day=Integer.valueOf(split[0]);
				size=Integer.valueOf(split[1]);
				blackSize=Integer.valueOf(split[2]);
			}
			if (size==0) {
				return 0;
			}
			map.put(OverSpeedCar.Property.time.name()+"-ge", StrUtil.getTodayTopTime(new DateTime(new Date()).minusDays(day).toDate()));
			map.put(OverSpeedCar.Property.time.name()+"-le", StrUtil.getTodayBottomTime(new Date()));
			map.put(OverSpeedCar.Property.status.name(), 1);
			List<OverSpeedCar> list = sp.getCarparkInOutService().findOverSpeedCarByMap(0, 100, map);
			String templateCode = getSystemSetting(SystemSettingTypeEnum.固定车超速发送短信);
			if (isFixCar&&!StrUtil.isEmpty(user.getTelephone())&&overSpeedCar.getStatus()==1&&!StrUtil.isEmpty(templateCode)) {
				SmsInfo smsInfo = new SmsInfo();
				smsInfo.setTel(user.getTelephone());
				smsInfo.setOverSpeedSize(list.size());
				smsInfo.setOverSpeedTime(overSpeedCar.getTime());
				smsInfo.setUserName(user.getName());
				smsInfo.setPlate(plateNo);
				smsInfo.setSpeed(overSpeedCar.getCurrentSpeed());
				smsInfo.setAddress(overSpeedCar.getPlace());
				smsInfo.setTemplateCode(templateCode);
				JSONObject jo=new JSONObject();
				jo.put("plate", plateNo);
				jo.put("name", user.getName());
				jo.put("overSpeedsize", list.size());
				jo.put("time", StrUtil.formatDateTime(overSpeedCar.getTime()));
				jo.put("day", day);
				jo.put("address", overSpeedCar.getPlace());
				jo.put("speed", overSpeedCar.getCurrentSpeed());
				smsInfo.setData(jo.toJSONString());
				sp.getCarparkInOutService().saveSmsInfo(smsInfo);
			}
			if (list.size()>=size) {
				if (isFixCar) {
					user.setRemark(user.getRemark()==null?"":(user.getRemark()+";")+"超速"+list.size()+"次");
					user.setValidTo(null);
					sp.getCarparkUserService().saveUser(user);
					sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.固定用户, "用户有效期："+StrUtil.formatDate(user.getValidTo())+",超速"+list.size()+"次,自动删除", "系统操作");
				}else {
					SingleCarparkBlackUser bu=sp.getCarparkService().findBlackUserByPlateNO(plateNo);
					if (bu==null) {
						bu=new SingleCarparkBlackUser();
					}
					bu.setPlateNO(plateNo);
					bu.setValid(StrUtil.getTodayTopTime(new DateTime(new Date()).plusDays(blackSize).toDate()));
					bu.setRemark("超速"+list.size()+"次");
					sp.getCarparkService().saveBlackUser(bu);
				}
				for (OverSpeedCar c : list) {
					c.setStatus(0);
				}
				sp.getCarparkInOutService().saveEntity(list);
			}
			return list.size();
		}
		return -1;
	}
	private boolean booleanSetting(SystemSettingTypeEnum systemSettingTypeEnum) {
		return Boolean.valueOf(getSystemSetting(systemSettingTypeEnum));
	}
	private String getSystemSetting(SystemSettingTypeEnum systemSettingTypeEnum) {
		try {
			String string = cacheSetting.get(systemSettingTypeEnum.name(), new Callable<String>() {
				@Override
				public String call() throws Exception {
					String string = sp.getCarparkService().getSystemSettingValue(systemSettingTypeEnum);
					return string;
				}
			});
			return string;
		} catch (Exception e) {
		}
		return systemSettingTypeEnum.getDefaultValue();
	}
	
}
