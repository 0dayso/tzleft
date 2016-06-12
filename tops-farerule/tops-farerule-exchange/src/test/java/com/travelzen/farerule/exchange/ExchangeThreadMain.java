package com.travelzen.farerule.exchange;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExchangeThreadMain {

	public static void main(String[] args) {
		startThread();
	}
	
	private static void startThread() {
		new ExchangeTester().run();
		Thread t = new Thread(new ExchangeTester());
		t.start();
	}
	
	private static void startThreads() {
		int i = 3;
		while (i-- >0) {
			Thread daemon = new Thread(new ExchangeTester());
			daemon.setDaemon(true);
			daemon.start();
		}
		new Thread(new ExchangeTester()).start();
		System.out.println("Start?");
	}
	
	// First choice
	private static void startCachedThreadPool() {
		ExecutorService exec = Executors.newCachedThreadPool();
		int i = 3;
		while (i-- >0)
			exec.execute(new ExchangeTester(Thread.MIN_PRIORITY));
		exec.execute(new ExchangeTester(Thread.MAX_PRIORITY));
		exec.shutdown();
	}
	
	private static void startFixedThreadPool() {
		ExecutorService exec = Executors.newFixedThreadPool(5);
		int i = 5;
		while (i-- >0)
			exec.execute(new ExchangeTester());
		exec.shutdown();
	}
	
	private static void startSingleThreadPool() {
		ExecutorService exec = Executors.newSingleThreadExecutor();
		int i = 5;
		while (i-- >0)
			exec.execute(new ExchangeTester());
		exec.shutdown();
	}
}
