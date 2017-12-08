package com.donglu.carpark.ui.view.card.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Table;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.wb.swt.SWTResourceManager;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCard;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.common.base.Strings;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;

public class AddCardWizardPage extends WizardPage {
	private DataBindingContext m_bindingContext;
	private AddCardModel model;
	
	
	private Text txt_identifier;
	private Text txt_serialNumber;
	private Table table;
	private TableViewer tableViewer;

	protected AddCardWizardPage(AddCardModel model) {
		super("添加卡片");
		setTitle("添加卡片");
		setDescription("");
		this.model = model;
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, NONE);
		setControl(composite);
		composite.setLayout(new GridLayout(2, false));
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(new GridLayout(3, false));
		GridData gd_composite_1 = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_composite_1.widthHint = 303;
		composite_1.setLayoutData(gd_composite_1);
		
		Label label_1 = new Label(composite_1, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("卡片内码");
		
		txt_serialNumber = new Text(composite_1, SWT.BORDER);
		txt_serialNumber.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				System.out.println(e.keyCode);
				if(e.keyCode==StrUtil.SMAIL_KEY_ENTER||e.keyCode==StrUtil.BIG_KEY_ENTER){
					addCard();
				}
			}
		});
		txt_serialNumber.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		txt_serialNumber.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txt_serialNumber.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				setPageComplete(StrUtil.isEmpty(txt_serialNumber.getText()));		
			}
		});
		txt_serialNumber.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				setPageComplete(StrUtil.isEmpty(txt_serialNumber.getText()));
			}
			@Override
			public void focusLost(FocusEvent e) {
				setPageComplete(true);
			}
		});
		new Label(composite_1, SWT.NONE);
		
		Label label = new Label(composite_1, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label.setText("卡片编号");
		
		txt_identifier = new Text(composite_1, SWT.BORDER);
		txt_identifier.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		txt_identifier.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		txt_identifier.addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e) {
				if(e.keyCode==StrUtil.SMAIL_KEY_ENTER||e.keyCode==StrUtil.BIG_KEY_ENTER){
					e.doit=false;
				}
			}
		});
		
		Button button = new Button(composite_1, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addCard();
			}
		});
		button.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		button.setText("添加");
		
		Composite composite_2 = new Composite(composite, SWT.NONE);
		composite_2.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		tableViewer = new TableViewer(composite_2, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				SingleCarparkCard selected = model.getSelected();
				if(selected==null){
					return;
				}
				model.removeCard(selected);
			}
		});
		table.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		
		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tableColumn = tableViewerColumn.getColumn();
		tableColumn.setWidth(100);
		tableColumn.setText("卡片编号");
		
		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tableColumn_1 = tableViewerColumn_1.getColumn();
		tableColumn_1.setWidth(195);
		tableColumn_1.setText("卡片内码");
		m_bindingContext = initDataBindings();
	}

	protected void addCard() {
		String identifier = txt_identifier.getText();
		String serialNumber=txt_serialNumber.getText();
		if(StrUtil.isEmpty(identifier)||StrUtil.isEmpty(serialNumber)){
			return;
		}
		serialNumber=Strings.padStart(serialNumber, 16, '0').toUpperCase();
		System.out.println(serialNumber);
		if(!serialNumber.matches("[0-9A-F]{16}")){
			setErrorMessage("卡片格式不正确！");
			return;
		}
		
		SingleCarparkCard singleCarparkCard = new SingleCarparkCard(identifier,serialNumber);
		if(model.getList().contains(singleCarparkCard)){
			setErrorMessage("卡片已存在列表中！");
			return;
		}
		
		boolean checkCardExist = getWizard().checkCardExist(identifier, serialNumber);
		if (!checkCardExist) {
			return;
		}
		
		model.addCard(singleCarparkCard);
		txt_identifier.setText(getWizard().getNextIdentifier(identifier));
		txt_serialNumber.setText("");
		txt_serialNumber.setFocus();
		setErrorMessage(null);
	}
	
	@Override
	public AddCardWizard getWizard() {
		return (AddCardWizard) super.getWizard();
	}
	
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTxt_identifierObserveWidget = WidgetProperties.text(SWT.Modify).observe(txt_identifier);
		IObservableValue identifierModelObserveValue = BeanProperties.value("identifier").observe(model);
		bindingContext.bindValue(observeTextTxt_identifierObserveWidget, identifierModelObserveValue, null, null);
		//
		IObservableValue observeTextTxt_serialNumberObserveWidget = WidgetProperties.text(SWT.Modify).observe(txt_serialNumber);
		IObservableValue serialNumberModelObserveValue = BeanProperties.value("serialNumber").observe(model);
		bindingContext.bindValue(observeTextTxt_serialNumberObserveWidget, serialNumberModelObserveValue, null, null);
		//
		ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
		IObservableMap[] observeMaps = BeansObservables.observeMaps(listContentProvider.getKnownElements(), SingleCarparkCard.class, new String[]{"identifier", "serialNumber"});
		tableViewer.setLabelProvider(new ObservableMapLabelProvider(observeMaps));
		tableViewer.setContentProvider(listContentProvider);
		//
		IObservableList listModelObserveList = BeanProperties.list("list").observe(model);
		tableViewer.setInput(listModelObserveList);
		//
		return bindingContext;
	}
}
