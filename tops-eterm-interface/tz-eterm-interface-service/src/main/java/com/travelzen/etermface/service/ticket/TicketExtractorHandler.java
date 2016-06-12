package com.travelzen.etermface.service.ticket;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.javatuples.Quartet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.travelzen.etermface.service.PNRParser.RtInfoBean;
import com.travelzen.etermface.service.entity.PnrRet;
import com.travelzen.etermface.service.entity.PnrRet.TicketInfo;

/**
 *
 * 票号提取逻辑，该类目前没有使用
 */
public class TicketExtractorHandler implements RtAfterParserHandler {

    class TicketExtractorInfoBean {
	public int maxTktCnt;
	public int lastTktLineIdx;
    }

    // 22.TN/232-3982069276/P1 单张票
    // 22.TN/IN/232-3982069276/P1
    // TN/781-4481613445-46/P1
    // static Pattern p1 =
    // Pattern.compile("\\s*\\d{1,2}\\.TN\\/(IN\\/)?(.*?)/P(\\d{1,2})");
    static Pattern p1 = Pattern.compile("\\s*?TN\\/(IN\\/)?(.*?)/P(\\d{1,2})");

    static int TKT_NUM_IDX = 2;

    // 15.OSI 1E MUET TN/7812121563445-7812131563446 票号段
    // 15.OSI 1E MUET TN/2121563445-2131563446 票号段
    // static Pattern p21multiple =
    // Pattern.compile("\\s*\\d{1,2}\\.OSI\\s+.{2}\\s+.{4}\\s+TN\\/(\\d{10,13})-(\\d{10,13})");
    static Pattern p21multiple = Pattern.compile("\\s*?OSI\\s+.{2}\\s+.{4}\\s+TN\\/(\\d{10,13})-(\\d{10,13})");

    // 14.OSI 1E MUET TN/7812133427202 单张票
    // static Pattern p22 =
    // Pattern.compile("\\s*\\d{1,2}\\.OSI\\s+.{2}\\s+.{4}\\s+TN\\/(\\d{10,13})");
    static Pattern p22 = Pattern.compile("\\s*?OSI\\s+.{2}\\s+.{4}\\s+TN\\/(\\d{10,13})");

    // 14.OSI 1E MUET TN/781-2133427202 单张票
    // static Pattern p23 =
    // Pattern.compile("\\s*\\d{1,2}\\.OSI\\s+.{2}\\s+.{4}\\s+TN\\/(\\d{3}-\\d{10})");
    static Pattern p23 = Pattern.compile("\\s*?OSI\\s+.{2}\\s+.{4}\\s+TN\\/(\\d{3}-\\d{10})");

    // 三种格式 7812133427202 2133427202 781-2133427202

    // 如果只有一个乘客， 后面会没有P1 P2 这样的后缀吗?
    // 比如 OSI 1E MUET TN/7812133427202
    // http://192.168.163.185/issues/495
    // 就是按照我上次给你的那几种格式，后面没有P的，就不会再

    // 22.TN/232-3982069276/P1
    // 23.TN/232-3982069277/P2
    // 这两行 一定是连在一起的吧， 不会隔开吧？
    // 不会

    // 6.T MFE/T TN/7312370506376
    // 6.T MFE/T TN/7312370506376-7312370506377
    // static String p3prefix = "\\s*\\d{1,2}\\.T\\s+MFE\\/T\\s+TN\\/";
    static String p3prefix = "\\s*?T\\s+MFE\\/T\\s+TN\\/";
    static Pattern p31multiple = Pattern.compile(p3prefix + "((\\d{10,13})-(\\d{10,13}))");
    static Pattern p32 = Pattern.compile(p3prefix + "/((\\d{10,13}))");

    // static String p4prefix = "\\s*\\d{1,2}\\.T\\/";
    static String p4prefix = "\\s*?T\\/";
    static Pattern p41multiple = Pattern.compile(p4prefix + "((\\d{10,13})-(\\d{10,13}))");
    static Pattern p42 = Pattern.compile(p4prefix + "/((\\d{10,13}))");
    // T/781-4481435040-53
    static Pattern p43multiple = Pattern.compile(p4prefix + "((\\d{3})-(\\d{10})-(\\d{2}))");
    static int TKT_NUM_IDX_P43 = 1;

