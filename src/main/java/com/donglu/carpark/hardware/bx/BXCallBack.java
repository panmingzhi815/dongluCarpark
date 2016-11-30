package com.donglu.carpark.hardware.bx;

import com.sun.jna.win32.StdCallLibrary.StdCallCallback;

public interface BXCallBack extends StdCallCallback {
	public void invoke(String szMessagge, Integer nProgress);
}