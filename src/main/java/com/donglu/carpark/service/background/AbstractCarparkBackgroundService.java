package com.donglu.carpark.service.background;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.AbstractScheduledService;

public abstract class AbstractCarparkBackgroundService extends AbstractScheduledService {
	String name;
	protected static Logger log = LoggerFactory.getLogger(AbstractCarparkBackgroundService.class);
	private Scheduler scheduler;
	int num=1;
	public AbstractCarparkBackgroundService(Scheduler scheduler, String name) {
		this.scheduler=scheduler;
		this.name=name;
	}
	@Override
	protected void runOneIteration() throws Exception {
		long currentTimeMillis = System.currentTimeMillis();
		log.debug("第{}次执行服务:{}",num,name);
		run();
		num++;
		log.debug("服务：【{}】执行完成花费时间：{}",name,(System.currentTimeMillis() - currentTimeMillis));
	}

	protected abstract void run();
	@Override
	protected Scheduler scheduler() {
		return scheduler;
	}
}
