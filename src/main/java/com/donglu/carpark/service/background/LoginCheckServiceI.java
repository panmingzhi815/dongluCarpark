package com.donglu.carpark.service.background;

import com.donglu.carpark.service.background.impl.LoginCheckServiceImpl;
import com.google.common.util.concurrent.Service;
import com.google.inject.ImplementedBy;

@ImplementedBy(LoginCheckServiceImpl.class)
public interface LoginCheckServiceI extends Service {

}
