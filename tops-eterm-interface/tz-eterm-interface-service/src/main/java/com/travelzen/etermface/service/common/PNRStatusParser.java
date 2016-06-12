package com.travelzen.etermface.service.common;

import org.javatuples.Pair;

import com.travelzen.etermface.service.enums.PNRStatus;

/**
 * @author hongqiang.mao
 * 
 * @date 2013-6-22 下午2:20:48
 * 
 * @description
 */
public class PNRStatusParser {

	/**
	 * 检查pnr状态
	 * 
	 * @param pnrContent
	 * @return
	 */
	public static Pair<PNRStatus, String> checkPNRStatus(String pnrContent) {
		Pair<PNRStatus, String> lvResult = null;
		if (null == pnrContent) {
			lvResult = Pair.with(PNRStatus.UNKOWN_STATUS, "rt内容为空");
			return lvResult;
		}
		pnrContent = pnrContent.replace("\r", "\n").trim();
		String[] lvStrs = pnrContent.split("\n");
		if ("".equals(pnrContent.trim())) {
			lvResult = Pair.with(PNRStatus.UNKOWN_STATUS, "rt内容为空字符");
		} else if (pnrContent.startsWith("NO PNR")) {
			lvResult = Pair.with(PNRStatus.NO_PNR, "pnr不存在");
		} else if (pnrContent.contains("ENTIRELY") && pnrContent.contains("CANCELLED")) {
			lvResult = Pair.with(PNRStatus.ENTIRELY_CANCELLED, "已取消");
		} else if (pnrContent.contains("ELECTRONIC") && pnrContent.contains("TICKET")) {
			lvResult = Pair.with(PNRStatus.ELECTRONIC_TICKET, "已出电子票");
		} else if (pnrContent.startsWith("需要授权")) {
			lvResult = Pair.with(PNRStatus.UNAUTHORIZED, "需要授权");
		} else if (lvStrs.length < 5) {
			lvResult = Pair.with(PNRStatus.UNKOWN_STATUS, "未知错误");
		} else {
			lvResult = Pair.with(PNRStatus.NORNAL, "正常");
		}
		return lvResult;
	}

	public static void main(String[] args) {
		String ssn = "\n";
		String s = "    **ELECTRONIC TICKET PNR**" + ssn + "1.CHEN/WEI MS 2.YUAN/HUILIANG MR HMEMEK" + ssn
				+ "3.  HX707  S   FR27DEC  HKGDPS HK2   1200 1705          E --I" + ssn
				+ "4.  HX706  S   MO30DEC  DPSHKG HK2   1805 2255          E I --" + ssn
				+ "5.SHA/T SHA/T021-62277798/BU YE CHENG BOOKING OFFICE/JINHUA ABCDEFG" + ssn + "6.T" + ssn
				+ "7.SSR ADTK 1E BY SHA11DEC13/1527 OR CXL HX 707 S27DEC" + ssn
				+ "8.SSR TKNE HX HK1 HKGDPS 707 S27DEC 8514388113736/1/P2" + ssn
				+ "9.SSR TKNE HX HK1 HKGDPS 707 S27DEC 8514388113735/1/P1" + ssn
				+ "10.SSR TKNE HX HK1 DPSHKG 706 S30DEC 8514388113735/2/P1" + ssn
				+ "11.SSR TKNE HX HK1 DPSHKG 706 S30DEC 8514388113736/2/P2" + ssn
				+ "12.SSR DOCS HX HK1 P/CN/G46351237/CN/14AUG74/F/27OCT20/CHEN/WEI/P1";

		Pair<PNRStatus, String> pair = checkPNRStatus(s);

		System.out.println(pair);

	}
}
