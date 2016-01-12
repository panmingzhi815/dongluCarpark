package com.donglu.carpark.model;

import java.util.List;

import com.donglu.carpark.util.CarparkUtils;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.mapper.BeanUtil;
import com.google.common.collect.Lists;

public class ShowInOutHistoryModel extends SingleCarparkInOutHistory{
	
	private List<SingleCarparkInOutHistory> listSearch=Lists.newArrayList();
	private int countSearchAll;
	private int countSearch;
	private String nowPlateNo;
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
	public void setInfo(SingleCarparkInOutHistory ioh){
		String[] filedName = CarparkUtils.getFiledName(ioh);
		filedName[0]="id";
		BeanUtil.copyProperties(ioh, this, filedName);
		setNowPlateNo(ioh.getPlateNo());
	}
	public SingleCarparkInOutHistory getBean(){
		SingleCarparkInOutHistory ioh = new SingleCarparkInOutHistory();
		String[] filedName = CarparkUtils.getFiledName(ioh);
		filedName[0]="id";
		BeanUtil.copyProperties(this, ioh, filedName);
		return ioh;
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

	public String getNowPlateNo() {
		return nowPlateNo;
	}

	public void setNowPlateNo(String nowPlateNo) {
		this.nowPlateNo = nowPlateNo;
		if (pcs != null)
			pcs.firePropertyChange("nowPlateNo", null, null);
	}
	
	
}
