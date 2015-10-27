package com.donglu.carpark;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;

public class AppRunTimeInfo {
	private SingleCarparkSystemUser systemUser;

	public SingleCarparkSystemUser getSystemUser() {
		return systemUser;
	}

	public void setSystemUser(SingleCarparkSystemUser systemUser) {
		this.systemUser = systemUser;
	}
	
	public static void main(String[] args) {
		System.out.println(dateDiff("20151022200000", "20151023000000", "yyyyMMddHHmmss", ""));
	}
	public static Long dateDiff(String startTime, String endTime,  String format, String str) { 
        // 按照传入的格式生成一个simpledateformate对象  
        SimpleDateFormat sd = new SimpleDateFormat(format);  
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数  
        long nh = 1000 * 60 * 60;// 一小时的毫秒数  
        long nm = 1000 * 60;// 一分钟的毫秒数  
        long ns = 1000;// 一秒钟的毫秒数  
        long diff;  
        long day = 0;  
        long hour = 0;  
        long min = 0;  
        long sec = 0;  
        // 获得两个时间的毫秒时间差异  
        try {  
            diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();  
            day = diff / nd;// 计算差多少天  
            hour = diff % nd / nh + day * 24;// 计算差多少小时  
            min = diff % nd % nh / nm + day * 24 * 60;// 计算差多少分钟  
            sec = diff % nd % nh % nm / ns;// 计算差多少秒  
            // 输出结果  
            System.out.println("时间相差：" + day + "天" + (hour - day * 24) + "小时" 
                    + (min - day * 24 * 60) + "分钟" + sec + "秒。");  
            System.out.println("hour=" + hour + ",min=" + min);  
            System.out.println(diff/nm);
            if (str.equalsIgnoreCase("h")) {  
                return hour;  
            } else {  
                return min;  
            }  
            
        } catch (ParseException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        if (str.equalsIgnoreCase("h")) {  
            return hour;  
        } else {  
            return min;  
        }  
}

}
