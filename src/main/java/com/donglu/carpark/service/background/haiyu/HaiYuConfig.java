package com.donglu.carpark.service.background.haiyu;

import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * 贵州海誉同步项目
 * Created by xiaopan on 2016/8/15.
 */
@Singleton
public class HaiYuConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(HaiYuConfig.class);
    private static final String fileName = "HaiYuConfig.properties";

    private boolean isEnable = false;

    private String username;
    private String password;
    private String key = "";
    private String encryptKey = "";

    private String cardUsagePostUrl = "";
    private int cardUsageDelayTime = 30;

    private String consumptionHistoryPostUrl = "";
    private int consumptionHistoryDelayTime = 30;

    private String userHistoryAddAndUpdatePostUrl = "";
    private String userHistoryRemovePostUrl = "";
    private int userHistoryDelayTime = 30;

    private String cardHistoryAddAndUpdatePostUrl = "";
    private String cardHistoryRemovePostUrl = "";
    private int cardHistoryDelayTime = 30;

    private String privilegeGroupHistoryAddAndUpdatePostUrl = "";
    private String privilegeGroupHistoryRemovePostUrl = "";
    private int privilegeGroupHistoryDelayTime = 30;

    private String privilegeCardHistoryAddAndUpdatePostUrl = "";
    private String privilegeCardHistoryRemovePostUrl = "";
    private int privilegeCardHistoryDelayTime = 30;

    private String privilegeDeviceHistoryAddAndUpdatePostUrl = "";
    private String privilegeDeviceHistoryRemovePostUrl = "";
    private int privilegeDeviceHistoryDelayTime = 30;

    private String deviceHistoryAddAndUpdatePostUrl = "";
    private String deviceHistoryRemovePostUrl = "";
    private int deviceHistoryDelayTime = 30;

    private String linkHistoryAddAndUpdatePostUrl = "";
    private String linkHistoryRemovePostUrl = "";
    private int linkHistoryDelayTime = 30;

    private String carparkPostUrl = "";
    private int carparkDelayTime = 30;

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEncryptKey() {
        return encryptKey;
    }

    public void setEncryptKey(String encryptKey) {
        this.encryptKey = encryptKey;
    }

    public String getCardUsagePostUrl() {
        return cardUsagePostUrl;
    }

    public void setCardUsagePostUrl(String cardUsagePostUrl) {
        this.cardUsagePostUrl = cardUsagePostUrl;
    }

    public int getCardUsageDelayTime() {
        return cardUsageDelayTime;
    }

    public void setCardUsageDelayTime(int cardUsageDelayTime) {
        this.cardUsageDelayTime = cardUsageDelayTime;
    }

    public String getConsumptionHistoryPostUrl() {
        return consumptionHistoryPostUrl;
    }

    public void setConsumptionHistoryPostUrl(String consumptionHistoryPostUrl) {
        this.consumptionHistoryPostUrl = consumptionHistoryPostUrl;
    }

    public int getConsumptionHistoryDelayTime() {
        return consumptionHistoryDelayTime;
    }

    public void setConsumptionHistoryDelayTime(int consumptionHistoryDelayTime) {
        this.consumptionHistoryDelayTime = consumptionHistoryDelayTime;
    }

    public String getUserHistoryAddAndUpdatePostUrl() {
        return userHistoryAddAndUpdatePostUrl;
    }

    public void setUserHistoryAddAndUpdatePostUrl(String userHistoryAddAndUpdatePostUrl) {
        this.userHistoryAddAndUpdatePostUrl = userHistoryAddAndUpdatePostUrl;
    }

    public String getUserHistoryRemovePostUrl() {
        return userHistoryRemovePostUrl;
    }

    public void setUserHistoryRemovePostUrl(String userHistoryRemovePostUrl) {
        this.userHistoryRemovePostUrl = userHistoryRemovePostUrl;
    }

    public int getUserHistoryDelayTime() {
        return userHistoryDelayTime;
    }

    public void setUserHistoryDelayTime(int userHistoryDelayTime) {
        this.userHistoryDelayTime = userHistoryDelayTime;
    }

    public String getCardHistoryAddAndUpdatePostUrl() {
        return cardHistoryAddAndUpdatePostUrl;
    }

    public void setCardHistoryAddAndUpdatePostUrl(String cardHistoryAddAndUpdatePostUrl) {
        this.cardHistoryAddAndUpdatePostUrl = cardHistoryAddAndUpdatePostUrl;
    }

    public String getCardHistoryRemovePostUrl() {
        return cardHistoryRemovePostUrl;
    }

    public void setCardHistoryRemovePostUrl(String cardHistoryRemovePostUrl) {
        this.cardHistoryRemovePostUrl = cardHistoryRemovePostUrl;
    }

    public int getCardHistoryDelayTime() {
        return cardHistoryDelayTime;
    }

    public void setCardHistoryDelayTime(int cardHistoryDelayTime) {
        this.cardHistoryDelayTime = cardHistoryDelayTime;
    }

    public String getPrivilegeGroupHistoryAddAndUpdatePostUrl() {
        return privilegeGroupHistoryAddAndUpdatePostUrl;
    }

    public void setPrivilegeGroupHistoryAddAndUpdatePostUrl(String privilegeGroupHistoryAddAndUpdatePostUrl) {
        this.privilegeGroupHistoryAddAndUpdatePostUrl = privilegeGroupHistoryAddAndUpdatePostUrl;
    }

    public String getPrivilegeGroupHistoryRemovePostUrl() {
        return privilegeGroupHistoryRemovePostUrl;
    }

    public void setPrivilegeGroupHistoryRemovePostUrl(String privilegeGroupHistoryRemovePostUrl) {
        this.privilegeGroupHistoryRemovePostUrl = privilegeGroupHistoryRemovePostUrl;
    }

    public int getPrivilegeGroupHistoryDelayTime() {
        return privilegeGroupHistoryDelayTime;
    }

    public void setPrivilegeGroupHistoryDelayTime(int privilegeGroupHistoryDelayTime) {
        this.privilegeGroupHistoryDelayTime = privilegeGroupHistoryDelayTime;
    }

    public String getPrivilegeCardHistoryAddAndUpdatePostUrl() {
        return privilegeCardHistoryAddAndUpdatePostUrl;
    }

    public void setPrivilegeCardHistoryAddAndUpdatePostUrl(String privilegeCardHistoryAddAndUpdatePostUrl) {
        this.privilegeCardHistoryAddAndUpdatePostUrl = privilegeCardHistoryAddAndUpdatePostUrl;
    }

    public String getPrivilegeCardHistoryRemovePostUrl() {
        return privilegeCardHistoryRemovePostUrl;
    }

    public void setPrivilegeCardHistoryRemovePostUrl(String privilegeCardHistoryRemovePostUrl) {
        this.privilegeCardHistoryRemovePostUrl = privilegeCardHistoryRemovePostUrl;
    }

    public int getPrivilegeCardHistoryDelayTime() {
        return privilegeCardHistoryDelayTime;
    }

    public void setPrivilegeCardHistoryDelayTime(int privilegeCardHistoryDelayTime) {
        this.privilegeCardHistoryDelayTime = privilegeCardHistoryDelayTime;
    }

    public String getPrivilegeDeviceHistoryAddAndUpdatePostUrl() {
        return privilegeDeviceHistoryAddAndUpdatePostUrl;
    }

    public void setPrivilegeDeviceHistoryAddAndUpdatePostUrl(String privilegeDeviceHistoryAddAndUpdatePostUrl) {
        this.privilegeDeviceHistoryAddAndUpdatePostUrl = privilegeDeviceHistoryAddAndUpdatePostUrl;
    }

    public String getPrivilegeDeviceHistoryRemovePostUrl() {
        return privilegeDeviceHistoryRemovePostUrl;
    }

    public void setPrivilegeDeviceHistoryRemovePostUrl(String privilegeDeviceHistoryRemovePostUrl) {
        this.privilegeDeviceHistoryRemovePostUrl = privilegeDeviceHistoryRemovePostUrl;
    }

    public int getPrivilegeDeviceHistoryDelayTime() {
        return privilegeDeviceHistoryDelayTime;
    }

    public void setPrivilegeDeviceHistoryDelayTime(int privilegeDeviceHistoryDelayTime) {
        this.privilegeDeviceHistoryDelayTime = privilegeDeviceHistoryDelayTime;
    }

    public String getDeviceHistoryAddAndUpdatePostUrl() {
        return deviceHistoryAddAndUpdatePostUrl;
    }

    public void setDeviceHistoryAddAndUpdatePostUrl(String deviceHistoryAddAndUpdatePostUrl) {
        this.deviceHistoryAddAndUpdatePostUrl = deviceHistoryAddAndUpdatePostUrl;
    }

    public String getDeviceHistoryRemovePostUrl() {
        return deviceHistoryRemovePostUrl;
    }

    public void setDeviceHistoryRemovePostUrl(String deviceHistoryRemovePostUrl) {
        this.deviceHistoryRemovePostUrl = deviceHistoryRemovePostUrl;
    }

    public int getDeviceHistoryDelayTime() {
        return deviceHistoryDelayTime;
    }

    public void setDeviceHistoryDelayTime(int deviceHistoryDelayTime) {
        this.deviceHistoryDelayTime = deviceHistoryDelayTime;
    }

    public String getLinkHistoryAddAndUpdatePostUrl() {
        return linkHistoryAddAndUpdatePostUrl;
    }

    public void setLinkHistoryAddAndUpdatePostUrl(String linkHistoryAddAndUpdatePostUrl) {
        this.linkHistoryAddAndUpdatePostUrl = linkHistoryAddAndUpdatePostUrl;
    }

    public String getLinkHistoryRemovePostUrl() {
        return linkHistoryRemovePostUrl;
    }

    public void setLinkHistoryRemovePostUrl(String linkHistoryRemovePostUrl) {
        this.linkHistoryRemovePostUrl = linkHistoryRemovePostUrl;
    }

    public int getLinkHistoryDelayTime() {
        return linkHistoryDelayTime;
    }

    public void setLinkHistoryDelayTime(int linkHistoryDelayTime) {
        this.linkHistoryDelayTime = linkHistoryDelayTime;
    }

    public String getCarparkPostUrl() {
        return carparkPostUrl;
    }

    public void setCarparkPostUrl(String carparkPostUrl) {
        this.carparkPostUrl = carparkPostUrl;
    }

    public int getCarparkDelayTime() {
        return carparkDelayTime;
    }

    public void setCarparkDelayTime(int carparkDelayTime) {
        this.carparkDelayTime = carparkDelayTime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void read() {
        try {
            Properties2 properties = Properties2.load(fileName);

            this.isEnable = properties.getBoolean("isEnable", false);
            this.username = properties.getProperty("username", "");
            this.password = properties.getProperty("password", "");
            this.encryptKey = properties.getProperty("encryptKey", "");
            this.key = properties.getProperty("key", "");

            this.cardUsagePostUrl = properties.getProperty("cardUsagePostUrl", "");
            this.cardUsageDelayTime = properties.getInteger("cardUsageDelayTime", 30);

            this.consumptionHistoryPostUrl = properties.getProperty("consumptionHistoryPostUrl", "");
            this.consumptionHistoryDelayTime = properties.getInteger("consumptionHistoryDelayTime", 30);

            this.userHistoryAddAndUpdatePostUrl = properties.getProperty("userHistoryAddAndUpdatePostUrl", "");
            this.userHistoryRemovePostUrl = properties.getProperty("userHistoryRemovePostUrl", "");
            this.userHistoryDelayTime = properties.getInteger("userHistoryDelayTime", 30);

            this.cardHistoryAddAndUpdatePostUrl = properties.getProperty("cardHistoryAddAndUpdatePostUrl", "");
            this.cardHistoryRemovePostUrl = properties.getProperty("cardHistoryRemovePostUrl", "");
            this.cardHistoryDelayTime = properties.getInteger("cardHistoryDelayTime", 30);

            this.privilegeGroupHistoryAddAndUpdatePostUrl = properties.getProperty("privilegeGroupHistoryAddAndUpdatePostUrl", "");
            this.privilegeGroupHistoryRemovePostUrl = properties.getProperty("privilegeGroupHistoryRemovePostUrl", "");
            this.privilegeGroupHistoryDelayTime = properties.getInteger("privilegeGroupHistoryDelayTime", 30);

            this.privilegeCardHistoryAddAndUpdatePostUrl = properties.getProperty("privilegeCardHistoryAddAndUpdatePostUrl", "");
            this.privilegeCardHistoryRemovePostUrl = properties.getProperty("privilegeCardHistoryRemovePostUrl", "");
            this.privilegeCardHistoryDelayTime = properties.getInteger("privilegeCardHistoryDelayTime", 30);

            this.privilegeDeviceHistoryAddAndUpdatePostUrl = properties.getProperty("privilegeDeviceHistoryAddAndUpdatePostUrl", "");
            this.privilegeDeviceHistoryRemovePostUrl = properties.getProperty("privilegeDeviceHistoryRemovePostUrl", "");
            this.privilegeDeviceHistoryDelayTime = properties.getInteger("privilegeDeviceHistoryDelayTime", 30);

            this.deviceHistoryAddAndUpdatePostUrl = properties.getProperty("deviceHistoryAddAndUpdatePostUrl", "");
            this.deviceHistoryRemovePostUrl = properties.getProperty("deviceHistoryRemovePostUrl", "");
            this.deviceHistoryDelayTime = properties.getInteger("deviceHistoryDelayTime", 30);

            this.linkHistoryAddAndUpdatePostUrl = properties.getProperty("linkHistoryAddAndUpdatePostUrl", "");
            this.linkHistoryRemovePostUrl = properties.getProperty("linkHistoryRemovePostUrl", "");
            this.linkHistoryDelayTime = properties.getInteger("linkHistoryDelayTime", 30);

            this.carparkPostUrl = properties.getProperty("carparkPostUrl", "");
            this.carparkDelayTime = properties.getInteger("carparkDelayTime", 30);


            LOGGER.info("读取配置文件:{} 成功", fileName);
        } catch (Exception e) {
            LOGGER.warn("读取配置文件:{} 失败", fileName);
        }
    }

    public void write() {
        FileOutputStream fos = null;
        PrintWriter out = null;
        try {
            fos = new FileOutputStream(fileName, false);
            out = new PrintWriter(fos);

            out.println(String.format("#配置修改时间:%s", StrUtil.formatDateTime(new Date())));
            println(out, "是否开启贵州海誉同步", "%s=%s", "isEnable", this.isEnable);
            println(out, "同步时验证用户名", "%s=%s", "username", this.username);
            println(out, "同步时验证密码", "%s=%s", "password", this.password);
            println(out, "同步时验证密钥", "%s=%s", "encryptKey", this.encryptKey);
            println(out, "同步时验证公钥", "%s=%s", "key", this.key);

            println(out, "刷卡记录同步地址", "%s=%s", "cardUsagePostUrl", this.cardUsagePostUrl);
            println(out, "刷卡记录同步时间间隔(秒)", "%s=%d", "cardUsageDelayTime", this.cardUsageDelayTime);

            println(out, "消费记录同步地址", "%s=%s", "consumptionHistoryPostUrl", this.consumptionHistoryPostUrl);
            println(out, "消费记录同步时间间隔(秒)", "%s=%d", "consumptionHistoryDelayTime", this.consumptionHistoryDelayTime);

            println(out, "用户信息添加与修改同步地址", "%s=%s", "userHistoryAddAndUpdatePostUrl", this.userHistoryAddAndUpdatePostUrl);
            println(out, "用户信息删除同步地址", "%s=%s", "userHistoryRemovePostUrl", this.userHistoryRemovePostUrl);
            println(out, "用户信息同步时间间隔(秒)", "%s=%d", "userHistoryDelayTime", this.userHistoryDelayTime);

            println(out, "卡片信息添加与修改同步地址", "%s=%s", "cardHistoryAddAndUpdatePostUrl", this.cardHistoryAddAndUpdatePostUrl);
            println(out, "卡片信息删除同步地址", "%s=%s", "cardHistoryRemovePostUrl", this.cardHistoryRemovePostUrl);
            println(out, "卡片信息同步时间间隔(秒)", "%s=%d", "cardHistoryDelayTime", this.cardHistoryDelayTime);

            println(out, "权限组信息添加与修改同步地址", "%s=%s", "privilegeGroupHistoryAddAndUpdatePostUrl", this.privilegeGroupHistoryAddAndUpdatePostUrl);
            println(out, "权限组信息删除同步地址", "%s=%s", "privilegeGroupHistoryRemovePostUrl", this.privilegeGroupHistoryRemovePostUrl);
            println(out, "权限组信息同步时间间隔(秒)", "%s=%d", "privilegeGroupHistoryDelayTime", this.privilegeGroupHistoryDelayTime);

            println(out, "权限组卡片信息添加与修改同步地址", "%s=%s", "privilegeCardHistoryAddAndUpdatePostUrl", this.privilegeCardHistoryAddAndUpdatePostUrl);
            println(out, "权限组卡片信息删除同步地址", "%s=%s", "privilegeCardHistoryRemovePostUrl", this.privilegeCardHistoryRemovePostUrl);
            println(out, "权限组卡片信息同步时间间隔(秒)", "%s=%d", "privilegeCardHistoryDelayTime", this.privilegeCardHistoryDelayTime);

            println(out, "权限组设备信息添加与修改同步地址", "%s=%s", "privilegeDeviceHistoryAddAndUpdatePostUrl", this.privilegeDeviceHistoryAddAndUpdatePostUrl);
            println(out, "权限组设备信息删除同步地址", "%s=%s", "privilegeDeviceHistoryRemovePostUrl", this.privilegeDeviceHistoryRemovePostUrl);
            println(out, "权限组设备信息同步时间间隔(秒)", "%s=%d", "privilegeDeviceHistoryDelayTime", this.privilegeDeviceHistoryDelayTime);

            println(out, "设备信息添加与修改同步地址", "%s=%s", "deviceHistoryAddAndUpdatePostUrl", this.deviceHistoryAddAndUpdatePostUrl);
            println(out, "设备信息删除同步地址", "%s=%s", "deviceHistoryRemovePostUrl", this.deviceHistoryRemovePostUrl);
            println(out, "设备信息同步时间间隔(秒)", "%s=%d", "deviceHistoryDelayTime", this.deviceHistoryDelayTime);

            println(out, "设备组信息添加与修改同步地址", "%s=%s", "linkHistoryAddAndUpdatePostUrl", this.linkHistoryAddAndUpdatePostUrl);
            println(out, "设备组信息删除同步地址", "%s=%s", "linkHistoryRemovePostUrl", this.linkHistoryRemovePostUrl);
            println(out, "设备组信息同步时间间隔(秒)", "%s=%d", "linkHistoryDelayTime", this.linkHistoryDelayTime);

            println(out, "停车场信息同步地址", "%s=%s", "carparkPostUrl", this.carparkPostUrl);
            println(out, "停车场信息同步时间间隔(秒)", "%s=%d", "carparkDelayTime", this.carparkDelayTime);


            out.flush();
        } catch (Exception e) {
            LOGGER.error("写入配置文件失败", e);
        } finally {
            if (fos != null) {
                try {
                    out.close();
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private void println(PrintWriter out, String tips, String format, String key, Object value) {
        out.println("#" + tips);
        out.println(String.format(format, key, value));
        out.println();
    }

    public static void main(String[] args) {
        HaiYuConfig haiYuConfig = new HaiYuConfig();
        haiYuConfig.write();
        LOGGER.debug("配置文件地址：{}", new File(fileName).getAbsolutePath());
    }
}
