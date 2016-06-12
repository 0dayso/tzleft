package com.travelzen.farerule.jpecker.consts;

public class PassengerConst {

	private final static String SEAT = "((?:WITH(?:OUT|OUTH)?)\\s*A\\s*SEAT|NOT\\s*OCCUPYING\\s*A\\s*SEAT)?";
	
	public final static String STU1 = "STUDENT(?:\\.|\\s*WITH)";
	public final static String STU2 = "STUDENT(?:\\s*AGD|\\s*AGED)?\\s*(\\d+)\\s*OR\\s*OLDER";
	public final static String STU3 = "STUDENT(?:\\s*AGD|\\s*AGED)?\\s*(\\d+)\\s*-\\s*(\\d+)(?:\\s*YEARS)?(?:\\s*OF\\s*AGE|\\s*OLD)?";
	
	public final static String YOU = "YOUTH\\s*CONFIRMED?(?:\\s*AGD|\\s*AGED)?\\s*(\\d+)\\s*-\\s*(\\d+)(?:\\s*YEARS)?(?:\\s*OF\\s*AGE|\\s*OLD)?";	
	public final static String SEA = "SEAMAN";
	public final static String LAB = "(LABORER|WORKER)";
	public final static String SCT = "SENIOR\\s*CITIZEN(?:\\s*AGD|\\s*AGED)?\\s*(\\d+)\\s*OR\\s*OLDER";
	
	public final static String INF1 = "((?:UN)?ACCOMPANIED\\s*)?INFANT(?:S)?" + "\\s*" + SEAT;
	public final static String INF2 = "((?:UN)?ACCOMPANIED\\s*)?INFANT(?:S)?(?:\\s*AGD|\\s*AGED)?\\s*UNDER\\s*(\\d+)(?:\\s*YEARS)?(?:\\s*OF\\s*AGE|\\s*OLD)?" + "\\s*" + SEAT;
	public final static String INF3 = "((?:UN)?ACCOMPANIED\\s*)?INFANT(?:S)?(?:\\s*AGD|\\s*AGED)?\\s*(\\d+)\\s*-\\s*(\\d+)(?:\\s*YEARS)?(?:\\s*OF\\s*AGE|\\s*OLD)?" + "\\s*" + SEAT;
	
	public final static String CHD1 = "((?:UN)?ACCOMPANIED\\s*)?CHILD(?:REN)?(\\s*WITHOUT\\s*ADULT\\s*ACCOMPANY)?";
	public final static String CHD2 = "((?:UN)?ACCOMPANIED\\s*)?CHILD(?:REN)?(?:\\s*AGD|\\s*AGED)?\\s*UNDER\\s*(\\d+)(?:\\s*YEARS)?(?:\\s*OF\\s*AGE|\\s*OLD)?";
	public final static String CHD3 = "((?:UN)?ACCOMPANIED\\s*)?CHILD(?:REN)?(?:\\s*AGD|\\s*AGED)?\\s*(\\d+)\\s*-\\s*(\\d+)(?:\\s*YEARS)?(?:\\s*OF\\s*AGE|\\s*OLD)?";
	
	public final static String DISCOUNT = "(CHARGE\\s*(\\d+)|NO\\s*DISCOUNT|(?:TRAVEL\\s*)?(?:IS\\s*)?NOT\\s*PERMITTED)";

}
