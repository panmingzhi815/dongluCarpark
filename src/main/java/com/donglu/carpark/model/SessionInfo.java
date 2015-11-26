package com.donglu.carpark.model;

/**
 * sessionInfo模型，只要登录成功，就需要设置到session里面，便于系统使用
 * 
 * @author 孙宇
 * 
 */
public class SessionInfo implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -366530817264680736L;
	private String loginName;
	private String loginPassword;
	private String storeName;
	
	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getLoginPassword() {
		return loginPassword;
	}

	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
}
