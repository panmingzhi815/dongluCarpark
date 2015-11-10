package com.donglu.carpark.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.wb.swt.SWTResourceManager;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.info.CarparkChargeInfo;
import com.donglu.carpark.server.imgserver.FileuploadSend;
import com.donglu.carpark.server.servlet.ImageUploadServlet;
import com.donglu.carpark.ui.CarparkClientConfig;
import com.donglu.carpark.ui.CarparkManageApp;
import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.exception.DongluAppException;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.ui.util.FileUtils;
import com.google.common.io.Files;

public class CarparkUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(CarparkUtils.class);
	
	public static final String PLATENO_REGEX="^[\u4e00-\u9fa5][A-Za-z0-9]{6}$";
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
	public static byte[] getImageByte(String img) {
		
		if (StrUtil.isEmpty(img)) {
			return null;
		}
		String filePath=(String) FileUtils.readObject(CarparkManageApp.CLIENT_IMAGE_SAVE_FILE_PATH);
		try {
			byte[] image;
			String pathname = filePath+"/img/"+img;
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
	/**
	 * 0.0返回0
	 * @param s
	 * @return
	 */
	public static String formatFloatString(String s) {
		String substring = s.substring(s.indexOf(".") + 1, s.indexOf(".") + 2);
		Integer intValueOf = Integer.valueOf(substring);
		if (intValueOf == 0) {
			String ss = s.replace("." + intValueOf, "");
			System.out.println(ss);
			return ss;
		}
		return s;
	}
	public static <T> List<T> sortObjectPropety(List<T> list, String string,boolean order) {
		
		try {
			Collections.sort(list, new Comparator<T>() {
				
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
}
