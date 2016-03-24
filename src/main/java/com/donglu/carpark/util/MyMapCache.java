package com.donglu.carpark.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * 自定义map缓存，可以设置缓存时间
 * @author huangjianxiong
 *
 * @param <K>
 * @param <V>
 */
public class MyMapCache<K, V> extends HashMap<K, V> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long cacheTime=0L;

	Map<K, Date> mapPutTime=new HashMap<>();
	List<K> listKey=new LinkedList<>();


	private int cacheNum=100;
	
	public MyMapCache(){
		
	}
	/**
	 * 启动缓存
	 * @param cacheTime 缓存时间
	 * @param cacheNum 缓存数量  控制缓存的清理  当缓存数量超过了设置的数量就会执行清理方法 0表示不清理
	 * @param name 名称
	 */
	
	public MyMapCache(long cacheTime,int cacheNum){
		this.cacheTime=cacheTime;
		this.cacheNum=cacheNum;
	}
	
	public void startCache(){
		
	}
	@Override
	public V put(K key, V value) {
		listKey.add(key);
		V put = super.put(key, value);
		cleanCache(key);
		return put;
	}
	private void cleanCache(K key) {
		if (cacheTime>0) {
			mapPutTime.put(key, new Date());
			if (cacheNum > 0 && size() > cacheNum) {
				Calendar c = Calendar.getInstance();
				c.setTime(new Date());
				c.add(Calendar.MILLISECOND, cacheTime.intValue() * -1);
				Date time = c.getTime();
				List<K> list = new ArrayList<>(mapPutTime.keySet());
				for (K k : list) {
					if (mapPutTime.get(k).before(time)) {
						mapPutTime.remove(k);
						remove(k);
					}
				}
				if (size() > cacheNum) {
					synchronized (listKey) {
						K remove = listKey.remove(0);
						remove(remove);
					}
				}
			}
		}
		
	}
	@Override
	public void clear() {
		super.clear();
		mapPutTime.clear();
	}
	public static void main(String[] args) {
		MyMapCache<Integer, Integer> m=new MyMapCache<>(1000000, 3);
		for (int i = 0; i < 20; i++) {
			long nanoTime = System.nanoTime();
			m.put(i, i);
			System.out.println((System.nanoTime()-nanoTime)+"========="+m);
		}
	}
}
