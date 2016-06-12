package com.travelzen.farerule.mongo.front.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {

	@RequestMapping("/originalRule")
	public ModelAndView originalRule(HttpServletRequest request, HttpServletResponse response, 
			ModelMap modelMap) {
		return new ModelAndView("originalRule", modelMap);
	}
	
	@RequestMapping("/tzRule")
	public ModelAndView jpeckerRule(HttpServletRequest request, HttpServletResponse response, 
			ModelMap modelMap) {
		return new ModelAndView("tzRule", modelMap);
	}
	
	@RequestMapping("/location")
	public ModelAndView location(HttpServletRequest request, HttpServletResponse response, 
			ModelMap modelMap) {
		return new ModelAndView("location", modelMap);
	}
	
	@RequestMapping("/ruleInfo")
	public ModelAndView ruleInfo(HttpServletRequest request, HttpServletResponse response, 
			ModelMap modelMap) {
		return new ModelAndView("ruleInfo", modelMap);
	}
	
}
