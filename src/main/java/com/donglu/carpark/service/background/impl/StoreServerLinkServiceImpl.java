package com.donglu.carpark.service.background.impl;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.caucho.hessian.client.HessianProxyFactory;
import com.donglu.carpark.model.SessionInfo;
import com.donglu.carpark.model.storemodel.FreeInfo;
import com.donglu.carpark.model.storemodel.Info;
import com.donglu.carpark.model.storemodel.LoginInfo;
import com.donglu.carpark.model.storemodel.SearchCarInInfo;
import com.donglu.carpark.model.storemodel.SearchFreeInfo;
import com.donglu.carpark.model.storemodel.SearchPayInfo;
import com.donglu.carpark.model.storemodel.TreeInfo;
import com.donglu.carpark.server.json.FastjsonFilter;
import com.donglu.carpark.server.json.Grid;
import com.donglu.carpark.server.json.Json;
import com.donglu.carpark.server.json.Tree;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.StoreHessianServiceI;
import com.donglu.carpark.service.StoreServiceI;
import com.donglu.carpark.service.background.AbstractCarparkBackgroundService;
import com.donglu.carpark.service.background.StoreServerLinkServiceI;
import com.donglu.carpark.util.CarparkFileUtils;
import com.donglu.carpark.util.ConstUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkStore;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkStoreChargeHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkStoreFreeHistory;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class StoreServerLinkServiceImpl extends AbstractCarparkBackgroundService implements StoreServerLinkServiceI {
	private Logger logger = LoggerFactory.getLogger(StoreServerLinkServiceImpl.class);
	@Inject
	private CarparkDatabaseServiceProvider sp;

	static HessianProxyFactory h = new HessianProxyFactory();
	private static StoreHessianServiceI storeHessianService;
	private String path;

	public StoreServerLinkServiceImpl() {
		super(Scheduler.newFixedDelaySchedule(1000, 20, TimeUnit.MILLISECONDS), "连接商铺服务器");
	}

	@Override
	protected void run() {
		if (path == null) {
			logger.warn("store网页服务器地址为{},停止运行",path);
			stopAsync();
			return;
		}
		getObject();
	}

	private void getObject() {
		try {
			h.setConnectTimeout(5000);
			Info send = storeHessianService.keepLink();
			logger.debug("服务器获取数据：{}",send);
			if (send != null) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Info info = null;
							if (!StrUtil.isEmpty(send.getLoginName())) {
								if (send.getClass().equals(LoginInfo.class)) {
									info = login((LoginInfo) send);
								} else if (send.getClass().equals(TreeInfo.class)) {
									info = tree((TreeInfo) send);
								}
								if (send.getClass().equals(SearchFreeInfo.class)) {
									info = searchFree((SearchFreeInfo) send);
								}
								if (send.getClass().equals(SearchPayInfo.class)) {
									info = searchPay((SearchPayInfo) send);
								}
								if (send.getClass().equals(SearchCarInInfo.class)) {
									info = searchCarIn((SearchCarInInfo) send);
								}
								if (send.getClass().equals(FreeInfo.class)) {
									FreeInfo freeInfo = (FreeInfo) send;
									int useType = freeInfo.getUseType();
									switch (useType) {
									case 1:
										info = add(freeInfo);
										break;
									case 2:
										break;
									case 3:
										info = getFreeById(freeInfo);
										break;
									case 4:
										info = getInOutById(freeInfo);
										break;
									}
								} 
							}
							logger.debug("处理服务器消息，获得值：" + info);
							storeHessianService.send(info);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		} catch (Exception e) {
			logger.error("连接失败",e);
		} 
	}

	private Info getInOutById(FreeInfo info) {
		try {
			Long id = info.getId();
			if (StrUtil.isEmpty(id)) {
				return null;
			}
			SingleCarparkInOutHistory findInOutById = sp.getCarparkInOutService().findInOutById(id);
			if (!StrUtil.isEmpty(findInOutById.getOutTime())) {
				throw new Exception("该车已经出场");
			}
			SingleCarparkStoreFreeHistory free = new SingleCarparkStoreFreeHistory();
			Boolean freeType = null;
			free.setIsAllFree(freeType);
			free.setFreePlateNo(findInOutById.getPlateNo());
			String writeJson = writeJson(free);
			info.setMsg(writeJson);
			return info;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private Info searchCarIn(SearchCarInInfo info) {
		try {
			String plateNO = info.getPlateNO();
			Long countNotOutHistory = sp.getCarparkInOutService().countNotOutHistory(plateNO);
			Grid grid = new Grid();
			grid.setTotal(countNotOutHistory);
			int page = info.getPage() - 1;
			int rows = info.getRows();
			List<SingleCarparkInOutHistory> ius = sp.getCarparkInOutService().searchNotOutHistory(page, rows, plateNO);
			grid.setRows(ius);
			String writeJson = writeJson(grid);
			info.setMsg(writeJson);
			return info;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private Info getFreeById(FreeInfo info) {
		SingleCarparkStoreFreeHistory free = null;
		try {
			Long id = info.getId();
			if (StrUtil.isEmpty(id)) {
				free = new SingleCarparkStoreFreeHistory();
				Boolean freeType = null;
				free.setIsAllFree(freeType);
				return null;
			}
			String storeName = info.getStoreName();
			if (StrUtil.isEmpty(storeName)) {
				return null;
			}
			StoreServiceI storeService = sp.getStoreService();
			free = storeService.findStoreFreeById(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String writeJson = writeJson(free);
		info.setMsg(writeJson);
		return info;
	}

	private Info tree(TreeInfo info) {
		logger.debug("{}请求获取菜单",info);
		Tree parent = new Tree();
		parent.setText("商铺优惠");
		Map<String, String> attributesp = new HashMap<String, String>();
		attributesp.put("url", "");
		attributesp.put("target", "");
		parent.setAttributes(attributesp);
		
		List<Tree> tree = new ArrayList<Tree>();
		Tree node = new Tree();
		node.setText("在线优惠");
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("url", "/securityJsp/base/Syuser.jsp");
		attributes.put("target", "");
		node.setAttributes(attributes);
		tree.add(node);

		Tree node1 = new Tree();
		node1.setText("充值记录");
		Map<String, String> attributes1 = new HashMap<String, String>();
		attributes1.put("url", "/securityJsp/base/payHistory.jsp");
		attributes1.put("target", "");
		node1.setAttributes(attributes1);
		tree.add(node1);

		Tree node2 = new Tree();
		node2.setText("场内车辆");
		Map<String, String> attributes2 = new HashMap<String, String>();
		attributes2.put("url", "/securityJsp/base/SyCar.jsp");
		attributes2.put("target", "");
		node2.setAttributes(attributes2);
		tree.add(node2);
		parent.setChildren(tree);

		String writeJsonByFilter = writeJsonByFilter(Arrays.asList(parent), null, null);
		info.setMsg(writeJsonByFilter);
		return info;
	}

	private Info searchPay(SearchPayInfo info) {
		try {
			// HttpSession session = req.getSession();
			// System.out.println("qqqqqqqqqqqqq"+session);
			logger.debug("收到商铺{}的请求：searchPay",info.getStoreName());
			StoreServiceI storeService = sp.getStoreService();

			String storeName = info.getStoreName();
			if (StrUtil.isEmpty(storeName)) {
				throw new Exception("没有找到商铺信息");
			}
			List<SingleCarparkStore> findStoreByCondition = storeService.findStoreByCondition(0, Integer.MAX_VALUE, storeName);
			if (StrUtil.isEmpty(findStoreByCondition)) {
				throw new Exception("商铺信息不正确");
			}
			String operaName = info.getOperaName();
			if (!StrUtil.isEmpty(operaName)) {
				operaName = decode(operaName);
			}
			Date start = info.getStart();
			Date end = info.getEnd();

			Grid grid = new Grid();
			grid.setTotal(storeService.countStoreChargeHistoryByTime(storeName, operaName, start, end));
			int page = info.getPage() - 1;
			int rows = info.getRows();
			List<SingleCarparkStoreChargeHistory> findStoreChargeHistoryByTime = storeService.findStoreChargeHistoryByTime(page, rows, storeName, operaName, start, end);
			grid.setRows(findStoreChargeHistoryByTime);
			String writeJson = writeJson(grid);
			info.setMsg(writeJson);
			logger.debug("请求：searchPay：处理完成返回：{}",info);
			return info;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private Info searchFree(SearchFreeInfo info) {
		System.out.println("searchFree");
		try {
			Date start = info.getStart();
			Date end = info.getEnd();
			String plateNO = info.getPlateNO();
			String used = info.getUsed();

			String storeName = info.getStoreName();
			StoreServiceI storeService = sp.getStoreService();
			Grid grid = new Grid();
			Long countByPlateNO = storeService.countByPlateNO(storeName, plateNO, used, start, end);
			grid.setTotal(countByPlateNO);
			int page = info.getPage() - 1;
			int rows = info.getRows();
			List<SingleCarparkStoreFreeHistory> findByPlateNO = storeService.findByPlateNO(page, rows, storeName, plateNO, used, start, end);
			grid.setRows(findByPlateNO);
			String writeJson = writeJson(grid);
			info.setMsg(writeJson);
			return info;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param startTime
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String decode(String startTime) throws UnsupportedEncodingException {
		if (StrUtil.isEmpty(startTime)) {
			return null;
		}
		return new String(Base64.getDecoder().decode(startTime), "utf-8");
	}

//	private void edit(FreeInfo info) {
//		Json json = new Json();
//		try {
//			System.out.println("edit");
//			json.setSuccess(true);
//			json.setMsg("修改成功");
//		} catch (Exception e) {
//			e.printStackTrace();
//			json.setSuccess(false);
//			json.setMsg("保存失败");
//		}
//		writeJson(json);
//
//	}

	// 添加优惠信息
	private Info add(FreeInfo info) {
		// Object attribute = req.getSession().getAttribute("user");
		// if (StrUtil.isEmpty(attribute)) {
		// return;
		// }
		Json json = new Json();
		try {
			System.out.println("add");
			StoreServiceI storeService = sp.getStoreService();
			String storeName = info.getStoreName();
			if (StrUtil.isEmpty(storeName)) {
				throw new Exception("没有找到商铺信息");
			}
			List<SingleCarparkStore> findStoreByCondition = storeService.findStoreByCondition(0, Integer.MAX_VALUE, storeName);
			if (StrUtil.isEmpty(findStoreByCondition)) {
				throw new Exception("商铺信息不正确");
			}
			SingleCarparkStore store = findStoreByCondition.get(0);
			Long id = info.getId();
			String plateNo = info.getPlateNo();
			Float hour = info.getHour();
			Float money = info.getMoney();
			String freeType = info.getFreeType();
			System.out.println(plateNo + "====" + hour + "=========" + money);
			SingleCarparkStoreFreeHistory free = new SingleCarparkStoreFreeHistory();
			if (!StrUtil.isEmpty(id)) {
				free = storeService.findStoreFreeById(id);
				if (!free.getStoreName().equals(storeName)) {
					throw new Exception("商铺信息不对应");
				}
				if (free.getUsed().equals("已使用")) {
					throw new Exception("该优惠已使用");
				}
				if (freeType == null || freeType.equals("false")) {
					store.setLeftFreeMoney(store.getLeftFreeMoney() + free.getFreeMoney());
					store.setLeftFreeHour(store.getLeftFreeHour() + free.getFreeHour());
				}
			} else {
				free.setCreateTime(new Date());
				free.setUsed("未使用");
				free.setStoreName(storeName);
			}
			free.setFreePlateNo(plateNo);
			if (freeType == null || freeType.equals("false") || freeType.equals("优惠")) {
				free.setFreeHour(hour == null ? 0 : hour);
				free.setFreeMoney(money == null ? 0 : money);
				float leftFreeMoney = store.getLeftFreeMoney() - free.getFreeMoney();
				float leftFreeHour = store.getLeftFreeHour() - free.getFreeHour();
				store.setLeftFreeMoney(leftFreeMoney);
				store.setLeftFreeHour(leftFreeHour);
				if (leftFreeMoney < 0) {
					throw new Exception("优惠金额不足");
				}
				if (leftFreeHour < 0) {
					throw new Exception("优惠时间不足");
				}
				free.setIsAllFree(false);
				// freeType=false;
				storeService.saveStore(store);
			} else {
				if (!store.getCanAllFree()) {
					throw new Exception("商铺不允许全免");
				}
				free.setFreeHour(0F);
				free.setFreeMoney(0F);
				free.setIsAllFree(true);
				// this.freeType=true;
			}
			storeService.saveStoreFree(free);
			json.setSuccess(true);
			json.setMsg("保存成功！");
		} catch (Exception e) {
			json.setMsg("保存失败" + e.getMessage());
		}
		String writeJson = writeJson(json);
		info.setMsg(writeJson);
		return info;
	}

	private Info login(LoginInfo info) {
		Json json = new Json();
		try {
			System.out.println("login");
			StoreServiceI storeService = sp.getStoreService();
			String userName = info.getName();
			String password = info.getPwd();
			System.out.println(userName + "====" + password);
			SingleCarparkStore store = storeService.findByLogin(userName, password);
			if (!StrUtil.isEmpty(store)) {
				json.setSuccess(true);
				SessionInfo sessioninfo = new SessionInfo();
				sessioninfo.setStoreName(store.getStoreName());
				sessioninfo.setLoginName(store.getLoginName());
				sessioninfo.setUserName(store.getUserName());
				json.setObj(sessioninfo);
			} else {
				json.setMsg("用户名或密码错误");
			}
		} catch (Exception e) {
			e.printStackTrace();
			json.setMsg("登录时发生错误" + e);
		}
		String writeJson = writeJson(json);
		info.setMsg(writeJson);
		return info;
	}

	private String writeJson(Object object) {
		return writeJsonByFilter(object, null, null);
	}

	public String writeJsonByFilter(Object object, String[] includesProperties, String[] excludesProperties) {
		try {
			FastjsonFilter filter = new FastjsonFilter();// excludes优先于includes
			if (excludesProperties != null && excludesProperties.length > 0) {
				filter.getExcludes().addAll(Arrays.<String> asList(excludesProperties));
			}
			if (includesProperties != null && includesProperties.length > 0) {
				filter.getIncludes().addAll(Arrays.<String> asList(includesProperties));
			}
			logger.debug("对象转JSON：要排除的属性[" + excludesProperties + "]要包含的属性[" + includesProperties + "]");
			String json;
			// String User_Agent = req.getHeader("User-Agent");
			// if (StringUtils.indexOfIgnoreCase(User_Agent, "MSIE 6") > -1) {
			// // 使用SerializerFeature.BrowserCompatible特性会把所有的中文都会序列化为\\uXXXX这种格式，字节数会多一些，但是能兼容IE6
			// json = JSON.toJSONString(object, filter, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.BrowserCompatible);
			// } else {
			// 使用SerializerFeature.WriteDateUseDateFormat特性来序列化日期格式的类型为yyyy-MM-dd hh24:mi:ss
			// 使用SerializerFeature.DisableCircularReferenceDetect特性关闭引用检测和生成
			json = JSON.toJSONString(object, filter, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.DisableCircularReferenceDetect);
			// }
			logger.debug("转换后的JSON字符串：" + json);
			return json;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void startUp() throws Exception {
		path = (String) CarparkFileUtils.readObject(ConstUtil.STORE_SERVER_PATH);
		logger.info("商铺网页服务器地址：{}",path);
		try {
			storeHessianService = (StoreHessianServiceI) h.create(StoreHessianServiceI.class, path + "/StoreHessianServlet");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			path=null;
		}
		super.startUp();
	}
}
