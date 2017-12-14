package com.donglu.carpark.hardware.eq2013;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 大屏内容推送
 * @author HuangJianxiong
 * 2017年12月14日 上午11:56:15
 */
@Singleton
public class BigScreenPrinterRunner {
	private static final String screenConfigPath = System.getProperty("user.dir")+File.separator+"screen";

    private final Logger LOGGER = LoggerFactory.getLogger(BigScreenPrinterRunner.class);
    private final Map<String, Integer> mapIntegerData=new HashMap<>();
    
    private Map<String, String> mapLastContentText=new HashMap<String, String>();
    Map<String, Long> mapLastTime=new HashMap<>();
    private long lastTimeUpdateTime=0;

    @Inject
    public BigScreenPrinterRunner() {
    }


    /**
     * 更新屏幕显示内容
     * @param screen
     */
    public void updateBigScreen(String ip,Map<String, String> values){
        try {
        	if(System.currentTimeMillis()-lastTimeUpdateTime>(30*60*1000)){
        		EQ2013Library.updateTime(ip);
        	}
			LOGGER.debug("更新屏幕:{}",ip);
			File file=new File(screenConfigPath+File.separator+ip+"");
			if(!file.exists()||!file.isDirectory()){
				LOGGER.info("系统中没有找到：{} 的配置文件");
				return;
			}
			File[] listFiles = file.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.getName().endsWith("txt");
				}
			});
			for (File f : listFiles) {
				String replace = replace(values,f.getName(),ip);
				LOGGER.debug("根据模板生成大屏内容:\r\n{}",replace);
				String key = ip+"-"+f.getName();
				String last = mapLastContentText.getOrDefault(key, "");
				if(last.equals(replace)&&System.currentTimeMillis()-mapLastTime.getOrDefault(key, 0l)<60000){
					continue;
				}
				ScreenConfig screenConfig = ScreenConfig.createOrRead(screenConfigPath+File.separator+ip+File.separator+f.getName().split("\\.")[0]+".json");
				boolean sendRealText = EQ2013Library.sendRealText(ip.replace('-', ':'), replace,screenConfig);
				if (sendRealText) {
					mapLastTime.put(key, System.currentTimeMillis());
					mapLastContentText.put(key, replace);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
    }

    /**
     * 通过map替换模板中的内容
     * @param replaceMap 占位符
     * @param file_name
     * @param screenIdentifier 
     * @return String 通过点位符替换模板过后的内容
     */
    public String replace(Map<String, String> replaceMap, String file_name, String screenIdentifier){
    	
        Path path = null;
        if(screenIdentifier==null){
        	path = Paths.get(file_name);
        }else{
            path = Paths.get(screenConfigPath+File.separator+screenIdentifier+File.separator+file_name);
        }

        try {;
            byte[] readAllBytes = Files.readAllBytes(path);
			String result = new String(readAllBytes,"gbk");
            for (String string : replaceMap.keySet()) {
                result = result.replaceAll("#"+string+"#",replaceMap.get(string));
            }
            return result;
        } catch (IOException e) {
            LOGGER.error("读取大屏：{} 配置文件错误:{}", screenIdentifier,file_name);
            LOGGER.error("",e);
            return null;
        }
    }

	public Map<String, Integer> getMapIntegerData() {
		return mapIntegerData;
	}
	public static void main(String[] args) throws Exception {
		String s = "部门:安全部      姓名:杨玉祥";
		System.out.println(s.toCharArray().length);
		System.out.println(s.getBytes("GBK").length);
		
		s="总经办TL";
		System.out.println(s.replaceAll("[a-zA-Z]", ""));
	}
}
