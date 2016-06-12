package com.travelzen.etermface.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.etermface.service.PNRParser;
import com.travelzen.etermface.service.entity.ParseConfBean;
import com.travelzen.framework.core.util.TZUtil;

public class TopsPnrParserServlet extends HttpServlet {
    private static final long serialVersionUID = 6056285579841006160L;
    private static Logger logger = LoggerFactory.getLogger(TopsPnrParserServlet.class);

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
    	logger.info("request: /TopsPnrParser");
    	
        boolean isDomestic = Boolean.parseBoolean(request.getParameter("isDomestic"));
        boolean needXsfsm = Boolean.parseBoolean(request.getParameter("needXsfsm"));
        boolean needFare = Boolean.parseBoolean(request.getParameter("needFare"));
        boolean needAccurateCodeShare = Boolean.parseBoolean(request.getParameter("needAccurateCodeShare"));
        boolean needIssueTktOffice = Boolean.parseBoolean(request.getParameter("needIssueTktOffice"));
        boolean useAv4SharecodeAndDistance = Boolean.parseBoolean(request.getParameter("useAv4SharecodeAndDistance"));

        String source = request.getParameter("source");
        String role = request.getParameter("role");
        String tripartCode = request.getParameter("tripartCode");

        ParseConfBean parseConfBean = new ParseConfBean();

        if (StringUtils.isNotBlank(source)) {
            parseConfBean.source = source;
        }
        if (StringUtils.isNotBlank(role)) {
            parseConfBean.role = role;
        }
        String officeId = request.getParameter("officeId");
        if (StringUtils.isNotBlank(officeId)) {
            parseConfBean.setOfficeId(officeId);
        }

        String traceId = request.getParameter("traceId");
        if (StringUtils.isNotBlank(traceId)) {
            parseConfBean.setTraceId(traceId);
        }

        if (StringUtils.isNotBlank(tripartCode)) {
            parseConfBean.tripartCode = tripartCode;
        }

        parseConfBean.isDomestic = isDomestic;

        parseConfBean.needFare = needFare;
        parseConfBean.needSleep = false;
        parseConfBean.needMakeFareByPNR = false;
        parseConfBean.needPnrRet = true;

        parseConfBean.needXsfsm = needXsfsm;

        parseConfBean.needAccurateCodeShare = needAccurateCodeShare;
        parseConfBean.useAv4SharecodeAndDistance = useAv4SharecodeAndDistance;

        parseConfBean.setNeedIssueTktOffice(needIssueTktOffice);

        String xml = new PNRParser(parseConfBean).parsePnrWithRetryOnceOnSessionExpire(parseConfBean, request.getParameter("pnr"))
                .getValue0();

        try {
            response.setContentType("text/plain;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("pragma", "no-cache");

            PrintWriter out = response.getWriter();
            out.println(xml);

        } catch (IOException e) {
            logger.error(TZUtil.stringifyException(e));
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        doGet(request, response);
    }
}