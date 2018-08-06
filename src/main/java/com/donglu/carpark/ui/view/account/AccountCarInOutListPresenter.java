package com.donglu.carpark.ui.view.account;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.View;
import com.donglu.carpark.util.ExcelImportExportImpl;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.QueryParameter;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.ui.util.ProcessBarMonitor;
import com.google.inject.Inject;

public class AccountCarInOutListPresenter extends AbstractListPresenter<SingleCarparkInOutHistory> {
	private CarparkDatabaseServiceProvider sp;

	private String plateNo;
	private String name;
	private Date outTimeStart;
	private Date outTimeEnd;

	private CommonUIFacility commonui;
	
	@Inject
	public AccountCarInOutListPresenter(CarparkDatabaseServiceProvider sp,CommonUIFacility commonui) {
		this.sp = sp;
		this.commonui = commonui;
	}
	@Override
	protected View createView(Composite c) {
		return new AccountCarInOutListView(c);
	}
	@Override
	public AccountCarInOutListView getView() {
		return (AccountCarInOutListView) super.getView();
	}
	@Override
	protected int getTotalSize() {
		try {
			List<QueryParameter> parameter = getParameter();
			parameter.add(QueryParameter.countMutil(QueryParameter.rowCount(),QueryParameter.sum(SingleCarparkInOutHistory.Property.shouldMoney.name())));
			Object[] countMutil = sp.getCarparkUserService().countMutil(SingleCarparkInOutHistory.class, parameter);
			Long size=(Long) countMutil[0];
			Double d=(Double) countMutil[1];
			getView().setTotal(d);
			return size==null?0:size.intValue();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return sp.getCarparkUserService().count(SingleCarparkInOutHistory.class, getParameter()).intValue();
	}
	public List<QueryParameter> getParameter(){
		List<QueryParameter> asList = Arrays.asList(QueryParameter.isNotNull(SingleCarparkInOutHistory.Property.userName.name()),
				QueryParameter.gt(SingleCarparkInOutHistory.Property.shouldMoney.name(),0f),
				QueryParameter.like(SingleCarparkInOutHistory.Property.plateNo.name(), "%"+plateNo+"%"),
        		QueryParameter.like(SingleCarparkInOutHistory.Property.userName.name(), "%"+name+"%"),
        		QueryParameter.ge(SingleCarparkInOutHistory.Property.outTime.name(), outTimeStart),
        		QueryParameter.le(SingleCarparkInOutHistory.Property.outTime.name(), outTimeEnd),
        		QueryParameter.eq(SingleCarparkInOutHistory.Property.chargedType.name(), 2));
		return new ArrayList<>(asList);
	}
	@Override
	protected List<SingleCarparkInOutHistory> findListInput() {
		List<QueryParameter> parameter = getParameter();
		parameter.add(QueryParameter.firstResult(current));
		parameter.add(QueryParameter.maxResult(pageSize));
		return sp.getCarparkUserService().find(SingleCarparkInOutHistory.class, parameter);
	}
	public void search(String plateNo, String name, Date start, Date end) {
		this.plateNo = plateNo;
		this.name = name;
		outTimeStart = start;
		outTimeEnd = end;
		refresh();
	}
	public void export() {
		String path = commonui.selectToSave();
		if (path==null) {
			return;
		}
		String s=StrUtil.checkPath(path, new String[]{".xls",".xlsx"}, ".xls");
		final List<SingleCarparkInOutHistory> list = sp.getCarparkUserService().find(SingleCarparkInOutHistory.class, getParameter());
		final ProcessBarMonitor monitor = commonui.showProgressBar("导出数据", 0, list.size()).getMonitor();
		new Thread(new Runnable() {
			public void run() {
				try {
					float d=0;
					List<Object> arrayList = new ArrayList<>();
					for (SingleCarparkInOutHistory singleCarparkInOutHistory : list) {
						d+=singleCarparkInOutHistory.getShouldMoney();
						arrayList.add(singleCarparkInOutHistory);
					}
					Map<String, Object> e = new HashMap<>();
					e.put("outTimeLabel", "总金额");
					e.put("shouldMoney",d);
					arrayList.add(e);
					new ExcelImportExportImpl().export(s, new String[] { "车牌","名称","进场时间","出场时间","金额" }, new String[] {
							SingleCarparkInOutHistory.Property.plateNo.name(),
							SingleCarparkInOutHistory.Property.userName.name(),
							SingleCarparkInOutHistory.Label.inTimeLabel.name(),
							SingleCarparkInOutHistory.Label.outTimeLabel.name(),
							SingleCarparkInOutHistory.Property.shouldMoney.name(),
					}, arrayList, monitor);
					monitor.finish();
					commonui.info("提示", "导出完成");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
