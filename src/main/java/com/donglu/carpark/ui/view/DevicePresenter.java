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
import com.donglu.carpark.util.CarparkUtils;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
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
		presenter.handPhotograph(ip);
		model.getMapHandPhotograph().put(ip, new Date());
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
			presenter.openDoor(model.getMapIpToDevice().get(ip));
			model.getMapOpenDoor().put(ip, true);
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
			String ip = model.getMapDeviceTabItem().get(selection);
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
			boolean ping = CarparkUtils.ping(ip);
			String msg="";
			if (!ping) {
				msg="摄像机["+ip+"]通讯失败\n";
			}
			SingleCarparkDevice device = model.getMapIpToDevice().get(ip);
			String linkAddress = device.getLinkAddress();
			if(linkAddress!=null&&linkAddress.indexOf(":")>-1){
				String substring = linkAddress.substring(0, linkAddress.indexOf(":"));
				boolean ping2 = CarparkUtils.ping(substring);
				if (!ping2) {
					msg+="控制器["+substring+"]通讯失败";
				}
			}else{
				msg+="控制器不正确";
			}
			if (StrUtil.isEmpty(msg)) {
				msg="设备检测正常";
			}
			commonui.info("结果", msg);
		} catch (Exception e) {
			log.error("设备检测时发生错误",e);
			commonui.error("失败", "设备检测时发生错误",e);
		}
	}
}
