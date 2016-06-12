package com.travelzen.etermface.service.ticket;

import java.util.List;

import com.travelzen.etermface.service.constant.PnrParserConstant;

/**
 * 国内票号提取
 */
public class DomTktExtractorLogic extends BaseTktExtractorLogic implements ITktExtractorLogic {
    /**
     * 国内:先取OSI 1E， 加上 SSR TKNE， 如果为空， 则取 .T, 如果不为空， 则不处理.T
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

    public static void main(String arv[]) {
//        String str = "31.OSI 1E MUET TN/7812189770471-7812189770480    ";
        String str = "31.OSI 1E MUET TN/7812189770471-7812189770480   ";
        DomTktExtractorLogic del = new DomTktExtractorLogic();
        TicketExtractorInfoBean tib = new TicketExtractorInfoBean();
        tib.extractedTktCnt = 0;
        tib.maxTktCnt = 10;
        tib.passengerCount = 10;
        List<TicketParseInfo> list = del.processOSI_Multiple(str.split("\r"), 0, tib);
        System.out.println(list);
    }
}
