package com.donglu.carpark.service.background.haiyu;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkInOutServiceI;
import com.donglu.carpark.service.background.AbstractCarparkBackgroundService;
import com.dongluhitec.card.domain.db.singlecarpark.haiyu.CarparkRecordHistory;
import com.dongluhitec.card.domain.db.singlecarpark.haiyu.ProcessEnum;
import com.dongluhitec.card.domain.db.singlecarpark.haiyu.UpdateEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by xiaopan on 2016/8/27.
 */
public class AsynHaiYuRecordServiceImpl extends AbstractCarparkBackgroundService implements AsynHaiYuRecordService{
    private final Logger LOGGER = LoggerFactory.getLogger(AsynHaiYuRecordServiceImpl.class);
    private final CarparkDatabaseServiceProvider sp;
    private final HaiYuConfig haiYuConfig;

    @Inject
    public AsynHaiYuRecordServiceImpl(CarparkDatabaseServiceProvider sp, HaiYuConfig haiYuConfig) {
        super(Scheduler.newFixedDelaySchedule(10, 10, TimeUnit.SECONDS), "与贵州海誉同步进出记录");
        this.sp = sp;
        this.haiYuConfig = haiYuConfig;
    }

    @Override
    protected void run() {
        if (!haiYuConfig.isEnable()) {
            return;
        }
        LOGGER.debug("开始同步停车场进出记录成功");
        try {
            runAddAndUpdate();
            LOGGER.debug("同步停车场进出记录成功");
        }catch (Exception e){
            LOGGER.error("同步停车场进出记录失败",e);
        }
    }

    private void runAddAndUpdate() throws IOException {
        final CarparkInOutServiceI cardUsageService = sp.getCarparkInOutService();

        UpdateEnum[] updateEnums = {UpdateEnum.新添加};
        ProcessEnum[] processEnums = {ProcessEnum.未处理,ProcessEnum.处理失败};
        List<CarparkRecordHistory> consumptionRecordList = cardUsageService.findHaiYuRecordHistory(0, 10, updateEnums, processEnums);
        if (StrUtil.isEmpty(consumptionRecordList)) {
            return;
        }

        String generateJsonStr = DesUtils.generateJsonStr(haiYuConfig, consumptionRecordList);

        List<Long> longList = consumptionRecordList.stream().map(map->map.getId()).collect(Collectors.toList());
        try {
            LOGGER.info("向海誉发送停车场进出记录数据 {}",generateJsonStr);
            DesUtils desUtils = new DesUtils(haiYuConfig.getEncryptKey());
            String post = desUtils.post(haiYuConfig.getCarparkPostUrl(), generateJsonStr);

            Boolean returnJsonSuccess = DesUtils.isReturnJsonSuccess(post);
            if (returnJsonSuccess != null && returnJsonSuccess) {
                cardUsageService.updateHaiYuRecordHistory(longList, ProcessEnum.己处理);
            }

            LOGGER.info("海誉响应停车场进出记录数据 {}",post);

        } catch (Exception e) {
            cardUsageService.updateHaiYuRecordHistory(longList, ProcessEnum.处理失败);
            LOGGER.error("向海誉发送停车场进出记录失败！",e);
        }
    }

    @Override
    protected void startUp() throws Exception {
        super.startUp();
        try {
            haiYuConfig.read();
        } catch (Exception e) {

        }
    }
}
