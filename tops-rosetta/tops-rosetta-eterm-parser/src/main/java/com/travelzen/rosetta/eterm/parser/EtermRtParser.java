package com.travelzen.rosetta.eterm.parser;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.rosetta.eterm.common.pojo.EtermRtResponse;
import com.travelzen.rosetta.eterm.common.pojo.enums.EtermCmdSource;
import com.travelzen.rosetta.eterm.parser.rt.state.PnrParserStateHandler;

/**
 * Eterm RT　指令解析类
 * <p>
 * @author yiming.yan
 * @Date Dec 04, 2015
 */
public enum EtermRtParser {
	
	;

    private static final Logger LOGGER = LoggerFactory.getLogger(EtermRtParser.class);

    /**
	 * 解析Eterm RT文本
	 * <p>
	 * @param text Eterm RT文本
	 * @param isDomestic 国内/国际
	 * @param etermCmdSource Eterm 文本来源
	 * @return EtermRtResponse
	 */
    public static EtermRtResponse parse(String pnr, boolean isDomestic, EtermCmdSource etermCmdSource) {
    	LOGGER.info("PNR解析请求：" + pnr);
        long startTime = new Date().getTime();
        String pnrNo = null;
        String pnrContent = pnr;
        PnrParserStateHandler stateMachine =
                new PnrParserStateHandler(pnrNo, pnrContent, isDomestic);
        EtermRtResponse etermRtResponse = stateMachine.process();
        long endTime = new Date().getTime();
        LOGGER.info("PNR解析用时：{}ms", endTime-startTime);
        LOGGER.info("PNR解析结果：" + etermRtResponse);
        return etermRtResponse;
    }

}
