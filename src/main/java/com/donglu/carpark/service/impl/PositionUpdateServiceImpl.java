package com.donglu.carpark.service.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.donglu.carpark.server.imgserver.ImageServerUI;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.PositionUpdateServiceI;
import com.dongluhitec.card.domain.db.shanghaiyunpingtai.YunCarparkSlot;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser.CarparkSlotTypeEnum;
import com.dongluhitec.card.util.ThreadUtil;

public class PositionUpdateServiceImpl implements PositionUpdateServiceI {
	
	private ExecutorService updatePosionThreadExecutor;
	private CarparkDatabaseServiceProvider sp;
	@Override
	public void updatePosion(SingleCarparkCarpark carpark,Long userId, boolean inOrOut) {
		if (updatePosionThreadExecutor==null) {
			updatePosionThreadExecutor= Executors.newSingleThreadExecutor(ThreadUtil.createThreadFactory("更新车位数线程池"));
			sp=ImageServerUI.serverInjector.getInstance(CarparkDatabaseServiceProvider.class);
		}
		int slot=1;
		boolean carType=false;
		if (userId!=null) {
			SingleCarparkUser user=sp.getCarparkUserService().findUserById(userId);
			if (user!=null) {
				carType=true;
				if (user.getCarparkSlotType().equals(CarparkSlotTypeEnum.固定车位)) {
					return;
				}else{
					if (inOrOut) {
						slot=-1;
					}
				}
			}
		}else{
			if (inOrOut) {
				slot=-1;
			}
		}
		updatePosion(carpark, carType, slot);
	}
	@Override
	public void updatePosion(SingleCarparkCarpark carpark, boolean isFixOrTemp, int slot) {

		if (updatePosionThreadExecutor==null) {
			updatePosionThreadExecutor= Executors.newSingleThreadExecutor(ThreadUtil.createThreadFactory("更新车位数线程池"));
			sp=ImageServerUI.serverInjector.getInstance(CarparkDatabaseServiceProvider.class);
		}
		updatePosionThreadExecutor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					SingleCarparkCarpark findCarparkById = sp.getCarparkService().findCarparkById(carpark.getId());
					SingleCarparkCarpark c = findCarparkById.getMaxParent();
					int leftFixNumberOfSlot = c.getTrueLeftFixNumberOfSlot()==null?c.getFixNumberOfSlot():c.getTrueLeftFixNumberOfSlot();
					int leftTempNumberOfSlot = c.getTrueLeftTempNumberOfSlot()==null?c.getTempNumberOfSlot():c.getTrueLeftTempNumberOfSlot();
					if (isFixOrTemp||findCarparkById.getTempNumberOfSlot()==0) {
						leftFixNumberOfSlot += slot;
					} else {
						leftTempNumberOfSlot += slot;
					}

					c.setLeftFixNumberOfSlot(leftFixNumberOfSlot);
					c.setLeftTempNumberOfSlot(leftTempNumberOfSlot);
					try {
						sp.getCarparkService().saveCarpark(c);
					} catch (Exception e) {
						updatePosionThreadExecutor.submit(this);
					}
					try {
						YunCarparkSlot s = new YunCarparkSlot();
						s.setTotBerthNum(c.getTotalNumberOfSlot());
						s.setMonthlyBerthNum(c.getFixNumberOfSlot());
						s.setGuesBerthNum(c.getTempNumberOfSlot());
						s.setTotRemainNum(c.getLeftFixNumberOfSlot()+c.getLeftTempNumberOfSlot());
						s.setMonthlyRemainNum(c.getLeftFixNumberOfSlot());
						s.setGuestRemainNum(c.getLeftTempNumberOfSlot());
						sp.getYunCarparkService().saveCarparkSlot(s);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
