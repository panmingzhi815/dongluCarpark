package com.donglu.carpark.ui.wizard;

import java.util.*;

import net.miginfocom.swt.MigLayout;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.dongluhitec.card.domain.db.carpark.CarparkAcrossDayTypeEnum;
import com.dongluhitec.card.domain.db.carpark.CarparkDurationPrice;
import com.dongluhitec.card.domain.db.carpark.CarparkDurationStandard;
import com.dongluhitec.card.domain.db.carpark.CarparkDurationTypeEnum;
import com.dongluhitec.card.domain.db.carpark.CarparkHolidayTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;

/**
 * Created with IntelliJ IDEA.
 * User: panmingzhi
 * Date: 13-11-19
 * Time: 下午4:14
 * To change this template use File | Settings | File Templates.
 */
public class AddTempChargeBasicPage extends WizardPage {
	private DataBindingContext m_bindingContext;
	
	private final String INPUTLAYOUTDATA = "w 100!";

    private Composite container;
	private TabFolder tf;
	private DateTime startTime;
	private DateTime endTime;
	
	private List<Integer> existHour = new ArrayList<Integer>();
	private ComboViewer createComboViewer_durationType;
	private Text text_onedayMaxCharge;
	private ComboViewer createComboViewer_holidayType;
	private Text text_freeDuration;
	private ComboViewer createComboViewer_crossDayChargeType;
	private ComboViewer createComboViewer_enableCharge;
	private ComboViewer createComboViewer_carType;
	private Text text_code;
	private Text text_name;

	private Text text_crossDayUnitTimeLength;

	private Text text_crossDayUnitPrice;

	private Text text_startStepTimeLength;

	private Text text_startStepPrice;

    public AddTempChargeBasicPage() {
        super("wizardPage");

        setTitle("添加停车场临时收费标准");
        setDescription("请输入收费标准详细参数");
    }
    
