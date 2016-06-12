package com.travelzen.farerule.exchange;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import com.travelzen.farerule.exchange.webservicex.*;

public class ExchangeTester implements Runnable {

	private int priority;
	private CurrencyConvertor convertor;
	private CurrencyConvertorSoap convertorSoap;
	
	public ExchangeTester() {
		this.priority = Thread.NORM_PRIORITY;
		convertor = new CurrencyConvertorLocator();
		try {
			convertorSoap = convertor.getCurrencyConvertorSoap();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
	
	public ExchangeTester(int priority) {
		this.priority = priority;
		convertor = new CurrencyConvertorLocator();
		try {
			convertorSoap = convertor.getCurrencyConvertorSoap();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
	
	private static int taskCount = 1;
	private final int id = taskCount++;
	
	@Override
	public void run() {
		Thread.currentThread().setPriority(priority);
		Currency fromCurrency = Currency.USD;
		Currency toCurrency = Currency.CNY;
		double rate = 0;
		int countDown = 5;
		while (countDown-- > 0) {
			try {
				rate = convertorSoap.conversionRate(fromCurrency, toCurrency);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			System.out.println("(" + id + ") " + fromCurrency + "->" + toCurrency + ":" + rate);
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("END-" + id);
	}
}
