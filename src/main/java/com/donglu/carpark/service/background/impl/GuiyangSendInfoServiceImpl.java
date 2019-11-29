package com.donglu.carpark.service.background.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.background.AbstractCarparkBackgroundService;
import com.donglu.carpark.service.background.GuiyangSendInfoServiceI;
import com.donglu.carpark.util.HttpRequestUtil;
import com.donglu.carpark.util.Md5Util;
import com.dongluhitec.card.domain.db.singlecarpark.UploadHistory;
import com.google.inject.Inject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GuiyangSendInfoServiceImpl extends AbstractCarparkBackgroundService implements GuiyangSendInfoServiceI {

	private static final String GUIYANGPLATEUPLOAD = "guiyangplateupload";
	private CarparkDatabaseServiceProvider sp;
	private String url;
	private String csId;
	private String parkAppId;
	private String key;
	
	@Inject
	public GuiyangSendInfoServiceImpl(CarparkDatabaseServiceProvider sp) {
		super(Scheduler.newFixedDelaySchedule(10, 5, TimeUnit.SECONDS), "贵阳车牌报送上传服务");
		this.sp = sp;
	}

	@Override
	protected void run() {
		try {
			Map<String,Object> map=new HashMap<>();
			map.put(UploadHistory.Property.processState.name(), 0);
			map.put(UploadHistory.Property.processState.name(), GUIYANGPLATEUPLOAD);
			List<UploadHistory> list = sp.getCarparkInOutService().findByMap(0, 10, UploadHistory.class, map);
			for (UploadHistory uploadHistory : list) {
				String data = new String(uploadHistory.getData(),"UTF-8");
				JSONObject object = JSON.parseObject(data);
				object.put("cs_id", csId);
				object.put("park_app_id", parkAppId);
				TreeMap<String,Object> treeMap = new TreeMap<>(object);
				Iterator<String> iterator = treeMap.keySet().iterator();
				StringBuffer sb=new StringBuffer();
				while(iterator.hasNext()) {
					String next = iterator.next();
					Object object2 = treeMap.get(next);
					if (object2==null) {
						continue;
					}
					sb.append(next);
					sb.append("=");
					sb.append(String.valueOf(object2));
					sb.append("&");
				}
				sb.append("key=");
				sb.append(key);
				log.info("待加密的数据:{}",sb);
				object.put("sign", Md5Util.md5(sb.toString()).toUpperCase());
				String httpPostMssage = HttpRequestUtil.httpPostMssage(url, "data="+object.toJSONString(), 3000);
				log.info("返回数据：{}",httpPostMssage);
				JSONObject result = JSON.parseObject(httpPostMssage);
				if (result.getIntValue("code")==200) {
					uploadHistory.setProcessState(0);
				}else {
					uploadHistory.setProcessStatus(result.getString("errorMsg"));
				}
				sp.getCarparkInOutService().saveEntity(uploadHistory);
			}
		} catch (Exception e) {
			log.info("贵阳车牌报送时发生错误",e);
		}
	}
	
	@Override
	protected void startUp() throws Exception {
		GuiyangSendInfoConfig config = GuiyangSendInfoConfig.getInstance();
		if (!config.isEnable()) {
			stopAsync();
			return;
		}
		url = config.getUrl();
		csId = config.getCsId();
		parkAppId = config.getParkAppId();
		key = config.getKey();
	}

}
