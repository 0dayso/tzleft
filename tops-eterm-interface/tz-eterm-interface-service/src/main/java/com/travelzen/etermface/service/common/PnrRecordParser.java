package com.travelzen.etermface.service.common;

import org.apache.commons.lang3.StringUtils;

public class PnrRecordParser {
	private final static String LEGAL_PNR = "[A-Z0-9]{6}";

	private final static String SegmentRegex = "\\*?[A-Za-z0-9]{2}\\s+[A-Za-z0-9]{2,5}\\s+[A-Za-z0-9]{1,2}\\s+[A-Za-z0-9]{7}\\s+[A-Za-z]{6}\\s+[A-Za-z]{2}[0-9]{1,2}\\s+[0-9]{4}\\s+[0-9]{4}";

	/**
	 * 生成pnr成功标记
	 */
	private final static String EOT_SUCCESS = "-EOT SUCCESSFUL";
	
	private final static String TICKET_LIMIT = "出票时限";
	
	public static String getPNRFromSSReturn(String ssRtstr) {
		String[] strs = ssRtstr.replaceAll("\r", "\n").split("\n");
		String pnr = null;
		for (String str : strs) {
			str = str.trim();
			if (str.matches(SegmentRegex)) {
				continue;
			}

			if (str.length() == 6) {
				pnr = str;
			} else {
				int index = str.indexOf(EOT_SUCCESS);
				if (index != -1) {
					pnr = str.substring(0, index).trim();
					if (pnr.length() != 6) {
						pnr = null;
					}
				} else if (str.contains(TICKET_LIMIT)) {
					String[] ss = str.split("\\s+");
					if (null != ss && ss.length > 0) {
						pnr = ss[0];
					}
				}
			}

			if (validate(pnr)) {
				break;
			} else {
				pnr = null;
			}
		}

		return pnr;
	}

	/**
	 * PNR合法性校验(字母或数字)
	 * 
	 * @param pnr
	 * @return
	 */
	private static boolean validate(String pnr) {
		if (StringUtils.isBlank(pnr)) {
			return false;
		}

		if (pnr.matches(LEGAL_PNR)) {
			return true;
		} else {
			return false;
		}
	}
}
