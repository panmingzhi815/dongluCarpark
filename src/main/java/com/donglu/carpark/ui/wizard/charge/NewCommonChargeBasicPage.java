package com.donglu.carpark.ui.wizard.charge;

import java.text.SimpleDateFormat;
import java.util.*;

import net.miginfocom.swt.MigLayout;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.carpark.CarTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkAcrossDayTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkDurationPrice;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkDurationStandard;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkDurationTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkHolidayTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.common.base.Strings;
import com.ibm.icu.util.Calendar;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkCarType;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.swt.widgets.Combo;

/**
 * Created with IntelliJ IDEA.
 * User: panmingzhi
 * Date: 13-11-19
 * Time: 下午4:14
 * To change this template use File | Settings | File Templates.
 */
public class NewCommonChargeBasicPage extends WizardPage {
	private DataBindingContext m_bindingContext;
	
	private final String INPUTLAYOUTDATA = "w 100!";

    private final NewCommonChargeModel model;
    private Composite container;
	private TabFolder tf;
	private DateTime startTime;
	private DateTime endTime;
	
	private List<Integer> existHour = new ArrayList<Integer>();

	private Text text_crossDayUnitTimeLength;

	private Text text_crossDayUnitPrice;
	private Composite composite_1;
	private Text text;
	private Text text_1;
	private Text text_2;
	private Text text_3;
	private ComboViewer comboViewer;
	private ComboViewer comboViewer_1;
	private ComboViewer comboViewer_2;
	private ComboViewer comboViewer_3;
	private ComboViewer comboViewer_4;
	private Button button_6;

    public NewCommonChargeBasicPage(NewCommonChargeModel model) {
        super("wizardPage");
        this.model = model;

        setDescription("请输入收费标准详细参数");
    }
    
    @Override
    public void createControl(Composite parent) {
        container = new Composite(parent, SWT.BORDER);
        setControl(container);
        container.setLayout(new GridLayout(1,false));
        
        composite_1 = new Composite(container, SWT.NONE);
        composite_1.setLayout(new GridLayout(7, false));
        composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        
        Label label_1 = new Label(composite_1, SWT.NONE);
        label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        label_1.setText("收费编码");
        
        text = new Text(composite_1, SWT.BORDER);
        GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        gd_text.widthHint = 87;
        text.setLayoutData(gd_text);
        new Label(composite_1, SWT.NONE);
        
        Label label_2 = new Label(composite_1, SWT.NONE);
        label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        label_2.setText("收费名称");
        
        text_1 = new Text(composite_1, SWT.BORDER);
        GridData gd_text_1 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        gd_text_1.widthHint = 88;
        text_1.setLayoutData(gd_text_1);
        
        Label label_3 = new Label(composite_1, SWT.NONE);
        label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        label_3.setText("收费时长类型");
        
        comboViewer = new ComboViewer(composite_1, SWT.READ_ONLY);
        Combo combo = comboViewer.getCombo();
        combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        comboViewer.setContentProvider(new ArrayContentProvider());
        comboViewer.setLabelProvider(new LabelProvider());
        comboViewer.setInput(CarparkDurationTypeEnum.values());
        
        Label label_4 = new Label(composite_1, SWT.NONE);
        label_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        label_4.setText("一天最大收费");
        
        text_2 = new Text(composite_1, SWT.BORDER);
        text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        
        Label label_13 = new Label(composite_1, SWT.NONE);
        GridData gd_label_13 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_label_13.widthHint = 14;
        label_13.setLayoutData(gd_label_13);
        label_13.setText("元");
        
        Label label_11 = new Label(composite_1, SWT.NONE);
        label_11.setText("车辆类型");
        label_11.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        
        comboViewer_4 = new ComboViewer(composite_1, SWT.READ_ONLY);
        Combo combo_4 = comboViewer_4.getCombo();
        combo_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        comboViewer_4.setContentProvider(new ArrayContentProvider());
        comboViewer_4.setLabelProvider(new LabelProvider());
        comboViewer_4.setInput(model.getCarparkCarTypeList());
        
        Label label = new Label(composite_1, SWT.NONE);
        label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        label.setText("工作日类型");
        
        comboViewer_1 = new ComboViewer(composite_1, SWT.READ_ONLY);
        Combo combo_1 = comboViewer_1.getCombo();
        combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        comboViewer_1.setContentProvider(new ArrayContentProvider());
        comboViewer_1.setLabelProvider(new LabelProvider());
        comboViewer_1.setInput(CarparkHolidayTypeEnum.values());
        
        Label label_5 = new Label(composite_1, SWT.NONE);
        label_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        label_5.setText("免费时长");
        
        text_3 = new Text(composite_1, SWT.BORDER);
        GridData gd_text_3 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        gd_text_3.widthHint = 88;
        text_3.setLayoutData(gd_text_3);
        
        Label label_14 = new Label(composite_1, SWT.NONE);
        label_14.setText("分");
        
        Label label_8 = new Label(composite_1, SWT.NONE);
        label_8.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        label_8.setText("免费时长收费");
        
        comboViewer_3 = new ComboViewer(composite_1, SWT.READ_ONLY);
        Combo combo_3 = comboViewer_3.getCombo();
        combo_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        comboViewer_3.setContentProvider(new ArrayContentProvider());
        comboViewer_3.setLabelProvider(new LabelProvider());
        comboViewer_3.setInput(new String[]{"是","否"});
        
        button_6 = new Button(composite_1, SWT.CHECK);
        button_6.setText("跨天继续免费");
        new Label(composite_1, SWT.NONE);
        
        Label label_7 = new Label(composite_1, SWT.NONE);
        label_7.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        label_7.setText("跨天计费方式");
        
        comboViewer_2 = new ComboViewer(composite_1, SWT.READ_ONLY);
        Combo combo_2 = comboViewer_2.getCombo();
        combo_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        comboViewer_2.setContentProvider(new ArrayContentProvider());
        comboViewer_2.setLabelProvider(new LabelProvider());
        comboViewer_2.setInput(CarparkAcrossDayTypeEnum.values());
        new Label(composite_1, SWT.NONE);
        new Label(composite_1, SWT.NONE);
        new Label(composite_1, SWT.NONE);
        new Label(composite_1, SWT.NONE);
        new Label(composite_1, SWT.NONE);
        
        Label label_12 = new Label(composite_1, SWT.NONE);
        label_12.setText("收费时段设置");
        
        Composite composite = new Composite(composite_1, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 6, 1));
        composite.setLayout(new GridLayout(4, false));
        
