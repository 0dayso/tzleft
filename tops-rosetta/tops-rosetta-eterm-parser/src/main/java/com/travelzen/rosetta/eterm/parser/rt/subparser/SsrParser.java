package com.travelzen.rosetta.eterm.parser.rt.subparser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.rosetta.eterm.common.pojo.rt.OriginalText;

public enum SsrParser {

	;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SsrParser.class);
	
	public static OriginalText parse(OriginalText originalText, String text) {
		text = "\n" + text;
		Matcher matcher_foid = getMatcher("FOID", text);
		if (matcher_foid.find())
			originalText.setSsrFoid(matcher_foid.group(1));
		Matcher matcher_fqtv = getMatcher("FQTV", text);
		if (matcher_fqtv.find())
			originalText.setSsrFqtv(matcher_fqtv.group(1));
		Matcher matcher_adtk = getMatcher("ADTK", text);
		if (matcher_adtk.find())
			originalText.setSsrAdtk(matcher_adtk.group(1));
		Matcher matcher_tkne = getMatcher("TKNE", text);
		if (matcher_tkne.find())
			originalText.setSsrTkne(matcher_tkne.group(1));
		Matcher matcher_docs = getMatcher("DOCS", text);
		if (matcher_docs.find())
			originalText.setSsrDocs(matcher_docs.group(1));
		Matcher matcher_inft = getMatcher("INFT", text);
		if (matcher_inft.find())
			originalText.setSsrInft(matcher_inft.group(1));
		Matcher matcher_chld = getMatcher("CHLD", text);
		if (matcher_chld.find())
			originalText.setSsrChld(matcher_chld.group(1));
		return originalText;
	}
	
	private static Matcher getMatcher(String type, String text) {
		Pattern pattern = Pattern.compile(
				"\\n( *[0-9]{1,3}\\.SSR " + type + "[\\w\\W]*?)"
				+ "(?:\\n *[0-9]{1,3}\\.SSR (?!" + type + ")|$)");
		return pattern.matcher(text);
	}
	
	public static void main(String[] args) {
		String s = "18.SSR FOID 1231241412\n"
				+ "19.SSR OTHS 1E UA 835 26JUL BUSINESSFIRST \n"
				+ "OFFERED THIS FLIGHT\n"
				+ "20.SSR GRPS 1E TCP10 FAC\n"
				+ "21.SSR ADTK 1E  KK1 . UA - OR PNR WILL AUTO CANCEL\n"
				+ "27.SSR TKNE UA HK1 SFOIAH 1937 Q19JUL 0169379813747/2/P1\n"
				+ "31.SSR DOCS";
		System.out.println(parse(new OriginalText(), s));
	}

}
