package com.donglu.carpark.service.background;

import com.donglu.carpark.service.background.impl.StoreServerLinkServiceImpl;
import com.google.common.util.concurrent.Service;
import com.google.inject.ImplementedBy;
@ImplementedBy(StoreServerLinkServiceImpl.class)
public interface StoreServerLinkServiceI  extends Service {

}
