package com.dongluhitec.card.domain.db.singlecarpark;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;

import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.util.StrUtil;
@Entity
public class SingleCarparkSystemOperaLog extends DomainObject{
	
	public enum Property{
		operaName,operaDate,type,content, operaDateLabel
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -3207836534894290785L;
	private String operaName;
	private Date operaDate;
	@Enumerated(EnumType.STRING)
	private SystemOperaLogTypeEnum type;
	private String content;
	@Lob
	private byte[] remark;
	
	public String getOperaName() {
		return operaName;
	}
	public void setOperaName(String operaName) {
		this.operaName = operaName;
		if (pcs != null)
			pcs.firePropertyChange("operaName", null, null);
	}
	
	public String getOperaDateLabel() {
		return StrUtil.formatDate(operaDate, "yyyy-MM-dd HH:mm:ss");
	}
	public Date getOperaDate() {
		return operaDate;
	}
	public void setOperaDate(Date operaDate) {
		this.operaDate = operaDate;
		if (pcs != null)
			pcs.firePropertyChange("operaDate", null, null);
	}
	public SystemOperaLogTypeEnum getType() {
		return type;
	}
	public void setType(SystemOperaLogTypeEnum type) {
		this.type = type;
		if (pcs != null)
			pcs.firePropertyChange("type", null, null);
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
		if (pcs != null)
			pcs.firePropertyChange("content", null, null);
	}
	public byte[] getRemark() {
		return remark;
	}
	public void setRemark(byte[] remark) {
		this.remark = remark;
		firePropertyChange("remark", null, null);
	}
	public String getRemarkString() {
		String remark2 = null;
		try {
			remark2 = new String(remark,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return remark2;
	}
	public void setRemarkString(String remark) {
		try {
			this.remark = remark.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		firePropertyChange("remark", null, null);
	}
	
}
