package com.travelzen.farerule.mongo.front.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.travelzen.farerule.RuleSourceEnum;
import com.travelzen.farerule.TzRule;
import com.travelzen.farerule.TzRuleInfo;
import com.travelzen.farerule.jpecker.server.DisplayRule;
import com.travelzen.farerule.mongo.front.simpecker.SimPecker;
import com.travelzen.farerule.mongo.morphia.TzRuleMorphia;
import com.travelzen.farerule.translator.RuleTranslator;

@Controller
@RequestMapping("/tzRule")
public class TzRuleController {
	
	private static final Logger logger = LoggerFactory.getLogger(TzRuleController.class);

	private static TzRuleMorphia morphia = TzRuleMorphia.Instance;
	
	@RequestMapping(value = "/count", method = RequestMethod.POST)
	public ModelAndView count() {
		return new ModelAndView("tzRule", "count_result", "Sum:" + morphia.count());
	}
	
	@RequestMapping(value = "/find", method = RequestMethod.POST)
	public ModelAndView find(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String source = request.getParameter("source");
		String id = request.getParameter("id");
		if (source == null)
			return new ModelAndView("tzRule", "find_result", "Source is null!");
		if (id.equals(""))
			return new ModelAndView("tzRule", "find_result", "ID is null!");
		TzRuleInfo tzRuleInfo = new TzRuleInfo();
		if (source.equals("jpecker")) {
			tzRuleInfo.setRuleSource(RuleSourceEnum.JPECKER).setJpeckerRuleId(id);
		} else if (source.equals("paperfare")) {
			tzRuleInfo.setRuleSource(RuleSourceEnum.PAPERFARE).setPaperfareRuleId(Long.parseLong(id));
		}
		TzRule tzRule = morphia.findByTzRuleInfo(tzRuleInfo);
		logger.info("FIND: " + tzRule);
		if (tzRule == null)
			return new ModelAndView("tzRule", "find_result", "ID not exist!");
		DisplayRule result = RuleTranslator.translate(tzRule);
		Map<String, String> modelMap = new HashMap<String, String>();
		modelMap.put("find_result_source", source);
		modelMap.put("find_result_id", id);
		modelMap.put("find_result_content_6", result.getMinStay());
		modelMap.put("find_result_content_7", result.getMaxStay());
		modelMap.put("find_result_content_14", result.getTravelDate());
		modelMap.put("find_result_content_16", result.getPenalties());
		return new ModelAndView("tzRule", modelMap);
	}
	
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public ModelAndView insert(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("utf-8");
		String source = request.getParameter("source");
		String id = request.getParameter("id");
		if (source == null)
			return new ModelAndView("tzRule", "insert_result", "Source is null!");
		if (id.equals(""))
			return new ModelAndView("tzRule", "insert_result", "ID is null!");
		String minStayText = request.getParameter("content_text_6");
		if (minStayText.equals(""))
			return new ModelAndView("tzRule", "insert_result", "MinStay Text is null!");
		String maxStayText = request.getParameter("content_text_7");
		if (maxStayText.equals(""))
			return new ModelAndView("tzRule", "insert_result", "MaxStay Text is null!");
		String travelDateText = request.getParameter("content_text_14");
		if (travelDateText.equals(""))
			return new ModelAndView("tzRule", "insert_result", "TravelDate Text is null!");
		String penaltiesText = request.getParameter("content_text_16");
		if (penaltiesText.equals(""))
			return new ModelAndView("tzRule", "insert_result", "Penalties Text is null!");
		TzRule tzRule = new TzRule();
		TzRuleInfo tzRuleInfo = new TzRuleInfo();
		if (source.equals("jpecker")) {
			tzRuleInfo.setRuleSource(RuleSourceEnum.JPECKER).setJpeckerRuleId(id);
		} else if (source.equals("paperfare")) {
			tzRuleInfo.setRuleSource(RuleSourceEnum.PAPERFARE).setPaperfareRuleId(Long.parseLong(id));
		}
		SimPecker simPecker = new SimPecker();
		simPecker.process(tzRuleInfo, minStayText, maxStayText, travelDateText, penaltiesText);
		tzRule = simPecker.getTzRule();
		logger.info("INSERT: " + tzRule);
		if (tzRule != null) {
			morphia.forceSave(tzRule);
		} else {
			return new ModelAndView("tzRule", "insert_result", "Fail!");
		}
		return new ModelAndView("tzRule", "insert_result", "Success!");
	}
	
	@RequestMapping(value = "/remove", method = RequestMethod.POST)
	public ModelAndView remove(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String source = request.getParameter("source");
		String id = request.getParameter("id");
		if (source == null)
			return new ModelAndView("tzRule", "remove_result", "Source is null!");
		if (id.equals(""))
			return new ModelAndView("tzRule", "remove_result", "ID is null!");
		TzRuleInfo tzRuleInfo = new TzRuleInfo();
		if (source.equals("jpecker")) {
			tzRuleInfo.setRuleSource(RuleSourceEnum.JPECKER).setJpeckerRuleId(id);
		} else if (source.equals("paperfare")) {
			tzRuleInfo.setRuleSource(RuleSourceEnum.PAPERFARE).setPaperfareRuleId(Long.parseLong(id));
		}
		if (morphia.findByTzRuleInfo(tzRuleInfo) == null)
			return new ModelAndView("tzRule", "remove_result", "ID not exist!");
		morphia.deleteById(tzRuleInfo);
		return new ModelAndView("tzRule", "remove_result", "Success!");
	}
	
	@RequestMapping(value = "/removeAll", method = RequestMethod.POST)
	public ModelAndView removeAll() {
		morphia.deleteAll();
		return new ModelAndView("tzRule", "remove_all_result", "Success!");
	}
	
	@RequestMapping(value = "/findAll", method = RequestMethod.POST)
	public ModelAndView findAll(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (morphia.count() == 0)
			return new ModelAndView("tzRule", "find_all_result", "DB is Empty!");
		StringBuilder allResult = new StringBuilder();
		List<TzRule> tzRuleList = morphia.findAll();
		for (TzRule tzRule:tzRuleList) {
			TzRuleInfo tzRuleInfo = tzRule.getTzRuleInfo();
			allResult.append("Source: ").append(tzRuleInfo.getRuleSource()).append("<br/>");
			if (tzRuleInfo.getRuleSource() == RuleSourceEnum.JPECKER)
				allResult.append("ID: ").append(tzRuleInfo.getJpeckerRuleId());
			else if (tzRuleInfo.getRuleSource() == RuleSourceEnum.PAPERFARE)
				allResult.append("ID: ").append(tzRuleInfo.getPaperfareRuleId());
			allResult.append("<br/><br/>");
		}
		return new ModelAndView("tzRule", "find_all_result", "Success!<br/>" + allResult);
	}
}
