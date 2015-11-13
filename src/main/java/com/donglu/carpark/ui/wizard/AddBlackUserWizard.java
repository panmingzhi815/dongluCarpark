package com.donglu.carpark.ui.wizard;

import org.eclipse.jface.wizard.Wizard;
import org.joda.time.DateTime;

import com.donglu.carpark.util.CarparkUtils;
import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkBlackUser;
import com.dongluhitec.card.domain.util.StrUtil;


public class AddBlackUserWizard extends Wizard implements AbstractWizard{
	SingleCarparkBlackUser model;
	private AddBlackUserWizardPage page;
	public AddBlackUserWizard(SingleCarparkBlackUser model) {
		this.model=model;
		if (StrUtil.isEmpty(model.getPlateNO())) {
			setWindowTitle("添加黑名单");
		}else{
			setWindowTitle("修改黑名单");
		}
		
	}

	@Override
	public void addPages() {
		page = new AddBlackUserWizardPage(model);
		addPage(page);
		getShell().setImage(JFaceUtil.getImage("carpark_32"));
	}

	@Override
	public boolean performFinish() {
		if (StrUtil.isEmpty(model.getPlateNO())) {
			page.setErrorMessage("车牌不能为空");
			return false;
		}
		if (!model.getPlateNO().matches(CarparkUtils.PLATENO_REGEX)) {
			page.setErrorMessage("车牌格式不正确");
			return false;
		}
		String hoursStartLabel = model.getHoursStartLabel();
		String hoursEndLabel = model.getHoursEndLabel();
		String minuteStartLabel = model.getMinuteStartLabel();
		String minuteEndLabel = model.getMinuteEndLabel();
		
		int hs=0 ;
		int ms=0 ;
		int he=0 ;
		int me=0 ;
		 try {
			hs = Integer.parseInt(hoursStartLabel);
			 ms = Integer.parseInt(minuteStartLabel);
			 he = Integer.parseInt(hoursEndLabel);
			 me = Integer.parseInt(minuteEndLabel);
			if (hs<0||hs>=24) {
				 page.setErrorMessage("请输入正确时间");
					return false;
			}
			if (he<0||he>=24) {
				 page.setErrorMessage("请输入正确时间");
					return false;
			}
			if (ms<0||ms>=60) {
				 page.setErrorMessage("请输入正确时间");
					return false;
			}
			if (me<0||me>=60) {
				 page.setErrorMessage("请输入正确时间");
					return false;
			}
			DateTime start = new DateTime(2015,1,1,hs,ms);
			DateTime end = new DateTime(2015,1,1,he,me);
			if (end.isBefore(start.getMillis())) {
				page.setErrorMessage("请输入正确时间段");
				return false;
			}
			model.setHoursStart(hs);
			model.setHoursEnd(he);
			model.setMinuteEnd(me);
			model.setMinuteStart(ms);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			page.setErrorMessage("时间请输入数字");
			return false;
		}
		return true;
	}

	public Object getModel() {
		
		return model;
	}

}
