package com.donglu.carpark.wizard;

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

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.exception.DongluAppException;
import com.dongluhitec.card.domain.util.StrUtil;

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
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class InOutHistoryDetailWizardPage extends WizardPage {
	private DataBindingContext m_bindingContext;
	private SingleCarparkInOutHistory model;
	private Text text;
	private Text text_1;
	private String file;
	private Label lbl_bigImg;
	private Label lbl_smallImg;
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
		composite_1.setLayout(new GridLayout(3, false));
		
		Composite composite_3 = new Composite(composite_1, SWT.NONE);
		GridData gd_composite_3 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_composite_3.widthHint = 232;
		composite_3.setLayoutData(gd_composite_3);
		composite_3.setLayout(new GridLayout(2, false));
		
		Label lblNewLabel = new Label(composite_3, SWT.NONE);
		lblNewLabel.setText("车牌");
		
		text = new Text(composite_3, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text.setEditable(false);
		
		Label lblNewLabel_2 = new Label(composite_3, SWT.NONE);
		lblNewLabel_2.setText("用户名");
		
		text_1 = new Text(composite_3, SWT.BORDER);
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_1.setEditable(false);
		
		Composite composite_2 = new Composite(composite_1, SWT.NONE);
		composite_2.setLayout(new FillLayout(SWT.HORIZONTAL));
		GridData gd_composite_2 = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_composite_2.widthHint = 262;
		composite_2.setLayoutData(gd_composite_2);
		
		lbl_smallImg = new Label(composite_2, SWT.NONE);
		lbl_smallImg.setBounds(0, 0, 249, 60);
		
		Button button = new Button(composite_1, SWT.NONE);
		button.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getWizard().init();
			}
		});
		button.setText("刷新");
		Composite composite_4 = new Composite(composite, SWT.NONE);
		composite_4.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite_4.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		lbl_bigImg = new Label(composite_4, SWT.NONE);
		lbl_bigImg.setBounds(0, 0, 550, 350);
		setBigImg();
		setSmallImg();
		m_bindingContext = initDataBindings();
	}
	
	public void setBigImg(){
		String bigImg ="/img/"+ model.getBigImg();
		if (!StrUtil.isEmpty(file)) {
			bigImg=file+bigImg;
		}
		try {
			Image image2 = getImage(bigImg, lbl_bigImg, getShell());
			lbl_bigImg.setImage(image2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public Image getImage(String fileName,Label insmallimg, Shell shell) throws IOException {
//		byte[] readAllBytes = java.nio.file.Files.readAllBytes(Paths.get(fileName));
		
		try(FileInputStream fis=new FileInputStream(new File(fileName))) {
			Image img = new Image(shell.getDisplay(), fis);
			Rectangle bounds = insmallimg.getBounds();
			ImageData id = img.getImageData().scaledTo(bounds.width, bounds.height);
			Image createImg = new Image(shell.getDisplay(), id);
			img.dispose();
			return createImg;
		} catch (Exception e) {
			throw new DongluAppException("图片转换错误", e);
		} finally {
		}
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
			IObservableValue observeTextTextObserveWidget = WidgetProperties.text(SWT.Modify).observe(text);
			IObservableValue plateNoModelObserveValue = BeanProperties.value("plateNo").observe(model);
			bindingContext.bindValue(observeTextTextObserveWidget, plateNoModelObserveValue, null, null);
			//
			IObservableValue observeTextText_1ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_1);
			IObservableValue userNameModelObserveValue = BeanProperties.value("userName").observe(model);
			bindingContext.bindValue(observeTextText_1ObserveWidget, userNameModelObserveValue, null, null);
		//
		return bindingContext;
	}

	@Override
	public InOutHistoryDetailWizard getWizard() {
		
		return (InOutHistoryDetailWizard) super.getWizard();
	}

	public void setSmallImg() {
		String bigImg ="/img/"+ model.getSmallImg();
		if (!StrUtil.isEmpty(file)) {
			bigImg=file+bigImg;
		}
		try {
			Image image2 = getImage(bigImg, lbl_smallImg, getShell());
			lbl_smallImg.setImage(image2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
