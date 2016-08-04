package com.donglu.carpark.service.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.donglu.carpark.server.imgserver.YunConfigUI;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.IpmsServiceI;
import com.donglu.carpark.util.CarparkFileUtils;
import com.donglu.carpark.yun.CarparkYunConfig;
import com.dongluhitec.card.domain.db.singlecarpark.CarPayHistory;
import com.dongluhitec.card.domain.db.singlecarpark.CarTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyUserPayHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class IpmsServiceImpl implements IpmsServiceI {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private String name = "东陆高新";
	@Inject
	private CarparkDatabaseServiceProvider sp;
	//023ed7e373464323875e28c64dcd368b
	private String buildindId="023ed7e373464323875e28c64dcd368b1";
	private String parkId="992c1a9463184ebb8f690a68a3f0407c";
	private String httpUrl="http://121.41.26.188:8080/ipms";
	public IpmsServiceImpl() {
		try {
			CarparkYunConfig instance =(CarparkYunConfig) CarparkFileUtils.readObject(YunConfigUI.CARPARK_YUN_CONFIG);
			parkId=instance.getAreaCode();
			buildindId=instance.getCompanyCode();
			httpUrl=instance.getUrl();
			name=instance.getCompany();
			log.info("获取信息parkId={}，buildindId={}，httpUrl={}，name={}",parkId,buildindId,httpUrl,name);
		} catch (Exception e) {
			httpUrl=null;
		}
	}
	@Override
	public boolean addInOutHistory(SingleCarparkInOutHistory ioh) {
		synchroInOutHistory("delete", ioh);
		return synchroInOutHistory("add", ioh);
	}
	/**
	 * 同步停车记录
	 * @param type 类型 add,update,delete
	 * @param ioh
	 * @return
	 */
	private boolean synchroInOutHistory(String type,SingleCarparkInOutHistory ioh){
		try {
			log.info("{}停车场记录",type);
			String url=httpUrl+"/api/syncParkingRecord.action";
			String content="{\"operation\":\""+type+"\",\"origin\":\""+name+"\","
					+ "\"parkingRecord\":{\"carNum\":\"{}\",\"carType\":\"0\",\"id\":\"{}\",\"inTimeStr\":\"{}\",\"outTimeStr\":\"{}\",\"buildingId\":\""+buildindId+"\",\"parkId\":\""+parkId+"\",\"parkName\":\"测试停车场\",\"status\":\"{}\",\"userType\":\"{}\"},\"syncId\":\"{}\",\"deptFee\"={},\"fee\"={}}";
			String carInfo=null;
			if (ioh!=null) {
					String plateNo = ioh.getPlateNo();
					Long id = ioh.getId();
					String inTime=StrUtil.formatDateTime(ioh.getInTime());
					int userType = 4;
					if (!StrUtil.isEmpty(ioh.getUserName())&&ioh.getCarType().equals("固定车")) {
						userType=1;
					}
					int status = 0;
					String outTime="";
					if (!StrUtil.isEmpty(ioh.getOutTime())) {
						status = 2;
						outTime=StrUtil.formatDateTime(ioh.getOutTime());
					}
					int depFree=(int) (ioh.getFactMoney()==null?0:ioh.getFactMoney()*100);
					int free=(int) (ioh.getShouldMoney()==null?0:ioh.getShouldMoney()*100);
					content=StrUtil.formatString(content, plateNo,parkId+id,inTime,outTime,status,userType,id,depFree,free);
					carInfo="data="+URLEncoder.encode("["+content+"]", "UTF-8");
			}
			String httpPostMssage = httpPostMssage(url, carInfo);
			log.info("{}停车场记录,结果:{}",type,httpPostMssage);
			return JSONObject.parseObject(httpPostMssage).get("ret").toString().equals("0");
		} catch (Exception e) {
			log.error(type+"停车场记录时发生错误",e);
		}
		return false;
	}
	@Override
	public boolean updateInOutHistory(SingleCarparkInOutHistory ioh) {
		return synchroInOutHistory("update", ioh);
	}

	@Override
	public boolean addUser(SingleCarparkUser user) {
		return synchroUser("add", user);
	}
	
	private boolean synchroUser(String type,SingleCarparkUser user) {
		log.info("{}用户信息",type);
		String url=httpUrl+"/api/syncMonthCard.action";
		try {
			String userInfo="";
			Long id = user.getId();
			if (!type.equals("delete")) {
				userInfo=",\"userMonthCard\":"
						+ "{\"carNum\":\"{}\",\"carType\":\"{}\",\"cardType\":\"1\",\"id\":\"{}\",\"buildingId\":\""+buildindId+"\","
						+ "\"parkId\":\""+parkId+"\",\"status\":\"{}\",\"tpEndTime\":\"{}\",\"tpStartTime\":\"2016-01-01 00:00:00\","
								+ "\"userAddress\":\"{}\",\"userName\":\"{}\",\"userPhone\":\"{}\",\"monthFee\":0}";
				String name = user.getName()==null?"":user.getName();
				String plateNo = user.getPlateNo();
				int carTypeIndex=0;
				CarTypeEnum carType = user.getCarType();
				if (carType.equals(CarTypeEnum.Motorcycle)) {
					carTypeIndex=3;
				}else if (carType.equals(CarTypeEnum.BigCar)) {
					carTypeIndex=1;
				}
				int userStatus=1;
				Date validTo = user.getValidTo()==null?user.getCreateDate():user.getValidTo();
				if (user.getValidTo()==null) {
					userStatus=0;
				}else if (validTo.before(new Date())) {
					userStatus=-1;
				}
				String address = user.getAddress()==null?"":user.getAddress();
				String telephone = user.getTelephone()==null?"":user.getTelephone();
				userInfo=StrUtil.formatString(userInfo, plateNo,carTypeIndex,parkId+id,userStatus,StrUtil.formatDateTime(validTo),address,name,telephone);
			}
			String parameters="[{\"dataId\":\"{}\",\"operation\":\""+type+"\",\"origin\":\"东陆高新\",\"syncId\":\"{}\""+userInfo+"}]";
			parameters=StrUtil.formatString(parameters, parkId+id,id);
			parameters="data="+URLEncoder.encode(parameters, "UTF-8");
			String httpPostMssage = httpPostMssage(url, parameters);
			JSONObject parseObject = JSONObject.parseObject(httpPostMssage);
			Object object = parseObject.get("ret");
			log.info("{}用户信息,结果：{}",type,httpPostMssage);
			return object.toString().equals("0");
		} catch (Exception e) {
			log.error(type+"用户信息",e);
		}
		return false;
	}

	@Override
	public boolean updateUser(SingleCarparkUser user) {
		return synchroUser("update", user);
	}
	@Override
	public boolean deleteUser(SingleCarparkUser user) {
		return synchroUser("delete", user);
	}
	@Override
	public void updateTempCarChargeHistory() {
		try {
			log.info("更新临时车缴费记录");
			String url=httpUrl+"/api/pullPaymentRecord.action?buildingId="+buildindId;
			String httpPostMssage = httpPostMssage(url, null);
			JSONObject Object = JSONObject.parseObject(httpPostMssage);
			String result = Object.getString("result");
			String data = Object.getString("data");
			if (!result.equals("success")||data==null) {
				return;
			}
			String string = URLDecoder.decode(data, "UTF-8");
			JSONArray parseArray = JSONObject.parseArray(string);
			log.info("更新临时车缴费记录，结果：{}",string);
			for (Object object2 : parseArray) {
				JSONObject jo=(JSONObject) object2;
				System.out.println(jo);
				JSONObject jData = JSONObject.parseObject(jo.getString("data"));
				String idLabel=jo.getString("id");
				float payedMoney=jData.getFloat("balanceAmount")/100;
				String plateNo = jData.getString("carNum");
				Date createTime = StrUtil.parseDateTime(jData.getString("createTimeStr"));
				CarPayHistory pay=new CarPayHistory();
				pay.setPayedMoney(payedMoney);
				pay.setPayTime(createTime);
				pay.setCreateDate(new Date());
				pay.setPlateNO(plateNo);
				pay.setRemark("CJLAPP支付");
				sp.getCarPayService().saveCarPayHistory(pay);
				String resultUrl=httpUrl+"/api/responseResult.action?ids="+idLabel;
				httpPostMssage(resultUrl, null);
			}
		} catch (Exception e) {
			log.error("更新临时车缴费记录时发生错误",e);
		}
	}
	@Override
	public void updateUserInfo() {
		try {
			log.info("更新固定用户信息");
			String url=httpUrl+"/api/pullMonthCard.action?buildingId="+buildindId;
			String httpPostMssage = httpPostMssage(url, null);
			JSONObject parseObject = JSONObject.parseObject(httpPostMssage);
			String result = (String) parseObject.get("result");
			if (!result.equals("success")) {
				return;
			}
			String data = (String) parseObject.get("data");
			if (data==null) {
				return;
			}
			data=URLDecoder.decode(data, "UTF-8");
			log.info("获取更新的用户信息：{}",data);
			JSONArray parseArray = JSONObject.parseArray(data);
			for (Object object : parseArray) {
				JSONObject jo = (JSONObject) object;
				JSONObject po = JSONObject.parseObject(jo.getString("data"));
				String status = po.getString("status");
				if (!status.equals("1")) {
					continue;
				}
				String tpEndTime = po.getString("tpEndTime");
				String id = ((String) po.get("id")).replace(parkId, "");
				Long valueOf = Long.valueOf(id);
				SingleCarparkUser user = sp.getCarparkUserService().findUserById(valueOf);
				if (user==null) {
					continue;
				}
				user.setValidTo(StrUtil.getTodayBottomTime(StrUtil.parseDateTime(tpEndTime)));
				sp.getCarparkUserService().saveUser(user);
				String resultUrl=httpUrl+"/api/responseResult.action?ids="+jo.getString("id");
				httpPostMssage(resultUrl, null);
			}
		} catch (Exception e) {
			log.error("更新固定用户信息时发生错误",e);
		}
	}
	@Override
	public void updateFixCarChargeHistory() {
		try {
			log.info("更新固定车充值记录");
			String url=httpUrl+"/api/pullMonthCardRecharge.action?buildingId="+buildindId;
			String httpPostMssage = httpPostMssage(url, null);
			JSONObject parseObject = JSONObject.parseObject(httpPostMssage);
			String result = parseObject.getString("result");
			if (!result.equals("success")) {
				return;
			}
			String data = parseObject.getString("data");
			if (StrUtil.isEmpty(data)) {
				return;
			}
			data=URLDecoder.decode(data, "UTF-8");
			log.info("更新固定车充值记录：{}",data);
			JSONArray parseArray = JSONObject.parseArray(data);
			for (Object object : parseArray) {
				JSONObject jo=(JSONObject) object;
				JSONObject jData = JSONObject.parseObject(jo.getString("data"));
				String createDateTime = JSONObject.parseObject(jData.getString("createDate")).getString("time");
				String endDateTime = JSONObject.parseObject(jData.getString("endDate")).getString("time");
				Long id=Long.valueOf(jData.getString("monthCardId").replace(parkId, ""));
				float money=Float.valueOf(jData.getString("rechargeAmount"))/100;
				SingleCarparkUser user = sp.getCarparkUserService().findUserById(id);
				if (user==null) {
					continue;
				}
				if (user.getType().equals("储值")) {
					user.setLeftMoney(user.getLeftMoney()+money);
					sp.getCarparkUserService().saveUser(user);
				}
				SingleCarparkMonthlyUserPayHistory mup=new SingleCarparkMonthlyUserPayHistory();
				mup.setCreateTime(new Date(Long.valueOf(createDateTime)));
				mup.setOldOverDueTime(user.getValidTo());
				mup.setOverdueTime(new Date(Long.valueOf(endDateTime)));
				mup.setPlateNO(user.getPlateNo());
				mup.setChargesMoney(money);
				mup.setCarType(user.getCarType().toString());
				mup.setUserName(user.getName());
				mup.setUserType(user.getType());
				mup.setParkingSpace(user.getParkingSpace());
				mup.setRemark("CJLAPP充值");
				sp.getCarparkService().saveMonthlyUserPayHistory(mup);
				String resultUrl=httpUrl+"/api/responseResult.action?ids="+jo.getString("id");
				httpPostMssage(resultUrl, null);
			}
		} catch (Exception e) {
			log.error("更新固定用户充值记录时发生错误",e);
		}
		
	}
	public int pay(SingleCarparkInOutHistory inout,float chargeMoney){
		try {
			log.info("车辆:{}出场,请求扣费:{}",inout.getPlateNo(),chargeMoney);
			if (chargeMoney==0) {
				return 2005;
			}
			int fenMoney=(int) (chargeMoney*100);
			String recordId=parkId+inout.getId();
			String plateNO=URLEncoder.encode(inout.getPlateNo(), "UTF-8");
			int countTime = StrUtil.countTime(inout.getInTime(), inout.getOutTime(), TimeUnit.MINUTES);
			String url=httpUrl+"/api/autoCharge.action?recordId="+recordId+"&buildingId="+buildindId+"&carNum="+plateNO+"&deptFee="+fenMoney+"&parkId="+parkId+"&totalTime="+countTime;
			String httpPostMssage = httpPostMssage(url, null);
			JSONObject parseObject = JSONObject.parseObject(httpPostMssage);
			String string = parseObject.get("resultCode").toString();
			int valueOf = Integer.valueOf(string.trim());
			return valueOf;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 9999;
	}
	
	public static void main(String[] args) throws Exception {
//		String upload = FileuploadSend.upload("http://127.0.0.1:8899/server/", null);
//		System.out.println(upload);
		IpmsServiceImpl is = new IpmsServiceImpl();
//		SingleCarparkInOutHistory ioh = new SingleCarparkInOutHistory();
//		ioh.setPlateNo("粤BD021W");
//		ioh.setId(9L);
//		ioh.setInTime(StrUtil.parseDateTime("2016-08-03 08:16:59"));
//		//添加记录
//		is.addInOutHistory(ioh);
		//删除记录
//		is.synchroInOutHistory("delete", ioh);
		//更新记录
//		ioh.setShouldMoney(5);
//		ioh.setFactMoney(25);
//		ioh.setOutTime(new Date());
//		is.synchroInOutHistory("update", ioh);
		
//		ioh.setOutTime(DateTime.now().plusHours(4).toDate());
//		is.pay(ioh, 1);
		
//		is.updateUserInfo();
		
//		is.updateTempCarChargeHistory();
//		is.updateFixCarChargeHistory();
		
		SingleCarparkUser user = new SingleCarparkUser();
		user.setId(1L);
//		user.setCreateDate(new Date());
//		user.setValidTo(DateTime.now().plusMonths(2).toDate());
//		user.setPlateNo("粤BD022W");
//		user.setCarType(CarTypeEnum.SmallCar);
//		is.addUser(user);
		
		is.synchroUser("delete", user);
		
		
//		String parameters="[{\"dataId\":\"\",\"operation\":\"add\",\"origin\":\"东陆高新\",\"syncId\":\"3\",\"userMonthCard\":{\"carNum\":\" 粤B12345\",\"carType\":\"1\",\"cardType\":\"1\",\"id\":\"3\",\"buildingId\":\"1111111111111\",\"parkId\":\"1111111111111\",\"status\":\"1\",\"tpEndTime\":\"2016-03-16 10:20:25\",\"tpStartTime\":\"2016-03-1610:20:26\",\"userAddress\":\"\",\"userName\":\"AAA\",\"userPhone\":\"123456789\",\"monthFee\":1000}}]";
//		JSONArray parseArray = JSONObject.parseArray(parameters);
//		System.out.println();
	}
	private String postMssage(String actionUrl, Map<String, Object> maps) throws Exception{
		Set<String> keySet = maps.keySet();
		String parameters=null;
		for (String p : keySet) {
			Object object = maps.get(p);
			if(parameters==null){
				parameters=p+"="+object;
			}else{
				parameters+="&"+p+"="+object;
			}
		}
		return httpPostMssage(actionUrl, parameters);
	}
	private String httpPostMssage(String actionUrl, String parameters) throws Exception {
		log.debug("准备对地址：["+actionUrl+"]发送消息:"+parameters);
		URL url = new URL(actionUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setUseCaches(false);
		connection.setInstanceFollowRedirects(true);
		connection.setRequestProperty("Charset", "UTF-8");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.connect();
		if (parameters!=null) {
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			out.writeBytes(parameters);
			out.flush();
			out.close();
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String lines;
		StringBuffer sbf = new StringBuffer();
		while ((lines = reader.readLine()) != null) {
			lines = new String(lines.getBytes(), "utf-8");
			sbf.append(lines);
		}
		String msg = sbf.toString();
		log.debug(msg);
		reader.close();
		// 断开连接
		connection.disconnect();
		return msg;
	}
}
