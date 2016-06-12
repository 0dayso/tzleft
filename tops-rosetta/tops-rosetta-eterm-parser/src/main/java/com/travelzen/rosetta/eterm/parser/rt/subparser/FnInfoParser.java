package com.travelzen.rosetta.eterm.parser.rt.subparser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.rosetta.eterm.common.pojo.enums.PassengerType;
import com.travelzen.rosetta.eterm.common.pojo.rt.FnInfo;
import com.travelzen.rosetta.eterm.common.pojo.rt.PassengerInfo;
import com.travelzen.rosetta.eterm.common.pojo.rt.FnInfo.FnFare;
import com.travelzen.rosetta.eterm.common.pojo.rt.FnInfo.FnFareItem;
import com.travelzen.rosetta.eterm.common.pojo.rt.PassengerInfo.Passenger;

/**
 * FN价格信息解析
 * @author yiming.yan
 */
public enum FnInfoParser {
	
	;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FnInfoParser.class);
	
	public static FnInfo parse(String text, boolean isDomestic, PassengerInfo passengerInfo) {
		if (text.length() == 0)
			return null;
		FnInfo fnInfo = null;
		Matcher matcher_fp = Pattern.compile(
				"(?:\n|^)\\d+\\.FP(?:/IN)?/([A-Z]+),([A-Z]{3})").matcher(text);
		if (matcher_fp.find()) {
			fnInfo = new FnInfo();
			fnInfo.setMethod(matcher_fp.group(1));
			fnInfo.setCurrency(matcher_fp.group(2));
		} else {
			LOGGER.info("PNR解析：FN价格FP信息解析失败！解析文本：" + text);
			return null;
		}
		List<FnFare> fares = new ArrayList<FnFare>();
		Matcher matcher_fn = Pattern.compile(
				"(?:\n|^)\\d+\\.FN/([\\w\\W]+?)(?=\n\\d+\\.)").matcher(text);
		while (matcher_fn.find()) {
			FnFare fare = parseFN(
					matcher_fn.group(1), fnInfo.getCurrency(), isDomestic, passengerInfo);
			fares.add(fare);
		} 
		if (fares.size() == 0) {
			LOGGER.info("PNR解析：FN价格FN信息解析失败！解析文本：" + text);
			return null;
		}
		fnInfo.setFares(fares);
		return fnInfo;
	}
	
	private static FnFare parseFN(String text, String currency, boolean isDomestic, 
			PassengerInfo passengerInfo) {
		FnFare fare = new FnFare();
		if (text.startsWith("A/IN") || text.startsWith("IN"))
			fare.setPsgType(PassengerType.INF);
		List<FnFareItem> fareItems = new ArrayList<FnFareItem>();
		FnFareItem fareItem = new FnFareItem();
		Matcher matcher_f = Pattern.compile(
				"F" + "([A-Z]{3}" + "\\d+\\.\\d{2})").matcher(text);
		Matcher matcher_s = Pattern.compile(
				"S" + currency + "(\\d+\\.\\d{2})").matcher(text);
		Matcher matcher_x = Pattern.compile(
				"X" + currency + "(\\d+\\.\\d{2})").matcher(text);
		Matcher matcher_a = Pattern.compile(
				"A" + currency + "(\\d+\\.\\d{2})").matcher(text);
		Matcher matcher_c = Pattern.compile(
				"C" + "(\\d+\\.\\d{2})").matcher(text);
		if (matcher_f.find())
			fareItem.setFaceFare(matcher_f.group(1));
		if (matcher_s.find())
			fareItem.setCurrentFare(matcher_s.group(1));
		if (matcher_x.find())
			fareItem.setTax(matcher_x.group(1));
		if (matcher_a.find())
			fareItem.setTotalFare(matcher_a.group(1));
		if (matcher_c.find())
			fareItem.setCommission(matcher_c.group(1));
		if (isDomestic) {
			Matcher matcher_cn = Pattern.compile(
					"T" + currency + "(\\d+\\.\\d{2})CN").matcher(text);
			Matcher matcher_yq = Pattern.compile(
					"T" + currency + "(\\d+\\.\\d{2})YQ").matcher(text);
			if (matcher_cn.find())
				fareItem.setCnTax(matcher_cn.group(1));
			if (matcher_yq.find())
				fareItem.setYqTax(matcher_yq.group(1));
		}
		if (passengerInfo != null && passengerInfo.getPassengers() != null) {
			Matcher matcher_psgno = Pattern.compile(
					"/P(\\d+)(?:/\\d)? *(?:\n|$)").matcher(text);
			if (matcher_psgno.find()) {
				int psgNo = Integer.parseInt(matcher_psgno.group(1));
				if (passengerInfo.getPassengers().size() >= psgNo && 
						passengerInfo.getPassengers().get(psgNo-1).getPsgType() == PassengerType.CHD) {
					fare.setPsgType(PassengerType.CHD);
				}
			}
		}
		fareItem.setFnItemText(text);
		if (null != fareItem.getCurrentFare())
			fareItems.add(fareItem);
		fare.setFareItems(fareItems);
		fare.setFnText(text);
		return fare;
	}
	
	public static void main(String[] args) {
		String text0 = "";
		String text1 = 
				"15.FN/A/\n"
				+ "16.TN/781-6750102156/P1 \n"
				+ "17.FP/CASH,CNY  ";
		String text2 = 
				"29.FN/A/FCNY1940.00/SCNY1940.00/C2.00/XCNY160.00/TCNY100.00CN/TCNY60.00YQ/  \n"
				+ "    ACNY2100.00 \n"
				+ "30.TN/999-6750105103/P1 \n"
				+ "31.TN/999-6750105104/P2 \n"
				+ "32.TN/999-6750105105/P3 \n"
				+ "33.FP/CASH,CNY  \n";
		String text3 = 
				"20.FN/A/FUSD450.00/ECNY2800.00/SCNY2800.00/C0.00/XCNY1286.00/TCNY35.00AY/   \n"
				+ "	    TCNY110.00US/TCNY1141.00XT/ACNY4086.00    \n"
				+ "21.TN/016-8911283312/P1                                                        \n"
				+ "22.FP/CASH,CNY  ";
		String text4 = 
				"26.FN/FCNY8700.00/SCNY2000.00/C5.00/XCNY1179.00/TCNY90.00CN/TCNY139.00TS/      \n"
				+ "    TCNY950.00YQ/ACNY9879.00\n"
				+ "27.FN/FCNY6530.00/SCNY2000.00/C5.00/XCNY1089.00/TEXEMPTCN/TCNY139.00TS/ \n"
				+ "    TCNY950.00YQ/ACNY7619.00/P2 \n"
				+ "28.TN/018-8911854074/P1 \n"
				+ "29.TN/018-8911854075/P2 \n"
				+ "30.TN/018-8911854076/P3 \n"
				+ "31.TN/018-8911854077/P4 \n"
				+ "32.FP/CASH,CNY  ";
		String text5 = 
				"17.FN/A/IT//SCNY1010.00/C0.00/XCNY1041.00/TCNY90.00CN/TCNY139.00BP/TCNY812.00YQ 18.TN/988-8911854091/P1 \n"
				+ "19.FP/CASH,CNY  ";
		String text6 = 
				"22.FN/A/IN/FEUR79.00/ECNY580.00/SCNY580.00/C0.00/ACNY580.00                    \n"
				+ "23.FN/A/FEUR790.00/ECNY5730.00/SCNY5730.00/C0.00/XCNY1592.00/TCNY93.00FR/      \n"
				+ "    TCNY32.00FR/TCNY1467.00XT/ACNY7322.00   \n"
				+ "24.TN/117-8911853655/P1 \n"
				+ "25.TN/IN/117-8911853656/P1  \n"
				+ "26.FP/CASH,CNY  ";
		String text7 = 
				"18.FN/A/IT//SCNY3450.00/C0.00/XCNY1413.00/TCNY90.00CN/TCNY38.00OO/TCNY1285.00XT 19.TN/618-8911853666/P1 \n"
				+ "20.FP/CASH,CNY/AGT08307736 QS-- ";
		String text8 = 
				"18.FN/IT//SCNY1170.00/C0.00/XCNY1071.00/TCNY90.00CN/TCNY169.00BP/TCNY812.00YQ   19.TN/988-8911853719/P1 \n"
				+ "20.FP/CASH,CNY  ";
		String text9 = 
				"22.FN/A/IN/FEUR79.00/ECNY580.00/SCNY580.00/C0.00/ACNY580.00                    \n"
				+ "23.FN/A/FEUR790.00/ECNY5730.00/SCNY5730.00/C0.00/XCNY1592.00/TCNY93.00FR/      \n"
				+ "    TCNY32.00FR/TCNY1467.00XT/ACNY7322.00   \n"
				+ "24.TN/117-8911853655/P1 \n"
				+ "25.TN/IN/117-8911853656/P1  \n"
				+ "26.FP/CASH,CNY  ";
		String text10 = 
				"19.FN/FCNY3060.00/SCNY1630.00/C0.00/XCNY358.00/TCNY90.00CN/TCNY268.00YQ/\n"
				+ "    ACNY3418.00 \n"
				+ "20.TN/170-8911687212/P1 \n"
				+ "21.TN/170-8911687213/P2 \n"
				+ "22.FP/CHECK,CNY  ";
		String text11 = 
				"26.FN/A/IT//SCNY4050.00/C0.00/XCNY1413.00/TCNY90.00CN/TCNY38.00OO/TCNY1285.00XT\n"
				+ "27.FN/A/IT//SCNY3190.00/C0.00/XCNY1323.00/TEXEMPTCN/TCNY38.00OO/TCNY1285.00XT       /P2   \n"
				+ "28.TN/618-8911853586/P1 \n"
				+ "29.TN/618-8911853587/P2 \n"
				+ "30.TN/618-8911853588/P3 \n"
				+ "31.FP/CASH,CNY/AGT08307736 QS-- ";
		// 换开
		String text12 = 
				"13.FN/RCNY9010.00/SCNY0.00/C0.00/XCNY750.00/TCNY750.00XP/OCNY80.00IQ/   \n"
				+ "    OCNY2046.00XT/ACNY750.00\n"
				+ "14.TN/607-8911853589/P1 \n"
				+ "15.FP/CASH,CNY  ";
		String text13 = 
				"26.FN/RMYR1703/ECNY2940.00/SCNY700.00/C0.00/XCNY180.00/TCNY180.00OD/\n"
				+ "    OCNY48.00MY/OCNY58.00YR/ACNY880.00  \n"
				+ "27.TN/232-8912624279/P1 \n"
				+ "28.FP/CASH,CNY  ";
		// 多个价格
		String text14 = 
				"39.FN/A/FCNY1500.00/SCNY1500.00/C3.00/XCNY753.00/TCNY90.00CN/TCNY53.00SW/      \n"
				+ "    TCNY610.00YQ/ACNY2253.00\n"
				+ "40.FN/A/FCNY1500.00/SCNY1500.00/C3.00/XCNY637.00/TEXEMPTCN/TCNY27.00SW/ \n"
				+ "    TCNY610.00YQ/ACNY2137.00/P1 \n"
				+ "41.FN/IN/FCNY1100.00/SCNY1100.00/C0.00/TEXEMPTCN/ACNY1100.00/P2/3   \n"
				+ "42.TN/018-8913670051/P1 \n"
				+ "43.TN/018-8913670052/P2 \n"
				+ "44.TN/IN/018-8913670053/P2  \n"
				+ "45.TN/018-8913670054/P3 \n"
				+ "46.TN/IN/018-8913670055/P3  \n"
				+ "47.TN/018-8913670056/P4 \n"
				+ "48.FP/IN/CASH,CNY/P2/3  ";
	
		System.out.println(parse(text0, true, null));
		System.out.println(parse(text1, true, null));
		System.out.println(parse(text2, true, null));
		System.out.println(parse(text3, false, null));
		System.out.println(parse(text4, false, null));
		System.out.println(parse(text5, false, null));
		System.out.println(parse(text6, false, null));
		System.out.println(parse(text7, false, null));
		System.out.println(parse(text8, false, null));
		System.out.println(parse(text9, false, null));
		System.out.println(parse(text10, false, null));
		System.out.println(parse(text11, false, null));
		System.out.println(parse(text12, false, null));
		System.out.println(parse(text13, false, null));
		PassengerInfo passengerInfo = new PassengerInfo();
		List<Passenger> passengers = new ArrayList<Passenger>();
		Passenger passenger1 = new Passenger();
		Passenger passenger2 = new Passenger();
		passenger1.setPsgType(PassengerType.CHD);
		passengers.add(passenger1);
		passengers.add(passenger2);
		passengerInfo.setPassengers(passengers);
		System.out.println(parse(text14, false, passengerInfo));
	}
	
}
