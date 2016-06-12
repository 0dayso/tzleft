package com.travelzen.etermface.service.ticket;

import com.travelzen.etermface.service.PNRParser.RtInfoBean;
import com.travelzen.etermface.service.entity.PnrRet;

public class GeneralTktExtractorHandler implements RtAfterParserHandler {
    ITktExtractorLogic tktExtractorLogic;

    public GeneralTktExtractorHandler(ITktExtractorLogic tktExtractorLogic) {
        super();
        this.tktExtractorLogic = tktExtractorLogic;
    }

    @Override
    public void handle(RtInfoBean rtInfoBean, String[] lines, int startLineIdx, PnrRet pnrRet) {
        tktExtractorLogic.handle(rtInfoBean, lines, startLineIdx, pnrRet);
    }
}
