package com.donglu.carpark.service.impl;

import java.util.Base64;
import java.util.Date;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.WebService;
import com.donglu.carpark.ui.CarparkMainPresenter;
import com.donglu.carpark.util.CarparkUtils;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class WebServiceImpl implements  WebService{
	private static final Logger LOGGER = LoggerFactory.getLogger(WebServiceImpl.class);
	private static final String url="http://112.124.115.117/WebService/RQDataExchange.asmx?WSDL";
	private static final String company="深圳市元诺智能系统有限公司";
	private static final String area="测试停车场";
	private JaxWsDynamicClientFactory factory;
	private Client client;
	
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Override
	public boolean sendUser(SingleCarparkUser u) {
		String content="<Root LicenseNumber =\"{}\" KaTypeName=\"{}\" Back=\"{}\" CLAdder=\"{}\" CLName=\"{}\" "
				+ "CLDH=\"{}\" StartDate =\"{}\" EndDate =\"{}\" DutyNumber =\"\" DutyName =\"\" "
				+ "ChargesAmount =\"{}\" Company =\"{}\" Area=\"{}\"></Root>";
		String plateNo = u.getPlateNo();
		Object send = send(url,"MonthCardData",content,plateNo,"",u.getRemark(),u.getAddress(),u.getName(),"",
				StrUtil.formatDate(u.getCreateDate(), CarparkUtils.DATE_PATTERN),StrUtil.formatDate(u.getValidTo(), CarparkUtils.DATE_PATTERN),200,company,area);
		String s=(String) send;
		
		int parseInt = Integer.parseInt(s.substring(13, 14));
		if (parseInt==1) {
			LOGGER.info("上传用户{}信息成功",plateNo);
			return true;
		}else{
			LOGGER.error("上传用户{}信息失败",plateNo);
			return false;
		}
	}
	@Override
	public boolean sendInHistory(SingleCarparkInOutHistory in){
		String content="<Root  EntereDate=\"{}\" LicenseNumber=\"{}\" Category=\"0\" CarType=\"{}\" DutyNumber=\"\" "
				+ "DutyName=\"\" PortCharges=\"\" Company=\"{}\" Area=\"{}\" PD=\"0\"><Document xmlns:dt=\"urn:schemas-microsoft-com:datatypes\" "
				+ "dt:dt=\"bin.base64\" DocumentName=\"{}\" DocumentExt=\"{}\">{}</Document></Root>";
		
		String encodeToString = Base64.getEncoder().encodeToString(CarparkUtils.getImageByte(in.getBigImg()));
		String plateNo = in.getPlateNo();
		Object send = send(url,"EntereData", content, StrUtil.formatDate(in.getInTime(), CarparkUtils.DATE_MINUTE_PATTEN),plateNo,in.getCarType(),company,area,in.getBigImg().substring(in.getBigImg().lastIndexOf("/")),"jpg",encodeToString);
		String s=(String) send;
		int parseInt = Integer.parseInt(s.substring(13, 14));
		if (parseInt==1) {
			LOGGER.info("上传{}进场信息成功",plateNo);
			return true;
		}else{
			LOGGER.error("上传{}进场信息失败",plateNo);
			return false;
		}
	}
	@Override
	public boolean sendOutHistory(SingleCarparkInOutHistory out){
		String content="<Root  OutDate=\"{}\" EntereDate=\"{}\" KaTypeName=\"\" LicenseNumber=\"{}\" "
				+ "Category=\"1\"  CarType=\"{}\" DutyNumber=\"\" DutyName=\"\" PortCharges=\"\" ChargesAmount=\"{}\" "
						+ "IfFree=\"{}\" FreeAmount=\"{}\" FreeReason=\"\" IfPreferential=\"{}\" PreferentialAmount=\"{}\" "
						+ "PreferentialReason=\"\" Company =\"\" Area=\"\"> <Document xmlns:dt=\"urn:schemas-microsoft-com:datatypes\" "
						+ "dt:dt=\"bin.base64\" DocumentName=\"{}\" DocumentExt=\"{}\">{}</Document></Root>";
		String encodeToString = Base64.getEncoder().encodeToString(CarparkUtils.getImageByte(out.getOutBigImg()));
		Float factMoney = out.getFactMoney();
		int ifFree=factMoney<=0?1:0;
		Float freeMoney = out.getFreeMoney();
		int IfPreferential=factMoney>0&&freeMoney>0?1:0;
		String outTime = StrUtil.formatDate(out.getOutTime(), CarparkUtils.DATE_MINUTE_PATTEN);
		String inTime = StrUtil.formatDate(out.getInTime(), CarparkUtils.DATE_MINUTE_PATTEN);
		String imgName = out.getBigImg().substring(out.getBigImg().lastIndexOf("/"));
		String plateNo = out.getPlateNo();
		String carType = out.getCarType();
		Object send = send(url,"EntereData", content,outTime,inTime,plateNo,carType,factMoney,ifFree,freeMoney,IfPreferential,freeMoney,imgName,"jpg",encodeToString);
		String s=(String) send;
		int parseInt = Integer.parseInt(s.substring(13, 14));
		if (parseInt==1) {
			LOGGER.info("上传{}出场信息成功",plateNo);
			return true;
		}else{
			LOGGER.error("上传{}出场信息失败",plateNo);
			return false;
		}
	}
	@Override
	public boolean sendCarparkInfo(){
		String content="<Root TYCode=\"9083783\" TNum01=\"{}\" TNum02=\"{}\"  TNum03=\"{}\"  TNum04=\"0\" TNum05=\"0\"  Num02=\"0\"  "
				+ "Num06=\"0\"  Company =\"{}\" Area=\"{}\"></Root>";
		int totalCar=sp.getCarparkInOutService().findTotalCarIn();
		int tempCar=sp.getCarparkInOutService().findTotalTempCarIn();
		int fixCar=sp.getCarparkInOutService().findTotalFixCarIn();
		
		Object send = send(url,"VehicleData", content, totalCar,tempCar,fixCar,company,area);
		String s=(String) send;
		int parseInt = Integer.parseInt(s.substring(13, 14));
		if (parseInt==1) {
			LOGGER.info("上传停车场信息成功");
			return true;
		}else{
			LOGGER.error("上传停车场信息失败");
			return false;
		}
	}
	
	private Object send(String url,String method, String content,Object... o) {
		if (StrUtil.isEmpty(factory)) {
			factory = JaxWsDynamicClientFactory.newInstance();
		}
		if (StrUtil.isEmpty(client)) {
			client = factory.createClient(url);
		}
	    try {
	    	
			String message = MessageFormatter.arrayFormat(content, o).getMessage();
			LOGGER.info("上传{}信息到{}",message,url);
			Object[] obj =client.invoke(method,message);
			LOGGER.info("获得返回值{}",obj[0]);
			return obj[0];
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		WebServiceImpl w=new WebServiceImpl();
		w.userTest();
	}
	private void userTest(){
		SingleCarparkUser u = new SingleCarparkUser();
		u.setPlateNo("粤A12345");
		u.setName("黄XX");
		u.setCreateDate(new Date());
		u.setRemark("去去去去去去");
		u.setAddress("西乡");
		u.setValidTo(new DateTime(2888,12,12,1,1).toDate());
		sendUser(u);
	}
}
