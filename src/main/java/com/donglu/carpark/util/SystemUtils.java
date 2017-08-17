package com.donglu.carpark.util;

import java.util.Calendar;
import java.util.Date;


import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase.SYSTEMTIME;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.platform.win32.WinNT.LUID;
import com.sun.jna.platform.win32.WinNT.LUID_AND_ATTRIBUTES;
import com.sun.jna.platform.win32.WinNT.TOKEN_PRIVILEGES;

public class SystemUtils {
	/**
	 * 获取权限
	 * 	bool System::GetPrivileges()
        {
            // 取得当前进程的[Token](标识)句柄
            HANDLE hToken;
            if (!OpenProcessToken(GetCurrentProcess(), TOKEN_ADJUST_PRIVILEGES | TOKEN_QUERY, &hToken))
                return false;
         
            // 确保能关闭句柄
            qp::Handle handle(hToken);
         
            // 取得关闭系统的[LUID](本地唯一的标识符)值
            TOKEN_PRIVILEGES stTokenPrivilege;
            if (!LookupPrivilegeValue(NULL, SE_SHUTDOWN_NAME, &stTokenPrivilege.Privileges[0].Luid))
                return false;
         
            // 设置特权数组的元素个数
            stTokenPrivilege.PrivilegeCount = 1;
         
            // 设置[LUID]的属性值
            stTokenPrivilege.Privileges[0].Attributes = SE_PRIVILEGE_ENABLED;
         
            // 为当前进程取得DEBUG权限
            if (!AdjustTokenPrivileges(hToken, FALSE, &stTokenPrivilege, 0, NULL, NULL))
                return false;
         
            return true;
        }
	 * @return
	 */
	public static boolean getPrivileges(){
		Advapi32 advapi32 = Advapi32.INSTANCE;
		Kernel32 kernel32=Kernel32.INSTANCE;
		HANDLEByReference TokenHandle=new HANDLEByReference();
		int DesiredAccess=WinNT.TOKEN_ADJUST_PRIVILEGES | WinNT.TOKEN_QUERY;
		boolean openProcessToken = advapi32.OpenProcessToken(kernel32.GetCurrentProcess(), DesiredAccess, TokenHandle);
		System.out.println("openProcessToken======"+openProcessToken);
		if (!openProcessToken) {
			return false;
		}
		//SE_SYSTEMTIME_NAME
		TOKEN_PRIVILEGES stTokenPrivilege=new TOKEN_PRIVILEGES(1);
		stTokenPrivilege.Privileges[0]=new LUID_AND_ATTRIBUTES(new LUID(),new DWORD());
		boolean lookupPrivilegeValue = advapi32.LookupPrivilegeValue(null, WinNT.SE_SYSTEMTIME_NAME, stTokenPrivilege.Privileges[0].Luid);
		System.out.println("lookupPrivilegeValue=========="+lookupPrivilegeValue);
		if (!lookupPrivilegeValue) {
			return false;
		}
		 stTokenPrivilege.PrivilegeCount = new DWORD(1);
         
         // 设置[LUID]的属性值
         stTokenPrivilege.Privileges[0].Attributes = new DWORD(WinNT.SE_PRIVILEGE_ENABLED);
		boolean adjustTokenPrivileges = advapi32.AdjustTokenPrivileges(TokenHandle.getValue(), false, stTokenPrivilege, 0, null, null);
		System.out.println("adjustTokenPrivileges===="+adjustTokenPrivileges);
		if (!adjustTokenPrivileges) {
			return false;
		}
		return true;
	}
	
	public static boolean setLocalTime(Date time){
		SYSTEMTIME lpSystemTime=new SYSTEMTIME();
		Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time.getTime());
        lpSystemTime.wYear = (short) cal.get(Calendar.YEAR);
        lpSystemTime.wMonth = (short) (1 + cal.get(Calendar.MONTH) - Calendar.JANUARY);  // 1 = January
        lpSystemTime.wDay = (short) cal.get(Calendar.DAY_OF_MONTH);
        lpSystemTime.wHour = (short) cal.get(Calendar.HOUR_OF_DAY);
        lpSystemTime.wMinute = (short) cal.get(Calendar.MINUTE);
        lpSystemTime.wSecond = (short) cal.get(Calendar.SECOND);
        lpSystemTime.wMilliseconds = (short) cal.get(Calendar.MILLISECOND);
        lpSystemTime.wDayOfWeek = (short) (cal.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY); // 0 = Sunday
		
		return MyKernel32.INSTANCE.SetLocalTime(lpSystemTime);
	}
	public static void main(String[] args) {
	}
}
