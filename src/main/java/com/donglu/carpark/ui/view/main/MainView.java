package com.donglu.carpark.ui.view.main;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.Login;
import com.donglu.carpark.ui.common.AbstractView;
import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.util.ConstUtil;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkPrivilegeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkModuleEnum;
import com.dongluhitec.card.domain.util.StrUtil;

import org.eclipse.swt.layout.FillLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.wb.swt.SWTResourceManager;

public class MainView extends AbstractView {

	private TabFolder tabFolder;
	private List<Presenter> presenters=new ArrayList<>();
	private Map<Presenter,Composite> mapViews=new HashMap<>();
	public MainView(Composite parent) {
		super(parent, parent.getStyle());
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		tabFolder.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		tabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int selectionIndex = tabFolder.getSelectionIndex();
				System.out.println(selectionIndex);
				Presenter p = presenters.get(selectionIndex);
				if (mapViews.get(p)==null) {
					System.out.println("创建页面："+selectionIndex);
					Composite c = new Composite(tabFolder, SWT.NONE);
					c.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
					tabFolder.getItems()[selectionIndex].setControl(c);
					c.setLayout(new FillLayout(SWT.HORIZONTAL));
					p.go(c);
					mapViews.put(p,c);
					c.layout();
					tabFolder.getLayout();
				}
			}
		});
	}
	
	public void addPresenter(Presenter p,String name){
		presenters.add(p);
		CarparkPrivilege privilege = p.getClass().getAnnotation(CarparkPrivilege.class);
		if (privilege!=null) {
			CarparkPrivilegeEnum privilegeEnum = privilege.value();
			boolean checkCarparkPrivilege = ConstUtil.checkCarparkPrivilege(privilegeEnum);
			if (!checkCarparkPrivilege) {
				return;
			}
		}
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(name);
		if (!StrUtil.isEmpty(presenters)) {
			return;
		}
		Composite c = new Composite(tabFolder, SWT.NONE);
		c.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		tabItem.setControl(c);
		c.setLayout(new FillLayout(SWT.HORIZONTAL));
		p.go(c);
		mapViews.put(p,c);
	}

	public void addModules(List<SingleCarparkModuleEnum> modules) {
		presenters.clear();
		for (SingleCarparkModuleEnum singleCarparkModuleEnum : modules) {
			Presenter p=Login.injector.getInstance(singleCarparkModuleEnum.getPresenter());
			addPresenter(p,singleCarparkModuleEnum.getModuleName());
		}
		tabFolder.setSelection(0);
	}

}
