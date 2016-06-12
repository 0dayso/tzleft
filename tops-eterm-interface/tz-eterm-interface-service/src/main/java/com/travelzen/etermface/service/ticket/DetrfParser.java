package com.travelzen.etermface.service.ticket;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.common.ufis.util.UfisException;
import com.travelzen.etermface.common.config.cdxg.exception.SessionExpireException;
import com.travelzen.etermface.service.EtermUfisClient;
import com.travelzen.etermface.service.EtermWebClient;
import com.travelzen.etermface.service.constant.UfisStatus;
import com.travelzen.etermface.service.entity.DetrfResult;
import com.travelzen.etermface.service.entity.ParseConfBean;
import com.travelzen.etermface.service.entity.config.FullDetrConfig;
import com.travelzen.framework.core.common.ReturnClass;
import com.travelzen.framework.core.common.ReturnCode;

public class DetrfParser {

    private static Logger logger = LoggerFactory.getLogger(DetrfParser.class);

    private String officeId = null;

    public DetrfParser() {
    }

    public DetrfParser(ParseConfBean confBean) {
        officeId = confBean.getOfficeId();
    }

    private String getRawResultStr(String tktNumber) {
    	String cmd = "detr: TN/" + tktNumber + ",f";
    	String ret = null;
    	if (UfisStatus.active && UfisStatus.detrf) {
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
        logger.info("detrf:{} \n{}", tktNumber, ret);
        return ret;
    }

    private ReturnClass<DetrfResult> rawParse(String detrStr) {
        ReturnClass<DetrfResult> retDetrResult = new ReturnClass<>();

        DetrfResult result = new DetrfResult();
        //System.out.println(detrStr);
        String[] lines = StringUtils.split(detrStr, "[\r\n]+");

        PROC:
        for (int i = 0; i < lines.length; i++) {
            final String NAME = "NAME: ";
            if (StringUtils.contains(lines[i], NAME)) {
                int idx = lines[i].indexOf(NAME);
                int idx2 = lines[i].indexOf("TKTN:");
                result.name = StringUtils.trim(lines[i].substring(idx + NAME.length(), idx2));

                continue PROC;
            }

            final String RECP = "RCPT:";
            if (StringUtils.contains(lines[i], RECP)) {
                for (int k = i + 1; k < lines.length; k++) {
                    String[] ks = StringUtils.trim(lines[k]).split("\\s+");
                    if (ks.length >= 2) {
                        result.rcpt.add(ks[1]);
                    }
                }
                continue PROC;
            }
        }
        retDetrResult.setStatus(ReturnCode.SUCCESS);
        retDetrResult.setObject(result);

        return retDetrResult;

    }

    public ReturnClass<DetrfResult> parse(FullDetrConfig param) {
        ReturnClass<DetrfResult> retDetrResult = new ReturnClass<>();
        String tktNumber = param.tktNumber;
        String ret = getRawResultStr(tktNumber);
        if (null != ret) {
            if (ret.contains("没有权限") || ret.contains("AUTHORITY")) {
                retDetrResult.setStatus(ReturnCode.E_ORDER_AUTHORIZATION_ERROR);
                return retDetrResult;
            } else {
                return rawParse(ret);
            }
        } else {
            retDetrResult.setStatus(ReturnCode.ERROR, null);
            return retDetrResult;
        }
    }

}
