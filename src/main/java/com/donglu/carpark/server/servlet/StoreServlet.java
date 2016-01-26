package com.donglu.carpark.server.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.donglu.carpark.model.SessionInfo;
import com.donglu.carpark.server.json.FastjsonFilter;
import com.donglu.carpark.server.json.Grid;
import com.donglu.carpark.server.json.Json;
import com.donglu.carpark.server.json.Tree;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.StoreServiceI;
import com.donglu.carpark.ui.CarparkMainApp;
import com.donglu.carpark.util.CarparkUtils;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkStore;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkStoreChargeHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkStoreFreeHistory;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class StoreServlet extends HttpServlet {

	private Logger logger = LoggerFactory.getLogger(CarparkMainApp.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -3799938296698687175L;

	@Inject
	private CarparkDatabaseServiceProvider sp;
	
	private Boolean freeType=false;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			sp.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String method = req.getParameter("method");
		System.out.println(method);
		if (StrUtil.isEmpty(method)) {
			logger.error("空的方法");
			return;
		}
		if (method.equals("tree")) {
			tree(req, resp);
		} else if (method.equals("login")) {
			login(req, resp);
		} else if (method.equals("add")) {
			add(req, resp);
		} else if (method.equals("edit")) {
			edit(req, resp);
		} else if (method.equals("searchFree")) {
			searchFree(req, resp);
		} else if (method.equals("searchPay")) {
			searchPay(req, resp);
		} else if (method.equals("getFreeById")) {
			getFreeById(req, resp);
		} else if (method.equals("searchCarIn")) {
			searchCarIn(req, resp);
		} else if (method.equals("getInOutById")) {
			getInOutById(req, resp);
		}

		else
			logger.error("没有找到方法为{}的方法", method);
	}

	private void getInOutById(HttpServletRequest req, HttpServletResponse resp) {
		try {
			String id = decode(req.getParameter("id"));
			if (StrUtil.isEmpty(id)) {
				return;
			}
			SingleCarparkInOutHistory findInOutById = sp.getCarparkInOutService().findInOutById(Long.valueOf(id));
			if (!StrUtil.isEmpty(findInOutById.getOutTime())) {
				throw new Exception("该车已经出场");
			}
			SingleCarparkStoreFreeHistory free = new SingleCarparkStoreFreeHistory();
			free.setIsAllFree(freeType);
			free.setFreePlateNo(findInOutById.getPlateNo());
			writeJson(free, req, resp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void searchCarIn(HttpServletRequest req, HttpServletResponse resp) {
		try {
			String plateNO = decode(req.getParameter("searchPlateNO"));
			Long countNotOutHistory = sp.getCarparkInOutService().countNotOutHistory(plateNO);
			Grid grid = new Grid();
			grid.setTotal(countNotOutHistory);
			int page = Integer.parseInt(decode(req.getParameter("page"))) - 1;
			int rows = Integer.parseInt(decode(req.getParameter("rows")));
			List<SingleCarparkInOutHistory> ius = sp.getCarparkInOutService().searchNotOutHistory(page, rows, plateNO);
			grid.setRows(ius);
			writeJson(grid, req, resp);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void getFreeById(HttpServletRequest req, HttpServletResponse resp) {
		SingleCarparkStoreFreeHistory free = null;
		try {
			String id = req.getParameter("id");
			if (StrUtil.isEmpty(id)) {
				free=new SingleCarparkStoreFreeHistory();
				free.setIsAllFree(freeType);
				return;
			}
			String storeName = req.getParameter("storeName");
			if (StrUtil.isEmpty(storeName)) {
				return;
			}
			StoreServiceI storeService = sp.getStoreService();
			free = storeService.findStoreFreeById(Long.valueOf(id));
		} catch (Exception e) {
			e.printStackTrace();
		}
		writeJson(free, req, resp);
	}

	private void tree(HttpServletRequest req, HttpServletResponse resp) {
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

		writeJsonByFilter(tree, null, null, req, resp);

	}

	private void searchPay(HttpServletRequest req, HttpServletResponse resp) {
		try {
			// HttpSession session = req.getSession();
			// System.out.println("qqqqqqqqqqqqq"+session);
			System.out.println("searchPay");
			sp.start();
			StoreServiceI storeService = sp.getStoreService();

			Map<String, String[]> map = req.getParameterMap();
			String storeName = map.get("storeName") == null ? null : map.get("storeName")[0];
			if (StrUtil.isEmpty(storeName)) {
				throw new Exception("没有找到商铺信息");
			}
			storeName = CarparkUtils.decod(storeName);
			List<SingleCarparkStore> findStoreByCondition = storeService.findStoreByCondition(0, Integer.MAX_VALUE, storeName);
			if (StrUtil.isEmpty(findStoreByCondition)) {
				throw new Exception("商铺信息不正确");
			}
			String[] operaNames = map.get("searchOperaName");
			String[] startTimes = map.get("searchStartTime");
			String[] endTimes = map.get("searchEndTime");
			String operaName = operaNames == null ? null : operaNames[0];
			if (!StrUtil.isEmpty(operaName)) {
				operaName = decode(operaName);
			}
			String startTime = startTimes == null ? null : startTimes[0];
			String endTime = endTimes == null ? null : endTimes[0];
			if (!StrUtil.isEmpty(startTime)) {
				startTime = decode(startTime);
			}
			if (!StrUtil.isEmpty(endTime)) {
				endTime = decode(endTime);
			}
			Date start = StrUtil.parse(startTime, "yyyy-MM-dd HH:mm:ss");
			Date end = StrUtil.parse(endTime, "yyyy-MM-dd HH:mm:ss");

			Grid grid = new Grid();
			grid.setTotal(storeService.countStoreChargeHistoryByTime(storeName, operaName, start, end));
			int page = Integer.parseInt(map.get("page")[0]) - 1;
			int rows = Integer.parseInt(map.get("rows")[0]);
			List<SingleCarparkStoreChargeHistory> findStoreChargeHistoryByTime = storeService.findStoreChargeHistoryByTime(page, rows, storeName, operaName, start, end);
			grid.setRows(findStoreChargeHistoryByTime);
			writeJson(grid, req, resp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void searchFree(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("searchFree");
		try {
			sp.start();
			Map<String, String[]> map = req.getParameterMap();
			String[] storeNames = map.get("storeName");
			String[] plateNOs = map.get("searchPlateNO");
			String[] useds = map.get("searchUsed");
			String[] startTimes = map.get("searchStartTime");
			String[] endTimes = map.get("searchEndTime");
			String startTime = startTimes == null ? null : startTimes[0];
			String endTime = endTimes == null ? null : endTimes[0];
			if (!StrUtil.isEmpty(startTimes)) {
				startTime = decode(startTime);
			}
			if (!StrUtil.isEmpty(endTime)) {
				endTime = decode(endTime);
			}
			Date start = StrUtil.parse(startTime, "yyyy-MM-dd HH:mm:ss");
			Date end = StrUtil.parse(endTime, "yyyy-MM-dd HH:mm:ss");
			String plateNO = plateNOs == null ? null : plateNOs[0];
			String used = useds == null ? null : useds[0];
			if (!StrUtil.isEmpty(used)) {
				used = decode(used);
			}

			String storeName = storeNames == null ? null : storeNames[0];
			storeName = CarparkUtils.decod(storeName);
			StoreServiceI storeService = sp.getStoreService();
			Grid grid = new Grid();
			Long countByPlateNO = storeService.countByPlateNO(storeName, plateNO, used, start, end);
			grid.setTotal(countByPlateNO);
			int page = Integer.parseInt(map.get("page")[0]) - 1;
			int rows = Integer.parseInt(map.get("rows")[0]);
			List<SingleCarparkStoreFreeHistory> findByPlateNO = storeService.findByPlateNO(page, rows, storeName, plateNO, used, start, end);
			grid.setRows(findByPlateNO);
			writeJson(grid, req, resp);
		} catch (Exception e) {
			e.printStackTrace();
		}

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

	private void edit(HttpServletRequest req, HttpServletResponse resp) {
		Json json = new Json();
		try {
			System.out.println("edit");
			Map<String, String[]> map = req.getParameterMap();
			String[] pwds = map.get("data.pwd");
			if (StrUtil.isEmpty(pwds)) {
				return;
			}
			String pwd = pwds[0];

			json.setSuccess(true);
			json.setMsg("修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			json.setSuccess(false);
			json.setMsg("保存失败");
		}
		writeJson(json, req, resp);

	}

	// 添加优惠信息
	private void add(HttpServletRequest req, HttpServletResponse resp) {
		// Object attribute = req.getSession().getAttribute("user");
		// if (StrUtil.isEmpty(attribute)) {
		// return;
		// }
		Json json = new Json();
		try {
			System.out.println("add");
			sp.start();
			StoreServiceI storeService = sp.getStoreService();
			Map<String, String[]> map = req.getParameterMap();
			String storeName = map.get("storeName") == null ? null : map.get("storeName")[0];
			storeName = CarparkUtils.decod(storeName);
			if (StrUtil.isEmpty(storeName)) {
				throw new Exception("没有找到商铺信息");
			}
			List<SingleCarparkStore> findStoreByCondition = storeService.findStoreByCondition(0, Integer.MAX_VALUE, storeName);
			if (StrUtil.isEmpty(findStoreByCondition)) {
				throw new Exception("商铺信息不正确");
			}
			SingleCarparkStore store = findStoreByCondition.get(0);
			String id = map.get("id") == null ? null : map.get("id")[0];
			String plateNo = map.get("plateNo") == null ? null : map.get("plateNo")[0];
			String hour = map.get("freehours") == null ? null : map.get("freehours")[0];
			String money = map.get("freeMoney") == null ? null : map.get("freeMoney")[0];
			String freeType = map.get("freeType") == null ? null : map.get("freeType")[0];
			freeType=decode(freeType);
			plateNo = decode(plateNo);
			System.out.println(plateNo + "====" + hour + "=========" + money);
			SingleCarparkStoreFreeHistory free = new SingleCarparkStoreFreeHistory();
			if (!StrUtil.isEmpty(id)) {
				free = storeService.findStoreFreeById(Long.valueOf(id));
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
			if (freeType == null || freeType.equals("false")||freeType.equals("优惠")) {
				free.setFreeHour(hour == null ? 0 : Float.valueOf(hour));
				free.setFreeMoney(money == null ? 0 : Float.valueOf(money));
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
				this.freeType=false;
				storeService.saveStore(store);
			} else {
				if (!store.getCanAllFree()) {
					throw new Exception("商铺不允许全免");
				}
				free.setFreeHour(0F);
				free.setFreeMoney(0F);
				free.setIsAllFree(true);
				this.freeType=true;
			}
			storeService.saveStoreFree(free);
			json.setSuccess(true);
			json.setMsg("保存成功！");
		} catch (Exception e) {
			json.setMsg("保存失败" + e.getMessage());
		}
		writeJson(json, req, resp);

	}

	private void login(HttpServletRequest req, HttpServletResponse resp) {
		// HttpSession session = req.getSession();

		Json json = new Json();
		try {
			System.out.println("login");
			sp.start();
			StoreServiceI storeService = sp.getStoreService();
			Map<String, String[]> map = req.getParameterMap();
			String userName = map.get("data.loginname")[0];
			String password = map.get("data.pwd")[0];
			System.out.println(userName + "====" + password);
			SingleCarparkStore store = storeService.findByLogin(userName, password);
			// !StrUtil.isEmpty(store)
			if (!StrUtil.isEmpty(store)) {
				// session.setAttribute(STORE_SESSION, store);
				// session.setMaxInactiveInterval(1000*60*15);
				json.setSuccess(true);
				SessionInfo info = new SessionInfo();
				info.setStoreName(CarparkUtils.encod(store.getStoreName()));
				info.setLoginName(CarparkUtils.encod(store.getLoginName()));
				info.setUserName(store.getUserName());
				json.setObj(info);
				// req.getRequestDispatcher(req.getContextPath()+"loginsuccess.jsp?userName="+store.getLoginName()+"&storeName="+store.getStoreName()+"").forward(req, resp);
			} else {
				json.setMsg("用户名或密码错误");
			}
		} catch (Exception e) {
			e.printStackTrace();
			json.setMsg("登录时发生错误" + e);
		}
		writeJson(json, req, resp);
	}

	private void writeJson(Object object, HttpServletRequest req, HttpServletResponse resp) {
		writeJsonByFilter(object, null, null, req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

	public void writeJsonByFilter(Object object, String[] includesProperties, String[] excludesProperties, HttpServletRequest req, HttpServletResponse resp) {
		try {
			FastjsonFilter filter = new FastjsonFilter();// excludes优先于includes
			if (excludesProperties != null && excludesProperties.length > 0) {
				filter.getExcludes().addAll(Arrays.<String> asList(excludesProperties));
			}
			if (includesProperties != null && includesProperties.length > 0) {
				filter.getIncludes().addAll(Arrays.<String> asList(includesProperties));
			}
			logger.info("对象转JSON：要排除的属性[" + excludesProperties + "]要包含的属性[" + includesProperties + "]");
			String json;
			String User_Agent = req.getHeader("User-Agent");
			if (StringUtils.indexOfIgnoreCase(User_Agent, "MSIE 6") > -1) {
				// 使用SerializerFeature.BrowserCompatible特性会把所有的中文都会序列化为\\uXXXX这种格式，字节数会多一些，但是能兼容IE6
				json = JSON.toJSONString(object, filter, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.BrowserCompatible);
			} else {
				// 使用SerializerFeature.WriteDateUseDateFormat特性来序列化日期格式的类型为yyyy-MM-dd hh24:mi:ss
				// 使用SerializerFeature.DisableCircularReferenceDetect特性关闭引用检测和生成
				json = JSON.toJSONString(object, filter, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.DisableCircularReferenceDetect);
			}
			logger.info("转换后的JSON字符串：" + json);
			resp.setContentType("text/html;charset=utf-8");
			resp.getWriter().write(json);
			resp.getWriter().flush();
			resp.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