        DateTime startTime = new DateTime(composite, SWT.BORDER | SWT.TIME);
        startTime.setSeconds(0);
        startTime.setMinutes(0);
        startTime.setHours(0);
        
        DateTime endTime = new DateTime(composite, SWT.BORDER | SWT.TIME);
        endTime.setSeconds(0);
        endTime.setMinutes(0);
        endTime.setHours(0);
        
        Button button_2 = new Button(composite, SWT.NONE);
        button_2.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		CarparkDurationStandard carparkDurationStandard = new CarparkDurationStandard();
            	carparkDurationStandard.setStartTime(new Date(1900, 1, 1, startTime.getHours(), startTime.getMinutes()));
            	carparkDurationStandard.setEndTime(new Date(1900, 1, 1, endTime.getHours(), endTime.getMinutes()));
            	
        		int hours = startTime.getHours();
        		int hours2 = endTime.getHours();
        		if(hours >= hours2) hours2 += 24;
        		for(int i = 1;i<= hours2 - hours;i++){
        			CarparkDurationPrice carparkDurationPrice = new CarparkDurationPrice();
        			carparkDurationPrice.setDurationLength(i);
        			carparkDurationPrice.setDurationLengthPrice(0);
        			carparkDurationStandard.addCarparkDurationPriceList(carparkDurationPrice);
        		}
        		createDurationTab(Arrays.asList(carparkDurationStandard));
        	}
        });
        button_2.setText("添加时段");
        
        Button button_3 = new Button(composite, SWT.NONE);
        button_3.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		if(tf.getItemCount() == 0) return;
				TabItem tabItem = tf.getSelection()[0];
				tabItem.dispose();
        	}
        });
        button_3.setText("删除时段");

        tf = new TabFolder(container,SWT.NONE);
        tf.setLayout(new FillLayout());
        tf.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        createDurationTab(this.model.getCarparkDurationStandards());
        
        m_bindingContext = initDataBindings();
        WizardPageSupport.create(this, m_bindingContext);
        init();
    }
    
    public List<CarparkDurationStandard> getDurationTable(){
    	List<CarparkDurationStandard> carparkDurationStandards = new ArrayList<CarparkDurationStandard>();
    	TabItem[] items = tf.getItems();
    	for (TabItem tabItem : items) {
    		CarparkDurationStandard carparkDurationStandard = new CarparkDurationStandard();
    		String[] hourPoint = tabItem.getText().split("-");
    		carparkDurationStandard.setStartTime(StrUtil.parse(hourPoint[0], "HH:mm"));
    		carparkDurationStandard.setEndTime(StrUtil.parse(hourPoint[1], "HH:mm"));
    		
    		Composite cp = (Composite)tabItem.getControl();
    		Control[] children = cp.getChildren();
    		for (Control control : children) {
				Composite cp0 = (Composite)control;
				if (cp0.getData("key") == "composite_start_step") {
					Text startStepPrice=(Text) cp0.getData("startStepPrice");
					Text startStepTime=(Text) cp0.getData("startStepTime");
					carparkDurationStandard.setStartStepPrice(Float.valueOf(startStepPrice.getText()));
					carparkDurationStandard.setStartStepTime(Integer.valueOf(startStepTime.getText()));
				}
				if(cp0.getData("key") == "composite_durationConfig"){
					Text unitDurationLength = (Text)cp0.getData("unitDurationLength");
					Text unitDurationPrice = (Text)cp0.getData("unitDurationPrice");
					Text crossDayUnitTimeLength = (Text)cp0.getData("crossDayUnitTimeLength");
					Text crossDayUnitPrice = (Text)cp0.getData("crossDayUnitPrice");
					
                    Object obj = cp0.getData("DurationId");
                    carparkDurationStandard.setId(obj == null ? null : (Long)obj);
					carparkDurationStandard.setUnitDuration(Integer.valueOf(unitDurationLength.getText()));
					carparkDurationStandard.setUnitPrice(Float.valueOf(unitDurationPrice.getText()));
					carparkDurationStandard.setCrossDayUnitDuration(Integer.valueOf(crossDayUnitTimeLength.getText()));
					carparkDurationStandard.setCrossDayPrice(Float.valueOf(crossDayUnitPrice.getText()));
					carparkDurationStandard.setStandardCode(text.getText());
				}
				if(cp0.getData("key") == "composite_durationPrice"){
					for(int i=1;i<=24;i++){
						Object data = cp0.getData(i+"");
						if(data != null){
							CarparkDurationPrice carparkDurationPrice = new CarparkDurationPrice();
							carparkDurationPrice.setDurationLength(i);
							carparkDurationPrice.setDurationLengthPrice(Float.valueOf(((Text)data).getText()));
							carparkDurationPrice.setStandardCode(text.getText());
							carparkDurationStandard.addCarparkDurationPriceList(carparkDurationPrice);
						}
					}
				}
			}
    		carparkDurationStandards.add(carparkDurationStandard);
    		
		}
    	return carparkDurationStandards;
    }
    
    public void createDurationTab(List<CarparkDurationStandard> durationStandards){
    	for (CarparkDurationStandard carparkDurationStandard : durationStandards) {
    		
    		TabItem tabItem = new TabItem(tf, SWT.NONE);
    		tabItem.setText(StrUtil.formatDate(carparkDurationStandard.getStartTime(), "HH:mm:ss") +
    				"-"+StrUtil.formatDate(carparkDurationStandard.getEndTime(), "HH:mm:ss"));
    		
    		Composite composite = new Composite(tf, SWT.NONE);
    		composite.setLayout(new GridLayout(1,false));
    		
    		Composite composite_start_step = new Composite(composite, SWT.NONE);
    		composite_start_step.setLayout(new GridLayout(4,false));
    		composite_start_step.setData("key", "composite_start_step");
    		 GridData gd_label_13 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
    	     gd_label_13.widthHint = 33;
    		Label label_9 = new Label(composite_start_step, SWT.NONE);
            label_9.setText("起步收费时长");
            Text text_startStepTime = new Text(composite_start_step, SWT.BORDER);
            text_startStepTime.setLayoutData(gd_label_13);
            text_startStepTime.setText(carparkDurationStandard.getStartStepTime()==null?"0":carparkDurationStandard.getStartStepTime()+"");
            composite_start_step.setData("startStepTime", text_startStepTime);
            
            Label label_10 = new Label(composite_start_step, SWT.NONE);
            label_10.setText("起步收费金额");
            Text text_startStepPrice = new Text(composite_start_step, SWT.BORDER);
            text_startStepPrice.setLayoutData(gd_label_13);
            text_startStepPrice.setText(carparkDurationStandard.getStartStepPrice()==null?"0.0":carparkDurationStandard.getStartStepPrice()+"");
            composite_start_step.setData("startStepPrice", text_startStepPrice);
    		
    		Composite composite_durationConfig = new Composite(composite, SWT.NONE);
    		composite_durationConfig.setData("key","composite_durationConfig");
    		composite_durationConfig.setLayout(new MigLayout("","[]5[]10[]5[]10[]5[]10[]5[]"));
            composite_durationConfig.setData("DurationId",carparkDurationStandard.getId());
            
           
            
    		Integer unitDuration = carparkDurationStandard.getUnitDuration();
    		new Label(composite_durationConfig,SWT.NONE).setText("单 位 时 长 ");
    		Text text2 = new Text(composite_durationConfig, SWT.BORDER);
    		text2.setText((unitDuration == null ? 0 : unitDuration) +"");
    		text2.setLayoutData("w 50");
    		composite_durationConfig.setData("unitDurationLength", text2);
    		
    		Float unitPrice = carparkDurationStandard.getUnitPrice();
    		new Label(composite_durationConfig,SWT.NONE).setText("单 位 金 额");
    		Text text3 = new Text(composite_durationConfig, SWT.BORDER);
    		text3.setText((unitPrice == null ? 0 : unitPrice)+"");
    		text3.setLayoutData("w 50");
    		composite_durationConfig.setData("unitDurationPrice", text3);
    		
    		Integer crossDayUnitDuration = carparkDurationStandard.getCrossDayUnitDuration();
            new Label(composite_durationConfig,SWT.NONE).setText("跨天单位时长");
            text_crossDayUnitTimeLength = new Text(composite_durationConfig, SWT.BORDER);
            text_crossDayUnitTimeLength.setText((crossDayUnitDuration == null ? 0 : crossDayUnitDuration)+"");
            text_crossDayUnitTimeLength.setLayoutData("w 50");
            composite_durationConfig.setData("crossDayUnitTimeLength", text_crossDayUnitTimeLength);
            
            Float crossDayPrice = carparkDurationStandard.getCrossDayPrice();
            new Label(composite_durationConfig,SWT.NONE).setText("跨天单位金额");
            text_crossDayUnitPrice = new Text(composite_durationConfig, SWT.BORDER);
            text_crossDayUnitPrice.setText((crossDayPrice == null ? 0 : crossDayPrice)+"");
            text_crossDayUnitPrice.setLayoutData("w 50");
            composite_durationConfig.setData("crossDayUnitPrice", text_crossDayUnitPrice);
    		
    		Composite composite_durationPrice = new Composite(composite, SWT.NONE);
    		composite_durationPrice.setData("key","composite_durationPrice");
    		composite_durationPrice.setLayoutData(new GridData(GridData.FILL_BOTH));
    		composite_durationPrice.setLayout(new GridLayout(12, false));
    		GridData gd = new GridData();
    		gd.widthHint = 30;
    		//先按时长排序
    		List<CarparkDurationPrice> carparkDurationPriceList = carparkDurationStandard.getCarparkDurationPriceList();
    		Collections.sort(carparkDurationPriceList, new Comparator<CarparkDurationPrice>() {
				@Override
				public int compare(CarparkDurationPrice o1, CarparkDurationPrice o2) {
					return o1.getDurationLength() - o2.getDurationLength();
				}
			});
    		
    		for (CarparkDurationPrice carparkDurationPrice : carparkDurationPriceList) {
    			new Label(composite_durationPrice, SWT.NONE).setText(carparkDurationPrice.getDurationLength()+"小时收费");
    			Text text4 = new Text(composite_durationPrice, SWT.BORDER);
    			text4.setText(carparkDurationPrice.getDurationLengthPrice()+"");
    			text4.setLayoutData(gd);
    			composite_durationPrice.setData(carparkDurationPrice.getDurationLength()+"", text4);
    			new Label(composite_durationPrice, SWT.NONE).setText("元　");
			}
    		
    		tabItem.setControl(composite);
    		tf.setSelection(tabItem);
		}
    }

    private void init() {
		if(Strings.isNullOrEmpty(this.model.getFreeTimeEnable())){
			this.model.setFreeTimeEnable("是");
		}
		if(this.model.getCarparkDurationTypeEnum() == null){
			this.model.setCarparkDurationTypeEnum(CarparkDurationTypeEnum.自然天);
		}
		if(this.model.getCarparkHolidayTypeEnum() == null){
			this.model.setCarparkHolidayTypeEnum(CarparkHolidayTypeEnum.工作日);
		}
		if(this.model.getCarparkCarType() == null && this.model.getCarparkCarTypeList().isEmpty() == false){
			this.model.setCarparkCarType(this.model.getCarparkCarTypeList().get(0));
		}
		if(this.model.getCarparkAcrossDayTypeEnum() == null){
			this.model.setCarparkAcrossDayTypeEnum(CarparkAcrossDayTypeEnum.重复计费);
		}
		if(this.model.getOnedayMaxCharge() == 0){
			this.model.setOnedayMaxCharge(0);
		}
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTextObserveWidget_1 = WidgetProperties.text(SWT.Modify).observe(text);
		IObservableValue codeModelObserveValue = BeanProperties.value("code").observe(model);
		bindingContext.bindValue(observeTextTextObserveWidget_1, codeModelObserveValue, null, null);
		//
		IObservableValue observeTextText_1ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_1);
		IObservableValue nameModelObserveValue = BeanProperties.value("name").observe(model);
		bindingContext.bindValue(observeTextText_1ObserveWidget, nameModelObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionComboViewer = ViewerProperties.singleSelection().observe(comboViewer);
		IObservableValue observe_durationType_model = BeansObservables.observeValue(model, "carparkDurationTypeEnum");
		bindingContext.bindValue(observeSingleSelectionComboViewer, observe_durationType_model, null, null);
		//
		IObservableValue observeTextText_2ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_2);
		IObservableValue onedayMaxChargeModelObserveValue = BeanProperties.value("onedayMaxCharge").observe(model);
		bindingContext.bindValue(observeTextText_2ObserveWidget, onedayMaxChargeModelObserveValue, null, null);
		//
		IObservableValue observeTextText_3ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_3);
		IObservableValue freeTimeModelObserveValue = BeanProperties.value("freeTime").observe(model);
		bindingContext.bindValue(observeTextText_3ObserveWidget, freeTimeModelObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionComboViewer_1 = ViewerProperties.singleSelection().observe(comboViewer_1);
		IObservableValue observe_holidayType_model = BeansObservables.observeValue(model, "carparkHolidayTypeEnum");
		bindingContext.bindValue(observeSingleSelectionComboViewer_1, observe_holidayType_model, null, null);
		//
		IObservableValue observeSingleSelectionComboViewer_2 = ViewerProperties.singleSelection().observe(comboViewer_2);
		IObservableValue observe_crossDayChargeType_model = BeansObservables.observeValue(model, "carparkAcrossDayTypeEnum");
		bindingContext.bindValue(observeSingleSelectionComboViewer_2, observe_crossDayChargeType_model, null, null);
		//
		IObservableValue observeSingleSelectionComboViewer_3 = ViewerProperties.singleSelection().observe(comboViewer_3);
		IObservableValue observe_enableCharge_model = BeansObservables.observeValue(model, "freeTimeEnable");
		bindingContext.bindValue(observeSingleSelectionComboViewer_3, observe_enableCharge_model, null, null);
		//
		IObservableValue observeSingleSelectionComboViewer_4 = ViewerProperties.singleSelection().observe(comboViewer_4);
		IObservableValue observe_carType_model = BeansObservables.observeValue(model, "carparkCarType");
		bindingContext.bindValue(observeSingleSelectionComboViewer_4, observe_carType_model, null, null);
		//
		IObservableValue observeSelectionButton_6ObserveWidget = WidgetProperties.selection().observe(button_6);
		IObservableValue acrossDayIsFreeModelObserveValue = BeanProperties.value("acrossDayIsFree").observe(model);
		bindingContext.bindValue(observeSelectionButton_6ObserveWidget, acrossDayIsFreeModelObserveValue, null, null);
		//
		return bindingContext;
	}
}
