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
	private static final Logger LOGGER = LoggerFactory.getLogger(CarparkMainPresenter.class);
	private static final String url="http://112.124.115.117/WebService/RQDataExchange.asmx?WSDL";
	private static final String company="深圳市彩生活物业管理有限公司";
	private static final String area="和亨广场";
	private JaxWsDynamicClientFactory factory;
	private Client client;
	
	@Inject
	private CarparkDatabaseServiceProvider sp;
	/**
	 * 上传月租信息到云平台
	 * @param u
	 * @return
	 */
	public boolean sendUser(SingleCarparkUser u) {
		String content="<Root LicenseNumber =\"{}\" KaTypeName=\"{}\" Back=\"{}\" CLAdder=\"{}\" CLName=\"{}\" "
				+ "CLDH=\"{}\" StartDate =\"{}\" EndDate =\"{}\" DutyNumber =\"\" DutyName =\"\" "
				+ "ChargesAmount =\"{}\" Company =\"{}\" Area=\"{}\"></Root>";
		Object send = send(url,content,u.getPlateNo(),"",u.getRemark(),u.getAddress(),u.getName(),"",
				StrUtil.formatDate(u.getCreateDate(), StrUtil.DATE_PATTERN),StrUtil.formatDate(u.getValidTo(), StrUtil.DATE_PATTERN),200,company,area);
		String s=(String) send;
		
		int parseInt = Integer.parseInt(s.substring(13, 14));
		if (parseInt==1) {
			return true;
		}else{
			LOGGER.error("上传月租信息失败失败");
			return false;
		}
	}
	/**
	 * 上传进场信息到云平台
	 * @param out
	 * @return
	 */
	public boolean sendInHistory(SingleCarparkInOutHistory in){
		String content="<Root  EntereDate=\"{}\" LicenseNumber=\"{}\" Category=\"0\" CarType=\"{}\" DutyNumber=\"\" "
				+ "DutyName=\"\" PortCharges=\"\" Company=\"{}\" Area=\"{}\" PD=\"0\"><Document xmlns:dt=\"urn:schemas-microsoft-com:datatypes\" "
				+ "dt:dt=\"bin.base64\" DocumentName=\"{}\" DocumentExt=\"{}\">{}</Document></Root>";
		
		String encodeToString = Base64.getEncoder().encodeToString(CarparkUtils.getImageByte(in.getBigImg()));
		Object send = send(url, content, StrUtil.formatDate(in.getInTime(), StrUtil.DATE_MINUTE_PATTEN),in.getPlateNo(),in.getCarType(),company,area,
				in.getBigImg().substring(in.getBigImg().lastIndexOf("/"), in.getBigImg().lastIndexOf(".")),"jpg",encodeToString);
		String s=(String) send;
		int parseInt = Integer.parseInt(s.substring(13, 14));
		if (parseInt==1) {
			return true;
		}else{
			LOGGER.error("上传进场信息失失败");
			return false;
		}
	}
	/**
	 * 上传出场信息到云平台
	 * @param out
	 * @return
	 */
	public boolean sendOutHistory(SingleCarparkInOutHistory out){
		String content="<Root  OutDate=\"{}\" EntereDate=\"{}\" KaTypeName=\"\" LicenseNumber=\"{}\" "
				+ "Category=\"1\"  CarType=\"{}\" DutyNumber=\"\" DutyName=\"\" PortCharges=\"\" ChargesAmount=\"{}\" "
						+ "IfFree=\"{}\" FreeAmount=\"{}\" FreeReason=\"\" IfPreferential=\"{}\" PreferentialAmount=\"{}\" "
						+ "PreferentialReason=\"\" Company =\"\" Area=\"\"> <Document xmlns:dt=\"urn:schemas-microsoft-com:datatypes\" "
						+ "dt:dt=\"bin.base64\" DocumentName=\"{}\" DocumentExt=\"{}\">{}</Document></Root>";
		String encodeToString = Base64.getEncoder().encodeToString(CarparkUtils.getImageByte(out.getOutBigImg()));
		int ifFree=out.getFactMoney()<=0?1:0;
		int IfPreferential=out.getFactMoney()>0&&out.getFreeMoney()>0?1:0;
		Object send = send(url, content,StrUtil.formatDate(out.getOutTime(), StrUtil.DATE_MINUTE_PATTEN),StrUtil.formatDate(out.getInTime(), StrUtil.DATE_MINUTE_PATTEN),
				out.getPlateNo(),out.getCarType(),out.getFactMoney(),ifFree,out.getFreeMoney(),IfPreferential,out.getFreeMoney(),
				out.getBigImg().substring(out.getBigImg().lastIndexOf("/"), out.getBigImg().lastIndexOf(".")),"jpg",encodeToString);
		String s=(String) send;
		int parseInt = Integer.parseInt(s.substring(13, 14));
		if (parseInt==1) {
			return true;
		}else{
			LOGGER.error("上传进场信息失失败");
			return false;
		}
	}
	/**
	 * 上传停车场信息到云平台
	 * @return
	 */
	public boolean sendCarparkInfo(){
		String content="<Root TYCode=\"9083783\" TNum01=\"{}\" TNum02=\"{}\"  TNum03=\"{}\"  TNum04=\"0\" TNum05=\"0\"  Num02=\"0\"  "
				+ "Num06=\"0\"  Company =\"{}\" Area=\"{}\"></Root>";
		int totalCar=sp.getCarparkInOutService().findTotalCarIn();
		int tempCar=sp.getCarparkInOutService().findTotalTempCarIn();
		int fixCar=sp.getCarparkInOutService().findTotalFixCarIn();
		
		Object send = send(url, content, totalCar,tempCar,fixCar,company,area);
		String s=(String) send;
		int parseInt = Integer.parseInt(s.substring(13, 14));
		if (parseInt==1) {
			return true;
		}else{
			LOGGER.error("上传进场信息失失败");
			return false;
		}
	}
	
	private Object send(String url, String content,Object... o) {
		if (StrUtil.isEmpty(factory)) {
			factory = JaxWsDynamicClientFactory.newInstance();
		}
		if (StrUtil.isEmpty(client)) {
			client = factory.createClient(url);
		}
	    try {
	    	
			String message = MessageFormatter.arrayFormat(content, o).getMessage();
			System.out.println(message);
			LOGGER.info("上传{}信息到{}",message,url);
			Object[] obj =client.invoke("MonthCardData",message);
			System.out.println("resp:"+obj[0]);
			return obj[0];
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		WebServiceImpl w=new WebServiceImpl();
		SingleCarparkUser u = new SingleCarparkUser();
		u.setPlateNo("粤A12345");
		u.setName("黄XX");
		u.setCreateDate(new Date());
		u.setRemark("去去去去去去");
		u.setAddress("西乡");
		u.setValidTo(new DateTime(new Date()).plusMonths(12).toDate());
		w.sendUser(u);
	}

}
