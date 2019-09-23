package com.donglu.carpark.ui.view.speed;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.common.ImageDialog;
import com.donglu.carpark.ui.common.View;
import com.donglu.carpark.ui.view.speed.wizard.OverSpeedSettingWizard;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.OverSpeedCar;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class OverSpeedCarListPresenter extends AbstractListPresenter<OverSpeedCar>{
	private OverSpeedCarListView view;
	private Date start;
	private Date end;
	private String plateNo;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Inject
	private CommonUIFacility commonui;
	private int status;

	@Override
	protected List<OverSpeedCar> findListInput() {
		return sp.getCarparkInOutService().findOverSpeedCarByMap(current, pageSize, getMap());
	}

	/**
	 * @return
	 */
	public Map<String, Object> getMap() {
		Map<String, Object> map=new HashMap<String, Object>();
		if (!StrUtil.isEmpty(plateNo)) {
			map.put(OverSpeedCar.Property.plate.name() + "-like", "%" + plateNo + "%");
		}
		map.put(OverSpeedCar.Property.time.name()+"-ge", start);
		map.put(OverSpeedCar.Property.time.name()+"-le", end);
		if (status>0) {
			map.put(OverSpeedCar.Property.status.name(), status-1);
		}
		return map;
	}
	
	@Override
	protected int getTotalSize() {
		return sp.getCarparkInOutService().countOverSpeedCarByMap(getMap()).intValue();
	}
	
	public void search(Date start, Date end, String plateNo, int status) {
		this.start=start;
		this.end=end;
		this.plateNo = plateNo;
		this.status = status;
		refresh();
	}
	@Override
	public void mouseDoubleClick(List<OverSpeedCar> list) {
		if (StrUtil.isEmpty(list)) {
			return;
		}
		OverSpeedCar singleCarparkOpenDoorLog = list.get(0);
		ImageDialog id=new ImageDialog(singleCarparkOpenDoorLog.getImage());
		id.open();
	}
	@Override
	protected View createView(Composite c) {
		view=new OverSpeedCarListView(c,c.getStyle());
		view.setPresenter(this);
		view.setTableTitle("手动抬杆记录");
		return view;
	}

	public void setting() {
		OverSpeedSettingWizard w=new OverSpeedSettingWizard(sp);
		commonui.showWizard(w);
	}

	public void setNomal() {
		try {
			List<OverSpeedCar> list = view.getModel().getSelected();
			if (StrUtil.isEmpty(list)) {
				return;
			}
			boolean confirm = commonui.confirm("提示", "是否将选中的条"+list.size()+"记录设置为正常!");
			if (!confirm) {
				return;
			}
			for (OverSpeedCar car : list) {
				if (car.getStatus()==0) {
					continue;
				}
				car.setStatus(0);
				sp.getCarparkInOutService().saveOverSpeedCar(car);
			}
			refresh();
		} catch (Exception e) {
			commonui.error("提示", "修改时发生错误", e);
		}
	}
}
