package com.travelzen.farerule.mongo.front.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.travelzen.fare.RuleInfo;
import com.travelzen.farerule.mongo.morphia.RuleInfoMorphia;

@Controller
@RequestMapping("/ruleInfo")
public class RuleInfoController {
	
	private static RuleInfoMorphia morphia = RuleInfoMorphia.Instance;

	@RequestMapping(value = "/count", method = RequestMethod.POST)
	public ModelAndView count() {
		return new ModelAndView("ruleInfo", "count_result", "Sum:" + morphia.count());
	}
	
	@RequestMapping(value = "/find", method = RequestMethod.POST)
	public ModelAndView find(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String id = request.getParameter("id");
		if (id.equals(""))
			return new ModelAndView("ruleInfo", "find_result", "ID is null!");
		RuleInfo ruleInfo = morphia.findByRuleInfoId(id);
		if (ruleInfo == null)
			return new ModelAndView("ruleInfo", "find_result", "ID not exist!");
		Map<String, String> modelMap = new HashMap<String, String>();
		modelMap.put("find_result_id", ruleInfo.getId());
		modelMap.put("find_result_text", ruleInfo.toString());
		return new ModelAndView("ruleInfo", modelMap);
	}
	
	@RequestMapping(value = "/remove", method = RequestMethod.POST)
	public ModelAndView remove(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String id = request.getParameter("id");
		if (id.equals(""))
			return new ModelAndView("ruleInfo", "remove_result", "ID is null!");
		if (morphia.findByRuleInfoId(id) == null)
			return new ModelAndView("ruleInfo", "remove_result", "ID not exist!");
		morphia.deleteByRuleInfoId(id);
		return new ModelAndView("ruleInfo", "remove_result", "Success!");
	}
	
	@RequestMapping(value = "/removeAll", method = RequestMethod.POST)
	public ModelAndView removeAll() {
		morphia.deleteAll();
		return new ModelAndView("ruleInfo", "remove_all_result", "Success!");
	}
	
	@RequestMapping(value = "/findAll", method = RequestMethod.POST)
	public ModelAndView findAll(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (morphia.count() == 0)
			return new ModelAndView("ruleInfo", "find_all_result", "DB is Empty!");
		StringBuilder allResult = new StringBuilder();
		List<RuleInfo> ruleInfoList = morphia.findAll();
		for (RuleInfo ruleInfo:ruleInfoList) {
			allResult.append("<tr><td>").append(ruleInfo.getId()).append("</td><td>")
				.append(ruleInfo.getOriginalRuleInfoId()).append("</td></tr>");
		}
		return new ModelAndView("ruleInfo", "find_all_result", 
				"Success!<br/><br/><table border=\"0\"><th>RuleInfo ID</th><th>OriginalRule ID</th>" + allResult);
	}
}
