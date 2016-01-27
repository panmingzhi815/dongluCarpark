package com.donglu.carpark.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;

public class SearchErrorCarModel extends DomainObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7264776956712866252L;
	private List<SingleCarparkInOutHistory> havePlateNoList=new ArrayList<>();
	private List<SingleCarparkInOutHistory> noPlateNoList=new ArrayList<>();
	private String plateNo;
	private SingleCarparkInOutHistory havePlateNoSelect;
	private SingleCarparkInOutHistory noPlateNoSelect;
	private byte[] bigImg;
	private byte[] smallImg;
	private String saveBigImg;
	private String saveSmallImg;
	private boolean inOrOut=true;
	
	private SingleCarparkCarpark carpark;
	
	public List<SingleCarparkInOutHistory> getHavePlateNoList() {
		return havePlateNoList;
	}
	public void setHavePlateNoList(List<SingleCarparkInOutHistory> havePlateNoList) {
		this.havePlateNoList = havePlateNoList;
		if (pcs != null)
			pcs.firePropertyChange("havePlateNoList", null, null);
	}
	public void addHavePlateNoList(Set<SingleCarparkInOutHistory> havePlateNoList) {
		if (havePlateNoList==null) {
			this.havePlateNoList=new ArrayList<>();
		}
		this.havePlateNoList.addAll(havePlateNoList);
		if (pcs != null)
			pcs.firePropertyChange("havePlateNoList", null, null);
	}
	public List<SingleCarparkInOutHistory> getNoPlateNoList() {
		return noPlateNoList;
	}
	public void setNoPlateNoList(List<SingleCarparkInOutHistory> noPlateNoList) {
		this.noPlateNoList = noPlateNoList;
		if (pcs != null)
			pcs.firePropertyChange("noPlateNoList", null, null);
	}
	
	public void addHavePlateNoList(List<SingleCarparkInOutHistory> havePlateNoList) {
		this.havePlateNoList.addAll(havePlateNoList);
		if (pcs != null)
			pcs.firePropertyChange("havePlateNoList", null, null);
	}
	public String getPlateNo() {
		return plateNo;
	}
	public void setPlateNo(String plateNo) {
		this.plateNo = plateNo;
		if (pcs != null)
			pcs.firePropertyChange("plateNo", null, null);
	}
	public SingleCarparkInOutHistory getHavePlateNoSelect() {
		return havePlateNoSelect;
	}
	public void setHavePlateNoSelect(SingleCarparkInOutHistory havePlateNoSelect) {
		this.havePlateNoSelect = havePlateNoSelect;
		if (pcs != null)
			pcs.firePropertyChange("havePlateNoSelect", null, null);
	}
	public SingleCarparkInOutHistory getNoPlateNoSelect() {
		return noPlateNoSelect;
	}
	public void setNoPlateNoSelect(SingleCarparkInOutHistory noPlateNoSelect) {
		this.noPlateNoSelect = noPlateNoSelect;
		if (pcs != null)
			pcs.firePropertyChange("noPlateNoSelect", null, null);
	}
	public byte[] getBigImg() {
		return bigImg;
	}
	public void setBigImg(byte[] bigImg) {
		this.bigImg = bigImg;
		if (pcs != null)
			pcs.firePropertyChange("bigImg", null, null);
	}
	public byte[] getSmallImg() {
		return smallImg;
	}
	public void setSmallImg(byte[] smallImg) {
		this.smallImg = smallImg;
		if (pcs != null)
			pcs.firePropertyChange("smallImg", null, null);
	}
	public boolean isInOrOut() {
		return inOrOut;
	}
	public void setInOrOut(boolean inOrOut) {
		this.inOrOut = inOrOut;
		if (pcs != null)
			pcs.firePropertyChange("inOrOut", null, null);
	}
	public String getSaveBigImg() {
		return saveBigImg;
	}
	public void setSaveBigImg(String saveBigImg) {
		this.saveBigImg = saveBigImg;
		if (pcs != null)
			pcs.firePropertyChange("saveBigImg", null, null);
	}
	public String getSaveSmallImg() {
		return saveSmallImg;
	}
	public void setSaveSmallImg(String saveSmallImg) {
		this.saveSmallImg = saveSmallImg;
		if (pcs != null)
			pcs.firePropertyChange("saveSmallImg", null, null);
	}
	public SingleCarparkCarpark getCarpark() {
		return carpark;
	}
	public void setCarpark(SingleCarparkCarpark carpark) {
		this.carpark = carpark;
		if (pcs != null)
			pcs.firePropertyChange("carpark", null, null);
	}
}
