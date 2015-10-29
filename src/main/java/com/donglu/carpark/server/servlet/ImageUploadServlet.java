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

import com.donglu.carpark.server.imgserver.ImageServerUI;
import com.dongluhitec.card.blservice.DongluServiceException;
import com.dongluhitec.card.ui.util.FileUtils;
import com.google.common.base.Strings;

public class ImageUploadServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageUploadServlet.class);

    private static final long serialVersionUID = 884523916637749569L;

    private String root;

    public void setFolder(String folderName) {
        root = System.getProperty("user.dir") + File.separator + folderName + File.separator;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	req.setCharacterEncoding("UTF-8");
    	resp.setCharacterEncoding("UTF-8");
    	
        String id = req.getParameter("id");
        Object o=FileUtils.readObject(ImageServerUI.IMAGE_SAVE_DIRECTORY)==null?System.getProperty("user.dir"):FileUtils.readObject(ImageServerUI.IMAGE_SAVE_DIRECTORY);
        System.out.println(o+"========"+id);
        String filePathFromId = parseFilePathFromId(id,o+"\\img\\");
        System.out.println(filePathFromId);

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
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if(isMultipart) {
			FileItemFactory factory =new DiskFileItemFactory();
			ServletFileUpload upload =new ServletFileUpload(factory);
			@SuppressWarnings("rawtypes")
			Iterator items;
			try{
				items = upload.parseRequest(request).iterator();
				while(items.hasNext()) {
					FileItem item = (FileItem) items.next();
					if(!item.isFormField()) {
						// 取出上传文件的文件名称
						String name = item.getName();
						 System.out.println("name"+name);
						String fileName =name.substring(name.lastIndexOf("img"), name.length());
						 System.out.println("fileName="+fileName);
						Object o=FileUtils.readObject(ImageServerUI.IMAGE_SAVE_DIRECTORY)==null?System.getProperty("user.dir"):FileUtils.readObject(ImageServerUI.IMAGE_SAVE_DIRECTORY);
						String path =o +""+ File.separatorChar+ fileName;
						// 上传文件
						File uploadedFile =new File(path);
						com.google.common.io.Files.createParentDirs(uploadedFile);
						item.write(uploadedFile);
						
						response.setContentType("text/html;charset=utf-8");
						PrintWriter out = response.getWriter();
						 System.out.println(path);
						out.print("上传的文件为："+ name);
						out.print("保存的地址为："+ path );
						out.flush();
						out.close();
					}
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
    
    public static String parseFilePathFromId(String id,String root){
        if (Strings.isNullOrEmpty(id)) {
        	throw new DongluServiceException("id不能空");
        }
        if (id.endsWith(".jpg") == false) {
            id += ".jpg";
        }
        String year = id.substring(0, 4);
        String month = id.substring(4, 6);
        String date = id.substring(6, 8);
        String hours = id.substring(8, 10);
        System.out.println(year+"=="+month+"=="+date+"=="+hours);
        String filePath = new StringBuilder(root)
        .append(year).append(File.separator)
        .append(month).append(File.separator)
        .append(date).append(File.separator)
        .append(hours).append(File.separator)
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
