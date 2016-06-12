/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.cpecker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;

import com.travelzen.farerule.cpecker.LogBase;
import com.travelzen.farerule.cpecker.pecker.*;

@RunWith(JUnit4.class)
public class TmpCpeckerTest {

	private static final Logger log = LoggerFactory.getLogger(TmpCpeckerTest.class);

	@Test
	public void testNormal() {
		LogBase.logBack();
		
		String text6 = 
				"在2014年11月22日/前旅行\n"
				+ "必须经过一个星期天。\n"
				+ "\n"
				+ "在2014年11月23日/后和2014年11月26日/前旅行\n"
				+ "必须经过一个星期四。\n"
				+ "\n"
				+ "在2014年11月29日/后和2014年12月16日/前旅行\n"
				+ "必须经过一个星期天。\n"
				+ "\n"
				+ "在2014年12月17日/后和2014年12月23日/前旅行\n"
				+ "必须经过一个星期六。\n"
				+ "\n"
				+ "在2014年12月26日/后和2014年12月30日/前旅行\n"
				+ "必须经过一个星期六。\n"
				+ "\n"
				+ "在2015年1月02日/后旅行\n"
				+ "必须经过一个星期天。";
				
//		String text61 = 
//				"改期：必须在起飞前至少21天。\n"
//				+ "出票：必须在改期后72小时内或起飞前至少21天。\n"
//				+ "改期/出票取较早者。\n"
//				+ "\n"
//				+ "中转：1天。";
		
		String text7 = "中国始发：14天。台湾始发：1月。";
		
		String text71 = "中国始发-\n"
				+ "7天。\n\n"
				+ "香港始发-\n"
				+ "1月。";
		
		long startTime = System.nanoTime();
		
		try {		
//			Cpecker6 cp6 = new Cpecker6();
//			cp6.parse(text6);
//			System.out.println(cp6.getMinStay());
			
			Cpecker7 cp7 = new Cpecker7();
			cp7.parse(text7);
			System.out.println(cp7.getMaxStay());
		} catch(Exception e) {
			e.printStackTrace();
		}	
		
		long endTime = System.nanoTime();
		log.debug((double)(endTime-startTime)/1e9 + " s");
	}

}
