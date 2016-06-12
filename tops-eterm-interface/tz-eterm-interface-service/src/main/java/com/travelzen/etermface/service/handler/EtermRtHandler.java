package com.travelzen.etermface.service.handler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.etermface.common.config.cdxg.exception.SessionExpireException;
import com.travelzen.etermface.service.EtermUfisClient;
import com.travelzen.etermface.service.EtermWebClient;
import com.travelzen.etermface.service.constant.UfisStatus;
import com.travelzen.etermface.service.entity.PnrContentResponse;
import com.travelzen.framework.core.common.ReturnClass;
import com.travelzen.framework.core.common.ReturnCode;

/**
 * EtermRtHandler
 * <p>
 * @author yiming.yan
 * @Date Jan 11, 2016
 */
public class EtermRtHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(EtermRtHandler.class);
	
	public static String handle(String pnr, String office) {
		PnrContentResponse response = null;
		if (UfisStatus.active && UfisStatus.rt) {
			response = rtByUfis(pnr, office);
	        if (null == response || ReturnCode.ERROR.getErrorCode().equals(response.getReturnCode())) {
	            response = rtByUfis(pnr, office);
	        }
		} else {
			response = rtByEterm(pnr, office);
	        if (null == response || ReturnCode.ERROR.getErrorCode().equals(response.getReturnCode())) {
	            response = rtByEterm(pnr, office);
	        }
		}
        return PnrContentResponse.convertToXML(response);
	}
	
	private static final Pattern GROUP_PATTERN = Pattern.compile(
            "(?:^|\\n|\\r) *0\\.\\d+.+ NM\\d+");
	
    private static PnrContentResponse rtByEterm(String pnr, String office) {
        EtermWebClient client = new EtermWebClient();

        PnrContentResponse response = null;
        try {
            client.connect(office);
        } catch (Exception e) {
            client.close();
            LOGGER.error(e.getMessage(), e);
            response = new PnrContentResponse();
            response.setReturnCode(ReturnCode.ERROR.getErrorCode());
            return response;
        }
        
        try {
            ReturnClass<String> rtClass = client.getRT(pnr, false);
            response = new PnrContentResponse();
            if (rtClass.isSuccess()) {
                response.setReturnCode(ReturnCode.SUCCESS.getErrorCode());
                Matcher matcher_group = GROUP_PATTERN.matcher(rtClass.getObject());
                if (matcher_group.find()) {
                    response.setContent(client.getRTN(false));
                } else {
                    response.setContent(rtClass.getObject());
                }
            } else {
                response.setReturnCode(ReturnCode.ERROR.getErrorCode());
            }
        } catch (SessionExpireException e) {
            LOGGER.error(e.getMessage(), e);
            response = new PnrContentResponse();
            response.setReturnCode(ReturnCode.ERROR.getErrorCode());
            return response;
        } finally {
            client.close();
        }

        return response;
    }
    
    private static PnrContentResponse rtByUfis(String pnr, String office) {
        PnrContentResponse response = new PnrContentResponse();
        EtermUfisClient client = null;
		try {
			client = new EtermUfisClient(office);
            String value = client.execRtAll("RT " + pnr);
            if (value != null) {
                response.setReturnCode(ReturnCode.SUCCESS.getErrorCode());
                response.setContent(value);
            }
        } catch (Exception e) {
        	LOGGER.error(e.getMessage(), e);
            response.setReturnCode(ReturnCode.ERROR.getErrorCode());
            return response;
        } finally {
        	if (null != client)
				client.close();
        }
        
        return response;
    }

}
