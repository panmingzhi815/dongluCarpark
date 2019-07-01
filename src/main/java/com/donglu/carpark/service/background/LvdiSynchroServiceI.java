package com.donglu.carpark.service.background;

import com.donglu.carpark.service.background.impl.LvdiSynchroServiceImpl;
import com.google.common.util.concurrent.Service;
import com.google.inject.ImplementedBy;

@ImplementedBy(LvdiSynchroServiceImpl.class)
public interface LvdiSynchroServiceI extends Service {
	
}
