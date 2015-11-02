package com.donglu.carpark.ui.wizard.sn;


import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.core.crypto.appauth.AppAuthorization;
import com.dongluhitec.core.crypto.appauth.AppVerifier;
import com.dongluhitec.core.crypto.appauth.ModuleEnum;
import com.google.common.base.Charsets;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.core.databinding.beans.BeanProperties;


/**
 * 用来导入用户注册码文件
 *
 * @author wudong
 */
public class ImportSNPage extends WizardPage {
	private DataBindingContext m_bindingContext;
	private Text text;
	private AppVerifier verifier;
	private Text text_companyName;
	private Text text_projectName;
//	private Text text_validFrom;
//	private Text text_validTo;
	private Text text_modules;

	private ImportSNModel model;
	/**
	 * Create the wizard.
	 * @param model 
	 */
	public ImportSNPage(AppVerifier verifier, ImportSNModel model) {
		super("wizardPage");
		this.verifier = verifier;
		this.model=model;
		setTitle("注册码");
		setDescription("请输入或者选择注册码，请注意先插好加密狗后再进行这一步");
	}

	/**
	 * Create contents of the wizard.
	 *
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		GridLayout gd = new GridLayout(2, false);
		int margin = 10;
		gd.marginBottom = margin;
		gd.marginTop = margin;
		gd.marginLeft = margin;
		gd.marginRight = margin;
		container.setLayout(gd);

		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setText("输入注册码");
		
				Button btnNewButton = new Button(container, SWT.NONE);
				btnNewButton.setText("选择文件");
				btnNewButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
						final String open = dialog.open();
						selectAndReadFile(open);
					}
				});

		text = new Text(container, SWT.BORDER|SWT.WRAP|SWT.MULTI);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		Label label_6 = new Label(container, SWT.NONE);
		label_6.setText("授权信息");
		new Label(container, SWT.NONE);
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				verify(text.getText(),false);
			}
		});
		
		Composite composite = new Composite(container, SWT.BORDER);
		composite.setLayout(new GridLayout(4, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		
		Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("公司名");
		
		text_companyName = new Text(composite, SWT.BORDER);
		text_companyName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_companyName.setEditable(false);
		
		Label label_1 = new Label(composite, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("项目名称");
		
		text_projectName = new Text(composite, SWT.BORDER);
		text_projectName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_projectName.setEditable(false);
		
//		Label label_2 = new Label(composite, SWT.NONE);
//		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
//		label_2.setText("有效期起");
//		
//		text_validFrom = new Text(composite, SWT.BORDER);
//		text_validFrom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
//		text_validFrom.setEditable(false);
//		
//		Label label_3 = new Label(composite, SWT.NONE);
//		label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
//		label_3.setText("有效期至");
//		
//		text_validTo = new Text(composite, SWT.BORDER);
//		text_validTo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
//		text_validTo.setEditable(false);
		
		Label label_4 = new Label(composite, SWT.NONE);
		label_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_4.setText("授权模块");
		
		text_modules = new Text(composite, SWT.BORDER);
		text_modules.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
		text_modules.setEditable(false);
		m_bindingContext = initDataBindings();

//		this.setPageComplete(false);
	}

	/**
	 * Select the file.
	 */
	public void selectAndReadFile(String open) {
		

		if (open != null) {
			//check if the files are too big.
			try {
				final Path path = FileSystems.getDefault().getPath(open);
				final long size = Files.size(path);
				if (size > 5000) {
					MessageBox mb = new MessageBox(this.getShell(), SWT.ICON_ERROR);
					mb.setMessage("备选文件非法，请确认选择了正确的注册码文件");
					mb.setText("错误");
					mb.open();
					return;
				}

				final String s = com.google.common.io.Files.toString(path.toFile(), Charsets.UTF_8);
				text.setText(s);
				verify(text.getText(),true);
			} catch (IOException e) {
				MessageBox mb = new MessageBox(this.getShell(), SWT.ICON_ERROR);
				mb.setMessage("无法读取选择的文件");
				mb.setText("错误");
				mb.open();
				return;
			}
		}
	}

	/**
	 * 验证输入的注册码
	 *
	 * @param sn
	 * @return
	 */
	private void verify(String sn,Boolean write) {
		this.setErrorMessage(null);
		final AppAuthorization decrypt = verifier.decrypt(sn);
		this.setPageComplete(decrypt != null);
		if (decrypt == null) {
			this.setErrorMessage("无法验证注册码，请检查输入注册码是否正确，以及加密狗是否安装好");
		} else {
			//saveCardUserGroup the file to
			try {
				if(write){					
					com.google.common.io.Files.write(sn, new File(AppVerifier.App_Module_Config), Charsets.UTF_8);
				}
				this.setPageComplete(true);
				final DateFormat dateInstance = DateFormat.getDateInstance();
				text_companyName.setText(decrypt.getCompanyName());
//				text_validFrom.setText(dateInstance.format(decrypt.getDateOfIssue()));
//				text_validTo.setText(dateInstance.format(decrypt.getDateOfExpire()));
				text_modules.setText(Arrays.toString(decrypt.getAuthorizations()));
				//TODO
				text_projectName.setText(decrypt.getProjectId());
				
				Date dateOfExpire = decrypt.getDateOfExpire();
				boolean before = StrUtil.getTodayBottomTime(dateOfExpire).before(new Date());
				if (before) {
					this.setErrorMessage("注册码已到期");
					this.setPageComplete(!before);
				}
			} catch (Exception e) {
				this.setErrorMessage("无法写入注册文件，请检查文件权限");
			}
		}
//		text_companyName.setText("1111");
//		text_modules.setText("1111");
//		text_projectName.setText("1111");
	}

	public Text getText() {
		return text;
	}

	public Text getText_companyName() {
		return text_companyName;
	}

	public Text getText_projectName() {
		return text_projectName;
	}

	public Text getText_modules() {
		return text_modules;
	}
	public Date getValidTo(){
		String sn = text.getText();
		final AppAuthorization decrypt = verifier.decrypt(sn);
		try {
//			Date dateOfExpire=new DateTime(2015,8,5,13,15).toDate();
			Date dateOfExpire = decrypt.getDateOfExpire();
			return dateOfExpire;
		} catch (Exception e) {
			return null;
		}
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTextObserveWidget = WidgetProperties.text(SWT.Modify).observe(text);
		IObservableValue snModelObserveValue = BeanProperties.value("sn").observe(model);
		bindingContext.bindValue(observeTextTextObserveWidget, snModelObserveValue, null, null);
		//
		return bindingContext;
	}
}
