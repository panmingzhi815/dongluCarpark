package com.donglu.carpark.ui.view.card;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.common.View;
import com.donglu.carpark.ui.view.card.wizard.AddCardModel;
import com.donglu.carpark.ui.view.card.wizard.AddCardWizard;
import com.donglu.carpark.ui.view.card.wizard.EditCardWizard;
import com.donglu.carpark.util.CarparkUtils;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCard;
import com.dongluhitec.card.domain.db.singlecarpark.SystemOperaLogTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class CardListPresenter extends AbstractListPresenter<SingleCarparkCard> {
	CardListView view;

	String identifier;
	String serialNumber;
	@Inject
	private CommonUIFacility commonui;
	@Inject
	private CarparkDatabaseServiceProvider sp;


	
	
	@Override
	public void add() {
		try {
			AddCardModel model=new AddCardModel();
			SingleCarparkCard card=sp.getCardService().findLastCard();
			AddCardWizard addCardWizard = new AddCardWizard(model,sp);
			if (card!=null) {
				model.setIdentifier(addCardWizard.getNextIdentifier(card.getIdentifier()));
			}
			model = (AddCardModel) commonui.showWizard(addCardWizard);
			if(model==null){
				return;
			}
			List<SingleCarparkCard> list = model.getList();
			sp.getCardService().saveCard(list);
			refresh();
		} catch (Exception e) {
			commonui.error("", "添加卡片失败", e);
		}

	}

	@Override
	public void delete(List<SingleCarparkCard> list) {
		try {
			if (StrUtil.isEmpty(list)) {
				return;
			}
			boolean confirm = commonui.confirm("提示", "确认删除选中的"+list.size()+"个卡片信息吗？");
			if (!confirm) {
				return;
			}
			for (SingleCarparkCard visitor : list) {
				sp.getCardService().deleteCard(Arrays.asList(visitor));
				sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.卡片, "删除卡片："+visitor.getSerialNumber(), CarparkUtils.getLoginUserName());
			}
			refresh();
		} catch (Exception e) {
			commonui.error("成功", "删除成功", e);
		}
	}

	@Override
	public void refresh() {
		view.getModel().setList(new ArrayList<>());
		String identifier2 = StrUtil.isEmpty(identifier)?"":"%"+identifier+"%";
		String serialNumber2 = StrUtil.isEmpty(serialNumber)?"":"%"+serialNumber+"%";
		List<SingleCarparkCard> findVisitorByLike = sp.getCardService().findCard(0, 50000, identifier2, serialNumber2);
		view.getModel().setList(findVisitorByLike);
		view.getModel().setCountSearch(findVisitorByLike.size());
		view.getModel().setCountSearchAll(findVisitorByLike.size());
	}

	public void search(String userName, String plateNo) {
		this.identifier = userName;
		this.serialNumber = plateNo;
		refresh();
	}

	public void edit() {
		try {
			List<SingleCarparkCard> selected = view.getModel().getSelected();
			if (StrUtil.isEmpty(selected)) {
				return;
			}
			SingleCarparkCard card = selected.get(0);
			
			EditCardWizard wizard = new EditCardWizard(card.clone(), sp);
			card = (SingleCarparkCard) commonui.showWizard(wizard);
			if(card==null){
				return;
			}
			sp.getCardService().saveCard(Arrays.asList(card));
			refresh();
		} catch (Exception e) {
			commonui.error("错误", "修改失败", e);
		}
	}

	@Override
	protected View createView(Composite c) {
		view = new CardListView(c, c.getStyle());
		return view;
	}
	@Override
	protected void continue_go() {
		view.setTableTitle("卡片列表");
		view.setShowMoreBtn(true);
		new Thread(new Runnable() {
			public void run() {
				refresh();
			}
		}).start();
	}

	public void searchMore() {
		
	}

}
