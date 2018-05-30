package com.donglu.carpark.ui.view.setting.wizard;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.Login;
import com.donglu.carpark.util.CarparkFileUtils;
import com.donglu.carpark.util.ConstUtil;
import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.common.ui.CommonUIFacility.Progress;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.CameraTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.hardware.plateDevice.PlateNOJNA;
import com.dongluhitec.card.hardware.plateDevice.PlateNOResult;
import com.dongluhitec.card.hardware.plateDevice.bean.PlateDownload;
import com.dongluhitec.card.ui.util.FileUtils;
import com.dongluhitec.card.ui.util.ProcessBarMonitor;
import com.dongluhitec.card.ui.util.WidgetUtil;

public class DownloadPlateWizard extends Wizard implements AbstractWizard {

	private DownloadPlateModel model;
	private DownloadPlateWizardPage page;
	private CommonUIFacility commonui;
	protected boolean canClose=true;
	private CarparkDatabaseServiceProvider sp;

	public DownloadPlateWizard(DownloadPlateModel model, CommonUIFacility commonui) {
		this.model = model;
		this.commonui = commonui;
		setWindowTitle("对设备下载白名单信息");
	}

	@Override
	public void addPages() {
		page = new DownloadPlateWizardPage(model);
		addPage(page);
		getShell().setSize(550, 500);
		getShell().setImage(JFaceUtil.getImage("carpark_32"));
		WidgetUtil.center(getShell());
		getShell().addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				e.doit=canClose;
			}
		});
		sp = Login.injector.getInstance(CarparkDatabaseServiceProvider.class);
	}

	@Override
	public boolean performFinish() {
		return canClose;
	}

	@Override
	public Object getModel() {

		return model;
	}

	protected void downloadPlate() {
		final java.util.List<DownloadDeviceInfo> listSelected = model.getListSelected();
		if (StrUtil.isEmpty(listSelected)) {
			return;
		}
		final java.util.List<DownloadDeviceInfo> errorListInfo = new ArrayList<>();
		Progress showProgressBar = commonui.showProgressBar("下载车牌数据到设备", 0, listSelected.size() + 1);
		new Thread(new Runnable() {
			public void run() {
				canClose=false;
				try {
					long nanoTime = System.nanoTime();
					ProcessBarMonitor monitor = showProgressBar.getMonitor();
					int i = 0;
					String message = "车牌下载完成。";
					for (DownloadDeviceInfo downloadDeviceInfo : listSelected) {
						List<PlateDownload> listPlate = getPlateDownloads(downloadDeviceInfo.getCarpark());
						System.out.println(listPlate.size());
						if(StrUtil.isEmpty(listPlate)){
							continue;
						}
						if (showProgressBar.isDisposed()) {
							break;
						}
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
							if (listPlate.size() > 6000) {
								int j = listPlate.size() - 6000;
								listPlate = listPlate.subList(0, 6000);
								message += "\n设备" + downloadDeviceInfo.getIp() + "下满6000条,有" + j + "条下载失败";
							}
						}
						if (type.equals(CameraTypeEnum.信路威)) {
							if (listPlate.size() > 6000) {
								int j = listPlate.size() - 6000;
								listPlate = listPlate.subList(0, 6000);
								message += "\n设备" + downloadDeviceInfo.getIp() + "下满6000条,有" + j + "条下载失败";
							}
						}
						if (type.equals(CameraTypeEnum.臻识)) {
							if (listPlate.size() > 5000) {
								int j = listPlate.size() - 5000;
								listPlate = listPlate.subList(0, 5000);
								message += "\n设备" + downloadDeviceInfo.getIp() + "下满5000条,有" + j + "条下载失败";
							}
						}
						String ip = downloadDeviceInfo.getIp();
						monitor.showMessage("正在下载车牌信息到:" + ip);
						PlateNOJNA plateNOJNA = type.getJNA(Login.injector);
						try {
							boolean openEx = plateNOJNA.openEx(ip,0, new PlateNOResult() {
								@Override
								public void invok(String ip, int channel, String plateNO, byte[] bigImage, byte[] smallImage, float rightSize) {

								}
							});
							if (!openEx) {
								throw new Exception("摄像机："+ip+"连接失败");
							}
							if (!StrUtil.isEmpty(plateNOJNA)) {
								int errorSize=plateNOJNA.plateDownload(listPlate, ip);
								if (errorSize>0) {
									message += ip + "成功：" + (listPlate.size() - errorSize)+"，失败:"+errorSize+"\t\n";
								}
							}
						} catch (Exception e) {
							errorListInfo.add(downloadDeviceInfo);
							message+=e.getMessage();
							continue;
						} finally {
							plateNOJNA.closeEx(ip);
						}
					}
					if (!showProgressBar.isDisposed()) {
						showProgressBar.finish();
					}
					String msg = message;
					Runnable runnable = new Runnable() {
						public void run() {
							commonui.info("提示", msg);
						}
					};
					model.setMsg(model.getMsg() + listSelected);
					getShell().getDisplay().asyncExec(runnable);
					System.out.println("下载花费时间：" + (System.nanoTime() - nanoTime));
				} finally {
					canClose=true;
				}
			}
		}).start();
	}

	protected List<PlateDownload> getPlateDownloads(SingleCarparkCarpark carpark) {
		List<SingleCarparkUser> findAll = sp.getCarparkUserService().findUserByNameOrCarpark(null, carpark, null);
		ArrayList<PlateDownload> list = new ArrayList<>();
		Map<String, String> map=new HashMap<>();
		for (SingleCarparkUser user : findAll) {
			String[] split = user.getPlateNo().split(",");
			if (split.length>1) {
				continue;
			}
			PlateDownload pd=new PlateDownload();
			Date validTo = user.getValidTo();
			if (validTo==null||validTo.before(new Date())) {
				pd.setUse(false);
			}
			String key = user.getPlateNo();
			if (map.get(key)==null) {
				pd.setDate(validTo);
				pd.setPlate(user.getPlateNo());
				list.add(pd);
				map.put(key, key);
			}
		}
		return list;
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
					info.setCarpark(singleCarparkDevice.getCarpark());
					list.add(info);
				}
				model.setList(list);
			}
		}
		List<DownloadDeviceInfo> readObject2 = (List<DownloadDeviceInfo>) FileUtils.readObject(DownloadPlateModel.DOWNLOAD_PLATE_DEVICES);
		if (readObject2!=null) {
			for (DownloadDeviceInfo downloadDeviceInfo : readObject2) {
				model.addInfo(downloadDeviceInfo);
			}
		}
	}

}
