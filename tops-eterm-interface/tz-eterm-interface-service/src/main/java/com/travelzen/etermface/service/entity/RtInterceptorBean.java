package com.travelzen.etermface.service.entity;

import java.util.ArrayList;
import java.util.List;

import com.travelzen.etermface.service.ticket.RtAfterParserHandler;
import com.travelzen.etermface.service.ticket.RtBeforeParserHandler;

public class RtInterceptorBean {
	
	public  List<RtBeforeParserHandler> rtParserBeforeHandlerList = new ArrayList<>();
	public  List<RtAfterParserHandler> rtParserAfterHandlerList   = new ArrayList<>();
	 

}
