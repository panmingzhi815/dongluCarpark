package com.donglu.carpark.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class TestMap<K,V,V1> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3696199013527233004L;
	
	Map<K, V> map=new HashMap<>();
	Map<K, V1> map1=new HashMap<>();
	
	public void put(K k,V v,V1 v1){
		map.put(k, v);
		map1.put(k, v1);
	}
	public V get(K k){
		return map.get(k);
	}
	public V1 get1(K k){
		return map1.get(k);
	}
	public Object[] getAll(K k){
		Object[] os=new Object[2];
		os[0]=map.get(k);
		os[1]=map1.get(k);
		return os;
	}
}
