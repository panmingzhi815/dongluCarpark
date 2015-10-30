package com.donglu.carpark.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.joda.time.DateTime;

import com.donglu.carpark.server.imgserver.FileuploadSend;
import com.donglu.carpark.ui.CarparkClientConfig;
import com.dongluhitec.card.domain.exception.DongluAppException;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.common.io.Files;

public class CarparkUtils {
	
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
			throw new DongluAppException("图片转换错误", e);
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
			e.printStackTrace();
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
	public static byte[] getImageByte(String filePath,String img) {
		if (StrUtil.isEmpty(img)) {
			return null;
		}
		try {
			byte[] image;
			File file=new File(filePath+"/img/"+img);
			if (file.exists()) {
				image=Files.toByteArray(file);
			}else{
				String substring = img.substring(img.lastIndexOf("/")+1);
				String actionUrl = "http://"+CarparkClientConfig.getInstance().getDbServerIp()+":8899/carparkImage";
				image = FileuploadSend.download(actionUrl, substring);
			}
			return image;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
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
    		System.out.println(fields[i].getType());
    		fieldNames[i]=fields[i].getName();
    	}
    	return fieldNames;
    }
    
    /**
     * 获取属性类型(type)，属性名(name)，属性值(value)的map组成的list
     * */
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
		System.out.println(splitPlateNO("粤BD021W"));
	}
}
