package com.donglu.carpark.ui.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import com.donglu.carpark.util.CarparkUtils;

public class TableSort extends ViewerSorter {
	public static final Map<String,Comparator<Object>> mapComparator=new HashMap<>();
	
	private boolean sort;
	private String sortProperty;
	public TableSort(boolean sort,String sortProperty) {
		this.sort = sort;
		this.sortProperty = sortProperty;
	}
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (mapComparator.get(sortProperty)!=null) {
			if (sort) {
				return mapComparator.get(sortProperty).compare(e1, e2);
			}else{
				return mapComparator.get(sortProperty).compare(e2, e1);
			}
		}
		int compareTo = 0;
		try {
			Object object = CarparkUtils.getFieldValueByName(sortProperty, e1);
			Object object1 = CarparkUtils.getFieldValueByName(sortProperty, e2);
			if (isEmpty(object) && object1 != null) {
				compareTo = object1.toString().compareTo("");
			} else if (isEmpty(object1) && object != null) {
				compareTo = "".compareTo(object.toString());
			} else if(object != null&&object1 != null){
				String s = object == null ? "" : object.toString();
				String s1 = object1 == null ? "" : object1.toString();
				if ((object instanceof Float)&&(object1 instanceof Float)) {
					Float f = Float.valueOf(s);
					Float f1 = Float.valueOf(s1);
					if (sort) {
						compareTo = f.compareTo(f1);
					} else {
						compareTo = f1.compareTo(f);
					}
				} else if ((object instanceof Integer)&&(object1 instanceof Integer)) {
					Integer f = Integer.valueOf(s);
					Integer f1 = Integer.valueOf(s1);
					if (sort) {
						compareTo = f.compareTo(f1);
					} else {
						compareTo = f1.compareTo(f);
					}
				} else {
					if (sort) {
						compareTo = s.compareTo(s1);
					} else {
						compareTo = s1.compareTo(s);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return compareTo;
	}
	
	private boolean isEmpty(Object object) {
		return object==null||object.toString().equals("");
	}
	public static void main(String[] args) {
		List<String> asList = Arrays.asList(new String[]{null,"1","9","23",null,"5"});
		Collections.sort(asList, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				int flag = 0;
				if (o1 == null && o2 != null) {
					 return o2.compareTo("");
//					flag = "".compareTo(o2);
				} else if (o2 == null && o1 != null) {
//					 return o1.compareTo("");
					flag = "".compareTo(o1);
				} else {

					if (o1 == null) {
						o1 = "";
					}
					if (o2 == null) {
						o2 = "";
					}
					flag = o1.compareTo(o2);
				}
				System.out.println(flag+":"+o1+"====="+o2);
				return flag;
			}
		});
		System.out.println(asList);
	}
	
}
