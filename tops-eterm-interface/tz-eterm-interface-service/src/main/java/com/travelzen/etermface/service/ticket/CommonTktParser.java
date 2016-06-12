package com.travelzen.etermface.service.ticket;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.etermface.common.config.cdxg.exception.SessionExpireException;
import com.travelzen.etermface.service.EtermWebClient;
import com.travelzen.etermface.service.entity.ParseConfBean;
import com.travelzen.framework.core.common.ReturnClass;
import com.travelzen.framework.core.util.TZUtil;

public abstract class CommonTktParser<TParam, TResult> {
    private static Logger logger = LoggerFactory.getLogger(CommonTktParser.class);
    protected EtermWebClient client;
    ServletContext servletContext;

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public CommonTktParser() {
    }

    public CommonTktParser(ServletContext servletContext, ParseConfBean confBean) {
        this.servletContext = servletContext;
        client = new EtermWebClient(confBean);

    }

    /**
     * 子类实现的处理逻辑，门面模式
     *
     * @param servletContext
     */
    public CommonTktParser(ServletContext servletContext) {
        this(servletContext, new ParseConfBean());
    }

    // when called with EtermWebClient, connect is not needed
    abstract ReturnClass<TResult> parse(EtermWebClient client, TParam param) throws SessionExpireException;

    public ReturnClass<TResult> parse(TParam param) throws SessionExpireException {
        ReturnClass<TResult> ret = null;
        try {
            client.connect();
            ret = parse(client, param);
        } catch (Exception e) {
            logger.error(TZUtil.stringifyException(e));
        } finally {
            client.close();
        }
        return ret;
    }
}
