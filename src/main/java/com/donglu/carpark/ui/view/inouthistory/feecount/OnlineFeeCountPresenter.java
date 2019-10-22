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

public class OnlineFeeCountPresenter extends AbstractPresenter {
	
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Inject
	private CommonUIFacility commonui;
	private List<Object[]> list;
	private OnlineFeeCountViewer v;
	private Date start;
	private Date end;
	private int type;
	private String userName="在线缴费";

	@Override
	protected View createView(Composite c) {
		v = new OnlineFeeCountViewer(c);
		return v;
	}

	public List<Object[]> feeCount(Date start, Date end, int type) {
		if (userName!=null&&"全部".equals(userName.trim())) {
			userName=null;
		}
		this.start = start;
		this.end = end;
		this.type = type;
		list = sp.getCarparkInOutService().countCarPayBySearch(start,end,userName,type);
		return list;
	}
	
	public Map<String,Map<String,Map<String,Integer>>> countFreeReasonSize(){
		List<Object[]> listFreeSize=sp.getCarparkInOutService().countCarPayBySearch(start,end,userName,type);
		Map<String,Map<String,Map<String,Integer>>> map=new HashMap<>();
		for (Object[] objects : listFreeSize) {
			String time=objects[0].toString();
			String operaName=objects[1].toString();
			String freeReason=objects[2].toString();
			freeReason = freeReason.split("-")[0];
			Integer size=(Integer) objects[3];
			Map<String, Map<String, Integer>> userMap = map.getOrDefault(time, new HashMap<>());
			Map<String, Integer> reasonMap = userMap.getOrDefault(operaName, new HashMap<>());
			reasonMap.put(freeReason, size);
			userMap.put(operaName, reasonMap);
			map.put(time, userMap);
		}
		return map;
	}
	
	@Override
	protected void continue_go() {
		
	}

	public void importData() {
		if (StrUtil.isEmpty(list)) {
			return;
		}
		String open = commonui.selectToSave();
		if (open == null) {
			return;
		}
		try {
			open = StrUtil.checkPath(open, new String[] {".xls",".xlsx"}, ".xls");
			List<List<Object>> listExcelData = getTableData();

			new ExcelImportExportImpl().export(open, new String[] {}, new String[] {}, listExcelData);
			commonui.info("提示", "导出成功");
		} catch (Exception e) {
			e.printStackTrace();
			commonui.info("失败", "导出失败," + e.getMessage());
		}
	}

	/**
	 * @return
	 */
	public List<List<Object>> getTableData() {
		List<List<Object>> listExcelData = new ArrayList<>();
		Map<String, Map<String, Double>> map = new HashMap<>();
		Set<String> setName = new HashSet<>();
		for (Object[] objects : list) {
			if (objects[0] == null) {
				continue;
			}
			String name = String.valueOf(objects[1]);
			String date = String.valueOf(objects[2]);
			Double online=objects[5]==null?0:(Double) objects[5];
//			System.out.println(name+"=="+date+"=="+should+"=="+fact+"=="+online+"=="+free);
			Map<String, Double> mapMoney = map.getOrDefault(date, new HashMap<>());
			if ("1".equals(name)) {
				name="支付宝";
			}else if("2".equals(name)) {
				name="微信";
			}
			mapMoney.put(name, online);
			map.put(date, mapMoney);
			setName.add(name);
		}
		List<String> listName = new ArrayList<>(setName);
		ArrayList<String> timelist = new ArrayList<>(map.keySet());
		timelist.sort((s1, s2) -> s1.compareTo(s2));
		listName.sort((s1, s2) -> s1.compareTo(s2));
		List<Object> listTitle = new ArrayList<>();
		listTitle.add("时间");
		for (String name : listName) {
			listTitle.add(name );
		}
		listTitle.add("网上合计");
		listExcelData.add(listTitle);
		double total = 0;
		Map<String,Double> mapTotal=new HashMap<>();
		for (String string : timelist) {
			Map<String, Double> map2 = map.get(string);
			List<Object> line = new ArrayList<>();
			line.add(string);
			double wtotal=0;
			for (String string2 : listName) {
				Double doubles = map2.getOrDefault(string2,0d);
				line.add(String.valueOf(doubles));
				total+=doubles;
				wtotal+=doubles;
				Double orDefault = mapTotal.getOrDefault(string2, 0d);
				mapTotal.put(string2, orDefault+doubles);
			}
			line.add(wtotal);
			listExcelData.add(line);
		}
		List<Object> line = new ArrayList<>();
		line.add("总计");
		for (String name : listName) {
			line.add(mapTotal.getOrDefault(name, 0d));
		}
		line.add(total);
		listExcelData.add(line);
		return listExcelData;
	}


}
