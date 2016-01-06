package com.donglu.carpark.server.servlet;

import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.donglu.carpark.server.CarparkServerConfig;
import com.dongluhitec.card.domain.util.StrUtil;

public class ServerServlet extends HttpServlet {

    private static final long serialVersionUID = 884523916637749569L;


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
    	try {
    		req.setCharacterEncoding("UTF-8");
    		response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=UTF-8");
			PrintWriter out = response.getWriter();
			CarparkServerConfig instance = CarparkServerConfig.getInstance();
			String s = StrUtil.getHostIp()+"/"+instance.getDbServerPort()+"/"+instance.getDbServerUsername()+"/"+instance.getDbServerPassword()+"/"+instance.getDbServerType();
			System.out.println(s);
			out.print(s);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
