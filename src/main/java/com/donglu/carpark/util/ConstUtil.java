package com.donglu.carpark.util;

import java.util.HashMap;
import java.util.Map;

import com.dongluhitec.card.domain.db.singlecarpark.SystemUserTypeEnum;

/**
 * 保存一些常量,静态变量
 */
public class ConstUtil {
	private static final Map<String, String> mapCache=new HashMap<>();
	/**
	 * Text 的data的key 值 true时回车不改变焦点
	 */
	public static final String NO_CHANGE_FOCUS = "noChangeFocus";
	/**
	 * 启用发送车位  值不等于空则启用
	 */
	public static final String AUTO_SEND_POSITION_TO_DEVICE = "autoSendPositionToDevice";
	/**
	 * 设备保存名称
	 */
	public static final String MAP_IP_TO_DEVICE = "mapIpToDevice";
	/**
	 * 过期语音格式化
	 */
	public static final String VILIDTO_DATE = ",有效期至yyyy年MM月dd日";
	/**
	 * 临时车自动收费  虚拟机参数   true
	 */
	public static final String TEMP_CAR_AUTO_PASS = "tempCarAutoPass";
	/**
	 * 客户端图片保存位置
	 */
	public static final String CLIENT_IMAGE_SAVE_FILE_PATH = "clientImageSaveFilePath";
	/**
	 * 格式化日期  yyyy-MM-dd
	 */
	public static final String YYYY_MM_DD = "yyyy-MM-dd";
	/**
	 * 服务器图片保存位置
	 */
	public static final String IMAGE_SAVE_DIRECTORY = "directory";
	/**
	 * 保存商铺服务器地址
	 */
	public static final String STORE_SERVER_PATH = "yunServerAddress";
	/**
	 * 登录名
	 */
	public static final String USER_NAME = "userName";
	/**
	 * 开闸延迟
	 */
	public static final String OPEN_DOOR_DELAY = "openDoorDelay";
	public static final String AN_HOUR_SHOULD_MONEY = "anHourShouldMoney";
	/**
	 * 自动刷新停车场监控设置
	 */
	public static final String AUTO_REFRESH_CAMERA = "autoRefreshCamera";
	/**
	 * 访客车名称
	 */
	public static final String VISITOR_NAME="visitorName";
	/**
	 * 获取权限级别
	 * @return
	 */
	public static int getLevel(){
		String userName = System.getProperty(USER_NAME);
		if (userName.equals("admin")) {
			return 99;
		}
		String property = System.getProperty("userType");
		return SystemUserTypeEnum.getLevel(property);
	}
	public static boolean checkPrivilege(SystemUserTypeEnum... userTypes) {
		if (userTypes==null) {
			return true;
		}
		int level = getLevel();
		if (level==99) {
			return true;
		}
		for (SystemUserTypeEnum userType : userTypes) {
			if (userType==null) {
				continue;
			}
			if (userType.getLevel()==level) {
				return true;
			}
		}
		return false;
	}
	public static String getUserName() {
		String string = mapCache.get(USER_NAME);
		if (string!=null) {
			return string;
		}
		String property = System.getProperty(USER_NAME);
		mapCache.put(USER_NAME, property);
		return property;
	}
	public static String getVisitorName() {
		String string = mapCache.get(VISITOR_NAME);
		if (string!=null) {
			return string;
		}
		String property = System.getProperty(VISITOR_NAME);
		mapCache.put(VISITOR_NAME, property);
		return System.getProperty(VISITOR_NAME, "访客车");
	}
}
