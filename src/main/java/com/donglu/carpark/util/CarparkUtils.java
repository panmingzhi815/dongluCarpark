package com.donglu.carpark.util;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.Holder;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.wb.swt.SWTResourceManager;
import org.ini4j.Config;
import org.ini4j.Ini;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.server.CarparkServerConfig;
import com.donglu.carpark.ui.CarparkClientConfig;
import com.donglu.carpark.ui.CarparkMainPresenter;
import com.dongluhitec.card.domain.db.singlecarpark.DeviceRoadTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.DeviceVoiceTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.util.DatabaseUtil;

public class CarparkUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(CarparkUtils.class);
	
	public static final String PLATENO_REGEX="^[\u4e00-\u9fa5][A-Za-z0-9]{6}$";

	private static final String defaultKey = "donglucarpark";

	public static final String DATE_MINUTE_PATTEN = "yyyy-MM-dd HH:mm:ss";

	public static final String DATE_PATTERN = "yyyy-MM-dd";
	public static List<String> splitPlateNO(String plateNo){
		if (StrUtil.isEmpty(plateNo)) {
			return new ArrayList<>();
		}
		List<String> list=new ArrayList<>();
		for (int i=plateNo.length();i>0;i--) {
			for (int j=0;j<plateNo.length();j++) {
				if ((j+i)>plateNo.length()) {
					break;
				}
				String substring = plateNo.substring(j, j+i);
				list.add(substring);
			}
		}
		return list;
	}
	/**
	 * 日期相减的小时分钟差
	 * 
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static int MinusMinute(Date time1, Date time2) {
		long nm = 1000 * 60;// 一分钟的毫秒数
		long diff;
		// 获得两个时间的毫秒时间差异
		diff = time2.getTime() - time1.getTime();
		return Long.valueOf(diff / nm).intValue();
	}
	public static List<Date> cutDaysByDay(Date start, Date end) {
		long nd = 1000 * 60 * 60 * 24;// 一分钟的毫秒数
		long diff;
		// 获得两个时间的毫秒时间差异
		diff = end.getTime() - start.getTime();
		int day=Long.valueOf(diff/nd).intValue();
		List<Date> list=new ArrayList<>();
		list.add(start);
		Date date=start;
		if (day>0) {
			for (int i = 0; i < day; i++) {
				DateTime d = new DateTime(start);
				DateTime dateTime = new DateTime(d.getYear(),d.getMonthOfYear(),d.getDayOfMonth(),23,59,59).plusDays(i);
				date=dateTime.toDate();
				list.add(date);
			}
		}
		list.add(end);
		return list;
	}
	public static List<Date> cutDaysByHours(Date start, Date end) {
		long nd = 1000 * 60 * 60 ;// 一分钟的毫秒数
		long diff;
		// 获得两个时间的毫秒时间差异
		diff = end.getTime() - start.getTime();
		int hours=Long.valueOf(diff/nd).intValue()/24;
		List<Date> list=new ArrayList<>();
		list.add(start);
		Date date=start;
		if (hours>0) {
			for (int i = 0; i < hours; i++) {
				DateTime d = new DateTime(date).plusDays(1);
				date=d.toDate();
				list.add(date);
			}
		}
		list.add(end);
		return list;
	}
	/**
	 * 根据属性名获取属性值
	 * */
    public static Object getFieldValueByName(String fieldName, Object o) {
        try {
        	if (o instanceof Map) {
				Map m=(Map) o;
				return m.get(fieldName);
			}
//        	Field field = o.getClass().getDeclaredField(fieldName);
//        	Object value = field.get(o);
            String firstLetter = fieldName.substring(0, 1).toUpperCase();  
            String getter = "get" + firstLetter + fieldName.substring(1);  
            Method method = o.getClass().getMethod(getter, new Class[] {});  
            Object value = method.invoke(o, new Object[] {});  
            return value;  
        } catch (Exception e) {  
            e.printStackTrace();
            return null;  
        }  
    } 
    /**
	 * 根据属性名获取属性值
	 * */
    public static boolean setFieldValueByName(String fieldName, Object o,Object value) {
        try {
        	
//        	Field field = o.getClass().getDeclaredField(fieldName);
//        	Object value = field.get(o);
            String firstLetter = fieldName.substring(0, 1).toUpperCase();  
            String getter = "set" + firstLetter + fieldName.substring(1);  
            Method method = o.getClass().getMethod(getter, new Class[] {value.getClass()});
            method.invoke(o, new Object[] {value});  
            return true;  
        } catch (Exception e) {  
            e.printStackTrace();
            return false;  
        }  
    }
    
    /**
     * 获取属性名数组
     * */
    public static String[] getFiledName(Object o){
    	Field[] fields=o.getClass().getDeclaredFields();
       	String[] fieldNames=new String[fields.length];
    	for(int i=0;i<fields.length;i++){
//    		System.out.println(fields[i].getType());
    		fieldNames[i]=fields[i].getName();
    	}
    	return fieldNames;
    }
    
    /**
     * 获取属性类型(type)，属性名(name)，属性值(value)的map组成的list
     * */
    @SuppressWarnings("all")
	public static List getFiledsInfo(Object o){
    	Field[] fields=o.getClass().getDeclaredFields();
       	String[] fieldNames=new String[fields.length];
       	List list = new ArrayList();
       	Map infoMap=null;
    	for(int i=0;i<fields.length;i++){
    		infoMap = new HashMap();
    		infoMap.put("type", fields[i].getType().toString());
    		infoMap.put("name", fields[i].getName());
    		infoMap.put("value", getFieldValueByName(fields[i].getName(), o));
    		list.add(infoMap);
    	}
    	return list;
    }
    
    /**
     * 获取对象的所有属性值，返回一个对象数组
     * */
    public static Object[] getFiledValues(Object o){
    	String[] fieldNames=getFiledName(o);
    	Object[] value=new Object[fieldNames.length];
    	for(int i=0;i<fieldNames.length;i++){
    		value[i]=getFieldValueByName(fieldNames[i], o);
    	}
    	return value;
    }	

	
	public static void main(String[] args) {
		splitString("粤BD021W", 5);
		System.out.println(StrUtil.formatDateTime(getHourBottomTime(new Date())));
	}
	/**
	 * 0.0返回0
	 * @param s
	 * @return
	 */
	public static String formatFloatString(String s) {
//		String substring = s.substring(s.indexOf(".") + 1, s.indexOf(".") + 2);
//		Integer intValueOf = Integer.valueOf(substring);
//		if (intValueOf == 0) {
//			String ss = s.replace("." + intValueOf, "");
//			System.out.println(ss);
//			return ss;
//		}
		String ss=s;
		if(s.contains(".0")){
			int indexOf = s.indexOf(".0");
			try {
				Integer.valueOf(s.substring(indexOf + 2, indexOf + 3));
			} catch (Exception e) {
				ss=s.replace(".0", "");
			}
		}
		if(s.contains(".00")){
			ss=s.replace(".00", "");
		}
		System.out.println(ss);
		return ss;
	}
	/**
	 * 对list内的对象排序
	 * @param list
	 * @param string
	 * @param order
	 * @return
	 */
	public static <T> List<T> sortObjectPropety(List<T> list, String string,boolean order) {
		
		try {
			Collections.sort(list, new Comparator<T>() {
				
				@Override
				public int compare(T o1, T o2) {
					Object obj1 = getFieldValueByName(string, o1);
					Object obj2 = getFieldValueByName(string, o2);
					if (obj1 instanceof String) {
						String f1=((String)obj1)==null?"":((String)obj1);
						String f2=((String)obj2)==null?"":((String)obj2);
						if (order) {
							int compareTo = f1.compareTo(f2);
							return compareTo;
						}else{
							int compareTo = f2.compareTo(f1);
							return compareTo;
						}
						
					}
					if (obj1 instanceof Float) {
						Float f1=((Float)obj1)==null?0F:((Float)obj1);
						Float f2=((Float)obj2)==null?0F:((Float)obj2);
						if (order) {
							int compareTo = f1.compareTo(f2);
							return compareTo;
						}else{
							int compareTo = f2.compareTo(f1);
							return compareTo;
						}
					}
					if (obj1 instanceof Integer) {
						Integer f1=((Integer)obj1)==null?0:((Integer)obj1);
						Integer f2=((Integer)obj2)==null?0:((Integer)obj2);
						if (order) {
							return f1.compareTo(f2);
						}else{
							return f2.compareTo(f1);
						}
					}
					if (obj1 instanceof Long) {
						Long f1=((Long)obj1)==null?0l:((Long)obj1);
						Long f2=((Long)obj2)==null?0l:((Long)obj2);
						if (order) {
							int compareTo = f1.compareTo(f2);
							return compareTo;
						}else{
							int compareTo = f2.compareTo(f1);
							return compareTo;
						}
					}
					return 1;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	@SuppressWarnings("all")
	public static void enableSort(TableViewer tableViewer) {
		final List input = (List)tableViewer.getData("list");
		TableColumn[] columns = tableViewer.getTable().getColumns();
		for (TableColumn tableColumn : columns) {
			tableColumn.addSelectionListener(new SelectionListener() {
				boolean flag=false;
				@Override
				public void widgetSelected(SelectionEvent e) {
					tableViewer.getTable().clearAll();
					tableViewer.setInput(CarparkUtils.sortObjectPropety(input,"code",flag));
					flag = !flag;
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					
				}
			});
		}
		
		
		
	}
	/**
	 * 删除文件夹以及其中的文件
	 * @param dir
	 * @return
	 */
	public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                deleteDir(new File(dir, children[i]));
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }
	public static void setComboSelect(Combo combo, int i) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				combo.select(i);
			}
		});
	}
	public static boolean checkDaysIsOneDay(Date startTime, Date endTime) {
		int d1 = new DateTime(startTime).getDayOfYear();
		int d2 = new DateTime(endTime).getDayOfYear();
		return d1==d2;
	}
	public static Image getSwtImage(String name) {
		String file=System.getProperty("user.dir")+"\\img\\"+name;
		return SWTResourceManager.getImage(file);
	}
	public static String getUserName() {
		return System.getProperty("userName");
	}
	public static void setFocus(Composite combo) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				combo.setFocus();
			}
		});
	}
	public static boolean backupDateBase(String path,String ip,String port,String userName,String password) {
		String sql="backup database carpark to disk='"+path+"'";
		System.out.println(sql);
		boolean executeSQL = DatabaseUtil.executeSQL(ip, port, "master", userName, password, sql, "SQLSERVER 2008");
		return executeSQL;
	}
	public static int countDayByBetweenTime(Date start,Date end){
		Long l=end.getTime()-start.getTime();
		Long day=l/(1000*60*60*24);
		Long dayMore=l%(1000*60*60*24);
		if (dayMore>0) {
			day+=1;
		}else if(dayMore<0){
			day-=1;
		}
		return day.intValue();
	}
	
	public static Ini loadIniFromFile(File file) throws IOException {
        Ini ini = new Ini();
        Config global = Config.getGlobal();
        global.setGlobalSection(true);
        ini.setConfig(global);

        ini.setFile(file);
        ini.load();
        return ini;
    }
	public static String getCarStillTime(String totalTime) {
		//停车满1天语音播报天数
		String[] split = totalTime.split(":");
		Integer valueOf = Integer.valueOf(split[0].trim());
		Integer day=valueOf/24;
		Integer hour=valueOf%24;
		String string ="停车";
		if (day>0) {
			string += day+"天";
		}
		if (hour>0&&valueOf>0) {
			string += hour+"小时";
		}
		Integer minute = Integer.valueOf(split[1].trim());
		if (minute>0) {
			string+=minute+"分钟,";
		}
		if (string.length()==2) {
			string+="0分钟";
		}
		return string;
	}
	/**
	 * 移除逗号连接的字符串中的一段
	 * @param source
	 * @param move
	 * @return
	 */
	public static String removeString(String source, String move) {
		try {
			int flag=0;
			int beginIndex = source.indexOf(move)+move.length();
			if (beginIndex<source.length()) {
				flag++;
			}
			return source.substring(beginIndex+flag);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	/**
	 * 家码
	 * @param s
	 * @return
	 */
	public static String encod(String s) {
		try {
			String ss = Base64.getEncoder().encodeToString(s.getBytes("utf-8"));
			ss=StrUtil.StringXor(ss, defaultKey);
			String sss = Base64.getEncoder().encodeToString(ss.getBytes("utf-8"));
			return sss;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 解码
	 * @param s
	 * @return
	 */
	public static String decod(String s) {
		try {
			byte[] decode2 = Base64.getDecoder().decode(s);
			String ss = new String(decode2,"utf-8");
			ss=StrUtil.StringXor(ss, defaultKey);
			byte[] decode = Base64.getDecoder().decode(ss);
			String string = new String(decode,"utf-8");
			return string;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<String> splitString(String s,int size){
		List<String> list=new ArrayList<>();
		if (s.length()<=size) {
			list.add(s);
			return list;
		}
		
		for (int i = 0; i <= s.length()-size; i++) {
			String substring = s.substring(i, i+size);
			list.add(substring);
		}
		
		return list;
	}
	public static String getSettingValue(Map<SystemSettingTypeEnum, String> map, SystemSettingTypeEnum type) {
		
		return map.get(type)==null?type.getDefaultValue():map.get(type);
	}
	/**
	 * 清理重复进场记录
	 */
	public static void cleanSameInOutHistory() {
		try {
			LOGGER.info("准备清理数据库中的重复进出场记录");
			CarparkServerConfig cf = CarparkServerConfig.getInstance();
			String sql="update [carpark].[dbo].[SingleCarparkInOutHistory] set outTime=getDate() WHERE outTime is null and id not in(SELECT MAX(id) FROM [carpark].[dbo].[SingleCarparkInOutHistory] where outTime is null group by plateNo,carparkId)";
			boolean executeSQL = DatabaseUtil.executeSQL(cf.getDbServerIp(), cf.getDbServerPort(), CarparkServerConfig.CARPARK, 
					cf.getDbServerUsername(), cf.getDbServerPassword(), sql, DatabaseUtil.SQLSERVER2008);
			LOGGER.info("清理数据库中的重复进出场记录结果：{}",executeSQL);
		} catch (Exception e) {
			LOGGER.info("清理数据库中的重复进出场记录发生错误",e);
		}
	}
	/**
	 * 把分钟转成小时：分钟格式
	 * @param minute
	 * @return
	 */
	public static String getMinuteToTime(int minute){
		String s="";
		int h=minute/60;
		int m=minute%60;
		
		if (h>0) {
			if (h<10) {
				s+="0";
			}
			s+=h+":";
		}else{
			s+="00:";
		}
		if (m>0) {
			s+=m+"";
		}else{
			s+="00";
		}
		return s;
	}
	/**
	 * 检测通道类型
	 * @param device
	 * @return 
	 */
	public static boolean checkRoadType(SingleCarparkDevice device,CarparkMainModel model,CarparkMainPresenter presenter,DeviceRoadTypeEnum... types) {
		for (DeviceRoadTypeEnum deviceRoadTypeEnum : types) {
			if (device.getRoadType().equals(deviceRoadTypeEnum.name())) {
				presenter.showContentToDevice(device, model.getMapVoice().get(DeviceVoiceTypeEnum.valueOf(deviceRoadTypeEnum.name()+"语音")).getContent(), false);
				return true;
			}
		}
		return false;
	}
	/**
	 * 获取外网IP
	 * @return
	 */
	public static String getTCPIP(String tcpGetUrl) {
		InputStream ins = null;
		try {
			String spec = "http://1212.ip138.com/ic.asp";
			if (tcpGetUrl!=null) {
				spec=tcpGetUrl;
			}
			URL url = new URL(spec);
			URLConnection con = url.openConnection();
			ins = con.getInputStream();
			InputStreamReader isReader = new InputStreamReader(ins, "GB2312");
			BufferedReader bReader = new BufferedReader(isReader);
			StringBuffer webContent = new StringBuffer();
			String str = null;
			while ((str = bReader.readLine()) != null) {
				webContent.append(str);
			}
			int start = webContent.indexOf("[") + 1;
			int end = webContent.indexOf("]");
			return webContent.substring(start, end);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ins != null) {
				try {
					ins.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return "";
	}
	/**
	 * 格式化字符串，类似于log4j的格式化
	 * @param content
	 * @param o
	 * @return
	 */
	public static String formatString(String content, Object... o) {
		String message = MessageFormatter.arrayFormat(content, o).getMessage();
		return message;
	}
	public static int countSecondByDate(Date inTime, Date outTime) {
		Long s=(outTime.getTime()-inTime.getTime())/1000;
		return s.intValue();
	}
	/**
	 * 计算两个时间的时间差
	 * @param startTime
	 * @param endTime
	 * @param timeUnit
	 * @return
	 */
	public static int countTime(Date startTime, Date endTime, TimeUnit... timeUnit) {
		Long millis=endTime.getTime()-startTime.getTime();
		Long second;
		Long minute;
		Long hour;
		Long day;
		if (timeUnit==null||timeUnit[0].equals(TimeUnit.MILLISECONDS)) {
			return millis.intValue();
		}
		if (timeUnit[0].equals(TimeUnit.SECONDS)) {
			second=millis/1000;
			return second.intValue();
		}
		if (timeUnit[0].equals(TimeUnit.MINUTES)) {
			minute=millis/1000/60;
			return minute.intValue();
		}
		if (timeUnit[0].equals(TimeUnit.HOURS)) {
			hour=millis/1000/60/60;
			return hour.intValue();
		}
		if (timeUnit[0].equals(TimeUnit.DAYS)) {
			day=millis/1000/60/60/24;
			return day.intValue();
		}
		return 0;
	}
	/**
	 *检查服务器是否是在本地
	 * @return
	 */
	public static boolean checkServerIsLocal() {
		String dbServerIp = CarparkClientConfig.getInstance().getDbServerIp();
		return dbServerIp.equals("localhost") || dbServerIp.equals("127.0.0.1") || dbServerIp.equals(StrUtil.getHostIp());
	}
	public static Date getDate(int year, int month, int date, int hours, int minutes, int seconds) {
		Calendar c = Calendar.getInstance();
		c.set(year, month, date, hours, minutes, seconds);
		return c.getTime();
	}
	public static String getLoginUserName() {
		return System.getProperty(ConstUtil.USER_NAME);
	}
	public static boolean ping(String ip){
		long currentTimeMillis = System.currentTimeMillis();
		try {
			Process exec = Runtime.getRuntime().exec("ping "+ip);
			InputStream inputStream = exec.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "GBK"));
			String readLine = br.readLine();
			int i=1;
			while (readLine!=null&&System.currentTimeMillis()-currentTimeMillis<3000) {
				if (i==3) {
					break;
				}
				readLine = br.readLine();
				i++;
			}
			exec.destroy();
			return readLine.indexOf("TTL")>-1;
		} catch (Exception e) {
		}
		return false;
	}
	/**
	 * 根据时间 车牌生成大小图片存放路径
	 * @param date
	 * @param plateNO
	 * @param isBig
	 * @return
	 */
	public static String FormatImagePath(Date date, String plateNO,boolean isBig) {
		String imageSavefolder = StrUtil.formatDate(date, "yyyy/MM/dd/HH");
		String fileName = StrUtil.formatDate(date, "yyyyMMddHHmmssSSS");
		if (isBig) {
			String bigImgFileName = fileName + "_" + plateNO + "_big.jpg";
			bigImgFileName=imageSavefolder + "/" + bigImgFileName;
			return bigImgFileName;
		}else{
    		String smallImgFileName = fileName + "_" + plateNO + "_small.jpg";
    		smallImgFileName = imageSavefolder + "/" + smallImgFileName;
    		return smallImgFileName;
		}
	}

	public static Server startServer(int port, String path, HttpServlet servlet) {
		try {
			Server server = new Server(port);
			ServletHandler handler = new ServletHandler();
			ServletHolder holer = new ServletHolder(Holder.Source.EMBEDDED);
			holer.setAsyncSupported(false);
			holer.setServlet(servlet);
			handler.addServletWithMapping(holer, path);
			server.setHandler(handler);
			server.start();
			return server;
		} catch (Exception e) {
			LOGGER.error("启动服务是发生错误");
			return null;
		}
	}
	
	/**
	 * 分割车牌替换字符为‘_’ 进行车牌模糊匹配
	 * @param plateNO
	 * @param likeSize
	 */
	public static Set<String> splitPlateWithIgnoreSize(String plateNO, int likeSize) {
		char[] charArray = plateNO.toCharArray();
		Set<String> setS=new HashSet<>();
		
		int length = plateNO.length();
		for (int i = length-likeSize; i < length; i++) {
			for (int j = 0; j < length; j++) {
				char[] cs=new char[length];
				System.arraycopy(charArray, 0, cs, 0, length);
				cs[j]='_';
				String string = new String(cs);
				setS.add(string);
			}
		}
		return setS;
	}
	/**
	 * 返回相似的一个字符串
	 * @param plateNO
	 * @param strings
	 * @return
	 */
	public static String checkAlikeString(String plateNO, String[] strings) {
		if (strings==null) {
			return null;
		}
		if (strings.length==1) {
			return strings[0];
		}
		String s=plateNO;
		int size=0;
		for (int i = 0; i < strings.length; i++) {
			String string = strings[i];
			int like=checkAlikeSize(plateNO,string);
			
			if (like>size) {
				size=like;
				s=string;
			}
		}
		return s;
	}
	/**
	 * 返回两个字符的相似度
	 * @param plateNO
	 * @param string
	 * @return
	 */
	public static int checkAlikeSize(String plateNO, String string) {
		int size=0;
		char[] c1 = plateNO.toCharArray();
		char[] c2 = string.toCharArray();
		for (int i = 0; i < Math.min(c2.length, c1.length); i++) {
			char c = c1[i];
			char cc = c2[i];
			if (c==cc) {
				size++;
			}
		}
		return size;
	}
	
	public static Date getHourBottomTime(Date d){
		 if (d == null) return null;
	        Calendar instance = Calendar.getInstance();
	        instance.setTime(d);
	        instance.set(Calendar.MINUTE, 59);
	        instance.set(Calendar.SECOND, 59);
	        instance.set(Calendar.MILLISECOND, 999);
	        
	        return instance.getTime();
	}
	public static int checkNotAlikeSize(String source, String s) {
		int size=0;
		char[] c1 = source.toCharArray();
		char[] c2 = s.toCharArray();
		for (int i = 0; i < Math.max(c2.length, c1.length); i++) {
			if (c1.length<i+1||c2.length<i+1) {
				i++;
				continue;
			}
			char c = c1[i];
			char cc = c2[i];
			if (c!=cc) {
				size++;
			}
		}
		return size;
	}
	
}
