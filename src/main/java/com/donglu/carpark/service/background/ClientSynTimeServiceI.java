package com.donglu.carpark.service.background;

import com.donglu.carpark.service.background.impl.ClientSynTimeServiceImpl;
import com.google.common.util.concurrent.Service;
import com.google.inject.ImplementedBy;

@ImplementedBy(ClientSynTimeServiceImpl.class)
public interface ClientSynTimeServiceI extends Service {
}
