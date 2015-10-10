package com.donglu.carpark.wizard.charge;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkService;
import com.dongluhitec.card.blservice.DatabaseServiceDaemon;
import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.carpark.MonthlyCarparkCharge;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkChargeStandard;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkDurationStandard;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.eclipse.jface.wizard.Wizard;

/**
 * Created with IntelliJ IDEA.
 * User: panmingzhi
 * Date: 13-11-19
 * Time: 下午4:14
 * To change this template use File | Settings | File Templates.
 */
public class NewCommonChargeWizard extends Wizard implements AbstractWizard {

    private final NewCommonChargeModel model;
   
    private CommonUIFacility commonui;
   
	private CarparkDatabaseServiceProvider sp;
    
    private NewCommonChargeBasicPage newCommonChargeBasicPage;
    
    public NewCommonChargeWizard(NewCommonChargeModel model,CarparkDatabaseServiceProvider sp,CommonUIFacility commonui) {
        this.model = model;
        this.sp=sp;
        this.commonui=commonui;
        setWindowTitle("停车场临时收费设置");
    }

    @Override
    public void addPages() {
        newCommonChargeBasicPage = new NewCommonChargeBasicPage(model);
        addPage(newCommonChargeBasicPage);

        getShell().setSize(700, 600);
    }

    @Override
    public NewCommonChargeModel getModel() {
        return model;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean performFinish() {
    	
    	if(checkCodeHasOverride()){
    		commonui.error("错误", "该收费编码己存在,请重新输入");
    		return false;
    	}
    	
    	//收集数据
    	List<CarparkDurationStandard> durationTable = newCommonChargeBasicPage.getDurationTable();
    	this.model.setCarparkDurationStandards(durationTable);
    	
    	
    	if(this.model.getCarparkDurationStandards().size() == 0){
    		commonui.error("错误", "时段个数不能为0");
    		return false;
    	}
    	
    	if(checkTimeHasOverride(this.model.getCarparkDurationStandards())) {
    		commonui.error("错误", "时段之间不能重叠");
    		return false;
    	}
    	
    	int totalHour = 0;
    	for (CarparkDurationStandard carparkDurationStandard : durationTable) {
    		totalHour += carparkDurationStandard.getCarparkDurationPriceList().size();
		}
    	if(totalHour != 24){
    		commonui.error("错误", "时段不完整");
    		return false;
    	}
    	
        return true;
    }
    
    /**
     * 检查时间段之间是否有重叠
     * @param carparkDurationStandards
     * @return true 有重叠
     */
    public boolean checkTimeHasOverride(List<CarparkDurationStandard> carparkDurationStandards){
    	Set<String> addedList = new HashSet<String>();
    	for(CarparkDurationStandard carparkDurationStandard : carparkDurationStandards){
    		int sh = carparkDurationStandard.getStartTime().getHours();
    		int eh = carparkDurationStandard.getEndTime().getHours();
    		if(sh > eh) eh += 24;
    		for(int i=sh;i<eh;i++){
    			if(addedList.contains(i+"")) return true;
    			
    			if(i > 24){
    				addedList.add((i-24)+"");
    			}else{    				
    				addedList.add(i+"");
    			}
    		}
    	}
    	return false;
    }
    
    /**
     * 检查收费编码是否己重新
     * @return
     */
    public boolean checkCodeHasOverride(){
    	final CarparkService carparkService = sp.getCarparkService();
    	CarparkChargeStandard carparkChargeStandard = carparkService.findCarparkChargeStandardByCode(this.model.getCode());
    	if(carparkChargeStandard != null && carparkChargeStandard.getId() != this.getModel().getId()){
    		return true;
    	}
//    	MonthlyCarparkCharge monthlyCarparkCharge = carparkService.findMonthlyCarparkChargeByCode(this.getModel().getCode());
//    	if(monthlyCarparkCharge != null && monthlyCarparkCharge.getId() != this.getModel().getId()){
//    		return true;
//    	}
    	return false;
    }
    
}
