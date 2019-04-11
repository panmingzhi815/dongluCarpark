package com.donglu.carpark.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

public class ClassUtils {
	static final Logger LOGGER=LoggerFactory.getLogger(ClassUtils.class);
	static final Map<Method,String[]> mapParameterCache=new HashMap<>();
	public static String[] getParameterNames(Method method) {
		if(mapParameterCache.get(method)!=null) {
			return mapParameterCache.get(method);
		}
		ClassPool classPool = ClassPool.getDefault();
		try {
			CtClass ctClass = classPool.get(method.getDeclaringClass().getName());
			CtMethod ctMethod = ctClass.getDeclaredMethod(method.getName());
			MethodInfo methodInfo = ctMethod.getMethodInfo();
			CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
			LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute
					.getAttribute(LocalVariableAttribute.tag);
			if (attr != null) {
				int len = ctMethod.getParameterTypes().length;
				// 非静态的成员函数的第一个参数是this
				int pos = Modifier.isStatic(ctMethod.getModifiers()) ? 0 : 1;
				System.out.print(method.getName()+" : ");
				List<String> parameterNames=new ArrayList<>();
				for (int i = 0; i < len; i++) {
					System.out.print(attr.variableName(i + pos) + ' ');
					parameterNames.add(attr.variableName(i + pos));
				}
				LOGGER.info("方法：{} 參數名：{}",method.getName(),parameterNames);
				System.out.println();
				String[] array = parameterNames.toArray(new String[parameterNames.size()]);
				mapParameterCache.put(method, array);
				return array;
			}
		} catch (Exception e) {
			LOGGER.info("获取方法：{} 参数时发生错误",method,e);
		}
		return null;
	}
}
