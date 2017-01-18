package com.donglu.carpark.ui.view.card;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkUserService;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.common.View;
import com.donglu.carpark.ui.view.card.wizard.AddCardWizard;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCard;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.google.inject.Inject;

public class CardListPresenter extends AbstractListPresenter<SingleCarparkCard> {
	
	private CarparkDatabaseServiceProvider sp;
	private CommonUIFacility commonui;
	private String serialNumber;
	private List<SingleCarparkUser> listUser;
	private CarparkUserService carparkUserService;
	private String userName;
	private String plateNo;
	@Inject
	public CardListPresenter(CarparkDatabaseServiceProvider sp,CommonUIFacility commonui) {
		this.sp = sp;
		this.commonui = commonui;
		carparkUserService = sp.getCarparkUserService();
	}

	@Override
	protected View createView(Composite c) {
		return new CardListView(c, c.getStyle());
	}
	@Override
	public List<SingleCarparkCard> getListInput(int size) {
		return carparkUserService.findSingleCarparkCardBySearch(size, 500, serialNumber, userName,plateNo);
	}
	@Override
	public int getTotalSize() {
		return carparkUserService.countSingleCarparkCardBySearch(serialNumber, userName,plateNo).intValue();
	}
	
	public void search(String serialNumber,List<SingleCarparkUser> listUser){
		this.serialNumber = serialNumber;
		this.listUser = listUser;
		refresh();
	}
	@Override
	public void add() {
		SingleCarparkCard model=new SingleCarparkCard();
		addAndEdit(model);
	}

	/**
	 * @param model
	 */
	public void addAndEdit(SingleCarparkCard model) {
		try {
			AddCardWizard w = new AddCardWizard(model);
			SingleCarparkCard m = (SingleCarparkCard) commonui.showWizard(w);
			if (m==null) {
				return;
			}
			sp.getCarparkUserService().saveSingleCarparkCard(m);
			refresh();
			commonui.info("提示", "保存成功");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("保存卡片信息是发生错误"+model.getId(),e);
		}
	}
	@Override
	protected void edit(SingleCarparkCard t) {
		addAndEdit(t);
	}
	@Override
	public void delete(List<SingleCarparkCard> list) {
		try {
			for (SingleCarparkCard singleCarparkCard : list) {
				carparkUserService.deleteSingleCarparkCard(singleCarparkCard);
			}
			commonui.info("提示", "删除完成");
			refresh();
		} catch (Exception e) {
			e.printStackTrace();
			commonui.error("失败", "删除时发生错误",e);
		}
	}

	public void search(String serialNumber, String userName, String plateNo) {
		String s="%";
		this.serialNumber = serialNumber;
		this.userName = s+userName+s;
		this.plateNo = s+plateNo+s;
		refresh();
	}
}
