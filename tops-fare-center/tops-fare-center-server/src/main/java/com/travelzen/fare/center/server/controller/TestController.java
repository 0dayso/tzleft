package com.travelzen.fare.center.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Description
 * <p>
 * @author yiming.yan
 * @Date Nov 24, 2015
 */
@Controller
public class TestController {
	
	@RequestMapping("test")
	@ResponseBody
	public String test() {
		return "TTT";
	}

}
