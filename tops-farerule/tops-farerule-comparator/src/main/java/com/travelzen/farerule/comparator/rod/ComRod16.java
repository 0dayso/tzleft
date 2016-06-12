package com.travelzen.farerule.comparator.rod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.farerule.Penalties;
import com.travelzen.farerule.comparator.pool.ComPool16;
import com.travelzen.farerule.translator.RuleTranslator16;

public class ComRod16 {

	private static final Logger log = LoggerFactory.getLogger(ComRod16.class);
	
	// Temporarily save the errors.
	public static void saveSizeErrorText(String airCompany, String rawRule, 
			String ibeRule1, String ibeRule2, String ibeRule3,
			Penalties myPenalties, Penalties ibePenalties) {
		String mystr = RuleTranslator16.translate(myPenalties);
		String ibestr = RuleTranslator16.translate(ibePenalties);
		try {
			String str = "~~<S>~~\n"
				+ "Airline: "+airCompany+"\n"+rawRule+"\n"+myPenalties+"\n"+mystr+"\n~~~~~~~~~~~~~\n"
				+ibeRule1+"\n~~~\n"+ibeRule2+"\n~~~\n"+ibeRule3+"\n~~~\n"+ibePenalties+"\n"+ibestr+"\n"
				+ "Error origin number! ";
			if (myPenalties == null || myPenalties.getPenaltiesItemList() == null)
				str += "null" + ":";
			else
				str += myPenalties.getPenaltiesItemList().size() + ":";
			if (ibePenalties == null || ibePenalties.getPenaltiesItemList() == null)
				str += "null" + "\n";
			else
				str += ibePenalties.getPenaltiesItemList().size() + "\n";
			str += "~~<E>~~\n\n";
			ComPool16.originErrorBuilder.append(str);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
		
	// Temporarily save the results.
	public static void saveResults(String airCompany, String rawRule, String ibeRule1, String ibeRule2, String ibeRule3,
			Penalties myPenalties, Penalties ibePenalties, String tag) {					
		try {
			StringBuilder str = new StringBuilder();
			str.append("\nAirline: "+airCompany+"\n");
			str.append("E~~~~~~~~~~~~~~~\n");
			str.append(rawRule);
			str.append("EE~~~~~~~~~~~~~~~\n");
			String mystr = RuleTranslator16.translate(myPenalties);
			str.append(mystr + "\n");
			if (tag == "cancel" || tag == "all")
				str.append("C1~~~~~~\n" + ibeRule1+"\n");
			if (tag == "change" || tag == "all")
				str.append("C2~~~~~~\n" + ibeRule2+"\n");
			if (tag == "noshow" || tag == "all")
				str.append("C3~~~~~~\n" + ibeRule3+"\n");
			str.append("CE~~~~~~\n");
			String ibestr = RuleTranslator16.translate(ibePenalties);
			str.append(ibestr + "\n");
			str.append("~~~~~~~~~~~~~~~\n");
			// Save results to StringBuilders
			if (tag == "cancel")
				ComPool16.cancelBuilder.append(str);
			if (tag == "change")
				ComPool16.changeBuilder.append(str);
			if (tag == "noshow")
				ComPool16.noshowBuilder.append(str);
			if (tag == "all")
				ComPool16.penaltyBuilder.append(str);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}
