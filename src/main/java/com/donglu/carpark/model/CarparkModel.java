package com.donglu.carpark.model;

import java.util.ArrayList;
import java.util.List;

import com.donglu.carpark.info.CarparkChargeInfo;
import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;

public class CarparkModel extends DomainObject{
	
	private List<SingleCarparkCarpark> listCarpark=new ArrayList<>();
	private SingleCarparkCarpark carpark;
	private List<CarparkChargeInfo> listCarparkCharge=new ArrayList<>();
	private CarparkChargeInfo carparkChargeInfo;
	public List<SingleCarparkCarpark> getListCarpark() {
		return listCarpark;
	}
	public void setListCarpark(List<SingleCarparkCarpark> listCarpark) {
		this.listCarpark = listCarpark;
		if (pcs != null)
			pcs.firePropertyChange("listCarpark", null, null);
	}
	public SingleCarparkCarpark getCarpark() {
		return carpark;
	}
	public void setCarpark(SingleCarparkCarpark carpark) {
		this.carpark = carpark;
		if (pcs != null)
			pcs.firePropertyChange("carpark", null, null);
	}
	public List<CarparkChargeInfo> getListCarparkCharge() {
		return listCarparkCharge;
	}
	public void setListCarparkCharge(List<CarparkChargeInfo> listCarparkCharge) {
		this.listCarparkCharge = listCarparkCharge;
		if (pcs != null)
			pcs.firePropertyChange("listCarparkCharge", null, null);
	}
	public CarparkChargeInfo getCarparkChargeInfo() {
		return carparkChargeInfo;
	}
	public void setCarparkChargeInfo(CarparkChargeInfo carparkChargeInfo) {
		this.carparkChargeInfo = carparkChargeInfo;
		if (pcs != null)
			pcs.firePropertyChange("carparkChargeInfo", null, null);
	}
}
