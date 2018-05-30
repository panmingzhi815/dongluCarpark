package com.donglu.carpark.ui.view.message;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.ui.view.message.MessageBoxUI.MessageBoxBtnCallback;

public class MessageUtil {
	private static final Logger LOGGER=LoggerFactory.getLogger(MessageUtil.class);
	private static Map<String, MessageBoxUI> mapTitle2Ui=new HashMap<>();
	static{
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				for (MessageBoxUI messageBoxUI : mapTitle2Ui.values()) {
					messageBoxUI.close();
				}
			}
		}));
	}
	
	public static void info(String msg){
		info("提示", msg, 600000, null);
	}
	public static void info(String msg,int stayTime){
		info("提示", msg, stayTime, null);
	}
	public static void info(String title,String msg){
		info(title, msg, 0, null);
	}
	public static void info(String title,String msg,int stayTime){
		info(title, msg, stayTime, null);
	}
	public static void info(String title,String msg,int stayTime,Point location){
		info(title, msg, null, stayTime, location, null);
	}
	public static void info(String title,String msg,String[] btns,int stayTime,Point location,MessageBoxBtnCallback callback){
		LOGGER.info("消息提示：{}:{}",title,msg);
		close(title);
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				MessageBoxUI ui = new MessageBoxUI(title, msg, btns, stayTime,false,location,callback);
				mapTitle2Ui.put(title, ui);
				ui.open();
			}
		});
	}
	public static void info(String title, String msg, String[] btns, int stayTime,MessageBoxBtnCallback callback) {
		info(title, msg,btns, stayTime,null, callback);
	}
	public static void close(String title){
		MessageBoxUI ui = mapTitle2Ui.get(title);
		if (ui!=null) {
			ui.close();
		}
	}
}
