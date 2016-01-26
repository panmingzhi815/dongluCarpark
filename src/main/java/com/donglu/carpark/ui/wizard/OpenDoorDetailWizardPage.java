package com.donglu.carpark.ui.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;

import com.donglu.carpark.util.CarparkUtils;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkOpenDoorLog;
import org.eclipse.swt.layout.GridData;


import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DisposeEvent;

public class OpenDoorDetailWizardPage extends WizardPage {
	private DataBindingContext m_bindingContext;
	private SingleCarparkOpenDoorLog model;
	private static Image image;
	private Label label;
	/**
	 * Create the wizard.
	 * @param model 
	 * @param file 
	 */
	public OpenDoorDetailWizardPage(SingleCarparkOpenDoorLog model) {
		super("wizardPage");
		setDescription("开闸抓拍图片");
		this.model=model;
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	@Override
	public void createControl(Composite parent) {
		parent.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				System.out.println("OpenDoorDetailWizardPage is dispose");
				if (image!=null) {
					image.dispose();
					image=null;
				}
			}
		});
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(1, false));
		
		Composite composite = new Composite(container, SWT.BORDER);
		composite.setLayout(new GridLayout(1, false));
		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_composite.widthHint = 554;
		gd_composite.heightHint = 427;
		composite.setLayoutData(gd_composite);
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		composite_1.setLayout(new GridLayout(1, false));
		
		Button button = new Button(composite_1, SWT.NONE);
		button.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setImage();
			}
		});
		button.setText("刷新");
		
		label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		label.setBounds(0, 0, 500, 300);
		setImage();
		m_bindingContext = initDataBindings();
	}
	
	private void setImage() {
		try {
			if (image!=null) {
				image.dispose();
				image=null;
			}
			label.setImage(CarparkUtils.getImage(CarparkUtils.getImageByte(model.getImage()), label, getShell()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	@Override
	public InOutHistoryDetailWizard getWizard() {
		
		return (InOutHistoryDetailWizard) super.getWizard();
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		return bindingContext;
	}
}
