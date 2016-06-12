package com.travelzen.etermface.service.ticket;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.travelzen.etermface.service.EtermUfisClient;
import com.travelzen.etermface.service.EtermWebClient;
import com.travelzen.etermface.service.PNRParser.RtInfoBean;
import com.travelzen.etermface.service.constant.PnrParserConstant;
import com.travelzen.etermface.service.entity.DetrsResult;
import com.travelzen.etermface.service.entity.PnrRet;
import com.travelzen.etermface.service.entity.PnrRet.PassengerInfo.Passenger;
import com.travelzen.etermface.service.entity.PnrRet.TicketInfo;
import com.travelzen.etermface.service.entity.TkneInfo;
import com.travelzen.framework.core.common.ReturnClass;

/**
 * 票号提取抽象类
 * <p/>
 * 特别说明：很多正则表达式的Pattern没有设置未静态变量，主要是为了代码的可读性
 */
public abstract class BaseTktExtractorLogic implements ITktExtractorLogic {
    private static Logger logger = LoggerFactory.getLogger(BaseTktExtractorLogic.class);

    private static Comparator<TkneInfo> tkneInfoComparator;

    static {
        tkneInfoComparator = new Comparator<TkneInfo>() {
            @Override
            public int compare(TkneInfo o1, TkneInfo o2) {
                int cond1 = o1.psgIdx - o2.psgIdx;
                if (cond1 != 0) {
                    return cond1;
                } else {
                    return o1.tktType.compareTo(o2.tktType);
                }
            }
        };
    }


    // 票类判断： ticketMode   只要判断B2B 如果是B2B的返回B2B 非B2B的都返回空
    Map<String, TkneInfo> tktNumber2fltsegMap = Maps.newHashMap();
    Map<String, Integer> flt6codeToIdxMap = Maps.newHashMap();

    Map<Integer, Boolean> lineProceed = Maps.newHashMap();
    Set<String> tktNumProceed = Sets.newHashSet();

    protected abstract void processTkt(List<TicketParseInfo> tktList, TicketExtractorInfoBean teBean, String[] lines, int startLineIdx);