    @Override
	public void createControl(Composite parent) {
        container = new Composite(parent, SWT.BORDER);
        setControl(container);
        container.setLayout(new GridLayout(1,false));

        Composite composite1 = new Composite(container, SWT.NONE);
        composite1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        composite1.setLayout(new MigLayout("","[right][][right][][right][]",""));

        tf = new TabFolder(container,SWT.NONE);
        tf.setLayout(new FillLayout());
        tf.setLayoutData(new GridData(GridData.FILL_BOTH));
        

        new Label(composite1,SWT.NONE).setText("收费编码");
        text_code = new Text(composite1, SWT.BORDER);
        text_code.setLayoutData(INPUTLAYOUTDATA);
        
        new Label(composite1,SWT.NONE).setText("收费名称");
        text_name = new Text(composite1, SWT.BORDER);
        text_name.setLayoutData(INPUTLAYOUTDATA);

        new Label(composite1,SWT.NONE).setText("时长类型");
        createComboViewer_durationType = new ComboViewer(composite1, SWT.BORDER | SWT.READ_ONLY);
        createComboViewer_durationType.setContentProvider(new ArrayContentProvider());
        createComboViewer_durationType.setLabelProvider(new LabelProvider());
        createComboViewer_durationType.setInput(CarparkDurationTypeEnum.values());
        createComboViewer_durationType.getCombo().setLayoutData(INPUTLAYOUTDATA+",wrap");
        
        new Label(composite1,SWT.NONE).setText("一天最大收费");
        text_onedayMaxCharge = new Text(composite1, SWT.BORDER);
        text_onedayMaxCharge.setLayoutData(INPUTLAYOUTDATA);

        new Label(composite1,SWT.NONE).setText("工作日类型");
        createComboViewer_holidayType = new ComboViewer(composite1, SWT.BORDER | SWT.READ_ONLY);
        createComboViewer_holidayType.setContentProvider(new ArrayContentProvider());
        createComboViewer_holidayType.setLabelProvider(new LabelProvider());
        createComboViewer_holidayType.setInput(CarparkHolidayTypeEnum.values());
        createComboViewer_holidayType.getCombo().setLayoutData(INPUTLAYOUTDATA);

        new Label(composite1,SWT.NONE).setText("免费时长");
        text_freeDuration = new Text(composite1, SWT.BORDER);
        text_freeDuration.setLayoutData(INPUTLAYOUTDATA+",wrap");
        
        new Label(composite1,SWT.NONE).setText("跨天计费方式");
        createComboViewer_crossDayChargeType = new ComboViewer(composite1, SWT.BORDER | SWT.READ_ONLY);
        createComboViewer_crossDayChargeType.setContentProvider(new ArrayContentProvider());
        createComboViewer_crossDayChargeType.setLabelProvider(new LabelProvider());
        createComboViewer_crossDayChargeType.setInput(CarparkAcrossDayTypeEnum.values());
        createComboViewer_crossDayChargeType.getCombo().setLayoutData(INPUTLAYOUTDATA);

        new Label(composite1,SWT.NONE).setText("免费时长收费");
        createComboViewer_enableCharge = new ComboViewer(composite1, SWT.BORDER | SWT.READ_ONLY);
        createComboViewer_enableCharge.setContentProvider(new ArrayContentProvider());
        createComboViewer_enableCharge.setLabelProvider(new LabelProvider());
        createComboViewer_enableCharge.setInput(new String[]{"是","否"});
        createComboViewer_enableCharge.getCombo().setLayoutData(INPUTLAYOUTDATA);

        new Label(composite1,SWT.NONE).setText("车辆类型");
        createComboViewer_carType = new ComboViewer(composite1, SWT.BORDER | SWT.READ_ONLY);
        createComboViewer_carType.setContentProvider(new ArrayContentProvider());
        createComboViewer_carType.setLabelProvider(new LabelProvider());
        createComboViewer_carType.setInput(new ArrayList<String>());
        createComboViewer_carType.getCombo().setLayoutData(INPUTLAYOUTDATA+",wrap");
        
        new Label(composite1,SWT.NONE).setText("起步收费时长");
        text_startStepTimeLength = new Text(composite1, SWT.BORDER);
        text_startStepTimeLength.setLayoutData(INPUTLAYOUTDATA);
        
        new Label(composite1,SWT.NONE).setText("起步收费金额");
        text_startStepPrice = new Text(composite1, SWT.BORDER);
        text_startStepPrice.setLayoutData(INPUTLAYOUTDATA+",wrap");
        
        Label label = new Label(composite1,SWT.NONE);
        label.setText("收费时段设置");
        

        startTime = new DateTime(composite1,SWT.BORDER|SWT.TIME);
        startTime.setHours(0);
        startTime.setMinutes(0);
        startTime.setSeconds(0);
        startTime.setLayoutData("spanx,split 4");

        endTime = new DateTime(composite1,SWT.BORDER|SWT.TIME);
        endTime.setHours(0);
        endTime.setMinutes(0);
        endTime.setSeconds(0);
        
        startTime.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				startTime.setMinutes(0);
				startTime.setSeconds(0);
			}
		});
        
        endTime.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				endTime.setMinutes(0);
				endTime.setSeconds(0);
			}
		});

        Button button = new Button(composite1, SWT.NONE);
        button.setText("添加时段");
        
        Button button2 = new Button(composite1, SWT.NONE);
        button2.setText("删除时段");
        button.addSelectionListener(new SelectionAdapter() {
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
        
        button2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(tf.getItemCount() == 0) return;
				TabItem tabItem = tf.getSelection()[0];
				tabItem.dispose();
			}
		});
        
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
					carparkDurationStandard.setStandardCode(text_code.getText());
				}
				if(cp0.getData("key") == "composite_durationPrice"){
					for(int i=1;i<=24;i++){
						Object data = cp0.getData(i+"");
						if(data != null){
							CarparkDurationPrice carparkDurationPrice = new CarparkDurationPrice();
							carparkDurationPrice.setDurationLength(i);
							carparkDurationPrice.setDurationLengthPrice(Float.valueOf(((Text)data).getText()));
							carparkDurationPrice.setStandardCode(text_code.getText());
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
}
