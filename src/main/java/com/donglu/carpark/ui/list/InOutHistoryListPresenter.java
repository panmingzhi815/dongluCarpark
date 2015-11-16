package com.donglu.carpark.ui.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.server.servlet.ImageUploadServlet;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkInOutServiceI;
import com.donglu.carpark.ui.CarparkManageApp;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.wizard.InOutHistoryDetailWizard;
import com.donglu.carpark.util.ExcelImportExport;
import com.donglu.carpark.util.ExcelImportExportImpl;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.ui.util.FileUtils;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.inject.Inject;

public class InOutHistoryListPresenter extends AbstractListPresenter<SingleCarparkInOutHistory> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ImageUploadServlet.class);
	private InOutHistoryListView v;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Inject
	private CommonUIFacility commonui;

	private String plateNo;
	private String userName;
	private Date start;
	private Date end;
	private String operaName;
	private String carType;
	private String inout;
	private String inDevice;
	private String outDevice;
	private Long returnAccount;

	private SingleCarparkInOutHistory inOutHistory;
	private SingleCarparkCarpark carpark = new SingleCarparkCarpark();
	private String modifyPlateNO;

	@Override
	public void go(Composite c) {
		v = new InOutHistoryListView(c, c.getStyle());
		v.setPresenter(this);
	}

	public void refresh() {
		v.getModel().setList(new ArrayList<>());
		defaultSearch();

	}

	private void defaultSearch() {
		AbstractListView<SingleCarparkInOutHistory>.Model model = v.getModel();
		CarparkInOutServiceI carparkInOutService = sp.getCarparkInOutService();
		List<SingleCarparkInOutHistory> findByCondition = carparkInOutService.findByCondition(model.getList().size(), 500, plateNo, userName, carType, inout, start, end, operaName, inDevice,
				outDevice, returnAccount, carpark.getId());
		Long countByCondition = carparkInOutService.countByCondition(plateNo, userName, carType, inout, start, end, operaName, inDevice, outDevice, returnAccount, carpark.getId());
//		List<SingleCarparkInOutHistory> filter = filter(findByCondition, modifyPlateNO);
		model.setCountSearchAll(countByCondition.intValue());
		model.AddList(findByCondition);
		model.setCountSearch(model.getList().size());
		countMoney();
	}

	/**
	 * @param findByCondition
	 * @param modifyPlateNO
	 */
	public List<SingleCarparkInOutHistory> filter(List<SingleCarparkInOutHistory> findByCondition, String modifyPlateNO) {
		Collection<SingleCarparkInOutHistory> filter = Collections2.filter(findByCondition, new Predicate<SingleCarparkInOutHistory>() {

			@Override
			public boolean apply(SingleCarparkInOutHistory input) {
				if (modifyPlateNO.equals("全部")) {
					return true;
				}
				if (modifyPlateNO.equals("所有车牌")) {
					if (input.getPlateNo() == input.getInPlateNO() || input.getPlateNo() == input.getOutPlateNO()) {
						return true;
					}
				}
				if (modifyPlateNO.equals("进场车牌")) {
					if (input.getPlateNo() == input.getInPlateNO()) {
						return true;
					}
				}
				if (modifyPlateNO.equals("出场车牌")) {
					if (input.getPlateNo() == input.getInPlateNO()) {
						return true;
					}
				}
				return false;
			}
		});
		List<SingleCarparkInOutHistory> list = new ArrayList<>();
		list.addAll(filter);
		 v.setColumnWidth(new int[]{100,100,100,100,200,100,200,100,90,90,90,90,100,100});
		return list;
	}

	public void searchMore() {
		AbstractListView<SingleCarparkInOutHistory>.Model model = v.getModel();
		if (model.getCountSearchAll() <= model.getCountSearch()) {
			return;
		}
		defaultSearch();
	}

	public void search(String plateNo, String userName, Date start, Date end, String operaName, String carType, String inout, String inDevice, String outDevice, String returnAccount,
			SingleCarparkCarpark carpark, String modifyPlateNO) {
		this.plateNo = plateNo;
		this.userName = userName;
		this.start = start;
		this.end = end;
		this.operaName = operaName;
		this.carType = carType;
		this.inout = inout;
		this.inDevice = inDevice;
		this.outDevice = outDevice;
		try {
			Integer valueOf = Integer.valueOf(returnAccount);
			this.returnAccount = valueOf * 1L;
		} catch (NumberFormatException e) {
			this.returnAccount = null;
		}
		this.carpark = carpark;
		this.modifyPlateNO = modifyPlateNO;
		refresh();
	}

	public float[] countMoney() {
		List<SingleCarparkInOutHistory> list = v.getModel().getList();
		if (StrUtil.isEmpty(list)) {
			return null;
		}
		float should = 0;
		float fact = 0;
		float free = 0;
		for (SingleCarparkInOutHistory singleCarparkInOutHistory : list) {
			float i = singleCarparkInOutHistory.getShouldMoney() == null ? 0 : singleCarparkInOutHistory.getShouldMoney().floatValue();
			float j = singleCarparkInOutHistory.getFactMoney() == null ? 0 : singleCarparkInOutHistory.getFactMoney().floatValue();
			float k = singleCarparkInOutHistory.getFreeMoney() == null ? 0 : singleCarparkInOutHistory.getFreeMoney().floatValue();
			should += i;
			fact += j;
			free += k;
		}
		v.setMoney(should + "", fact + "", free + "");
		return new float[] { should, fact };
	}

	public void lookDetail() {
		try {
			if (StrUtil.isEmpty(v.getModel().getSelected())) {
				return;
			}
			SingleCarparkInOutHistory h = v.getModel().getSelected().get(0);
			InOutHistoryDetailWizard wizard = new InOutHistoryDetailWizard(h);
			inOutHistory = (SingleCarparkInOutHistory) commonui.showWizard(wizard);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public SingleCarparkInOutHistory getInOutHistory() {
		return inOutHistory;
	}

	@Override
	public void mouseDoubleClick(List<SingleCarparkInOutHistory> list) {
		try {
			SingleCarparkInOutHistory h = list.get(0);
			String file = (String) FileUtils.readObject(CarparkManageApp.CLIENT_IMAGE_SAVE_FILE_PATH);
			LOGGER.info("本地文件存放位置{}", file);
			InOutHistoryDetailWizard wizard = new InOutHistoryDetailWizard(h);
			inOutHistory = (SingleCarparkInOutHistory) commonui.showWizard(wizard);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void exportSearch() {
		List<SingleCarparkInOutHistory> list = v.getModel().getList();
		if (StrUtil.isEmpty(list)) {
			return;
		}
		String selectToSave = commonui.selectToSave();
		if (StrUtil.isEmpty(selectToSave)) {
			return;
		}
		String path = StrUtil.checkPath(selectToSave, new String[] { ".xls", ".xlsx" }, ".xls");
		String[] columnProperties = v.getColumnProperties();
		String[] nameProperties = v.getNameProperties();
		ExcelImportExport excelImportExport = new ExcelImportExportImpl();
		try {
			excelImportExport.export(path, nameProperties, columnProperties, list);
			commonui.info("操作成功", "导出成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
