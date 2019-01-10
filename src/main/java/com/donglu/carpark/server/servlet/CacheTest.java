package com.donglu.carpark.server.servlet;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class CacheTest {
	public static void main(String[] args) throws Exception {
		Cache<Object, Object> build = CacheBuilder.newBuilder().expireAfterWrite(3, TimeUnit.SECONDS).build();
		build.put("1", "1");
		System.out.println(build.getIfPresent("1"));
		Thread.sleep(5000);
		System.out.println(build.asMap().get("1"));
	}
}
