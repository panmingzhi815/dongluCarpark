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

public class InOutHistoryDetailWizardPage extends WizardPage {
	private DataBindingContext m_bindingContext;
	private SingleCarparkInOutHistory model;
	private String file;
	private Label lbl_bigImg;
	private Label lbl_outSmallImg;
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
		GridData gd_composite_3 = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_composite_3.widthHint = 232;
		composite_3.setLayoutData(gd_composite_3);
		GridLayout gl_composite_3 = new GridLayout(1, false);
		gl_composite_3.verticalSpacing = 0;
		gl_composite_3.marginWidth = 0;
		gl_composite_3.marginHeight = 0;
		gl_composite_3.horizontalSpacing = 0;
		composite_3.setLayout(gl_composite_3);
		
		Label lbl_inSmallImg = new Label(composite_3, SWT.NONE);
		lbl_inSmallImg.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 1, 1));
		
		Composite composite_2 = new Composite(composite_1, SWT.NONE);
		composite_2.setLayout(new FillLayout(SWT.HORIZONTAL));
		GridData gd_composite_2 = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_composite_2.heightHint = 56;
		gd_composite_2.widthHint = 262;
		composite_2.setLayoutData(gd_composite_2);
		
		lbl_outSmallImg = new Label(composite_2, SWT.NONE);
		lbl_outSmallImg.setBounds(0, 0, 249, 60);
		
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
		try {
			Image image2 = getImage(getByte(model.getBigImg()), lbl_bigImg);
			lbl_bigImg.setImage(image2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	protected Image getImage(byte[] image, Label lbl) {
		if (image==null) {
			return null;
		}
		ByteArrayInputStream stream = null;
		try {
			stream = new ByteArrayInputStream(image);
			Image newImg = new Image(getShell().getDisplay(), stream);
			Rectangle rectangle = lbl.getBounds();
			ImageData data = newImg.getImageData().scaledTo(rectangle.width, rectangle.height);
			ImageDescriptor createFromImageData = ImageDescriptor.createFromImageData(data);
			Image createImg = createFromImageData.createImage();
			newImg.dispose();
			newImg = null;
			lbl.setText("");
			return createImg;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			if (stream!=null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	protected byte[] getByte(String img) {
		try {
			byte[] image;
			File file=new File(this.file+"/img/"+img);
			if (file.exists()) {
				image=Files.toByteArray(file);
			}else{
				String substring = img.substring(img.lastIndexOf("/")+1);
				String actionUrl = "http://"+CarparkClientConfig.getInstance().getDbServerIp()+":8899";
				image = FileuploadSend.download(actionUrl, substring);
			}
			return image;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public InOutHistoryDetailWizard getWizard() {
		
		return (InOutHistoryDetailWizard) super.getWizard();
	}

	public void setSmallImg() {
		
		try {
			Image image2 = getImage(getByte(model.getSmallImg()), lbl_outSmallImg);
			lbl_outSmallImg.setImage(image2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		return bindingContext;
	}
}
