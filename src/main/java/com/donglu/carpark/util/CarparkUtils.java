package com.donglu.carpark.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
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
import com.donglu.carpark.server.imgserver.FileuploadSend;
import com.donglu.carpark.ui.CarparkClientConfig;
import com.donglu.carpark.ui.CarparkMainPresenter;
import com.donglu.carpark.ui.CarparkManageApp;
import com.donglu.carpark.ui.common.ImageDialog;
import com.dongluhitec.card.domain.db.singlecarpark.DeviceRoadTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.DeviceVoiceTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.util.DatabaseUtil;
import com.google.common.io.Files;

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
	 * 获取保存到CLabel的图片
	 * @param smallImage
	 * @param cl
	 * @param shell
	 * @return
	 */
	public static Image getImage(final byte[] smallImage, CLabel cl, Shell shell) {
		if (smallImage == null || smallImage.length <= 0) {
			cl.setText("无图片");
			return null;
		}

		ByteArrayInputStream stream = null;
		try {
			stream = new ByteArrayInputStream(smallImage);
			Image img = new Image(shell.getDisplay(), stream);
			Rectangle rectangle = cl.getBounds();
			ImageData data = img.getImageData().scaledTo(rectangle.width, rectangle.height);
			ImageDescriptor createFromImageData = ImageDescriptor.createFromImageData(data);
			Image createImg = createFromImageData.createImage();
			img.dispose();
			img = null;
			cl.setText("");
			return createImg;
		} catch (Exception e) {
			LOGGER.error("图片转换错误",e);
			return null;
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 将图片数据直接显示至cLabel背景
	 * 每次显示图片后，将图片保存至clabel的引用中，下次再要显示时，先判断是否有引用，如果有引用，则先要销毁以前的引用，避免swt 的 handler 资源泄漏
	 * @param imageBytes 数据原始二进制数据
	 * @param cLabel 显示控件
	 * @param device 当前显示的窗体 display
	 */
	static Map<Control, DisposeListener> mapControlToDisposeListener=new HashMap<>();
	public static void setBackgroundImage(byte[] imageBytes, Control cLabel, Display device) {
		if (cLabel.getData("lastImage") != null){
			Image lastImage = (Image)cLabel.getData("lastImage");
			lastImage.dispose();
			cLabel.setBackgroundImage(null);
			cLabel.setData("lastImage",null);
			LOGGER.info("销毁图片成功！");
		}
		if (mapControlToDisposeListener.get(cLabel)==null) {
			DisposeListener listener = new DisposeListener() {
				@Override
				public void widgetDisposed(DisposeEvent e) {
					if (cLabel.getData("lastImage") != null){
						Image lastImage = (Image)cLabel.getData("lastImage");
						lastImage.dispose();
						cLabel.setBackgroundImage(null);
						cLabel.setData("lastImage",null);
						LOGGER.info("销毁图片成功！");
						cLabel.removeDisposeListener(this);
						mapControlToDisposeListener.remove(cLabel);
					}
				}
			};
			cLabel.addDisposeListener(listener);
			mapControlToDisposeListener.put(cLabel, listener);
		}
		if (imageBytes == null || imageBytes.length <= 0) {
			if (cLabel instanceof CLabel) {
				((CLabel)cLabel).setText("无图片");
			}
			return;
		}

		try (ByteArrayInputStream stream = new ByteArrayInputStream(imageBytes)) {
			Image img = new Image(device, stream);
			int width = cLabel.getBounds().width;
			int height = cLabel.getBounds().height;
			if (width<=0||height<=0) {
				return;
			}
			ImageData data = img.getImageData().scaledTo(width, height);
			Image createImg = ImageDescriptor.createFromImageData(data).createImage();
			img.dispose();
			if (cLabel instanceof CLabel) {
				((CLabel)cLabel).setText("");
			}
			cLabel.setBackgroundImage(createImg);
			cLabel.setData("lastImage",createImg);
		} catch (Exception e) {
			LOGGER.error("图片转换错误", e);
		}
	}
	
	/**
	 * 将图片数据直接显示至Label背景
	 * 每次显示图片后，将图片保存至label的引用中，下次再要显示时，先判断是否有引用，如果有引用，则先要销毁以前的引用，避免swt 的 handler 资源泄漏
	 * @param imageBytes 数据原始二进制数据
	 * @param Label 显示控件
	 * @param device 当前显示的窗体 display
	 */
	public static void setBackgroundImage(byte[] imageBytes, Label cLabel, Display device) {
		if (cLabel.getData("lastImage") != null){
			Image lastImage = (Image)cLabel.getData("lastImage");
			lastImage.dispose();
			cLabel.setBackgroundImage(null);
			cLabel.setData("lastImage",null);
			LOGGER.info("销毁图片成功！");
		}

		if (imageBytes == null || imageBytes.length <= 0) {
			cLabel.setText("无图片");
			return;
		}

		try (ByteArrayInputStream stream = new ByteArrayInputStream(imageBytes)) {
			Image img = new Image(device, stream);
			int width = cLabel.getBounds().width;
			int height = cLabel.getBounds().height;
			if (width<=0||height<=0) {
				return;
			}
			ImageData data = img.getImageData().scaledTo(width, height);
			Image createImg = ImageDescriptor.createFromImageData(data).createImage();
			img.dispose();
			cLabel.setText("");
			cLabel.setBackgroundImage(createImg);
			cLabel.setData("lastImage",createImg);
		} catch (Exception e) {
			LOGGER.error("图片转换错误", e);
		}
	}
	
	
	/**
	 * 获得保存到Label的图片
	 * @param image
	 * @param lbl
	 * @param shell
	 * @return
	 */
	public static Image getImage(byte[] image, Label lbl,Shell shell) {
		if (image==null||image.length<=0) {
			return null;
		}
		ByteArrayInputStream stream = null;
		try {
			stream = new ByteArrayInputStream(image);
			Image newImg = new Image(shell.getDisplay(), stream);
			Rectangle rectangle = lbl.getBounds();
			System.out.println(rectangle);
			if (rectangle.width==0) {
				return newImg;
			}
			ImageData data = newImg.getImageData().scaledTo(rectangle.width, rectangle.height);
			ImageDescriptor createFromImageData = ImageDescriptor.createFromImageData(data);
			Image createImg = createFromImageData.createImage();
			newImg.dispose();
			newImg = null;
			return createImg;
		} catch (Exception e) {
			LOGGER.error("获取图片失败",e);
			return null;
		}finally{
			if (stream!=null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 获取图片字节
	 * @param filePath
	 * @param img
	 * @return
	 */
	public static byte[] getImageByte(String img) {
		
		if (StrUtil.isEmpty(img)) {
			return null;
		}
		String filePath=(String) CarparkFileUtils.readObject(CarparkManageApp.CLIENT_IMAGE_SAVE_FILE_PATH);
		try {
			byte[] image;
			String pathname = (filePath==null?"":filePath)+"/img/"+img;
			File file=new File(pathname);
			LOGGER.info("获取图片{}",pathname);
			if (file.exists()) {
				LOGGER.info("在本地找到图片，获取图片{}",pathname);
				image=Files.toByteArray(file);
			}else{
				String substring = img.substring(img.lastIndexOf("/")+1);
				String actionUrl = "http://"+CarparkClientConfig.getInstance().getDbServerIp()+":8899/carparkImage/";
				LOGGER.info("本地未找到图片，准备发送请求{}获取图片{}",actionUrl,pathname);
				image = FileuploadSend.download(actionUrl, substring);
				LOGGER.info("从{}获取图片成功",actionUrl);
			}
			return image;
		} catch (IOException e) {
			LOGGER.info("根据图片名称获得图片失败");
			return null;
		}

	}
	public static byte[] getImageBytes(Image image){
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			ImageLoader imageLoader = new ImageLoader();
			imageLoader.data = new ImageData[] { image.getImageData() };
			imageLoader.save(baos, image.type);
			
			byte[] imageByteArray = baos.toByteArray();
			try {
				baos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return imageByteArray;
		} catch (Exception e) {
			LOGGER.info("更据图片获取图片字节失败");
			return null;
		}
	}
	/**
	 * 根据属性名获取属性值
	 * */
    public static Object getFieldValueByName(String fieldName, Object o) {
        try {  
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
		String ss=s.replace(".0", "");
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
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
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
			String sql="DELETE FROM [carpark].[dbo].[SingleCarparkInOutHistory] WHERE outTime is null and id not in(SELECT MAX(id) FROM [carpark].[dbo].[SingleCarparkInOutHistory] where outTime is null group by plateNo,carparkId)";
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
	 * 设置图片名称
	 * @param lbl_outSmallImg
	 * @param string
	 */
	public static void setBackgroundImageName(CLabel lbl_outSmallImg, String string) {
		lbl_outSmallImg.setData("imgName", string);
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
	static final Map<Control, MouseAdapter> mapControlToMouseAdapter=new HashMap<>();
	public static void setBackgroundImage(byte[] bigImage, CLabel label, String imageName) {
		label.getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				label.setData("imgName", imageName);
				if (mapControlToMouseAdapter.get(label)==null) {
					MouseAdapter listener = new MouseAdapter() {
						@Override
						public void mouseDoubleClick(MouseEvent e) {
							String lastImage = (String)label.getData("imgName");
							if (StrUtil.isEmpty(lastImage)) {
								return;
							}
							ImageDialog imageDialog = new ImageDialog(lastImage);
							imageDialog.open();
						}
					};
					mapControlToMouseAdapter.put(label, listener);
					label.addMouseListener(listener);
				}
				setBackgroundImage(bigImage, label, label.getDisplay());
			}
		});
	}
	public static void setBackgroundImage(byte[] bigImage, CLabel label, String imageName, boolean isClose) {
		label.getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				MouseAdapter listener=null;
				if (isClose) {
					listener = new MouseAdapter() {
						@Override
						public void mouseDoubleClick(MouseEvent e) {
							label.getShell().dispose();
						}
					};
					label.addDisposeListener(new DisposeListener() {
						
						@Override
						public void widgetDisposed(DisposeEvent e) {
							mapControlToMouseAdapter.remove(label);
						}
					});
				} else {
					listener = new MouseAdapter() {
						@Override
						public void mouseDoubleClick(MouseEvent e) {
							String lastImage = (String) label.getData("imgName");
							if (StrUtil.isEmpty(lastImage)) {
								return;
							}
							ImageDialog imageDialog = new ImageDialog(lastImage);
							imageDialog.open();
						}
					};
				}
				if (mapControlToMouseAdapter.get(label)==null) {
					label.setData("imgName", imageName);
					mapControlToMouseAdapter.put(label, listener);
					label.addMouseListener(listener);
				}
				setBackgroundImage(bigImage, label, label.getDisplay());
			}
		});
	}
}
