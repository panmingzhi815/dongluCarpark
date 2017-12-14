package com.donglu.carpark.hardware.eq2013;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Created by panmingzhi on 2016/11/26 0026.
 */
public class ScreenConfig implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenConfig.class);
    private static final String SCREEN_PATH = "EQ2013显示配置.json";

    private Integer fontSize = 9;
    private String fontName = "宋体";
    private Integer hSpace = 1;
    private Integer vSpace = 1;
    private Integer x = 0;
    private Integer y = 0;
    // 0－ 左对齐
    // 1－居中
    // 2－右对齐
    public Integer iAlignStyle=0; // 对齐方式
    // 0-顶对齐
    // 1-上下居中
    // 2-底对齐
    public Integer iVAlignerStyle=0; // 上下对齐方式
    
    public Boolean bFontBold=false;//字体是否加粗
    
    public int width=288;
    public int height=32;
    
    public boolean showCharacter=false;
    public boolean showNumber=true;

    public Integer getFontSize() {
        return fontSize;
    }

    public void setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public Integer gethSpace() {
        return hSpace;
    }

    public void sethSpace(Integer hSpace) {
        this.hSpace = hSpace;
    }

    public Integer getvSpace() {
        return vSpace;
    }

    public void setvSpace(Integer vSpace) {
        this.vSpace = vSpace;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }


    public void fillFont(EQ2013JNA.User_FontSet fontInfo) {
        fontInfo.strFontName = this.fontName;
        fontInfo.iFontSize = this.fontSize;
        fontInfo.iRowSpace = this.vSpace;
        fontInfo.iAlignStyle=getiAlignStyle();
        fontInfo.iVAlignerStyle=getiVAlignerStyle();
        fontInfo.bFontBold=getbFontBold();
    }

    public static ScreenConfig createOrRead() {
        return createOrRead(SCREEN_PATH);
    }
    public static ScreenConfig createOrRead(String fileName) {
        try {
            Path path = Paths.get(fileName);
            if (Files.exists(path)){
                ScreenConfig screenConfig = JSON.parseObject(new String(Files.readAllBytes(path), "UTF-8"), ScreenConfig.class);
                return screenConfig;
            }else{
                ScreenConfig screenConfig = new ScreenConfig();
                String toJSONString = JSON.toJSONString(screenConfig, true);
                Files.write(path,toJSONString.getBytes("UTF-8"), StandardOpenOption.CREATE_NEW,StandardOpenOption.WRITE);
                return screenConfig;
            }
        } catch (Exception e) {
            LOGGER.error("读取屏幕字体配置文件{}失败",fileName,e);
            return new ScreenConfig();
        }
    }

	public Integer getiAlignStyle() {
		return iAlignStyle;
	}

	public void setiAlignStyle(Integer iAlignStyle) {
		this.iAlignStyle = iAlignStyle;
	}

	public Integer getiVAlignerStyle() {
		return iVAlignerStyle;
	}

	public void setiVAlignerStyle(Integer iVAlignerStyle) {
		this.iVAlignerStyle = iVAlignerStyle;
	}

	public Boolean getbFontBold() {
		if(bFontBold==null){
			return false;
		}
		return bFontBold;
	}

	public void setbFontBold(Boolean bFontBold) {
		this.bFontBold = bFontBold;
	}

	public static ScreenConfig createOrRead(String configfilename, String identifier) {
		Path path = Paths.get(configfilename.split("\\.")[0]+"-"+identifier+"."+configfilename.split("\\.")[1]);
		if(Files.exists(path)){
			configfilename=path.toFile().getName();
		}
		return createOrRead(configfilename);
	}
}
