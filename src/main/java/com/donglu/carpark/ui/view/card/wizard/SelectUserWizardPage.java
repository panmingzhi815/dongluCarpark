package com.donglu.carpark.ui.view.card.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;

import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.beans.BeansObservables;

import com.donglu.carpark.ui.common.AbstractListPresenter.SelectedRun;
import com.donglu.carpark.ui.view.user.UserPresenter;
import com.donglu.carpark.util.TextUtils;
import com.dongluhitec.card.domain.db.singlecarpark.CarTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCard;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser.CarparkSlotTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser.UserType;
import com.dongluhitec.card.domain.util.StrUtil;

import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;

public class SelectUserWizardPage extends WizardPage {
	SingleCarparkCard model;
	private UserPresenter userPresenter;

	
	/**
	 * Create the wizard.
	 * @param model 
	 */
	public SelectUserWizardPage(SingleCarparkCard model,UserPresenter userPresenter) {
		super("wizardPage");
		this.model=model;
		this.userPresenter = userPresenter;
		if (StrUtil.isEmpty(model.getId())) {
			setDescription("添加固定用户");
		}else{
			setDescription("修改固定用户");
		}
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		userPresenter.go(container);
		userPresenter.getListPresenter().setSelectedRun(new SelectedRun<SingleCarparkUser>() {
			@Override
			public void run(List<SingleCarparkUser> list) {
				System.out.println("用户选择界面选择事件="+getClass()+"=="+list);
				if (StrUtil.isEmpty(list)) {
					return;
				}
				model.setUser(list.get(0));
			}
		});
		initDataBindings();
	}

	@Override
	public AddCardWizard getWizard() {
		
		return (AddCardWizard) super.getWizard();
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		return bindingContext;
	}
}
