package com.donglu.carpark.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.server.imgserver.ImageServerUI;
import com.donglu.carpark.service.ImageServiceI;
import com.donglu.carpark.util.CarparkFileUtils;
import com.dongluhitec.card.blservice.DongluServiceException;
import com.google.common.base.Strings;

public class ImageServiceImpl implements ImageServiceI {
	private static final Logger LOGGER = LoggerFactory.getLogger(ImageServiceImpl.class);
	@Override
	public String saveImageInServer(byte[] image, String imageName) {
		Object o=CarparkFileUtils.readObject(ImageServerUI.IMAGE_SAVE_DIRECTORY)==null?System.getProperty("user.dir"):CarparkFileUtils.readObject(ImageServerUI.IMAGE_SAVE_DIRECTORY);
		try {
			String fileName =imageName.substring(imageName.lastIndexOf("img"));
			Path path = Paths.get(o+"\\"+fileName);
			if (!Files.exists(path)) {
				com.google.common.io.Files.createParentDirs(path.toFile());
				Files.createFile(path);
			}
			Files.write(path, image);
			LOGGER.debug("保存图片{}到{}成功",imageName,path);
			return path.toString();
		} catch (Exception e) {
			LOGGER.error("保存图片失败",e);
		}
		return null;
	}

	@Override
	public byte[] getImage(String imageName) {
		try {
			Object o=CarparkFileUtils.readObject(ImageServerUI.IMAGE_SAVE_DIRECTORY)==null?System.getProperty("user.dir"):CarparkFileUtils.readObject(ImageServerUI.IMAGE_SAVE_DIRECTORY);
			LOGGER.debug("服务器图片保存位置{}，接收到请求图片：{}",o,imageName);
			String filePathFromId = parseFilePathFromId(imageName,o+"\\img\\");
			LOGGER.debug("服务器图片位置：{}",filePathFromId);
			byte[] bytes = getBytes(filePathFromId);
			return bytes;
		} catch (Exception e) {
			LOGGER.info("获取图片失败",e);
		}
		return null;
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
