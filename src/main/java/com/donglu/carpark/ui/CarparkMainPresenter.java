package com.donglu.carpark.ui;

import java.awt.Canvas;
import java.awt.Frame;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.SWTResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkInOutServiceI;
import com.donglu.carpark.ui.common.App;
import com.donglu.carpark.ui.view.InOutHistoryPresenter;
import com.donglu.carpark.wizard.AddDeviceModel;
import com.donglu.carpark.wizard.AddDeviceWizard;
import com.donglu.carpark.wizard.ChangeUserWizard;
import com.donglu.carpark.wizard.ReturnAccountWizard;
import com.donglu.carpark.wizard.model.ChangeUserModel;
import com.donglu.carpark.wizard.model.ReturnAccountModel;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkReturnAccount;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.hardware.device.WebCameraDevice;
import com.dongluhitec.card.hardware.xinluwei.XinlutongJNA;
import com.dongluhitec.card.mapper.BeanUtil;
import com.google.common.util.concurrent.Uninterruptibles;
import com.google.inject.Inject;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class CarparkMainPresenter {
	private Logger LOGGER = LoggerFactory.getLogger(CarparkMainPresenter.class);
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Inject
	private CommonUIFacility commonui;
	@Inject
	private XinlutongJNA xinlutongJNA;
	
	@Inject
	private WebCameraDevice webCameraDevice;
	@Inject
	private InOutHistoryPresenter inOutHistoryPresenter;
	
	// 保存设备的进出口信息
	Map<String, String> mapDeviceType ;

	// 保存设备的界面信息
	Map<CTabItem, String> mapDeviceTabItem;
	//保存设备的信息
	Map<String, SingleCarparkDevice> mapIpToDevice;
	
	private CarparkMainModel model;
	
	private CarparkMainApp view;
	
	private App app;
	public void setCarNo(){
		
	}
	/**
	 * 删除一个设备tab页
	 * 
	 * @param selection
	 */
	protected void deleteDeviceTabItem(CTabItem selection) {
		if (selection != null) {
			String ip = mapDeviceTabItem.get(selection);
			System.out.println("删除设备" + ip);
			selection.dispose();
			xinlutongJNA.closeEx(ip);
			mapDeviceTabItem.remove(selection);
			mapDeviceType.remove(ip);
			mapIpToDevice.remove(ip);
			com.dongluhitec.card.ui.util.FileUtils.writeObject("mapIpToDevice", mapIpToDevice);
		}
	}
	/**
	 * 弹窗添加设备
	 * 
	 * @param string
	 * @param tabFolder
	 * 
	 */
	public void addDevice(CTabFolder tabFolder, String type) {
		try {
			AddDeviceModel model = new AddDeviceModel();
			model.setList(sp.getCarparkService().findAllCarpark());
			model.setType("tcp");
			AddDeviceWizard v = new AddDeviceWizard(model);
			
			AddDeviceModel showWizard = (AddDeviceModel) commonui.showWizard(v);
			if (showWizard == null) {
				return;
			}
			String ip = showWizard.getIp();
			String name = showWizard.getName();
			showWizard.setInType(type);
			addDevice(showWizard.getDevice());
			addDevice(tabFolder, type, ip, name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void addDevice(SingleCarparkDevice device) throws Exception {
		String ip = device.getIp();
		SingleCarparkDevice singleCarparkDevice = mapIpToDevice.get(ip);
		if (!StrUtil.isEmpty(singleCarparkDevice)) {
			throw new Exception("ip" + ip + "的设备已存在");
		}
		mapIpToDevice.put(ip, device);
		com.dongluhitec.card.ui.util.FileUtils.writeObject("mapIpToDevice", mapIpToDevice);
	}

	/**
	 * 普通添加设备
	 * 
	 * @param tabFolder
	 * @param type
	 * @param ip
	 * @param name
	 */
	public void addDevice(CTabFolder tabFolder, String type, String ip, String name) {
		if (mapDeviceType.get(ip) != null) {
			commonui.error("添加失败", "设备" + ip + "已存在");
			return;
		}
		CTabItem tabItem = new CTabItem(tabFolder, SWT.NONE);
		tabItem.setFont(SWTResourceManager.getFont("微软雅黑", 15, SWT.NORMAL));
		tabItem.setText(name);
		Composite composite = new Composite(tabFolder, SWT.BORDER | SWT.EMBEDDED);
		tabItem.setControl(composite);
		composite.setLayout(new FillLayout());
		if (type.equals("进口")) {
			createLeftCamera(ip, composite);
		} else if (type.equals("出口")) {
			createRightCamera(ip, composite);
		}
		tabFolder.setSelection(tabItem);
		mapDeviceTabItem.put(tabItem, ip);
		mapDeviceType.put(ip, type);
	}
	/**
	 * 创建出口监控
	 * @param ip
	 * @param northCamera
	 * 
	 */
	public void createRightCamera(String ip, Composite northCamera) {
		Frame new_Frame1 = SWT_AWT.new_Frame(northCamera);
		Canvas canvas1 = new Canvas();
		new_Frame1.add(canvas1);
		new_Frame1.pack();
		new_Frame1.setVisible(true);
		final String url = "rtsp://" + ip + ":554/h264ESVideoTest";
		final EmbeddedMediaPlayer createPlayRight = webCameraDevice.createPlay(new_Frame1, url);
		createPlayRight.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			@Override
			public void finished(final MediaPlayer mediaPlayer) {
				new Runnable() {
					public void run() {
						while (!mediaPlayer.isPlaying()) {
//							LOGGER.info("设备连接{}已断开", url);
							mediaPlayer.playMedia(url);
							Uninterruptibles.sleepUninterruptibly(10, TimeUnit.SECONDS);
						}
					}
				}.run();
			}
		});

		getView().shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				createPlayRight.release();
			}
		});
		northCamera.addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				createPlayRight.release();
			}
		});
		xinlutongJNA.openEx(ip, getView());
	}

	/**
	 * 创建进口监控
	 * 
	 * @param ip
	 * @param southCamera
	 * 
	 */
	public void createLeftCamera(String ip, Composite southCamera) {
		Frame new_Frame1 = SWT_AWT.new_Frame(southCamera);
		Canvas canvas1 = new Canvas();
		new_Frame1.add(canvas1);
		new_Frame1.pack();
		new_Frame1.setVisible(true);
		final String url = "rtsp://" + ip + ":554/h264ESVideoTest";
		final EmbeddedMediaPlayer createPlayLeft = webCameraDevice.createPlay(new_Frame1, url);
		createPlayLeft.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			@Override
			public void finished(final MediaPlayer mediaPlayer) {
				new Runnable() {
					public void run() {
						while (!mediaPlayer.isPlaying()) {
							LOGGER.info("设备连接{}已断开", url);
							mediaPlayer.playMedia(url);
							Uninterruptibles.sleepUninterruptibly(10, TimeUnit.SECONDS);
						}
					}
				}.run();
			}

			@Override
			public void error(MediaPlayer mediaPlayer) {

			}
		});
		getView().shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				createPlayLeft.release();
			}
		});
		southCamera.addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				createPlayLeft.release();
			}
		});
		xinlutongJNA.openEx(ip, getView());
	}
	/**
	 * @param type
	 * @param tabFolder
	 * 
	 */
	public void editDevice(CTabFolder tabFolder, String type) {
		try {
			CTabItem selection = tabFolder.getSelection();
			if (StrUtil.isEmpty(selection)) {
				return;
			}
			String oldIp = mapDeviceTabItem.get(selection);
			SingleCarparkDevice device = mapIpToDevice.get(oldIp);
			AddDeviceModel model = new AddDeviceModel();
			model.setDevice(device);
			AddDeviceWizard v = new AddDeviceWizard(model);
			model.setList(sp.getCarparkService().findAllCarpark());
			AddDeviceModel showWizard = (AddDeviceModel) commonui.showWizard(v);
			if (showWizard == null) {
				return;
			}
			String ip = showWizard.getIp();

			if (ip.equals(oldIp)) {
				selection.setText(showWizard.getName());
				mapIpToDevice.put(ip, showWizard.getDevice());
				com.dongluhitec.card.ui.util.FileUtils.writeObject("mapIpToDevice", mapIpToDevice);
				commonui.info("修改成功", "修改设备" + ip + "成功");
				return;
			} else {
				if (mapDeviceType.get(ip) != null) {
					commonui.error("修改失败", "设备" + ip + "已存在");
					return;
				}
				deleteDeviceTabItem(selection);
				addDevice(showWizard.getDevice());
				addDevice(tabFolder, type, ip, showWizard.getName());
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public CarparkMainApp getView() {
		return view;
	}
	public void setView(CarparkMainApp view) {
		this.view = view;
	}
	
	public Map<String, String> getMapDeviceType() {
		return mapDeviceType;
	}
	public void setMapDeviceType(Map<String, String> mapDeviceType) {
		this.mapDeviceType = mapDeviceType;
	}
	public Map<CTabItem, String> getMapDeviceTabItem() {
		return mapDeviceTabItem;
	}
	public void setMapDeviceTabItem(Map<CTabItem, String> mapDeviceTabItem) {
		this.mapDeviceTabItem = mapDeviceTabItem;
	}
	public Map<String, SingleCarparkDevice> getMapIpToDevice() {
		return mapIpToDevice;
	}
	public void setMapIpToDevice(Map<String, SingleCarparkDevice> mapIpToDevice) {
		this.mapIpToDevice = mapIpToDevice;
	}
	public CarparkMainModel getModel() {
		return model;
	}
	public void setModel(CarparkMainModel model) {
		this.model = model;
	}
	/**
	 * 归账
	 */
	/**
	 * 
	 */
	public void returnAccount() {
		try {
			ReturnAccountModel model = new ReturnAccountModel();
			String userName = this.model.getUserName();
			model.setReturnUser(userName);
			CarparkInOutServiceI carparkInOutService = sp.getCarparkInOutService();
			float shouldMoney=carparkInOutService.findShouldMoneyByName(userName);
			float factMoney=carparkInOutService.findFactMoneyByName(userName);
			float freeMoney=carparkInOutService.findFreeMoneyByName(userName);
			model.setShouldReturn(shouldMoney);
			model.setFactReturn(factMoney);
			ReturnAccountWizard wizard=new ReturnAccountWizard(model,sp);
			ReturnAccountModel m = (ReturnAccountModel) commonui.showWizard(wizard);
			if (StrUtil.isEmpty(m)) {
				return;
			}
			SingleCarparkReturnAccount a = new SingleCarparkReturnAccount();
			BeanUtil.copyProperties(m, a, "returnUser","factReturn","shouldReturn","operaName");
			a.setReturnTime(new Date());
			Long saveReturnAccount = sp.getCarparkService().saveReturnAccount(a);
			
			List<SingleCarparkInOutHistory> list=carparkInOutService.findNotReturnAccount(a.getReturnUser());
			for (SingleCarparkInOutHistory singleCarparkInOutHistory : list) {
				singleCarparkInOutHistory.setReturnAccount(saveReturnAccount);
			}
			carparkInOutService.saveInOutHistoryOfList(list);
			this.model.setTotalCharge(carparkInOutService.findFactMoneyByName(userName));
			this.model.setTotalFree(carparkInOutService.findFreeMoneyByName(userName));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 显示屏显示，语音
	 * @param device 设备
	 * @param string 显示
	 * @param i 音量
	 * @param j 显示屏上下
	 */
	public void showMesToDevice(SingleCarparkDevice device, String string, int i, int j) {
		
		
	}
	/**
	 * 开门，设备开闸
	 * @param device
	 */
	public void openDoor(SingleCarparkDevice device) {
		
		
	}
	/**
	 * 免费放行
	 */
	public void freeCarPass() {
		String plateNo = model.getPlateNo();
		SingleCarparkInOutHistory history = model.getHistory();
		
		float real = 0;
		float shouldMony = model.getShouldMony();
		boolean confirm = commonui.confirm("收费确认", "车牌："+plateNo+"应收："+shouldMony+"免费放行");
		if (!confirm) {
			return;
		}
		history.setFactMoney(real);
		history.setFreeMoney(shouldMony-real);
		sp.getCarparkInOutService().saveInOutHistory(history);
		
	}
	/**
	 * 收费放行
	 */
	public void chargeCarPass() {
		
		String plateNo = model.getPlateNo();
		SingleCarparkInOutHistory history = model.getHistory();
		
		float real = model.getReal();
		float shouldMony = model.getShouldMony();
		boolean confirm = commonui.confirm("收费确认", "车牌："+plateNo+"应收："+shouldMony+"实收："+real);
		if (!confirm) {
			return;
		}
		history.setFactMoney(real);
		history.setFreeMoney(shouldMony-real);
		sp.getCarparkInOutService().saveInOutHistory(history);
	}
	/**
	 * 计算收费
	 * @return
	 */
	public float countShouldMoney() {
		
		return 20;
	}
	/**
	 * 换班
	 */
	public void changeUser() {
		
		ChangeUserWizard wizard=new ChangeUserWizard(new ChangeUserModel(), sp);
		ChangeUserModel showWizard = (ChangeUserModel) commonui.showWizard(wizard);
		if (StrUtil.isEmpty(showWizard)) {
			return;
		}
		SingleCarparkSystemUser systemUser = showWizard.getSystemUser();
		String userName = systemUser.getUserName();
		System.setProperty("userName", userName);
		System.setProperty("userType", systemUser.getType());
		
		model.setBtnClick(false);
		model.setTotalSlot(sp.getCarparkInOutService().findTotalSlotIsNow());
		model.setUserName(userName);
		model.setWorkTime(new Date());
		model.setTotalCharge(sp.getCarparkInOutService().findShouldMoneyByName(userName));
		model.setTotalFree(sp.getCarparkInOutService().findFreeMoneyByName(userName));
		model.setMonthSlot(sp.getCarparkInOutService().findFixSlotIsNow());
		model.setHoursSlot(sp.getCarparkInOutService().findTempSlotIsNow());
	}
	/**
	 * 打开记录查询页面
	 */
	public void showSearchInOutHistory() {
		System.out.println(inOutHistoryPresenter.getListPresenter().getInOutHistory());;
		if (app!=null) {
			if (app.isOpen()) {
				app.focus();
			}else{
				app.open();
			}
			return;
		}
		ShowApp showApp=new ShowApp();
		showApp.setPresenter(inOutHistoryPresenter);
		app=showApp;
		app.open();
	}
}
