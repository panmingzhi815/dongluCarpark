package com.donglu.carpark.service.background;

import com.donglu.carpark.service.background.impl.SmsSendServiceImpl;
import com.google.common.util.concurrent.Service;
import com.google.inject.ImplementedBy;

@ImplementedBy(SmsSendServiceImpl.class)
public interface SmsSendServiceI extends Service {

}
