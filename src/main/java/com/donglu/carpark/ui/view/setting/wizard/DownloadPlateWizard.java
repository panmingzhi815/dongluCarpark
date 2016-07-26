package com.donglu.carpark.ui.view.setting.wizard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.wizard.Wizard;

import com.donglu.carpark.ui.Login;
import com.donglu.carpark.util.CarparkFileUtils;
import com.donglu.carpark.util.ConstUtil;
import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.common.ui.CommonUIFacility.Progress;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.CameraTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.hardware.plateDevice.PlateNOJNA;
import com.dongluhitec.card.hardware.plateDevice.PlateNOResult;
import com.dongluhitec.card.hardware.plateDevice.bean.PlateDownload;
import com.dongluhitec.card.ui.util.ProcessBarMonitor;
import com.dongluhitec.card.ui.util.WidgetUtil;

public class DownloadPlateWizard extends Wizard implements AbstractWizard {

	private DownloadPlateModel model;
	private DownloadPlateWizardPage page;
	private CommonUIFacility commonui;

	public DownloadPlateWizard(DownloadPlateModel model, CommonUIFacility commonui) {
		this.model = model;
		this.commonui = commonui;
		setWindowTitle("对设备下载白名单信息");
	}

	@Override
	public void addPages() {
		page = new DownloadPlateWizardPage(model);
		addPage(page);
		getShell().setSize(470, 500);
		getShell().setImage(JFaceUtil.getImage("carpark_32"));
		WidgetUtil.center(getShell());
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	@Override
	public Object getModel() {

		return model;
	}

	protected void downloadPlate() {
		final java.util.List<DownloadDeviceInfo> listSelected = model.getListSelected();
		final java.util.List<DownloadDeviceInfo> errorListInfo = new ArrayList<>();
		Progress showProgressBar = commonui.showProgressBar("下载车牌数据到设备", 0, listSelected.size() + 1);
		new Thread(new Runnable() {
			public void run() {
				long nanoTime = System.nanoTime();
				ProcessBarMonitor monitor = showProgressBar.getMonitor();
				int i = 0;
				List<PlateDownload> listPlate = model.getListPlate();
				String message = "车牌下载完成,总共有"+listPlate.size()+"个车牌下载";
				for (DownloadDeviceInfo downloadDeviceInfo : listSelected) {
					i++;
					monitor.dowork(i);
					if (StrUtil.isEmpty(listPlate)) {
						break;
					}
					CameraTypeEnum type = downloadDeviceInfo.getType();
					if (type.equals(CameraTypeEnum.其他)) {
						continue;
					}
					if (type.equals(CameraTypeEnum.智芯)) {
						if (listPlate.size()>6000) {
							int j = listPlate.size()-6000;
							listPlate=listPlate.subList(0, 6000);
							message+="\n设备"+downloadDeviceInfo.getIp()+"下满6000条,有"+j+"条下载失败";
						}
					}
					if (type.equals(CameraTypeEnum.信路威)) {
						if (listPlate.size() > 6000) {
							int j = listPlate.size()-6000;
							listPlate=listPlate.subList(0, 6000);
							message+="\n设备"+downloadDeviceInfo.getIp()+"下满6000条,有"+j+"条下载失败";
						}
					}
					if (type.equals(CameraTypeEnum.臻识)) {
						if (listPlate.size() > 5000) {
							int j = listPlate.size()-5000;
							listPlate=listPlate.subList(0, 5000);
							message+="\n设备"+downloadDeviceInfo.getIp()+"下满5000条,有"+j+"条下载失败";
						}
					}
					String ip = downloadDeviceInfo.getIp();
					monitor.showMessage("正在下载车牌信息到:" + ip);
					PlateNOJNA plateNOJNA = type.getJNA(Login.injector);
					try {
						plateNOJNA.openEx(ip, new PlateNOResult() {
							@Override
							public void invok(String ip, int channel, String plateNO, byte[] bigImage, byte[] smallImage, float rightSize) {

							}
						});
						if (!StrUtil.isEmpty(plateNOJNA)) {
							plateNOJNA.plateDownload(listPlate, ip);
						}
					} catch (Exception e) {
						errorListInfo.add(downloadDeviceInfo);
						continue;
					} finally {
						plateNOJNA.closeEx(ip);
					}
				}
				showProgressBar.finish();
				String msg=message;
				Runnable runnable = new Runnable() {
					public void run() {
						commonui.info("提示", msg);
					}
				};
				getShell().getDisplay().asyncExec(runnable);
				System.out.println("下载花费时间：" + (System.nanoTime() - nanoTime));
			}
		}).start();
	}

	@SuppressWarnings("unchecked")
	protected void init() {
		Object readObject = CarparkFileUtils.readObject(ConstUtil.MAP_IP_TO_DEVICE);
		if (readObject != null) {
			Map<String, SingleCarparkDevice> map = (Map<String, SingleCarparkDevice>) readObject;
			if (map != null) {
				java.util.List<SingleCarparkDevice> listDevice = new ArrayList<>(map.values());
				java.util.List<DownloadDeviceInfo> list = new ArrayList<>();
				for (SingleCarparkDevice singleCarparkDevice : listDevice) {
					DownloadDeviceInfo info = new DownloadDeviceInfo();
					info.setIp(singleCarparkDevice.getIp());
					info.setType(singleCarparkDevice.getCameraType());
					list.add(info);
				}
				model.setList(list);
			}
		}

	}

}
