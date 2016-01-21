package com.donglu.carpark.ui.view.inouthistory;

import java.util.ArrayList;
import java.util.List;

import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;

public class InOutHistoryModel extends DomainObject {
	
	private List<SingleCarparkCarpark> listCarpark=new ArrayList<>();
	private SingleCarparkCarpark selectCarpark;
	public List<SingleCarparkCarpark> getListCarpark() {
		return listCarpark;
	}
	public void setListCarpark(List<SingleCarparkCarpark> listCarpark) {
		this.listCarpark = listCarpark;
		if (pcs != null)
			pcs.firePropertyChange("listCarpark", null, null);
	}
	public SingleCarparkCarpark getSelectCarpark() {
		return selectCarpark;
	}
	public void setSelectCarpark(SingleCarparkCarpark selectCarpark) {
		this.selectCarpark = selectCarpark;
		if (pcs != null)
			pcs.firePropertyChange("selectCarpark", null, null);
	}
}
