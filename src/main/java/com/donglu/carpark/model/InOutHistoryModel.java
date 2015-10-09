package com.donglu.carpark.model;

import java.util.List;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.google.common.collect.Lists;

public class InOutHistoryModel extends SingleCarparkInOutHistory{
	
	private List<SingleCarparkInOutHistory> listSearch=Lists.newArrayList();
	private int countSearchAll;
	private int countSearch;
	public List<SingleCarparkInOutHistory> getListSearch() {
		return listSearch;
	}

	public void setListSearch(List<SingleCarparkInOutHistory> listSearch) {
		this.listSearch = listSearch;
		if (pcs != null)
			pcs.firePropertyChange("listSearch", null, null);
	}

	public int getCountSearchAll() {
		return countSearchAll;
	}

	public void setCountSearchAll(int countSearchAll) {
		this.countSearchAll = countSearchAll;
		if (pcs != null)
			pcs.firePropertyChange("countSearchAll", null, null);
	}

	public int getCountSearch() {
		return countSearch;
	}

	public void setCountSearch(int countSearch) {
		this.countSearch = countSearch;
		if (pcs != null)
			pcs.firePropertyChange("countSearch", null, null);
	}

	public void addListSearch(List<SingleCarparkInOutHistory> listSearch) {
		this.listSearch.addAll(listSearch);
		if (pcs != null)
			pcs.firePropertyChange("listSearch", null, null);
		
	}
	
	
}
