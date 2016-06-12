package com.travelzen.etermface.service.util;

import java.io.StringWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.travelzen.etermface.service.entity.PnrRet;
import com.travelzen.etermface.service.enums.PNRStatus;
import com.travelzen.framework.core.common.ReturnClass;
import com.travelzen.framework.core.exception.BizException;

public class PnrParseUtil {
	
	public static Pattern SPACE_PATTERN = Pattern.compile("\\s+");
	
	public static Pattern PAT_HEAD_PATTERN = Pattern.compile("^(\\d{2}) (.*)");
	
	private static Logger logger = LoggerFactory.getLogger(PnrParseUtil.class);
	
	
	public static  	  Pair<String, String> opfail(XStream xstream, String errorMsg) {

		PnrRet pnrRet = new PnrRet();
		
		StringWriter writer = new StringWriter();
		xstream.marshal(pnrRet, new PrettyPrintWriter(writer, new NoNameCoder()));

		String xml = writer.toString();

		return Pair.with(xml, "");
	}
	
	public static  Pair<Boolean, String> checkQuickFail(String rt) {
		boolean quickFail = false;
		String errorMsg = "";

		if (StringUtils.equalsIgnoreCase(rt.trim(), "NO PNR")) {
			quickFail = true;
			errorMsg = "NO PNR";
		}

		if (rt.trim().startsWith("需要授权")) {
			quickFail = true;
			errorMsg = PNRStatus.UNAUTHORIZED.toString();
		}

		if (rt.trim().startsWith("*THIS PNR WAS ENTIRELY CANCELLED*")) {
			quickFail = true;
			errorMsg = PNRStatus.ENTIRELY_CANCELLED.toString();
		}

		return Pair.with(quickFail, errorMsg);
	}
	
	// (PAT:A
		// 01 RT/R+RT/T FARE:CNY1240.00 TAX:CNY100.00 YQ:CNY220.00 TOTAL:1560.00
		// SFC:01
		// 02 R+T FARE:CNY1380.00 TAX:CNY100.00 YQ:CNY220.00 TOTAL:1700.00
		// SFC:02

	static public ReturnClass<List<PnrRet.PatResult>> parsePAT(String rt) throws BizException {

			List<PnrRet.PatResult> patResultList = Lists.newArrayList();

			String[] lines = StringUtils.trimToEmpty(rt).split("\n+");

			for (String line : lines) {
				if (StringUtils.isNotBlank(line)) {
					Matcher m = PAT_HEAD_PATTERN.matcher(line);
					if (m.find()) {

						PnrRet.PatResult patResult = new PnrRet.PatResult();

						String flgSegIdx = m.group(1);
						String patLineStr = m.group(2);

						patResult.patLineStr = patLineStr;
						patResult.sfc = flgSegIdx;

						String[] items = SPACE_PATTERN.split(patLineStr.trim());
						if (items.length >= 5) {
							String fareBasis = items[0];

							patResult.fareBasis = fareBasis;

							for (int i = 1; i < items.length; i++) {
								String item = items[i];
								int idx = item.indexOf(":");

								if (idx > 0) {
									String left = item.substring(0, idx);
									String right = item.substring(idx + 1);

									switch (left) {
									case "FARE":
										patResult.fare = right;
										break;

									case "TAX":
										patResult.tax = right;
										break;

									case "YQ":
										patResult.yq = right;
										break;

									case "TOTAL":
										patResult.total = right;
										break;

									}

								} else {
									logger.warn("invlid fare format:{}", items[i]);
								}
							}

						} else {
							logger.warn("invlid pat format:{}", line);
						}

						patResultList.add(patResult);
					}
				}
			}
			ReturnClass<List<PnrRet.PatResult>> ret = new ReturnClass<>();
			ret.setObject(patResultList);
			return ret;
		}

}
