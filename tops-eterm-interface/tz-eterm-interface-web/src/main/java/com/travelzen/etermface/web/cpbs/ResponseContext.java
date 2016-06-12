package com.travelzen.etermface.web.cpbs;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:14/12/14
 * Time:上午10:32
 * <p/>
 * Description:
 */
public class ResponseContext {
    private static ThreadLocal<HttpServletResponse> responseThreadLocal = new ThreadLocal<>();

    public static HttpServletResponse getResponse() {
        return responseThreadLocal.get();
    }

    public static void setResponse(HttpServletResponse response) {
        responseThreadLocal.set(response);
    }

    public static void removeResponse() {
        responseThreadLocal.remove();
    }
}
