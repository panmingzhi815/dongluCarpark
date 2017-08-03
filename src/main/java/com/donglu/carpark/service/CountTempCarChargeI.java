package com.donglu.carpark.service;

import java.io.Serializable;
import java.util.Date;

import com.donglu.carpark.model.CarparkMainModel;

/**
 * 停车场计费的类，一般将其序列化保存到本地，程序启动时，会检测本地是否是否有该类，有的话直接读取本地的类，没有则从程序中new 一个对象保存到本地
 * 方便以后修改收费，改了之后不用发程序，直接发个新的序列化文件过去就行了
 * @author 黄建雄
 *
 */
public interface CountTempCarChargeI extends Serializable {
	public float charge(Long carparkId,Long carType, Date inTime, Date outTime,CarparkDatabaseServiceProvider sp,CarparkMainModel model,boolean reCharge);
}