    /**
     * 国际：先取P_SSR_TKNE,加上SSR
     * 国内：先取SSR TKNE,加上SSR
     * 差别在于国际和国内的SSR TKNE解析规则不一样
     *
     * @param rtInfoBean
     * @param lines
     * @param startLineIdx
     * @param pnrRet
     */
    public void handle(RtInfoBean rtInfoBean, String[] lines, int startLineIdx, PnrRet pnrRet) {
        for (int i = 0; i < lines.length; i++) {
            lineProceed.put(i, false);
        }

        int idx = 1;
        for (PnrRet.Flight flt : pnrRet.Flights) {
            if (!StringUtils.equals(flt.FltType, PnrParserConstant.CONST_SEGTYPE_ARNK)) {
                flt6codeToIdxMap.put(StringUtils.upperCase(flt.BoardPoint + flt.OffPoint), idx);
                idx++;
            }
        }
        List<TkneInfo> tktPsgFltIdList = Lists.newArrayList();
        if (!rtInfoBean.isDomestic()) {
            tktPsgFltIdList = parseTktPsgidxFltidxList(lines, startLineIdx);
        } else {
            //非团队票，首先解析TKNE项，如果团队票不解析此项，直接通过OSI 1E获取，否者会出现票号乱序现象
            if (!rtInfoBean.isGroup) {
                tktPsgFltIdList = parseTKNEDomestic(lines, startLineIdx);
            }
        }

        Set<String> tkneTktNumSet = Sets.newHashSet();
        for (TkneInfo tk : tktPsgFltIdList) {
            tkneTktNumSet.add(tk.tktNum);
        }

        logger.info("tktInfo:{}", tktPsgFltIdList.toString());

        //this is the max value, the real tkt count will never exceed this number
        final int maxTktCnt = pnrRet.PassengerInfo.Passengers.size() * pnrRet.Flights.size();

        TicketExtractorInfoBean teBean = new TicketExtractorInfoBean();

        teBean.maxTktCnt = maxTktCnt;
        teBean.lastTktLineIdx = lines.length - 1;
        teBean.passengerCount = pnrRet.PassengerInfo.Passengers.size();
        teBean.etermWebClient = rtInfoBean.etermWebClient;
        teBean.pnrRet = rtInfoBean.pnrRet;
        teBean.isDomestic = rtInfoBean.isDomestic();

        if (tktPsgFltIdList.size() != 0) {
            teBean.personIdxOffset = Iterables.getLast(tktPsgFltIdList).psgIdx + 1;
        }

        List<TicketParseInfo> tktList = Lists.newArrayList();
        //门面模式的回调，国内和国际处理不一样
        processTkt(tktList, teBean, lines, startLineIdx);

        Map<String, String> validTktNumberStatusMap = Maps.newHashMap();
        for (TicketParseInfo tktPsg : tktList) {
            validTktNumberStatusMap.put(tktPsg.tktNum.replace("-", ""), tktPsg.ticketStatus);
        }

        //如果tktPsgidxFltidxList这个存在数据, 就按照它里面的对照关系来出 票号->航段（1个航段）-乘客
        //如果tktPsgidxFltidxList里不存在, 则认为多个航段只有1张票, 每个票号->1个乘客, 多个航段.
        List<TicketInfo> ticketInfos = Lists.newArrayList();
        if (!tktPsgFltIdList.isEmpty()) {
            for (TkneInfo item : tktPsgFltIdList) {
                String ticketNo = item.tktNum;
                String psgID = item.psgIdx + "";
                TicketInfo ticketInfo = new TicketInfo(ticketNo, item.fltsegIdxs, psgID, item.isAccurate);
                ticketInfo.ticketType = item.tktType;
                ticketInfo.ticketStatus = validTktNumberStatusMap.get(ticketNo);

                ticketInfos.add(ticketInfo);
            }
        }

        if (teBean.extractFailed) {
            return;
        }

        for (TicketParseInfo tktPsg : tktList) {
            // only process the tktNumber that is no appear in tkne list
            if (!tkneTktNumSet.contains(tktPsg.tktNum)) {
                List<String> fltsegIDs = Lists.newArrayList();
                for (int i = 0; i < pnrRet.Flights.size(); i++) {
                    fltsegIDs.add(String.valueOf(i + 1));
                }

                String ticketNo = tktPsg.tktNum;
                String psgID = String.valueOf(tktPsg.psgIdx);
                TicketInfo ticketInfo = new TicketInfo(ticketNo, fltsegIDs, psgID);
                ticketInfo.isAccurate = tktPsg.isAccurate;
                if (StringUtils.isNotBlank(tktPsg.ticketType)) {
                    ticketInfo.ticketType = tktPsg.ticketType;
                }
                if (StringUtils.isNotBlank(tktPsg.ticketMode)) {
                    ticketInfo.ticketMode = tktPsg.ticketMode;
                }
                ticketInfo.ticketStatus = validTktNumberStatusMap.get(ticketNo);

                ticketInfos.add(ticketInfo);
            }
        }
        pnrRet.TicketInfos = ticketInfos;
        logger.info(tktList.toString());
    }

    public static class TicketExtractorInfoBean {
        PnrRet pnrRet;
        EtermUfisClient etermWebClient;
        public int maxTktCnt;
        public int lastTktLineIdx;
        public int extractedTktCnt;
        public int personIdxOffset = 1;
        public String ticketMode;
        public int passengerCount;
        public boolean extractFailed = false;
        public boolean isDomestic;

        public boolean needFurtherExtract() {
            return extractedTktCnt < maxTktCnt;
        }

        public void incExtractedTktCnt(int cnt) {
            extractedTktCnt += cnt;
        }
    }

