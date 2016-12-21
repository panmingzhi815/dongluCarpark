package com.donglu.carpark.ui;

import java.util.Random;

import com.donglu.carpark.ui.servlet.CardRecordServlet;
import com.donglu.carpark.util.CarparkUtils;

public class ServerTest {
	public static void main(String[] args) {
//		CarparkUtils.startServer(10004, "/*", new CardRecordServlet(null));
		
		for (int i = 0; i < 10; i++) {
			System.out.println(new Random().nextInt(3));
		}
	}
}
