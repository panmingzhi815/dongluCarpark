package com.donglu.carpark.server.module;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class TestMethodInterceptor implements MethodInterceptor {
	@Override
	public Object invoke(MethodInvocation mi) throws Throwable {
		Object[] arguments = mi.getArguments();
		if (arguments!=null) {
			for (Object object : arguments) {
				System.out.print(object + "--");
			}
			System.out.println();
			System.out.println("===============================");
		}
		Method method = mi.getMethod();
		CacheMethod annotation = method.getAnnotation(CacheMethod.class);
		if (annotation!=null) {
			System.out.println("annotation======================"+annotation.value()[0]);
		}
		System.out.println(method);
		for (Object object : arguments) {
			System.out.println(method.getName()+"======"+object);
		}
		return mi.proceed();
	}

}
