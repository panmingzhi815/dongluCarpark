package com.donglu.carpark.util;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class InjectorUtil {
	private static boolean isStart=false;
	private static Injector injector;
	
	public static void start(final Module... modules){
		Runnable runnable = new Runnable() {
			public void run() {
				startsyn(modules);
			}
		};
		new Thread(runnable).start();
	}
	public <T> T getInstance(Class<T> t){
		if (!isStart) {
			return null;
		}
		return injector.getInstance(t);
	}
	public static boolean isStart() {
		return isStart;
	}
	public static Injector getInjector() {
		return injector;
	}
	/**
	 * @param modules
	 */
	public static Injector startsyn(final Module... modules) {
		injector = Guice.createInjector(modules);
		isStart=true;
		return injector;
	}
}
