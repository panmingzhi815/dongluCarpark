package com.donglu.carpark.ui.view;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.joda.time.DateTime;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.CarparkMainPresenter;
import com.donglu.carpark.ui.common.App;
import com.donglu.carpark.ui.keybord.KeySetting;
import com.donglu.carpark.ui.keybord.KeySetting.KeyReleaseTypeEnum;
import com.donglu.carpark.ui.task.CarOutTask;
import com.donglu.carpark.ui.view.hand.HandSearchPresenter;
import com.donglu.carpark.ui.view.message.MessageUtil;
import com.donglu.carpark.ui.wizard.PresenterWizard;
import com.donglu.carpark.util.CarparkUtils;
import com.donglu.carpark.util.ConstUtil;
import com.donglu.carpark.util.ImageUtils;
import com.donglu.carpark.util.InjectorUtil;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.db.singlecarpark.CarCheckHistory;
import com.dongluhitec.card.domain.db.singlecarpark.DeviceVoiceTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.ScreenTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;

import lombok.extern.slf4j.Slf4j;

import org.eclipse.swt.widgets.Table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.widgets.Combo;

@Slf4j
public class CarChargeApp implements App {
	public class Model extends DomainObject{
		private static final long serialVersionUID = 1L;
		private List<SingleCarparkInOutHistory> list=new ArrayList<>();

		public List<SingleCarparkInOutHistory> getList() {
			return list;
		}

		public void setList(List<SingleCarparkInOutHistory> list) {
			this.list = list;
			firePropertyChange("list", null, null);
		}
	}

	protected Shell shell;
	private Text txt_plate;
	private Text txt_inTime;
	private Text txt_startTime;
	private Text txt_endTime;
	private Text txt_should;
	private Table table;
	private Text txt_fact;
	private Text txt_stillTime;
	
	private SingleCarparkUser user;
	private SingleCarparkInOutHistory cch;
	private Text txt_userName;
	private Text txt_slotNo;
	private Text txt_carparkName;
	private Text txt_userPlate;
	private Text txt_valid;
	private CarOutTask task;
	private CarparkDatabaseServiceProvider sp;
	
	private Model model=new Model();
	private TableViewer tableViewer;
	private CLabel lbl_outImage;
	private CLabel lbl_inImage;
	private Text txt_paid;
	private CarCheckHistory cc;
	
