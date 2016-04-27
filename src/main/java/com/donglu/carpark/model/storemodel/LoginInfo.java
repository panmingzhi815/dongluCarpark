package com.donglu.carpark.model.storemodel;


public class LoginInfo extends Info {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3040067511949840127L;
	private String name;
	private String pwd;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
}
