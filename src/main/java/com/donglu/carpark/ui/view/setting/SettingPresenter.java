package com.donglu.carpark.ui.view.setting;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkService;
import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.list.BlackUserListPresenter;
import com.donglu.carpark.ui.view.setting.wizard.DownloadPlateModel;
import com.donglu.carpark.ui.view.setting.wizard.DownloadPlateWizard;
import com.donglu.carpark.ui.wizard.holiday.AddYearHolidayModel;
import com.donglu.carpark.ui.wizard.holiday.AddYearHolidayWizard;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.Holiday;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemSetting;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemOperaLogTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.hardware.plateDevice.bean.PlateDownload;
import com.google.inject.Inject;

public class SettingPresenter implements Presenter {
	private Logger LOGGER=LoggerFactory.getLogger(SettingPresenter.class);
	private static final String OPERANAME = System.getProperty("userName");
	private SettingView view;
	@Inject
	private BlackUserListPresenter listPresenter;
	@Inject
	private CommonUIFacility commonui;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	
	private Map<SystemSettingTypeEnum, String> mapSystemSetting=new HashMap<>();

	@Override
	public void go(Composite c) {
		init();
		view = new SettingView(c, c.getStyle(),mapSystemSetting);
		view.setPresenter(this);
		listPresenter.go(view.getListComposite());
	}
	
	public void init() {
		List<SingleCarparkSystemSetting> findAllSystemSetting = sp.getCarparkService().findAllSystemSetting();
		for (SystemSettingTypeEnum type : SystemSettingTypeEnum.values()) {
			mapSystemSetting.put(type, type.getDefaultValue());
		}
		for (SingleCarparkSystemSetting singleCarparkSystemSetting : findAllSystemSetting) {
			mapSystemSetting.put(SystemSettingTypeEnum.valueOf(singleCarparkSystemSetting.getSettingKey()), singleCarparkSystemSetting.getSettingValue());
		}

	}
	public BlackUserListPresenter getListPresenter() {
		return listPresenter;
	}

	public void saveAll(Map<SystemSettingTypeEnum, String> mapSystemSetting,boolean isRestart) {
		boolean confirm = commonui.confirm("提示", "确定保存设置信息？");
		if (!confirm) {
			return;
		}
		CarparkService carparkService = sp.getCarparkService();
		for (SystemSettingTypeEnum t : mapSystemSetting.keySet()) {
			SingleCarparkSystemSetting h = new SingleCarparkSystemSetting();
			h.setSettingKey(t.name());
			h.setSettingValue(mapSystemSetting.get(t) == null ? t.getDefaultValue() : mapSystemSetting.get(t));
			carparkService.saveSystemSetting(h);
		}
		sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.参数设置, "保存了设置信息",OPERANAME);
		String s="请重启监控界面或等待监控界面5分钟后自动刷新！";
		if(isRestart) {
			s="重启监控界面后生效！！！";
		}
		commonui.info("成功", "保存设置成功。"+s);

	}

	public void setHoliday() {
		try {
			CarparkService carparkService = sp.getCarparkService();
			int year = Calendar.getInstance().get(Calendar.YEAR);
			List<Holiday> findHolidayByYear = carparkService.findHolidayByYear(year);
			List<Date> listDate = new ArrayList<>();
			for (Holiday h : findHolidayByYear) {

				listDate.add(h.getStart());
			}
			AddYearHolidayModel model = new AddYearHolidayModel();
			model.setSelect(listDate);
			model.setYear(year);
			AddYearHolidayWizard wizard = new AddYearHolidayWizard(model, sp, commonui);
			AddYearHolidayModel m = (AddYearHolidayModel) commonui.showWizard(wizard);
			if (StrUtil.isEmpty(m)) {
				return;
			}
			carparkService.deleteHoliday(findHolidayByYear);
			List<Date> select = m.getSelect();
			List<Holiday> list = new ArrayList<>();
			for (Date date : select) {
				System.out.println(StrUtil.formatDate(date, "yyyy-MM-dd"));
				Holiday holiday = new Holiday();
				holiday.setStart(date);
				holiday.setLength(1);
				list.add(holiday);
			}
			carparkService.saveHoliday(list);
			sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.参数设置, "修改节假日",OPERANAME);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void clearAllHistory() {
		try {
			boolean confirm = commonui.confirm("提示", "确定删除所有的进出场、充值、归账记录？");
			if (!confirm) {
				return;
			}
			sp.getCarparkInOutService().deleteAllHistory();
			sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.参数设置, "清除记录",OPERANAME);
			commonui.info("提示", "清除成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void cleanCarWithIn() {

		String input = commonui.input("场内车清理", "请输入需要清理停了多少天的场内车","30");
		if (input==null) {
			return;
		}
		Integer date;
		try {
			date = Integer.valueOf(input);
			if (date<=0) {
				commonui.info("提示", "天数不能小于1");
				cleanCarWithIn();
				return;
			}
		} catch (NumberFormatException e) {
			return;
		}
		

		boolean confirm = commonui.confirm("提示", "确认清理停了"+date+"天的场内车");
		if (!confirm) {
			return;
		}
		sp.getCarparkInOutService().clearCarHistoryWithInByDate(date);
		sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.参数设置, "清除场内车："+date,OPERANAME);
		commonui.info("提示", "清除成功");
	}

	public void backup(String path) {
		try {
			boolean confirm = commonui.confirm("提示", "是否确认备份数据库!!!");
			if (!confirm) {
				return;
			}
			boolean executeSQL = sp.getSettingService().backupDataBase(path);
			sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.参数设置, "备份数据库："+path,OPERANAME);
			if (executeSQL) {
				commonui.info("成功", "备份数据库到" + path + "成功");
			} else {
				commonui.error("失败", "备份数据库到" + path + "失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void restoreDataBase(String path) {
		try {
			boolean confirm = commonui.confirm("提示", "是否确认还原数据库!!!");
			if (!confirm) {
				return;
			}
			LOGGER.info("还原数据库");
			int restoreDataBase = sp.getSettingService().restoreDataBase(path);
			if (restoreDataBase == 0) {
				commonui.error("错误", "没有找到数据库备份文件");
				return;
			}
			if (restoreDataBase == 1) {
				commonui.error("还原失败", "还原数据库失败");
			}
			if (restoreDataBase == 2) {
				commonui.error("失败", "重新建立数据库连接失败，请重启程序");
			}
			sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.参数设置, "还原数据库："+restoreDataBase,OPERANAME);
			commonui.info("成功", "还原数据库成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 下载车牌信息到设备
	 */
	public void downloadPlate() {
		List<SingleCarparkCarpark> findAllCarpark = sp.getCarparkService().findAllCarpark();
		if(findAllCarpark.size()<=0){
			return;
		}
		DownloadPlateModel model = new DownloadPlateModel();
		model.setListCarpark(findAllCarpark);
		model.setCarpark(findAllCarpark.get(0));
		DownloadPlateWizard w=new DownloadPlateWizard(model,commonui);
		commonui.showWizard(w);
		sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.参数设置, "打开了车牌下载车牌界面,"+model.getMsg(),OPERANAME);
	}

	public String setFreeReson() {
		String input = commonui.input("免费原因", "请输入免费原因");
		if (StrUtil.isEmpty(input)) {
			return null;
		}
		return input;
	}

	public String getDatabaseFilePath() {
		String path = null;
		ServiceFileChoser fs=new ServiceFileChoser(view.getShell(), SWT.CLOSE);
		path=fs.open();
		return path;
	}

	public void search(String plate) {
		listPresenter.search(plate);
	}

}
