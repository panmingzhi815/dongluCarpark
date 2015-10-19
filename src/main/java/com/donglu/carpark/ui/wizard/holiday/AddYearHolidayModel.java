package com.donglu.carpark.ui.wizard.holiday;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dongluhitec.card.domain.BasicJavaBeanModel;
import com.dongluhitec.card.domain.db.Holiday;
import com.dongluhitec.card.domain.db.attendance.AttendanceHoliday;
import com.dongluhitec.card.ui.setting.settingwizard.AddMoreHolidayDate;

public class AddYearHolidayModel extends BasicJavaBeanModel {

	private List<AddMoreHolidayDate> all = new ArrayList<>();
	private List<Date> select=new ArrayList<Date>();
	private List<Holiday> allh=new ArrayList<Holiday>();
	private List<Integer> week=new ArrayList<Integer>();
	private int year;
	public List<AddMoreHolidayDate> getAll() {
	    return all;
	}

	public void setAll(List<AddMoreHolidayDate> all) {
	    pcs.firePropertyChange("all", this.all, this.all = all);
	}

	public List<Date> getSelect() {
	    return select;
	}
    public void setSelect(){
    	
    }

	private void addHoliday(Holiday h) {
		// TODO 自动生成的方法存根2015年5月29日，Michael
		Date date=h.getStart();
		int length=h.getLength();
		for (int i = 0; i < length; i++) {
			Calendar c=Calendar.getInstance();
			c.setTime(date);
			c.add(Calendar.DATE, i);
			select.add(c.getTime());
		}
		
	}

	public void setSelect(List<Date> select) {
		this.select.clear();
	    pcs.firePropertyChange("select", this.select, this.select = select);
	}
	public void addDate(Date d){
		this.select.add(d);
	}
	public List<Holiday> getAllh() {
	    return allh;
	}

	public void setAllh(List<Holiday> allh) {
		this.allh.clear();
	    pcs.firePropertyChange("allh", this.allh, this.allh = allh);
	}

	public List<Integer> getWeek() {
	    return week;
	}

	public void setWeek(List<Integer> week) {
	    pcs.firePropertyChange("week", this.week, this.week = week);
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		pcs.firePropertyChange("year", this.year, this.year = year);
	}
}
