package com.donglu.carpark.ui.view.inouthistory.wizard;

import java.util.ArrayList;
import java.util.List;

import com.dongluhitec.card.domain.db.singlecarpark.CarparkCarType;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkPlateCarType;

public class SetCarTypeModel extends CarparkPlateCarType {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8770145426777222033L;
	private List<CarparkCarType> list=new ArrayList<>();
	private CarparkCarType selected;
	public List<CarparkCarType> getList() {
		return list;
	}
	public void setList(List<CarparkCarType> list) {
		this.list = list;
	}
	public CarparkCarType getSelected() {
		return selected;
	}
	public void setSelected(CarparkCarType selected) {
		this.selected = selected;
	}
}
