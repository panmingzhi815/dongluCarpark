package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.view.OpenDoorLogPresenter;
import com.donglu.carpark.ui.view.ReturnAccountPresenter;
import com.donglu.carpark.ui.view.SystemLogPresenter;
import com.donglu.carpark.ui.view.account.AccountCarInOutPresenter;
import com.donglu.carpark.ui.view.account.AccountCarPresenter;
import com.donglu.carpark.ui.view.carpark.CarparkPresenter;
import com.donglu.carpark.ui.view.deviceerror.DeviceErrorPresenter;
import com.donglu.carpark.ui.view.free.TempCarFreePresenter;
import com.donglu.carpark.ui.view.history.HistoryPresenter;
import com.donglu.carpark.ui.view.img.ImageHistoryPresenter;
import com.donglu.carpark.ui.view.inouthistory.CarPayPresenter;
import com.donglu.carpark.ui.view.inouthistory.InOutHistoryPresenter;
import com.donglu.carpark.ui.view.inouthistory.event.EventPresenter;
import com.donglu.carpark.ui.view.main.AboutPresenter;
import com.donglu.carpark.ui.view.offline.CarparkOffLineHistoryPresenter;
import com.donglu.carpark.ui.view.setting.SettingPresenter;
import com.donglu.carpark.ui.view.store.StoreChargePresenter;
import com.donglu.carpark.ui.view.store.StoreFreePresenter;
import com.donglu.carpark.ui.view.store.StorePresenter;
import com.donglu.carpark.ui.view.systemuser.SystemUserListPresenter;
import com.donglu.carpark.ui.view.user.CarparkPayHistoryPresenter;
import com.donglu.carpark.ui.view.user.PrepaidUserPayHistoryPresenter;
import com.donglu.carpark.ui.view.user.UserPresenter;
import com.donglu.carpark.ui.view.visitor.VisitorPresenter;

public enum SingleCarparkModuleEnum {
	停车场设置(null,0,CarparkPresenter.class),
	固定车设置(null,1,UserPresenter.class),
	记录查询(null,2,HistoryPresenter.class),
	系统用户(null,3,SystemUserListPresenter.class),
	记账车(null,4,AccountCarPresenter.class),
	商铺优惠(null,4,StorePresenter.class),
	访客车管理(null,5,VisitorPresenter.class),
	临时车优惠(null,6,TempCarFreePresenter.class),
	参数设置(null,7,SettingPresenter.class),
	关于(null,8,AboutPresenter.class),
	进出记录查询(记录查询,0,InOutHistoryPresenter.class),
	充值记录查询(记录查询,1,CarparkPayHistoryPresenter.class),
	归账记录查询(记录查询,2,ReturnAccountPresenter.class),
	操作员日志(记录查询,3,SystemLogPresenter.class),
	手动抬杆记录(记录查询,4,OpenDoorLogPresenter.class),
	商铺充值记录(记录查询,5,StoreChargePresenter.class),
	商铺免费记录(记录查询,6,StoreFreePresenter.class),
	缴费记录(记录查询,7,CarPayPresenter.class),记账车费用(记录查询,7,AccountCarInOutPresenter.class),
	储值车消费记录(记录查询,8,PrepaidUserPayHistoryPresenter.class),
	设备故障记录(记录查询,9,DeviceErrorPresenter.class),
	离线记录(记录查询,10,CarparkOffLineHistoryPresenter.class),
	图片记录(记录查询,11,ImageHistoryPresenter.class),
	停车场事件(记录查询,12,EventPresenter.class),
	;
	
	
	private SingleCarparkModuleEnum parent;
	private int index;
	private Class<? extends Presenter> presenter;
	private String moduleName;

	SingleCarparkModuleEnum(SingleCarparkModuleEnum parent,int index,Class<? extends Presenter> presenter){
		this.parent = parent;
		this.index = index;
		this.presenter = presenter;
	}

	public SingleCarparkModuleEnum getParent() {
		return parent;
	}

	public int getIndex() {
		return index;
	}
	public static List<SingleCarparkModuleEnum> getByParent(SingleCarparkModuleEnum parent){
		List<SingleCarparkModuleEnum> list=Arrays.asList(SingleCarparkModuleEnum.values());
		list=list.stream().filter(t -> t.parent==parent).collect(Collectors.toList());
		list.sort((o1, o2) -> Integer.compare(o1.index, o2.index));
		return list;
	}

	public Class<? extends Presenter> getPresenter() {
		return presenter;
	}

	public String getModuleName() {
		if (moduleName==null) {
			return name();
		}
		return moduleName;
	}
}
