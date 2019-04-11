package com.donglu.carpark.server.servlet;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.donglu.carpark.model.Result;
import com.donglu.carpark.util.ClassUtils;


public abstract class AbstractHttpServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Map<String,Method> mapMapping=new HashMap<>();
	protected static final Logger LOGGER=LoggerFactory.getLogger(AbstractHttpServlet.class);
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String pathInfo = req.getPathInfo();
			LOGGER.info("检测到请求:{}-{}",pathInfo,req.getSession(true).getId());
			for (String string : req.getParameterMap().keySet()) {
				System.out.println(string+"=="+req.getParameter(string));
			}
			if(mapMapping.get(pathInfo)!=null) {
				Method method = mapMapping.get(pathInfo);
				Parameter[] parameters = method.getParameters();
				Object[] objects=new Object[parameters.length];
				String[] parameterNames = ClassUtils.getParameterNames(method);
				if(parameterNames.length!=parameters.length) {
					LOGGER.info("方法：{} 获取参数名错误",method);
					return;
				}
				for (int i = 0; i < parameters.length; i++) {
					Parameter parameter = parameters[i];
					if(parameter.getType().equals(HttpServletRequest.class)) {
						objects[i]=req;
						continue;
					}else if(parameter.getType().equals(HttpServletResponse.class)) {
						objects[i]=resp;
						continue;
					}
					RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
					String name=parameterNames[i];
					if (requestParam!=null) {
						name = requestParam.value();
					}
					System.out.println(name);
					String p = req.getParameter(name);
					if(parameter.getType().equals(Integer.class)||parameter.getType().equals(int.class)) {
						if(p==null) {
							objects[i]=parameter.getType().equals(int.class)?0:null;
						}else {
							objects[i]=Integer.valueOf(p);
						}
					}else if(parameter.getType().equals(Long.class)||parameter.getType().equals(long.class)) {
						if(p==null) {
							objects[i]=parameter.getType().equals(int.class)?0:null;
						}else {
							objects[i]=Long.valueOf(p);
						}
					}
					else if(parameter.getType().equals(Double.class)||parameter.getType().equals(double.class)) {
						if(p==null) {
							objects[i]=parameter.getType().equals(int.class)?0d:null;
						}else {
							objects[i]=Double.valueOf(p);
						}
					}
					else if(parameter.getType().equals(Float.class)||parameter.getType().equals(float.class)) {
						if(p==null) {
							objects[i]=parameter.getType().equals(int.class)?0:null;
						}else {
							objects[i]=Float.valueOf(p);
						}
					}else {
						objects[i]=p;
					}
					
				}
				Object invoke = method.invoke(this, objects);
				if (invoke!=null) {
					resp.setContentType("application/json;charset=utf-8");//指定返回的格式为JSON格式
					resp.setCharacterEncoding("UTF-8");
					resp.getOutputStream().write(JSON.toJSONString(invoke).getBytes("UTF-8"));
					resp.getOutputStream().flush();
					resp.getOutputStream().close();
				}
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.service(req, resp);
	}
	@Override
	public void init() throws ServletException {
		super.init();
		Method[] methods = getClass().getMethods();
		for (Method method : methods) {
			RequestMapping annotation = method.getAnnotation(RequestMapping.class);
			if (annotation==null) {
				continue;
			}
			String value = annotation.value();
			mapMapping.put(value, method);
		}
	}
	
	
	
	public static String getClassFileName(Class<?> clazz) {
		String className = clazz.getName();
		int lastDotIndex = className.lastIndexOf(".");
		return className.substring(lastDotIndex + 1) + ".class";
	}
	public Object createResult(boolean b, int i, String string, Object object) {
		Result result = new Result();
		result.setSuccess(b);
		result.setCode(i);
		result.setMsg(string);
		result.setObj(object);
		return result;
	}
}
