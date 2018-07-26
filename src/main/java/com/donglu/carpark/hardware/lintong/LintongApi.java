package com.donglu.carpark.hardware.lintong;

import com.sun.jna.Library;

public interface LintongApi extends Library {
	/**
	 * 
	 * @param szPlate 输入参数，传入的车牌号
	 * @param nTime 输出参数，车辆的检验时间
	 * @return 	ERR_SUCCESS = 0,	// 处理成功
            	ERR_CONNECT,		// 连接失败
            	ERR_NORECORD,		// 没有记录
            	ERR_EXCUTE		// 执行错误
	 */
	int GetCarTimesFunc(String szPlate, byte[] nTime);
	
	int DelCarInfoFunc(String szPlate);
}
