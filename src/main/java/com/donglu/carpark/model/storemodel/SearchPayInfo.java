package com.donglu.carpark.model.storemodel;

import java.util.Date;


public class SearchPayInfo extends Info{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8280197706235698665L;
	private String storeName;
	private String operaName;
	private Date start;
	private Date end;
	private int page;

	private int rows;
	
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public String getOperaName() {
		return operaName;
	}
	public void setOperaName(String operaName) {
		this.operaName = operaName;
	}
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
