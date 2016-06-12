package com.travelzen.etermface.service.handler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.common.ufis.util.UfisException;
import com.travelzen.etermface.common.config.cdxg.exception.SessionExpireException;
import com.travelzen.etermface.service.EtermUfisClient;
import com.travelzen.etermface.service.EtermWebClient;
import com.travelzen.etermface.service.constant.UfisStatus;
import com.travelzen.framework.core.common.ReturnClass;
import com.travelzen.rosetta.eterm.common.pojo.EtermRtResponse;
import com.travelzen.rosetta.eterm.common.pojo.enums.EtermCmdSource;
import com.travelzen.rosetta.eterm.parser.EtermRtParser;

/**
 * 通过pnr提取票号处理类
 * <p>
 * @author yiming.yan
 * @Date Dec 8, 2015
 */
public class TicketNoExtractHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TicketNoExtractHandler.class);
	
	public static EtermRtResponse handle(String pnr, String office, boolean isDomestic) {
		String result = null;
		if (UfisStatus.active && UfisStatus.extractTktNoByPnr) {
			EtermUfisClient client = null;
			try {
				client = new EtermUfisClient();
				result = client.execRtAll("RT " + pnr);
			} catch (UfisException e) {
				LOGGER.error("Ufis异常: {}", e.getMessage());
			} finally {
				if (null != client)
					client.close();
			}
		} else {
			EtermWebClient client = new EtermWebClient();
			client.connect(office);
			try {
	            ReturnClass<String> rtClass = client.getRT(pnr, false);
	            if (rtClass.isSuccess()) {
	                Matcher matcher_group = Pattern.compile(
	                        "(?:^|\\n|\\r) *0\\.\\d+.+ NM\\d+").matcher(rtClass.getObject());
	                if (matcher_group.find()) {
	                	result = client.getRTN(false);
	                } else {
	                	result = rtClass.getObject();
	                }
	            }
	        } catch (SessionExpireException e) {
	            LOGGER.error("SessionExpireException异常: {}", e.getMessage());
	        } finally {
	            client.close();
	        }
		}
		LOGGER.info("RT {} 得到文本：{}", pnr, result);
		EtermCmdSource etermCmdSource = UfisStatus.active?EtermCmdSource.UFIS:EtermCmdSource.ETERM;
		EtermRtResponse etermRtResponse = EtermRtParser.parse(result, isDomestic, etermCmdSource);
		LOGGER.info("返回RT解析结果：{}", etermRtResponse);
		return etermRtResponse;
	}

}
