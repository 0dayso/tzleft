package com.travelzen.farerule.exchange.task;

import java.util.Arrays;
import java.util.List;

import javax.xml.rpc.ServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

import com.travelzen.farerule.exchange.consts.ExchangeConst;
import com.travelzen.farerule.exchange.webservicex.Currency;
import com.travelzen.farerule.exchange.webservicex.CurrencyConvertor;
import com.travelzen.farerule.exchange.webservicex.CurrencyConvertorLocator;
import com.travelzen.farerule.exchange.webservicex.CurrencyConvertorSoap;

public class ExchangeFetchTask implements Runnable {
	
	private static final Logger log = LoggerFactory.getLogger(ExchangeFetchTask.class);
	
	static {
		LoggerContext lc = (LoggerContext)LoggerFactory.getILoggerFactory();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(lc);
        lc.reset();
        try {
            configurator.doConfigure("src/main/resources/properties/logback.xml");
       } catch (JoranException e) {
    	   e.printStackTrace();
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
	}
	
	private CurrencyConvertor convertor;
	private CurrencyConvertorSoap convertorSoap;
	
	public ExchangeFetchTask() {
		convertor = new CurrencyConvertorLocator();
		try {
			convertorSoap = convertor.getCurrencyConvertorSoap();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
	
	public static final List<String> currencyList = Arrays.asList(
			new String[]{"AED","AFN","ALL","AMD","ANG","AOA","ARS","AUD","AWG","AZM","BAM","BBD","BDT","BGN",
					"BHD","BIF","BMD","BND","BOB","BOV","BRL","BSD","BTN","BWP","BYR","BZD","CAD","CDF","CHF",
					"CLF","CLP","CNY","COP","COU","CRC","CSD","CUP","CVE","CYP","CZK","DJF","DKK","DOP","DZD",
					"ECU","EEK","EGP","ERN","ETB","EUR","FJD","FKP","GBP","GEL","GHC","GIP","GMD","GNF","GTQ",
					"GYD","HKD","HNL","HRK","HTG","HUF","IDR","ILS","INR","IQD","IRR","ISK","JMD","JOD","JPY",
					"KES","KGS","KHR","KMF","KPW","KRW","KWD","KYD","KZT","LAK","LBP","LKR","LRD","LSL","LTL",
					"LVL","LYD","MAD","MDL","MGA","MKD","MMK","MNT","MOP","MRO","MTL","MUR","MVR","MWK","MXN",
					"MXV","MYR","MZM","NAD","NGN","NIO","NOK","NPR","NZD","OMR","PAB","PEN","PGK","PHP","PKR",
					"PLN","PYG","QAR","ROL","RUB","RWF","SAR","SBD","SCR","SDD","SEK","SGD","SHP","SIT","SKK",
					"SLL","SOS","SRD","STD","SVC","SYP","SZL","THB","TJS","TMM","TND","TOP","TRL","TRY","TTD",
					"TWD","TZS","UAH","UGX","USD","USN","USS","UYU","UZS","VEB","VND","VUV","WST","XAF","XAG",
					"XAU","XCD","XOF","XPF","YER","ZAR","ZMK","ZWD"});
	
	@Override
	public void run() {
		while (true) {
			int count = 0;
			try {
				log.info("Start fetching exchange...(" + ++count +")");
				for (String currency:currencyList) {
					Currency fromCurrency = Currency.fromString(currency);
					if (fromCurrency == null)
						continue;
					Currency toCurrency = Currency.CNY;
					double rate = convertorSoap.conversionRate(fromCurrency, toCurrency);
					ExchangeConst.exchangeMap.put(currency, rate);
//					System.out.println(currency + " -> " + rate);
				}
				log.info("Exchange fetching finish!");
//				System.out.println(ExchangeConst.exchangeMap.get("XAU"));
				Thread.sleep(600000);
			} catch (Exception e) {
				log.info("Exchange Fetching Error!", e);
				break;
			}
		}
	}

}
