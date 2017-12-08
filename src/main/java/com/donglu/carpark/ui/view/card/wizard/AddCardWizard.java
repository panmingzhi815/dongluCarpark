package com.donglu.carpark.ui.view.card.wizard;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.wizard.Wizard;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCard;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.common.base.Strings;

public class AddCardWizard extends Wizard implements AbstractWizard {

	private AddCardModel model;
	private CarparkDatabaseServiceProvider sp;
	private AddCardWizardPage page;

	public AddCardWizard(AddCardModel model, CarparkDatabaseServiceProvider sp) {
		this.model = model;
		this.sp = sp;
	}
	@Override
	public void addPages() {
		page = new AddCardWizardPage(model);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	@Override
	public AddCardModel getModel() {
		return model;
	}
	
	public boolean checkCardExist(String identifier,String serialNumber){
		List<SingleCarparkCard> findCard = sp.getCardService().findCard(0, 1, identifier, null);
		if(!StrUtil.isEmpty(findCard)){
			page.setErrorMessage("卡片编号已存在系统中");
			return false;
		}
		findCard = sp.getCardService().findCard(0, 1, null, serialNumber);
		if(!StrUtil.isEmpty(findCard)){
			page.setErrorMessage("卡片内码已存在系统中");
			return false;
		}
		return true;
	}
	public String getNextIdentifier(String identifier) {
		Pattern compile = Pattern.compile("[0-9]+");
		Matcher matcher = compile.matcher(identifier);
		int start=-1;
		if (matcher.find()) {
			start=matcher.start();
		}
		if(start>-1){
			String  numString= identifier.substring(start);
			try {
				Integer integer = Integer.valueOf(numString);
				return (start>0?identifier.substring(0, start):"")+Strings.padStart((integer+1)+"", numString.length(), '0');
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "";
	}
	
}
