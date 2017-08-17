package com.donglu.carpark.util;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.W32APIOptions;

public interface MyKernel32 extends WinNT {

    /** The instance. */
    MyKernel32 INSTANCE = (MyKernel32) Native.loadLibrary("kernel32", MyKernel32.class, W32APIOptions.DEFAULT_OPTIONS);
    
    boolean SetLocalTime(SYSTEMTIME lpSystemTime);
}