package com.travelzen.etermface.service.ticket;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.common.ufis.util.UfisException;
import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.travelzen.etermface.common.config.cdxg.exception.SessionExpireException;
import com.travelzen.etermface.service.EtermUfisClient;
import com.travelzen.etermface.service.EtermWebClient;
import com.travelzen.etermface.service.PNRParser;
import com.travelzen.etermface.service.constant.UfisStatus;
import com.travelzen.etermface.service.entity.DetrTimeResult;
import com.travelzen.etermface.service.entity.ParseConfBean;
import com.travelzen.framework.core.common.ReturnClass;
import com.travelzen.framework.core.common.ReturnCode;
import com.travelzen.framework.core.exception.BizException;
import com.travelzen.rosetta.eterm.common.pojo.EtermRtResponse;
import com.travelzen.rosetta.eterm.common.pojo.enums.EtermCmdSource;
import com.travelzen.rosetta.eterm.parser.EtermRtParser;

public class DetrTimeParser {

    private static Logger logger = LoggerFactory.getLogger(DetrTimeParser.class);

    public ReturnClass<DetrTimeResult> parse(String pnr) throws SessionExpireException {
        Optional<Pair<Integer, String>> ret = getRtcText(pnr);
        ReturnClass<DetrTimeResult> drRet = new ReturnClass<>();
        if (ret.isPresent()) {
            String[] lines = ret.get().getValue1().split("(\r|\n)+");
            for (int i = 1; i < lines.length; i++) {
                if (lines[i].startsWith("001     ")) {
                    String tarStr = lines[i + ret.get().getValue0()];
                    DetrTimeResult dr = new DetrTimeResult();
                    dr.detrTime = tarStr;
                    drRet.setStatus(ReturnCode.SUCCESS);
                    drRet.setObject(dr);
                    return drRet;
                }
            }
            drRet.setStatus(ReturnCode.ERROR);
            return drRet;
        } else {
            drRet.setStatus(ReturnCode.ERROR);
            return drRet;
        }
    }
    
    private Optional<Pair<Integer, String>> getRtcText(String pnr) {
        if (UfisStatus.active && UfisStatus.detrTime) {
        	EtermUfisClient client = null;
        	try {
        		client = new EtermUfisClient();
                String rtText = client.execRt(pnr, true);
                EtermRtResponse rtResponse = EtermRtParser.parse(rtText, false, EtermCmdSource.UFIS);
                if (null == rtResponse || null == rtResponse.getPassengerInfo() || null == rtResponse.getPassengerInfo().getPassengers()) {
                	logger.warn("DetrTimeParser get unexpect rt result :" + new Gson().toJson(rtResponse));
                    return Optional.of(Pair.with(0, ""));
                }
                int psgNum = rtResponse.getPassengerInfo().getPassengers().size();
                String rtcText = client.execCmd("RTC", true);
                return Optional.of(Pair.with(psgNum, rtcText));
            } catch (UfisException e) {
                logger.error("UfisException:{}", e.getMessage());
            } finally {
            	if (null != client)
        			client.close();
            }
        } else {
//        	ParseConfBean conf = new ParseConfBean();
//            PNRParser parser = new PNRParser(conf);
//            parser.parsePnrForTops(conf, pnr);
//            logger.info("pnr:" + pnr);
//            if (parser.pnrRet == null || parser.pnrRet.PassengerInfo == null || parser.pnrRet.PassengerInfo.Passengers == null) {
//                logger.warn("DetrTimeParser get unexpect info :" + new Gson().toJson(parser.pnrRet));
//                return Optional.of(Pair.with(0, ""));
//            }
//            int psgNum = parser.pnrRet.PassengerInfo.Passengers.size();
//            
//        	EtermWebClient client = new EtermWebClient();
//            try {
//                client.connect();
//                client.getRT(pnr, true);
//                ReturnClass<String> ret = client.rawExecuteCmd("RTC", false);
//                return Optional.of(Pair.with(psgNum, ret.getObject()));
//            } catch (BizException e) {
//                logger.error("err:{}", e.getMessage());
//            } finally {
//                client.close();
//            }
        }

        return Optional.absent();
    }

}