	private boolean open=true;
	private Combo combo_carType;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = Display.getDefault();
		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
			public void run() {
				try {
					CarChargeApp window = new CarChargeApp();
					window.open();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public CarChargeApp() {
	}
	public CarChargeApp(CarOutTask task) {
		this.task = task;
		sp = task.getSp();
		user=task.getUser();
		cch = task.getCch();
	}
	/**
	 * Open the window.
	 */
	public void open() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				createContents();
				shell.open();
				shell.layout();
			}
		});
	}

	/**
	 * Create contents of the window.
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		shell = new Shell(task.getPresenter().getView().getShell(),SWT.TOP|SWT.CLOSE);
		shell.setSize(996, 660);
		shell.setText("停车收费");
		shell.setLayout(new GridLayout(1, false));
		shell.setImage(JFaceUtil.getImage("donglu"));
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				try {
					log.info("用户取消确认");
					cc.setStatus("取消确认");
					sp.getCarparkInOutService().saveEntity(cc);
					task.getPresenter().getModel().getMapInOutWindow().remove(task.getPlateNO());
					task.getPresenter().refreshCarCheck();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tabFolder.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("车辆收费信息");
		
		Composite composite_2 = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(composite_2);
		composite_2.setLayout(new GridLayout(2, false));
		
		Composite composite = new Composite(composite_2, SWT.NONE);
		GridData gd_composite = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gd_composite.widthHint = 343;
		composite.setLayoutData(gd_composite);
		composite.setSize(746, 489);
		composite.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		composite.setLayout(new GridLayout(2, false));
		
		Label label = new Label(composite, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("车牌号");
		
		txt_plate = new Text(composite, SWT.BORDER);
		txt_plate.setEditable(false);
		txt_plate.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		txt_plate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label_7 = new Label(composite, SWT.NONE);
		label_7.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_7.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_7.setText("用户姓名");
		
		txt_userName = new Text(composite, SWT.BORDER);
		txt_userName.setEditable(false);
		txt_userName.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		txt_userName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_8 = new Label(composite, SWT.NONE);
		label_8.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_8.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_8.setText("用户车牌");
		
		txt_userPlate = new Text(composite, SWT.BORDER);
		txt_userPlate.setEditable(false);
		txt_userPlate.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		txt_userPlate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_10 = new Label(composite, SWT.NONE);
		label_10.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_10.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_10.setText("用户车位号");
		
		txt_slotNo = new Text(composite, SWT.BORDER);
		txt_slotNo.setEditable(false);
		txt_slotNo.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		txt_slotNo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_9 = new Label(composite, SWT.NONE);
		label_9.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_9.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_9.setText("用户有效期");
		
		txt_valid = new Text(composite, SWT.BORDER);
		txt_valid.setEditable(false);
		txt_valid.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		txt_valid.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_11 = new Label(composite, SWT.NONE);
		label_11.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_11.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_11.setText("停车场");
		
		txt_carparkName = new Text(composite, SWT.BORDER);
		txt_carparkName.setEditable(false);
		txt_carparkName.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		txt_carparkName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_1 = new Label(composite, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("进场时间");
		
		txt_inTime = new Text(composite, SWT.BORDER);
		txt_inTime.setEditable(false);
		txt_inTime.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		txt_inTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label_2 = new Label(composite, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_2.setText("收费起始时间");
		
		txt_startTime = new Text(composite, SWT.BORDER);
		txt_startTime.setEditable(false);
		txt_startTime.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		txt_startTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label_3 = new Label(composite, SWT.NONE);
		label_3.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_3.setText("收费截止时间");
		
		txt_endTime = new Text(composite, SWT.BORDER);
		txt_endTime.setEditable(false);
		txt_endTime.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		txt_endTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label_6 = new Label(composite, SWT.NONE);
		label_6.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_6.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_6.setText("收费时长");
		
		txt_stillTime = new Text(composite, SWT.BORDER);
		txt_stillTime.setEditable(false);
		txt_stillTime.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		txt_stillTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_4 = new Label(composite, SWT.NONE);
		label_4.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_4.setText("应收金额");
		
		txt_should = new Text(composite, SWT.BORDER);
		txt_should.setText("0");
		txt_should.setEditable(false);
		txt_should.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		txt_should.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_12 = new Label(composite, SWT.NONE);
		label_12.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_12.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_12.setText("已收金额");
		
		txt_paid = new Text(composite, SWT.BORDER);
		txt_paid.setEditable(false);
		txt_paid.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		txt_paid.setText("0");
		txt_paid.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_5 = new Label(composite, SWT.NONE);
		label_5.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_5.setText("实收金额");
		
		txt_fact = new Text(composite, SWT.BORDER);
		txt_fact.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		txt_fact.setText("0");
		txt_fact.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lbl_carType = new Label(composite, SWT.NONE);
		lbl_carType.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_lbl_carType = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		lbl_carType.setLayoutData(gd_lbl_carType);
		lbl_carType.setText("车流类型");
		
		combo_carType = new Combo(composite, SWT.READ_ONLY);
		combo_carType.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		GridData gd_combo_carType = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		combo_carType.setLayoutData(gd_combo_carType);
		combo_carType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String carparkCarType2 = combo_carType.getText();
				if (carparkCarType2.equals("请选择车型")) {
					return;
				}
				SingleCarparkInOutHistory h = task.getCch();
				SingleCarparkDevice device = task.getDevice();
				if (StrUtil.isEmpty(h)) {
					return;
				}
				Date inTime = h.getInTime();
				Date outTime = h.getOutTime();
				CarparkMainPresenter presenter = task.getPresenter();
				CarparkMainModel model = presenter.getModel();
				float countShouldMoney = presenter.countShouldMoney(device.getCarpark().getId(), carparkCarType2, inTime, outTime, h);
				String totalTime = StrUtil.MinusTime2(inTime, outTime);
				log.info("等待收费：车辆{}，停车场{}，车辆类型{}，进场时间{}，出场时间{}，停车：{}，应收费：{}元", h.getPlateNo(), device.getCarpark(), carparkCarType2, h.getInTime(),
						h.getOutTime(), totalTime,
						countShouldMoney);
				presenter.showContentToDevice(h.getPlateNo(), device,
						CarparkUtils.getCarStillTime(totalTime) + CarparkUtils.formatFloatString("请缴费" + countShouldMoney + "元"), false);
				cch.setShouldMoney(countShouldMoney);
				Float chargedMoney = cch.getFactMoney();
				h.setShouldMoney(countShouldMoney);
				if (countShouldMoney - chargedMoney > 0 && device.getScreenType().equals(ScreenTypeEnum.一体机) && model.booleanSetting(SystemSettingTypeEnum.无车牌时使用二维码进出场)) {
					if (device.getIsHandCharge()) {
						model.getMapWaitInOutHistory().put(device.getIp(), h);
						presenter.qrCodeInOut(h.getPlateNo(), device, false, h, "缴费" + countShouldMoney + "元,请在黄线外扫码付费");
						return;
					}
				}
				task.setCarType(carparkCarType2);
				init();
			}
		});
		boolean isManyCharge=task.getPresenter().getModel().getMapTempCharge().keySet().size()<=1;
		gd_lbl_carType.exclude=isManyCharge;
		gd_combo_carType.exclude=isManyCharge;
		
		
		new Label(composite, SWT.NONE);
		
		Composite composite_3 = new Composite(composite, SWT.NONE);
		composite_3.setLayout(new GridLayout(2, false));
		composite_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Button button = new Button(composite_3, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				charge();
			}
		});
		button.setFont(SWTResourceManager.getFont("微软雅黑", 15, SWT.BOLD));
		button.setText(" 收费放行 ");
		button.setFocus();
		
		Button button_1 = new Button(composite_3, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				free();
			}
		});
		button_1.setFont(SWTResourceManager.getFont("微软雅黑", 15, SWT.BOLD));
		button_1.setText(" 免费放行 ");
		new Label(composite, SWT.NONE);
		
		Composite composite_4 = new Composite(composite, SWT.NONE);
		composite_4.setLayout(new GridLayout(2, false));
		composite_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Button button_2 = new Button(composite_4, SWT.NONE);
		button_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handSearch();
			}
		});
		button_2.setFont(SWTResourceManager.getFont("微软雅黑", 15, SWT.BOLD));
		button_2.setText(" 模糊查找 ");
		
		Button button_3 = new Button(composite_4, SWT.NONE);
		button_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openDoor();
			}
		});
		button_3.setFont(SWTResourceManager.getFont("微软雅黑", 15, SWT.BOLD));
		button_3.setText(" 手动抬杆 ");
		
		SashForm sashForm = new SashForm(composite_2, SWT.VERTICAL);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Group group = new Group(sashForm, SWT.NONE);
		group.setText("进场图片");
		group.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		lbl_inImage = new CLabel(group, SWT.NONE);
		
		Group group_1 = new Group(sashForm, SWT.NONE);
		group_1.setText("出场图片");
		group_1.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		lbl_outImage = new CLabel(group_1, SWT.NONE);
		sashForm.setWeights(new int[] {1, 1});
		
		TabItem tbtmNewItem_1 = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem_1.setText("相关车辆进出记录");
		
		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem_1.setControl(composite_1);
		composite_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		composite_1.setLayout(new GridLayout(1, false));
		
		tableViewer = new TableViewer(composite_1, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tableColumn = tableViewerColumn.getColumn();
		tableColumn.setWidth(120);
		tableColumn.setText("车牌号");
		
		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tableColumn_1 = tableViewerColumn_1.getColumn();
		tableColumn_1.setWidth(170);
		tableColumn_1.setText("进场时间");
		
		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tableColumn_2 = tableViewerColumn_2.getColumn();
		tableColumn_2.setWidth(170);
		tableColumn_2.setText("出场时间");
		
		TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tableColumn_3 = tableViewerColumn_3.getColumn();
		tableColumn_3.setWidth(100);
		tableColumn_3.setText("停车场名称");
		
		TableViewerColumn tableViewerColumn_4 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tableColumn_4 = tableViewerColumn_4.getColumn();
		tableColumn_4.setWidth(200);
		tableColumn_4.setText("备注");
		init();
		initDataBindings();
	}

	protected void openDoor() {
		try {
			boolean showContentToDevice = task.getPresenter().showContentToDevice("手动开闸", task.getDevice(), task.getPresenter().getModel().getMapVoice().get(DeviceVoiceTypeEnum.临时车出场语音).getContent(), true);
			if (showContentToDevice) {
				task.getPresenter().saveOpenDoor(task.getDevice(), task.getBigImage(), txt_plate.getText(), true,new Date());
				cc.setStatus("手动抬杆");
				if (cch!=null) {
					cc.setShouldMoney(cch.getShouldMoney().doubleValue());
				}
				cc.setOperaName(ConstUtil.getUserName());
	    		sp.getCarparkInOutService().saveEntity(cc);
	    		task.getPresenter().refreshCarCheck();
	    		close();
			}
		} catch (Exception e) {
			log.error("出场手动抬杆时发生错误",e);
			MessageUtil.info("出场手动抬杆时发生错误", e.getMessage());
		}
	}
	protected void handSearch() {
		String plateNo = txt_plate.getText();
		log.info("人工查找车牌：{},",plateNo);
		HandSearchPresenter presenter = InjectorUtil.getInstance(HandSearchPresenter.class);
		presenter.getModel().setPlateNo(plateNo);
		presenter.getModel().setHavePlateNoSelect(null);
		presenter.getModel().setNoPlateNoSelect(null);
		presenter.getModel().setSaveBigImg(task.getBigImgFileName());
		presenter.getModel().setSaveSmallImg(task.getSmallImgFileName());
		presenter.getModel().setCarpark(task.getDevice().getCarpark());
		PresenterWizard wizard = new PresenterWizard(presenter);
		CommonUIFacility commonui = InjectorUtil.getInstance(CommonUIFacility.class);
		Object showWizard = commonui.showWizard(wizard);
		if (StrUtil.isEmpty(showWizard)) {
			return;
		}
		SingleCarparkInOutHistory select = presenter.getModel().getHavePlateNoSelect() == null ? presenter.getModel().getNoPlateNoSelect()
				: presenter.getModel().getHavePlateNoSelect();
		System.out.println(select);
		if (StrUtil.isEmpty(select)) {
			return;
		}
		task.getPresenter().getModel().getMapInOutWindow().remove(task.getPlateNO());
		task.setEditPlateNO(select.getPlateNo());
		task.refreshUserAndHistory(true);
		try {
			task.setShowChargeWindow(false);
			task.checkUserAndOut(false);
			cch=task.getCch();
			if (cch.getShouldMoney()<=0) {
				close();
				return;
			}
			init();
		} catch (Exception e) {
			log.error("模糊查找时发生错误",e);
			MessageUtil.info("模糊查找时发生错误,"+e);
		}
	}
	private void init() {
		try {
			shell.setText(task.getEditPlateNo()+"停车收费");
			Set<String> carTypes = task.getPresenter().getModel().getMapTempCharge().keySet();
			List<String> listCarTypes = new ArrayList<>();
			listCarTypes.add("请选择车型");
			listCarTypes.addAll(carTypes);
			combo_carType.setItems(listCarTypes.toArray(new String[listCarTypes.size()]));
			Set<String> plates=new HashSet<>();
			if (user!=null) {
				txt_userName.setText(user.getName());
				txt_userPlate.setText(user.getPlateNo());
				txt_userPlate.setToolTipText(user.getPlateNo());
				txt_slotNo.setText(user.getParkingSpace()==null?"":user.getParkingSpace());
				txt_valid.setText(user.getValitoLabel());
				plates.addAll(Arrays.asList(user.getName().split(",")));
			}else {
				if (cch!=null) {
					plates.add(cch.getPlateNo());
				}
			}
			if (!plates.isEmpty()) {
				Map<String, Object> map = new HashMap<>();
				map.put(SingleCarparkInOutHistory.Property.inTime.name() + "-ge", new DateTime(cch.getInTime()).minusMonths(1).toDate());
				map.put(SingleCarparkInOutHistory.Property.plateNo.name() + "-in", plates.toArray());
				List<SingleCarparkInOutHistory> list = sp.getCarparkInOutService().findByMap(0, Integer.MAX_VALUE, SingleCarparkInOutHistory.class, map);
				list.sort((e1, e2) -> e2.getInTime().compareTo(e1.getInTime()));
				model.setList(list);
			}
			txt_plate.setText(task.getEditPlateNo());
			if (cch!=null) {
				txt_inTime.setText(StrUtil.formatDateTime(cch.getInTime()));
				txt_carparkName.setText(cch.getCarparkName());
				txt_startTime.setText(StrUtil.formatDateTime(cch.getReviseInTime()));
				txt_endTime.setText(StrUtil.formatDateTime(cch.getChargeTime()));
				txt_stillTime.setText(StrUtil.MinusTime2(cch.getReviseInTime(), cch.getChargeTime()));
				txt_should.setText(String.valueOf(cch.getShouldMoney()));
				txt_paid.setText(String.valueOf(cch.getFactMoney()));
				txt_fact.setText(String.valueOf(cch.getShouldMoney()-cch.getFactMoney()));
				ImageUtils.setBackgroundImage(ImageUtils.getImageByte(cch.getBigImg()), lbl_inImage,cch.getBigImg());
				if (task.getCarType()!=null) {
					combo_carType.setText(task.getCarType());
				}else {
					combo_carType.select(0);
				}
			}
			ImageUtils.setBackgroundImage(task.getBigImage(), lbl_outImage,task.getBigImgFileName());
			if ((cc=task.getCarCheck())==null) {
				cc = new CarCheckHistory();
				cc.setPlate(task.getPlateNO());
				cc.setTime(task.getDate());
				cc.setType("出场");
				cc.setSourcePlate(task.getPlateNO());
				cc.setBigImage(task.getBigImgFileName());
				cc.setSmallImage(task.getSmallImgFileName());
				cc.setDeviceIp(task.getDevice().getIp());
				cc.setDeviceName(task.getDevice().getName());
				cc.setOperaName(ConstUtil.getUserName());
				Long saveEntity = sp.getCarparkInOutService().saveEntity(cc);
				cc.setId(saveEntity);
				task.getPresenter().getModel().addCarChecks(Arrays.asList(cc));
				task.setCarCheck(cc);
			}
			task.getPresenter().getModel().getMapInOutWindow().put(task.getPlateNO(), this);
			KeySetting keySetting = KeySetting.read();
			Listener listener = new Listener() {
				@Override
				public void handleEvent(Event e) {
					if (shell.getDisplay().getActiveShell()!=shell) {
						return;
					}
					// 收费放行
					if (e.keyCode == keySetting.getKeyCode(KeyReleaseTypeEnum.收费放行)) {
						charge();
					}
					// 免费放行
					if (e.keyCode == keySetting.getKeyCode(KeyReleaseTypeEnum.免费放行)) {
						free();
					}
				}
			};
			Display.getDefault().addFilter(SWT.KeyUp, listener);
			shell.addDisposeListener(new DisposeListener() {
				@Override
				public void widgetDisposed(DisposeEvent e) {
					shell.getDisplay().removeFilter(SWT.KeyUp, listener);
				}
			});
		} catch (Exception e) {
			MessageUtil.info("出场时发生错误"+e);
			log.info("出场确认时发生错误",e);
			close();
		}
	}
	
	public void close() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					if (!shell.isDisposed()) {
						shell.dispose();
					}
					task.getPresenter().refreshCarCheck();
				} catch (Exception e) {
					log.error("关闭确认时发生错误", e);
				} 
			}
		});
	}
	protected void free() {
		try {
			if (cch==null) {
				return;
			}
			if (task.getCarType()==null) {
				return;
			}
			boolean chargeCarPass = task.getPresenter().chargeCarPass(task.getDevice(), cch, true, cch.getShouldMoney(), cch.getFactMoney(), 0, false);
			if (chargeCarPass) {
				updateCheckStatus("确认放行");
				close();
			}
		} catch (Exception e) {
			MessageUtil.info("免费时发生错误"+e);
			log.error("收费时发生错误",e);
		}
	}

	protected void charge() {
		try {
			if (cch==null) {
				return;
			}
			if (task.getCarType()==null) {
				return;
			}
			boolean chargeCarPass = task.getPresenter().chargeCarPass(task.getDevice(), cch, true, cch.getShouldMoney(), cch.getFactMoney(), Float.valueOf(txt_fact.getText()), false);
			if (chargeCarPass) {
				updateCheckStatus("确认放行");
				close();
			}
		} catch (Exception e) {
			MessageUtil.info("收费时发生错误"+e);
			log.error("收费时发生错误",e);
		}
	}
	/**
	 * @param s 
	 * 
	 */
	public void updateCheckStatus(String s) {
		cc.setPlate(txt_plate.getText());
		cc.setEditedPlate(!cc.getSourcePlate().equals(txt_plate.getText()));
		cc.setShouldMoney(Double.valueOf(txt_should.getText()));
		cc.setCarType(task.getCarType());
		cc.setStatus(s);
		cc.setOperaName(ConstUtil.getUserName());
		sp.getCarparkInOutService().saveEntity(cc);
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
		IObservableMap[] observeMaps = BeansObservables.observeMaps(listContentProvider.getKnownElements(), SingleCarparkInOutHistory.class, new String[]{"plateNo", "inTimeLabel", "outTimeLabel", "carparkName", "remarkString"});
		tableViewer.setLabelProvider(new ObservableMapLabelProvider(observeMaps));
		tableViewer.setContentProvider(listContentProvider);
		//
		IObservableList listModelObserveList = BeanProperties.list("list").observe(model);
		tableViewer.setInput(listModelObserveList);
		//
		return bindingContext;
	}
	@Override
	public boolean isOpen() {
		return open;
	}
	@Override
	public void focus() {
		
	}
	@Override
	public Shell getShell() {
		return shell;
	}
}
