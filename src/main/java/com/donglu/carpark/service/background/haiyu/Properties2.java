package com.donglu.carpark.service.background.haiyu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by xiaopan on 2016/8/16.
 */
public class Properties2 extends Properties {


    public static Properties2 load(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            throw new FileNotFoundException("文件不存在:" + fileName);
        }
        Properties2 properties2 = new Properties2();
        properties2.load(new FileInputStream(fileName));
        return properties2;
    }

    public Boolean getBoolean(String key) {
        String property = getProperty(key);
        return Boolean.valueOf(property);
    }

    public Boolean getBoolean(String key,Boolean defaultValue) {
        String property = getProperty(key);
        if (property == null || property.isEmpty()) {
            return defaultValue;
        }
        return Boolean.valueOf(property);
    }

    public Integer getInteger(String key) {
        String property = getProperty(key);
        return Integer.valueOf(property);
    }

    public Integer getInteger(String key, Integer defaultValue) {
        String property = getProperty(key);
        if (property == null || property.isEmpty()) {
            return defaultValue;
        }
        return Integer.valueOf(property);
    }

}
