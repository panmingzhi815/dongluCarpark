package com.donglu.carpark.ui;

import com.donglu.carpark.ui.servlet.CardRecordServlet;
import com.donglu.carpark.util.CarparkUtils;

public class ServerTest {
	public static void main(String[] args) {
		CarparkUtils.startServer(10004, "/*", new CardRecordServlet(null));
	}
}
