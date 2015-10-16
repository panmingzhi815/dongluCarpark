package com.donglu.carpark.server.imgserver;

import java.util.HashMap;
import java.util.Map;

public class Call {

    public static void main(String[] args) throws Exception {
        
//        /* Post Request */
//        Map dataMap = new HashMap();
//        dataMap.put("username", "Nick Huang");
//        dataMap.put("blog", "IT");
//        System.out.println(new HttpRequestor().doPost("http://localhost:8080/OneHttpServer/", dataMap));
        
        /* Get Request */
        System.out.println(new HttpRequestor().doGet("http://localhost:8889/OneHttpServer/"));
    }

}
