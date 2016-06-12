/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.cpecker;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;

import com.travelzen.farerule.cpecker.LogBase;
import com.travelzen.farerule.cpecker.pecker.*;
import com.travelzen.farerule.translator.RuleTranslator16;

@RunWith(JUnit4.class)
public class TmpCpecker16Test {

	@Test
	public void testPenalties() {
		LogBase.logBack();
		
		Cpecker16 cp16;
		String str1 = "", str2 = "", str3 = "";
		
		try {
		// 1
		str1 = "完全未使用客票：\n允许，成人收取退票费500.0人民币。\n儿童收取退票费500.0人民币。\n部分使用客票：允许。";
		str2 = "允许，收取手续费100人民币。\n路线更改：允许，收取手续费500.0人民币。";
		str3 = "退票，起飞前，误机情况下，取消/退票收取手续费1500人民币，\n"
				+ "即取消/退票费500人民币加上误机费1000人民币。\n"
				+ "更改，误机情况下，收取手续费1000人民币，即零重新订座费\n"
				+ "加上误机费1000人民币。";
		cp16 = new Cpecker16();
		cp16.parse(str1, str2,str3);
		System.out.println("1\n"+cp16.getPenalties());
		System.out.println(RuleTranslator16.translate(cp16.getPenalties()));
		
		// 2
		str1 = "起飞前，允许，收取票价的25%。\n起飞后，不允许。";
		str2 = "收取票价的25%。";
		str3 = "收取票价的25%。";
		cp16 = new Cpecker16();
		cp16.parse(str1, str2,str3);
		System.out.println("2\n"+cp16.getPenalties());
		System.out.println(RuleTranslator16.translate(cp16.getPenalties()));
		
		// 3
		str1 = "完全未使用的客票-\n"
				+ "允许，每张客票收取手续费6000日元/其他等值货币。\n\n"
				+ "部分使用的客票-\n"
				+ "允许，每张客票扣除已使用的的同等舱位或更高舱位的票价加上退票费6000日元/\n"
				+ "其他等值货币。";
		str2 = "去程-换开：允许，收取手续费4000日元。回程-换开：允许，收取手续费4000日元。";
		str3 = "允许。";
		cp16 = new Cpecker16();
		cp16.parse(str1, str2,str3);
		System.out.println("3\n"+cp16.getPenalties());
		System.out.println(RuleTranslator16.translate(cp16.getPenalties()));
		
		// 4
		str1 = "全部未使用客票：允许，\n成人票收取手续费500.0人民币。\n儿童票收取手续费500.0人民币。\n部分使用客票：不允许。";
		str2 = "收取手续费300 人民币。";
		str3 = "收取手续费200人民币。";
		cp16 = new Cpecker16();
		cp16.parse(str1, str2,str3);
		System.out.println("4\n"+cp16.getPenalties());
		System.out.println(RuleTranslator16.translate(cp16.getPenalties()));
		
		// 5
		str1 = "允许，收取手续费500人民币/80.00美元";
		str2 = "起飞前，允许，收取手续费130人民币/160港币。\n起飞后，允许，收取手续费200人民币/250港币。";
		str3 = "无限制。";
		cp16 = new Cpecker16();
		cp16.parse(str1, str2,str3);
		System.out.println("5\n"+cp16.getPenalties());
		System.out.println(RuleTranslator16.translate(cp16.getPenalties()));
		
		// 6
		str1 = "起飞前，退票收取手续费300人民币。\n起飞后，退票不允许。";
		str2 = "出票后改期，日期更改换开：允许，免费。\n路线更改：允许，收取手续费500.0人民币";
		str3 = "无限制。";
		cp16 = new Cpecker16();
		cp16.parse(str1, str2,str3);
		System.out.println("6\n"+cp16.getPenalties());
		System.out.println(RuleTranslator16.translate(cp16.getPenalties()));
		
		// 7
		str1 = "起飞前，允许，收取手续费300人民币。\n起飞后，不允许。";
		str2 = "允许。\n路线更改：\n允许。";
		str3 = "无限制。";
		cp16 = new Cpecker16();
		cp16.parse(str1, str2,str3);
		System.out.println("7\n"+cp16.getPenalties());
		System.out.println(RuleTranslator16.translate(cp16.getPenalties()));
		
		// 8
		str1 = "起飞前，允许，收取手续费1000人民币/125.00欧元。\n起飞后，不允许。";
		str2 = "允许，收取手续费200人民币。\n路线更改，允许，收取手续费200人民币。";
		str3 = "无限制。";
		cp16 = new Cpecker16();
		cp16.parse(str1, str2,str3);
		System.out.println("8\n"+cp16.getPenalties());
		System.out.println(RuleTranslator16.translate(cp16.getPenalties()));
		
		// 9
		str1 = "起飞前，允许，收取手续费130人民币/160港币。\n起飞后，允许，收取手续费200人民币/250港币。";
		str2 = "起飞前，允许，收取手续费130人民币/160港币。\n起飞后，允许，收取手续费200人民币/250港币。";
		str3 = "无限制。";
		cp16 = new Cpecker16();
		cp16.parse(str1, str2,str3);
		System.out.println("9\n"+cp16.getPenalties());
		System.out.println(RuleTranslator16.translate(cp16.getPenalties()));
		
		// 10
		str1 = "欧洲始发-\n允许，收取手续费100.00欧元。\n\n3区始发-\n允许，收取手续费100.00美元。\n";
		str2 = "欧洲始发-\n允许，收取手续费100.00欧元。\n\n3区始发-\n允许，收取手续费100.00美元。";
		str3 = "不允许。";
		cp16 = new Cpecker16();
		cp16.parse(str1, str2,str3);
		System.out.println("10\n"+cp16.getPenalties());
		System.out.println(RuleTranslator16.translate(cp16.getPenalties()));
		
		// 11
		str1 = "2014年5月19日/前出票\n"
			+ "允许，收取手续费450人民币。\n"
			+ "\n"
			+ "2014年5月20日/后出票\n"
			+ "允许，收取手续费650人民币。\n";
		str2 = "2014年5月19日/前出票\n"
			+ "改期：允许。\n"
			+ "换开：允许，收取手续费300人民币。\n"
			+ "\n"
			+ "2014年5月20日/后出票\n"
			+ "改期：允许。\n"
			+ "换开：允许，收取手续费400人民币。\n";
		str3 = "2014年5月19日/前出票\n"
			+ "退票：收取手续费450人民币。\n"
			+ "\n"
			+ "2014年5月20日/后出票\n"
			+ "退票：收取手续费650人民币。\n";
		str3 = "无限制。";
		cp16 = new Cpecker16();
		cp16.parse(str1, str2,str3);
		System.out.println("11\n"+cp16.getPenalties());
		System.out.println(RuleTranslator16.translate(cp16.getPenalties()));
		
		// 12
		str1 = "2014年5月19日或之前旅行\n"
			+ "允许，收取手续费450人民币。\n"
			+ "\n"
			+ "2014年5月20日或之后旅行\n"
			+ "允许，收取手续费650人民币。\n";
		str2 = "2014年5月19日或之前旅行\n"
			+ "改期：允许。\n"
			+ "换开：允许，收取手续费300人民币。\n"
			+ "\n"
			+ "2014年5月20日或之后旅行\n"
			+ "改期：允许。\n"
			+ "换开：允许，收取手续费400人民币。\n";
		str3 = "2014年5月19日或之前旅行\n"
			+ "退票：收取手续费450人民币。\n"
			+ "\n"
			+ "2014年5月20日或之后旅行\n"
			+ "退票：收取手续费650人民币。\n";
		str3 = "无限制。";
		cp16 = new Cpecker16();
		cp16.parse(str1, str2,str3);
		System.out.println("12\n"+cp16.getPenalties());
		System.out.println(RuleTranslator16.translate(cp16.getPenalties()));
		
		// 13
		str1 = "中国始发-\n"
			+ "2014年5月19日/前出票\n"
			+ "允许，收取手续费450人民币。\n"
			+ "\n"
			+ "2014年5月20日/后出票\n"
			+ "允许，收取手续费650人民币。\n"
			+ "美国始发-\n"
			+ "2014年5月19日/前出票\n"
			+ "允许，收取手续费75美元。\n"
			+ "\n"
			+ "2014年5月20日/后出票\n"
			+ "允许，收取手续费100美元。\n";
		str2 = "中国始发-\n"
			+ "2014年5月19日/前出票\n"
			+ "改期：允许。\n"
			+ "换开：允许，收取手续费300人民币。\n"
			+ "\n"
			+ "2014年5月20日/后出票\n"
			+ "改期：允许。\n"
			+ "换开：允许，收取手续费400人民币。\n"
			+ "美国始发-\n"
			+ "2014年5月19日/前出票\n"
			+ "改期：允许。\n"
			+ "换开：允许，收取手续费50美元。\n"
			+ "\n"
			+ "2014年5月20日/后出票\n"
			+ "改期：允许。\n"
			+ "换开：允许，收取手续费65美元。\n";
		str3 = "不允许。\n";
		cp16 = new Cpecker16();
		cp16.parse(str1, str2,str3);
		System.out.println("13\n"+cp16.getPenalties());
		System.out.println(RuleTranslator16.translate(cp16.getPenalties()));
		
		// 14
		str1 = "新喀里多尼亚始发-\n"
			+ "允许。\n"
			+ "\n"
			+ "欧洲始发-\n" 
			+ "允许，收取手续费225.00欧元。\n";
		str2 = "新喀里多尼亚始发-\n"
			+ "2014年3月31日或之前出票：\n"
			+ "改期：允许。\n"
			+ "换开：允许，收取手续费8700法国和平的法郎。\n"
			+ "\n"
			+ "2014年4月01日或之后出票：\n"
			+ "改期：允许。\n"
			+ "换开：允许。\n"
			+ "\n"
			+ "欧洲始发-\n"
			+ "允许。\n";
		str3 = "新喀里多尼亚始发-\n"
			+ "退票：允许。\n"
			+ "\n"
			+ "欧洲始发-\n"
			+ "退票：收取手续费225.00欧元。\n";
		cp16 = new Cpecker16();
		cp16.parse(str1, str2,str3);
		System.out.println("14\n"+cp16.getPenalties());
		System.out.println(RuleTranslator16.translate(cp16.getPenalties()));
		
		// 15
		str1 = "台湾始发-\n"
			+ "在2014年5月19日/前出票\n"
			+ "允许，收取手续费2000台币。\n"
			+ "\n"
			+ "在2014年5月20日/后出票\n"
			+ "允许，收取手续费3000台币。\n"
			+ "\n"
			+ "3区始发-\n"
			+ "在2014年5月19日/前出票\n"
			+ "允许，收取手续费75.00台币。\n"
			+ "\n"
			+ "3区始发-\n"
			+ "在2014年5月20日/后出票\n"
			+ "允许，收取手续费100.00台币。\n";
		str2 = "台湾始发-\n"
			+ "在2014年5月19日/前出票\n"
			+ "改期：允许。\n"
			+ "换开：允许，收取手续费1500台币。\n"
			+ "\n"
			+ "在2014年5月20日/后出票\n"
			+ "改期：允许。\n"
			+ "换开：允许，收取手续费1800台币。\n"
			+ "\n"
			+ "\n"
			+ "3区始发-\n"
			+ "在2014年5月19日/前出票\n"
			+ "改期：允许。\n"
			+ "换开：允许，收取手续费50.00台币。\n"
			+ "\n"
			+ "3区始发-\n"
			+ "在2014年5月20日/后出票\n"
			+ "改期：允许。\n"
			+ "换开：允许，收取手续费60.00台币。\n";
		str3 = "台湾始发-\n"
			+ "在2014年5月19日/前出票\n"
			+ "退票：收取手续费2000台币。\n"
			+ "\n"
			+ "在2014年5月20日/后出票\n"
			+ "退票：收取手续费3000台币。\n"
			+ "\n"
			+ "3区始发-\n"
			+ "在2014年5月19日/前出票\n"
			+ "退票：收取手续费75.00台币。\n"
			+ "\n"
			+ "3区始发-\n"
			+ "在2014年5月20日/后出票\n"
			+ "退票：收取手续费100.00台币。\n";
		cp16 = new Cpecker16();
		cp16.parse(str1, str2,str3);
		System.out.println("15\n"+cp16.getPenalties());
		System.out.println(RuleTranslator16.translate(cp16.getPenalties()));
		
		// 16
		str1 = "香港始发-\n允许。\n\n3区始发-\n允许。";
		str2 = "香港始发-\n改期：允许。\n换开：允许，收取手续费400港币。\n\n3区始发-\n\n3区始发-\n改期：允许。\n换开：允许，收取手续费50.00美元。\n";
		str3 = "香港始发-\n退票：允许。\n\n3区始发-\n退票：允许。\n";
		cp16 = new Cpecker16();
		cp16.parse(str1, str2,str3);
		System.out.println("16\n"+cp16.getPenalties());
		System.out.println(RuleTranslator16.translate(cp16.getPenalties()));
		
		
		// 17
		str1 = "商务舱，允许，每个航段收取手续费1000港币。\n经济舱，允许，每个航段收取手续费500港币。";
		str2 = "换开允许，收取手续费200港币。\n重新订座允许。\n路线更该允许。\n\n";
		str3 = "无限制。";
		cp16 = new Cpecker16();
		cp16.parse(str1, str2,str3);
		System.out.println("17\n"+cp16.getPenalties());
		System.out.println(RuleTranslator16.translate(cp16.getPenalties()));
		
		// 18
		str1 = "" //"全部未使用\n"
				+ "对于C舱，免费退票。\n"
				+ "对于D/Y/B/M/H舱，收取手续费200人民币或等值货币。\n"
				+ "对于K/L/Q舱，收取手续费300人民币或等值货币。\n"
				+ "部分使用的客票，扣除退票费。\n"
				+ "仅SPA航段已经使用，不允许退票。\n"
				+ "SPA航段由国外承运人承运，不允许退票。\n";
		str2 = "自愿更改航班或日期，在最短停留时间内预订相同舱等免费。\n"
				+ "自愿更改舱等，收取原舱等和新舱等间的差价。\n"
				+ "自愿延长最长停留时间，不允许。\n";
		str3 = "退票：\n起飞前，收取手续费2000人民币。\n起飞后，不允许。";
		cp16 = new Cpecker16();
		cp16.parse(str1, str2,str3);
		System.out.println("18\n"+cp16.getPenalties());
		System.out.println(RuleTranslator16.translate(cp16.getPenalties()));
		
		// 19
		str1 = "允许。\n起飞后退票，用已付总票款扣除已使用航段的票款。";
		str2 = "无限制。";
		str3 = "无限制。";
		cp16 = new Cpecker16();
		cp16.parse(str1, str2,str3);
		System.out.println("19\n"+cp16.getPenalties());
		System.out.println(RuleTranslator16.translate(cp16.getPenalties()));
		
		// 20
		str1 = "a.全部未使用客票，允许，收取手续费1000台币/等值货币。\n"
				+ "b.部分使用客票，允许，扣除已使用相同或更高舱位适用票\n"
				+ "价并收取手续费1000台币/等值货币。\n"
				+ "c.部分使用客票，仅退最后一回程航段，不允许。\n";
		str2 = "更改\n"
				+ "a.在有效期内允许。\n"
				+ "b.低票价更改至高票价，允许，收取票价差额。\n"
				+ "c.高票价更改至低票价，不允许。\n"
				+ "升舱\n"
				+ "a.在hx航空出票后，允许。\n"
				+ "b.客票往返的一半票价，并加上升舱后的基础票价差额。\n"
				+ "延期/路线更改\n"
				+ "延期，不允许。\n"
				+ "路线更改，允许，目的地仅限在一个国家。\n";
		str3 = "无限制。";
		cp16 = new Cpecker16();
		cp16.parse(str1, str2,str3);
		System.out.println("20\n"+cp16.getPenalties());
		System.out.println(RuleTranslator16.translate(cp16.getPenalties()));
		
		// 21
		str1 = "中国始发\n允许，收取手续费200.0人民币。";
		str2 = "出票后改期，日期更改换开：允许，免费。\n"
				+ "路线更改：允许，收取手续费500.0人民币。";
		str3 = "中国始发\n允许，收取手续费200.0人民币。";
		cp16 = new Cpecker16();
		cp16.parse(str1, str2,str3);
		System.out.println("21\n"+cp16.getPenalties());
		System.out.println(RuleTranslator16.translate(cp16.getPenalties()));
		
		// 22
		str1 = "起飞前，允许，收取手续费200人民币/35.00美元。\n"
				+ "起飞后，不允许。";
		str2 = "去程，不允许。\n"
				+ "回程，起飞前，允许，免费。\n"
				+ "起飞后，允许，收取手续费。";
		str3 = "无限制。";
		cp16 = new Cpecker16();
		cp16.parse(str1, str2,str3);
		System.out.println("22\n"+cp16.getPenalties());
		System.out.println(RuleTranslator16.translate(cp16.getPenalties()));
		
		// 23
		str1 = "起飞前，允许，收取手续费300马来西亚元。\n"
				+ "完全未使用客票，允许，收取手续费300马来西亚元。\n"
				+ "部分未使用客票，不允许。\n"
				+ "起飞后，不允许。\n"
				+ "完全未使用客票：允许，收取手续费300马来西亚元。\n"
				+ "部分未使用客票：不允许。";
		str2 = "允许，但是自愿延长最长停留时间是不允许的。";
		str3 = "更改：退票，起飞前，误机情况下，取消/退票收取手续费800人民币，\n"
				+ "即取消/退票费800人民币加上零误机费。\n"
				+ "\n"
				+ "更改，误机情况下，收取手续费800人民币，即重新订座费800人民币\n"
				+ "加上零误机费。";
		cp16 = new Cpecker16();
		cp16.parse(str1, str2,str3);
		System.out.println("23\n"+cp16.getPenalties());
		System.out.println(RuleTranslator16.translate(cp16.getPenalties()));
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