    /**
     * 处理单张票的逻辑,包含如下两种类型
     * <p/>
     * 14.OSI 1E MUET TN/7812133427202 单张票
     * 14.OSI 1E MUET TN/781-2133427202 单张票
     *
     * @param lines
     * @param startLineIdx
     * @param tktExtrInfoBean
     * @return
     */
    protected List<TicketParseInfo> processOSI_Single(String[] lines, int startLineIdx, TicketExtractorInfoBean tktExtrInfoBean) {
        if (!tktExtrInfoBean.needFurtherExtract()) {
            return Lists.newArrayList();
        }

        List<TicketParseInfo> tktList = Lists.newArrayList();
        boolean findFirstTkt = false;
        int personIdx = 1;

        // 14.OSI 1E MUET TN/7812133427202 单张票
        Pattern OSI_p1 = Pattern.compile("\\s*?OSI\\s+.{2}\\s.*TN\\/(\\d{10,13})");
        // OSI 1E MUET TN/781-2133427202 单张票
        // OSI 1E TN/876-1234554321 单张票
        Pattern OSI_p2 = Pattern.compile("\\s*?OSI\\s+.{2}.*TN\\/(\\d{3}-\\d{10})");
        Pattern OSI_p3 = Pattern.compile("\\s*?OSI\\s+.{2}.*TKNO\\/(\\d{3}-\\d{10})");
        FIND_TKT:
        for (int i = lines.length; i > startLineIdx; i--) {
            int lineNum = i - 1;

            if (lineProceed.get(lineNum).booleanValue()) {
                continue FIND_TKT;
            }
            String tktNum = "";
            Matcher m = null;
            while (true) {
                Matcher m2 = OSI_p1.matcher(lines[lineNum]);
                if (m2.find()) {
                    m = m2;
                    break;
                }

                Matcher m3 = OSI_p2.matcher(lines[lineNum]);
                if (m3.find()) {
                    m = m3;
                    break;
                }

                Matcher m4 = OSI_p3.matcher(lines[lineNum]);
                if (m4.find()) {
                    m = m4;
                    break;
                }
                break;
            }

            if (null != m) {
                lineProceed.put(lineNum, true);
                tktExtrInfoBean.lastTktLineIdx = i;
                findFirstTkt = true;
                tktNum = m.group(1);
                tktNum = tktNum.replace("-", "");
                if (tktNumProceed.contains(tktNum)) {
                    continue FIND_TKT;
                }
                tktNumProceed.add(tktNum);
                TicketParseInfo tp = new TicketParseInfo(tktNum, personIdx);
                tp.ticketMode = PnrParserConstant.TKT_MODE_B2B;
                tktList.add(tp);
                personIdx++;
            } else {
                if (findFirstTkt) {
                    break FIND_TKT;
                }
            }
        }
        reverseTktIdxWhenNoSsrTkneInfo(tktList);
        tktExtrInfoBean.incExtractedTktCnt(tktList.size());
        return tktList;
    }

    /**
     * 处理多张票的逻辑
     *
     * @param lines
     * @param startLineIdx
     * @param ticketExtractorInfoBean
     * @return
     */
    protected List<TicketParseInfo> processOSI_Multiple(String[] lines, int startLineIdx, TicketExtractorInfoBean ticketExtractorInfoBean) {
        ticketExtractorInfoBean.personIdxOffset = 1;
        // OSI 1E MUET TN/7812121563445-7812131563446 票号段
        // OSI 1E MUET TN/2121563445-2131563446 票号段
        // OSI 1E CAET TN/9992119347860-9992119347861
        // OSI 1E ET-3UET TN/8761234554321
        // OSI 1E HOET TN/0182317521554-0182317521557
        Pattern OSI_multiple = Pattern.compile("\\s*?OSI\\s+.{2}.*TN\\/(\\d{10,13})-(\\d{10,13})");

        if (!ticketExtractorInfoBean.needFurtherExtract()) {
            return Lists.newArrayList();
        }
        List<List<TicketParseInfo>> tktlistList = Lists.newArrayList();
        boolean findFirstTkt = false;

        FIND_TKT:
        for (int i = lines.length; i > startLineIdx; i--) {
            int lineNum = i - 1;
            if (lineProceed.get(lineNum).booleanValue()) {
                continue FIND_TKT;
            }
            String line = lines[lineNum].intern();
            Matcher m = OSI_multiple.matcher(line);
            if (m.find()) {
                lineProceed.put(lineNum, true);
                ticketExtractorInfoBean.lastTktLineIdx = i;
                List<TicketParseInfo> tktList = Lists.newArrayList();
                findFirstTkt = true;
                // 起止票号位数必须相等， 10或13位
                checkArgument(m.group(1).length() == m.group(2).length(), "invalid ticket number:%s", line);

                long startTktNum = NumberUtils.toLong(m.group(1));
                long endTktNum = NumberUtils.toLong(m.group(2));
                boolean useDetr = false;
                Map<String, Integer> foidPsgidMap = Maps.newHashMap();
                Map<String, Integer> psgNamePsgidMap = Maps.newHashMap();

                int tktcn2skiptBeforeAddPsgidxCnt = 1;
                if ((endTktNum - startTktNum + 1) % ticketExtractorInfoBean.passengerCount == 0) {
                    tktcn2skiptBeforeAddPsgidxCnt = (int) ((endTktNum - startTktNum + 1) / ticketExtractorInfoBean.passengerCount);
                } else {
                    if (ticketExtractorInfoBean.isDomestic) {
                        ticketExtractorInfoBean.extractFailed = true;
                        return Lists.newArrayList();
                    } else {
                        // international tkt
                        useDetr = true;
                        List<Passenger> psgs = ticketExtractorInfoBean.pnrRet.PassengerInfo.Passengers;
                        for (int idx = 0; idx < psgs.size(); idx++) {
                            Passenger psg = psgs.get(idx);
                            psgNamePsgidMap.put(psg.Name, idx + 1);
                        }
                    }
                }

                int tktCnt = 0;
                int personIdx = ticketExtractorInfoBean.personIdxOffset;
                //定义票号长度
                int TKT_NUMBER_LENGTH = 13;
                PROCESS_LINE:
                for (long idx = startTktNum; idx <= endTktNum; idx++) {
                    String tktNum = StringUtils.leftPad(String.valueOf(idx), TKT_NUMBER_LENGTH, "0");
                    if (tktNumProceed.contains(tktNum)) {
                        continue PROCESS_LINE;
                    }
                    tktNumProceed.add(tktNum);
                    TicketParseInfo tp = new TicketParseInfo(tktNum, personIdx);
                    tp.ticketMode = ticketExtractorInfoBean.ticketMode;

                    tktCnt++;
                    if (useDetr) {
                        DetrsParser dp = new DetrsParser();
                        ReturnClass<DetrsResult> ret = dp.parserDetrWithRetry(tktNum);
                        if (ret.isSuccess()) {
                            if (psgNamePsgidMap.containsKey(ret.getObject().psgName)) {
                                tp.psgIdx = psgNamePsgidMap.get(ret.getObject().psgName);
                                tp.isAccurate = true;
                            }
                        }
                    } else {
                        if (tktCnt % tktcn2skiptBeforeAddPsgidxCnt == 0) {
                            personIdx++;
                        }
                    }
                    tktList.add(tp);
                    // prevent error ticketNumber
                    if (personIdx == ticketExtractorInfoBean.maxTktCnt + 1) {
                        break PROCESS_LINE;
                    }
                }
                tktlistList.add(tktList);
            } else {
                if (findFirstTkt) {
                    break FIND_TKT;
                }
            }
        }
        Collections.reverse(tktlistList);
        List<TicketParseInfo> finalList = Lists.newArrayList();
        for (List<TicketParseInfo> list : tktlistList) {
            finalList.addAll(list);
        }
        ticketExtractorInfoBean.incExtractedTktCnt(finalList.size());
        return finalList;
    }

