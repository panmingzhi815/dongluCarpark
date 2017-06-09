package com.donglu.carpark.ui.view.history;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractView;
import com.donglu.carpark.ui.common.Presenter;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkModuleEnum;
import com.dongluhitec.card.domain.util.StrUtil;

import org.eclipse.swt.layout.FillLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class HistoryViewer extends AbstractView {

	private TabFolder tabFolder;
	private List<SingleCarparkModuleEnum> listModel;
	public HistoryViewer(Composite parent) {
		super(parent, parent.getStyle());
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		tabFolder = new TabFolder(this, SWT.BOTTOM);
		
		
	}
	private void init(List<SingleCarparkModuleEnum> listModel,List<Presenter> listPresenters){
		this.listModel = listModel;
		for (int i = 0; i < listModel.size(); i++) {
			SingleCarparkModuleEnum moduleEnum = listModel.get(i);
			TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
			tabItem.setText(moduleEnum.name());
			
			Composite composite = new Composite(tabFolder, SWT.NONE);
			tabItem.setControl(composite);
			composite.setLayout(new FillLayout(SWT.HORIZONTAL));
			listPresenters.get(i).go(composite);
		}
	}
	
	public void selection(SingleCarparkModuleEnum module){
		if (StrUtil.isEmpty(listModel)) {
			return;
		}
		tabFolder.setSelection(listModel.indexOf(module));
	}
}
