package com.travelzen.farerule.mongo.morphia;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.travelzen.farerule.mongo.morphia.config.MongoConfig;

@RunWith(JUnit4.class)
public class ConfigTest {

	@Test
	public void test() {
		System.out.println(MongoConfig.getFareruleUri());
		System.out.println(MongoConfig.getFareUri());
	}

}
