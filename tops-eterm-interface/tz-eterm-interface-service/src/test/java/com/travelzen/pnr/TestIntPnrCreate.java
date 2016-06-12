package com.travelzen.pnr;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;

import com.travelzen.etermface.common.config.cdxg.exception.SessionExpireException;
import com.travelzen.etermface.service.CreateIntPNR;
import com.travelzen.etermface.service.createpnr.CreatePnrReturnClass;
import com.travelzen.etermface.service.createpnr.CreatePnrReturnCode;
import com.travelzen.etermface.service.entity.FlightInfo;
import com.travelzen.etermface.service.entity.IntPnrCreateRequest;
import com.travelzen.etermface.service.entity.PassengerInfo;

public class TestIntPnrCreate {
	
	
	@Test
	public void testCreate() {

		IntPnrCreateRequest request = new IntPnrCreateRequest();
		List<FlightInfo> flightInfoList = new ArrayList<FlightInfo>();
		List<PassengerInfo> passengerList = new ArrayList<PassengerInfo>();

		request.setTelephone("15210627148");

		request.setFlightInfoList(flightInfoList);
		request.setPassengerList(passengerList);

		FlightInfo flightInfo = new FlightInfo();
		flightInfo.setCabinCode("M");
		flightInfo.setCarrier("KA");
		flightInfo.setFromCity("PEK");
		flightInfo.setToCity("HKG");
		flightInfo.setFromDate("2013-10-31");
		flightInfo.setFlightNo("KA905");
		flightInfoList.add(flightInfo);

		flightInfo = new FlightInfo();
		flightInfo.setCabinCode("M");
		flightInfo.setCarrier("CX");
		flightInfo.setFromCity("HKG");
		flightInfo.setToCity("HND");
		flightInfo.setFromDate("2013-11-01");
		flightInfo.setFlightNo("CX542");
		flightInfoList.add(flightInfo);

		PassengerInfo passengerInfo = new PassengerInfo("MAO/HONG QIANG","G67682323","P","CN","2020-10-01","CN","1985-08-26","M","ADT","15210627148");
		passengerList.add(passengerInfo);
		
		passengerInfo = new PassengerInfo("LI/SI","G67682353","P","CN","2020-10-01","CN","2000-09-29","F","CHD","15210627148");
		passengerList.add(passengerInfo);

		passengerInfo = new PassengerInfo("ZHANG/SAN","G67687853","P","CN","2020-10-01","CN","2010-10-29","M","INF","15210627148");
		passengerList.add(passengerInfo);
		
		ServletContext servletContext = new MockServletContext();
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/eterm-interface-appctx.xml",
                "classpath*:spring/webApplicationContext.xml");
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);

		CreateIntPNR createPNR = new CreateIntPNR();
		
		String rqXml  = IntPnrCreateRequest.convertToXml(request);
		String rsXml = createPNR.createIntPNR(rqXml);
		
		CreatePnrReturnClass<String> rtClass = CreatePnrReturnClass.convertToObject(rsXml);

		System.out.println(rtClass);
		
		assert(rtClass.getReturnCode() == CreatePnrReturnCode.SUCCESS);
	}
	
	/**
	 * 东方航空公司
	 */
	@Test
	public void testMUCreate() throws SessionExpireException {

		IntPnrCreateRequest request = new IntPnrCreateRequest();
		List<FlightInfo> flightInfoList = new ArrayList<FlightInfo>();
		List<PassengerInfo> passengerList = new ArrayList<PassengerInfo>();

		request.setTelephone("15210627148");

		request.setFlightInfoList(flightInfoList);
		request.setPassengerList(passengerList);

		FlightInfo flightInfo = new FlightInfo();
		flightInfo.setCabinCode("E");
		flightInfo.setCarrier("MU");
		flightInfo.setFromCity("HKG");
		flightInfo.setToCity("PVG");
		flightInfo.setFromDate("2013-12-26");
		flightInfo.setFlightNo("MU726");
		flightInfoList.add(flightInfo);

		PassengerInfo passengerInfo = new PassengerInfo("MAO/QIANG","G67682323","P","CN","2020-10-01","CN","1985-08-26","M","ADT","15210627148");
		passengerList.add(passengerInfo);
		
		passengerInfo = new PassengerInfo("LI/MIAO","G67682353","P","CN","2020-10-01","CN","2000-09-29","F","CHD","15210627148");
		passengerList.add(passengerInfo);

		passengerInfo = new PassengerInfo("ZHANG/LIN","G67687853","P","CN","2020-10-01","CN","2010-10-29","M","INF","15210627148");
		passengerList.add(passengerInfo);
		
		ServletContext servletContext = new MockServletContext();
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/eterm-interface-appctx.xml",
                "classpath*:spring/webApplicationContext.xml");
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);

		CreateIntPNR createPNR = new CreateIntPNR();
		
		CreatePnrReturnClass<String> rtClass = createPNR.createIntPNR(request);

		System.out.println(rtClass);
		
		assert(rtClass.getReturnCode() == CreatePnrReturnCode.SUCCESS);
	}


	/**
	 * 上海航空公司
	 */
	@Test
	public void testFMCreate() throws SessionExpireException {

		IntPnrCreateRequest request = new IntPnrCreateRequest();
		List<FlightInfo> flightInfoList = new ArrayList<FlightInfo>();
		List<PassengerInfo> passengerList = new ArrayList<PassengerInfo>();

		request.setTelephone("15210627148");

		request.setFlightInfoList(flightInfoList);
		request.setPassengerList(passengerList);

		FlightInfo flightInfo = new FlightInfo();
		flightInfo.setCabinCode("E");
		flightInfo.setCarrier("FM");
		flightInfo.setFromCity("HKG");
		flightInfo.setToCity("PVG");
		flightInfo.setFromDate("2013-12-28");
		flightInfo.setFlightNo("FM726");
		flightInfoList.add(flightInfo);

		PassengerInfo passengerInfo = new PassengerInfo("WANG/JIE","G67682323","P","CN","2020-10-01","CN","1985-08-26","M","ADT","15210627148");
		passengerList.add(passengerInfo);
		
		passengerInfo = new PassengerInfo("SUN/MIAO","G67682353","P","CN","2020-10-01","CN","2000-09-29","F","CHD","15210627148");
		passengerList.add(passengerInfo);

		passengerInfo = new PassengerInfo("LI/LIN","G67687853","P","CN","2020-10-01","CN","2010-10-29","M","INF","15210627148");
		passengerList.add(passengerInfo);
		
		ServletContext servletContext = new MockServletContext();
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/eterm-interface-appctx.xml",
                "classpath*:spring/webApplicationContext.xml");
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);

		CreateIntPNR createPNR = new CreateIntPNR();
		
		CreatePnrReturnClass<String> rtClass = createPNR.createIntPNR(request);

		System.out.println(rtClass);
		
		assert(rtClass.getReturnCode() == CreatePnrReturnCode.SUCCESS);
	}
	
	
	/**
	 * 国泰航空公司
	 */
	@Test
	public void testCXCreate() {

		IntPnrCreateRequest request = new IntPnrCreateRequest();
		List<FlightInfo> flightInfoList = new ArrayList<FlightInfo>();
		List<PassengerInfo> passengerList = new ArrayList<PassengerInfo>();

		request.setTelephone("15210627148");

		request.setFlightInfoList(flightInfoList);
		request.setPassengerList(passengerList);

		FlightInfo flightInfo = new FlightInfo();
		flightInfo.setCabinCode("Y");
		flightInfo.setCarrier("CX");
		flightInfo.setFromCity("HKG");
		flightInfo.setToCity("PVG");
		flightInfo.setFromDate("2013-12-28");
		flightInfo.setFlightNo("CX6832");
		flightInfoList.add(flightInfo);

		PassengerInfo passengerInfo = new PassengerInfo("LI/JIE","G67682323","P","CN","2020-10-01","CN","1985-08-26","M","ADT","15210627148");
		passengerList.add(passengerInfo);
		
		passengerInfo = new PassengerInfo("WANG/MIAO","G67682353","P","CN","2020-10-01","CN","2000-09-29","F","CHD","15210627148");
		passengerList.add(passengerInfo);

		passengerInfo = new PassengerInfo("MA/LIN","G67687853","P","CN","2020-10-01","CN","2010-10-29","M","INF","15210627148");
		passengerList.add(passengerInfo);
		
		ServletContext servletContext = new MockServletContext();
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/eterm-interface-appctx.xml",
                "classpath*:spring/webApplicationContext.xml");
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);

		CreateIntPNR createPNR = new CreateIntPNR();
		
		CreatePnrReturnClass<String> rtClass = createPNR.createIntPNR(request);

		System.out.println(rtClass);
		
		assert(rtClass.getReturnCode() == CreatePnrReturnCode.SUCCESS);
	}
	
	/**
	 * 港龙航空公司
	 */
	@Test
	public void testKACreate()  throws SessionExpireException{

		IntPnrCreateRequest request = new IntPnrCreateRequest();
		List<FlightInfo> flightInfoList = new ArrayList<FlightInfo>();
		List<PassengerInfo> passengerList = new ArrayList<PassengerInfo>();

		request.setTelephone("15210627148");

		request.setFlightInfoList(flightInfoList);
		request.setPassengerList(passengerList);

		FlightInfo flightInfo = new FlightInfo();
		flightInfo.setCabinCode("Y");
		flightInfo.setCarrier("KA");
		flightInfo.setFromCity("HKG");
		flightInfo.setToCity("PVG");
		flightInfo.setFromDate("2013-12-28");
		flightInfo.setFlightNo("KA802");
		flightInfoList.add(flightInfo);

		PassengerInfo passengerInfo = new PassengerInfo("LI/JUN","G67682323","P","CN","2020-10-01","CN","1985-08-26","M","ADT","15210627148");
		passengerList.add(passengerInfo);
		
		passengerInfo = new PassengerInfo("WANG/XING","G67682353","P","CN","2020-10-01","CN","2000-09-29","F","CHD","15210627148");
		passengerList.add(passengerInfo);

		passengerInfo = new PassengerInfo("WEI/LIN","G67687853","P","CN","2020-10-01","CN","2010-10-29","M","INF","15210627148");
		passengerList.add(passengerInfo);
		
		ServletContext servletContext = new MockServletContext();
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/eterm-interface-appctx.xml",
                "classpath*:spring/webApplicationContext.xml");
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);

		CreateIntPNR createPNR = new CreateIntPNR();
		
		CreatePnrReturnClass<String> rtClass = createPNR.createIntPNR(request);

		System.out.println(rtClass);
		
		assert(rtClass.getReturnCode() == CreatePnrReturnCode.SUCCESS);
	}
	
	/**
	 * 全日空
	 */
	@Test
	public void testNHCreate() {

		IntPnrCreateRequest request = new IntPnrCreateRequest();
		List<FlightInfo> flightInfoList = new ArrayList<FlightInfo>();
		List<PassengerInfo> passengerList = new ArrayList<PassengerInfo>();

		request.setTelephone("15210627148");

		request.setFlightInfoList(flightInfoList);
		request.setPassengerList(passengerList);

		FlightInfo flightInfo = new FlightInfo();
		flightInfo.setCabinCode("B");
		flightInfo.setCarrier("NH");
		flightInfo.setFromCity("HKG");
		flightInfo.setToCity("NRT");
		flightInfo.setFromDate("2013-12-26");
		flightInfo.setFlightNo("NH912");
		flightInfoList.add(flightInfo);

		PassengerInfo passengerInfo = new PassengerInfo("MA/JUN","G67682323","P","CN","2020-10-01","CN","1985-08-26","M","ADT","15210627148");
		passengerList.add(passengerInfo);
		
		passengerInfo = new PassengerInfo("LIU/XING","G67682353","P","CN","2020-10-01","CN","2000-09-29","F","CHD","15210627148");
		passengerList.add(passengerInfo);

		passengerInfo = new PassengerInfo("SUN/LI","G67687853","P","CN","2020-10-01","CN","2010-10-29","M","INF","15210627148");
		passengerList.add(passengerInfo);
		
		ServletContext servletContext = new MockServletContext();
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/eterm-interface-appctx.xml",
                "classpath*:spring/webApplicationContext.xml");
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);

		CreateIntPNR createPNR = new CreateIntPNR();
		
		CreatePnrReturnClass<String> rtClass = createPNR.createIntPNR(request);

		System.out.println(rtClass);
		
		assert(rtClass.getReturnCode() == CreatePnrReturnCode.SUCCESS);
	}
	
	
	/**
	 * 日本航空公司
	 */
	@Test
	public void testJLCreate()  throws SessionExpireException{

		IntPnrCreateRequest request = new IntPnrCreateRequest();
		List<FlightInfo> flightInfoList = new ArrayList<FlightInfo>();
		List<PassengerInfo> passengerList = new ArrayList<PassengerInfo>();

		request.setTelephone("15210627148");

		request.setFlightInfoList(flightInfoList);
		request.setPassengerList(passengerList);

		FlightInfo flightInfo = new FlightInfo();
		flightInfo.setCabinCode("B");
		flightInfo.setCarrier("JL");
		flightInfo.setFromCity("HKG");
		flightInfo.setToCity("NRT");
		flightInfo.setFromDate("2013-12-26");
		flightInfo.setFlightNo("JL736");
		flightInfoList.add(flightInfo);

		PassengerInfo passengerInfo = new PassengerInfo("MA/QIANG","G67682323","P","CN","2020-10-01","CN","1985-08-26","M","ADT","15210627148");
		passengerList.add(passengerInfo);
		
		passengerInfo = new PassengerInfo("LIU/JUN","G67682353","P","CN","2020-10-01","CN","2000-09-29","F","CHD","15210627148");
		passengerList.add(passengerInfo);

		passengerInfo = new PassengerInfo("SUN/PING","G67687853","P","CN","2020-10-01","CN","2010-10-29","M","INF","15210627148");
		passengerList.add(passengerInfo);
		
		ServletContext servletContext = new MockServletContext();
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/eterm-interface-appctx.xml",
                "classpath*:spring/webApplicationContext.xml");
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);

		CreateIntPNR createPNR = new CreateIntPNR();
		
		CreatePnrReturnClass<String> rtClass = createPNR.createIntPNR(request);

		System.out.println(rtClass);
		
		assert(rtClass.getReturnCode() == CreatePnrReturnCode.SUCCESS);
	}
	
	
	/**
	 * 大韩航空公司
	 * @throws SessionExpireException 
	 */
	@Test
	public void testKECreate() throws SessionExpireException {

		IntPnrCreateRequest request = new IntPnrCreateRequest();
		List<FlightInfo> flightInfoList = new ArrayList<FlightInfo>();
		List<PassengerInfo> passengerList = new ArrayList<PassengerInfo>();

		request.setTelephone("15210627148");

		request.setFlightInfoList(flightInfoList);
		request.setPassengerList(passengerList);

		FlightInfo flightInfo = new FlightInfo();
		flightInfo.setCabinCode("Y");
		flightInfo.setCarrier("KE");
		flightInfo.setFromCity("HKG");
		flightInfo.setToCity("ICN");
		flightInfo.setFromDate("2013-12-26");
		flightInfo.setFlightNo("KE608");
		flightInfoList.add(flightInfo);
		
		flightInfo = new FlightInfo();
		flightInfo.setCabinCode("Y");
		flightInfo.setCarrier("KE");
		flightInfo.setFromCity("GMP");
		flightInfo.setToCity("HND");
		flightInfo.setFromDate("2013-12-26");
		flightInfo.setFlightNo("KE2709");
		flightInfoList.add(flightInfo);

		PassengerInfo passengerInfo = new PassengerInfo("MA/MIN","G67682323","P","CN","2020-10-01","CN","1985-08-26","M","ADT","15210627148");
		passengerList.add(passengerInfo);
		
		passengerInfo = new PassengerInfo("LIU/BING","G67682353","P","CN","2020-10-01","CN","2000-09-29","F","CHD","15210627148");
		passengerList.add(passengerInfo);

		passengerInfo = new PassengerInfo("SUN/CHAO","G67687853","P","CN","2020-10-01","CN","2010-10-29","M","INF","15210627148");
		passengerList.add(passengerInfo);
		
		ServletContext servletContext = new MockServletContext();
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/eterm-interface-appctx.xml",
                "classpath*:spring/webApplicationContext.xml");
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);

		CreateIntPNR createPNR = new CreateIntPNR();
		
		CreatePnrReturnClass<String> rtClass = createPNR.createIntPNR(request);

		System.out.println(rtClass);
		
		assert(rtClass.getReturnCode() == CreatePnrReturnCode.SUCCESS);
	}
	
	/**
	 *  中华航空公司
	 */
	@Test
	public void testCICreate() throws SessionExpireException {

		IntPnrCreateRequest request = new IntPnrCreateRequest();
		List<FlightInfo> flightInfoList = new ArrayList<FlightInfo>();
		List<PassengerInfo> passengerList = new ArrayList<PassengerInfo>();

		request.setTelephone("15210627148");

		request.setFlightInfoList(flightInfoList);
		request.setPassengerList(passengerList);

		FlightInfo flightInfo = new FlightInfo();
		flightInfo.setCabinCode("T");
		flightInfo.setCarrier("CI");
		flightInfo.setFromCity("HKG");
		flightInfo.setToCity("TPE");
		flightInfo.setFromDate("2013-12-26");
		flightInfo.setFlightNo("CI922");
		flightInfoList.add(flightInfo);
		
		flightInfo = new FlightInfo();
		flightInfo.setCabinCode("T");
		flightInfo.setCarrier("CI");
		flightInfo.setFromCity("TPE");
		flightInfo.setToCity("NRT");
		flightInfo.setFromDate("2013-12-26");
		flightInfo.setFlightNo("CI106");
		flightInfoList.add(flightInfo);

		PassengerInfo passengerInfo = new PassengerInfo("MA/CHENG","G67682323","P","CN","2020-10-01","CN","1985-08-26","M","ADT","15210627148");
		passengerList.add(passengerInfo);
		
		passengerInfo = new PassengerInfo("ZHAO/JUN","G67682353","P","CN","2020-10-01","CN","2000-09-29","F","CHD","15210627148");
		passengerList.add(passengerInfo);

		passengerInfo = new PassengerInfo("WANG/PING","G67687853","P","CN","2020-10-01","CN","2010-10-29","M","INF","15210627148");
		passengerList.add(passengerInfo);
		
		ServletContext servletContext = new MockServletContext();
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/eterm-interface-appctx.xml",
                "classpath*:spring/webApplicationContext.xml");
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);

		CreateIntPNR createPNR = new CreateIntPNR();
		
		CreatePnrReturnClass<String> rtClass = createPNR.createIntPNR(request);

		System.out.println(rtClass);
		
		assert(rtClass.getReturnCode() == CreatePnrReturnCode.SUCCESS);
	}
	
	/**
	 *  中国国际航空
	 * @throws SessionExpireException 
	 */
	@Test
	public void testCACreate() throws SessionExpireException {

		IntPnrCreateRequest request = new IntPnrCreateRequest();
		List<FlightInfo> flightInfoList = new ArrayList<FlightInfo>();
		List<PassengerInfo> passengerList = new ArrayList<PassengerInfo>();

		request.setTelephone("15210627148");

		request.setFlightInfoList(flightInfoList);
		request.setPassengerList(passengerList);

		FlightInfo flightInfo = new FlightInfo();
		flightInfo.setCabinCode("G");
		flightInfo.setCarrier("CA");
		flightInfo.setFromCity("HKG");
		flightInfo.setToCity("PEK");
		flightInfo.setFromDate("2013-12-26");
		flightInfo.setFlightNo("CA118");
		flightInfoList.add(flightInfo);
		
		flightInfo = new FlightInfo();
		flightInfo.setCabinCode("G");
		flightInfo.setCarrier("CA");
		flightInfo.setFromCity("PEK");
		flightInfo.setToCity("HND");
		flightInfo.setFromDate("2013-12-26");
		flightInfo.setFlightNo("CA183");
		flightInfoList.add(flightInfo);

		PassengerInfo passengerInfo = new PassengerInfo("MA/CHENG","G67682323","P","CN","2020-10-01","CN","1985-08-26","M","ADT","15210627148");
		passengerList.add(passengerInfo);
		
		passengerInfo = new PassengerInfo("ZHAO/JUN","G67682353","P","CN","2020-10-01","CN","2000-09-29","F","CHD","15210627148");
		passengerList.add(passengerInfo);

		passengerInfo = new PassengerInfo("WANG/PING","G67687853","P","CN","2020-10-01","CN","2010-10-29","M","INF","15210627148");
		passengerList.add(passengerInfo);
		
		ServletContext servletContext = new MockServletContext();
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/eterm-interface-appctx.xml",
                "classpath*:spring/webApplicationContext.xml");
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);

		CreateIntPNR createPNR = new CreateIntPNR();
		
		CreatePnrReturnClass<String> rtClass = createPNR.createIntPNR(request);

		System.out.println(rtClass);
		
		assert(rtClass.getReturnCode() == CreatePnrReturnCode.SUCCESS);
	}
	
	
	/**
	 *   加拿大航空(婴儿有问题)
	 * @throws SessionExpireException 
	 */
	@Test
	public void testACCreate() throws SessionExpireException {

		IntPnrCreateRequest request = new IntPnrCreateRequest();
		List<FlightInfo> flightInfoList = new ArrayList<FlightInfo>();
		List<PassengerInfo> passengerList = new ArrayList<PassengerInfo>();

		request.setTelephone("15210627148");

		request.setFlightInfoList(flightInfoList);
		request.setPassengerList(passengerList);

		FlightInfo flightInfo = new FlightInfo();
		flightInfo.setCabinCode("T");
		flightInfo.setCarrier("AC");
		flightInfo.setFromCity("HKG");
		flightInfo.setToCity("YYZ");
		flightInfo.setFromDate("2013-12-26");
		flightInfo.setFlightNo("AC16");
		flightInfoList.add(flightInfo);
		
		flightInfo = new FlightInfo();
		flightInfo.setCabinCode("N");
		flightInfo.setCarrier("AC");
		flightInfo.setFromCity("YYZ");
		flightInfo.setToCity("YXU");
		flightInfo.setFromDate("2013-12-26");
		flightInfo.setFlightNo("AC7723");
		flightInfoList.add(flightInfo);

		PassengerInfo passengerInfo = new PassengerInfo("MA/MING","G67682323","P","CN","2020-10-01","CN","1985-08-26","M","ADT","15210627148");
		passengerList.add(passengerInfo);
		
		passengerInfo = new PassengerInfo("ZHU/JUN","G67682353","P","CN","2020-10-01","CN","2000-09-29","F","CHD","15210627148");
		passengerList.add(passengerInfo);

		passengerInfo = new PassengerInfo("LI/SI","G67687853","P","CN","2020-10-01","CN","2013-10-29","M","INF","15210627148");
		passengerList.add(passengerInfo);
		
		ServletContext servletContext = new MockServletContext();
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/eterm-interface-appctx.xml",
                "classpath*:spring/webApplicationContext.xml");
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);

		CreateIntPNR createPNR = new CreateIntPNR();
		
		CreatePnrReturnClass<String> rtClass = createPNR.createIntPNR(request);

		System.out.println(rtClass);
		
		assert(rtClass.getReturnCode() == CreatePnrReturnCode.SUCCESS);
	}
	
	
	/**
	 *    阿联酋航空
	 */
	@Test
	public void testEKCreate() throws SessionExpireException {

		IntPnrCreateRequest request = new IntPnrCreateRequest();
		List<FlightInfo> flightInfoList = new ArrayList<FlightInfo>();
		List<PassengerInfo> passengerList = new ArrayList<PassengerInfo>();

		request.setTelephone("15210627148");

		request.setFlightInfoList(flightInfoList);
		request.setPassengerList(passengerList);

		FlightInfo flightInfo = new FlightInfo();
		flightInfo.setCabinCode("U");
		flightInfo.setCarrier("EK");
		flightInfo.setFromCity("HKG");
		flightInfo.setToCity("DXB");
		flightInfo.setFromDate("2013-12-26");
		flightInfo.setFlightNo("EK381");
		flightInfoList.add(flightInfo);
		
		flightInfo = new FlightInfo();
		flightInfo.setCabinCode("U");
		flightInfo.setCarrier("EK");
		flightInfo.setFromCity("DXB");
		flightInfo.setToCity("LAX");
		flightInfo.setFromDate("2013-12-26");
		flightInfo.setFlightNo("EK215");
		flightInfoList.add(flightInfo);

		PassengerInfo passengerInfo = new PassengerInfo("MA/MING","G67682323","P","CN","2020-10-01","CN","1985-08-26","M","ADT","15210627148");
		passengerList.add(passengerInfo);
		
		passengerInfo = new PassengerInfo("ZHU/JUN","G67682353","P","CN","2020-10-01","CN","2000-09-29","F","CHD","15210627148");
		passengerList.add(passengerInfo);

		passengerInfo = new PassengerInfo("WANG/PING","G67687853","P","CN","2020-10-01","CN","2010-10-29","M","INF","15210627148");
		passengerList.add(passengerInfo);
		
		ServletContext servletContext = new MockServletContext();
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/eterm-interface-appctx.xml",
                "classpath*:spring/webApplicationContext.xml");
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);

		CreateIntPNR createPNR = new CreateIntPNR();
		
		CreatePnrReturnClass<String> rtClass = createPNR.createIntPNR(request);

		System.out.println(rtClass);
		
		assert(rtClass.getReturnCode() == CreatePnrReturnCode.SUCCESS);
	}
	
	
	/**
	 *  菲律宾航空
	 */
	@Test
	public void testPRCreate()  throws SessionExpireException{

		IntPnrCreateRequest request = new IntPnrCreateRequest();
		List<FlightInfo> flightInfoList = new ArrayList<FlightInfo>();
		List<PassengerInfo> passengerList = new ArrayList<PassengerInfo>();

		request.setTelephone("15210627148");

		request.setFlightInfoList(flightInfoList);
		request.setPassengerList(passengerList);

		FlightInfo flightInfo = new FlightInfo();
		flightInfo.setCabinCode("H");
		flightInfo.setCarrier("PR");
		flightInfo.setFromCity("HKG");
		flightInfo.setToCity("MNL");
		flightInfo.setFromDate("2013-12-26");
		flightInfo.setFlightNo("PR313");
		flightInfoList.add(flightInfo);
		
		flightInfo = new FlightInfo();
		flightInfo.setCabinCode("H");
		flightInfo.setCarrier("PR");
		flightInfo.setFromCity("MNL");
		flightInfo.setToCity("LAX");
		flightInfo.setFromDate("2013-12-26");
		flightInfo.setFlightNo("PR102");
		flightInfoList.add(flightInfo);

		PassengerInfo passengerInfo = new PassengerInfo("WANG/MING","G67682323","P","CN","2020-10-01","CN","1985-08-26","M","ADT","15210627148");
		passengerList.add(passengerInfo);
		
		passengerInfo = new PassengerInfo("FU/JUN","G67682353","P","CN","2020-10-01","CN","2000-09-29","F","CHD","15210627148");
		passengerList.add(passengerInfo);

		passengerInfo = new PassengerInfo("JIA/PING","G67687853","P","CN","2020-10-01","CN","2010-10-29","M","INF","15210627148");
		passengerList.add(passengerInfo);
		
		ServletContext servletContext = new MockServletContext();
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/eterm-interface-appctx.xml",
                "classpath*:spring/webApplicationContext.xml");
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);

		CreateIntPNR createPNR = new CreateIntPNR();
		
		CreatePnrReturnClass<String> rtClass = createPNR.createIntPNR(request);

		System.out.println(rtClass);
		
		assert(rtClass.getReturnCode() == CreatePnrReturnCode.SUCCESS);
	}
	
	
	
	/**
	 *  达美航空
	 */
	@Test
	public void testDLCreate() throws SessionExpireException {

		IntPnrCreateRequest request = new IntPnrCreateRequest();
		List<FlightInfo> flightInfoList = new ArrayList<FlightInfo>();
		List<PassengerInfo> passengerList = new ArrayList<PassengerInfo>();

		request.setTelephone("15210627148");

		request.setFlightInfoList(flightInfoList);
		request.setPassengerList(passengerList);

		FlightInfo flightInfo = new FlightInfo();
		flightInfo.setCabinCode("M");
		flightInfo.setCarrier("DL");
		flightInfo.setFromCity("HKG");
		flightInfo.setToCity("NRT");
		flightInfo.setFromDate("2013-12-26");
		flightInfo.setFlightNo("DL156");
		flightInfoList.add(flightInfo);
		
		flightInfo = new FlightInfo();
		flightInfo.setCabinCode("M");
		flightInfo.setCarrier("DL");
		flightInfo.setFromCity("NRT");
		flightInfo.setToCity("LAX");
		flightInfo.setFromDate("2013-12-26");
		flightInfo.setFlightNo("DL284");
		flightInfoList.add(flightInfo);

		PassengerInfo passengerInfo = new PassengerInfo("LI/DA","G67682323","P","CN","2020-10-01","CN","1985-08-26","M","ADT","15210627148");
		passengerList.add(passengerInfo);
		
		passengerInfo = new PassengerInfo("ZHOU/MING","G67682353","P","CN","2020-10-01","CN","2000-09-29","F","CHD","15210627148");
		passengerList.add(passengerInfo);

		passengerInfo = new PassengerInfo("ZHOU/PING","G67687853","P","CN","2020-10-01","CN","2010-10-29","M","INF","15210627148");
		passengerList.add(passengerInfo);
		
		ServletContext servletContext = new MockServletContext();
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/eterm-interface-appctx.xml",
                "classpath*:spring/webApplicationContext.xml");
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);

		CreateIntPNR createPNR = new CreateIntPNR();
		
		CreatePnrReturnClass<String> rtClass = createPNR.createIntPNR(request);

		System.out.println(rtClass);
		
		assert(rtClass.getReturnCode() == CreatePnrReturnCode.SUCCESS);
	}
	
	
	
	/**
	 *  卡塔尔航空
	 */
	@Test
	public void testQRCreate() throws SessionExpireException {

		IntPnrCreateRequest request = new IntPnrCreateRequest();
		List<FlightInfo> flightInfoList = new ArrayList<FlightInfo>();
		List<PassengerInfo> passengerList = new ArrayList<PassengerInfo>();

		request.setTelephone("15210627148");

		request.setFlightInfoList(flightInfoList);
		request.setPassengerList(passengerList);

		FlightInfo flightInfo = new FlightInfo();
		flightInfo.setCabinCode("V");
		flightInfo.setCarrier("QR");
		flightInfo.setFromCity("HKG");
		flightInfo.setToCity("DOH");
		flightInfo.setFromDate("2013-12-26");
		flightInfo.setFlightNo("QR817");
		flightInfoList.add(flightInfo);
		
		flightInfo = new FlightInfo();
		flightInfo.setCabinCode("V");
		flightInfo.setCarrier("QR");
		flightInfo.setFromCity("DOH");
		flightInfo.setToCity("BAH");
		flightInfo.setFromDate("2013-12-27");
		flightInfo.setFlightNo("QR1108");
		flightInfoList.add(flightInfo);

		PassengerInfo passengerInfo = new PassengerInfo("LI/DA","G67682323","P","CN","2020-10-01","CN","1985-08-26","M","ADT","15210627148");
		passengerList.add(passengerInfo);
		
		passengerInfo = new PassengerInfo("ZHOU/MING","G67682353","P","CN","2020-10-01","CN","2000-09-29","F","CHD","15210627148");
		passengerList.add(passengerInfo);

		passengerInfo = new PassengerInfo("ZHOU/PING","G67687853","P","CN","2020-10-01","CN","2010-10-29","M","INF","15210627148");
		passengerList.add(passengerInfo);
		
		ServletContext servletContext = new MockServletContext();
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/eterm-interface-appctx.xml",
                "classpath*:spring/webApplicationContext.xml");
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);

		CreateIntPNR createPNR = new CreateIntPNR();
		
		CreatePnrReturnClass<String> rtClass = createPNR.createIntPNR(request);

		System.out.println(rtClass);
		
		assert(rtClass.getReturnCode() == CreatePnrReturnCode.SUCCESS);
	}
	
	
	/**
	 * 印度航空
	 */
	@Test
	public void testAICreate() {

		IntPnrCreateRequest request = new IntPnrCreateRequest();
		List<FlightInfo> flightInfoList = new ArrayList<FlightInfo>();
		List<PassengerInfo> passengerList = new ArrayList<PassengerInfo>();

		request.setTelephone("15210627148");

		request.setFlightInfoList(flightInfoList);
		request.setPassengerList(passengerList);

		FlightInfo flightInfo = new FlightInfo();
		flightInfo.setCabinCode("Y");
		flightInfo.setCarrier("AI");
		flightInfo.setFromCity("HKG");
		flightInfo.setToCity("DEL");
		flightInfo.setFromDate("2013-12-26");
		flightInfo.setFlightNo("AI315");
		flightInfoList.add(flightInfo);
		
		flightInfo = new FlightInfo();
		flightInfo.setCabinCode("U");
		flightInfo.setCarrier("AI");
		flightInfo.setFromCity("DEL");
		flightInfo.setToCity("BAH");
		flightInfo.setFromDate("2013-12-27");
		flightInfo.setFlightNo("AI941");
		flightInfoList.add(flightInfo);

		PassengerInfo passengerInfo = new PassengerInfo("LI/MING","G67682323","P","CN","2020-10-01","CN","1985-08-26","M","ADT","15210627148");
		passengerList.add(passengerInfo);
		
		passengerInfo = new PassengerInfo("ZHOU/JUAN","G67682353","P","CN","2020-10-01","CN","2000-09-29","F","CHD","15210627148");
		passengerList.add(passengerInfo);

		passengerInfo = new PassengerInfo("CUI/PING","G67687853","P","CN","2020-10-01","CN","2010-10-29","M","INF","15210627148");
		passengerList.add(passengerInfo);
		
		ServletContext servletContext = new MockServletContext();
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/eterm-interface-appctx.xml",
                "classpath*:spring/webApplicationContext.xml");
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);

		CreateIntPNR createPNR = new CreateIntPNR();
		
		CreatePnrReturnClass<String> rtClass = createPNR.createIntPNR(request);

		System.out.println(rtClass);
		
		assert(rtClass.getReturnCode() == CreatePnrReturnCode.SUCCESS);
	}
	
	/**
	 * 新加坡航空
	 */
	@Test
	public void testSQCreate() {

		IntPnrCreateRequest request = new IntPnrCreateRequest();
		List<FlightInfo> flightInfoList = new ArrayList<FlightInfo>();
		List<PassengerInfo> passengerList = new ArrayList<PassengerInfo>();

		request.setTelephone("15210627148");

		request.setFlightInfoList(flightInfoList);
		request.setPassengerList(passengerList);

		FlightInfo flightInfo = new FlightInfo();
		flightInfo.setCabinCode("S");
		flightInfo.setCarrier("SQ");
		flightInfo.setFromCity("HKG");
		flightInfo.setToCity("SIN");
		flightInfo.setFromDate("2013-12-26");
		flightInfo.setFlightNo("SQ857");
		flightInfoList.add(flightInfo);
		
		flightInfo = new FlightInfo();
		flightInfo.setCabinCode("S");
		flightInfo.setCarrier("SQ");
		flightInfo.setFromCity("SIN");
		flightInfo.setToCity("DXB");
		flightInfo.setFromDate("2013-12-26");
		flightInfo.setFlightNo("SQ494");
		flightInfoList.add(flightInfo);
		
		flightInfo = new FlightInfo();
		flightInfo.setCabinCode("Y");
		flightInfo.setCarrier("GF");
		flightInfo.setFromCity("DXB");
		flightInfo.setToCity("BAH");
		flightInfo.setFromDate("2013-12-26");
		flightInfo.setFlightNo("GF511");
		flightInfoList.add(flightInfo);

		PassengerInfo passengerInfo = new PassengerInfo("WANG/MIN","G67682323","P","CN","2020-10-01","CN","1985-08-26","M","ADT","15210627148");
		passengerList.add(passengerInfo);
		
		passengerInfo = new PassengerInfo("LI/JUAN","G67682353","P","CN","2020-10-01","CN","2000-09-29","F","CHD","15210627148");
		passengerList.add(passengerInfo);

		passengerInfo = new PassengerInfo("MA/XING","G67687853","P","CN","2020-10-01","CN","2010-10-29","M","INF","15210627148");
		passengerList.add(passengerInfo);
		
		ServletContext servletContext = new MockServletContext();
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/eterm-interface-appctx.xml",
                "classpath*:spring/webApplicationContext.xml");
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);

		CreateIntPNR createPNR = new CreateIntPNR();
		
		CreatePnrReturnClass<String> rtClass = createPNR.createIntPNR(request);

		System.out.println(rtClass);
		
		assert(rtClass.getReturnCode() == CreatePnrReturnCode.SUCCESS);
	}
	
	
	/**
	 * 约旦皇家航空
	 */
	@Test
	public void testRJCreate() {

		IntPnrCreateRequest request = new IntPnrCreateRequest();
		List<FlightInfo> flightInfoList = new ArrayList<FlightInfo>();
		List<PassengerInfo> passengerList = new ArrayList<PassengerInfo>();

		request.setTelephone("15210627148");

		request.setFlightInfoList(flightInfoList);
		request.setPassengerList(passengerList);

		FlightInfo flightInfo = new FlightInfo();
		flightInfo.setCabinCode("M");
		flightInfo.setCarrier("RJ");
		flightInfo.setFromCity("HKG");
		flightInfo.setToCity("AMM");
		flightInfo.setFromDate("2013-12-26");
		flightInfo.setFlightNo("RJ183");
		flightInfoList.add(flightInfo);
		
		flightInfo = new FlightInfo();
		flightInfo.setCabinCode("M");
		flightInfo.setCarrier("RJ");
		flightInfo.setFromCity("AMM");
		flightInfo.setToCity("BAH");
		flightInfo.setFromDate("2013-12-27");
		flightInfo.setFlightNo("RJ3301");
		flightInfoList.add(flightInfo);

		PassengerInfo passengerInfo = new PassengerInfo("WANG/MIN","G67682323","P","CN","2020-10-01","CN","1985-08-26","M","ADT","15210627148");
		passengerList.add(passengerInfo);
		
		passengerInfo = new PassengerInfo("LI/JUAN","G67682353","P","CN","2020-10-01","CN","2000-09-29","F","CHD","15210627148");
		passengerList.add(passengerInfo);

		passengerInfo = new PassengerInfo("MA/XING","G67687853","P","CN","2020-10-01","CN","2010-10-29","M","INF","15210627148");
		passengerList.add(passengerInfo);
		
		ServletContext servletContext = new MockServletContext();
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/eterm-interface-appctx.xml",
                "classpath*:spring/webApplicationContext.xml");
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);

		CreateIntPNR createPNR = new CreateIntPNR();
		
		CreatePnrReturnClass<String> rtClass = createPNR.createIntPNR(request);

		System.out.println(rtClass);
		
		assert(rtClass.getReturnCode() == CreatePnrReturnCode.SUCCESS);
	}
	
	
	/**
	 * 土耳其航空
	 */
	@Test
	public void testTKCreate() {

		IntPnrCreateRequest request = new IntPnrCreateRequest();
		List<FlightInfo> flightInfoList = new ArrayList<FlightInfo>();
		List<PassengerInfo> passengerList = new ArrayList<PassengerInfo>();

		request.setTelephone("15210627148");

		request.setFlightInfoList(flightInfoList);
		request.setPassengerList(passengerList);

		FlightInfo flightInfo = new FlightInfo();
		flightInfo.setCabinCode("Q");
		flightInfo.setCarrier("TK");
		flightInfo.setFromCity("HKG");
		flightInfo.setToCity("IST");
		flightInfo.setFromDate("2013-12-26");
		flightInfo.setFlightNo("TK71");
		flightInfoList.add(flightInfo);
		
		flightInfo = new FlightInfo();
		flightInfo.setCabinCode("Q");
		flightInfo.setCarrier("TK");
		flightInfo.setFromCity("IST");
		flightInfo.setToCity("BAH");
		flightInfo.setFromDate("2013-12-27");
		flightInfo.setFlightNo("TK778");
		flightInfoList.add(flightInfo);

		PassengerInfo passengerInfo = new PassengerInfo("WANG/MING","G67682323","P","CN","2020-10-01","CN","1985-08-26","M","ADT","15210627148");
		passengerList.add(passengerInfo);
		
		passengerInfo = new PassengerInfo("LI/JUAN","G67682353","P","CN","2020-10-01","CN","2000-09-29","F","CHD","15210627148");
		passengerList.add(passengerInfo);

		passengerInfo = new PassengerInfo("MA/XING","G67687853","P","CN","2020-10-01","CN","2010-10-29","M","INF","15210627148");
		passengerList.add(passengerInfo);
		
		ServletContext servletContext = new MockServletContext();
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/eterm-interface-appctx.xml",
                "classpath*:spring/webApplicationContext.xml");
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);

		CreateIntPNR createPNR = new CreateIntPNR();
		
		CreatePnrReturnClass<String> rtClass = createPNR.createIntPNR(request);

		System.out.println(rtClass);
		
		assert(rtClass.getReturnCode() == CreatePnrReturnCode.SUCCESS);
	}
	
	
	/**
	 * 印度捷特航空(航空号如果是两位，则前面加0)
	 */
	@Test
	public void test9WCreate() {

		IntPnrCreateRequest request = new IntPnrCreateRequest();
		List<FlightInfo> flightInfoList = new ArrayList<FlightInfo>();
		List<PassengerInfo> passengerList = new ArrayList<PassengerInfo>();

		request.setTelephone("15210627148");

		request.setFlightInfoList(flightInfoList);
		request.setPassengerList(passengerList);

		FlightInfo flightInfo = new FlightInfo();
		flightInfo.setCabinCode("N");
		flightInfo.setCarrier("9W");
		flightInfo.setFromCity("HKG");
		flightInfo.setToCity("DEL");
		flightInfo.setFromDate("2013-12-26");
		flightInfo.setFlightNo("9W77");
		flightInfoList.add(flightInfo);
		
		flightInfo = new FlightInfo();
		flightInfo.setCabinCode("T");
		flightInfo.setCarrier("9W");
		flightInfo.setFromCity("DEL");
		flightInfo.setToCity("BOM");
		flightInfo.setFromDate("2013-12-27");
		flightInfo.setFlightNo("9W304");
		flightInfoList.add(flightInfo);
		
		flightInfo = new FlightInfo();
		flightInfo.setCabinCode("S");
		flightInfo.setCarrier("9W");
		flightInfo.setFromCity("BOM");
		flightInfo.setToCity("BAH");
		flightInfo.setFromDate("2013-12-27");
		flightInfo.setFlightNo("9W592");
		flightInfoList.add(flightInfo);

		PassengerInfo passengerInfo = new PassengerInfo("WANG/MING","G67682323","P","CN","2020-10-01","CN","1985-08-26","M","ADT","15210627148");
		passengerList.add(passengerInfo);
		
		passengerInfo = new PassengerInfo("LI/JUAN","G67682353","P","CN","2020-10-01","CN","2000-09-29","F","CHD","15210627148");
		passengerList.add(passengerInfo);

		passengerInfo = new PassengerInfo("MA/XING","G67687853","P","CN","2020-10-01","CN","2010-10-29","M","INF","15210627148");
		passengerList.add(passengerInfo);
		
		ServletContext servletContext = new MockServletContext();
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/eterm-interface-appctx.xml",
                "classpath*:spring/webApplicationContext.xml");
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);

		CreateIntPNR createPNR = new CreateIntPNR();
		
		CreatePnrReturnClass<String> rtClass = createPNR.createIntPNR(request);

		System.out.println(rtClass);
		
		assert(rtClass.getReturnCode() == CreatePnrReturnCode.SUCCESS);
	}
}
