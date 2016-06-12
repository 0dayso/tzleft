/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */
 
package com.travelzen.farerule.jpecker.pecker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.travelzen.farerule.Penalties;
import com.travelzen.farerule.jpecker.pecker.pecker16.*;

public class Jpecker16 {
	
	public Jpecker16() {
		airCompany = "";
		penalties = new Penalties();
	}
	
	public Jpecker16(String airCompany) {
		this.airCompany = airCompany;
		penalties = new Penalties();
	}

	private String airCompany;
	private Penalties penalties;
	
	public Penalties getPenalties() {
		return penalties;
	}
	
	public void parse(String ruleText) {
		Matcher matcher_no = Pattern.compile(
				"NO PENALTY REQUIREMENTS\\.?\n").matcher(ruleText);
		if (matcher_no.find()) {
			penalties = null;
			return;
		}
		
		ruleText = "\n" + ruleText.replaceAll("\\.0+", "");
		
		switch (airCompany) {
		case "3U":
			Jpecker16_3U _3u = new Jpecker16_3U();
			_3u.process(ruleText);
			penalties = _3u.getPenalties();
			break;
		case "AM":
			Jpecker16_AM am = new Jpecker16_AM();
			am.process(ruleText);
			penalties = am.getPenalties();
			break;
		case "AY":
			Jpecker16_AY ay = new Jpecker16_AY();
			ay.process(ruleText);
			penalties = ay.getPenalties();
			break;
		case "BA":
			Jpecker16_BA ba = new Jpecker16_BA();
			ba.process(ruleText);
			penalties = ba.getPenalties();
			break;
		case "CA":
			Jpecker16_CA ca = new Jpecker16_CA();
			ca.process(ruleText);
			penalties = ca.getPenalties();
			break;
		case "CZ":
			Jpecker16_CZ cz = new Jpecker16_CZ();
			cz.process(ruleText);
			penalties = cz.getPenalties();
			break;
		case "EK":
			Jpecker16_EK ek = new Jpecker16_EK();
			ek.process(ruleText);
			penalties = ek.getPenalties();
			break;
		case "ET":
			Jpecker16_ET et = new Jpecker16_ET();
			et.process(ruleText);
			penalties = et.getPenalties();
			break;
		case "GA":
			Jpecker16_GA ga = new Jpecker16_GA();
			ga.process(ruleText);
			penalties = ga.getPenalties();
			break;
		case "GE":
			Jpecker16_GE ge = new Jpecker16_GE();
			ge.process(ruleText);
			penalties = ge.getPenalties();
			break;
		case "MF":
			Jpecker16_MF mf = new Jpecker16_MF();
			mf.process(ruleText);
			penalties = mf.getPenalties();
			break;
		case "OZ":
			Jpecker16_OZ oz = new Jpecker16_OZ();
			oz.process(ruleText);
			penalties = oz.getPenalties();
			break;
		case "QR":
			Jpecker16_QR qr = new Jpecker16_QR();
			qr.process(ruleText);
			penalties = qr.getPenalties();
			break;
		case "SC":
			Jpecker16_SC sc = new Jpecker16_SC();
			sc.process(ruleText);
			penalties = sc.getPenalties();
			break;
		case "SU":
			Jpecker16_SU su = new Jpecker16_SU();
			su.process(ruleText);
			penalties = su.getPenalties();
			break;
		case "TG":
			Jpecker16_TG tg = new Jpecker16_TG();
			tg.process(ruleText);
			penalties = tg.getPenalties();
			break;
		case "U6":
			Jpecker16_U6 u6 = new Jpecker16_U6();
			u6.process(ruleText);
			penalties = u6.getPenalties();
			break;
		case "UN":
			Jpecker16_UN un = new Jpecker16_UN();
			un.process(ruleText);
			penalties = un.getPenalties();
			break;
		case "UO":
			Jpecker16_UO uo = new Jpecker16_UO();
			uo.process(ruleText);
			penalties = uo.getPenalties();
			break;
		default:
			Jpecker16__General general = new Jpecker16__General();
			general.process(airCompany, ruleText);
			penalties = general.getPenalties();
		}
	}

}
