package com.donglu.carpark.ui.list;

import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.common.ImageDialog;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkOpenDoorLog;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class OpenDoorListPresenter extends AbstractListPresenter<SingleCarparkOpenDoorLog>{
	private OpenDoorLogListView view;
	private String operaName;
	private Date start;
	private Date end;
	private String deviceName;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	private String plate;
	
	@Override
	protected List<SingleCarparkOpenDoorLog> findListInput() {
		return sp.getCarparkInOutService().findOpenDoorLogBySearch(current,pageSize,operaName,start,end,deviceName,plate);
	}
	
	@Override
	protected int getTotalSize() {
		return sp.getCarparkInOutService().countOpenDoorLogBySearch(operaName, start, end, deviceName, plate).intValue();
	}
	
	public void search(String operaName, Date start, Date end, String deviceName, String plate) {
		this.operaName=operaName;
		this.start=start;
		this.end=end;
		this.deviceName=deviceName;
		this.plate = plate;
		refresh();
	}
	@Override
	public void mouseDoubleClick(List<SingleCarparkOpenDoorLog> list) {
		if (StrUtil.isEmpty(list)) {
			return;
		}
		SingleCarparkOpenDoorLog singleCarparkOpenDoorLog = list.get(0);
		ImageDialog id=new ImageDialog(singleCarparkOpenDoorLog.getImage());
		id.open();
	}
	@Override
	protected View createView(Composite c) {
		view=new OpenDoorLogListView(c,c.getStyle());
		view.setPresenter(this);
		view.setTableTitle("手动抬杆记录");
		return view;
	}
}
