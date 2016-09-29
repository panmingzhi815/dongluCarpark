package com.donglu.carpark.ui.common;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import com.donglu.carpark.util.CarparkUtils;

public class TableSort extends ViewerSorter {
	
	private boolean sort;
	private String sortProperty;
	public TableSort(boolean sort,String sortProperty) {
		this.sort = sort;
		this.sortProperty = sortProperty;
	}
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		try {
			Object object = CarparkUtils.getFieldValueByName(sortProperty, e1);
			Object object1 = CarparkUtils.getFieldValueByName(sortProperty, e2);
			if (object!=null&&object1!=null) {
				String s=object==null?"":object.toString();
				String s1=object1==null?"":object1.toString();
				if (sort) {
					return s.compareTo(s1);
				}else{
					return s1.compareTo(s);
				}
			}else{
				return 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return super.compare(viewer, e1, e2);
	}
	
}
