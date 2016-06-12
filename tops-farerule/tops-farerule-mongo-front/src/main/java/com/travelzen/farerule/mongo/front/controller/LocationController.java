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

import com.travelzen.farerule.mongo.Location;
import com.travelzen.farerule.mongo.morphia.LocationMorphia;

@Controller
@RequestMapping("/location")
public class LocationController {
	
	private static LocationMorphia morphia = LocationMorphia.Instance;

	@RequestMapping(value = "/count", method = RequestMethod.POST)
	public ModelAndView count() {
		return new ModelAndView("location", "count_result", "Sum:" + morphia.count());
	}
	
	@RequestMapping(value = "/find", method = RequestMethod.POST)
	public ModelAndView find(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String enLoc = request.getParameter("enLoc");
		if (enLoc.equals(""))
			return new ModelAndView("location", "find_result", "ID is null!");
		Location location = morphia.find(enLoc);
		if (location == null)
			return new ModelAndView("location", "find_result", "ID not exist!");
		Map<String, String> modelMap = new HashMap<String, String>();
		modelMap.put("find_result_en", location.getEnLoc());
		modelMap.put("find_result_cn", location.getCnLoc());
		return new ModelAndView("location", modelMap);
	}
	
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public ModelAndView insert(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("utf-8");
		String enLoc = request.getParameter("enLoc");
		if (enLoc.equals(""))
			return new ModelAndView("location", "insert_result", "EnLoc is null!");
		String cnLoc = request.getParameter("cnLoc");
		if (cnLoc.equals(""))
			return new ModelAndView("location", "insert_result", "CnLoc is null!");
		Location location = new Location().setEnLoc(enLoc).setCnLoc(cnLoc);
		morphia.forceSave(location);
		return new ModelAndView("location", "insert_result", "Success!");
	}
	
	@RequestMapping(value = "/remove", method = RequestMethod.POST)
	public ModelAndView remove(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String enLoc = request.getParameter("enLoc");
		if (enLoc.equals(""))
			return new ModelAndView("location", "remove_result", "ID is null!");
		if (morphia.find(enLoc) == null)
			return new ModelAndView("location", "remove_result", "ID not exist!");
		morphia.delete(enLoc);
		return new ModelAndView("location", "remove_result", "Success!");
	}
	
	@RequestMapping(value = "/removeAll", method = RequestMethod.POST)
	public ModelAndView removeAll() {
		morphia.deleteAll();
		return new ModelAndView("location", "remove_all_result", "Success!");
	}
	
	@RequestMapping(value = "/findAll", method = RequestMethod.POST)
	public ModelAndView findAll(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (morphia.count() == 0)
			return new ModelAndView("location", "find_all_result", "DB is Empty!");
		StringBuilder allResult = new StringBuilder();
		List<Location> locationList = morphia.findAll();
		for (Location location:locationList) {
			allResult.append("<tr><td>").append(location.getEnLoc()).append("</td><td>")
				.append(location.getCnLoc()).append("</td></tr>");
		}
		return new ModelAndView("location", "find_all_result", 
				"Success!<br/><br/><table border=\"0\"><th>EnLoc</th><th>CnLoc</th>" + allResult + "</table>");
	}
	
	@RequestMapping(value = "/findNew", method = RequestMethod.POST)
	public ModelAndView findNew(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (morphia.count() == 0)
			return new ModelAndView("location", "find_new_result", "DB is Empty!");
		StringBuilder allResult = new StringBuilder();
		List<Location> locationList = morphia.findNew();
		if (locationList.size() == 0)
			return new ModelAndView("location", "find_new_result", "No new location!");
		for (Location location:locationList) {
			allResult.append(location.getEnLoc()).append("<br/>");
		}
		return new ModelAndView("location", "find_new_result", "Success!<br/><br/>" + allResult);
	}
}
