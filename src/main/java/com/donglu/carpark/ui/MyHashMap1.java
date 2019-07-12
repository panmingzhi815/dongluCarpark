package com.donglu.carpark.ui;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyHashMap1<K, V> extends HashMap<K, V> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Logger LOGGER=LoggerFactory.getLogger(MyHashMap1.class);
	private Map<K, V> map;
	
	public MyHashMap1(Map<K,V> map) {
		this.map = map;
		putAll(map);
	}
	
	@Override
	public V remove(Object key) {
		LOGGER.info("移除数据：{}",key);
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		for (int i = 0; i < Math.min(stackTrace.length, 8); i++) {
			StackTraceElement element = stackTrace[i];
			LOGGER.info("{}-{}-{} 调用remove",element.getClassName(),element.getMethodName(),element.getLineNumber());
		}
		map.remove(key);
		return super.remove(key);
	}
	
	@Override
	public V put(K key, V value) {
		map.put(key, value);
		return super.put(key, value);
	}
}
