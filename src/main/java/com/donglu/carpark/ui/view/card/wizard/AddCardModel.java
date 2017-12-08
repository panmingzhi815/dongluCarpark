package com.donglu.carpark.ui.view.card.wizard;

import java.util.ArrayList;
import java.util.List;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCard;

public class AddCardModel extends SingleCarparkCard {
	private List<SingleCarparkCard> list=new ArrayList<>();
	private SingleCarparkCard selected;
	
	public List<SingleCarparkCard> getList() {
		return list;
	}
	public void setList(List<SingleCarparkCard> list) {
		this.list = list;
		firePropertyChange("list", null, null);
	}
	public SingleCarparkCard getSelected() {
		return selected;
	}
	public void setSelected(SingleCarparkCard selected) {
		this.selected = selected;
		firePropertyChange("selected", null, null);
	}
	public void addCard(SingleCarparkCard singleCarparkCard) {
		list.add(singleCarparkCard);
		firePropertyChange("list", null, null);
	}
	public void removeCard(SingleCarparkCard selected) {
		list.remove(selected);
		firePropertyChange("list", null, null);
	}
	
}
