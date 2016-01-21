package com.donglu.carpark.ui.view.inouthistory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.model.ShowInOutHistoryModel;
import com.donglu.carpark.server.servlet.ImageUploadServlet;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.wizard.InOutHistoryDetailWizard;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkLockCar;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class CarInListPresenter extends AbstractListPresenter<CarInInfo> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ImageUploadServlet.class);
	private CarInListView v;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Inject
	private CommonUIFacility commonui;

	private String plateNo;
	private Map<String, SingleCarparkLockCar> mapLockCar;
	private Map<String, SingleCarparkUser> mapUser;

	@Override
	public void go(Composite c) {
		v = new CarInListView(c, c.getStyle());
		v.setPresenter(this);
	}

	public void refresh() {
		v.getModel().setList(new ArrayList<>());
		defaultSearch();

	}

	private void defaultSearch() {
		List<SingleCarparkInOutHistory> findByNoOut = sp.getCarparkInOutService().findInOutHistoryByCarparkAndPlateNO(null, plateNo);
			List<SingleCarparkLockCar> findLockCar = sp.getCarparkInOutService().findLockCar(null, SingleCarparkLockCar.Status.已锁定.name(), null, null, null);
			mapLockCar = new HashMap<String, SingleCarparkLockCar>();
			for (SingleCarparkLockCar singleCarparkLockCar : findLockCar) {
				mapLockCar.put(singleCarparkLockCar.getPlateNO(), singleCarparkLockCar);
			} 
			List<SingleCarparkUser> findAll = sp.getCarparkUserService().findAll();
			mapUser = new HashMap<>();
			for (SingleCarparkUser singleCarparkUser : findAll) {
				mapUser.put(singleCarparkUser.getPlateNo(), singleCarparkUser);
			} 
		List<CarInInfo> list=new ArrayList<>();
		for (SingleCarparkInOutHistory io : findByNoOut) {
			CarInInfo info=new CarInInfo();
			info.setId(io.getId());
			String plateNo2 = io.getPlateNo();
			info.setPlateNO(plateNo2);
			info.setInTime(StrUtil.formatDate(io.getInTime(), StrUtil.DATETIME_PATTERN));
			SingleCarparkUser singleCarparkUser = mapUser.get(plateNo2);
			if (StrUtil.isEmpty(singleCarparkUser)) {
				info.setUserType("临时车");
			}else{
    			info.setUserName(singleCarparkUser.getName());
    			info.setUserType(singleCarparkUser.getType());
			}
			SingleCarparkLockCar singleCarparkLockCar = mapLockCar.get(plateNo2);
			if (StrUtil.isEmpty(singleCarparkLockCar)) {
			}else{
				info.setStatus(SingleCarparkLockCar.Status.已锁定.name());
			}
			list.add(info);
		}
		v.getModel().setList(list);
	}

	public void searchMore() {
		AbstractListView<CarInInfo>.Model model = v.getModel();
		if (model.getCountSearchAll() <= model.getCountSearch()) {
			return;
		}
		defaultSearch();
	}

	public void search(String plateNo) {
		this.plateNo = plateNo;
		refresh();
	}


	public void lookDetail() {
		

	}
	@Override
	public void mouseDoubleClick(List<CarInInfo> list) {
		try {
			if (StrUtil.isEmpty(list)) {
				return;
			}
			CarInInfo carInInfo = list.get(0);
			editPlateNO(carInInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void editPlateNO(CarInInfo carInInfo) {
		if (carInInfo==null) {
			List<CarInInfo> selected = v.getModel().getSelected();
			if (StrUtil.isEmpty(selected)) {
				return;
			}
			carInInfo=selected.get(0);
		}
		SingleCarparkInOutHistory findInOutById = sp.getCarparkInOutService().findInOutById(carInInfo.getId());
		if (StrUtil.isEmpty(findInOutById)||!StrUtil.isEmpty(findInOutById.getOutTime())) {
			commonui.info("", "记录不存在或已经出场");
			return;
		}
		ShowInOutHistoryModel model=new ShowInOutHistoryModel();
		model.setInfo(findInOutById);
		String plateNo2 = findInOutById.getPlateNo();
		model.setNowPlateNo(plateNo2);
		InOutHistoryDetailWizard wizard = new InOutHistoryDetailWizard(model, true, null, null);
		ShowInOutHistoryModel m = (ShowInOutHistoryModel) commonui.showWizard(wizard);
		if (StrUtil.isEmpty(m)) {
			return;
		}
		if (!m.getNowPlateNo().equals(plateNo2)) {
			findInOutById.setPlateNo(m.getNowPlateNo());
			sp.getCarparkInOutService().saveInOutHistory(findInOutById);
			refresh();
		}
	}

	public void locakCar() {
		try {
			List<CarInInfo> selected = v.getModel().getSelected();
			if (StrUtil.isEmpty(selected)) {
				return;
			}
			CarInInfo carInInfo = selected.get(0);
			SingleCarparkInOutHistory findInOutById = sp.getCarparkInOutService().findInOutById(carInInfo.getId());
			if (StrUtil.isEmpty(findInOutById)||!StrUtil.isEmpty(findInOutById.getOutTime())) {
				return;
			}
			if (StrUtil.isEmpty(carInInfo.getStatus())) {
				sp.getCarparkInOutService().lockCar(carInInfo.getPlateNO());
			}else{
				SingleCarparkLockCar findLockCarByPlateNO = sp.getCarparkInOutService().findLockCarByPlateNO(carInInfo.getPlateNO(),true);
				if (StrUtil.isEmpty(findLockCarByPlateNO)) {
					return;
				}
				findLockCarByPlateNO.setStatus(SingleCarparkLockCar.Status.已解锁.name());
				sp.getCarparkInOutService().saveLockCar(findLockCarByPlateNO);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		refresh();
	}
}
