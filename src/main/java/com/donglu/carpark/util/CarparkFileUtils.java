package com.donglu.carpark.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Created by panmingzhi815 on 2014/12/24.
 */
public class CarparkFileUtils {

    public static String root = System.getProperty("user.dir")+ File.separator;
	public static String objectTemp = root + "temp" + File.separator;
	
	public static void writeObject(String name, Object obj){
		try {
			writeObjectForException(name, obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void writeObjectForException(String name, Object obj) throws Exception {
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
			throw e;
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
		return null;
	}
}