    private static Logger logger = LoggerFactory.getLogger(TicketExtractorHandler.class);

    // 14.OSI 1E MUET TN/7812133427202 单张票
    // 14.OSI 1E MUET TN/781-2133427202 单张票
    private List<TicketParseInfo> processP22P23Single(String[] lines, int startLineIdx, TicketExtractorInfoBean ticketExtractorInfoBean) {

	List<TicketParseInfo> tktList = Lists.newArrayList();

	boolean findFirstTkt = false;

	int personIdx = 1;

	FIND_TKT: for (int i = lines.length; i > startLineIdx; i--) {

	    String tktNum = "";

	    Matcher m1 = p22.matcher(lines[i - 1]);
	    Matcher m2 = p23.matcher(lines[i - 1]);

	    Matcher m = null;
	    if (m1.find()) {
		m = m1;
	    }

	    if (m2.find()) {
		m = m2;
	    }

	    if (null != m) {

		ticketExtractorInfoBean.lastTktLineIdx = i;

		findFirstTkt = true;

		tktNum = m.group(1);

		tktList.add(new TicketParseInfo(tktNum, personIdx));

		personIdx++;
	    } else {
		if (findFirstTkt) {
		    break FIND_TKT;
		}
	    }

	}

	reverseTktIdxWhenNoSsrTkneInfo(tktList);
	return tktList;
    }

    // private void reverseTktPersonIdx(List<Pair<String, Integer>> tktList ){
    // int cnt = tktList.size();
    // //10 item, 1th->10, 2->9 , 3->8
    // for(Pair<String, Integer> item: tktList){
    // item.setValue( cnt+1- item.getValue());
    // }
    //
    // }

    private List<TicketParseInfo> processPCommonSingle(String[] lines, int startLineIdx, TicketExtractorInfoBean ticketExtractorInfoBean, Pattern pattern) {

	List<TicketParseInfo> tktList = Lists.newArrayList();

	boolean findFirstTkt = false;

	int personIdx = 1;
	FIND_TKT: for (int i = lines.length; i > startLineIdx; i--) {

	    String tktNum = "";

	    Matcher m = pattern.matcher(lines[i - 1]);
	    if (m.find()) {

		ticketExtractorInfoBean.lastTktLineIdx = 1;

		findFirstTkt = true;

		tktNum = m.group(1);
		logger.info("{}:{}", tktNum, personIdx);

		personIdx++;
	    } else {
		if (findFirstTkt) {
		    break FIND_TKT;
		}
	    }
	}

	reverseTktIdxWhenNoSsrTkneInfo(tktList);
	return tktList;
    }

    private List<TicketParseInfo> processP1Single(String[] lines, int startLineIdx, TicketExtractorInfoBean ticketExtractorInfoBean) {
	List<TicketParseInfo> tktList = Lists.newArrayList();

	boolean findFirstTkt = false;

	FIND_TKT: for (int i = lines.length - 1; i > startLineIdx; i--) {

	    String unprocessedTktNum = "";
	    String personIdx = "";
	    String ticketType = "";

	    Matcher m = p1.matcher(lines[i]);
	    if (m.find()) {

		ticketExtractorInfoBean.lastTktLineIdx = i;

		findFirstTkt = true;

		ticketType = StringUtils.trimToEmpty(m.group(TKT_NUM_IDX - 1));

		personIdx = m.group(TKT_NUM_IDX + 1);
		logger.info("{}:{}", unprocessedTktNum, personIdx);

		// 781-4481613445-46
		unprocessedTktNum = m.group(TKT_NUM_IDX);

		List<String> tktNumList = buildTktList(unprocessedTktNum);

		for (String tktNum : tktNumList) {

		    TicketParseInfo data = new TicketParseInfo(tktNum, NumberUtils.toInt(personIdx));
		    data.ticketType = ticketType;

		    if (StringUtils.stripEnd(lines[i], null).endsWith("VT")) {
			data.ticketStatus = "VT";
		    }

		    tktList.add(data);

		}

	    } else {
		if (findFirstTkt) {
		    break FIND_TKT;
		}
	    }
	}

	reverseTktIdxWhenNoSsrTkneInfo(tktList);
	return tktList;
    }

