package com.donglu.carpark.model.storemodel;

import java.util.Date;


public class SearchFreeInfo extends Info{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8280197706235698665L;
	
	private Date start;
	private Date end;
	private String plateNO;
	private String used;
	private String storeName;

	private int page;

	private int rows;
	
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}
	public String getPlateNO() {
		return plateNO;
	}
	public void setPlateNO(String plateNO) {
		this.plateNO = plateNO;
	}
	public String getUsed() {
		return used;
	}
	public void setUsed(String used) {
		this.used = used;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public void setPage(int page) {
		this.page=page;
	}
	public void setRows(int rows) {
		this.rows=rows;
	}
	public int getPage() {
		return page;
	}
	public int getRows() {
		return rows;
	}
	
}
