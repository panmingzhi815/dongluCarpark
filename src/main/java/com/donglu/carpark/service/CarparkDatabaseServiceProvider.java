
package com.donglu.carpark.service;

public interface CarparkDatabaseServiceProvider {

	/**
	 * 开启服务并提供服务接口给其它对象。 次方法必须在其它所有getXXXService方法调用之前被执行。
	 */
	public void start() throws Exception;

	/**
	 * 关闭所有服务接口。
	 */
	public void stop() throws Exception;

	CarparkService getCarparkService();
	
	CarparkUserService getCarparkUserService();
	
	SystemUserServiceI getSystemUserService();
	
	CarparkInOutServiceI getCarparkInOutService();
	
	SystemOperaLogServiceI getSystemOperaLogService();
	
	StoreServiceI getStoreService();
	
	PlateSubmitServiceI getPlateSubmitService();
}

