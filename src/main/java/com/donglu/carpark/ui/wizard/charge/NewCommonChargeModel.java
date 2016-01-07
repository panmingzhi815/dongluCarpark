package com.donglu.carpark.ui.wizard.charge;

import java.util.ArrayList;
import java.util.List;

import com.dongluhitec.card.domain.db.singlecarpark.CarparkCarType;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkChargeStandard;

/**
 * Created with IntelliJ IDEA.
 * User: panmingzhi
 * Date: 13-11-19
 * Time: 下午4:14
 * To change this template use File | Settings | File Templates.
 */
public class NewCommonChargeModel extends CarparkChargeStandard {
	
	private static final long serialVersionUID = 1328643144822716698L;
	private boolean checkHolidayType=false;
	private boolean checkFreeTime=false;
	public static enum Property{
		carparkCarTypeList,freeTimeEnable
	}
	
	private List<CarparkCarType> carparkCarTypeList = new ArrayList<CarparkCarType>();
	private String freeTimeEnable;

	public List<CarparkCarType> getCarparkCarTypeList() {
		return carparkCarTypeList;
	}

	public void setCarparkCarTypeList(List<CarparkCarType> carparkCarTypeList) {
		this.carparkCarTypeList.clear();
		this.carparkCarTypeList.addAll(carparkCarTypeList);
		firePropertyChange(Property.carparkCarTypeList.name(), null, null);
	}

	public String getFreeTimeEnable() {
		return freeTimeEnable;
	}

	public void setFreeTimeEnable(String freeTimeEnable) {
		firePropertyChange(Property.freeTimeEnable.name(), this.freeTimeEnable, this.freeTimeEnable = freeTimeEnable);
		if(freeTimeEnable == "是"){
			setAcrossdayChargeEnable(1);
		}else{
			setAcrossdayChargeEnable(0);
		}
		
	}

	public boolean isCheckHolidayType() {
		return checkHolidayType;
	}

	public void setCheckHolidayType(boolean checkHolidayType) {
		this.checkHolidayType = checkHolidayType;
		if (pcs != null)
			pcs.firePropertyChange("checkHolidayType", null, null);
	}

	public boolean isCheckFreeTime() {
		return checkFreeTime;
	}

	public void setCheckFreeTime(boolean checkFreeTime) {
		this.checkFreeTime = checkFreeTime;
		if (pcs != null)
			pcs.firePropertyChange("checkFreeTime", null, null);
	}
}
