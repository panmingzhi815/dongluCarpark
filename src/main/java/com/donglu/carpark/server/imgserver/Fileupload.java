package com.donglu.carpark.server.imgserver;

import java.io.File;

import java.io.IOException;

import java.io.PrintWriter;

import java.util.Iterator;

import

javax.servlet.ServletException;

import

javax.servlet.http.HttpServlet;

import

javax.servlet.http.HttpServletRequest;

import

javax.servlet.http.HttpServletResponse;

import

org.apache.commons.fileupload.FileItem;

import

org.apache.commons.fileupload.FileItemFactory;

import

org.apache.commons.fileupload.disk.DiskFileItemFactory;

import

org.apache.commons.fileupload.servlet.ServletFileUpload;

@SuppressWarnings("serial")

public class Fileupload extends HttpServlet {

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
						// System.out.println(name);
						String fileName =name.substring(name.lastIndexOf('\\') + 1, name.length());
						// System.out.println(fileName);
						String path =request.getRealPath("upload") + File.separatorChar+ fileName;
						// 上传文件
						File uploadedFile =new File(path);
						item.write(uploadedFile);
						response.setContentType("text/html;charset=gb2312");
						PrintWriter out = response.getWriter();
						// System.out.println(path);
						out.print("<font size='2'>上传的文件为："+ name +"<br>");
						out.print("保存的地址为："+ path +"</font>");
					}
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
