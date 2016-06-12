package com.travelzen.rosetta.eterm.parser.rt.state;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.rosetta.eterm.common.pojo.EtermRtResponse;

public class PnrParserStateHandler {
	
	private static Logger logger = LoggerFactory.getLogger(PnrParserStateHandler.class);
	
	private PnrParserContext context;
	
	public PnrParserStateHandler(String pnrNo, String pnrContent, boolean isDomestic) {
		context = new PnrParserContext();
		context.setState(PnrParserStateMachine.STATUS);
		context.setDomestic(isDomestic);
		context.setPnrLines(preProcessPnrContent(pnrContent));
		context.setNode(0);
		context.setEtermRtResponse(new EtermRtResponse(pnrNo));
	}
	
	private String[] preProcessPnrContent(String pnrContent) {
		pnrContent = pnrContent.toUpperCase(Locale.ENGLISH)
				.replaceAll("\\^.", "")
				.replaceAll(" {5}[\\+\\-]", "")
				.replaceAll("[\r\n]+", "\n");
		List<String> resList = new ArrayList<String>();
		// 对于乘客信息和航班信息之间未换行的文本进行切割
		// 1.胡信 2.凌金秀   HWFW69   3.  MU2732 G   SU26APR  WUXPEK RR10  1050 1250          E
		Pattern pattern1 = Pattern.compile(
				"(.+? [A-Z0-9]{6}) +(\\d+\\. [ \\*][A-Z0-9]{2}[A-Z0-9]+ +[A-Z0-9]+ +[A-Z]{2}\\d{2}[A-Z]{3}.+)");
		// 1.%#$%#$(80个字符)　17.  HO1611 U   TU12JAN  PVGHKT RR16  1755 2340          E T2--
		Pattern pattern2 = Pattern.compile(
				"(.{80}) *(\\d+\\. [ \\*][A-Z0-9]{2}[A-Z0-9]+ +[A-Z0-9]+ +[A-Z]{2}\\d{2}[A-Z]{3}.+)");
		for (String line:pnrContent.split("\\r|\\n")) {
			if (line.length() > 80) {
				Matcher matcher1 = pattern1.matcher(line);
				Matcher matcher2 = pattern2.matcher(line);
				if (matcher1.find()) {
					resList.add(matcher1.group(1).trim());
					resList.add(matcher1.group(2).trim());
				} else if (matcher2.find()) { 
					resList.add(matcher2.group(1).trim());
					resList.add(matcher2.group(2).trim());
				} else {
					resList.add(line.trim());
				}
			} else {
				resList.add(line.trim());
			}
		}
		return resList.toArray(new String[0]);
	}
	
	public EtermRtResponse process() {
		try {
			while(context.getState().process(context));
		} catch (Exception e) {
			logger.error("PNR解析未知异常：" + e.getMessage(), e);
		}
		return context.getEtermRtResponse();
	}
	
}