    /**
     * 因为票号的parser是从后到前处理的, 所以, 处理完票号, 需要再倒置一次 passeger和 tktIdx的对应关系
     */
    private void reverseTktIdxWhenNoSsrTkneInfo(List<TicketParseInfo> tktList) {
        int cnt = tktList.size();
        for (TicketParseInfo item : tktList) {
            if (!item.isAccurate) {
                int idx = item.psgIdx;
                int realIdx = cnt + 1 - idx;
                item.psgIdx = realIdx;
            }
        }
    }

    /**
     * 通过P_SSR_TKNE,获取国际机票的票号
     * P_SSR_TKNE 模式对应的是非B2B
     *
     * @param lines
     * @param startLineIdx
     */
    private List<TkneInfo> parseTktPsgidxFltidxList(String[] lines, int startLineIdx) {
        // 8.SSR TKNE FM HK1 SHAHKG 845 R08MAY 7743936980810/1/P1
        // 9.SSR TKNE FM HK1 HKGSHA 8238 N11MAY 7743936980810/2/P1
        Pattern P_SSR_TKNE = Pattern.compile("\\s*?SSR\\s+TKNE\\s+(.*?)$");
        //处理:7743936980810/1/P1,沒有处理:7812145022222/1/S/P1
        Pattern P_TKNE_INFO = Pattern.compile("(.*?)\\/(\\d+)\\/P(\\d+)");
        int TKT_OFFSET_1BASE = 6;


        List<TkneInfo> tktInfoList = Lists.newArrayList();
        FIND_TKT_LINE:
        for (int i = startLineIdx; i < lines.length; i++) {
            Matcher m = P_SSR_TKNE.matcher(lines[i]);
            if (m.find()) {
                TkneInfo tkneInfo = new TkneInfo();
                tkneInfo.isAccurate = true;

                String tktInfo = m.group(1);
                String[] infos = tktInfo.trim().split("\\s+");

                if (infos.length < 6) {
                    continue FIND_TKT_LINE;
                }

                checkPositionIndex(TKT_OFFSET_1BASE, infos.length);
                String tktStr = infos[TKT_OFFSET_1BASE - 1];
                if (StringUtils.contains(tktStr, "DPN")) {
                    continue FIND_TKT_LINE;
                }

                Matcher mTktInfo = P_TKNE_INFO.matcher(tktStr);
                if (mTktInfo.find()) {
                    String tktNumber = mTktInfo.group(1);

                    tktNumber = tktNumber.replace("-", "");

                    boolean needAddNewItem = true;
                    if (tktNumProceed.contains(tktNumber)) {
                        needAddNewItem = false;
                    }
                    tktNumProceed.add(tktNumber);

                    String tktType = "";
                    String INF = "INF";
                    if (StringUtils.startsWith(tktNumber, INF)) {
                        tktType = INF;
                        tktNumber = tktNumber.substring(INF.length());
                    }

                    tkneInfo.flt6code = StringUtils.upperCase(infos[2]);

                    int fltsegIdx = NumberUtils.toInt(mTktInfo.group(2));

                    if (flt6codeToIdxMap.containsKey(tkneInfo.flt6code)) {
                        fltsegIdx = flt6codeToIdxMap.get(tkneInfo.flt6code);
                    }

                    int psgIdx = NumberUtils.toInt(mTktInfo.group(3));

                    tkneInfo.psgIdx = psgIdx;
                    tkneInfo.tktType = tktType;
                    tkneInfo.tktNum = tktNumber;

                    if (infos.length > 3) {
                        tkneInfo.fltNum = infos[3];
                    }
                    if (infos.length > 4) {
                        tkneInfo.classCode = StringUtils.substring(infos[4], 0, 1);
                        tkneInfo.departDate = StringUtils.substring(infos[4], 1);
                    }

                    if (needAddNewItem) {
                        tkneInfo.fltsegIdxs.add(fltsegIdx + "");
                        tktInfoList.add(tkneInfo);

                        tktNumber2fltsegMap.put(tktNumber, tkneInfo);
                    } else {
                        tktNumber2fltsegMap.get(tktNumber).fltsegIdxs.add(fltsegIdx + "");
                    }
                }
            }
        }

        // order by psgIdx and ticketType(INF)
        Collections.sort(tktInfoList, tkneInfoComparator);

        return tktInfoList;
    }

