package com.travelzen.farerule.exchange;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import com.travelzen.farerule.exchange.webservicex.*;

public class ExchangeMain {

	public static void main(String[] args) {
		try {
			CurrencyConvertor convertor = new CurrencyConvertorLocator();
			CurrencyConvertorSoap convertorSoap = convertor.getCurrencyConvertorSoap();
			@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new FileReader(new File("src/main/resources/data/currency.txt")));
			String currency = "";
			while ((currency = reader.readLine()) != null) {
				Currency fromCurrency = Currency.fromString(currency);
				if (fromCurrency == null) {
//					System.out.println(currency + " NULL");
					continue;
				}
				Currency toCurrency = Currency.CNY;
				double rate = convertorSoap.conversionRate(fromCurrency, toCurrency);
				System.out.println("put(\"" + currency + "\", " + rate + ");");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
