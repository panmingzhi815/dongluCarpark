package com.donglu.carpark.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;



public class ExecutorsUtils {
	private static final List<ExecutorService> listExecutorService=new ArrayList<>();
	
	static{
		try {
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				@Override
				public void run() {
					System.out.println("系统退出");
					for (ExecutorService executorService : listExecutorService) {
						executorService.shutdown();
					}
				}
			}));
			System.out.println("添加退出hook");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static ScheduledExecutorService scheduleWithFixedDelay(Runnable command,long initialDelay,long delay,TimeUnit unit,String threadName){
		ScheduledExecutorService executorService = null;
		if (threadName!=null) {
			executorService = Executors.newSingleThreadScheduledExecutor(createThreadFactory(threadName));
		}else{
			executorService = Executors.newSingleThreadScheduledExecutor();
		}
		executorService.scheduleWithFixedDelay(command, initialDelay, delay, unit);
		listExecutorService.add(executorService);
		return executorService;
	}
	
	public static ScheduledExecutorService scheduleWithFixedDelay(Runnable command,long initialDelay,long delay,TimeUnit unit){
		return scheduleWithFixedDelay(command, initialDelay, delay, unit, null);
	}
	
	public static ExecutorService newSingleThreadExecutor(String threadName){
		return newSingleThreadExecutor(createThreadFactory(threadName));
	}
	/**
	 * 单例线程池
	 * @param threadFactory
	 * @return
	 */
    public static ExecutorService newSingleThreadExecutor(ThreadFactory threadFactory){
    	return Executors.newSingleThreadExecutor(threadFactory);
    }
	private static ThreadFactory createThreadFactory(String title) {
		ThreadFactory threadFactory = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, title);
			}
		};
		return threadFactory;
	}
}