    private List<TicketParseInfo> processP31Multiple(String[] lines, int startLineIdx, TicketExtractorInfoBean ticketExtractorInfoBean) {
	return processPCommonMultiple(lines, startLineIdx, ticketExtractorInfoBean, p31multiple);
    }

    private List<TicketParseInfo> processP32Single(String[] lines, int startLineIdx, TicketExtractorInfoBean ticketExtractorInfoBean) {
	return processPCommonSingle(lines, startLineIdx, ticketExtractorInfoBean, p32);
    }

    private List<TicketParseInfo> processP21Multiple(String[] lines, int startLineIdx, TicketExtractorInfoBean ticketExtractorInfoBean) {
	return processPCommonMultiple(lines, startLineIdx, ticketExtractorInfoBean, p21multiple);
    }

    private List<TicketParseInfo> processP42Single(String[] lines, int startLineIdx, TicketExtractorInfoBean ticketExtractorInfoBean) {
	return processPCommonSingle(lines, startLineIdx, ticketExtractorInfoBean, p42);
    }

    private List<TicketParseInfo> processP41Multiple(String[] lines, int startLineIdx, TicketExtractorInfoBean ticketExtractorInfoBean) {
	return processPCommonMultiple(lines, startLineIdx, ticketExtractorInfoBean, p41multiple);
    }

    private List<TicketParseInfo> processP43Multiple(String[] lines, int startLineIdx, TicketExtractorInfoBean ticketExtractorInfoBean) {

	List<TicketParseInfo> tktList = Lists.newArrayList();

	boolean findFirstTkt = false;

	FIND_TKT: for (int i = lines.length - 1; i > startLineIdx; i--) {

	    String unprocessedTktNum = "";
	    String personIdx = "";
	    String ticketType = "";

	    Matcher m = p43multiple.matcher(lines[i]);

	    if (m.find()) {

		ticketExtractorInfoBean.lastTktLineIdx = i;

		findFirstTkt = true;

		// 781-4481613445-46
		unprocessedTktNum = m.group(TKT_NUM_IDX_P43);

		List<String> tktNumList = buildTktList(unprocessedTktNum);

		for (String tktNum : tktNumList) {

		    TicketParseInfo data = new TicketParseInfo(tktNum, NumberUtils.toInt(personIdx));
		    data.ticketType = ticketType;

		    tktList.add(data);

		}

	    } else {
		if (findFirstTkt) {
		    break FIND_TKT;
		}
	    }
	}

	// reverseTktIdxWhenNoSsrTkneInfo(tktList);
	return tktList;

    }

