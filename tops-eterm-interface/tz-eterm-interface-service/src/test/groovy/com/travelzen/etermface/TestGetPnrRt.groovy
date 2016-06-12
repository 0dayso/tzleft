package com.travelzen.etermface

import com.travelzen.etermface.service.EtermWebClient
import com.travelzen.etermface.service.PNRParser
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.travelzen.etermface.service.PNRParser.FlightResult
import com.travelzen.etermface.service.handler.RtBeforeParserHandler
import com.travelzen.etermface.service.ticket.TicketExtractorHandler;
import com.travelzen.framework.core.common.ReturnClass
import com.travelzen.framework.core.util.YRUtil

/**
 * @author liang.wang
 *
 */
class TestGetPnrRt {

	private static Logger logger = LoggerFactory.getLogger(PNRParser.class);



	static main(args) {

		PNRParser  parser = new PNRParser()

		EtermWebClient client = new EtermWebClient()

		String pnr = "HMFEES"

		pnr = pnr.toUpperCase()

		try {
			client.connect()
		}
		catch (Throwable e) {
			logger.error("clientErr:{}", YRUtil.stringifyException(e))
		}

		// 先取出第一页的内容
		ReturnClass<String> rtRet = client.getRT(pnr, false)


		FlightResult flightResult = new FlightResult()

		List<RtBeforeParserHandler> rtParserHandlerList = new ArrayList()
		
		rtParserHandlerList.add( new TicketExtractorHandler());
		
		String rtContent = rtRet.getObject(); 
		
		println rtContent

		parser.parseRT(rtContent , pnr, rtParserHandlerList)


		//		String xml = "";
		//		try {
		//			Pair<String, String> ret = new PNRParser().parsePnrContent(pnrContent);
		//
		//			System.out.println(ret.getValue0());
		//			System.out.println(ret.getValue1());
		//		} catch (BizException e) {
		//			logger.error("{}", YRUtil.stringifyException(e));
		//		}
	}
}
