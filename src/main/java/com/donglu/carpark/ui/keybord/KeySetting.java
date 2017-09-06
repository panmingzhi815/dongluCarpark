package com.donglu.carpark.ui.keybord;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.donglu.carpark.util.CarparkFileUtils;





public class KeySetting implements Serializable{
	
	public final static String fileName="KeyModel";
	
	public enum KeyReleaseTypeEnum{
		进口抬杆(16777227),进口落杆(16777226),出口抬杆(16777229),出口落杆(16777228),收费放行(16777236),免费放行(16777237),换班(16777232),归账(16777233),历史记录(16777234);
		int defaultKeyCode;
		KeyReleaseTypeEnum(int defaultKeyCode){
			this.defaultKeyCode = defaultKeyCode;
		}
	}
	final Map<KeyReleaseTypeEnum, Integer> map=new HashMap<>();
	
	public KeySetting() {
		KeyReleaseTypeEnum[] values = KeyReleaseTypeEnum.values();
		for (KeyReleaseTypeEnum keyReleaseTypeEnum : values) {
			map.put(keyReleaseTypeEnum, keyReleaseTypeEnum.defaultKeyCode);
		}
	}
	
	public Map<KeyReleaseTypeEnum, Integer> getMap() {
		return map;
	}
	
	public int getKeyCode(KeyReleaseTypeEnum keyReleaseTypeEnum){
		return map.get(keyReleaseTypeEnum);
	}
	
	public static KeySetting read(){
		KeySetting keySetting = (KeySetting) CarparkFileUtils.readObject(fileName);
		if (keySetting==null) {
			keySetting = new KeySetting();
		}
		return keySetting;
	}

	public void write() {
		CarparkFileUtils.writeObject(fileName, this);
	}
	
}
