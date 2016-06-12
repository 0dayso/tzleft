package com.travelzen.etermface.service.util;


public class PnrNumerUtil {

	public static long up10ceil(double value) {

		double div10 = value / 10;
		double upv = Math.ceil(div10);

		upv *= 10;
		return (long) upv;

	}

	public static void main(String[] args) {
		// System.out.print( getTimeLimitFromADTK("SHA03MAY13/2135"));

		// System.out.println(beforeDate(now(), 1));
		// 12NOV12

		// System.out.println(ibeDateStrToDateStr("12APR12"));
		// System.out.println(dateStrToIbeDateStr("2012-10-12"));
		// System.out.println(afterDate("31DEC12", 1));
		// System.out.println(dateStrToIbeMonthDateStr("2012-12-14"));
		// System.err.println(getDateAdd("2012-11-12", 1));
		// System.out.println(defaultTimeFormat);
	}

}
