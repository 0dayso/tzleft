package com.travelzen.etermface.service.ticket;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.common.ufis.util.UfisException;
import com.travelzen.etermface.common.config.cdxg.CdxgConstant;
import com.travelzen.etermface.common.config.cdxg.exception.SessionExpireException;
import com.travelzen.etermface.service.EtermUfisClient;
import com.travelzen.etermface.service.EtermWebClient;
import com.travelzen.etermface.service.constant.UfisStatus;
import com.travelzen.etermface.service.entity.DetrsResult;
import com.travelzen.etermface.service.entity.ParseConfBean;
import com.travelzen.etermface.service.entity.PnrRet;
import com.travelzen.framework.core.common.ReturnClass;
import com.travelzen.framework.core.common.ReturnCode;
import com.travelzen.framework.core.util.TZUtil;

/**
 * 解析detrs出来的文本
 */
public class DetrsParser {
	
    private static Logger logger = LoggerFactory.getLogger(DetrsParser.class);
    
    private String officeId = null;

    public DetrsParser() {
    }

    public DetrsParser(ParseConfBean confBean) {
        officeId = confBean.getOfficeId();
    }

    private String getDetrStr(String tktNumber) {
        String cmd = "detr: TN/" + tktNumber + ",S";
    	String ret = null;
    	if (UfisStatus.active && UfisStatus.detrs) {
    		EtermUfisClient client = null;
        	try {
        		client = new EtermUfisClient(officeId);
				ret = client.execCmd(cmd, true);
			} catch (UfisException e) {
				logger.error(e.getMessage(), e);
			} finally {
				if (null != client)
        			client.close();
			}
    	} else {
    		EtermWebClient client = new EtermWebClient(officeId);
    		client.connect();
    		try {
				ret = client.executeCmdWithRetry(cmd, false).getObject();
			} catch (SessionExpireException e) {
				logger.error(e.getMessage(), e);
			} finally {
				client.close();
			}
    	}
        logger.info("detrs:{} \n{}", tktNumber, ret);
        return ret;
    }

    public ReturnClass<DetrsResult> parserDetrWithRetry(String tktNumber) {
        final int RETRY_ON_SESSION_EXPIRE = 1;
        ReturnClass<DetrsResult> ret = new ReturnClass<DetrsResult>();

        RETRY:
        for (int cnt = 0; cnt < RETRY_ON_SESSION_EXPIRE; cnt++) {
            ret = parserDetr(tktNumber);
			return ret;
        }

        return ret;
    }

