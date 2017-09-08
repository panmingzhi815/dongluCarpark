package com.dongluhitec.card.domain.db.singlecarpark.weixin;

import java.io.Serializable;

public class WeixinAccount implements Serializable{
	/**
	 * 唯一的身份标识
	 */
	private String appId;
	/**
	 * 调用接口的密钥
	 */
	private String appScret;
	/**
	 * 公众号支付请求中用于加密的密钥
	 */
	private String paySignKey;
	/**
	 * 微信支付分配的商户号
	 */
	private String mchId;
	/**
	 * 加载支付证书文件的密码(默认为商户号)
	 */
	private String certificateKey;
	/**
	 * 微信支付分配的设备号
	 */
	private String deviceInfo;

	/**
	 * 财付通商户身份的标识
	 */
	private String partnerId;

	/**
	 * 微信分配的子商户公众账号ID
	 */
	private String subId;
	/**
	 * 微信支付分配的子商户号
	 */
	private String subMchId;
	/**
	 * 微信支付回调地址
	 */
	private String notifyUrl;
	
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getAppScret() {
		return appScret;
	}
	public void setAppScret(String appScret) {
		this.appScret = appScret;
	}
	public String getPaySignKey() {
		return paySignKey;
	}
	public void setPaySignKey(String paySignKey) {
		this.paySignKey = paySignKey;
	}
	public String getMchId() {
		return mchId;
	}
	public void setMchId(String mchId) {
		this.mchId = mchId;
	}
	public String getCertificateKey() {
		return certificateKey;
	}
	public void setCertificateKey(String certificateKey) {
		this.certificateKey = certificateKey;
	}
	public String getDeviceInfo() {
		return deviceInfo;
	}
	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	public String getPartnerId() {
		return partnerId;
	}
	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}
	public String getSubId() {
		return subId;
	}
	public void setSubId(String subId) {
		this.subId = subId;
	}
	public String getSubMchId() {
		return subMchId;
	}
	public void setSubMchId(String subMchId) {
		this.subMchId = subMchId;
	}
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}
}
