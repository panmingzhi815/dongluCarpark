package com.donglu.carpark.service.impl;

import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import com.donglu.carpark.server.imgserver.YunConfigUI;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.WebService;
import com.donglu.carpark.util.CarparkFileUtils;
import com.donglu.carpark.util.CarparkUtils;
import com.donglu.carpark.yun.CarparkYunConfig;
import com.donglu.carpark.yun.RQDataExchange;
import com.donglu.carpark.yun.RQDataExchangeSoap;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkLockCar;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class WebServiceImpl implements  WebService{
	private static final Logger LOGGER = LoggerFactory.getLogger(WebServiceImpl.class);
	private static String url="http://112.124.115.117/WebService/RQDataExchange.asmx?WSDL";
	private static final QName SERVICE_NAME = new QName("http://tempuri.org/", "RQDataExchange");
	private String company="深圳市元诺智能系统有限公司";
	private String area="测试停车场";

	@Inject
	private CarparkDatabaseServiceProvider sp;
	private RQDataExchangeSoap port;
	
	
	public WebServiceImpl() {
	}
	/**
	 * 
	 */
	@Override
	public void init() {
		CarparkYunConfig cf = (CarparkYunConfig) CarparkFileUtils.readObject(YunConfigUI.CARPARK_YUN_CONFIG);
		String company = cf.getCompany();
		this.company=company;
		String area = cf.getArea();
		this.area=area;
		String u = System.getProperty("yunUploadUrl");
		if (!StrUtil.isEmpty(u)) {
			url = u;
		}
		LOGGER.info("物业公司:{}，停车场:{}",company,area);
		URL wsdlURL = RQDataExchange.WSDL_LOCATION;
		if (!StrUtil.isEmpty(url)) {
			try {
				wsdlURL = new URL(url);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		
		RQDataExchange ss = new RQDataExchange(wsdlURL, SERVICE_NAME);
		port = ss.getRQDataExchangeSoap();
	}
	@Override
	public boolean sendUser(SingleCarparkUser u) {
		String s="";
		try {
			String content="<Root LicenseNumber =\"{}\" KaTypeName=\"{}\" Back=\"{}\" CLAdder=\"{}\" CLName=\"{}\" "
					+ "CLDH=\"{}\" StartDate =\"{}\" EndDate =\"{}\" DutyNumber =\"\" DutyName =\"\" "
					+ "ChargesAmount =\"{}\" Company =\"{}\" Area=\"{}\"></Root>";
			String plateNo = u.getPlateNo();
			String message = getMessage(content,plateNo,"",u.getRemark(),u.getAddress(),u.getName(),"",
					StrUtil.formatDate(u.getCreateDate(), CarparkUtils.DATE_PATTERN),StrUtil.formatDate(u.getValidTo(), CarparkUtils.DATE_PATTERN),200,company,area);
			s = port.monthCardData(message);
			if (StrUtil.isEmpty(s)) {
				return false;
			}
			if (s.indexOf("数据重复上传")>0) {
				LOGGER.info("成功结果：数据重复上传{}",s);
				return true;
			}
			int parseInt = Integer.parseInt(s.substring(13, 14));
			if (parseInt==1) {
				LOGGER.info("成功结果：{}",s);
				return true;
			}else{
				LOGGER.error("上传用户{}信息失败"+s,plateNo);
				return false;
			}
		} catch (Exception e) {
			LOGGER.info("上传失败"+s);
			return false;
		}
	}
	@Override
	public boolean sendInHistory(SingleCarparkInOutHistory in){
		String s = null;
		try {
			String content="<Root  EntereDate=\"{}\" LicenseNumber=\"{}\" Category=\"0\" CarType=\"{}\" DutyNumber=\"\" "
					+ "DutyName=\"\" PortCharges=\"\" Company=\"{}\" Area=\"{}\" PD=\"0\"><Document xmlns:dt=\"urn:schemas-microsoft-com:datatypes\" "
					+ "dt:dt=\"bin.base64\" DocumentName=\"{}\" DocumentExt=\"{}\">{}</Document></Root>";
			
			byte[] imageByte = CarparkUtils.getImageByte(in.getBigImg());
			if (imageByte==null) {
				imageByte=new byte[0];
			}
			String encodeToString = Base64.getEncoder().encodeToString(imageByte);
			String plateNo = in.getPlateNo();
			
			String message = getMessage(content, StrUtil.formatDate(in.getInTime(), CarparkUtils.DATE_MINUTE_PATTEN),plateNo,in.getCarType(),company,area,in.getBigImg().substring(in.getBigImg().lastIndexOf("/")),"jpg",encodeToString);
			s = port.entereData(message);
			if (StrUtil.isEmpty(s)) {
				return false;
			}
			if (s.indexOf("数据重复上传")>0) {
				LOGGER.info("成功结果：数据重复上传{}",s);
				return true;
			}
			int parseInt = Integer.parseInt(s.substring(13, 14));
			if (parseInt==1) {
				LOGGER.info("成功结果：{}",s);
				return true;
			}else{
				LOGGER.error("上传{}进场信息失败"+s,plateNo);
				return false;
			}
		} catch (Exception e) {
			LOGGER.info("上传失败"+s);
			return false;
		}
	}
	@Override
	public boolean sendOutHistory(SingleCarparkInOutHistory out){
		String s = null;
		try {
			String content="<Root  OutDate=\"{}\" EntereDate=\"{}\" KaTypeName=\"\" LicenseNumber=\"{}\" "
					+ "Category=\"1\"  CarType=\"{}\" DutyNumber=\"\" DutyName=\"\" PortCharges=\"\" ChargesAmount=\"{}\" "
							+ "IfFree=\"{}\" FreeAmount=\"{}\" FreeReason=\"\" IfPreferential=\"{}\" PreferentialAmount=\"{}\" "
							+ "PreferentialReason=\"\" Company =\"{}\" Area=\"{}\"> <Document xmlns:dt=\"urn:schemas-microsoft-com:datatypes\" "
							+ "dt:dt=\"bin.base64\" DocumentName=\"{}\" DocumentExt=\"{}\">{}</Document></Root>";
			String encodeToString = Base64.getEncoder().encodeToString(CarparkUtils.getImageByte(out.getOutBigImg()));
			Float factMoney = out.getFactMoney();
			int ifFree=factMoney<=0?1:0;
			Float freeMoney = out.getFreeMoney()==null?0:out.getFreeMoney();
			int IfPreferential=factMoney>0&&freeMoney>0?1:0;
			String outTime = StrUtil.formatDate(out.getOutTime(), CarparkUtils.DATE_MINUTE_PATTEN);
			String inTime = StrUtil.formatDate(out.getInTime(), CarparkUtils.DATE_MINUTE_PATTEN);
			String imgName = out.getBigImg().substring(out.getBigImg().lastIndexOf("/"));
			String plateNo = out.getPlateNo();
			String carType = out.getCarType();
			String message = getMessage(content,outTime,inTime,plateNo,carType,factMoney,ifFree,freeMoney,IfPreferential,freeMoney,company,area,imgName,"jpg",encodeToString);
//			LOGGER.info("上传数据：{}",message);
			s = port.chargesData(message);
			if (StrUtil.isEmpty(s)) {
				return false;
			}
			if (s.indexOf("数据重复上传")>0) {
				LOGGER.info("成功结果：数据重复上传{}",s);
				return true;
			}
			int parseInt = Integer.parseInt(s.substring(13, 14));
			if (parseInt==1) {
				LOGGER.info("成功结果：{}",s);
				return true;
			}else{
				LOGGER.error("上传{}出场信息失败"+s,plateNo);
				return false;
			}
		} catch (Exception e) {
			LOGGER.info("上传失败"+s);
			return false;
		}
	}
	@Override
	public boolean sendCarparkInfo(SingleCarparkCarpark carpark){
		String content="<Root TYCode=\"9083783\" TNum01=\"{}\" TNum02=\"{}\"  TNum03=\"{}\"  TNum04=\"0\" TNum05=\"0\"  Num02=\"0\"  "
				+ "Num06=\"0\"  Company =\"{}\" Area=\"{}\"></Root>";
		String s = null;
		try {
			int totalCar=sp.getCarparkInOutService().findTotalCarIn(carpark);
			int tempCar=sp.getCarparkInOutService().findTotalTempCarIn(carpark);
			int fixCar=sp.getCarparkInOutService().findTotalFixCarIn(carpark);
			LOGGER.info("停车场{},场内车辆总数{}，场内临时车总数{}，场内固定车总数{}",carpark,totalCar,tempCar,fixCar);
			String message = getMessage(content, totalCar,tempCar,fixCar,company,area);
			s = port.vehicleData(message);
			if (StrUtil.isEmpty(s)) {
				return false;
			}
			s.substring(13);
			int parseInt = Integer.parseInt(s.substring(13, 14));
			if (parseInt==1) {
				return true;
			}else{
				LOGGER.error("上传停车场信息失败"+s);
				return false;
			}
		} catch (Exception e) {
			LOGGER.info("上传失败"+s);
			return false;
		}
	}
	
	@Override
	public boolean lockCar(String plateNO,int status) {
		String content="<Root Company=\"{}\" Area=\"{}\" LicenseNumber=\"{}\" ZTID=\"{}\"/>";
		String message = getMessage(content, company,area,plateNO,status);
		String writeHTDate = port.writeHTDate(message);
		if (writeHTDate.indexOf("成功")!=-1) {
			return true;
		}
		return false;
	}
	@Override
	public boolean getLockCarInfo() {
		try {
			String content = "<Root DZ=\"读取所有待处理数据\" Company =\"{}\" Area=\"{}\"></Root>";
			String returnContent="<Root ID=\"{}\" JGID=\"{}\" JGBack=\"{}\"/>";
			String message = getMessage(content, company, area);
			String readDBDate = port.readDBDate(message);
			System.out.println("获取云平台上的所有锁车数据==================" + readDBDate);
			Document document = DocumentHelper.parseText(readDBDate);
			Element rootElement = document.getRootElement();
			Iterator<Element> iters = rootElement.elementIterator("V_KHXS0240");
			while (iters.hasNext()) {
				Element itemEle = (Element)iters.next();
				String  id= itemEle.attributeValue("ID");
				String plateNO = itemEle.attributeValue("LicenseNumber");
				String ztid = itemEle.attributeValue("ZTID");
				String info="";
				String result="1";
				try {
					if (ztid.equals("1")) {
						sp.getCarparkInOutService().lockCar(plateNO);
					}else if(ztid.equals("2")){
						SingleCarparkLockCar findLockCarByPlateNO = sp.getCarparkInOutService().findLockCarByPlateNO(plateNO,true);
						if (StrUtil.isEmpty(findLockCarByPlateNO)) {
							continue;
						}
						findLockCarByPlateNO.setStatus(SingleCarparkLockCar.Status.已解锁.name());
						sp.getCarparkInOutService().saveLockCar(findLockCarByPlateNO);
					}
				} catch (Exception e) {
					result="2";
				}finally{
					String writeDBDate = port.writeDBDate(getMessage(returnContent, id,result,info));
					LOGGER.info("获得数据：[车牌：{}，操作：{}]。操作结果：{},{}",plateNO,ztid,result,writeDBDate);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	private String getMessage(String content, Object... o) {
		String message = CarparkUtils.formatString(content,o);
		return message;
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
