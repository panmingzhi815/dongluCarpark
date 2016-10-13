package com.donglu.carpark.ui.view.inouthistory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.jfree.data.general.DefaultKeyedValues2DDataset;
import org.joda.time.DateTime;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.common.AbstractPresenter;
import com.donglu.carpark.util.JfreeChartUtil;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class CountFlowPresenter  extends AbstractPresenter{
	Map<String, Long> mapInDeviceToCount=new HashMap<>();
	Map<String, Long> mapOutDeviceToCount=new HashMap<>();
	
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Inject
	CommonUIFacility commonui;
	
	public void countFlows(Date start, Date end, String type) {
		initDevice();
		createChar(start, end, type);
		getView().setImage();
	}

	private void createChar(Date start, Date end, String type) {
		createDayFlowsChar(start, end, type);
	}

	private void createDayFlowsChar(Date start, Date end, String type) {
		String pattern = "MM/dd";
		int timeType=0;
		int j = 2;
		switch (type) {
		case "天流量":
			timeType=Calendar.DAY_OF_MONTH;
			break;
		case "月流量":
			j=1;
			pattern="yyyy/MM";
			timeType=Calendar.MONTH;
			break;
		}
		DefaultKeyedValues2DDataset data = new DefaultKeyedValues2DDataset();
		DefaultKeyedValues2DDataset inOutData = new DefaultKeyedValues2DDataset();
		DefaultKeyedValues2DDataset totalData = new DefaultKeyedValues2DDataset();
		List<Date> listDate=cutDateTithBetween(start,end,timeType);
		System.out.println(listDate);
		for (int i = 0; i < listDate.size()-j; i++) {
			Long inFlows=0L;
			Long outFlows=0L;
			Map<String, Long> deviceFlows = sp.getCarparkInOutService().getDeviceFlows(true, listDate.get(i), listDate.get(i+1));
			String formatDate = StrUtil.formatDate(listDate.get(i+1), pattern);
			System.out.println(formatDate+"========"+deviceFlows);
			for (String device : mapInDeviceToCount.keySet()) {
				Long long1 = deviceFlows.get(device);
				if (long1==null) {
					long1=0L;
				}
				inFlows+=long1;
				data.addValue(long1, device, formatDate);
			}
			inOutData.addValue(inFlows, "进口", formatDate);
			deviceFlows = sp.getCarparkInOutService().getDeviceFlows(false, listDate.get(i), listDate.get(i+1));
			System.out.println(formatDate+"========"+deviceFlows);
			for (String device : mapOutDeviceToCount.keySet()) {
				Long long1 = deviceFlows.get(device);
				if (long1==null) {
					long1=0L;
				}
				outFlows+=long1;
				data.addValue(long1, device, formatDate);
			}
			inOutData.addValue(outFlows, "出口", formatDate);
			totalData.addValue(inFlows+outFlows, "总数", formatDate);
		}
		
		JfreeChartUtil.createTimeXYChar("设备进出流量", "日期", "车辆进出数量", data, "temp.png",getView().getImageSize().width,getView().getImageSize().height);
		JfreeChartUtil.createTimeXYChar("进出口流量", "日期", "车辆进出数量", inOutData, "temp1.png",getView().getImageSize().width,getView().getImageSize().height);
		JfreeChartUtil.createTimeXYChar("总流量", "日期", "车辆进出数量", totalData, "temp2.png",getView().getImageSize().width,getView().getImageSize().height);
	}

	/**
	 * 初始化所有设备名称
	 */
	public void initDevice() {
		List<String> listDevice=sp.getCarparkInOutService().findAllDeviceName(true);
		for (String string : listDevice) {
			mapInDeviceToCount.put(string, null);
		}
		listDevice=sp.getCarparkInOutService().findAllDeviceName(false);
		for (String string : listDevice) {
			mapOutDeviceToCount.put(string, null);
		}
	}
	@Override
	protected CountFlowView createView(Composite c) {
		return new CountFlowView(c, c.getStyle());
	}

	@Override
	public CountFlowView getView() {
		return (CountFlowView) super.getView();
	}
	public List<Date> cutDateTithBetween(Date start, Date end, int type) {
		List<Date> list =new ArrayList<>();
		list.add(start);
		int i=0;
		while(start.before(end)){
			DateTime dateTime = new DateTime(start);
			if (type==Calendar.DAY_OF_MONTH) {
				if (i==0) {
					start=StrUtil.getTodayBottomTime(start);
				}else
				start=dateTime.plusDays(1).toDate();
			}else if (type==Calendar.MONTH) {
				if (i==0) {
					start=StrUtil.getMonthBottomTime(start);
				}else
				start=dateTime.plusMonths(1).toDate();
			}
			if (start.before(end)) {
				list.add(start);
			}else{
				list.add(end);
			}
			i++;
		}
		return list;
	}

	public void saveImage(String img) {
		String selectToOpen = commonui.selectToOpen();
		if (StrUtil.isEmpty(selectToOpen)) {
			return;
		}
		String checkPath = StrUtil.checkPath(selectToOpen, new String[]{".jpeg",".png"},".png");
		try {
			Files.copy(Paths.get(img), Paths.get(checkPath),StandardCopyOption.REPLACE_EXISTING);
			commonui.info("成功", "保存成功");
		} catch (Exception e) {
			e.printStackTrace();
			commonui.error("提示", "保存失败");
		}
		
	}
	
}
