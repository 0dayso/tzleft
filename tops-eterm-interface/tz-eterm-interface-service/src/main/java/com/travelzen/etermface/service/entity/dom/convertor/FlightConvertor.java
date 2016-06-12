package com.travelzen.etermface.service.entity.dom.convertor;

import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.travelzen.etermface.service.entity.abe.YeeskyAoisAv;
import com.travelzen.etermface.service.entity.dom.DomFlightResponse;
import com.travelzen.etermface.service.entity.dom.lList;

public class FlightConvertor {
	/**
	 * 两小时
	 */
	private static final long TWO_HOURS = 2*60*60*1000L;
	private final static SimpleDateFormat SDF = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");
	
	
	public static String convertToResult(String response){
		XStream xstream = new XStream();
		xstream.processAnnotations(YeeskyAoisAv.class);
		xstream.processAnnotations(DomFlightResponse.class);

		DomFlightResponse domFlightResponse = null;
		try {
			domFlightResponse = (DomFlightResponse) xstream.fromXML(response);
		} catch (Exception e) {
			e.printStackTrace();
			return "<response><code>error</code><content>"+response+"</content></response>";
		}

		lList rep = domFlightResponse.body.lList;

		YeeskyAoisAv ya = new YeeskyAoisAv();

		ya.line = Lists.newArrayList();

		int idx = 0;
		for (com.travelzen.etermface.service.entity.dom.Line d : rep.getLine()) {

			if(inTwoHours(d.getfDate(),d.getfTime())){
				continue;
			}
			
			YeeskyAoisAv.Line a = new YeeskyAoisAv.Line();
			a.aa = nullToEmpty(d.getAa());
			a.fNo = removeCarrierFromFlightNo(d.getfNo());
			a.oa = nullToEmpty(d.getOa());
			a.ot = nullToEmpty(d.getOt());
			a.at = nullToEmpty(d.getOt());
			a.flightTime = flightTime(d.getfTime(),d.getaTime());
			a.fDate = nullToEmpty(d.getfDate());
			a.fTime = nullToEmpty(d.getfTime());
			a.aTime = nullToEmpty(d.getaTime());
			a.aDate = nullToEmpty(d.getaDate());
			a.craft = nullToEmpty(d.getCraft());
			a.apt = nullToEmpty(d.getApt());
			a.aot = nullToEmpty(d.getAot());
			a.meal = nullToEmpty(d.getMeal());
			a.Mileage = nullToEmpty(d.getMileage());
			a.stop = nullToEmpty(d.getStop());
			a.eticket = formatEticket(d.getEticket());
			a.planeMode = nullToEmpty(transPlaneMode(d.getPlaneMode()));
			a.company = nullToEmpty(d.getCompany());
			a.ShareCarrier ="";
			a.ShareFlight ="";

			a.FlightID = formatNumber(idx);
			a.ID = String.valueOf(idx);
			a.ElementNo = String.valueOf(idx+1);

			ya.line.add(a);

			YeeskyAoisAv.Plist yplist = new YeeskyAoisAv.Plist();
			yplist.position = Lists.newArrayList();

			com.travelzen.etermface.service.entity.dom.Plist plist = d.getPlist();

			for (com.travelzen.etermface.service.entity.dom.Position postion : plist.getPosition()) {

				YeeskyAoisAv.Plist.Position ypostion = new YeeskyAoisAv.Plist.Position();
				ypostion.classCode = nullToEmpty(postion.getClassCode());
				ypostion.last = nullToEmpty(postion.getLast());
				ypostion.sysPrice = nullToEmpty(postion.getSysPrice());
				ypostion.ext = "";

				yplist.position.add(ypostion);

				if (StringUtils.equalsIgnoreCase(ypostion.classCode, "Y")) {
					a.YClassPrice = ypostion.sysPrice;
				}

			}
			
			a.YClassPrice = nullToEmpty(a.YClassPrice);
			a.plist = yplist;

			idx++;
		}

		StringWriter writer = new StringWriter();
		xstream.marshal(ya, new PrettyPrintWriter(writer, new NoNameCoder()));

		System.out.println(writer.toString());

		return writer.toString();
	}
	
	static Map<String, String> planeModeMap = Maps.newConcurrentMap();

	static {
		planeModeMap.put("窄体", "N");
		planeModeMap.put("宽体", "S");
	}

	private static String transPlaneMode(String s) {
		return planeModeMap.get(s);
	}

	/**
	 * 将null值转化为空字符串
	 * 
	 * @param str
	 * @return
	 */
	private static String nullToEmpty(String str) {
		if (null == str) {
			return "";
		}
		return str;
	}

	/**
	 * 将一位数字转化为两位数字
	 * example: 1  --> 01
	 * @param num
	 * @return
	 */
	private static String formatNumber(int num) {
		String number = String.valueOf(num);
		if (number.length() == 1) {
			number = "0" + number;
		}
		return number;
	}
	
	/**
	 * 飞行时长
	 * @param fromTime
	 * @param arriveTime
	 * @return
	 */
	private static String flightTime(String fromTime,String arriveTime){
		if( null == fromTime ||  null == arriveTime){
			return "";
		}
		
		String time_regex = "\\d{1,2}:\\d{1,2}";
		
		if( !fromTime.matches(time_regex)|| !arriveTime.matches(time_regex)){
			return "";
		}
		
		
		String[] fromTimes = fromTime.split(":");
		String[] arriveTimes = arriveTime.split(":");
		
		int borrow_hour = 0;
		int minutes = Integer.valueOf(arriveTimes[1]) - Integer.valueOf(fromTimes[1]);
		if(minutes < 0){
			minutes += 60;
			borrow_hour = 1;
		}
		
		int hours = Integer.valueOf(arriveTimes[0]) -Integer.valueOf(fromTimes[0]) - borrow_hour;
		if(hours < 0){
			hours += 24;
		}
		
		return hours+":"+minutes;
	}
	
	/**
	 * 从航班号中去掉航司代码
	 * @param flightNo
	 * @return
	 */
	private static String removeCarrierFromFlightNo(String flightNo){
		if(null == flightNo ){
			return "";
		}
		if(flightNo.length() < 2){
			return flightNo;
		}
		return flightNo.substring(2);		
	}
	
	/**
	 * 如果eticket字段为true时，则转化为E;其他情况转化为""
	 * @param eticket
	 * @return
	 */
	private static String formatEticket(String eticket){
		if("true".equalsIgnoreCase(eticket)){
			return "E";
		}else{
			return "";
		}
	}
	
	/**
	 * 两小时内
	 * @param pDate
	 * @param pTime
	 * @return
	 */
	private static boolean inTwoHours(String pDate,String pTime){
		Date lvTime = null;
		try {
			lvTime = SDF.parse(pDate+" "+pTime);
		} catch (ParseException e) {
			e.printStackTrace();
			return true;
		}
		
		if(lvTime.getTime() - System.currentTimeMillis() < TWO_HOURS ){
			return true;
		}else{
			return false;
		}
	}
	
	public static void main(String[] args){
		String date = "2013-8-20";
		String time = "18:00";
		System.out.println(inTwoHours(date,time));
	}

}
