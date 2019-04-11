package com.donglu.carpark.service.background;

import com.donglu.carpark.service.background.impl.ShanghaidibiaoSynchroServiceImpl;
import com.google.common.util.concurrent.Service;
import com.google.inject.ImplementedBy;

@ImplementedBy(ShanghaidibiaoSynchroServiceImpl.class)
public interface ShanghaidibiaoSynchroServiceI extends Service{
	
}
