package com.travelzen.controller.cpbs;

import com.travelzen.cpbs.utils.JsonUtil;
import com.travelzen.framework.logger.core.ri.CallInfo;
import com.travelzen.framework.logger.core.ri.RequestIdentityHolder;
import com.travelzen.framework.logger.ri.RequestIdentityLogger;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:14/11/6
 * Time:下午5:13
 * <p/>
 * Description:
 */
public class HttpRequest implements Callable<Object> {

    private final static Logger logger = RequestIdentityLogger.getLogger(HttpRequest.class);

    private String url;
    private Map<String, String> params;
    private HttpRequestType httpRequestType;

    public HttpRequest(String url) {
        this(url, null, HttpRequestType.GET);
    }

    public HttpRequest(String url, Map<String, String> params) {
        this(url, params, HttpRequestType.GET);
    }

    public HttpRequest(String url, Map<String, String> params, HttpRequestType httpRequestType) {
        this.setUrl(url);
        this.setParams(params);
        this.setHttpRequestType(httpRequestType);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public HttpRequestType getHttpRequestType() {
        return httpRequestType;
    }

    public void setHttpRequestType(HttpRequestType httpRequestType) {
        this.httpRequestType = httpRequestType;
    }

    /**
     * 获取指定链接的响应信息，GET方式
     *
     * @return
     * @throws Exception
     */
    private String sendGetRequest() throws Exception {
        StringBuffer lvFinalUrl = new StringBuffer(this.url);
        lvFinalUrl.append("?");
        if (null != this.params) {
            for (Map.Entry<String, String> entry : this.params.entrySet()) {
                lvFinalUrl.append("&");
                lvFinalUrl.append(entry.getKey());
                lvFinalUrl.append("=");
                lvFinalUrl.append(entry.getValue());
            }
        }
        HttpClient lvClient = new DefaultHttpClient();
        initHttpClient(lvClient);
        HttpGet lvRequest = new HttpGet(lvFinalUrl.toString());
        //将callinfo放入Header，将自己当前的CallInfo传递给服务提供方
        lvRequest.setHeader("CallInfo", JsonUtil.toJson(RequestIdentityHolder.get(), false));
        String lvResponseString = null;
        HttpResponse response = lvClient.execute(lvRequest);
        //将Response中的CallInfo设置到ThreadLocal
        try {
            Header[] headers = response.getHeaders("CallInfo");
            String callInfoStr = headers[0].getValue();
            RequestIdentityHolder.setOnRead((CallInfo) JsonUtil.fromJson(callInfoStr, CallInfo.class));
        } catch (Exception e) {
            logger.warn("服务提供方没有将CallInfo返回,不能进行调用链追踪.");
        }
        lvResponseString = EntityUtils.toString(response.getEntity());
        return lvResponseString;
    }

    /**
     * 获取指定链接的响应信息，POST方式
     *
     * @return
     * @throws Exception
     */
    private String sendPostRequest() throws Exception {
        HttpClient lvClient = new DefaultHttpClient();
        initHttpClient(lvClient);
        HttpPost lvHttpPost = new HttpPost(this.url);
        //将callinfo放入Header，将自己当前的CallInfo传递给服务提供方
        lvHttpPost.setHeader("CallInfo", JsonUtil.toJson(RequestIdentityHolder.get(), false));
        List<NameValuePair> lvParamsList = new ArrayList<NameValuePair>();
        if (null != this.params) {
            for (Map.Entry<String, String> entry : this.params.entrySet()) {
                NameValuePair tmNameValuePair = new BasicNameValuePair(entry.getKey(), entry.getValue());
                lvParamsList.add(tmNameValuePair);
            }
        }
        String lvResponseString = null;
        lvHttpPost.setEntity(new UrlEncodedFormEntity(lvParamsList, "UTF-8"));
        HttpResponse response = lvClient.execute(lvHttpPost);
        //将Response中的CallInfo设置到ThreadLocal
        try {
            Header[] headers = response.getHeaders("CallInfo");
            String callInfoStr = headers[0].getValue();
            RequestIdentityHolder.setOnRead((CallInfo) JsonUtil.fromJson(callInfoStr, CallInfo.class));
        } catch (Exception e) {
            logger.warn("服务提供方没有将CallInfo返回,不能进行调用链追踪.");
        }
        lvResponseString = EntityUtils.toString(response.getEntity(), "UTF-8");
        return lvResponseString;
    }

    @Override
    public String call() throws Exception {
        if (this.getHttpRequestType() == HttpRequestType.GET) {
            return sendGetRequest();
        }
        return sendPostRequest();
    }

    /**
     * 统一提交请求接口
     *
     * @param pUrl
     * @param pParams
     * @param pHttpRequestType
     * @return
     * @throws Exception
     */
    public static String sendRequest(String pUrl, Map<String, String> pParams, HttpRequestType pHttpRequestType)
            throws Exception {

        long startTime = System.currentTimeMillis();
        Callable<Object> lvCallable = new HttpRequest(pUrl, pParams, pHttpRequestType);
        Future<Object> lvFuture = ThreadPoolService.getInstance().submit(lvCallable);
        String lvResponse = null;

        lvResponse = (String) lvFuture.get();

        long endTime = System.currentTimeMillis();

        long last = endTime - startTime;

        logger.info("Http请求地址:" + pUrl + "&" + pParams + "\nHttp请求开始时间：" + DateFormatCollections.YYYY_MM_DD_HH_MM_SS_SSS_FORMAT.format(new Date(startTime))
                + "\nHttp请求结束时间：" + DateFormatCollections.YYYY_MM_DD_HH_MM_SS_SSS_FORMAT.format(new Date(endTime)) + "\n耗时：" + last / 1000 + "秒" + last % 1000
                + "毫秒\n返回结果\n" + lvResponse);

        return lvResponse;
    }

    public static String sendRequest(String pUrl, Map<String, String> pParams, HttpRequestType pHttpRequestType,
                                     boolean needLog) throws Exception {

        long startTime = System.currentTimeMillis();
        Callable<Object> lvCallable = new HttpRequest(pUrl, pParams, pHttpRequestType);
        Future<Object> lvFuture = ThreadPoolService.getInstance().submit(lvCallable);
        String lvResponse = null;

        lvResponse = (String) lvFuture.get();

        long endTime = System.currentTimeMillis();

        long last = endTime - startTime;

        if (needLog) {
            logger.info("Http请求地址:" + pUrl + "&" + pParams + "\nHttp请求开始时间：" + DateFormatCollections.YYYY_MM_DD_HH_MM_SS_SSS_FORMAT.format(new Date(startTime))
                    + "\nHttp请求结束时间：" + DateFormatCollections.YYYY_MM_DD_HH_MM_SS_SSS_FORMAT.format(new Date(endTime)) + "\n耗时：" + last / 1000 + "秒" + last % 1000
                    + "毫秒\n返回结果\n" + lvResponse);
        } else {
            logger.info("Http请求地址:" + pUrl + "&" + pParams + "\nHttp请求开始时间：" + DateFormatCollections.YYYY_MM_DD_HH_MM_SS_SSS_FORMAT.format(new Date(startTime))
                    + "\nHttp请求结束时间：" + DateFormatCollections.YYYY_MM_DD_HH_MM_SS_SSS_FORMAT.format(new Date(endTime)) + "\n耗时：" + last / 1000 + "秒" + last % 1000
                    + "毫秒");
        }

        return lvResponse;
    }

    /**
     * 设置http超时参数
     *
     * @param client
     */
    private void initHttpClient(HttpClient client) {
        if (null == client) {
            return;
        }
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
    }

    /**
     * 统一提交请求接口，GET方式
     *
     * @param pUrl
     * @return
     * @throws Exception
     */
    public static String sendRequest(String pUrl, Map<String, String> pParams) throws Exception {
        return sendRequest(pUrl, pParams, HttpRequestType.GET);
    }

    public static String sendRequest(String pUrl, Map<String, String> pParams, boolean needLog) throws Exception {
        return sendRequest(pUrl, pParams, HttpRequestType.GET, needLog);
    }
}
