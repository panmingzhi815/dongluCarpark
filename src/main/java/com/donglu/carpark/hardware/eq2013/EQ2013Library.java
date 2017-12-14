package com.donglu.carpark.hardware.eq2013;

import com.google.common.base.Strings;
import com.google.inject.Singleton;
import com.sun.jna.Native;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;


/**
 * Created by panmingzhi on 2016/11/21 0021.
 */
@Singleton
public class EQ2013Library {

    private static final Logger LOGGER = LoggerFactory.getLogger(EQ2013Library.class);
    static EQ2013JNA m_DllLibrary=null;
    final static String m_strUserPath = System.getProperty("user.dir");

    static {
        String strDllFileName = m_strUserPath + "\\screen\\EQ2008_Dll";
        m_DllLibrary = (EQ2013JNA) Native.loadLibrary(strDllFileName,EQ2013JNA.class);
        System.setProperty("jna.encoding","GBK");
    }

    public static void sendRealText(String address, List<Optional<String>> lineStr){
        if (Strings.isNullOrEmpty(address)) {
            LOGGER.error("屏幕发送地址不可为空");
            return;
        }
        String[] split = address.split(":");
        String ip = split[0];
        int port = Integer.valueOf(split[1]);
        if (!reloadSetIni("EQ2008_Dll_Set")){
            LOGGER.error("加载配置文件失败");
            return;
        }

        boolean realtimeConnect = m_DllLibrary.User_RealtimeConnect(port);
        if (!realtimeConnect) {
            LOGGER.error("连接屏幕:{}失败",ip);
//            return;
        }
//        m_DllLibrary.User_RealtimeScreenClear(port);
        EQ2013JNA.User_FontSet FontInfo = getUser_fontSet();
        StringBuilder sb = new StringBuilder();
        for (Optional<String> stringOptional : lineStr) {
            if (stringOptional.isPresent()) {
                sb.append(stringOptional.get());
            }
            sb.append("\r\n");
        }

        ScreenConfig screenConfig = ScreenConfig.createOrRead();
        screenConfig.fillFont(FontInfo);

        boolean sendText = m_DllLibrary.User_RealtimeSendText(port, screenConfig.getX(), screenConfig.getY(), 160, 96, sb.toString(), FontInfo);
        if (!sendText) {
            LOGGER.error("发送内容:\r\n{} 到屏幕:{}失败",sb,ip);
            return;
        }else{
            LOGGER.error("发送内容:\r\n{} 到屏幕:{}成功",sb,ip);
        }

        m_DllLibrary.User_RealtimeDisConnect(port);
    }

    public static boolean sendRealText(String address, String text){
    	return sendRealText(address, text,null,null, 288, 128,null);
    }
    public static boolean sendRealText(String ip, String text, ScreenConfig screenConfig) {
    	 EQ2013JNA.User_FontSet font = getUser_fontSet();
    	 screenConfig.fillFont(font);
    	return sendRealText(ip, text, screenConfig.getX(), screenConfig.getY(), screenConfig.width, screenConfig.height, font);
	}
    public static boolean sendRealText(String address, String text, Integer x, Integer y,int width,int height,EQ2013JNA.User_FontSet font){
        if (Strings.isNullOrEmpty(address)) {
            LOGGER.error("屏幕发送地址不可为空");
            return false;
        }
        String[] split = address.split(":");
        String ip = split[0];
        int port = Integer.valueOf(split[1]);
        if (!reloadSetIni("EQ2008_Dll_Set")){
            LOGGER.error("加载配置文件失败");
            return false;
        }

        boolean realtimeConnect = m_DllLibrary.User_RealtimeConnect(port);
        if (!realtimeConnect) {
            LOGGER.error("连接屏幕:{}失败",ip);
            return false;
        }
        EQ2013JNA.User_FontSet FontInfo = getUser_fontSet();
        ScreenConfig screenConfig = ScreenConfig.createOrRead();
        screenConfig.fillFont(FontInfo);
        if(font!=null){
        	FontInfo=font;
        }
        boolean sendText = m_DllLibrary.User_RealtimeSendText(port, x!=null?x:screenConfig.getX(), y!=null?y:screenConfig.getY(), width, height, text, FontInfo);
        if (!sendText) {
            LOGGER.error("向屏幕{}发送内容失败:\r\n{}",ip,text);
        }else{
            LOGGER.info("向屏幕{}发送内容成功:\r\n{}",ip,text);
        }

        m_DllLibrary.User_RealtimeDisConnect(port);
        return sendText;
    }
    
    

    public static EQ2013JNA.User_FontSet getUser_fontSet() {
        EQ2013JNA.User_FontSet FontInfo = new EQ2013JNA.User_FontSet();
        FontInfo.bFontBold = false;
        FontInfo.bFontItaic = false;
        FontInfo.bFontUnderline = false;
        FontInfo.colorFont = 0xFFFF;
        FontInfo.iFontSize = 9;
        FontInfo.strFontName = "宋体";
        FontInfo.iAlignStyle = 0;
        FontInfo.iVAlignerStyle = 0;
        FontInfo.iRowSpace = 1;

        return FontInfo;
    }

    private static boolean reloadSetIni(String ip) {
        String strEQ2008_dll_set_path = m_strUserPath + "\\screen\\" + ip + ".ini";
        Path path = Paths.get(strEQ2008_dll_set_path);
        if (!Files.exists(path)) {
            LOGGER.error("配置文件不存在:{}",strEQ2008_dll_set_path);
            return false;
        }

        m_DllLibrary.User_ReloadIniFile(strEQ2008_dll_set_path);
        return true;
    }

	public static void updateTime(String ip) {
		ip.split("-");
	}

}
