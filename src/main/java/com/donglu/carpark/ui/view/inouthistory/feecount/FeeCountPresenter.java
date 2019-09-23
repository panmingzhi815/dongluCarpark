package com.donglu.carpark.ui.view.inouthistory.feecount;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.common.AbstractPresenter;
import com.donglu.carpark.ui.common.View;
import com.donglu.carpark.util.ExcelImportExportImpl;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class FeeCountPresenter extends AbstractPresenter {
	
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Inject
	private CommonUIFacility commonui;
	private List<Object[]> list;
	private FeeCountViewer v;
	private Date start;
	private Date end;
	private int type;
	private String userName;
	private Set<String> freeReasonType=new HashSet<>();

	@Override
	protected View createView(Composite c) {
		v = new FeeCountViewer(c);
		return v;
	}

	public List<Object[]> feeCount(Date start, Date end, int type,String userName) {
		start=StrUtil.getTodayTopTime(start);
		end=StrUtil.getTodayBottomTime(end);
		if (userName!=null&&"全部".equals(userName.trim())) {
			userName=null;
		}
		this.start = start;
		this.end = end;
		this.type = type;
		this.userName = userName;
		list = sp.getCarparkInOutService().countFeeBySearch(start,end,userName,type);
		return list;
	}
	
	public Map<String,Map<String,Map<String,Integer>>> countFreeReasonSize(){
		List<Object[]> listFreeSize=sp.getCarparkInOutService().countFreeBySearch(start,end,userName,type);
		freeReasonType.clear();
		Map<String,Map<String,Map<String,Integer>>> map=new HashMap<>();
		for (Object[] objects : listFreeSize) {
			System.out.println(Arrays.asList(objects));
			String time=objects[0].toString();
			String operaName=objects[1].toString();
			String freeReason=objects[2].toString();
			freeReason = freeReason.split("-")[0];
			Integer size=(Integer) objects[3];
			freeReasonType.add(freeReason);
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
		List<SingleCarparkSystemUser> list2 = sp.getSystemUserService().findAllSystemUser();
		List<String> collect = list2.stream().map(e->e.getUserName()).collect(Collectors.toList());
		collect.add(0,"全部");
		v.setUserNames(collect.toArray(new String[collect.size()]));
	}

	public void importData() {
		if (StrUtil.isEmpty(list)) {
			return;
		}
		String open = commonui.selectToOpen();
		if (open == null) {
			return;
		}
		try {
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
		Map<String, Map<String, Double[]>> map = new HashMap<>();
		Set<String> setName = new HashSet<>();
		for (Object[] objects : list) {
			if (objects[0] == null) {
				continue;
			}
			String name = String.valueOf(objects[0]);
			String date = String.valueOf(objects[1]);
			Double should=objects[2]==null?0:(Double) objects[2];
			Double fact=objects[3]==null?0:(Double) objects[3];
			Double online=objects[4]==null?0:(Double) objects[4];
			Double free=objects[5]==null?0:(Double) objects[5];
			System.out.println(name+"=="+date+"=="+should+"=="+fact+"=="+online+"=="+free);
			Map<String, Double[]> mapMoney = map.getOrDefault(date, new HashMap<>());
			mapMoney.put(name, new Double[] { should, fact - online, online, free });
			map.put(date, mapMoney);
			setName.add(name);
		}
		List<String> listName = new ArrayList<>(setName);
		ArrayList<String> timelist = new ArrayList<>(map.keySet());
		timelist.sort((s1, s2) -> s1.compareTo(s2));
		listName.sort((s1, s2) -> s1.compareTo(s2));
		List<Object> listTitle = new ArrayList<>();
		listTitle.add("时间");
		List<String> freeReasonTypeList = new ArrayList<>(freeReasonType);
		freeReasonTypeList.sort((s1, s2) -> s1.compareTo(s2));
		for (String name : listName) {
			listTitle.add(name + "现金");
			listTitle.add(name + "网上");
			for (String string : freeReasonTypeList) {
				listTitle.add(name + string);
			}
		}
		listTitle.add("现金合计");
		listTitle.add("网上合计");
		listTitle.add("收费合计");
		for (String string : freeReasonTypeList) {
			listTitle.add(string + "合计");
		}
		listTitle.add("免费车合计");
		listExcelData.add(listTitle);
		Map<String, Double[]> mapTotal = new HashMap<>();
		double total = 0;
		double total1 = 0;
		Map<String, Map<String, Map<String, Integer>>> freeReasonSize = countFreeReasonSize();
		Map<String, Long> mapReasonTotal = new HashMap<>();
		Map<String,Map<String, Long>> mapUserReasonTotal=new HashMap<>();
		for (String string : timelist) {
			Map<String, Double[]> map2 = map.get(string);
			List<Object> line = new ArrayList<>();
			line.add(string);
			double utotal = 0;
			double wtotal = 0;
			Map<String, Map<String, Integer>> userReasonMap = freeReasonSize.getOrDefault(string, new HashMap<>());
			Map<String, Long> mapDateReasonTotal = new HashMap<>();
			for (String string2 : listName) {
				Double[] dt = mapTotal.getOrDefault(string2, new Double[2]);
				double factTotal = dt[0] == null ? 0 : dt[0];
				double onlineTotel = dt[1] == null ? 0 : dt[1];
				Double[] doubles = map2.get(string2);
				if (doubles == null) {
					doubles = new Double[] { 0d, 0d, 0d, 0d };
				}
				factTotal += doubles[1];
				onlineTotel += doubles[2];
				utotal += doubles[1];
				wtotal += doubles[2];
				mapTotal.put(string2, new Double[] { factTotal, onlineTotel });
				line.add(String.valueOf(doubles[1]));
				line.add(String.valueOf(doubles[2]));
				// 免费车数量
				Map<String, Integer> reasonMap = userReasonMap.getOrDefault(string2, new HashMap<>());
				Map<String, Long> map3 = mapUserReasonTotal.getOrDefault(string2, new HashMap<>());
				for (String reason : freeReasonTypeList) {
					Integer size = reasonMap.getOrDefault(reason, 0);
					mapDateReasonTotal.put(reason, mapDateReasonTotal.getOrDefault(reason, 0l) + size);
					line.add(size);
					map3.put(reason, map3.getOrDefault(reason, 0l)+size);
				}
				mapUserReasonTotal.put(string2, map3);
			}
			total += utotal;
			total1 += wtotal;
			line.add(String.valueOf(utotal));
			line.add(String.valueOf(wtotal));
			line.add(String.valueOf(utotal + wtotal));
			Long ut = 0l;
			System.out.println(mapDateReasonTotal);
			for (String string2 : freeReasonTypeList) {
				Long orDefault = mapDateReasonTotal.getOrDefault(string2, 0l);
				ut += orDefault;
				line.add(String.valueOf(orDefault));
				mapReasonTotal.put(string2, mapReasonTotal.getOrDefault(string2, 0l) + orDefault);
			}
			System.out.println(mapReasonTotal);
			line.add(String.valueOf(ut));
			listExcelData.add(line);
		}
		List<Object> line = new ArrayList<>();
		line.add("总计");
		for (String string2 : listName) {
			Double[] dt = mapTotal.getOrDefault(string2, new Double[2]);
			double factTotal = dt[0] == null ? 0 : dt[0];
			double onlineTotel = dt[1] == null ? 0 : dt[1];
			line.add(String.valueOf(factTotal));
			line.add(String.valueOf(onlineTotel));
			Map<String, Long> map2 = mapUserReasonTotal.getOrDefault(string2, new HashMap<>());
			for (String string3 : freeReasonTypeList) {
				line.add(map2.getOrDefault(string3, 0l));
			}
		}
		line.add(String.valueOf(total));
		line.add(String.valueOf(total1));
		line.add(String.valueOf(total + total1));
		Long ut = 0l;
		for (String string2 : freeReasonTypeList) {
			Long orDefault = mapReasonTotal.getOrDefault(string2, 0l);
			ut += orDefault;
			line.add(String.valueOf(orDefault));
		}
		line.add(ut);
		listExcelData.add(line);
		List<Object> objects = listExcelData.get(0);
		ArrayList<List<Object>> list2 = new ArrayList<>(listExcelData);
		listExcelData.clear();
		for (int i = 0; i < objects.size(); i++) {
			List<Object> arrayList = new ArrayList<>();
			for (List<Object> list : list2) {
				arrayList.add(list.get(i));
			}
			System.out.println(arrayList);
			listExcelData.add(arrayList);
		}
		return listExcelData;
	}

	public Set<String> getFreeReasonType() {
		return freeReasonType;
	}

}
