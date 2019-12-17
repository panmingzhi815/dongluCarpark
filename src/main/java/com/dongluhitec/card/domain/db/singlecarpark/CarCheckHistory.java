package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.Date;

import javax.persistence.Entity;

import com.donglu.carpark.util.CarparkUtils;
import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.common.base.Objects;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
public class CarCheckHistory extends DomainObject {
	
	public enum Label{
		plate,type,timeLabel,sourcePlate,status,editedPlateLabel,editedPlateSize,shouldMoney
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String plate;
	private String type;
	private Date time;
	private String sourcePlate;
	private String status="确认中";
	private boolean editedPlate;
	private String deviceIp;
	private String deviceName;
	private String bigImage;
	private String smallImage;
	private Integer editedPlateSize=0;
	private Double shouldMoney=0d;
	private String carType;
	
	public String getTimeLabel(){
		return StrUtil.formatDateTime(time);
	}
	public String getEditedPlateLabel(){
		return editedPlate?"已修改":"未修改";
	}
	public boolean equals(CarCheckHistory obj) {
		System.out.println(obj.getId());
		if (obj!=null&&obj.getId()!=null) {
			return Objects.equal(getId(), obj.getId());
		}
        return super.equals(obj);
    }
	public void setEditedPlate(boolean editedPlate) {
		this.editedPlate = editedPlate;
		setEditedPlateSize(CarparkUtils.checkNotAlikeSize(sourcePlate,plate));
	}
}
