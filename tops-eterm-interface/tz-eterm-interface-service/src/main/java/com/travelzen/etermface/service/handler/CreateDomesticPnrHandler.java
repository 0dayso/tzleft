package com.travelzen.etermface.service.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.common.ufis.util.UfisException;
import com.travelzen.etermface.common.config.cdxg.exception.SessionExpireException;
import com.travelzen.etermface.service.EtermUfisClient;
import com.travelzen.etermface.service.EtermWebClient;
import com.travelzen.etermface.service.common.WriteFile;
import com.travelzen.etermface.service.constant.UfisStatus;
import com.travelzen.framework.core.common.ReturnClass;
import com.travelzen.tops.flight.common.domestic.pojo.PnrAirSeg;
import com.travelzen.tops.flight.common.domestic.pojo.PnrEntityParam;
import com.travelzen.tops.flight.common.domestic.pojo.PnrPassenger;
import com.travelzen.tops.flight.common.domestic.pojo.PnrResult;
import com.travelzen.tops.flight.common.pojo.CommonCreatePnrResult;

/**
 * 创建国内PNR
 * <p>
 * 
 * @author yiming.yan
 * @Date Jan 26, 2016
 */
public class CreateDomesticPnrHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CreateDomesticPnrHandler.class);

	public static CommonCreatePnrResult handle(List<PnrEntityParam> peplist, boolean useUfis) {
		CommonCreatePnrResult result = new CommonCreatePnrResult();
		result.setBookingOfficeNo(peplist.get(0).getOfficeId());
		List<PnrResult> pnrResults = new ArrayList<PnrResult>();
		
		for (PnrEntityParam pep:peplist) {
			if (pep.getAirSegList() == null || pep.getAirSegList().size() == 0)
				continue;
			if (pep.getPassengerList() == null || pep.getPassengerList().size() == 0)
				continue;
			
			List<String> subCmds = new ArrayList<String>();
			subCmds.addAll(createSs(pep));
			subCmds.addAll(createNm(pep));
			subCmds.addAll(createSsrFoid(pep));
			subCmds.addAll(createSsrChld(pep));
			subCmds.addAll(createXn(pep));
			subCmds.addAll(createSsrInft(pep));
			subCmds.addAll(createOsiCtct(pep));
			subCmds.addAll(createTktl(pep));
			subCmds.addAll(createRmkAuth(pep));
			
			StringBuilder ssCmd = new StringBuilder();
			for (String subCmd:subCmds) {
				ssCmd.append(subCmd).append("\r");
			}
			ssCmd.append("\\I");
			LOGGER.info("生成SS指令：{}", ssCmd.toString());
			
			String retText = null;
			if (pep.getOfficeId() == null)
				pep.setOfficeId("SHA255");
			
			if (UfisStatus.active && UfisStatus.createDomPnr) {
				EtermUfisClient client = null;
				try {
					client = new EtermUfisClient(pep.getOfficeId());
					retText = client.execCmd(ssCmd.toString(), true);
				} catch (UfisException e) {
					LOGGER.error(e.getMessage(), e);
					return createErrorResultForUfis(result, e.getMessage());
				} finally {
					if (null != client)
						client.close();
				}
			} else {
				EtermWebClient client = new EtermWebClient(pep.getOfficeId());
				try {
					client.connect();
					ReturnClass<String> ret = client.executeCmd(ssCmd.toString(), false);
					if (ret != null)
						retText = ret.getObject();
				} catch (SessionExpireException e) {
					LOGGER.error(e.getMessage(), e);
					return createErrorResult(result, e.getMessage());
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
					return createErrorResult(result, e.getMessage());
				} finally {
					client.close();
				}
			}
			
			PnrResult pnrResult = new PnrResult();
			pnrResult.setSegmentList(pep.getAirSegList());
			if (null != retText) {
				pnrResult.setPnrNo(getPnrNo(retText, pep.getAirSegList().get(0).getAirNo().substring(0, 2), pep.getAirSegList().size()));
				if (pnrResult.getPnrNo() != null) {
					pnrResult.setOk(true);
					// 将创建好的PNR写入文本，每日定时删除
					WriteFile.write(pnrResult.getPnrNo());
				} else {
					pnrResult.setOk(false);
					pnrResult.setError("创建国内PNR失败！");
					if (retText.contains("UNABLE TO SELL.PLEASE CHECK THE AVAILABILITY"))
						pnrResult.setErrorDetail("您选择的航班库存不足，请重新查询预订！");
				}
			} else {
				pnrResult.setOk(false);
				pnrResult.setError("SS指令返回结果为空！");
			}
			pnrResults.add(pnrResult);
		}
		
		result.setDomesticResult(pnrResults);
		return result;
	}

	private static List<String> createSs(PnrEntityParam pep) {
		List<String> subCmds = new ArrayList<String>();
		String psgType = pep.getPassengerList().get(0).getType();
		int psgNum = countPsg(pep.getPassengerList());
		for (PnrAirSeg pnrAirSeg:pep.getAirSegList()) {
			StringBuilder subCmd = new StringBuilder();
			subCmd.append("SS " + pnrAirSeg.getAirNo() + " ");
			if (psgType.equals("CHD"))
				subCmd.append(pnrAirSeg.getSeatType() + " ");
			else
				subCmd.append(pnrAirSeg.getFltClass() + " ");
			subCmd.append(parseDeptDate(pnrAirSeg.getDepartureTime()) + " ");
			subCmd.append(pnrAirSeg.getOrgCity() + pnrAirSeg.getDesCity() + " NN" + psgNum);
			subCmds.add(subCmd.toString());
		}
		return subCmds;
	}
	
	private static int countPsg(List<PnrPassenger> passengerList) {
		int i = 0;
		for (PnrPassenger passenger:passengerList) {
			if (!passenger.getType().equals("INF"))
				i++;
		}
		return i;
	}

	private static List<String> createNm(PnrEntityParam pep) {
		List<String> subCmds = new ArrayList<String>();
		StringBuilder subCmd = new StringBuilder("NM");
		for (PnrPassenger passenger:pep.getPassengerList()) {
			if (!passenger.getType().equals("INF")){
				if(passenger.getType().equals("CHD")){
					String CHDName=new String(passenger.getName()).toUpperCase();
					if(CHDName.endsWith("CHD")){
						subCmd.append("1" + passenger.getName());
					}else{
						subCmd.append("1" + passenger.getName()+"CHD");
					}
				}else{
					subCmd.append("1" + passenger.getName());
				}
				
			}
				
		}
		subCmds.add(subCmd.toString());
		return subCmds;
	}
	
	private static List<String> createSsrFoid(PnrEntityParam pep) {
		List<String> subCmds = new ArrayList<String>();
		String airline = "";
		for (PnrAirSeg pnrAirSeg:pep.getAirSegList()) {
			String tempAirline = pnrAirSeg.getAirNo().substring(0, 2);
			if (tempAirline.equals(airline))
				continue;
			airline = tempAirline;
			int i = 1;
			for (PnrPassenger passenger:pep.getPassengerList()) {
				if (!passenger.getType().equals("INF")) {
					StringBuilder subCmd = new StringBuilder("SSR FOID ");
					subCmd.append(airline + " HK/NI" + passenger.getId() + "/P" + i);
					subCmds.add(subCmd.toString());
					i++;
				}
			}
		}
		return subCmds;
	}
	
	private static List<String> createSsrChld(PnrEntityParam pep) {
		List<String> subCmds = new ArrayList<String>();
		String airline = "";
		for (PnrAirSeg pnrAirSeg:pep.getAirSegList()) {
			String tempAirline = pnrAirSeg.getAirNo().substring(0, 2);
			if (tempAirline.equals(airline))
				continue;
			airline = tempAirline;
			int i = 1;
			for (PnrPassenger passenger:pep.getPassengerList()) {
				if (passenger.getType().equals("CHD")) {
					StringBuilder subCmd = new StringBuilder("SSR CHLD ");
					subCmd.append(airline + " HK1 " + getChldBirthday(passenger) + "/P" + i);
					subCmds.add(subCmd.toString());
				}
				i++;
			}
		}
		return subCmds;
	}
	
	private static String getChldBirthday(PnrPassenger passenger) {
		String str = null;
		String id = passenger.getId();
		if (id.length() > 14) {
			str = id.substring(6, 10) + "-" + id.substring(10, 12) + "-" + id.substring(12, 14);
		} else {
			str = passenger.getBirthday();
		}
		return parseBirthdayLong(str);
	}
	
	private static List<String> createXn(PnrEntityParam pep) {
		List<String> subCmds = new ArrayList<String>();
		int i = 1;
		for (PnrPassenger passenger:pep.getPassengerList()) {
			if (!passenger.getType().equals("INF"))
				continue;
			StringBuilder subCmd = new StringBuilder("XN IN/");
			subCmd.append(pureName(passenger.getName()) + " INF(" + parseBirthdayShort(passenger.getBirthday()) + ")/P" + i++);
			subCmds.add(subCmd.toString());
		}
		return subCmds;
	}
	
	private static String pureName(String name) {
		name = name.toUpperCase();
		if (name.endsWith("INF"))
			name = name.substring(0, name.length()-3);
		return name;
	}
	
	private static List<String> createSsrInft(PnrEntityParam pep) {
		List<String> subCmds = new ArrayList<String>();
		int i = 1;
		for (PnrPassenger passenger:pep.getPassengerList()) {
			if (!passenger.getType().equals("INF"))
				continue;
			for (PnrAirSeg pnrAirSeg:pep.getAirSegList()) {
				StringBuilder subCmd = new StringBuilder("SSR INFT ");
				subCmd.append(pnrAirSeg.getAirNo().substring(0, 2) + " NN1 ");
				subCmd.append(pnrAirSeg.getOrgCity() + pnrAirSeg.getDesCity() + " ");
				subCmd.append(pnrAirSeg.getAirNo().substring(2) + " " + pnrAirSeg.getFltClass() + " ");
				subCmd.append(parseDeptDate(pnrAirSeg.getDepartureTime()) + " ");
				subCmd.append(passenger.getPinyi() + " ");
				subCmd.append(parseBirthdayLong(passenger.getBirthday()) + "/P" + i);
				subCmds.add(subCmd.toString());
			}
			i++;
		}
		return subCmds;
	}
	
	private static List<String> createOsiCtct(PnrEntityParam pep) {
		List<String> subCmds = new ArrayList<String>();
		String airline = "";
		for (PnrAirSeg pnrAirSeg:pep.getAirSegList()) {
			String tempAirline = pnrAirSeg.getAirNo().substring(0, 2);
			if (tempAirline.equals(airline))
				continue;
			airline = tempAirline;
			StringBuilder subCmd = new StringBuilder("OSI ");
			subCmd.append(pnrAirSeg.getAirNo().substring(0, 2) + " ");
			subCmd.append("CTCT " + pep.getContactInfo());
			subCmds.add(subCmd.toString());
		}
		return subCmds;
	}
	
	private static List<String> createTktl(PnrEntityParam pep) {
		List<String> subCmds = new ArrayList<String>();
		StringBuilder subCmd = new StringBuilder("TKTL/");
		subCmd.append(parseTktlDateTime(pep.getAirSegList().get(0).getDepartureTime()));
		subCmd.append("/SHA255");
		subCmds.add(subCmd.toString());
		return subCmds;
	}
	
	private static List<String> createRmkAuth(PnrEntityParam pep) {
		List<String> subCmds = new ArrayList<String>();
		// 暂无
		return subCmds;
	}

	/**
	 * 构建创建PNR失败对象
	 *
	 * @param commonCreatePnrResult
	 * @param errorInfo
	 */
	public static CommonCreatePnrResult createErrorResult(CommonCreatePnrResult commonCreatePnrResult, String errorInfo) {
		if (commonCreatePnrResult == null) {
			commonCreatePnrResult = new CommonCreatePnrResult();
		}

		if (commonCreatePnrResult.getDomesticResult() == null) {
			List<PnrResult> domesticResult = new ArrayList<PnrResult>();
			commonCreatePnrResult.setDomesticResult(domesticResult);
		}

		PnrResult pnrResult = new PnrResult();
		pnrResult.setOk(false);
		pnrResult.setError("生成PNR失败！");
		if (errorInfo.startsWith("不存在该Office的配置"))
			pnrResult.setErrorDetail("请联系平台客服：4007206666");
		else
			pnrResult.setErrorDetail(errorInfo);
		commonCreatePnrResult.getDomesticResult().add(pnrResult);
		
		return commonCreatePnrResult;
	}
	
	public static CommonCreatePnrResult createErrorResultForUfis(CommonCreatePnrResult commonCreatePnrResult, String errorInfo) {
		if (commonCreatePnrResult == null) {
			commonCreatePnrResult = new CommonCreatePnrResult();
		}

		if (commonCreatePnrResult.getDomesticResult() == null) {
			List<PnrResult> domesticResult = new ArrayList<PnrResult>();
			commonCreatePnrResult.setDomesticResult(domesticResult);
		}

		PnrResult pnrResult = new PnrResult();
		pnrResult.setOk(false);
		pnrResult.setError("生成PNR失败！");
		if (errorInfo.contains("Ufis-Client无法获取有效的分组"))
			pnrResult.setErrorDetail("请联系平台客服：4007206666");
		else
			pnrResult.setErrorDetail(errorInfo);
		commonCreatePnrResult.getDomesticResult().add(pnrResult);
		
		return commonCreatePnrResult;
	}
	
	private static DateTimeFormatter deptDateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").withLocale(Locale.ENGLISH).withZone(DateTimeZone.forID("+08:00"));
	
	private static String parseDeptDate(String dateStr) {
		DateTime dateTime = deptDateTimeFormatter.parseDateTime(dateStr);
		return dateTime.toString("ddMMM", Locale.ENGLISH).toUpperCase();
	}
	
	private static String parseTktlDateTime(String dateStr) {
		DateTime dateTime = deptDateTimeFormatter.parseDateTime(dateStr);
		dateTime = dateTime.minusMinutes(30);
		return dateTime.toString("HHmm/ddMMM", Locale.ENGLISH).toUpperCase();
	}
	
	private static DateTimeFormatter birthDateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd").withLocale(Locale.ENGLISH).withZone(DateTimeZone.forID("+08:00"));
	
	private static String parseBirthdayShort(String dateStr) {
		DateTime dateTime = birthDateTimeFormatter.parseDateTime(dateStr);
		return dateTime.toString("MMMyy", Locale.ENGLISH).toUpperCase();
	}
	
	private static String parseBirthdayLong(String dateStr) {
		DateTime dateTime = birthDateTimeFormatter.parseDateTime(dateStr);
		return dateTime.toString("ddMMMyy", Locale.ENGLISH).toUpperCase();
	}
	
	private static Pattern pattern1 = Pattern.compile("([A-Z0-9]{6}) -EOT SUCCESSFUL");
	private static Pattern pattern2 = Pattern.compile("[\r\n]([A-Z0-9]{6}) -");
	
	private static String getPnrNo(String text, String airCompany, int airlineNum) {
		LOGGER.info("开始解析创建的PNR号：{}", text);
		Matcher matcher1 = pattern1.matcher(text.trim());
		Matcher matcher2 = pattern2.matcher(text.trim());
		if (matcher1.find()) {
			LOGGER.info("解析得到创建的PNR号：{}", matcher1.group(1));
			return matcher1.group(1);
		} else if (matcher2.find()) {
			LOGGER.info("解析得到创建的PNR号：{}", matcher2.group(1));
			return matcher2.group(1);
		}
		if (airCompany.equals("G5")) {
			String[] lines = text.split("\r");
			if (lines.length > airlineNum && lines[airlineNum].matches("[A-Z0-9]{6} *")) {
				LOGGER.info("解析得到创建的PNR号：{}", lines[airlineNum].trim());
				return lines[airlineNum].trim();
			}
		}
		LOGGER.info("未解析出PNR号：{}", text);
		return null;
	}
	
	public static void main(String args[]) {
		PnrEntityParam lvParam = new PnrEntityParam();
		lvParam.setContactInfo("13636341467");
		lvParam.setDateLimit("2016-04-09 16:00:00");
//
		PnrPassenger lvPNRPassenger = new PnrPassenger();
		lvPNRPassenger.setAirline("MU");
		lvPNRPassenger.setName("卢思维");
		lvPNRPassenger.setPinyi("LU/SIWEI");
		lvPNRPassenger.setIdtype("NI");
		lvPNRPassenger.setId("110106198208280917");
		lvPNRPassenger.setType("ADT");
		lvPNRPassenger.setBirthday("1982-08-28");

		PnrPassenger lvPNRPassenger2 = new PnrPassenger();
		lvPNRPassenger2.setAirline("MU");
		lvPNRPassenger2.setName("LU/XINYI");
		lvPNRPassenger2.setPinyi("LU/XINYI");
		lvPNRPassenger2.setType("INF");
		lvPNRPassenger2.setBirthday("2015-01-23");

        List<PnrPassenger> lvPNRPassengerList = new ArrayList<PnrPassenger>();
        lvPNRPassengerList.add(lvPNRPassenger);
        lvPNRPassengerList.add(lvPNRPassenger2);
		lvParam.setPassengerList(lvPNRPassengerList);
//
		PnrAirSeg lvPNRAirSeg = new PnrAirSeg();
		lvPNRAirSeg.setAirNo("MU5102");
		lvPNRAirSeg.setOrgCity("PEK");
		lvPNRAirSeg.setDesCity("SHA");
		lvPNRAirSeg.setSeatType('Y');
		lvPNRAirSeg.setFltClass('Y');
		lvPNRAirSeg.setActionCode("HK");
		lvPNRAirSeg.setTktNum(1);
		lvPNRAirSeg.setDepartureTime("2016-04-25 08:00");
		
		PnrAirSeg lvPNRAirSeg1 = new PnrAirSeg();
		lvPNRAirSeg1.setAirNo("MU5107");
		lvPNRAirSeg1.setOrgCity("SHA");
		lvPNRAirSeg1.setDesCity("PEK");
		lvPNRAirSeg1.setSeatType('Y');
		lvPNRAirSeg1.setFltClass('Y');
		lvPNRAirSeg1.setActionCode("HK");
		lvPNRAirSeg1.setTktNum(1);
		lvPNRAirSeg1.setDepartureTime("2016-04-28 11:00");
		
		List<PnrAirSeg> lvPNRAirSegList = new ArrayList<PnrAirSeg>();
		lvPNRAirSegList.add(lvPNRAirSeg);
		lvPNRAirSegList.add(lvPNRAirSeg1);
//
		lvParam.setAirSegList(lvPNRAirSegList);
		lvParam.setOfficeId("SHA255");
//
		List<PnrEntityParam> params = new ArrayList<>();
		params.add(lvParam);

		CommonCreatePnrResult result = handle(params, false);
		System.out.println("**************最终结果*****************");
		System.out.println(result);
		System.out.println("**************************************");
	}

}
