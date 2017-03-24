package com.donglu.carpark.ui.view.visitor.wizard;

import java.util.ArrayList;
import java.util.List;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkVisitor;
import com.dongluhitec.card.mapper.BeanUtil;

public class AddVisitorModel extends SingleCarparkVisitor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 345704052137142957L;
	private List<SingleCarparkCarpark> listCarpark=new ArrayList<>();
	
	private boolean isPrintDispatchNote=false;

	public List<SingleCarparkCarpark> getListCarpark() {
		return listCarpark;
	}

	public void setListCarpark(List<SingleCarparkCarpark> listCarpark) {
		this.listCarpark = listCarpark;
		firePropertyChange("listCarpark", null, null);
	}
	
	public void setVisitor(SingleCarparkVisitor visitor){
		BeanUtil.copyProperties(visitor, this, SingleCarparkVisitor.Property.values());
	}

	public SingleCarparkVisitor getVisitor() {
		SingleCarparkVisitor singleCarparkVisitor = new SingleCarparkVisitor();
		BeanUtil.copyProperties(this, singleCarparkVisitor, SingleCarparkVisitor.Property.values());
		return singleCarparkVisitor;
	}

	public boolean isPrintDispatchNote() {
		return isPrintDispatchNote;
	}

	public void setPrintDispatchNote(boolean isPrintDispatchNote) {
		this.isPrintDispatchNote = isPrintDispatchNote;
		firePropertyChange("isPrintDispatchNote", null, null);
	}
	
}
