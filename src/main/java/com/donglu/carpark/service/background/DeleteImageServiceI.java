package com.donglu.carpark.service.background;

import com.donglu.carpark.service.background.impl.DeleteImageServiceImpl;
import com.google.common.util.concurrent.Service;
import com.google.inject.ImplementedBy;

@ImplementedBy(DeleteImageServiceImpl.class)
public interface DeleteImageServiceI extends Service {

}
