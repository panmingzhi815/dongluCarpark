package com.donglu.carpark.ui.view;


import java.util.Date;
import java.util.List;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.ui.CarparkMainPresenter;
import com.donglu.carpark.ui.common.Presenter;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.DeviceVoiceTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class DevicePresenter  implements Presenter{
	static final Logger log = LoggerFactory.getLogger(DevicePresenter.class);
	private DeviceView view;
	@Inject
	private CommonUIFacility commonui;
	@Inject
	private CarparkMainModel model;
	
	private CarparkMainPresenter presenter;

	private String type="";
	
	private List<SingleCarparkDevice> listDevice;
	
	@Override
	public void go(Composite c) {
		view=new DeviceView(c, c.getStyle());
		view.setPresenter(this);
		view.initDevices(listDevice);
	}
	public void handPhotograph(String ip) {
		if (!checkStatus(ip)) {
			log.info("设备已停用");
			return;
		}
		presenter.handPhotograph(ip);
		model.getMapHandPhotograph().put(ip, new Date());
	}
	private boolean checkStatus(String ip) {
		Boolean boolean1 = model.getMapIpToDeviceStatus().get(ip);
		return boolean1==null||boolean1;
	}
	public void addDevice(CTabFolder tabFolder) {
		presenter.addDevice(tabFolder, type);
	}
	public void editDevice(CTabFolder tabFolder) {
		presenter.editDevice(tabFolder, type);
	}
	public void deleteDeviceTabItem(CTabItem selection) {
		presenter.deleteDeviceTabItem(selection);
	}
	public void deleteDevice(CTabItem selection) {
		boolean confirm = commonui.confirm("确定提示", "确定删除所选设备");
		if (!confirm) {
			return;
		}
		deleteDeviceTabItem(selection);
	}
	public void setPresenter(CarparkMainPresenter presenter) {
		this.presenter = presenter;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setListDevice(List<SingleCarparkDevice> listDevice) {
		this.listDevice = listDevice;
	}
	public void createRightCamera(String ip, Composite composite) {
		presenter.createCamera(model.getMapIpToDevice().get(ip), composite);
	}
	public void controlItem(Boolean dispose){
		if (StrUtil.isEmpty(view)) {
			return;
		}
		view.controlItem(dispose);
	}
	public void openDoor() {
		try {
			CTabItem selection = view.getTabFolder().getSelection();
			if (StrUtil.isEmpty(selection)) {
				return;
			}
			String ip = model.getMapDeviceTabItem().get(selection);
			
			SingleCarparkDevice device = model.getMapIpToDevice().get(ip);
//			presenter.openDoor(device);
			if (device.getInOrOut().contains("进口")) {
				presenter.showContentToDevice("手动开闸", device, model.getMapVoice().get(DeviceVoiceTypeEnum.进口开闸语音).getContent(), true);
			}else {
				presenter.showContentToDevice("手动开闸", device, model.getMapVoice().get(DeviceVoiceTypeEnum.出口开闸语音).getContent(), true);
			}
//			if (model.equalsSetting(SystemSettingTypeEnum.抬杆自动收费放行,"true")&&model.getMapWaitInOutHistory().get(ip)!=null) {
//				SingleCarparkInOutHistory data = model.getMapWaitInOutHistory().get(ip);
//				presenter.charge(false, true, data, device);
//			}else{
//				presenter.showContentToDevice("手动开闸", device, model.getMapVoice().get(DeviceVoiceTypeEnum.临时车出场语音).getContent(), true);
//			}
			model.getMapOpenDoor().put(ip, true);
			presenter.handPhotograph(ip);
			model.getMapHandPhotograph().put(ip, new Date());
		} catch (Exception e) {
			log.error("设备开闸时发生错误",e);
		}
	}
	public void closeDoor() {
		try {
			CTabItem selection = view.getTabFolder().getSelection();
			if (StrUtil.isEmpty(selection)) {
				return;
			}
			String ip = model.getMapDeviceTabItem().get(selection);
			if (!checkStatus(ip)) {
				log.info("设备已停用");
				return;
			}
			presenter.closeDoor(model.getMapIpToDevice().get(ip));
		} catch (Exception e) {
			log.error("设备落杆时发生错误",e);
		}
		
	}
	public String getType() {
		return type;
	}
	public void fleet(boolean isopen) {
		try {
			CTabItem selection = view.getTabFolder().getSelection();
			if (StrUtil.isEmpty(selection)) {
				return;
			}
			String ip = model.getMapDeviceTabItem().get(selection);
			presenter.fleetDoor(model.getMapIpToDevice().get(ip),isopen);
		} catch (Exception e) {
			log.error("设备车队是发生错误！",e);
		}
	}
	public CarparkMainModel getModel() {
		return model;
	}
	public void testDevice() {
		try {
			CTabItem selection = view.getTabFolder().getSelection();
			if (StrUtil.isEmpty(selection)) {
				return;
			}
			String ip = model.getMapDeviceTabItem().get(selection);
			String msg="设备正常";
			String m = presenter.checkDeviceStatus(ip);
			if (!StrUtil.isEmpty(m)) {
				msg=m;
			}
			commonui.info("结果", msg);
		} catch (Exception e) {
			log.error("设备检测时发生错误",e);
			commonui.error("失败", "设备检测时发生错误",e);
		}
	}
	public CarparkMainPresenter getPresenter() {
		return presenter;
	}
}
