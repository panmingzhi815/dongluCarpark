package com.donglu.carpark.ui.view.message;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

public class MessageUtil {
	public static void info(String msg){
		info("提示", msg, 0, null);
	}
	public static void info(String msg,int stayTime){
		info("提示", msg, stayTime, null);
	}
	public static void info(String title,String msg){
		info(title, msg, 0, null);
	}
	public static void info(String title,String msg,int stayTime,Point location){
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				MessageBoxUI ui = new MessageBoxUI(title, msg, null, stayTime,false,location);
				ui.open();
			}
		});
	}
}
