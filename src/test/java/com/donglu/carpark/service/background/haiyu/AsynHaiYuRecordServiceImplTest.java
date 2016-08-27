package com.donglu.carpark.service.background.haiyu;

import com.dongluhitec.card.domain.db.singlecarpark.haiyu.CarparkRecordHistory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by xiaopan on 2016/8/27.
 */
public class AsynHaiYuRecordServiceImplTest {
    private final Logger LOGGER = LoggerFactory.getLogger(AsynHaiYuRecordServiceImplTest.class);
    @Test
    public void test() throws Exception {
        HaiYuConfig haiYuConfig = new HaiYuConfig();
        haiYuConfig.read();

        CarparkRecordHistory carparkRecordHistory = new CarparkRecordHistory();
        carparkRecordHistory.setId(1L);
        carparkRecordHistory.setCarparkName("第一车库");
        carparkRecordHistory.setInTime("2016-08-26 00:12:00");
        carparkRecordHistory.setInDevice("进口1");
        carparkRecordHistory.setPlateNO("鄂A74125");
        carparkRecordHistory.setUserName("小潘");

        List<CarparkRecordHistory> carparkRecordHistories = Arrays.asList(carparkRecordHistory);

        String generateJsonStr = DesUtils.generateJsonStr(haiYuConfig, carparkRecordHistories);
        LOGGER.info("测试发送数据 {}",generateJsonStr);
        DesUtils desUtils = new DesUtils(haiYuConfig.getEncryptKey());
        String post = desUtils.post(haiYuConfig.getCardUsagePostUrl(), generateJsonStr);
        LOGGER.info("接收测试数据 {}",post);
    }

    @Test
    public void create(){
        HaiYuConfig haiYuConfig = new HaiYuConfig();
        haiYuConfig.write();
    }

}