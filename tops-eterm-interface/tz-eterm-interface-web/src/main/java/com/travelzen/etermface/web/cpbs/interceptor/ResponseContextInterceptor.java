package com.travelzen.etermface.web.cpbs.interceptor;

import com.travelzen.etermface.web.cpbs.ResponseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:14/12/12
 * Time:下午6:19
 * <p/>
 * Description:
 */
public class ResponseContextInterceptor implements HandlerInterceptor {
    private static Logger logger = LoggerFactory.getLogger(ResponseContextInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        ResponseContext.setResponse(response);
        logger.info("将response加入ThreadLocal");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ResponseContext.removeResponse();
        logger.info("将response从ThreadLocal中清除");
    }
}
