package com.donglu.carpark.ui.view;


import java.util.Date;
import java.util.List;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.ui.CarparkMainApp;
import com.donglu.carpark.ui.CarparkMainPresenter;
import com.donglu.carpark.ui.common.Presenter;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class DevicePresenter  implements Presenter{
	static final Logger log = LoggerFactory.getLogger(DevicePresenter.class);
	private DeviceView view;
	@Inject
	private CommonUIFacility commonui;
	
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
		presenter.handPhotograph(ip);
		CarparkMainApp.mapHandPhotograph.put(ip, new Date());
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
		presenter.createCamera(CarparkMainApp.mapIpToDevice.get(ip), composite);
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
			String ip = CarparkMainApp.mapDeviceTabItem.get(selection);
			presenter.openDoor(CarparkMainApp.mapIpToDevice.get(ip));
			CarparkMainApp.mapOpenDoor.put(ip, true);
			handPhotograph(ip);
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
			String ip = CarparkMainApp.mapDeviceTabItem.get(selection);
			presenter.closeDoor(CarparkMainApp.mapIpToDevice.get(ip));
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
			String ip = CarparkMainApp.mapDeviceTabItem.get(selection);
			presenter.fleetDoor(CarparkMainApp.mapIpToDevice.get(ip),isopen);
		} catch (Exception e) {
			log.error("设备车队是发生错误！",e);
		}
	}
}
