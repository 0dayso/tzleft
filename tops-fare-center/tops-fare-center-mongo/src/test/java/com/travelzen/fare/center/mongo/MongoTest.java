package com.travelzen.fare.center.mongo;

import org.junit.Test;

import com.travelzen.fare.center.mongo.morphia.FlightMorphia;

/**
 * @author yiming.yan
 * @Date Nov 26, 2015
 */
public class MongoTest {
	
	@Test
	public void test() {
//		FlightMorphia.INSTANCE.deleteAll();
		System.out.println(FlightMorphia.INSTANCE.countAll());
	}

}
