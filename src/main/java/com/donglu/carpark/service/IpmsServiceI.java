package com.donglu.carpark.service;

import com.donglu.carpark.service.impl.IpmsServiceImpl;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.google.inject.ImplementedBy;
/**
 * 长颈鹿停车场app服务
 * @author Administrator
 *
 */
@ImplementedBy(IpmsServiceImpl.class)
public interface IpmsServiceI {
	/**
	 * 添加停车记录
	 * @param inOutHistory
	 * @return
	 */
	public boolean addInOutHistory(SingleCarparkInOutHistory inOutHistory);
	/**
	 * 更新停车记录
	 * @param inOutHistory
	 * @return
	 */
	public boolean updateInOutHistory(SingleCarparkInOutHistory inOutHistory);
	/**
	 * 添加月租用户
	 * @param user
	 * @return
	 */
	public boolean addUser(SingleCarparkUser user);
	/**
	 * 更新月租用户
	 * @param user
	 * @return
	 */
	public boolean updateUser(SingleCarparkUser user);
	/**
	 * 更新月租用户
	 * @param user
	 * @return
	 */
	public boolean deleteUser(SingleCarparkUser user);
	/**
	 * 更新临时车缴费记录
	 */
	public void updateTempCarChargeHistory();
	/**
	 * 更新月卡信息
	 */
	public void updateUserInfo();
	/**
	 * 更新固定车缴费记录
	 */
	public void updateFixCarChargeHistory();
	/**
	 * 付款
	 * @param inout 记录
	 * @param chargeMoney 应付金额
	 * @return 2005-已支付,0000-自动缴费成功,1011-车辆已锁,
	 * 		1012-自动支付可用余额低于此次应缴纳费用,
	 *     	1014-自动支付额度低于此次应缴纳费用
	 *     	9999-未找到该停车记录时返回
	 */
	public int pay(SingleCarparkInOutHistory inout,float chargeMoney);
}
