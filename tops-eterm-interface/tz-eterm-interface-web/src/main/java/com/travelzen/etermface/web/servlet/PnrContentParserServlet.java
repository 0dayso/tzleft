package com.travelzen.etermface.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.common.ufis.util.UfisException;
import com.travelzen.etermface.service.PNRParser;
import com.travelzen.etermface.service.entity.ParseConfBean;
import com.travelzen.framework.core.util.BooleanUtil;
import com.travelzen.framework.core.util.TZUtil;
import com.travelzen.framework.core.util.YRUtil;

public class PnrContentParserServlet extends HttpServlet {
    private Logger logger = LoggerFactory.getLogger(PnrContentParserServlet.class);

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        HttpServletRequest request;
        HttpServletResponse response;

        if (!(req instanceof HttpServletRequest && res instanceof HttpServletResponse)) {
            throw new ServletException("non-HTTP request or response");
        }

        request = (HttpServletRequest) req;
        response = (HttpServletResponse) res;

        // logger.info("URL:{}",request.getRequestURL().toString());

        super.service(request, response);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
    	logger.info("request: /PnrContentParser GET");
    	
        try {
            response.getOutputStream().println("FORBIDDEN");
        } catch (IOException e) {
            logger.error(YRUtil.stringifyException(e));
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
    	logger.info("request: /PnrContentParser");
    	
        boolean isDomestic = Boolean.parseBoolean(request.getParameter("isDomestic"));
        boolean needFare = BooleanUtil.parseBooleanDefaultIfNull(request.getParameter("needFare"), true);
        boolean needPnrRet = BooleanUtil.parseBooleanDefaultIfNull(request.getParameter("needPnrRet"), true);
        boolean useAv4SharecodeAndDistance = Boolean.parseBoolean(request.getParameter("useAv4SharecodeAndDistance"));
        boolean needXsfsm = Boolean.parseBoolean(request.getParameter("needXsfsm"));
        boolean needAccurateCodeShare = Boolean.parseBoolean(request.getParameter("needAccurateCodeShare"));


        ParseConfBean parseConfBean = new ParseConfBean();

        String officeId = request.getParameter("officeId");
        if (StringUtils.isNotBlank(officeId)) {
            parseConfBean.setOfficeId(officeId);
        }

        parseConfBean.needFare = needFare;
        parseConfBean.needPnrRet = needPnrRet;
        parseConfBean.isDomestic = isDomestic;
        parseConfBean.needXsfsm = needXsfsm;
        parseConfBean.needAccurateCodeShare = needAccurateCodeShare;
        parseConfBean.useAv4SharecodeAndDistance = useAv4SharecodeAndDistance;

        String source = request.getParameter("source");
        String role = request.getParameter("role");

        if (StringUtils.isNotBlank(source)) {
            parseConfBean.source = source;
        }

        if (StringUtils.isNotBlank(role)) {
            parseConfBean.role = role;
        }

        String tripartCode = request.getParameter("tripartCode");
        if (StringUtils.isNotBlank(tripartCode)) {
            parseConfBean.tripartCode = tripartCode;
        }

        String traceId = request.getParameter("traceId");
        if (StringUtils.isNotBlank(traceId)) {
            parseConfBean.setTraceId(traceId);
        }

        String xml = "";
        try {
            xml = new PNRParser( parseConfBean).parsePnrContent(parseConfBean, request.getParameter("pnrContent")).getValue0();
        } catch (UfisException e1) {
            logger.error(" UfisException error:{}", TZUtil.stringifyException(e1));
        }

        try {
            response.setContentType("text/plain;charset=UTF-8");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("pragma", "no-cache");
            response.setCharacterEncoding("UTF-8");

            PrintWriter out = response.getWriter();
            out.println(xml);

            logger.info(xml);
        } catch (IOException e2) {
            logger.error(" error:{}", TZUtil.stringifyException(e2));
        }
    }
}