package com.donglu.carpark.util;

import java.net.NetworkInterface;
import java.util.Enumeration;

public class NetUtils {
	
	public static String getMacAddress(){
		try {  
            Enumeration<NetworkInterface> el = NetworkInterface.getNetworkInterfaces();  
            StringBuilder builder = new StringBuilder();  
            while (el.hasMoreElements()) {  
                NetworkInterface nextElement = el.nextElement();
//                System.out.println(nextElement.getName());
				byte[] mac = nextElement.getHardwareAddress();  
                if (mac == null||mac.length!=6){  
                   continue;  
                }  
                if(builder.length() > 0){  
                    builder.append(",");  
                }  
                for (byte b : mac) {  
                   //convert to hex string.  
                   String hex = Integer.toHexString(0xff & b);  
                   if(hex.length() == 1){  
                       hex  = "0" + hex;  
                   }  
                   builder.append(hex);  
//                   builder.append("-");  
                }  
//                builder.deleteCharAt(builder.length() - 1);  
           }  
             
           if(builder.length() == 0){  
               System.out.println("Sorry, can't find your MAC Address.");  
           }else{  
              return  builder.toString();
           }  
       }catch (Exception exception) {  
           exception.printStackTrace();  
       }  
		return null;
	}
}
