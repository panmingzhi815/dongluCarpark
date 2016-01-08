package com.donglu.carpark.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by panmingzhi815 on 2014/12/24.
 */
public class CarparkFileUtils {

    private static Logger LOGGER = LoggerFactory.getLogger(CarparkFileUtils.class);

    public static String root = System.getProperty("user.dir")+ File.separator;
	public static String objectTemp = root + "temp" + File.separator;

    public static String saveFile(String folder,String fileName,byte[] fileData){
        if(fileData == null){
            return null;
        }
        File file = new File(root + folder);
        if(!file.exists()){
            file.mkdirs();
        }
        String filePath = root + folder + File.separator + fileName;
        try(FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream)){
            bos.write(fileData);
            bos.flush();
            LOGGER.debug("保存文件成功：{}", filePath);
        }finally {
            return filePath;
        }
    }

    public static byte[] readFile(String filePath){
        File file = new File(filePath);
        if(!file.exists()){
            return null;
        }
        try(FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fileInputStream)){
            byte[] bytes = new byte[bis.available()];
            bis.read(bytes);
            return bytes;
        }catch (Exception e){
            LOGGER.error("读取文件时发生异常",e);
            return null;
        }
    }

	public static void writeObject(String name, Object obj) {
		ObjectOutputStream oos=null;
		FileOutputStream fos=null;
		try {
			Path dir = Paths.get(objectTemp);
			if(!Files.exists(dir)){
				Files.createDirectory(dir);
			}
			File file=new File(objectTemp+name+".temp");
			if (!file.exists()) {
				file.createNewFile();
			}
			fos=new FileOutputStream(file);
			oos=new ObjectOutputStream(fos);
			oos.writeObject(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (fos!=null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (oos!=null) {
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static Object readObject(String name) {
		ObjectInputStream ois=null;
		FileInputStream fis=null;
		try {
			File file=new File(objectTemp+name+".temp");
			if (!file.exists()) {
				return null;
			}
			fis=new FileInputStream(file);
			ois=new ObjectInputStream(fis);
			Object readObject = ois.readObject();
			return readObject;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (fis!=null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (ois!=null) {
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return name;
	}
}
