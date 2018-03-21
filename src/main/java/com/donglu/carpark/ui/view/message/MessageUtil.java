package com.donglu.carpark.ui.view.message;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

public class MessageUtil {
	private static List<MessageBoxUI> listUi=new ArrayList<>();
	static{
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				for (MessageBoxUI messageBoxUI : listUi) {
					messageBoxUI.close();
				}
			}
		}));
	}
	
	public static void info(String msg){
		info("提示", msg, 0, null);
	}
	public static void info(String title,String msg){
		info(title, msg, 0, null);
	}
	public static void info(String title,String msg,int stayTime){
		info(title, msg, stayTime, null);
	}
	public static void info(String title,String msg,int stayTime,Point location){
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				MessageBoxUI ui = new MessageBoxUI(title, msg, null, stayTime,false,location);
				listUi.add(ui);
				ui.open();
			}
		});
	}
}
