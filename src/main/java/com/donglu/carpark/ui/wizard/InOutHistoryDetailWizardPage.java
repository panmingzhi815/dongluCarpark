package com.donglu.carpark.ui.wizard;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.donglu.carpark.server.imgserver.FileuploadSend;
import com.donglu.carpark.ui.CarparkClientConfig;
import com.donglu.carpark.util.CarparkUtils;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.exception.DongluAppException;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.common.io.Files;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.FillLayout;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DisposeEvent;

public class InOutHistoryDetailWizardPage extends WizardPage {
	private DataBindingContext m_bindingContext;
	private SingleCarparkInOutHistory model;
	private String file;
	private Label lbl_inBigImg;
	private Label lbl_outBigImg;
	private SashForm sashForm;
	private Image inImage;
	private Image outImage;
	/**
	 * Create the wizard.
	 * @param model 
	 * @param file 
	 */
	public InOutHistoryDetailWizardPage(SingleCarparkInOutHistory model, String file) {
		super("wizardPage");
		setTitle("进出场抓拍信息");
		setDescription("进出场抓拍信息");
		this.model=model;
		this.file=file;
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		parent.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				System.out.println("InOutHistoryDetailWizardPage is dispose");
				if (inImage != null) {
					inImage.dispose();
					inImage = null;
				}
				if (outImage != null) {
					outImage.dispose();
					outImage = null;
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
		
		sashForm = new SashForm(composite, SWT.NONE);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite composite_4 = new Composite(sashForm, SWT.NONE);
		composite_4.setLayout(new GridLayout(1, false));
		
		lbl_inBigImg = new Label(composite_4, SWT.NONE);
		lbl_inBigImg.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite composite_5 = new Composite(sashForm, SWT.NONE);
		composite_5.setLayout(new GridLayout(1, false));
		
		lbl_outBigImg = new Label(composite_5, SWT.NONE);
		lbl_outBigImg.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		lbl_outBigImg.setSize(400,300);
		lbl_inBigImg.setSize(400,300);
		sashForm.setWeights(new int[] {1, 1});
		setImage();
		m_bindingContext = initDataBindings();
	}
	
	private void setImage() {
		try {
			if (inImage!=null) {
				inImage.dispose();
				inImage=null;
			}
			if (outImage!=null) {
				outImage.dispose();
				outImage=null;
			}
			
			inImage = CarparkUtils.getImage(CarparkUtils.getImageByte(file,model.getBigImg()), lbl_inBigImg, getShell());
			lbl_inBigImg.setImage(inImage);
			outImage = CarparkUtils.getImage(CarparkUtils.getImageByte(file,model.getOutBigImg()), lbl_outBigImg, getShell());
			lbl_outBigImg.setImage(outImage);
			if (StrUtil.isEmpty(outImage)) {
				sashForm.setWeights(new int[]{1,0});
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void setBigImg(){
		try {
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
