package com.travelzen.fare;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import com.travelzen.etermface.service.EtermWebClient;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;

import com.thoughtworks.xstream.XStream;
import com.travelzen.etermface.service.abe_imitator.fare.pojo.FareSearchRequest;
import com.travelzen.etermface.service.abe_imitator.fare.pojo.FareSearchResponse;
import com.travelzen.etermface.common.config.cdxg.exception.SessionExpireException;
import com.travelzen.framework.core.common.ReturnClass;
import com.travelzen.framework.core.exception.BizException;

public class FareRules {
    private static Logger logger = LoggerFactory.getLogger(FareRules.class);
    private EtermWebClient etermWebClient;

    {
        ServletContext servletContext = new MockServletContext();
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
                "classpath*:spring/eterm-interface-appctx.xml", "classpath*:spring/webApplicationContext.xml");
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);

        etermWebClient = new EtermWebClient();
    }

    @Test
    public void getInfo() {
        try {
            String rs1 = null;
            String filename = null;
            filename = "/media/B634186934182F3D/etermlog/01/IBSP-LOG/server-command-20130603.txt";
            System.out.println(filename);
            BufferedReader reader;
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "GBK"));
            String line;
            String rs = "";
            int n = 0;
            boolean isNeed = false;
            while ((line = reader.readLine()) != null) {

                line = line.trim();
                if (line.startsWith("M>NFN0")) {
                    isNeed = true;
                }
                if (!isNeed) {
                    continue;
                }
                n++;
                System.out.println(line);
                if (n % 100 == 0) {
                    Thread.sleep(1000);
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void publicFareSearchTest() throws SessionExpireException {

        // 22MAY(WED) WNZSHA
        // FDDLCSHA
        FareSearchRequest req = new FareSearchRequest();
        req.setFrom("SHA");
        req.setArrive("HKG");
        req.setDate("2014-03-29");
        req.setCarrier("MU");
        int n = 1;
        // AV H /SHAPEK/23JUL/CA
        StringBuffer needWriter = new StringBuffer();
        StringBuffer notes = new StringBuffer();
        try {
            String rs = publicFareSearch(req);
            XStream xstream = new XStream();
            System.out.println(xstream.toXML(rs));
            etermWebClient.connect();
            String errorMsg = "";
            ReturnClass<String> cmdRs = etermWebClient.executeCmdWithRetry("XS FSN" + 11 + "//all", false);
            String result = cmdRs.getObject();
            System.out.println(result);
            result = result.trim();
            int index = result.lastIndexOf("\r");
            String pageString = result.substring(index);
            result = result.substring(0, index);
            String[] strs = result.split("\r");
            int startEnd = 0;
            notes.append(result);
            notes.append("\r\n");
            char pageNumChar = pageString.charAt(pageString.length() - 1);
            char pageNumCharBefore = pageString.charAt(pageString.length() - 2);
            System.out.println();
            if (Character.isDigit(pageNumChar)) {
                String str = "";
                if (Character.isDigit(pageNumCharBefore)) {
                    str = str + pageNumCharBefore + pageNumChar;
                } else {
                    str = str + pageNumChar;
                }
                int pageNum = Integer.parseInt(String.valueOf(str));
                while (n < pageNum) {
                    n++;
                    System.out.println(n);
                    cmdRs = etermWebClient.executeCmdWithRetry("XS FSPN", false);
                    if (!cmdRs.isSuccess()) {
                        errorMsg = cmdRs.getStatus().toString();
                        break;
                    } else {
                        result = cmdRs.getObject();
                        if (null == result) {
                            logger.error("unknow error");
                            break;
                        }
                    }
                    result = result.trim();
                    index = result.lastIndexOf("\r");
                    pageString = result.substring(index);
                    result = result.substring(0, index);
                    if (startEnd == 0) {
                        for (String string : strs) {
                            int find = result.indexOf(string);
                            if (find != -1) {
                                startEnd++;
                                continue;
                            } else {
                                break;
                            }
                        }
                    }
                    for (int i = 0; i < startEnd; i++) {
                        result = result.substring(result.indexOf("\r") + 1);
                    }
                    notes.append(result);
                    notes.append("\r\n");
                }
                logger.info("bargainFareSearchRs :{" + notes.toString() + "}");
            }
            System.out.println(notes.toString());
            needWriter.append(notes.toString().replaceAll("\r", "\n"));
        } catch (Exception e) {
            e.printStackTrace();
            throw BizException.instance("from or arrive are not IATA code!");
        } finally {
            etermWebClient.close();
        }
    }

    public void publicFareSearch(FareSearchRequest req, FileWriter fw, int num) throws SessionExpireException {

        // 22MAY(WED) WNZSHA
        // FDDLCSHA
        // FareSearchRequest req = new FareSearchRequest();
        // req.setFrom("SHA");
        // req.setArrive("TYO");
        // req.setDate("2013-12-20");
        // req.setCarrier("MU");
        int n = 1;
        // AV H /SHAPEK/23JUL/CA
        StringBuffer needWriter = new StringBuffer();
        StringBuffer notes = new StringBuffer();
        try {
            String rs = publicFareSearch(req);
            XStream xstream = new XStream();
            System.out.println(xstream.toXML(rs));
            etermWebClient.connect();
            String errorMsg = "";
            ReturnClass<String> cmdRs = etermWebClient.executeCmdWithRetry("XS FSN" + num + "//all", false);
            String result = cmdRs.getObject();
            System.out.println(result);
            result = result.trim();
            int index = result.lastIndexOf("\r");
            String pageString = result.substring(index);
            result = result.substring(0, index);
            String[] strs = result.split("\r");
            int startEnd = 0;
            notes.append(result);
            notes.append("\r\n");
            char pageNumChar = pageString.charAt(pageString.length() - 1);
            char pageNumCharBefore = pageString.charAt(pageString.length() - 2);
            if (Character.isDigit(pageNumChar)) {
                String str = "";
                if (Character.isDigit(pageNumCharBefore)) {
                    str = str + pageNumCharBefore + pageNumChar;
                } else {
                    str = str + pageNumChar;
                }
                int pageNum = Integer.parseInt(String.valueOf(str));
                while (n < pageNum) {
                    n++;
                    System.out.println(n);
                    cmdRs = etermWebClient.executeCmdWithRetry("XS FSPN", false);
                    if (!cmdRs.isSuccess()) {
                        errorMsg = cmdRs.getStatus().toString();
                        break;
                    } else {
                        result = cmdRs.getObject();
                        if (null == result) {
                            logger.error("unknow error");
                            break;
                        }
                    }
                    result = result.trim();
                    index = result.lastIndexOf("\r");
                    pageString = result.substring(index);
                    result = result.substring(0, index);
                    if (startEnd == 0) {
                        for (String string : strs) {
                            int find = result.indexOf(string);
                            if (find != -1) {
                                startEnd++;
                                continue;
                            } else {
                                break;
                            }
                        }
                    }
                    for (int i = 0; i < startEnd; i++) {
                        result = result.substring(result.indexOf("\r") + 1);
                    }
                    notes.append(result);
                    notes.append("\r\n");
                }
                logger.info("bargainFareSearchRs :{" + notes.toString() + "}");
            }
            needWriter.append(notes.toString().replaceAll("\r", "\n"));
            fw.write(needWriter.toString());
            fw.flush();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw BizException.instance("from or arrive are not IATA code!");
        } finally {
            etermWebClient.close();
        }
    }

    public String publicFareSearch(FareSearchRequest req) throws SessionExpireException {
        FareSearchResponse fareSearchResponse = new FareSearchResponse();
        StringBuffer notes = new StringBuffer();
        String cmd = "";
        try {
            cmd = req.publicFareSearchCmd();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        cmd = cmd.replaceFirst("FD", "XS FSD");
        try {
            int n = 1;
            // 获取资源 并利用资源查询生成结果
            etermWebClient.connect();
            String errorMsg = "";
            ReturnClass<String> cmdRs = etermWebClient.executeCmdWithRetry(cmd + "/*OW", false);
            String result = "";
            if (!cmdRs.isSuccess()) {
                errorMsg = cmdRs.getStatus().toString();
                logger.error(errorMsg);
            } else {
                result = cmdRs.getObject();
                if (null == result) {
                    logger.error("结果为空");
                }
                if (result.contains("指定航段本日没有适用运价")) {
                    logger.error("指定航段本日没有适用运价");
                }
            }
            if (StringUtils.isBlank(result)) {
                fareSearchResponse.setSuccess(false);
                fareSearchResponse.setErrorInfo("raw result is blank!");
            }
            result = result.trim();

            notes.append(result);
            notes.append("\r");

            char pageNumChar = result.charAt(result.length() - 1);
            char pageNumCharBefore = result.charAt(result.length() - 2);
            if (Character.isDigit(pageNumChar)) {
                String str = "";
                if (Character.isDigit(pageNumCharBefore)) {
                    str = str + pageNumCharBefore + pageNumChar;
                } else {
                    str = str + pageNumChar;
                }
                int pageNum = Integer.parseInt(String.valueOf(str));
                while (n < pageNum) {
                    n++;
                    cmdRs = etermWebClient.executeCmdWithRetry("XS FSPN", false);
                    ;
                    if (!cmdRs.isSuccess()) {
                        errorMsg = cmdRs.getStatus().toString();
                        break;
                    } else {
                        result = cmdRs.getObject();
                        if (null == result) {
                            logger.error("unknow error");
                            break;
                        }
                    }
                    result = result.trim();
                    notes.append(result);
                    notes.append("\r");
                }
                logger.info("publicFareSearchRS:{" + notes.toString() + "}");
            }
        } catch (Exception e) {
            logger.error("error:{" + e.getMessage() + "}");
            logger.error("publicFare search  error:{" + notes.toString() + "}");
        } finally {
            etermWebClient.close();
        }

        return notes.toString();
    }

    public void bargainFareSearch(FareSearchRequest req, FileWriter fw, int num) throws SessionExpireException {
        // 22MAY(WED) WNZSHA
        // FDDLCSHA
        // FareSearchRequest req = new FareSearchRequest();
        // req.setFrom("SHA");
        // req.setArrive("TYO");
        // req.setDate("2013-11-20");
        // req.setCarrier("MU");
        int n = 1;
        // AV H /SHAPEK/23JUL/CA
        StringBuffer needWriter = new StringBuffer();
        StringBuffer notes = new StringBuffer();
        try {
            String rs = bargainFareSearch(req);
            needWriter.append(rs.replaceAll("\r", "\n"));
            needWriter.append("\n\n\n");
            XStream xstream = new XStream();
            System.out.println(xstream.toXML(rs));
            etermWebClient.connect();
            String errorMsg = "";
            ReturnClass<String> cmdRs = etermWebClient.executeCmdWithRetry("NFN:" + num, false);
            String result = cmdRs.getObject();
            System.out.println(result);
            result = result.trim();
            notes.append(result);
            notes.append("\r\n");
            char pageNumChar = result.charAt(result.length() - 1);
            int pageNum = Integer.parseInt(String.valueOf(pageNumChar));
            System.out.println();
            if (Character.isDigit(pageNumChar)) {
                while (n < pageNum) {
                    n++;
                    cmdRs = etermWebClient.executePn();
                    if (!cmdRs.isSuccess()) {
                        errorMsg = cmdRs.getStatus().toString();
                        break;
                    } else {
                        result = cmdRs.getObject();
                        if (null == result) {
                            logger.error("unknow error");
                            break;
                        }
                    }
                    result = result.trim();
                    notes.append(result);
                    notes.append("\r\n");
                }
                logger.info("bargainFareSearchRs :{" + notes.toString() + "}");
            }
            cmdRs = etermWebClient.executeCmdWithRetry("NFN:02", false);
            result = cmdRs.getObject();
            System.out.println(result);
            result = result.trim();
            notes.append(result);
            notes.append("\r\n");
            System.out.println(notes.toString());
            needWriter.append(notes.toString().replaceAll("\r", "\n"));
            fw.write(needWriter.toString());
            fw.flush();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw BizException.instance("from or arrive are not IATA code!");
        } finally {
            etermWebClient.close();
        }
    }

    public String bargainFareSearch(FareSearchRequest req) throws SessionExpireException {
        StringBuffer notes = new StringBuffer();
        FareSearchResponse fareSearchResponse = new FareSearchResponse();
        try {
            int n = 1;
            etermWebClient.connect();
            // 获取资源 并利用资源查询生成结果
            String errorMsg = "";
            ReturnClass<String> cmdRs = etermWebClient.executeCmdWithRetry(req.bargainFareSearchCmd(), false);
            String result = "";
            if (!cmdRs.isSuccess()) {
                errorMsg = cmdRs.getStatus().toString();
                logger.error(errorMsg);
                fareSearchResponse.setSuccess(false);
                fareSearchResponse.setErrorInfo(errorMsg);
            } else {
                result = cmdRs.getObject();
                if (null == result) {
                    logger.error("结果为空");
                    fareSearchResponse.setSuccess(false);
                    fareSearchResponse.setErrorInfo("结果为空");
                }
                if (result.contains("指定航段本日没有适用运价")) {
                    logger.error("指定航段本日没有适用运价");
                    fareSearchResponse.setSuccess(false);
                    fareSearchResponse.setErrorInfo("指定航段本日没有适用运价");
                }
            }
            if (StringUtils.isBlank(result)) {
                fareSearchResponse.setSuccess(false);
                fareSearchResponse.setErrorInfo("raw result is blank!");
            }

            result = result.trim();

            notes.append(result);
            notes.append("\r\n");
            char pageNumChar = result.charAt(result.length() - 1);
            int pageNum = Integer.parseInt(String.valueOf(pageNumChar));
            if (Character.isDigit(pageNumChar)) {
                while (n < 1) {
                    n++;
                    cmdRs = etermWebClient.executePn();
                    if (!cmdRs.isSuccess()) {
                        errorMsg = cmdRs.getStatus().toString();
                        break;
                    } else {
                        result = cmdRs.getObject();
                        if (null == result) {
                            logger.error("unknow error");
                            break;
                        }
                    }
                    result = result.trim();
                    notes.append(result);
                    notes.append("\r");
                }
                logger.info("bargainFareSearchRs :{" + notes.toString() + "}");
            }
        } catch (Exception e) {
            logger.error("error:{" + e.getMessage() + "}");
            logger.error("bargainFare search  error:{" + notes.toString() + "}");
        } finally {
            etermWebClient.close();
        }

        return notes.toString();
    }

    public static void main(String[] args) {
        BufferedReader bReader;
        List<String> fromCityList = new ArrayList<>();
        fromCityList.add("KMG");
        fromCityList.add("KMG");
        // fromCityList.add("SZX");
        FareRules fareRules = new FareRules();
        List<String> toCityList = new ArrayList<>();
        toCityList.add("HKG");
        toCityList.add("TYO");
        toCityList.add("NYC");
        String preString = "/home/guohuaxue/Documents/rules/";
        try {
            bReader = new BufferedReader(new FileReader(new File("/home/guohuaxue/Documents/airline.txt")));
            String line = null;
            while ((line = bReader.readLine()) != null) {
                if (line.contains("/")) {
                    line = line.substring(0, line.indexOf("/")).trim();
                }
                FareSearchRequest req = new FareSearchRequest();
                String fwFilenameString = "";
                String toCity = null;
                req.setDate("2013-12-30");
                if (line.contains("=")) {
                    int index = line.indexOf("=");
                    fwFilenameString = line.substring(0, index);
                    toCity = line.substring(index + 1);
                } else {
                    fwFilenameString = line;
                }
                req.setCarrier(fwFilenameString);
                String fromCity = fromCityList.get(randmon(2));
                fwFilenameString = fwFilenameString + "-" + fromCity;
                req.setFrom(fromCity);
                if (toCity == null) {
                    toCity = toCityList.get(randmon(3));
                }
                fwFilenameString = fwFilenameString + "-" + toCity;
                req.setArrive(toCity);
                int num = randmon(20) + 1;
                fwFilenameString = fwFilenameString + "-" + num;
                FileWriter fWriterPu = new FileWriter(new File(preString + fwFilenameString + "-pu.txt"));
                // FileWriter fWriterBr = new FileWriter(new File(preString +
                // fwFilenameString + "-br.txt"));
                try {
                    fareRules.publicFareSearch(req, fWriterPu, num);
                    // fareRules.bargainFareSearch(req, fWriterBr, num);
                } catch (SessionExpireException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static int randmon(int num) {
        double rs = Math.random() * num;
        return (int) Math.floor(rs);

    }
}