    private List<TicketParseInfo> processPCommonMultiple(String[] lines, int startLineIdx, TicketExtractorInfoBean ticketExtractorInfoBean, Pattern pattern) {

	List<List<TicketParseInfo>> tktlistList = Lists.newArrayList();

	boolean findFirstTkt = false;

	FIND_TKT: for (int i = lines.length; i > startLineIdx; i--) {

	    String line = lines[i - 1].intern();

	    Matcher m = pattern.matcher(line);
	    if (m.find()) {

		ticketExtractorInfoBean.lastTktLineIdx = i;

		List<TicketParseInfo> tktList = Lists.newArrayList();
		findFirstTkt = true;

		// 起止票号位数必须相等， 10或13位
		checkArgument(m.group(1).length() == m.group(2).length(), "invalid ticket number:%s", line);

		long startTktNum = NumberUtils.toLong(m.group(1));
		long endTktNum = NumberUtils.toLong(m.group(2));

		int personIdx = 1;
		PROCESS_LINE: for (long idx = startTktNum; idx <= endTktNum; idx++) {
		    tktList.add(new TicketParseInfo(String.valueOf(idx), personIdx));
		    personIdx++;

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

	return finalList;
    }

    // // 因为票号的parser是从后到前处理的, 所以, 处理完票号, 需要再倒置一次 passeger和 tktIdx的对应关系
    private void reverseTktIdxWhenNoSsrTkneInfo(List<TicketParseInfo> tktList) {
	int cnt = tktList.size();
	for (TicketParseInfo item : tktList) {
	    int idx = item.psgIdx;
	    int realIdx = cnt + 1 - idx;
	    item.psgIdx = realIdx;
	}

    }

    // static Pattern P_SSR_TKNE =
    // Pattern.compile("\\s*\\d{1,2}\\.SSR\\s+TKNE\\s+(.*?)$");
    static Pattern P_SSR_TKNE = Pattern.compile("\\s*?SSR\\s+TKNE\\s+(.*?)$");

    // 8.SSR TKNE FM HK1 SHAHKG 845 R08MAY 7743936980810/1/P1
    // 9.SSR TKNE FM HK1 HKGSHA 8238 N11MAY 7743936980810/2/P1

    // FM HK1 SHAHKG 845 R08MAY 7743936980810/1/P1
    static final int TKT_OFFSET_1BASE = 6;

    // 7743936980810/1/P1
    //
    static Pattern P_TKNE_INFO = Pattern.compile("(.*?)\\/(\\d+)\\/P(\\d+)");

    // 票号， 航段序号,乘客序号
    private List<Quartet<String, Integer, Integer, String>> parseTktPsgidxFltidxList(String[] lines, int startLineIdx) {

	List<Quartet<String, Integer, Integer, String>> tktInfoList = Lists.newArrayList();

	FIND_TKT_LINE: for (int i = startLineIdx; i < lines.length; i++) {

	    Matcher m = P_SSR_TKNE.matcher(lines[i]);
	    if (m.find()) {

		String tktInfo = m.group(1);
		String[] infos = tktInfo.trim().split("\\s+");

		if (infos.length < 6) {
		    continue FIND_TKT_LINE;
		}

		checkPositionIndex(TKT_OFFSET_1BASE, infos.length);

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
		    int psgIdx = NumberUtils.toInt(mTktInfo.group(3));
		    tktInfoList.add(Quartet.with(tktNumber, fltsegIdx, psgIdx, tktType));
		}

	    } else {
		// if (findFirstTkt) {
		// break FIND_TKT;
		// }
	    }
	}

	Collections.sort(tktInfoList, new Comparator<Quartet<String, Integer, Integer, String>>() {

	    @Override
	    public int compare(Quartet<String, Integer, Integer, String> o1, Quartet<String, Integer, Integer, String> o2) {

		int cond1 = o1.getValue2().compareTo(o2.getValue2());

		if (cond1 != 0) {
		    return cond1;

		} else {
		    return o1.getValue3().compareTo(o2.getValue3());
		}

	    }
	});

	return tktInfoList;
    }

    @Override
    public void handle(RtInfoBean rtInfoBean, String[] lines, int startLineIdx, PnrRet pnrRet) {

	checkNotNull(pnrRet.PassengerInfo.Passengers);
	checkNotNull(pnrRet.Flights);

	List<Quartet<String, Integer, Integer, String>> tktPsgFltIdList = parseTktPsgidxFltidxList(lines, startLineIdx);

	logger.info("tktInfo:{}", tktPsgFltIdList.toString());

	// this is the max value, the real tkt count will never exceed this
	// number
	final int maxTktCnt = pnrRet.PassengerInfo.Passengers.size() * pnrRet.Flights.size();

	TicketExtractorInfoBean teBean = new TicketExtractorInfoBean();
	teBean.maxTktCnt = maxTktCnt;
	teBean.lastTktLineIdx = lines.length - 1;

	List<TicketParseInfo> tktList = Lists.newArrayList();

	FIND_TKT: while (true) {

	    // priority1
	    tktList = processP1Single(lines, startLineIdx, teBean);
	    if (!tktList.isEmpty()) {
		logger.debug("processP1Single");
		break FIND_TKT;
	    }

	    // priority2
	    tktList = processP21Multiple(lines, startLineIdx, teBean);
	    if (!tktList.isEmpty()) {
		logger.debug("processP21Multiple");
		break FIND_TKT;
	    }

	    tktList = processP22P23Single(lines, startLineIdx, teBean);
	    if (!tktList.isEmpty()) {
		logger.debug("processP22P23Single");
		break FIND_TKT;
	    }

	    // priority3
	    tktList = processP31Multiple(lines, startLineIdx, teBean);
	    if (!tktList.isEmpty()) {
		logger.debug("processP31Multiple");
		break FIND_TKT;
	    }

	    tktList = processP32Single(lines, startLineIdx, teBean);
	    if (!tktList.isEmpty()) {
		logger.debug("processP32Single");
		break FIND_TKT;
	    }

	    // priority4
	    tktList = processP41Multiple(lines, startLineIdx, teBean);
	    if (!tktList.isEmpty()) {
		logger.debug("processP41Multiple");
		break FIND_TKT;
	    }

	    // priority4
	    tktList = processP43Multiple(lines, startLineIdx, teBean);
	    if (!tktList.isEmpty()) {
		logger.debug("processP41Multiple");
		break FIND_TKT;
	    }

	    tktList = processP42Single(lines, startLineIdx, teBean);
	    if (!tktList.isEmpty()) {
		logger.debug("processP42Single");
		break FIND_TKT;
	    }

	    break FIND_TKT;
	}

	Map<String, String> validTktNumberStatusMap = Maps.newHashMap();
	for (TicketParseInfo tktPsg : tktList) {
	    validTktNumberStatusMap.put(tktPsg.tktNum.replace("-", ""), tktPsg.ticketStatus);
	}

	// 如果 tktPsgidxFltidxList 这个存在数据, 就按照它里面的对照关系来出 票号->航段（1个航段）-乘客
	// 如果 tktPsgidxFltidxList里不存在, 则认为, 多个航段只有1张票, 每个票号->1个乘客, 多个航段.

	// 对于同一个乘客， 下面的每一行的 航段编号都要递增
	// 比如
	// 55/1/P1
	// 55/2/P1
	// 55/3/P1
	// 56/1/P1
	// 对于的航段应该分别是1，2,3,4
	String curPersonId = "";
	int curFltsegId = 0;

	List<TicketInfo> ticketInfos = Lists.newArrayList();

	if (!tktPsgFltIdList.isEmpty()) {

	    for (Quartet<String, Integer, Integer, String> item : tktPsgFltIdList) {
		String ticketNo = item.getValue0();
		String psgIdx = item.getValue2().toString();
		String psgId = item.getValue2() + item.getValue3();

		if (!curPersonId.equals(psgId)) {
		    curFltsegId = 1;
		} else {
		    curFltsegId++;
		}

		curPersonId = psgId;

		// a ticket appair in SSR seg must also appair in real tkt info
		// seg( p1 p2 p3 p4)
		// but in groupTkt, there's no 'TN seg'
		// if (!rtInfoBean.isGroup) {
		if (!validTktNumberStatusMap.keySet().contains(ticketNo))
		    continue;
		// }

		String tktType = item.getValue3();

		List<String> fltsegIDs = Arrays.asList(Integer.toString(curFltsegId));

		TicketInfo ticketInfo = new TicketInfo(ticketNo, fltsegIDs, psgIdx);
		ticketInfo.ticketType = tktType;
		ticketInfo.ticketStatus = validTktNumberStatusMap.get(ticketNo);

		ticketInfos.add(ticketInfo);
	    }
	} else {

	    for (TicketParseInfo tktPsg : tktList) {

		String psgId = tktPsg.psgIdx + tktPsg.ticketType;

		if (!curPersonId.equals(psgId)) {
		    curFltsegId = 1;
		}

		curPersonId = psgId;

		String psgID = String.valueOf(tktPsg.psgIdx);

		List<String> fltsegIDs = Lists.newArrayList();
		for (int i = 0; i < pnrRet.Flights.size(); i++) {
		    fltsegIDs.add(String.valueOf(curFltsegId));
		    curFltsegId++;
		}

		String ticketNo = tktPsg.tktNum;

		TicketInfo ticketInfo = new TicketInfo(ticketNo, fltsegIDs, psgID);
		if (StringUtils.isNotBlank(tktPsg.ticketType)) {
		    ticketInfo.ticketType = tktPsg.ticketType;
		}

		ticketInfo.ticketStatus = validTktNumberStatusMap.get(ticketNo);

		ticketInfos.add(ticketInfo);
	    }
	}

	pnrRet.TicketInfos = ticketInfos;

	Pattern fnPat = Pattern.compile("^\\s*\\d{1,3}\\.FN\\/");
	int fnStartIdx = startLineIdx;
	for (int idx = teBean.lastTktLineIdx; idx > startLineIdx; idx--) {
	    // 19.FN/FCNY5580.00/SCNY5580.00/C5.00/XCNY1115.00/TCNY90.00CN/TCNY1025.00YQ/
	    // ACNY6695.00
	    // 20.TN/781-4481078799/P1
	    Matcher m = fnPat.matcher(lines[idx]);
	    if (m.find()) {
		fnStartIdx = idx;
	    }
	}
	List<String> fnLines = Lists.newArrayList();
	for (int i = fnStartIdx; i < teBean.lastTktLineIdx; i++) {
	    fnLines.add(lines[i]);
	}
	String realFnLine = StringUtils.join(fnLines, " ");
	int pointIdx = realFnLine.indexOf(".");
	realFnLine = realFnLine.substring(pointIdx + 1);

	pnrRet.FnStr = realFnLine;

	logger.info(tktList.toString());

	// 22.TN/232-3982069276/P1
	// 23.TN/232-3982069277/P2
	// logger.info(lines[startLineIdx]);

    }

    /**
     * convert 781-4481613445-46 => 7814481613445, 7814481613446
     * 
     * @param str
     * @return
     */
    public static final int TKT_NUMBER_LENGTH = 13;

    private List<String> buildTktList(String str) {

	List<String> retStrList = Lists.newArrayList();

	String tkt = str.replace("-", "");
	if (tkt.length() > TKT_NUMBER_LENGTH) {

	    int lastHyphenIdx = str.lastIndexOf("-");

	    int subNumLengh = str.length() - lastHyphenIdx - 1;
	    int endNum = NumberUtils.toInt(str.substring(lastHyphenIdx + 1));
	    int startNum = NumberUtils.toInt(str.substring(lastHyphenIdx - 2, lastHyphenIdx));

	    // 781-4481613445-46 : 46 > 45
	    if (endNum > startNum) {

		String tktNumStr = str.substring(0, str.length() - 2 * subNumLengh - 1);
		String pureTkeNumStr = tktNumStr.replace("-", "");
		for (int i = startNum; i <= endNum; i++) {

		    String subNum = StringUtils.leftPad(String.valueOf(i), subNumLengh);
		    String fullTktNumStr = pureTkeNumStr + subNum;

		    retStrList.add(fullTktNumStr);
		}
	    }
	} else {
	    retStrList.add(tkt);
	}

	return retStrList;
    }

    public static void main(String arv[]) {
        TicketExtractorHandler ticketExtractorHandler=new TicketExtractorHandler();
        String[] strs=new String[1];
//        strs[0]="16.SSR TKNE CA XX1 PVGDLC 8954 G23SEP 9995499729225/1/DPN2054/P4";
        strs[0]="9.SSR TKNE MU HN1 SHAZUH 5201 V14OCT 7815499755900/1/DPN1178/P1";
        List list=ticketExtractorHandler.parseTktPsgidxFltidxList(strs,0);

	System.out.println(new TicketExtractorHandler().buildTktList("781-4481613445-46").toArray().toString());
    }
}
