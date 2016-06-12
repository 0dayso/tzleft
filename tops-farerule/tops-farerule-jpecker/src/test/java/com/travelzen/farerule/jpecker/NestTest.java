/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.jpecker;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;

import com.travelzen.farerule.jpecker.nest.*;

@RunWith(JUnit4.class)
public class NestTest {

	@Test
	public void testNormal() {
		LogBase.logBack();
		
		JpeckerNest jn = new JpeckerNest();
		long updateTime = 0;
		jn.nest(updateTime);
	}
}