    // when called with EtermWebClient, connect is not needed
    public ReturnClass<DetrsResult> parserDetr(String tktNumber) {
        ReturnClass<DetrsResult> retDetrResult = new ReturnClass<>();
        DetrsResult detrResult = new DetrsResult();
        String detrStr;
        String ret = getDetrStr(tktNumber);

        if (null != ret) {
            detrStr = ret;
            if (detrStr.contains("没有权限") || detrStr.contains("AUTHORITY")) {
                retDetrResult.setStatus(ReturnCode.E_ORDER_AUTHORIZATION_ERROR, null);
                return retDetrResult;
            }
            if (detrStr.contains("NO PNR")) {
                retDetrResult.setStatus(ReturnCode.E_NOTEXISTS, null);
                return retDetrResult;
            }
        } else {
            retDetrResult.setStatus(ReturnCode.ERROR, null);
            return retDetrResult;
        }
        // 出票时间/地点 03DEC13/SHANGHAI(666)(08688888(
        Pattern p1 = Pattern.compile(".*\\((.*?)\\(");
        //出票时间/地点      06DEC14/XIAMEN(42)<08673111>
        Pattern p2 = Pattern.compile(".*\\<(.*?)\\>");
        String[] lines = detrStr.split("\r|\n");

        final int ISSUE_OFFICE_STR_LENGTH = 6;

        int lineIdx = 0;
        PROC:
        for (lineIdx = 0; lineIdx < lines.length; lineIdx++) {
            if (StringUtils.contains(lines[lineIdx], "售票处信息")) {
                int targetLineIdx = lineIdx + 1;
                for (int tIdx = targetLineIdx; tIdx < lines.length; tIdx++) {
                    if (StringUtils.isNotBlank(lines[tIdx]) && lines[tIdx].trim().length() == ISSUE_OFFICE_STR_LENGTH) {

                        if (StringUtils.contains(lines[tIdx], "DEV") || StringUtils.contains(lines[tIdx], "-")) {
                            continue;
                        } else {
                            detrResult.issueTktOffice = StringUtils.trim(lines[tIdx]);
                        }
                    }
                }
            }

            // 出票时间/地点 03DEC13/SHANGHAI(666)(08688888(
            if (StringUtils.contains(lines[lineIdx], "出票时间/地点")) {
                String name = "出票时间/地点";
                String value = lines[lineIdx].substring(lines[lineIdx].indexOf(name) + name.length()).trim();
                int index = value.indexOf("/");
                if (index == 7) {
                    detrResult.issueTktDate = value.substring(index - 7, index);
                }
                Matcher m1 = p1.matcher(lines[lineIdx]);
                Matcher m2 = p2.matcher(lines[lineIdx]);
                if (m1.find()) {
                    String agentCode = m1.group(1);
                    if (StringUtils.length(agentCode) == 8) {
                        detrResult.agentCode = agentCode;
                    }
                } else if (m2.find()) {
                    String agentCode = m2.group(1);
                    if (StringUtils.length(agentCode) == 8) {
                        detrResult.agentCode = agentCode;
                    }
                }
                break PROC;
            }
        }

        // 旅客姓名 丛媛瑗
        // 身份识别号码 FFCA002461542874/C
        // 票价 货币 CNY 金额 690.00 \n 实付等值货币 CNY 金额 690.00 付款方式 CA -
        // 税款 CNY 50.00CN CNY 70.00YQ
        // 付款总额 CNY 810.00

        //		票价    货币       CNY  金额   350.00  \n实付等值货币       CNY  金额   350.00    付款方式 CC
        //		税款   CNY 50.00CN  CNY 120.00YQ  CNY EXEMPTXT
        //		付款总额           CNY  520.00

        final String PSG_NAME_TAG = "旅客姓名";
        final String ID_NUMBER = "身份识别号码";
        final String FARE_TAG = "票价";
        final String TAX_TAG = "税款";
        final String TOTAL_TAG = "付款总额";
        final String FC_TAG = "实付等值货币";
        final String FC_KEY = "付款方式";
        final String UNIT_TAG = "货币";
        final String VALUE_TAG = "金额";

        PnrRet.PatResult price = new PnrRet.PatResult();

        NEXT_LINE:
        for (; lineIdx < lines.length; lineIdx++) {

            StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(lines[lineIdx]));

            try {
                NEXT_SEG:
                while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
                    // 旅客姓名 丛媛瑗
                    if (tokenizer.ttype == StreamTokenizer.TT_WORD) {
                        if (StringUtils.equals(PSG_NAME_TAG, tokenizer.sval)) {
                            int idx = lines[lineIdx].indexOf(PSG_NAME_TAG);
                            detrResult.psgName = lines[lineIdx].substring(idx + PSG_NAME_TAG.length()).trim();
                            continue NEXT_LINE;
                        }
                        if (StringUtils.equals(ID_NUMBER, tokenizer.sval)) {
                            int idx = lines[lineIdx].indexOf(ID_NUMBER);
                            detrResult.docCode = lines[lineIdx].substring(idx + ID_NUMBER.length()).trim();
                            continue NEXT_LINE;
                        }
                        // 实付等值货币 CNY 金额 690.00 付款方式 CA -
                        if (StringUtils.equals(FARE_TAG, tokenizer.sval)) {
                            String unit = getMidSubString(UNIT_TAG, VALUE_TAG, lines[lineIdx]);
                            String value = getMidSubString(VALUE_TAG, FC_TAG, lines[lineIdx]);
                            if (!StringUtils.contains(lines[lineIdx], FC_TAG)) {
                                value = lines[lineIdx].substring(lines[lineIdx].indexOf(VALUE_TAG) + VALUE_TAG.length()).trim();
                            }

                            if (StringUtils.isNotBlank(unit) && StringUtils.isNotBlank(value)) {
                                price.fare = unit + "%" + value;
                            } else {
                                price.fare = value;
                            }

                            //			    CC/Y1/*9KW00142                    -                                                                          税款
                            int index = lines[lineIdx].indexOf(FC_KEY);
                            if (index > -1) {
                                price.fc = lines[lineIdx].substring(index + FC_KEY.length()).trim();
                            }

                            continue NEXT_LINE;
                        }

                        if (StringUtils.contains(lines[lineIdx], FC_KEY)) {
                            int index = lines[lineIdx].indexOf(FC_KEY);
                            price.fc = lines[lineIdx].substring(index + FC_KEY.length()).trim();
                            continue NEXT_LINE;
                        }

                        // 税款 CNY 50.00CN CNY 70.00YQ
                        // 税款   CNY 210.00OB  PD  50.00CN  PD  120.00YQ    92
                        if (StringUtils.equals(TAX_TAG, tokenizer.sval)) {
                            //税款可能多行
                            if (price.internalTaxs == null) {
                                price.internalTaxs = "";
                            }
                            if (StringUtils.isNotBlank(price.internalTaxs)) {
                                price.internalTaxs += "  ";
                            }
                            price.internalTaxs += lines[lineIdx].substring(TAX_TAG.length()).trim();

                            int tt = tokenizer.nextToken();
                            NEXT_TK:
                            while (tt != StreamTokenizer.TT_EOF) {
                                if (tt == StreamTokenizer.TT_NUMBER) {
                                    String nvalue = twoDigitFormat(tokenizer.nval);

                                    if (tokenizer.nextToken() == StreamTokenizer.TT_WORD) {
                                        if ("CN".equals(tokenizer.sval)) {
                                            price.tax = nvalue;
                                        }
                                        if ("YQ".equals(tokenizer.sval)) {
                                            price.yq = nvalue;
                                        }
                                        if ("OB".equals(tokenizer.sval)) {
                                            price.ob = nvalue;
                                        }
                                        tt = tokenizer.nextToken();
                                        continue NEXT_TK;
                                    }
                                }
                                tt = tokenizer.nextToken();
                            }
                        }

                        if (StringUtils.equals(TOTAL_TAG, tokenizer.sval)) {
                            String nvalue = getNumberFromString(lines[lineIdx]);
                            if (StringUtils.isNotBlank(nvalue)) {
                                String unit = getMidSubString(TOTAL_TAG, nvalue, lines[lineIdx]);
                                price.total = getTransmitMoney(unit, nvalue);
                                break NEXT_LINE;
                            }
                        }
                        continue NEXT_SEG;
                    }
                }
            } catch (IOException e) {
                logger.error(TZUtil.stringifyException(e));
            }
        }

        logger.info(price.toString());

        detrResult.price = price;
        if (StringUtils.isBlank(detrResult.issueTktOffice)) {
            detrResult.issueTktOffice = CdxgConstant.DEFAULT_OFFICE;
            detrResult.agentCode = CdxgConstant.DEFAULT_AGENT_CODE;
        }
        if (StringUtils.isNotBlank(detrResult.agentCode)) {
            retDetrResult.setObject(detrResult);
            return retDetrResult;
        } else {
            retDetrResult.setObjects(new String[]{detrStr});
            retDetrResult.setStatus(ReturnCode.ERROR);
            return retDetrResult;
        }

    }

    /**
     * 货币单位值传输格式
     * 若存在单位(CNY)则 CNY%354.0，否则直接返回alue值
     *
     * @param unit
     * @param value
     * @return
     */
    private static String getTransmitMoney(String unit, String value) {
        if (StringUtils.isNotBlank(unit)) {
            return unit + "%" + value;
        }

        return value;
    }

    /**
     * 截取字符串中包含的第一个数字
     *
     * @param totalStr
     * @return
     */
    private static String getNumberFromString(String totalStr) {
        String s = "\\d+.\\d+";

        Pattern pattern = Pattern.compile(s);
        Matcher matcher = pattern.matcher(totalStr);

        if (matcher.find()) {
            return matcher.group();
        }

        return null;
    }

    /**
     * 返回double类型两位小数格式字符串
     *
     * @param number
     * @return
     */
    private static String twoDigitFormat(double number) {
        DecimalFormat format = new DecimalFormat("0.00");
        return format.format(number);
    }

    /**
     * 获取中间段字符串
     *
     * @param befStr
     * @param aftStr
     * @param totalStr
     * @return
     */
    private static String getMidSubString(String befStr, String aftStr, String totalStr) {
        if (StringUtils.isBlank(totalStr)) {
            return "";
        }

        int befIndex = totalStr.indexOf(befStr);
        int aftIndex = totalStr.indexOf(aftStr);

        if (befIndex == -1 || aftIndex == -1) {
            return "";
        }

        if ((befIndex + befStr.length()) >= aftIndex) {
            return "";
        }

        return totalStr.substring(befIndex + befStr.length(), aftIndex).trim();
    }
}
