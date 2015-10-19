package com.donglu.carpark.server.servlet;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.hibernate.search.exception.impl.LogErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.server.CarparkServerConfig;
import com.donglu.carpark.server.imgserver.ImageServerUI;
import com.dongluhitec.card.blservice.DongluServiceException;
import com.dongluhitec.card.ui.util.FileUtils;
import com.google.common.base.Strings;

public class ServerServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerServlet.class);

    private static final long serialVersionUID = 884523916637749569L;


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
    	try {
    		req.setCharacterEncoding("UTF-8");
    		response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=UTF-8");
			PrintWriter out = response.getWriter();
			CarparkServerConfig instance = CarparkServerConfig.getInstance();
			String s = instance.getDbServerIp()+"/"+instance.getDbServerPort()+"/"+instance.getDbServerUsername()+"/"+instance.getDbServerPassword();
			System.out.println(s);
			out.print(s);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
