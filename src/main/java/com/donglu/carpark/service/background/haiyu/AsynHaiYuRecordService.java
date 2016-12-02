package com.donglu.carpark.service.background.haiyu;

import com.google.common.util.concurrent.Service;
import com.google.inject.ImplementedBy;

/**
 * Created by xiaopan on 2016/8/27.
 */
@ImplementedBy(AsynHaiYuRecordServiceImpl.class)
public interface AsynHaiYuRecordService extends Service {
}
