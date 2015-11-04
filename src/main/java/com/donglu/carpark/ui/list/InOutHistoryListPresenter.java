package com.donglu.carpark.ui.list;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Shell;
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
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.ui.util.FileUtils;
import com.google.inject.Inject;

public class InOutHistoryListPresenter  extends AbstractListPresenter<SingleCarparkInOutHistory>{
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
	@Override
	public void go(Composite c) {
		v=new InOutHistoryListView( c, c.getStyle());
		v.setPresenter(this);
	}
	public void refresh() {
		v.getModel().setList(new ArrayList<>());
		defaultSearch();
		
	}
	private void defaultSearch() {
		AbstractListView<SingleCarparkInOutHistory>.Model model = v.getModel();
		CarparkInOutServiceI carparkInOutService = sp.getCarparkInOutService();
		List<SingleCarparkInOutHistory> findByCondition = carparkInOutService.findByCondition(model.getList().size(), 500, plateNo,
				userName, carType, inout, start, end, operaName,inDevice,outDevice,returnAccount);
		Long countByCondition = carparkInOutService.countByCondition(plateNo, userName, carType, inout, start, end, operaName, inDevice, outDevice, returnAccount);
		
		model.setCountSearchAll(countByCondition.intValue());
		model.AddList(findByCondition);
		model.setCountSearch(model.getList().size());
	}
	public void searchMore() {
		AbstractListView<SingleCarparkInOutHistory>.Model model = v.getModel();
		if (model.getCountSearchAll()<=model.getCountSearch()) {
			return;
		}
		defaultSearch();
	}
	public void search(String plateNo, String userName, Date start, Date end, String operaName,
			String carType, String inout, String inDevice, String outDevice, String returnAccount) {
		this.plateNo=plateNo;
		this.userName=userName;
		this.start=start;
		this.end=end;
			this.operaName=operaName;
		this.carType=carType;
		this.inout=inout;
		this.inDevice=inDevice;
		this.outDevice=outDevice;
		try {
			Integer valueOf = Integer.valueOf(returnAccount);
			this.returnAccount=valueOf*1L;
		} catch (NumberFormatException e) {
			this.returnAccount=null;
		}
		refresh();		
	}
	public int[] countMoney() {
		List<SingleCarparkInOutHistory> list = v.getModel().getList();
		if (StrUtil.isEmpty(list)) {
			return null;
		}
		int should=0;
		int fact=0;
		for (SingleCarparkInOutHistory singleCarparkInOutHistory : list) {
			int i=singleCarparkInOutHistory.getShouldMoney()==null?0:singleCarparkInOutHistory.getShouldMoney().intValue();
			int j=singleCarparkInOutHistory.getFactMoney()==null?0:singleCarparkInOutHistory.getFactMoney().intValue();
			should+=i;
			fact+=j;
		}
		return new int[]{should,fact};
	}
	public void lookDetail() {
		
		try {
			if (StrUtil.isEmpty(v.getModel().getSelected())) {
				return;
			}
			SingleCarparkInOutHistory h =v.getModel().getSelected().get(0);
			
			
			
			String file = (String) FileUtils.readObject(CarparkManageApp.CLIENT_IMAGE_SAVE_FILE_PATH);
			
			InOutHistoryDetailWizard wizard =new InOutHistoryDetailWizard(h);
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
			SingleCarparkInOutHistory h =list.get(0);
			String file = (String) FileUtils.readObject(CarparkManageApp.CLIENT_IMAGE_SAVE_FILE_PATH);
			LOGGER.info("本地文件存放位置{}",file);
<<<<<<< HEAD

			InOutHistoryDetailWizard wizard =new InOutHistoryDetailWizard(h,
					file);
=======
			InOutHistoryDetailWizard wizard =new InOutHistoryDetailWizard(h);
>>>>>>> d5b5e3968adc0383bd903388fa809d02a6adf0d6
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
		String path = StrUtil.checkPath(selectToSave, new String[]{".xls",".xlsx"}, ".xls");
		String[] columnProperties = v.getColumnProperties();
		String[] nameProperties = v.getNameProperties();
		ExcelImportExport excelImportExport=new ExcelImportExportImpl();
		try {
			excelImportExport.export(path, nameProperties, columnProperties, list);
			commonui.info("操作成功", "导出成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
