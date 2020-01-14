package com.donglu.carpark.ui.view.speed;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.swt.widgets.Composite;
import org.joda.time.DateTime;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.common.ImageDialog;
import com.donglu.carpark.ui.common.View;
import com.donglu.carpark.ui.view.speed.wizard.OverSpeedSettingWizard;
import com.donglu.carpark.util.ConstUtil;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.OverSpeedCar;
import com.dongluhitec.card.domain.db.singlecarpark.QueryParameter;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkBlackUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyUserPayHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemOperaLogTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
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
				sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.停车场, String.format("修改超速记录[%s-%s]为正常", car.getPlate(),car.getTimeLabel()), ConstUtil.getUserName());
			}
			Set<String> set = list.stream().map(e->e.getPlate()).collect(Collectors.toSet());
			for (String plate : set) {
				List<SingleCarparkUser> find = sp.getCarparkUserService().find(SingleCarparkUser.class, QueryParameter.eq(SingleCarparkUser.Property.plateNo.name(), plate));
				SingleCarparkBlackUser blackUser = sp.getCarparkService().findBlackUserByPlateNO(plate);
				if (blackUser!=null) {
					sp.getCarparkService().deleteBlackUser(blackUser);
				}
				if (!find.isEmpty()) {
					for (SingleCarparkUser user : find) {
						List<SingleCarparkMonthlyUserPayHistory> list2 = sp.getCarparkService().findMonthlyUserPayHistoryByCondition(0, 1, null, user.getPlateNo(), null, null, null, null);
						if (list2.isEmpty()) {
							continue;
						}
						user.setValidTo(list2.get(0).getOverdueTime());
						sp.getCarparkUserService().saveUser(user);
					}
				}
			}
			refresh();
		} catch (Exception e) {
			commonui.error("提示", "修改时发生错误", e);
		}
	}
}
