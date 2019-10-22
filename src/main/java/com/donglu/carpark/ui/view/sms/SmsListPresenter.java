package com.donglu.carpark.ui.view.sms;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.common.View;
import com.donglu.carpark.ui.view.speed.wizard.OverSpeedSettingWizard;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.OverSpeedCar;
import com.dongluhitec.card.domain.db.singlecarpark.SmsInfo;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class SmsListPresenter extends AbstractListPresenter<SmsInfo>{
	private SmsListView view;
	private Date start;
	private Date end;
	private String plateNo;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Inject
	private CommonUIFacility commonui;
	private int status;
	private String name;
	private String tel;

	@Override
	protected List<SmsInfo> findListInput() {
		return sp.getCarparkInOutService().findByMap(current, pageSize,SmsInfo.class, getMap());
	}

	/**
	 * @return
	 */
	public Map<String, Object> getMap() {
		Map<String, Object> map=new HashMap<String, Object>();
		if (!StrUtil.isEmpty(plateNo)) {
			map.put(SmsInfo.Property.plate.name() + "-like", "%" + plateNo + "%");
		}
		if (!StrUtil.isEmpty(name)) {
			map.put(SmsInfo.Property.userName.name() + "-like", "%" + name + "%");
		}
		if (!StrUtil.isEmpty(tel)) {
			map.put(SmsInfo.Property.tel.name() + "-like", "%" + tel + "%");
		}
		map.put(SmsInfo.Property.createTime.name()+"-ge", start);
		map.put(SmsInfo.Property.createTime.name()+"-le", end);
		if (status>0) {
			map.put(SmsInfo.Property.status.name(), status-1);
		}
		return map;
	}
	
	@Override
	protected int getTotalSize() {
		return sp.getCarparkInOutService().countByMap(SmsInfo.class,getMap()).intValue();
	}
	
	public void search(Date start, Date end, String plateNo, String name, String tel, int status) {
		this.start=start;
		this.end=end;
		this.plateNo = plateNo;
		this.name = name;
		this.tel = tel;
		this.status = status;
		refresh();
	}
	@Override
	protected View createView(Composite c) {
		view=new SmsListView(c,c.getStyle());
		view.setPresenter(this);
		view.setTableTitle("短信记录");
		return view;
	}

	public void setting() {
		OverSpeedSettingWizard w=new OverSpeedSettingWizard(sp);
		commonui.showWizard(w);
	}
	
}
