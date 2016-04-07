package com.donglu.carpark.service.background;

import com.donglu.carpark.service.background.impl.ClientCheckSoftDogServiceImpl;
import com.google.common.util.concurrent.Service;
import com.google.inject.ImplementedBy;

@ImplementedBy(ClientCheckSoftDogServiceImpl.class)
public interface ClientCheckSoftDogServiceI extends Service {

}
