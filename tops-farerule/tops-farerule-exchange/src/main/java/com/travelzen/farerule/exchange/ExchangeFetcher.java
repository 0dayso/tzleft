package com.travelzen.farerule.exchange;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.travelzen.farerule.exchange.task.ExchangeYahooTask;

public class ExchangeFetcher {
	
	public void start() {
		ExecutorService exec = Executors.newSingleThreadExecutor();
//		exec.execute(new ExchangeFetchTask());
		exec.execute(new ExchangeYahooTask());
		exec.shutdown();
	}

	public static void main(String[] args) {
		ExecutorService exec = Executors.newSingleThreadExecutor();
//		exec.execute(new ExchangeFetchTask());
		exec.execute(new ExchangeYahooTask());
		exec.shutdown();
	}
}
