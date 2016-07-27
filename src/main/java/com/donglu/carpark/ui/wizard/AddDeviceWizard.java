package com.donglu.carpark.ui.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.ui.Login;
import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;


public class AddDeviceWizard extends Wizard implements AbstractWizard{
	AddDeviceModel model;
	private AddDeviceBasicPage page;
	
	public AddDeviceWizard(AddDeviceModel model) {
		this.model=model;
		setWindowTitle("添加设备");
		model.setVoice(model.getVolume()+"");
		if(model.getType().equals("485")){
//			model.setSerialAddress(model.getAddressLabel());
		}else{
			if (!StrUtil.isEmpty(model.getTcpLabel())) {
				model.setTcpAddress(model.getTcpLabel());
			}
		}
		
	}

	@Override
	public void addPages() {
		String string = Login.injector.getInstance(CarparkMainModel.class).getMapSystemSetting().get(SystemSettingTypeEnum.允许设备限时);
		page = new AddDeviceBasicPage(model,!string.equals("true"));
		addPage(page);
		getShell().setImage(JFaceUtil.getImage("carpark_32"));
	}

	@Override
	public boolean performFinish() {
		SingleCarparkCarpark carpark = model.getCarpark();
		String identifire = model.getIdentifire();
		String roadType = model.getRoadType();
		
		
		if (StrUtil.isEmpty(model.getName())||StrUtil.isEmpty(model.getIp())||
				StrUtil.isEmpty(roadType)||StrUtil.isEmpty(identifire)||StrUtil.isEmpty(carpark)) {
			page.setErrorMessage("请填写完整信息");
			return false;
		}
		if (model.getType().equals("tcp")) {
			if (!StrUtil.isEmpty(model.getTcpAddress())) {
				model.setLinkAddress(model.getTcpAddress()+":10001");
			}else{
				model.setLinkAddress(null);
			}
		}else if(model.getType().equals("485")){
			if (model.getSerialAddress()!=null) {
				model.setLinkAddress(model.getSerialAddress());
			}else{
				model.setLinkAddress(null);
			}
		}else if(StrUtil.isEmpty(model.getVoice())){
			page.setErrorMessage("请选择语言音量");
			return false;
		}
		if (model.getAdvertise().length()>39) {
			page.setErrorMessage("平时语音不能超过39个汉字");
			return false;
		}
		String voice = model.getVoice();
		
		model.setVolume(Integer.valueOf(voice));
		return true;
	}

	@Override
	public Object getModel() {
		
		return model;
	}

}
