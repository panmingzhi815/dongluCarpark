package com.donglu.carpark.server.servlet;

import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.search.exception.impl.LogErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dongluhitec.card.blservice.DongluServiceException;
import com.google.common.base.Strings;

public class ImageServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageServlet.class);

    private static final long serialVersionUID = 884523916637749569L;

    private String root;

    public void setFolder(String folderName) {
        root = System.getProperty("user.dir") + File.separator + folderName + File.separator;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	req.setCharacterEncoding("utf-8");
    	resp.setCharacterEncoding("utf-8");
    	
        String id = req.getParameter("id");
        System.out.println("========"+id);
        String filePathFromId = parseFilePathFromId(id,root);

        byte[] bytes = getBytes(filePathFromId);

        if (bytes == null) {
            return;
        }

        resp.setContentType("image/jpeg");

        try(ServletOutputStream outputStream = resp.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(outputStream)){
            bos.write(bytes);
            bos.flush();
        }finally {

        }
    }
    
    public static String parseFilePathFromId(String id,String root){
        if (Strings.isNullOrEmpty(id)) {
        	throw new DongluServiceException("id不能空");
        }
        if (id.endsWith(".jpeg") == false) {
            id += ".jpeg";
        }
        String year = id.substring(0, 4);
        String month = id.substring(4, 6);
        String date = id.substring(6, 8);
        String hour = id.substring(8, 10);
        String min = id.substring(10, 12);
        
        String filePath = new StringBuilder(root)
        .append(year).append(File.separator)
        .append(month).append(File.separator)
        .append(date).append(File.separator)
        .append(hour).append(File.separator)
        .append(min).append(File.separator)
        .append(id).toString();
        return filePath;
    }

    public static byte[] getBytes(String filePath) {
    	File file = new File(filePath);
    	if(!file.exists()){
    		throw new DongluServiceException("未找到指定路径下的照片:"+filePath);
    	}
        try(FileInputStream fis = new FileInputStream(filePath);ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];  
            int len = 0;  
            while( (len=fis.read(buffer)) != -1 ){  
                outStream.write(buffer, 0, len);  
            }  
            return outStream.toByteArray();  
        } catch (Exception e) {
        	LOGGER.error("获取照片信息失败!", e);
        	throw new DongluServiceException("获取照片信息失败:"+filePath,e);
        }
    }

}
