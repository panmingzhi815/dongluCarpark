package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrePersist;

import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.util.StrUtil;

@Entity
public class SmsInfo extends DomainObject {
	
	public enum Property{
		plate,tel,userName,createTime,status,overSpeedSize,address,remark,speed,templateCode
	}
	public enum Label{
		statusLabel,createTimeLabel,overSpeedTimeLabel
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String tel;
	private String userName;
	private String plate;
	private String address;
	private Date overSpeedTime;
	private int overSpeedSize;
	private int speed=0;
	private String data;
	@Column(length=2048)
	private String remark;
	private Date createTime;
	private Date sendTime;
	private int status=1;//0 发送成功，1 待发送 2发送失败 3已停止4数据错误
	private String sendMsg;
	private String templateCode;
	
	@PrePersist
	public void init() {
		createTime=new Date();
	}
	
	
	public String getStatusLabel() {
		String s="已发送";
		switch (status) {
		case 1:
			s="待发送";
			break;
		case 2:
			s="发送失败";
			break;
		case 3:
			s="已停止";
			break;
		case 4:
			s="数据错误";
			break;
		default:
			break;
		}
		return s;
	}
	
	public String getOverSpeedTimeLabel() {
		return StrUtil.formatDateTime(overSpeedTime);
	}
	public String getCreateTimeLabel() {
		return StrUtil.formatDateTime(createTime);
	}
	
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
		firePropertyChange("data", null, null);
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
		firePropertyChange("remark", null, null);
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
		firePropertyChange("createTime", null, null);
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
		firePropertyChange("status", null, null);
	}


	public String getSendMsg() {
		return sendMsg;
	}

	public void setSendMsg(String sendMsg) {
		this.sendMsg = sendMsg;
		firePropertyChange("sendMsg", null, null);
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
		firePropertyChange("tel", null, null);
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
		firePropertyChange("userName", null, null);
	}

	public String getPlate() {
		return plate;
	}

	public void setPlate(String plate) {
		this.plate = plate;
		firePropertyChange("plate", null, null);
	}

	public Date getOverSpeedTime() {
		return overSpeedTime;
	}

	public void setOverSpeedTime(Date overSpeedTime) {
		this.overSpeedTime = overSpeedTime;
		firePropertyChange("overSpeedTime", null, null);
	}

	public int getOverSpeedSize() {
		return overSpeedSize;
	}

	public void setOverSpeedSize(int overSpeedSize) {
		this.overSpeedSize = overSpeedSize;
		firePropertyChange("overSpeedSize", null, null);
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
		firePropertyChange("sendTime", null, null);
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
		//firePropertyChange("address", null, null);
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
		//firePropertyChange("speed", null, null);
	}


	public String getTemplateCode() {
		return templateCode;
	}


	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
		//firePropertyChange("templateCode", null, null);
	}
}
