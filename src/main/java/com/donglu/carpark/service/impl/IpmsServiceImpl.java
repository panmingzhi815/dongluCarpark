package com.donglu.carpark.service.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.json.JsonArray;
import javax.json.JsonObject;

import org.apache.noggit.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.donglu.carpark.model.Result;
import com.donglu.carpark.server.imgserver.YunConfigUI;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.IpmsServiceI;
import com.donglu.carpark.util.CarparkFileUtils;
import com.donglu.carpark.yun.CarparkYunConfig;
import com.dongluhitec.card.domain.db.singlecarpark.CarPayHistory;
import com.dongluhitec.card.domain.db.singlecarpark.CarTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyUserPayHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.CarPayHistory.PayTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class IpmsServiceImpl implements IpmsServiceI {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private String name = "东陆高新";
	SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmssSSS");
	
	private CarparkDatabaseServiceProvider sp;
	//b57c0ebcd45846d996eabc962f54766d
	private String buildindId="";
	private String parkId="";
	private String httpUrl="";
	
	public Map<Long, Integer> mapImageUploadErrorSize=new HashMap<>();
	
	@Inject
	public IpmsServiceImpl(CarparkDatabaseServiceProvider sp) {
		this.sp = sp;
		try {
			CarparkYunConfig instance =(CarparkYunConfig) CarparkFileUtils.readObject(YunConfigUI.CARPARK_YUN_CONFIG);
			parkId=instance.getAreaCode();
			buildindId=instance.getCompanyCode();
			httpUrl=instance.getUrl();
			name=instance.getCompany();
			log.info("获取信息parkId={}，buildindId={}，httpUrl={}，name={}",parkId,buildindId,httpUrl,name);
			if(!StrUtil.isEmpty(httpUrl)){
				int lastIndexOf = httpUrl.lastIndexOf("/");
				if(lastIndexOf==httpUrl.length()-1){
					httpUrl=httpUrl.substring(0, httpUrl.length()-1);
				}
			}
		} catch (Exception e) {
			
		}
	}
	public IpmsServiceImpl() {}
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
//			initCarpark(ioh.getCarparkId(),ioh.getId());
			log.info("{}停车场记录,车牌：{}", type,ioh.getPlateNo());
			String url = httpUrl + "/api/syncParkingRecord.action";
			String content = "{\"operation\":\"" + type + "\",\"origin\":\"" + name + "\","
					+ "\"parkingRecord\":{\"carNum\":\"{}\",\"carType\":\"{}\",\"id\":\"{}\",\"inTimeStr\":\"{}\",\"outTimeStr\":\"{}\",\"buildingId\":\"" + buildindId + "\",\"parkId\":\"" + parkId
					+ "\",\"parkName\":\""+ioh.getCarparkName()+"\",\"status\":\"{}\",\"userType\":\"{}\",\"deptFee\"={},\"fee\"={},\"couponValue\"={}},\"syncId\":\"{}\"}";
			String carInfo = null;
			String plateNo = ioh.getPlateNo();
			Long id = ioh.getId();
			String inTime = StrUtil.formatDateTime(ioh.getInTime());
			int userType = 4;
			if (!StrUtil.isEmpty(ioh.getUserName()) && (ioh.getCarType()==null||ioh.getCarType().equals("固定车"))) {
				userType = 1;
			}
			int carType=0;
			if (ioh.getUserType().contains("大车")) {
				carType=1;
			}else if (ioh.getUserType().contains("超大车")) {
				carType=2;
			}else if (ioh.getUserType().contains("摩托车")) {
				carType=3;
			}
			int status = 0;
			String outTime = "";
			if (!StrUtil.isEmpty(ioh.getOutTime())) {
				status = 2;
				outTime = StrUtil.formatDateTime(ioh.getOutTime());
			}
			int depFree = (int) (ioh.getFactMoney() == null ? 0 : ioh.getFactMoney() * 100);
			int fee = (int) (ioh.getShouldMoney() == null ? 0 : ioh.getShouldMoney() * 100);
			int couponValue = fee-depFree;
			content = StrUtil.formatString(content, plateNo,carType, parkId + id, inTime, outTime, status, userType, depFree, fee,couponValue, id);
//			System.out.println(content);
			carInfo = "data=" + URLEncoder.encode("["+content+"]", "UTF-8");
			String actionUrl = url;
//			System.out.println(actionUrl);
			String httpPostMssage = httpPostMssage(actionUrl, carInfo);
			log.info("{}停车场记录,结果:{}", type, httpPostMssage);
			boolean result = JSONObject.parseObject(httpPostMssage).get("ret").toString().equals("0");

			if (result && !type.equals("delete")) {
				url = httpUrl + "/api/syncImgData.action?recordId=" + parkId + id + "";
				Map<String, Object> map = new HashMap<>();
				if (type.equals("add")) {
					if (ioh.getBigImg()!=null) {
						byte[] image = sp.getImageService().getImage(ioh.getBigImg().substring(ioh.getBigImg().lastIndexOf("/") + 1));
						if (!StrUtil.isEmpty(image)) {
							map.put("enterImg", URLEncoder.encode(Base64.getEncoder().encodeToString(image), "UTF-8"));
						} 
					}
				}
				if (type.equals("update")&&ioh.getOutBigImg()!=null) {
					if (ioh.getOutBigImg()!=null) {
						byte[] image = sp.getImageService().getImage(ioh.getOutBigImg().substring(ioh.getOutBigImg().lastIndexOf("/") + 1));
						if (!StrUtil.isEmpty(image)) {
							map.put("exitImg", URLEncoder.encode(Base64.getEncoder().encodeToString(image), "UTF-8"));
						} 
					}
				}
				if (map.keySet().size() > 0) {
					httpPostMssage = postMssage(url, map);
					log.info("上传图片，结果：{}", httpPostMssage);
					result = JSONObject.parseObject(httpPostMssage).get("ret").toString().equals("0");
					if (!result) {
						Integer integer = mapImageUploadErrorSize.getOrDefault(id, 0);
						if (integer>=10) {
							result=true;
							mapImageUploadErrorSize.remove(id);
						}else{
							mapImageUploadErrorSize.put(id, integer+1);
						}
					}else{
						mapImageUploadErrorSize.remove(id);
						Thread.sleep(1000);
					}
				}
			}
			return result;
		} catch (Exception e) {
			log.error(type+"停车场记录时发生错误",e);
		}
		return false;
	}
	private void initCarpark(Long carparkId,Long hid) {
		if (carparkId==null) {
			carparkId=sp.getCarparkInOutService().findInOutById(hid).getCarparkId();
		}
		SingleCarparkCarpark carpark = sp.getCarparkService().findCarparkById(carparkId);
		parkId=carpark.getYunIdentifier();
		buildindId=carpark.getYunBuildIdentifier();
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
			String rid = parkId+id;
			if (!type.equals("delete")) {
				userInfo=",\"userMonthCard\":"
						+ "{\"carNum\":\"{}\",\"carType\":\"{}\",\"cardType\":\"1\",\"id\":\"{}\",\"buildingId\":\""+buildindId+"\","
						+ "\"parkId\":\""+parkId+"\",\"status\":\"{}\",\"tpEndTime\":\"{}\",\"tpStartTime\":\"{}\","
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
				String tpStartTime="2016-01-01 00:00:00";
				if (user.getCreateDate()!=null) {
					tpStartTime=StrUtil.formatDateTime(user.getCreateDate());
				}
				String address = user.getAddress()==null?"":user.getAddress();
				String telephone = user.getTelephone()==null?"":user.getTelephone();
				userInfo=StrUtil.formatString(userInfo, plateNo,carTypeIndex,rid,userStatus,StrUtil.formatDateTime(validTo),tpStartTime,address,name,telephone);
			}
			String parameters="[{\"dataId\":\"{}\",\"operation\":\""+type+"\",\"origin\":\"东陆高新\",\"syncId\":\"{}\""+userInfo+"}]";
			parameters=StrUtil.formatString(parameters, rid,id);
			parameters="data="+URLEncoder.encode(parameters, "UTF-8");
			String httpPostMssage = httpPostMssage(url, parameters);
			JSONObject parseObject = JSONObject.parseObject(httpPostMssage);
			Object object = parseObject.get("ret");
			log.info("{}用户信息,结果：{}",type,httpPostMssage);
			boolean result = object.toString().equals("0");
			if (type.equals("add")&&!result&&parseObject.getString("retInfo").contains("已存在相同车")) {
				JSONObject users = getUsers(user.getPlateNo());
				parameters="[{\"dataId\":\"{}\",\"operation\":\"delete\",\"origin\":\"东陆高新\",\"syncId\":\"{}\""+userInfo+"}]";
				parameters=StrUtil.formatString(parameters, users.getString("id"),id);
				parameters="data="+URLEncoder.encode(parameters, "UTF-8");
				httpPostMssage = httpPostMssage(url, parameters);
				System.out.println(httpPostMssage);
//				return synchroUser("update", user);
				
			}
			return result;
		} catch (Exception e) {
			log.error(type+"用户信息",e);
		}
		return false;
	}

	private JSONObject getUsers(String plate) {
		String url=httpUrl+"/api/queryCreatMonthCardData.action";
		HashMap<String, Object> map = new HashMap<>();
//		map.put("parkId", parkId);
//		map.put("type", "0");
//		map.put("carNum", plate);
//		map.put("limit", 10);
//		map.put("Offset", 1);
		try {
			url= url+"?parkId="+parkId+"&type=0&carNum="+URLEncoder.encode(plate,"UTF-8")+"&limit="+10+"&Offset="+1;
			System.out.println(url);
			String postMssage = postMssage(url, map);
			JSONObject parseObject = JSONObject.parseObject(postMssage);
			String  data = parseObject.getString("data");
			data=URLDecoder.decode(data, "UTF-8");
			JSONArray jsonArray = JSONObject.parseObject(data).getJSONArray("datas");
			for (Object object : jsonArray) {
				JSONObject j=(JSONObject)object;
				System.out.println(j);
				return j;
			}
			System.out.println(postMssage);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
			log.debug("更新临时车缴费记录");
			String url=httpUrl+"/api/pullPaymentRecord.action?buildingId="+buildindId;
			String httpPostMssage = httpPostMssage(url, null);
			JSONObject Object = JSONObject.parseObject(httpPostMssage);
			String result = Object.getString("result");
			String data = Object.getString("data");
			log.debug("更新临时车缴费记录,返回结果：{}",result);
			if (!result.equals("success")||data==null) {
				return;
			}
			String string = URLDecoder.decode(data, "UTF-8");
			JSONArray parseArray = JSONObject.parseArray(string);
			log.info("更新临时车缴费记录，结果：{}",string);
			for (Object object2 : parseArray) {
				JSONObject jo=(JSONObject) object2;
				String idLabel=jo.getString("id");
//				System.out.println(jo);
				Long saveCarPayHistory =0l;
				JSONObject jData = JSONObject.parseObject(jo.getString("data"));
				int paymentStatus = jData.getIntValue("paymentStatus");
				if (paymentStatus!=0) {
					String payId = jData.getString("id");
					if(sp.getCarPayService().findCarPayHistoryByPayId(payId)==null){//判断账单是否存在
						float payedMoney=jData.getFloat("totalCost")/100;
						double balanceAmount=jData.getDouble("balanceAmount")/100;
						double cashCost=jData.getDouble("cashCost")/100;
						double onlineCost=jData.getDouble("onlineCost")/100;
						double couponValue=jData.getDouble("couponValue")/100;
						int couponTime=jData.getIntValue("couponTime");
						String plateNo = jData.getString("carNum");
						Date createTime = StrUtil.parseDateTime(jData.getString("payFinishTimeStr"));
						CarPayHistory pay=new CarPayHistory();
						pay.setPayedMoney(payedMoney);
						pay.setPayTime(createTime);
						pay.setCreateDate(new Date());
						pay.setPlateNO(plateNo);
						pay.setRemark("云平台支付");
						pay.setBalanceAmount(balanceAmount);
						pay.setCashCost(cashCost);
						pay.setOnlineCost(onlineCost);
						pay.setPayType(PayTypeEnum.getType(jData.getIntValue("paymentMethod")));
						pay.setPayId(payId);
						pay.setCouponTime(couponTime);
						pay.setCouponValue(couponValue);
						pay.setOperaName("在线缴费");
						String parkingRecordId = jData.getString("parkingRecordId");
						if (parkingRecordId.contains(parkId)) {
							parkingRecordId=parkingRecordId.replaceAll(parkId, "");
							getIdByRecordId(parkId,parkingRecordId);
						}
						pay.setHistoryId(Long.valueOf(parkingRecordId));
						SingleCarparkInOutHistory inOutHistory = sp.getCarparkInOutService().findInOutById(Long.valueOf(parkingRecordId));
						if (inOutHistory!=null) {
							pay.setInTime(inOutHistory.getInTime());
							pay.setOutTime(inOutHistory.getOutTime());
						}
						saveCarPayHistory = sp.getCarPayService().saveCarPayHistory(pay);
					}
				}
				try {
					String resultUrl=httpUrl+"/api/responseResult.action?ids="+idLabel;
					httpPostMssage(resultUrl, null);
				} catch (Exception e) {
					e.printStackTrace();
					if (saveCarPayHistory!=null&&saveCarPayHistory>0) {
						sp.getCarPayService().deleteCarPayHistory(saveCarPayHistory);
					}
				}
			}
		} catch (Exception e) {
			log.error("更新临时车缴费记录时发生错误"+e);
		}
	}
	private Long getIdByRecordId(String parkId, String parkingRecordId) {
		String s=parkingRecordId.replaceAll(parkId, "");
		if (s.length()>17) {
			String string = s.substring(s.length()-17);
			try {
				sdf.parse(string);
				s=s.substring(0, s.length()-17);
			} catch (Exception e) {
				
			}
		}
		return Long.valueOf(s);
	}
	@Override
	public void updateUserInfo() {
		try {
			log.debug("更新固定用户信息");
			String url=httpUrl+"/api/pullMonthCard.action?buildingId="+buildindId;
			
//			System.out.println(URLDecoder.decode(httpPostMssage(httpUrl+"/api/queryCreatMonthCardData.action?parkId="+parkId+"&type=1&carNum="+URLEncoder.encode("粤BD021W", "UTF-8"), null), "UTF-8"));
			
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
			JSONArray parseArray = JSONObject.parseArray(data);
			log.info("获取更新的用户信息：{}",parseArray.size());
			for (Object object : parseArray) {
				JSONObject jo = (JSONObject) object;
				JSONObject po = JSONObject.parseObject(jo.getString("data"));
				String status = po.getString("status");
				if (!status.equals("1")) {
					String resultUrl=httpUrl+"/api/responseResult.action?ids="+jo.getString("id");
					httpPostMssage(resultUrl, null);
					continue;
				}
				String tpEndTime = po.getString("tpEndTime");
				SingleCarparkUser user;
//				String id = ((String) po.get("id")).replace(parkId, "");
//				Long valueOf = Long.valueOf(id);
//				user = sp.getCarparkUserService().findUserById(valueOf);
				user=sp.getCarparkUserService().findUserByPlateNo(po.getString("carNum"), null);
				if (user==null) {
					String resultUrl=httpUrl+"/api/responseResult.action?ids="+jo.getString("id");
					httpPostMssage(resultUrl, null);
					continue;
				}
				user.setValidTo(StrUtil.getTodayBottomTime(StrUtil.parseDateTime(tpEndTime)));
				user.setCreateHistory(false);
				sp.getCarparkUserService().saveUser(user);
				String resultUrl=httpUrl+"/api/responseResult.action?ids="+jo.getString("id");
				httpPostMssage(resultUrl, null);
			}
		} catch (Exception e) {
			log.error("更新固定用户信息时发生错误"+e);
		}
	}
	@Override
	public void updateFixCarChargeHistory() {
		try {
			log.debug("更新固定车充值记录");
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
				mup.setRemark("云平台充值");
				sp.getCarparkService().saveMonthlyUserPayHistory(mup);
				String resultUrl=httpUrl+"/api/responseResult.action?ids="+jo.getString("id");
				httpPostMssage(resultUrl, null);
			}
		} catch (Exception e) {
			log.error("更新固定用户充值记录时发生错误"+e);
		}
		
	}
	@Override
	public void updateParkSpace(){
		try {
			log.debug("同步停车场车位信息。");
			String url=httpUrl+"/api/syncParkSpace.action";
			SingleCarparkCarpark carpark = sp.getCarparkService().findCarparkTopLevel();
			if (carpark==null) {
				return;
			}
			Integer totalSlotIsNow = sp.getCarparkInOutService().findTotalSlotIsNow(carpark);
			int tempSlotIsNow = sp.getCarparkInOutService().findTempSlotIsNow(carpark);
			JSONArray array = new JSONArray();
			JSONObject e = new JSONObject();
			e.put("parkId", parkId);
			e.put("totalCount", tempSlotIsNow);
			e.put("surplusCount", totalSlotIsNow.intValue());
			array.add(e);
			Map<String, Object> maps=new HashMap<>();
			String jsonString = array.toJSONString();
			maps.put("data", jsonString);
			String mssage = postMssage(url, maps);
			log.debug("同步停车场：{} 车位信息，结果为：{}",carpark,mssage);
		} catch (Exception e) {
			log.error("同步停车场车位信息时发生错误！");
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
	@Override
	public Result getPayResult(SingleCarparkInOutHistory inout){
		Result result = new Result();
		String plateNo = inout.getPlateNo();
		try {
			log.info("车辆:{}出场,请求查看扣费结果",plateNo);
			String recordId=parkId+inout.getId();
			String plateNO=URLEncoder.encode(plateNo, "UTF-8");
			String url=httpUrl+"/api/getPayedResult.action?carNum={}&recordId={}&parkId={}";
			url = StrUtil.formatString(url, plateNO,recordId,parkId);
			System.out.println(url);
			String httpPostMssage = httpPostMssage(url, null,5000);
			log.info("车辆:{}出场,请求查看扣费结果:{}",plateNo,httpPostMssage);
			JSONObject jsonObject = JSONObject.parseObject(httpPostMssage);
			int intValue = jsonObject.getIntValue("resultCode");
			result.setCode(intValue);
			result.setMsg(jsonObject.getString("resultMsg"));
			result.setObj(httpPostMssage);
			result.setDeptFee(jsonObject.getFloatValue("deptFee"));
			result.setPayedFee(jsonObject.getFloatValue("payedFee"));
			result.setOutTime(StrUtil.parseDateTime(jsonObject.getString("outTime")));
			return result;
		} catch (Exception e) {
			log.error("{}请求查看扣费时发生错误：{}",plateNo,e);
		}
		result.setCode(2009);
		result.setMsg("未在线支付");
		return result;
	}
	
	public static void main(String[] args) throws Exception {
//		String upload = FileuploadSend.upload("http://127.0.0.1:8899/server/", null);
//		System.out.println(upload);
		IpmsServiceImpl is = new IpmsServiceImpl();
		System.out.println(UUID.randomUUID().toString().replace("-", "").length());
		
		System.out.println(URLEncoder.encode("http://www.dongluhitec.net/weixin_zr/test/getRecordByCarNum.html?channelId=7e257819d2764bb6aa5c1fd43baf2f71", "UTF-8"));
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
		
//		SingleCarparkUser user = new SingleCarparkUser();
//		user.setId(1L);
//		user.setCreateDate(new Date());
//		user.setValidTo(DateTime.now().plusMonths(2).toDate());
//		user.setPlateNo("粤BD022W");
//		user.setCarType(CarTypeEnum.SmallCar);
//		is.addUser(user);
		
//		is.synchroUser("delete", user);
		
		
//		String parameters="[{\"dataId\":\"\",\"operation\":\"add\",\"origin\":\"东陆高新\",\"syncId\":\"3\",\"userMonthCard\":{\"carNum\":\" 粤B12345\",\"carType\":\"1\",\"cardType\":\"1\",\"id\":\"3\",\"buildingId\":\"1111111111111\",\"parkId\":\"1111111111111\",\"status\":\"1\",\"tpEndTime\":\"2016-03-16 10:20:25\",\"tpStartTime\":\"2016-03-1610:20:26\",\"userAddress\":\"\",\"userName\":\"AAA\",\"userPhone\":\"123456789\",\"monthFee\":1000}}]";
//		JSONArray parseArray = JSONObject.parseArray(parameters);
//		System.out.println();
		
		String url="http%3A%2F%2Fwww.lightcar.cn%2Fipms%2Fweixin_zr%2FweixinAction%21getOpenStep2.action%3FchannelId%3Db57c0ebcd45846d996eabc962f54766d";
		System.out.println(URLDecoder.decode(url, "UTF-8"));
//		UUID uuid = UUID.randomUUID();
//		System.out.println(uuid.toString());
//		Map<Object,Object> map = new HashMap<>(100000000);
//		int i = 0;
//		try {
//			for (; i < 100000000; i++) {
//				map.put(i, "123213213123124123213");
//			}
//			long nanoTime = System.nanoTime();
//			System.out.println(map.get(5000000));
//			System.out.println("nanoTime=="+(System.nanoTime()-nanoTime));
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//		System.out.println(i);
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
		return httpPostMssage(actionUrl, parameters,10000);
	}
	private String httpPostMssage(String actionUrl, String parameters,int readTimeOut) throws Exception {
		if (StrUtil.isEmpty(actionUrl)) {
			return null;
		}
		log.debug("准备对地址：["+actionUrl+"]发送消息:"+parameters);
		URL url = new URL(actionUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setReadTimeout(readTimeOut);
		connection.setConnectTimeout(3000);
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
