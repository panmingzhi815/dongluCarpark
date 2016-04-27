package com.donglu.carpark.model.storemodel;

import java.io.Serializable;

public class Info implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8393256923246369604L;
	private String msg;
	private String loginName;
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	@Override
	public String toString() {
		return loginName+"-"+getClass();
	}
}
