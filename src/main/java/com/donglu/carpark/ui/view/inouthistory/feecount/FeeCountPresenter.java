package com.donglu.carpark.ui.view.inouthistory.feecount;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.common.AbstractPresenter;
import com.donglu.carpark.ui.common.View;
import com.donglu.carpark.util.ExcelImportExportImpl;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class FeeCountPresenter extends AbstractPresenter {
	
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Inject
	private CommonUIFacility commonui;
	private List<Object[]> list;

	@Override
	protected View createView(Composite c) {
		return new FeeCountViewer(c);
	}

	public List<Object[]> feeCount(Date start, Date end, int type,String userName) {
		start=StrUtil.getTodayTopTime(start);
		end=StrUtil.getTodayBottomTime(end);
		list = sp.getCarparkInOutService().countFeeBySearch(start,end,userName,type);
		System.out.println(list);
		return list;
	}

	public void importData() {
		if (StrUtil.isEmpty(list)) {
			return;
		}
		String open = commonui.selectToOpen();
		if (open==null) {
			return;
		}
		List<List<Object>> listExcelData=new ArrayList<>();
		Map<String,Map<String,Double[]>> map=new HashMap<>();
		Set<String> setName=new HashSet<>();
		for (Object[] objects : list) {
			if (objects[0]==null) {
				continue;
			}
			String name = String.valueOf(objects[0]);
			String date = String.valueOf(objects[1]);
			Double should=(Double) objects[2];
			Double fact=(Double) objects[3];
			Double online=(Double) objects[4];
			Double free=(Double) objects[5];
			Map<String,Double[]> mapMoney=map.getOrDefault(date, new HashMap<>());
			mapMoney.put(name, new Double[] {should,fact-online,online,free});
			map.put(date, mapMoney);
			setName.add(name);
		}
		List<String> listName=new ArrayList<>(setName);
		ArrayList<String> timelist = new ArrayList<>(map.keySet());
		timelist.sort((s1,s2)->s1.compareTo(s2));
		listName.sort((s1,s2)->s1.compareTo(s2));
		List<Object> listTitle=new ArrayList<>();
		listTitle.add("时间");
		for (String name : listName) {
			listTitle.add(name+"现金");
			listTitle.add(name+"网上");
		}
		listTitle.add("现金合计");
		listTitle.add("网上合计");
		listTitle.add("合计");
		listExcelData.add(listTitle);
		Map<String,Double[]> mapTotal=new HashMap<>();
		double total=0;
		double total1=0;
		for (String string : timelist) {
			Map<String, Double[]> map2 = map.get(string);
			List<Object> line=new ArrayList<>();
			line.add(string);
			double utotal=0;
			double wtotal=0;
			for (String string2 : listName) {
				Double[] dt=mapTotal.getOrDefault(string2, new Double[2] );
				double factTotal=dt[0]==null?0:dt[0];
				double onlineTotel=dt[1]==null?0:dt[1];
				Double[] doubles = map2.get(string2);
				if (doubles==null) {
					doubles=new Double[] {0d,0d,0d,0d};
				}
				factTotal+=doubles[1];
				onlineTotel+=doubles[2];
				utotal+=doubles[1];
				wtotal+=doubles[2];
				mapTotal.put(string2, new Double[] {factTotal,onlineTotel});
				line.add( String.valueOf(doubles[1]));
				line.add( String.valueOf(doubles[2]));
			}
			total+=utotal;
			total1+=wtotal;
			line.add( String.valueOf(utotal));
			line.add(String.valueOf(wtotal));
			line.add(String.valueOf(utotal+wtotal));
			listExcelData.add(line);
		}
		List<Object> line=new ArrayList<>();
		line.add( "总计");
		for (String string2 : listName) {
			Double[] dt=mapTotal.getOrDefault(string2, new Double[2] );
			double factTotal=dt[0]==null?0:dt[0];
			double onlineTotel=dt[1]==null?0:dt[1];
			line.add( String.valueOf(factTotal));
			line.add( String.valueOf(onlineTotel));
		}
		line.add( String.valueOf(total));
		line.add(  String.valueOf(total1));
		line.add(  String.valueOf(total+total1));
		listExcelData.add(line);
		try {
			new ExcelImportExportImpl().export(open, new String[] {}, new String[] {}, listExcelData);
			commonui.info("提示", "导出成功");
		} catch (Exception e) {
			commonui.info("失败", "导出失败,"+e.getMessage());
		}
	}

}