    /**
     * 解析国内的TKNE
     *
     * @param lines
     * @param startLineIdx
     * @return
     */
    private List<TkneInfo> parseTKNEDomestic(String[] lines, int startLineIdx) {
        //SSR国内TKNE头
        Pattern P_SSR_TKNE_Demestic = Pattern.compile("\\s*?SSR\\s+TKNE\\s+(.*?)$");
        //SSR中包含HK才解析票号
        String P_SSR_TKNE_HK = "HK";
        // FM HK1 SHAHKG 845 R08MAY 7743936980810/1/P1
        final int TKT_OFFSET_1BASE = 6;
        //7812145022222/1/S/P1和7812145022222/1/P1
        Pattern P_TKNE_INFO = Pattern.compile("(.*?)\\/(\\d+)(\\/?)(.*?)\\/P(\\d+)");

        List<TkneInfo> tktInfoList = Lists.newArrayList();
        FIND_TKT_LINE:
        for (int i = startLineIdx; i < lines.length; i++) {
            Matcher m = P_SSR_TKNE_Demestic.matcher(lines[i]);
            if (m.find()) {
                String tktInfo = m.group(1);
                String[] infos = tktInfo.trim().split("\\s+");
                if (infos.length < 6) {
                    continue FIND_TKT_LINE;
                }
                if (!infos[1].startsWith(P_SSR_TKNE_HK)) {
                    continue FIND_TKT_LINE;
                }
                String tktStr = infos[TKT_OFFSET_1BASE - 1];
                Matcher mTktInfo = P_TKNE_INFO.matcher(tktStr);
                if (mTktInfo.find()) {
                    String tktNumber = mTktInfo.group(1);
                    String tktType = "";
                    String INF = "INF";
                    if (StringUtils.startsWith(tktNumber, INF)) {
                        tktType = INF;
                        tktNumber = tktNumber.substring(INF.length());
                    }

                    int fltsegIdx = NumberUtils.toInt(mTktInfo.group(2));
                    int psgIdx = NumberUtils.toInt(mTktInfo.group(5));
                    TkneInfo tkneInfo = new TkneInfo();
                    tkneInfo.tktNum = tktNumber;
                    List<String> list = new ArrayList<String>();
                    list.add(String.valueOf(fltsegIdx));
                    tkneInfo.fltsegIdxs = list;
                    tkneInfo.tktType = tktType;
                    tkneInfo.psgIdx = psgIdx;
                    tktInfoList.add(tkneInfo);
                }
            }
        }
        // order by psgIdx and ticketType(INF)
        Collections.sort(tktInfoList, tkneInfoComparator);
        return tktInfoList;
    }
}
