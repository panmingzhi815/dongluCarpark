package com.donglu.carpark.ui.view.inouthistory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.model.ShowInOutHistoryModel;
import com.donglu.carpark.server.servlet.ImageUploadServlet;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkInOutServiceI;
import com.donglu.carpark.service.CarparkUserService;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.ShowDialog;
import com.donglu.carpark.ui.common.View;
import com.donglu.carpark.ui.view.inouthistory.feecount.FeeCountPresenter;
import com.donglu.carpark.ui.view.inouthistory.wizard.SetCarTypeModel;
import com.donglu.carpark.ui.view.inouthistory.wizard.SetCarTypeWizard;
import com.donglu.carpark.ui.wizard.InOutHistoryDetailWizard;
import com.donglu.carpark.util.ExcelImportExport;
import com.donglu.carpark.util.ExcelImportExport.ExcelImportExportData;
import com.donglu.carpark.util.ExcelImportExportImpl;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.common.ui.CommonUIFacility.Progress;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkCarType;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkPlateCarType;
import com.dongluhitec.card.domain.db.singlecarpark.QueryParameter;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.ui.util.ProcessBarMonitor;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.inject.Inject;

public class InOutHistoryListPresenter extends AbstractListPresenter<SingleCarparkInOutHistory> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ImageUploadServlet.class);
	private InOutHistoryListView v;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Inject
	private CommonUIFacility commonui;
	@Inject
	private CountFlowPresenter countFlowPresenter;
	@Inject
	private FeeCountPresenter feeCountPresenter;

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
	@SuppressWarnings("unused")
	private String modifyPlateNO;
	private float[] shouldMoney;
	private Date outStart;
	private Date outEnd;

	@Override
	public void refresh() {
		v.getModel().setList(new ArrayList<>());
		defaultSearch();
		CarparkInOutServiceI carparkInOutService = sp.getCarparkInOutService();
		Map<String,Integer> map=carparkInOutService.countFreeSize(plateNo, userName, carType, inout, start, end, outStart, outEnd, operaName, inDevice, outDevice, returnAccount, carpark.getId(),
				shouldMoney);
		if (map.keySet().size()>0) {
			String s="免费车：    ";
			for (String string : map.keySet().stream().sorted((s1,s2)->{return s1.compareTo(s2);}).collect(Collectors.toList())) {
				s+=string+"："+Strings.padEnd(String.valueOf(map.get(string)), 9, ' ');
			}
			v.setFreeSizeLabel(s);
		}else {
			v.setFreeSizeLabel("");
		}
		float[] countMoney = sp.getCarparkInOutService().countMoney(plateNo, userName, carType, inout, start, end, outStart, outEnd, operaName, inDevice, outDevice, returnAccount, carpark.getId(),
				shouldMoney);
//		System.out.println(countMoney[0]+"="+countMoney[1]+"="+countMoney[2]+"="+countMoney[3]);
		v.setMoney(countMoney[0] + "", (countMoney[1]-countMoney[2]) + "", countMoney[3] + "", countMoney[2] + "");
	}

	private void defaultSearch() {
		AbstractListView<SingleCarparkInOutHistory>.Model model = v.getModel();
		CarparkInOutServiceI carparkInOutService = sp.getCarparkInOutService();
		List<SingleCarparkInOutHistory> findByCondition = carparkInOutService.findByCondition(model.getList().size(), 500, plateNo, userName, carType, inout, start, end, outStart, outEnd, operaName,
				inDevice, outDevice, returnAccount, carpark.getId(), shouldMoney);
		Long countByCondition = carparkInOutService.countByCondition(plateNo, userName, carType, inout, start, end, outStart, outEnd, operaName, inDevice, outDevice, returnAccount, carpark.getId(),
				shouldMoney);
		// List<SingleCarparkInOutHistory> filter = filter(findByCondition, modifyPlateNO);
		// for (SingleCarparkInOutHistory singleCarparkInOutHistory : findByCondition) {
		// if(singleCarparkInOutHistory.getPlateNo().equals("粤WZR220")){
		// System.out.println(singleCarparkInOutHistory.getPlateNo()+"====="+singleCarparkInOutHistory.getInTime());
		// }
		// }
		// System.out.println(findByCondition.size());
		model.setCountSearchAll(countByCondition.intValue());
		model.AddList(findByCondition);
		model.setCountSearch(model.getList().size());
		
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
		v.setColumnWidth(new int[] { 100, 100, 100, 100, 200, 100, 200, 100, 90, 90, 90, 90, 100, 100 });
		return list;
	}

	public void searchMore() {
		AbstractListView<SingleCarparkInOutHistory>.Model model = v.getModel();
		if (model.getCountSearchAll() <= model.getCountSearch()) {
			return;
		}
		defaultSearch();
	}

	public void search(String plateNo, String userName, Date start, Date end, Date outStart, Date outEnd, String operaName, String carType, String inout, String inDevice, String outDevice,
			String returnAccount, SingleCarparkCarpark carpark, float... shouldMoney) {
		this.plateNo = plateNo;
		this.userName = userName;
		this.start = start;
		this.end = end;
		this.outStart = outStart;
		this.outEnd = outEnd;
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
		this.shouldMoney = shouldMoney;
		refresh();
	}

	public float[] countMoney(List<SingleCarparkInOutHistory> list) {
		if (StrUtil.isEmpty(list)) {
			return null;
		}
		float should = 0;
		float fact = 0;
		float free = 0;
		float online = 0;
		for (SingleCarparkInOutHistory singleCarparkInOutHistory : list) {
			String remarkString = singleCarparkInOutHistory.getRemarkString();
			float i = singleCarparkInOutHistory.getShouldMoney() == null ? 0 : singleCarparkInOutHistory.getShouldMoney().floatValue();
			float j = singleCarparkInOutHistory.getFactMoney() == null ? 0 : singleCarparkInOutHistory.getFactMoney().floatValue();
			float k = singleCarparkInOutHistory.getFreeMoney() == null ? 0 : singleCarparkInOutHistory.getFreeMoney().floatValue();
			should += i;
			if (remarkString != null && (remarkString.contains("缴费完成") || remarkString.contains("扫码缴费出场"))) {
				online += j;
			} else {
				if (singleCarparkInOutHistory.getOnlineMoney()>0) {
					online+=singleCarparkInOutHistory.getOnlineMoney();
					j-=singleCarparkInOutHistory.getOnlineMoney();
				}
				fact += j;
			}
			free += k;
		}
		return new float[] { should, fact, online, free };
	}

	public void lookDetail() {
		try {
			if (StrUtil.isEmpty(v.getModel().getSelected())) {
				return;
			}
			SingleCarparkInOutHistory h = v.getModel().getSelected().get(0);
			ShowInOutHistoryModel model = new ShowInOutHistoryModel();
			model.setInfo(h);
			InOutHistoryDetailWizard wizard = new InOutHistoryDetailWizard(model);
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
			ShowInOutHistoryModel model = new ShowInOutHistoryModel();
			model.setInfo(h);
			InOutHistoryDetailWizard wizard = new InOutHistoryDetailWizard(model);
			inOutHistory = (SingleCarparkInOutHistory) commonui.showWizard(wizard);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void exportSearch() {
		Integer countSearchAll = v.getModel().getCountSearchAll();
		if (countSearchAll <= 0) {
			return;
		}
		boolean exportCurtenRecord = false;
		boolean confirm = commonui.confirm("提示", "是否导出满足查询条件的" + countSearchAll + "条车辆进出场记录？");
		if (!confirm) {
			countSearchAll = v.getModel().getCountSearch();
			confirm = commonui.confirm("提示", "是否导出界面中" + countSearchAll + "条车辆进出场记录？");
			if (!confirm) {
				return;
			}
			exportCurtenRecord = true;
		}
		String selectToSave = commonui.selectToSave();
		if (StrUtil.isEmpty(selectToSave)) {
			return;
		}
		Progress showProgressBar = commonui.showProgressBar("导出" + countSearchAll + "条进出记录!", 0, countSearchAll);
		final ProcessBarMonitor monitor = showProgressBar.getMonitor();
		boolean exportType = exportCurtenRecord;
		int waitReportSize = countSearchAll.intValue();
		new Thread(new Runnable() {
			public void run() {
				monitor.showMessage("准备从数据库中获取数据！");
				try {
					CarparkInOutServiceI carparkInOutService = sp.getCarparkInOutService();
					List<SingleCarparkInOutHistory> list = new ArrayList<>();
//					if (!exportType) {
//						while (true) {
//							List<SingleCarparkInOutHistory> find = carparkInOutService.findByCondition(list.size(), 500, plateNo, userName, carType, inout, start, end, outStart, outEnd, operaName,
//									inDevice, outDevice, returnAccount, carpark.getId(), shouldMoney);
//							list.addAll(find);
//							monitor.showMessage("已加载" + list.size() + "条数据");
//							if (find.size() < 500) {
//								break;
//							}
//						}
//					} else {
//						list.addAll(v.getModel().getList());
//					}
//					if (list.isEmpty()) {
//						return;
//					}
//					float[] countMoney = countMoney(list);
//					if (countMoney != null && countMoney.length >= 4) {
//						SingleCarparkInOutHistory history = new SingleCarparkInOutHistory();
//						history.setOperaName("总计:");
//						history.setShouldMoney(countMoney[0]);
//						history.setFactMoney(countMoney[1] + countMoney[2]);
//						history.setFreeMoney(countMoney[3]);
//						history.setFreeReason("现金:" + countMoney[1] + "网上:" + countMoney[2]);
//						list.add(history);
//					}
					String path = StrUtil.checkPath(selectToSave, new String[] { ".xls", ".xlsx" }, ".xls");
					String[] columnProperties = v.getColumnProperties();
					String[] nameProperties = v.getNameProperties();
					ExcelImportExport excelImportExport = new ExcelImportExportImpl();
					if (monitor.isDispose()) {
						throw new Exception("导出已终止！");
					}
					// excelImportExport.export(path, nameProperties, columnProperties, list,
					// monitor);
					AtomicInteger totalSize = new AtomicInteger(0);
					ExcelImportExportData<SingleCarparkInOutHistory> excelImportExportData = new ExcelImportExport.ExcelImportExportData<SingleCarparkInOutHistory>() {
						float[] f = new float[4];

						@Override
						public List<SingleCarparkInOutHistory> getData(int current, int size) {
							if (!exportType) {
								list.clear();
								LOGGER.info("正在加载第{}-{}条数据", current, size);
								CarparkInOutServiceI carparkInOutService = sp.getCarparkInOutService();
								while (true) {
									List<SingleCarparkInOutHistory> find = carparkInOutService.findByCondition(list.size() + current, Math.min(size - list.size(), 500), plateNo, userName, carType,
											inout, start, end, outStart, outEnd, operaName, inDevice, outDevice, returnAccount, carpark.getId(), shouldMoney);
									list.addAll(find);
									if (monitor.isDispose()) {
										throw new RuntimeException("用户取消导出");
									}
									monitor.showMessage("已加载" + (list.size() + current) + "条数据");
									if (find.size() < 500 || list.size() >= size) {
										break;
									}
								}
								
								if (!StrUtil.isEmpty(list)) {
									f = carparkInOutService.countMoney( plateNo, userName, carType,
											inout, start, end, outStart, outEnd, operaName, inDevice, outDevice, returnAccount, carpark.getId(), shouldMoney);
									totalSize.addAndGet(list.size());
									if (totalSize.get() == waitReportSize) {
										SingleCarparkInOutHistory history = new SingleCarparkInOutHistory();
										history.setOperaName("总计:");
										history.setShouldMoney(f[0]);
										history.setFactMoney(f[1] - f[2]);
										history.setFreeMoney(f[3]);
										history.setFreeReason("现金:" + history.getFactMoney() + "网上:" + f[2]);
										list.add(history);
									}
								}
								return list;
							} else {
								List<SingleCarparkInOutHistory> list = v.getModel().getList();
								if (current >= list.size()) {
									return new ArrayList<>();
								}
								return list;
							}
						}
					};
					// excelImportExport.export(path, nameProperties, columnProperties,excelImportExportData, monitor);
					excelImportExport.exportInOutHistory(path, waitReportSize, excelImportExportData, monitor);
					commonui.info("操作成功", "导出成功");
					LOGGER.info("导出进出场记录成功");
				} catch (Throwable e) {
					commonui.info("操作失败", "操作失败" + e.getMessage());
					LOGGER.info("导出进出场记录失败", e);
				} finally {
					if (!monitor.isDispose()) {
						monitor.finish();
					}
				}
			}
		}).start();

	}

	public void flowStatistics() {
		ShowDialog d = new ShowDialog("报表统计");
		d.setPresenter(countFlowPresenter);
		d.setHaveButon(false);
		d.setSize(600, 600);
		d.open();
	}

	@Override
	protected View createView(Composite c) {
		v = new InOutHistoryListView(c, c.getStyle());
		return v;
	}

	public void setCarType() {
		List<SingleCarparkInOutHistory> list = v.getModel().getSelected();
		if (StrUtil.isEmpty(list)) {
			return;
		}

		try {
			List<CarparkCarType> carparkCarTypeList = sp.getCarparkService().getCarparkCarTypeList();
			SingleCarparkInOutHistory history = list.get(0);
			CarparkUserService carparkUserService = sp.getCarparkUserService();
			List<CarparkPlateCarType> find = carparkUserService.find(CarparkPlateCarType.class, QueryParameter.eq(CarparkPlateCarType.Property.plate.name(), history.getPlateNo()));
			CarparkCarType carType = carparkCarTypeList.get(0);
			if (!StrUtil.isEmpty(find)) {
				CarparkPlateCarType carparkPlateCarType = find.get(0);
				for (CarparkCarType carparkCarType : carparkCarTypeList) {
					if (carparkPlateCarType.getCarType().equals(carparkCarType.getName())) {
						carType = carparkCarType;
						break;
					}
				}
			}
			SetCarTypeModel model = new SetCarTypeModel();
			model.setSelected(carType);
			model.setList(carparkCarTypeList);
			SetCarTypeWizard setCarTypeWizard = new SetCarTypeWizard(model);
			Object showWizard = commonui.showWizard(setCarTypeWizard);
			if (showWizard == null) {
				return;
			}
			for (String plateNo : list.stream().map(t -> t.getPlateNo()).collect(Collectors.toSet())) {
				List<CarparkPlateCarType> find2 = carparkUserService.find(CarparkPlateCarType.class, QueryParameter.eq(CarparkPlateCarType.Property.plate.name(), plateNo));
				CarparkPlateCarType c = new CarparkPlateCarType();
				if (!StrUtil.isEmpty(find2)) {
					c = find2.get(0);
				}
				c.setPlate(plateNo);
				c.setCarType(model.getSelected().getName());
				c.setTid(model.getSelected().getTid());
				carparkUserService.save(c);
			}
			commonui.info("提示", "设置成功");
		} catch (Exception e) {
			e.printStackTrace();
			commonui.error("提示", "设置失败" + e);
		}
	}

	public void print() {
		List<SingleCarparkInOutHistory> list = v.getModel().getSelected();
		if (StrUtil.isEmpty(list)) {
			return;
		}
		try {
    		SingleCarparkInOutHistory singleCarparkInOutHistory=list.get(0);
    		if(singleCarparkInOutHistory.getOutTime()==null||singleCarparkInOutHistory.getFactMoney()<=0) {
    			commonui.info("提示", "只能打印实收金额大于0的记录");
    			return;
    		}
    		ExcelImportExportImpl excelImportExportImpl = new ExcelImportExportImpl();
    		String path = excelImportExportImpl.exportChargeInfo(singleCarparkInOutHistory);
			excelImportExportImpl.printExcel(path);
			commonui.info("成功","已发送打印命令");
		} catch (Exception e) {
			e.printStackTrace();
			commonui.info("错误","打印失败"+e);
		}
	}

	public void handOut() {
		List<SingleCarparkInOutHistory> list = v.getModel().getSelected();
		if (StrUtil.isEmpty(list)) {
			return;
		}
		try {
    		SingleCarparkInOutHistory singleCarparkInOutHistory=list.get(0);
    		boolean confirm = commonui.confirm("确认提示", "是否要手动放车辆：["+singleCarparkInOutHistory.getPlateNo()+"]出场");
    		if (!confirm) {
				return;
			}
    		singleCarparkInOutHistory.setOutTime(new Date());
    		singleCarparkInOutHistory.setRemarkString("管理员手动出场");
    		singleCarparkInOutHistory.setShouldMoney(0f);
    		singleCarparkInOutHistory.setFactMoney(0);
    		singleCarparkInOutHistory.setFreeMoney(0);
    		sp.getCarparkInOutService().saveInOutHistory(singleCarparkInOutHistory);
    		commonui.info("提示", "出场成功");
    		refresh();
		} catch (Exception e) {
			e.printStackTrace();
			commonui.info("错误","手动出场失败"+e);
		}
	}

	public void feeCount() {
		ShowDialog d = new ShowDialog("报表统计");
		d.setPresenter(feeCountPresenter);
		d.setHaveButon(false);
		d.setSize(1280, 960);
		d.open();
	}
}
