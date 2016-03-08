package com.donglu.carpark.server.module;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class TestMethodInterceptor implements MethodInterceptor {


	@Override
	public Object invoke(MethodInvocation mi) throws Throwable {
		Object[] arguments = mi.getArguments();
		Method method = mi.getMethod();
		System.out.println(method);
		for (Object object : arguments) {
			System.out.println(method.getName()+"======"+object);
		}
		return mi.proceed();
	}

}
