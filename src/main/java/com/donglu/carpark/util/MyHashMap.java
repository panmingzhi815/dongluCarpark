package com.donglu.carpark.util;

import java.util.HashMap;

public class MyHashMap<K, V> extends HashMap<K, V>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5582384543574418639L;
	@Override
	public V put(K key, V value) {
		return super.put(key, value);
	}
}
