package com.donglu.carpark.service.background;

import com.donglu.carpark.service.background.impl.IpmsSynchroServiceImpl;
import com.google.common.util.concurrent.Service;
import com.google.inject.ImplementedBy;

@ImplementedBy(IpmsSynchroServiceImpl.class)
public interface IpmsSynchroServiceI extends Service {
	
}
