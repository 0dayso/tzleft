package com.travelzen.etermface.service.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.common.ufis.util.UfisException;
import com.travelzen.etermface.common.pojo.eterm3in1.UfisDtResponse;
import com.travelzen.etermface.common.pojo.eterm3in1.UfisPidResponse;
import com.travelzen.etermface.common.pojo.eterm3in1.UfisVbiResponse;
import com.travelzen.etermface.common.pojo.eterm3in1.UfisViResponse;
import com.travelzen.etermface.common.pojo.eterm3in1.UfisDtResponse.Fare;
import com.travelzen.etermface.common.pojo.eterm3in1.UfisDtResponse.FlightSegment;
import com.travelzen.etermface.common.pojo.eterm3in1.UfisDtResponse.Passenger;
import com.travelzen.etermface.common.pojo.eterm3in1.UfisDtResponse.Ticket;
import com.travelzen.etermface.common.pojo.eterm3in1.UfisDtResponse.Fare.FareDetail;
import com.travelzen.etermface.common.pojo.eterm3in1.UfisDtResponse.FlightSegment.FlightType;
import com.travelzen.etermface.common.pojo.eterm3in1.UfisDtResponse.FlightSegment.Leg;
import com.travelzen.etermface.common.pojo.eterm3in1.UfisDtResponse.Passenger.IdCard;
import com.travelzen.etermface.service.EtermUfisClient;
import com.travelzen.etermface.service.entity.Eterm3in1DtResponse;
import com.travelzen.etermface.service.entity.Eterm3in1DtResponse.Segments.Segment;
import com.travelzen.etermface.service.util.AirportUtil;
import com.travelzen.etermface.service.util.DateTimeUtil;
import com.travelzen.etermface.service.util.XmlUtil;
import com.travelzen.framework.core.json.JsonUtil;

/**
 * Eterm3in1PrintHandler
 * <p>
 * @author yiming.yan
 * @Date Dec 8, 2015
 */
