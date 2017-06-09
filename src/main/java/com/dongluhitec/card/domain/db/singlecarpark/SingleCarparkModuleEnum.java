package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public enum SingleCarparkModuleEnum {
	停车场设置(null,0),
	固定车设置(null,1),
	记录查询(null,2),
	系统用户(null,3),
	商铺优惠(null,4),
	访客车管理(null,5),
	临时车优惠(null,6),
	参数设置(null,7),
	关于(null,8),
	进出记录查询(记录查询,0),
	充值记录查询(记录查询,1),
	归账记录查询(记录查询,2),
	操作员日志(记录查询,3),
	手动抬杆记录(记录查询,4),
	商铺充值记录(记录查询,5),
	商铺免费记录(记录查询,6),
	缴费记录(记录查询,7),
	储值车消费记录(记录查询,8),
	设备故障记录(记录查询,9),
	离线记录(记录查询,10),
	图片记录(记录查询,11),
	;
	
	
	private SingleCarparkModuleEnum parent;
	private int index;

	SingleCarparkModuleEnum(SingleCarparkModuleEnum parent,int index){
		this.parent = parent;
	}

	public SingleCarparkModuleEnum getParent() {
		return parent;
	}

	public int getIndex() {
		return index;
	}
	public static List<SingleCarparkModuleEnum> getByParent(SingleCarparkModuleEnum parent){
		List<SingleCarparkModuleEnum> list=Arrays.asList(SingleCarparkModuleEnum.values());
		list=list.stream().filter(new Predicate<SingleCarparkModuleEnum>() {
			@Override
			public boolean test(SingleCarparkModuleEnum t) {
				return t.parent==parent;
			}
		}).collect(Collectors.toList());
		list.sort(new Comparator<SingleCarparkModuleEnum>() {
			@Override
			public int compare(SingleCarparkModuleEnum o1, SingleCarparkModuleEnum o2) {
				return Integer.compare(o1.index, o2.index);
			}
		});
		return list;
	}
}
