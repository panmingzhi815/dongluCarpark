package com.donglu.carpark.model.storemodel;
public class SearchCarInInfo extends Info{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8280197706235698665L;
	
	private String plateNO;
	private int page;
	private int rows;
	public String getPlateNO() {
		return plateNO;
	}
	public void setPlateNO(String plateNO) {
		this.plateNO = plateNO;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	
}
