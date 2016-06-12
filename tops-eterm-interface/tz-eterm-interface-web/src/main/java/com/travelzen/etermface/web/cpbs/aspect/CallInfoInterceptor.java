package com.travelzen.etermface.web.cpbs.aspect;

import com.travelzen.cpbs.Cpbs;
import com.travelzen.cpbs.Handle;
import com.travelzen.etermface.web.cpbs.ResponseContext;
import com.travelzen.framework.core.json.JsonUtil;
import com.travelzen.framework.logger.core.ri.CallInfo;
import com.travelzen.framework.logger.core.ri.RequestIdentityHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by yangguo on 14-1-22.
 * <p/>
 * controller切面，主要作用是对CPBS拦截进行预处理
 */
@Component("callInfoInterceptor")
@Aspect
public class CallInfoInterceptor {
    private static Logger logger = LoggerFactory.getLogger(CallInfoInterceptor.class);


    //切入点拦截controller
    @Pointcut(value = "@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    private void controllerfoPointcut() {
    }


    @Around("controllerfoPointcut()")
    public Object controllerCallInfo(ProceedingJoinPoint pjp) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response = ResponseContext.getResponse();
        ResponseContext.removeResponse();
        String requestURI = request.getRequestURI();


        Object object = "CPBS拦截成功，达到访问的容量阀值！";

        if (request != null && response != null) {
            String callInfoStr = request.getHeader("CallInfo");
            try {
                RequestIdentityHolder.setOnRead((CallInfo) JsonUtil.fromJson(callInfoStr, CallInfo.class));
                response.setHeader("CallInfo", JsonUtil.toJson(RequestIdentityHolder.get(), false));
            } catch (Exception e) {
                logger.warn("切入点，设置CallInfo时出错");
            }
            logger.info(requestURI);

            if (RequestIdentityHolder.get() == null) {
                try {
                    logger.info("CallInfo未获取到，不走CPBS逻辑");
                    object = pjp.proceed();
                    return object;
                } catch (Throwable throwable) {
                    object = throwable.toString();
                    logger.error("切入点执行异常，请检查切入点方法:", throwable);
                }
            } else {
                //CPBS逻辑
                //因为调用方根本不知道自己使用了哪些指令，所以需要我们自己设置进去，后面的拦截才能生效
                //对detr指令进行拦截
                if (requestURI.contains("/flight/ticket/detrCombine") || requestURI.contains("/flight/ticket/detrf") || requestURI.contains("/flight/ticket/detr") || requestURI.contains("/contains/ticket/detrs")) {
                    RequestIdentityHolder.get().getOthers().put("command", "detr");
                }

                Cpbs cpbs = Cpbs.getInstance();
                Handle handle = cpbs.getHandle(RequestIdentityHolder.get());
                if (handle.waiting()) {
                    if (handle.doing()) {
                        try {
                            object = pjp.proceed();
                        } catch (Throwable throwable) {
                            object = throwable.toString();
                            logger.error("切入点执行异常，请检查切入点方法:", throwable);
                        } finally {
                            handle.done();
                        }
                    }
                }
            }
        } else {
            logger.error("在拦截器中，获取request和response对象失败");
        }
        RequestIdentityHolder.remove();
        return object;
    }
}
