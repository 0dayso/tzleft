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

import com.travelzen.farerule.mongo.OriginalRule;
import com.travelzen.farerule.mongo.morphia.OriginalRuleMorphia;

@Controller
@RequestMapping("/originalRule")
public class OriginalRuleController {
	
	private static OriginalRuleMorphia morphia = OriginalRuleMorphia.Instance;

	@RequestMapping(value = "/count", method = RequestMethod.POST)
	public ModelAndView count() {
		return new ModelAndView("originalRule", "count_result", "Sum:" + morphia.count());
	}
	
	@RequestMapping(value = "/find", method = RequestMethod.POST)
	public ModelAndView find(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String id = request.getParameter("id");
		if (id.equals(""))
			return new ModelAndView("originalRule", "find_result", "ID is null!");
		OriginalRule originalRule = morphia.findById(id);
		if (originalRule == null)
			return new ModelAndView("originalRule", "find_result", "ID not exist!");
		Map<String, String> modelMap = new HashMap<String, String>();
		modelMap.put("find_result_id", originalRule.getId());
		modelMap.put("find_result_text", originalRule.getText());
		if (originalRule.getAirCompany() != null)
			modelMap.put("find_result_airCompany", "from&nbsp;" + originalRule.getAirCompany());
		return new ModelAndView("originalRule", modelMap);
	}
	
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public ModelAndView insert(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("utf-8");
		String id = request.getParameter("id");
		if (id.equals(""))
			return new ModelAndView("originalRule", "insert_result", "ID is null!");
		String text = request.getParameter("text");
		if (text.equals(""))
			return new ModelAndView("originalRule", "insert_result", "Text is null!");
		OriginalRule originalRule = new OriginalRule()
			.setId(id).setText(text);
		morphia.forceSave(originalRule);
		return new ModelAndView("originalRule", "insert_result", "Success!");
	}
	
	@RequestMapping(value = "/remove", method = RequestMethod.POST)
	public ModelAndView remove(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String id = request.getParameter("id");
		if (id.equals(""))
			return new ModelAndView("originalRule", "remove_result", "ID is null!");
		if (morphia.findById(id) == null)
			return new ModelAndView("originalRule", "remove_result", "ID not exist!");
		morphia.deleteById(id);
		return new ModelAndView("originalRule", "remove_result", "Success!");
	}
	
	@RequestMapping(value = "/removeAll", method = RequestMethod.POST)
	public ModelAndView removeAll() {
		morphia.deleteAll();
		return new ModelAndView("originalRule", "remove_all_result", "Success!");
	}
	
	@RequestMapping(value = "/findAll", method = RequestMethod.POST)
	public ModelAndView findAll(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (morphia.count() == 0)
			return new ModelAndView("originalRule", "find_all_result", "DB is Empty!");
		StringBuilder allResult = new StringBuilder();
		List<OriginalRule> originalRuleList = morphia.findAll();
		for (OriginalRule originalRule:originalRuleList) {
			allResult.append("ID: ").append(originalRule.getId()).append("<br/>");
		}
		return new ModelAndView("originalRule", "find_all_result", "Success!<br/><br/>" + allResult);
	}
}
