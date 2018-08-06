package com.donglu.carpark.ui.view.history;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.Login;
import com.donglu.carpark.ui.common.AbstractView;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkModuleEnum;
import com.dongluhitec.card.domain.util.StrUtil;

import org.eclipse.swt.layout.FillLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.wb.swt.SWTResourceManager;

public class HistoryViewer extends AbstractView {

	private TabFolder tabFolder;
	private List<SingleCarparkModuleEnum> listModel;
	private Map<SingleCarparkModuleEnum, Integer> mapModuleIndex=new HashMap<>();
	public HistoryViewer(Composite parent) {
		super(parent, parent.getStyle());
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		tabFolder = new TabFolder(this, SWT.BOTTOM);
		tabFolder.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 9, SWT.BOLD));
		tabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int selectionIndex = tabFolder.getSelectionIndex();
				SingleCarparkModuleEnum moduleEnum = listModel.get(selectionIndex);
				if (mapModuleIndex.get(moduleEnum)==null) {
					Composite composite = new Composite(tabFolder, SWT.NONE);
					tabFolder.getItems()[selectionIndex].setControl(composite);
					composite.setLayout(new FillLayout(SWT.HORIZONTAL));
					Login.injector.getInstance(moduleEnum.getPresenter()).go(composite);
					composite.layout();
					tabFolder.layout();
					mapModuleIndex.put(moduleEnum, selectionIndex);
				}
			}
		});
		
	}
	protected void init(List<SingleCarparkModuleEnum> listModel){
		this.listModel = listModel;
		for (int i = 0; i < listModel.size(); i++) {
			SingleCarparkModuleEnum moduleEnum = listModel.get(i);
			TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
			tabItem.setText(moduleEnum.getModuleName());
			
//			Composite composite = new Composite(tabFolder, SWT.NONE);
//			tabItem.setControl(composite);
//			composite.setLayout(new FillLayout(SWT.HORIZONTAL));
//			Login.injector.getInstance(moduleEnum.getPresenter()).go(composite);
		}
	}
	
	public void selection(SingleCarparkModuleEnum module){
		if (StrUtil.isEmpty(listModel)) {
			return;
		}
		tabFolder.setSelection(listModel.indexOf(module));
	}
}
