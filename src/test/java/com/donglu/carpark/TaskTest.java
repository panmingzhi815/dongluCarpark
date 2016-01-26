package com.donglu.carpark;

public class TaskTest {
	
	public static void main(String[] args) {
		TaskTest taskTest = new TaskTest();
		TaskA a = taskTest.new TaskA();
		TaskB b = taskTest.new TaskB();
		a.run();
		b.run();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		a.notify();
		
	}
	public class TaskA implements Runnable {

		@Override
		public void run() {
			try {
				System.out.println("TaskA is start");
				wait(99999);
				System.out.println("TaskA is end");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	public class TaskB implements Runnable {

		@Override
		public void run() {
			try {
				System.out.println("TaskB is start");
				wait(99999);
				System.out.println("TaskB is end");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}