public class Eterm3in1PrintHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Eterm3in1PrintHandler.class);
	
	public static String handlePid(String ticketNo, String iteneraryNo, String officeId) throws IOException {
		String text = null;
		EtermUfisClient client = null;
		try {
			client = new EtermUfisClient(officeId, "API_DOMESTIC_ITINERARY");
			text = client.execCmd("pid " + ticketNo + " " + iteneraryNo);
		} catch (UfisException e) {
			LOGGER.error("UfisException: {}", e.getMessage());
			return JsonUtil.toJson(new UfisPidResponse(false, "Ufis 服务器异常！"), false);
		} finally {
			if (null != client)
				client.close();
		}
		LOGGER.info("Ufis pid 指令返回：　{}", text);
		if (null == text) {
			LOGGER.info("Ufis pid 指令返回结果为空！");
			return JsonUtil.toJson(new UfisPidResponse(false, "Ufis pid 指令返回结果为空！"), false);
		}
		if (text.startsWith("UFIS-ERROR")) {
			LOGGER.info("Ufis Error: " + text);
			return JsonUtil.toJson(new UfisPidResponse(false, text), false);
		} else if (text.contains("组帐号错误或没有可用PID")) {
			LOGGER.info("Eterm Error: " + text);
			return JsonUtil.toJson(new UfisPidResponse(false, text), false);
		}
		UfisPidResponse ufisResponse = null;
		try {
			ufisResponse = convPidResponse(text);
		} catch (Exception e) {
			LOGGER.info("UfisPidResponse 转化异常！");
			ufisResponse = new UfisPidResponse(false, "UfisPidResponse 转化异常！");
		}
		LOGGER.info("Ufis pid 解析结果：　{}", ufisResponse);
		String result = JsonUtil.toJson(ufisResponse, false);
		return result;
	}
	
	public static String handlePii(String ticketNo, String iteneraryNo, String ticketPrice, String officeId) throws IOException {
		String text = null;
		EtermUfisClient client = null;
		try {
			client = new EtermUfisClient(officeId, "API_DOMESTIC_ITINERARY");
			text = client.execCmd("pii " + ticketNo + " " + iteneraryNo + " " + ticketPrice);
		} catch (UfisException e) {
			LOGGER.error("UfisException: {}", e.getMessage());
//			return JsonUtil.toJson(new UfisPiiResponse(false, "Ufis 服务器异常！"), false);
		} finally {
			if (null != client)
				client.close();
		}
		LOGGER.info("Ufis pii 指令返回：　{}", text);
		// TODO
		return null;
	}

	public static String handleVi(String ticketNo, String iteneraryNo, String officeId) throws IOException {
		String text = null;
		EtermUfisClient client = null;
		try {
			client = new EtermUfisClient(officeId, "API_DOMESTIC_ITINERARY");
			text = client.execCmd("vi " + ticketNo + " " + iteneraryNo);
		} catch (UfisException e) {
			LOGGER.error("UfisException: {}", e.getMessage());
			return JsonUtil.toJson(new UfisViResponse(false, "Ufis 服务器异常！"), false);
		} finally {
			if (null != client)
				client.close();
		}
		LOGGER.info("Ufis vi 指令返回：　{}", text);
		if (null == text) {
			LOGGER.info("Ufis vi 指令返回结果为空！");
			return JsonUtil.toJson(new UfisViResponse(false, "Ufis vi 指令返回结果为空！"), false);
		}
		if (text.startsWith("UFIS-ERROR")) {
			LOGGER.info("Ufis Error: " + text);
			return JsonUtil.toJson(new UfisViResponse(false, text), false);
		} else if (text.contains("组帐号错误或没有可用PID")) {
			LOGGER.info("Eterm Error: " + text);
			return JsonUtil.toJson(new UfisViResponse(false, text), false);
		}
		UfisViResponse ufisResponse = null;
		try {
			ufisResponse = convViResponse(text);
		} catch (Exception e) {
			LOGGER.info("UfisViResponse 转化异常！");
			ufisResponse = new UfisViResponse(false, "UfisViResponse 转化异常！");
		}
		LOGGER.info("Ufis vi 解析结果：　{}", ufisResponse);
		String result = JsonUtil.toJson(ufisResponse, false);
		return result;
	}

	public static String handleVbi(String iteneraryNo, String officeId) throws IOException {
		String text = null;
		EtermUfisClient client = null;
		try {
			client = new EtermUfisClient(officeId, "API_DOMESTIC_ITINERARY");
			text = client.execCmd("vbi " + iteneraryNo);
		} catch (UfisException e) {
			LOGGER.error("UfisException: {}", e.getMessage());
			return JsonUtil.toJson(new UfisVbiResponse(false, "Ufis 服务器异常！"), false);
		} finally {
			client.close();
		}
		LOGGER.info("Ufis vbi 指令返回：　{}", text);
		if (null == text) {
			LOGGER.info("Ufis vbi 指令返回结果为空！");
			return JsonUtil.toJson(new UfisVbiResponse(false, "Ufis vbi 指令返回结果为空！"), false);
		}
		if (text.startsWith("UFIS-ERROR")) {
			LOGGER.info("Ufis Error: " + text);
			return JsonUtil.toJson(new UfisVbiResponse(false, text), false);
		} else if (text.contains("组帐号错误或没有可用PID")) {
			LOGGER.info("Eterm Error: " + text);
			return JsonUtil.toJson(new UfisVbiResponse(false, text), false);
		}
		UfisVbiResponse ufisResponse = null;
		try {
			ufisResponse = convVbiResponse(text);
		} catch (Exception e) {
			LOGGER.info("UfisVbiResponse 转化异常！");
			ufisResponse = new UfisVbiResponse(false, "UfisVbiResponse 转化异常！");
		}
		LOGGER.info("Ufis vbi 解析结果：　{}", ufisResponse);
		String result = JsonUtil.toJson(ufisResponse, false);
		return result;
	}
	
	public static String handleDt(String ticketNo, String officeId) throws IOException {
		String text = null;
		EtermUfisClient client = null;
		try {
			client = new EtermUfisClient(officeId, "API_DOMESTIC_ITINERARY");
			text = client.execCmd("dt " + ticketNo + " en");
		} catch (UfisException e) {
			LOGGER.error("UfisException: {}", e.getMessage());
			return JsonUtil.toJson(new UfisDtResponse(false, "Ufis 服务器异常！"), false);
		} finally {
			if (null != client)
				client.close();
		}
		LOGGER.info("Ufis dt en 指令返回：　{}", text);
		if (null == text) {
			LOGGER.info("Ufis dt en 指令返回结果为空！");
			return JsonUtil.toJson(new UfisDtResponse(false, "Ufis dt 指令返回结果为空！"), false);
		}
		if (text.startsWith("UFIS-ERROR")) {
			LOGGER.info("Ufis Error: " + text);
			return JsonUtil.toJson(new UfisDtResponse(false, text), false);
		} else if (text.contains("组帐号错误或没有可用PID")) {
			LOGGER.info("Eterm Error: " + text);
			return JsonUtil.toJson(new UfisDtResponse(false, text), false);
		}
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" ?>");
		sb.append("<Eterm3in1DtResponse>");
		if (text.trim().startsWith("<ErrorReason>"))
			sb.append(text.substring(0, text.lastIndexOf("</ErrorReason>")+14));
		else
			sb.append(text.substring(0, text.lastIndexOf("</NOTICE>")+9));
		sb.append("</Eterm3in1DtResponse>");
		LOGGER.info("Ufis dt en 指令返回结果补正：　{}", sb.toString());
		Eterm3in1DtResponse etermResponse = null;
		try {
			etermResponse = (Eterm3in1DtResponse) XmlUtil.fromXml(sb.toString(), Eterm3in1DtResponse.class);
		} catch (JAXBException e) {
			LOGGER.info("XML反序列化Eterm3in1DtResponse失败: {}", e.getMessage());
			return JsonUtil.toJson(new UfisDtResponse(false, "XML反序列化Eterm3in1DtResponse失败！"), false);
		}
		UfisDtResponse ufisResponse = null;
		try {
			ufisResponse = convDtResponse(etermResponse);
		} catch (Exception e) {
			LOGGER.info("ufisDtResponse转化异常！");
			ufisResponse = new UfisDtResponse(false, "ufisDtResponse转化异常！");
		}
		LOGGER.info("Ufis dt 解析结果：　{}", ufisResponse);
		String result = JsonUtil.toJson(ufisResponse, false);
		return result;
	}
	
	public static String handleDp(String pnr, String officeId) throws IOException {
		String text = null;
		EtermUfisClient client = null;
		try {
			client = new EtermUfisClient(officeId, "API_DOMESTIC_ITINERARY");
			text = client.execCmd("dp " + pnr);
		} catch (UfisException e) {
			LOGGER.error("UfisException: {}", e.getMessage());
//			return JsonUtil.toJson(new UfisDpResponse(false, "Ufis 服务器异常！"), false);
		} finally {
			if (null != client)
				client.close();
		}
		LOGGER.info("Ufis dp 指令返回：　{}", text);
		// TODO
		return text;
	}
	
	private static final Pattern ERROR_PATTERN = Pattern.compile("<ErrorReason>([\\w\\W]+?)</ErrorReason>");
	
	public static UfisPidResponse convPidResponse(String text) throws IOException {
		UfisPidResponse response = new UfisPidResponse();
		Matcher matcher = ERROR_PATTERN.matcher(text.trim());
		if (matcher.matches()) {
			response.setSuccess(false);
			response.setErrorMsg(matcher.group(1));
		} else {
			response.setSuccess(true);
			response.setText(text);
			if (text.startsWith("GP")) {
				response.setGpNo(text.split("[\r\n]+")[0].trim());
				String[] lines = text.split("\n");
				StringBuilder sb = new StringBuilder();
				sb.append("\n");
				sb.append(lines[0]);
				sb.append("\n");
				sb.append("\n");
				for (int i=3; i<lines.length; i++) {
					sb.append(lines[i]);
					sb.append("\n");
				}
				response.setText(sb.toString());
			}
		}
		return response;
	}
	
	public static UfisViResponse convViResponse(String text) {
		UfisViResponse response = new UfisViResponse();
		Matcher matcher = ERROR_PATTERN.matcher(text.trim());
		if (matcher.matches()) {
			response.setSuccess(false);
			response.setErrorMsg(matcher.group(1));
		} if (text.contains("DELETED SUCCESSFULLY")) {
			response.setSuccess(true);
		} else {
			response.setSuccess(false);
			response.setErrorMsg(text);
		}
		return response;
	}
	
	public static UfisVbiResponse convVbiResponse(String text) {
		UfisVbiResponse response = new UfisVbiResponse();
		Matcher matcher = ERROR_PATTERN.matcher(text.trim());
		if (matcher.matches()) {
			response.setSuccess(false);
			response.setErrorMsg(matcher.group(1));
		} else {
			response.setSuccess(true);
		}
		return response;
	}
	
	public static UfisDtResponse convDtResponse(Eterm3in1DtResponse etermResponse) {
		if (null == etermResponse)
			return null;
		UfisDtResponse ufisResponse = new UfisDtResponse();
		if (null != etermResponse.ErrorReason) {
			ufisResponse.setSuccess(false);
			ufisResponse.setErrorMsg(etermResponse.ErrorReason);
			return ufisResponse;
		}
		ufisResponse.setSuccess(true);
		ufisResponse.setPnrNo(etermResponse.PNRNO);
		ufisResponse.setAirlinePnrNo(etermResponse.AIRLINEPNRNO);
		if (null != etermResponse.AGENTINFO)
			ufisResponse.setIataNo(etermResponse.AGENTINFO.IATANUMBER);
		if (null != etermResponse.TICKETS && null != etermResponse.TICKETS.TICKETINFO 
				&& etermResponse.TICKETS.TICKETINFO.length > 0) {
			Ticket ticket = new Ticket();
			ticket.setTicketNo(etermResponse.TICKETS.TICKETINFO[0].TKTN);
			ticket.setReceiptPnt(etermResponse.TICKETS.TICKETINFO[0].RECEIPTPNT);
			ticket.setConjTicket(etermResponse.TICKETS.TICKETINFO[0].CONJTKT);
			ticket.setTicketStatus(etermResponse.TICKETS.TICKETINFO[0].TKTSTATUS);
			ticket.setIssuedDate(etermResponse.TICKETS.TICKETINFO[0].ISSUEDDATE);
			ticket.setIssuedBy(etermResponse.TICKETS.TICKETINFO[0].ISSUEDBY);
			ticket.setEr(etermResponse.TICKETS.TICKETINFO[0].ER);
			ufisResponse.setTicket(ticket);
		}
		if (null != etermResponse.PASSENGERLIST && null != etermResponse.PASSENGERLIST.PASSENGER 
				&& etermResponse.PASSENGERLIST.PASSENGER.length > 0) {
			Passenger passenger = new Passenger();
			passenger.setName(etermResponse.PASSENGERLIST.PASSENGER[0].NAME);
			if (null != etermResponse.PASSENGERLIST.PASSENGER[0].IDCARD) {
				IdCard idCard = new IdCard();
				idCard.setNumber(etermResponse.PASSENGERLIST.PASSENGER[0].IDCARD.CARDNUMBER);
				idCard.setType(etermResponse.PASSENGERLIST.PASSENGER[0].IDCARD.CARDTYPE);
				passenger.setIdCard(idCard);
			}
			ufisResponse.setPassenger(passenger);
		}
		if (null != etermResponse.SEGMENTS && null != etermResponse.SEGMENTS.SEGMENT && etermResponse.SEGMENTS.SEGMENT.length > 0) {
			List<FlightSegment> flightSegments = new ArrayList<FlightSegment>();
			for (int i=0; i<etermResponse.SEGMENTS.SEGMENT.length; i++) {
				Segment segment = etermResponse.SEGMENTS.SEGMENT[i];
				if (segment.ARNK != null)
					continue;
				if (segment.NORMAL != null) {
					FlightSegment flightSegment = new FlightSegment();
					flightSegment.setFlightType(FlightType.NORMAL);
					flightSegment.setAirline(segment.NORMAL.AIRLINE);
					flightSegment.setFlightNo(segment.NORMAL.FLIGHTNO);
					flightSegment.setDeptDate(segment.NORMAL.DATE);
					flightSegment.setCabinClass(segment.NORMAL.CLASS);
					flightSegment.setSeatStatus(segment.NORMAL.SEATSTATUS);
					flightSegment.setSegStatus(segment.NORMAL.SEGSTATUS);
					if (null != segment.NORMAL.LEGS && null != segment.NORMAL.LEGS.LEG && segment.NORMAL.LEGS.LEG.length > 0) {
						List<Leg> legs = new ArrayList<Leg>();
						for (int j=0; j<segment.NORMAL.LEGS.LEG.length; j++) {
							com.travelzen.etermface.service.entity.Eterm3in1DtResponse.Segments.Segment.Normal.Legs.Leg oldLeg = segment.NORMAL.LEGS.LEG[j];
							Leg leg = new Leg();
							leg.setDeptAirport(getAirportCode(oldLeg.ORIGIN));
							leg.setArrAirport(getAirportCode(oldLeg.DEST));
							leg.setDeptAirportEn(oldLeg.ORIGIN);
							leg.setArrAirportEn(oldLeg.DEST);
							leg.setDeptAirportCh(getAirportCh(leg.getDeptAirport()));
							leg.setArrAirportCh(getAirportCh(leg.getArrAirport()));
							leg.setDeptTerminal(oldLeg.ORI_TERMINAL);
							leg.setArrTerminal(oldLeg.DEST_TERMINAL);
							int nights = 0;
							if (null != oldLeg.ARRTIME && oldLeg.ARRTIME.length() == 6 && (oldLeg.ARRTIME.charAt(5) + "").matches("\\d")) {
								nights = Integer.parseInt(oldLeg.ARRTIME.charAt(5) + "");
							}
							if (null != oldLeg.DEPDATE && oldLeg.DEPDATE.length() >= 5 && null != oldLeg.DEPTIME && oldLeg.DEPTIME.length() >=4) {
								Pair<String, String> dates = DateTimeUtil.parseDates(oldLeg.DEPDATE, nights, oldLeg.DEPTIME.substring(0, 4));
								leg.setDeptDate(dates.getValue0());
								leg.setArrDate(dates.getValue1());
							}
							if (null != oldLeg.DEPTIME && oldLeg.DEPTIME.length() >=4)
								leg.setDeptTime(DateTimeUtil.getTime(oldLeg.DEPTIME.substring(0, 4)));
							if (null != oldLeg.ARRTIME && oldLeg.ARRTIME.length() >=4)
								leg.setArrTime(DateTimeUtil.getTime(oldLeg.ARRTIME.substring(0, 4)));
							leg.setFareBasis(oldLeg.FAREBASIS);
							leg.setBaggage(oldLeg.BAGGAGE);
							legs.add(leg);
						}
						if (legs.size() > 0)
							flightSegment.setLegs(legs);
					}
					flightSegments.add(flightSegment);
				} else if (segment.OPEN != null) {
					if (segment.OPEN.SEGSTATUS.equals("REFUNDED") || segment.OPEN.SEGSTATUS.equals("VOID"))
						continue;
					FlightSegment flightSegment = new FlightSegment();
					flightSegment.setFlightType(FlightType.OPEN);
					flightSegment.setAirline(segment.OPEN.AIRLINE);
					flightSegment.setDeptDate(DateTimeUtil.parseDate(segment.OPEN.DATE));
					flightSegment.setCabinClass(segment.OPEN.CLASS);
					flightSegment.setSeatStatus(segment.OPEN.SEATSTATUS);
					flightSegment.setSegStatus(segment.OPEN.SEGSTATUS);
					List<Leg> legs = new ArrayList<Leg>();
					Leg leg = new Leg();
					leg.setDeptAirport(getAirportCode(segment.OPEN.ORIGIN));
					leg.setArrAirport(getAirportCode(segment.OPEN.DEST));
					leg.setDeptAirportEn(segment.OPEN.ORIGIN);
					leg.setArrAirportEn(segment.OPEN.DEST);
					leg.setDeptAirportCh(getAirportCh(leg.getDeptAirport()));
					leg.setArrAirportCh(getAirportCh(leg.getArrAirport()));
					leg.setFareBasis(segment.OPEN.FAREBASIS);
					leg.setBaggage(segment.OPEN.BAGGAGE);
					legs.add(leg);
					flightSegment.setLegs(legs);
					flightSegments.add(flightSegment);
				}
			}
			if (flightSegments.size() > 0)
				ufisResponse.setFlightSegments(flightSegments);
		}
		if (null != etermResponse.FARES && null != etermResponse.FARES.FARE) {
			Fare fare = new Fare();
			if (null != etermResponse.FARES.FARE.TICKETFARE) {
				fare.setTicketFare(getFareDetail(etermResponse.FARES.FARE.TICKETFARE.AMOUNT, etermResponse.FARES.FARE.TICKETFARE.CURRENCY));
			}
			FareDetail totalFare = getFareDetail(etermResponse.FARES.FARE.TOTALFARE.AMOUNT, etermResponse.FARES.FARE.TOTALFARE.CURRENCY);
			if (null != etermResponse.FARES.FARE.TOTALFARE) {
				fare.setTotalFare(totalFare);
			}
			if (null != etermResponse.FARES.FARE.TAX && etermResponse.FARES.FARE.TAX.length > 0) {
				if (etermResponse.FARES.FARE.TAX.length == 2) {
					fare.setCnTax(getFareDetail(etermResponse.FARES.FARE.TAX[0].AMOUNT, etermResponse.FARES.FARE.TAX[0].CURRENCY));
					fare.setYqTax(getFareDetail(etermResponse.FARES.FARE.TAX[1].AMOUNT, etermResponse.FARES.FARE.TAX[1].CURRENCY));
				}
				if (etermResponse.FARES.FARE.TAX.length == 3) {
					fare.setObTax(fare.getTotalFare());
					fare.setYqTax(getFareDetail("0.0", ""));
					FareDetail fareDetail = getFareDetail(etermResponse.FARES.FARE.TAX[0].AMOUNT, etermResponse.FARES.FARE.TAX[0].CURRENCY);
					if (fareDetail.getAmount() != fare.getObTax().getAmount() && fareDetail.getAmount() != fare.getYqTax().getAmount()) {
						fare.setCnTax(fareDetail);
					} else {
						fareDetail = getFareDetail(etermResponse.FARES.FARE.TAX[1].AMOUNT, etermResponse.FARES.FARE.TAX[1].CURRENCY);
						if (fareDetail.getAmount() != fare.getObTax().getAmount() && fareDetail.getAmount() != fare.getYqTax().getAmount()) {
							fare.setCnTax(fareDetail);
						} else {
							fareDetail = getFareDetail(etermResponse.FARES.FARE.TAX[2].AMOUNT, etermResponse.FARES.FARE.TAX[2].CURRENCY);
							fare.setCnTax(fareDetail);
						}
					}
				}
			}
			if (null != etermResponse.FCS && null != etermResponse.FCS.FC 
					&& etermResponse.FCS.FC.length > 0) {
				fare.setFareCompute(etermResponse.FCS.FC[0]);
			}
			if (null != etermResponse.FPS && null != etermResponse.FPS.FP 
					&& etermResponse.FPS.FP.length > 0) {
				fare.setPaymentMode(etermResponse.FPS.FP[0].PAYMENTTYPE);
			}
			ufisResponse.setFare(fare);
		}
		return ufisResponse;
	}
	
	private static final Pattern DOUBLE_AMOUNT = Pattern.compile("\\d+\\.\\d+");

	private static FareDetail getFareDetail(String amount, String currency) {
		FareDetail fareDetail = new FareDetail();
		Matcher matcher = DOUBLE_AMOUNT.matcher(amount);
		if (matcher.matches())
			fareDetail.setAmount(Double.parseDouble(amount));
		else
			fareDetail.setAmount(0);
		fareDetail.setCurrency(currency);
		return fareDetail;
	}

	private static String getAirportCode(String airport) {
		String[] tokens = airport.split("--");
		if (tokens.length == 2)
			return tokens[0];
		airport = airport.substring(0, airport.indexOf(" AIRPORT"));
		return AirportUtil.getCodeByEn(airport);
	}

	private static String getAirportCh(String airport) {
		String airportCh = AirportUtil.getChByCode(airport);
		if (null == airportCh)
			airportCh = AirportUtil.getChByEn(airport);
		return airportCh;
	}

}
