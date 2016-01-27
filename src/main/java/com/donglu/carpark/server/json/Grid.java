package com.donglu.carpark.server.json;

import java.util.ArrayList;
import java.util.List;

/**
 * EasyUI DataGrid模型
 * 
 * @author 孙宇
 * 
 */
@SuppressWarnings("rawtypes")
public class Grid implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5091573712082586067L;
	private Long total = 0L;
	private List rows = new ArrayList();

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public List getRows() {
		return rows;
	}

	public void setRows(List rows) {
		this.rows = rows;
	}

}
