package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.Date;

import javax.persistence.Entity;

import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.util.StrUtil;

@Entity
public class SingleCarparkImageHistory extends DomainObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6818506711499205078L;
	public enum Property{
		plateNO,factPlateNO,bigImage,type,time
	}
	public enum Label{
		timeLabel
	}
	
	private String plateNO;
	private String factPlateNO;
	private String bigImage;
	private String type;
	private Date time;
	
	public String getTimeLabel(){
		return StrUtil.formatDateTime(time);
	}
	
	public String getPlateNO() {
		return plateNO;
	}
	public void setPlateNO(String plateNO) {
		this.plateNO = plateNO;
		firePropertyChange("plateNO", null, null);
	}
	public String getBigImage() {
		return bigImage;
	}
	public void setBigImage(String bigImage) {
		this.bigImage = bigImage;
		firePropertyChange("bigImage", null, null);
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
		firePropertyChange("type", null, null);
	}
	public String getFactPlateNO() {
		return factPlateNO;
	}
	public void setFactPlateNO(String factPlateNO) {
		this.factPlateNO = factPlateNO;
		firePropertyChange("factPlateNO", null, null);
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time=time;
		firePropertyChange("time", null, null);
	}
}
