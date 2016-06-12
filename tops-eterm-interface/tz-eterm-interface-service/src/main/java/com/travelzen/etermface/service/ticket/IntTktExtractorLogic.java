package com.travelzen.etermface.service.ticket;

import java.util.List;

import com.travelzen.etermface.service.constant.PnrParserConstant;

/**
 * 国际票号提取
 */
public class IntTktExtractorLogic extends BaseTktExtractorLogic implements ITktExtractorLogic {
    /**
     * 国际:先取SSR TKNE,OSI 1E 如果票号已经够了，不再取TN ， 如果不够， 再取TN
     *
     * @param tktList
     * @param teBean
     * @param lines
     * @param startLineIdx
     */
    @Override
    protected void processTkt(List<TicketParseInfo> tktList, TicketExtractorInfoBean teBean, String[] lines, int startLineIdx) {
        teBean.ticketMode = PnrParserConstant.TKT_MODE_B2B;
        tktList.addAll(processOSI_Multiple(lines, startLineIdx, teBean));
        tktList.addAll(processOSI_Single(lines, startLineIdx, teBean));
    }
}
