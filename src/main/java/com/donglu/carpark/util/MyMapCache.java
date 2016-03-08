package com.donglu.carpark.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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

	private Thread thread;
	Map<K, Date> mapPutTime=new HashMap<>();

	private String name;

	private int cacheNum=100;
	
	public MyMapCache(){
		
	}
	/**
	 * 启动缓存
	 * @param cacheTime 缓存时间
	 * @param cacheNum 缓存数量  控制缓存的清理  当缓存数量超过了设置的数量就会执行清理方法 0表示不清理
	 * @param name 名称
	 */
	
	public MyMapCache(long cacheTime,int cacheNum,String name){
		this.cacheTime=cacheTime;
		this.name=name;
		this.cacheNum=cacheNum;
	}
	
	public void startCache(){
		
	}
	@Override
	public V put(K key, V value) {
		V put = super.put(key, value);
		cleanCache(key);
		return put;
	}
	private void cleanCache(K key) {
		System.out.println("cacheTime===="+cacheTime+"---thread===="+thread);
		if (cacheTime>0) {
			mapPutTime.put(key, new Date());
			if (cacheNum>0&&size()>cacheNum) {
				if (thread==null) {
					thread = new Thread(new Runnable() {
						@Override
						public void run() {
							Calendar c= Calendar.getInstance();
							c.setTime(new Date());
							c.add(Calendar.MILLISECOND, cacheTime.intValue()*-1);
							Date time = c.getTime();
							List<K> list=new ArrayList<>(mapPutTime.keySet());
							System.out.println("list========"+list);
							for (K k : list) {
								if (mapPutTime.get(k).before(time)) {
									mapPutTime.remove(k);
									remove(k);
								}
							}
							thread=null;
						}
					});
					if (name!=null) {
						thread.setName(name);
					}
					thread.start();
				}
			}
		}
		
	}
	@Override
	public void clear() {
		super.clear();
		mapPutTime.clear();
	}
}
