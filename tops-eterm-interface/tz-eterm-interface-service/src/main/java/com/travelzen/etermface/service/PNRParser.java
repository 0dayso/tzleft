package com.travelzen.etermface.service;

import it.unimi.dsi.lang.MutableString;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jodd.util.StringUtil;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.joda.time.DateTime;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.io.ClassPathResource;

import com.common.ufis.util.UfisException;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.travelzen.etermface.common.config.cdxg.CdxgConstant;
import com.travelzen.etermface.service.common.PNRStatusParser;
import com.travelzen.etermface.service.constant.PnrParserConstant;
import com.travelzen.etermface.service.entity.CityDistance;
import com.travelzen.etermface.service.entity.DetrsResult;
import com.travelzen.etermface.service.entity.ERRuleResult;
import com.travelzen.etermface.service.entity.MakeFareByPNR;
import com.travelzen.etermface.service.entity.MakeFareByPNR.FareTax;
import com.travelzen.etermface.service.entity.ParseConfBean;
import com.travelzen.etermface.service.entity.PnrRet;
import com.travelzen.etermface.service.entity.PnrRet.PassengerInfo;
import com.travelzen.etermface.service.entity.PnrRet.PassengerInfo.Passenger;
import com.travelzen.etermface.service.entity.PnrRet.PatItem;
import com.travelzen.etermface.service.entity.PnrRet.PatResult;
import com.travelzen.etermface.service.entity.PnrRet.XsfsmItem;
import com.travelzen.etermface.service.entity.RtInterceptorBean;
import com.travelzen.etermface.service.entity.SegPrice;
import com.travelzen.etermface.service.entity.SegPriceSyntaxTree;
import com.travelzen.etermface.service.enums.PNRStatus;
import com.travelzen.etermface.service.nlp.TimeLimitParser;
import com.travelzen.etermface.service.nlp.TimeLimitTokenRepo;
import com.travelzen.etermface.service.segprice.SPToken;
import com.travelzen.etermface.service.segprice.SegPriceParser;
import com.travelzen.etermface.service.segprice.SegpriceLexer;
import com.travelzen.etermface.service.ticket.DetrsParser;
import com.travelzen.etermface.service.ticket.RtAfterParserHandler;
import com.travelzen.etermface.service.ticket.RtBeforeParserHandler;
import com.travelzen.etermface.service.util.CDataPrettyPrintWriter;
import com.travelzen.etermface.service.util.PnrDateUtil;
import com.travelzen.etermface.service.util.PnrNumerUtil;
import com.travelzen.etermface.service.util.PnrPassengerUtil;
import com.travelzen.framework.core.common.ReturnClass;
import com.travelzen.framework.core.common.ReturnCode;
import com.travelzen.framework.core.exception.BizException;
import com.travelzen.framework.core.time.DateTimeUtil;
import com.travelzen.framework.core.util.TZUtil;

@SuppressWarnings("unused")
public class PNRParser {
    /**
     * 找城市距离的起始位置
     */
    private final static String description = "CTY  TPM   CUM";

    private static Logger logger = LoggerFactory.getLogger(PNRParser.class);

    public EtermUfisClient getClient() {
        return client;
    }

    private EtermUfisClient client;
    private String officeId;

    public PNRParser() {
    }

    public PNRParser(ParseConfBean confBean) {
        officeId = confBean.getOfficeId();
    }

    public MakeFareByPNR fare = new MakeFareByPNR();
    public PnrRet pnrRet = new PnrRet();

    interface IPatContentProvider {
        String getAdtPat();

        String getChdPat();

        String getInfPat();
    }

    public static class FlightResult {
        public List<PnrRet.Flight> pnrRetFlights;
        public List<MakeFareByPNR.FlightInfo.FlightSegment> flightSegments;
    }

    public static class PnrRestLineParseStatus {
        public boolean osiParsed = false;
        public boolean ssrParsed = false;
        public boolean xninParsed = false;
        public boolean rmkParsed = false;
        public boolean docsParsed = false;
        public boolean ckinParsed = false;
        public boolean foidParsed = false;
        public boolean othsParsed = false;
        public boolean adtkParsed = false;
    }

    enum CarrierChoosenMode {
        SIMPLE, NORMAL, QUICK
    }

    PnrRestLineParseStatus parseStatus = new PnrRestLineParseStatus();

    static Pattern HEAD_INDEX_PATTERN = Pattern.compile("\\s*\\d{1,3}\\.");

    public static Pattern SPACE_PATTERN = Pattern.compile("\\s+");

    // OSI YY CTCT58525587

    static final String G_CARRIER = "carrier";
    static final String G_NUMBER = "number";

    static Pattern CTCT_PATTERN = Pattern.compile(String.format("OSI\\s+(?<%s>\\w{2})\\s+CTCT\\s*(?<%s>\\d+)?", G_CARRIER, G_NUMBER));

    // SSR FOID MU HK1 NI370682198510290835/P1
    static Pattern FOID_PATTERN = Pattern.compile("SSR FOID(.*?)\\/P(\\d{1,2})");

    //PNR大编码正则表达式
    //RT中的结果：RMK CA/MJ6W7F
    static Pattern BIG_PNR_PATTERN = Pattern.compile("RMK\\s*(\\w{2})\\/(\\w{6})");
    //RTR中的结果：-CA-NBGX6P
    static Pattern BIG_PNR_RTR_PATTERN = Pattern.compile(".*-(\\w{2})-(\\w{6})");

    static public Pattern RATE_PATTERN = Pattern.compile("RATE USED 1\\w{3}=([\\d.]+)\\w{3}.*$");

    // MO22APR13SVXPEK
    static Pattern DepartureDateStrPattern1 = Pattern.compile("\\w{2}\\d{2}\\w{3}\\d{2}\\w{6}");

    static Pattern AvhCodeSharePattern = Pattern.compile("\\s+(\\w)+\\s");

    static DateTimeFormatter dateTimeFormatterMMMM = DateTimeFormat.forPattern("MMMM");

    ParseConfBean parseConfBean;

    FlightResult flightResult = new FlightResult();

    public String PNR;

    public PnrRet.PassengerInfo passengers;
    public PnrRet.PassengerInfo infantPassengers;

    public List<String> makeFareByPnrPassenger = Lists.newArrayList();

    public Map<Integer, Boolean> passengerHasAppendDetailInfo = Maps.newConcurrentMap();

    public Set<String> carrierSet = Sets.newHashSet();

    public boolean hasAdt = false;
    public boolean hasInfant = false;
    public boolean hasChild = false;
    public boolean isGroupTicket = false;

    List<PatItem> patItemsFromFnA = Lists.newArrayList();

    List<PatItem> patItemsFromFn = Lists.newArrayList();

    PNRStatus pnrStatus = PNRStatus.NORNAL;

    private List<DateTime> ticketIssueTimeLimitList = Lists.newArrayList();

    /**
     * 判断是否是国际航班
     *
     * @param flt
     * @return
     */
    static public boolean isInternationalAirseg(PnrRet.Flight flt) {
        return !getDomesticAirportCodeMap().containsKey(flt.BoardPoint) || !getDomesticAirportCodeMap().containsKey(flt.OffPoint);
    }

    static private Map<String, String> domesticAirportCodeMap;

    /**
     * 初始化国内机场代号
     *
     * @return
     */
    public synchronized static Map<String, String> getDomesticAirportCodeMap() {
        if (domesticAirportCodeMap == null) {
            domesticAirportCodeMap = Maps.newHashMap();
            ClassPathResource resource = new ClassPathResource("airlinecode/domestic-airport-code.txt");
            try {
                Preconditions.checkNotNull(resource.getInputStream(), "domestic-airport-code file not existing");
                BufferedReader dr = new BufferedReader(new InputStreamReader(resource.getInputStream()));
                String airportCodeLine = "";
                while ((airportCodeLine = dr.readLine()) != null) {
                    // CGD|常德桃花源(斗姆湖)机场|常德|CGD
                    airportCodeLine = airportCodeLine.trim();
                    int idx = StringUtils.indexOf(airportCodeLine, '|');
                    String airlineCode = StringUtils.substring(airportCodeLine, 0, idx);
                    domesticAirportCodeMap.put(airlineCode, StringUtils.substring(airportCodeLine, idx));
                }
            } catch (IOException e) {
                logger.error(TZUtil.stringifyException(e));
            }
        }
        return domesticAirportCodeMap;
    }

    /**
     * 判断航司代号是否合法
     *
     * @param str
     * @return
     */
    static public boolean isCarrierCode(String str) {
        return getCarrierCodeMap().containsKey(str.trim().toString());
    }

    static public boolean isCarrierCode(MutableString str) {
        return isCarrierCode(str.trim().toString());
    }

    static private Map<String, String> carrierCodeMap;

    /**
     * 初始化航司代号
     *
     * @return
     */
    public synchronized static Map<String, String> getCarrierCodeMap() {
        if (carrierCodeMap == null) {
            carrierCodeMap = Maps.newHashMap();
            ClassPathResource resource = new ClassPathResource("carriercode/carriers.txt");
            try {
                Preconditions.checkNotNull(resource.getInputStream(), "carrier-code file not existing");
                BufferedReader dr = new BufferedReader(new InputStreamReader(resource.getInputStream()));
                String carrierCodeLine = "";
                while ((carrierCodeLine = dr.readLine()) != null) {
                    // CGD|常德桃花源(斗姆湖)机场|常德|CGD
                    carrierCodeLine = carrierCodeLine.trim();
                    carrierCodeLine = pureSkipHeadIdx(carrierCodeLine).trim();
                    int idx = StringUtils.indexOf(carrierCodeLine, ",");
                    String carrierCode = StringUtils.substring(carrierCodeLine, 0, idx).trim().toUpperCase();
                    if (StringUtils.isNotBlank(carrierCode)) {
                        carrierCodeMap.put(carrierCode, StringUtils.substring(carrierCodeLine, idx).trim());
                    }
                }
            } catch (IOException e) {
                logger.error(TZUtil.stringifyException(e));
            }
        }
        return carrierCodeMap;
    }

    private Pair<String, String> opfail(XStream xstream, String errorMsg) {
        StringWriter writer = new StringWriter();
        pnrRet.ERRORS = errorMsg;
        xstream.marshal(pnrRet, new PrettyPrintWriter(writer, new NoNameCoder()));
        String xml = writer.toString();

        writer = new StringWriter();
        fare.Error = errorMsg;
        xstream.marshal(fare, new PrettyPrintWriter(writer, new NoNameCoder()));
        String xml2 = writer.toString();

        // ignore excetpions on close opertion, force return error
        try {
        	if (null != client)
        		client.close();
        } catch (Exception e) {
            logger.error("clientErr:{}", TZUtil.stringifyException(e));
        }

        return Pair.with(xml, xml2);
    }

    static Pattern PNR_END_OF_LINE = Pattern.compile("\\s+(\\w{6})$");
    static Pattern LINE_ONLY_HAS_ONE_PNR = Pattern.compile("^(\\w{6})$");

    ReturnClass<String> extractPnrFromPnrContent(String pnrContent, boolean isGroupPnr) {
        ReturnClass<String> ret = new ReturnClass<String>();

        String[] lines = pnrContent.trim().replaceAll("\r", "\n").split("\n+");
        for (int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].replace("^M", "").trim();
        }
        List<String> lineList = processLineLongerThan80andTwoDigit(Lists.newArrayList(lines));

        final int maxLineIdx = 4;

        if (lineList.size() < maxLineIdx) {
            throw BizException.instance("lines.length<4");
        }

        for (int i = 0; i < maxLineIdx; i++) {
            Matcher m = PNR_END_OF_LINE.matcher(lineList.get(i));

            boolean maybeFound = false;
            if (m.find()) {
                ret.setObject(m.group(1).toUpperCase());
                maybeFound = true;
            }

            m = LINE_ONLY_HAS_ONE_PNR.matcher(lineList.get(i));
            if (m.find()) {
                ret.setObject(m.group(1));
                maybeFound = true;
            }

            if (isGroupPnr && maybeFound) {
                ret.setStatus(ReturnCode.SUCCESS);
                return ret;
            }

            if (maybeFound && preciseJudgeFlightLine(lineList.get(i + 1))) {
                ret.setStatus(ReturnCode.SUCCESS);
                return ret;
            }
        }

        List<String> pnrLineList = lineList.subList(0, maxLineIdx);

        for (int i = 0; i < pnrLineList.size() - 1; i++) {
            String str1 = pnrLineList.get(i);
            if (StringUtil.isBlank(str1)) {
                continue;
            }

            String[] ar1 = StringUtils.trim(str1).split("\\s+");

            String str2 = pnrLineList.get(i + 1);
            if (StringUtil.isBlank(str2)) {
                continue;
            }
            String[] ar2 = StringUtils.trim(str2).split("\\s+");

            if (ar1.length > 0 && ar2.length > 0) {
                String s1 = ar1[ar1.length - 1];
                String s2 = ar2[0];
                String s3 = s1 + s2;

                if (s3.length() == PnrParserConstant.PNR_LENGTH && StringUtils.isAlphanumeric(s3) && Character.isLetter(s3.charAt(0))) {
                    ret.setObject(s3);
                    ret.setStatus(ReturnCode.SUCCESS);
                    return ret;
                }
            }
        }
        ret.setStatus(ReturnCode.ERROR);
        return ret;
    }

    /**
     * 快速判断是否失败
     *
     * @param rt
     * @return
     */
    Pair<Boolean, String> checkQuickFail(String rt) {
        boolean quickFail = false;
        String errorMsg = "";

        if (StringUtils.equalsIgnoreCase(rt.trim(), "NO PNR")) {
            quickFail = true;
            errorMsg = "NO PNR";
        }

        if (rt.trim().startsWith("需要授权")) {
            quickFail = true;
            errorMsg = PNRStatus.UNAUTHORIZED.toString();
        }

        if (rt.trim().startsWith("*THIS PNR WAS ENTIRELY CANCELLED*")) {
            quickFail = true;
            errorMsg = PNRStatus.ENTIRELY_CANCELLED.toString();
        }

        if (this.pnrStatus == PNRStatus.ENTIRELY_CANCELLED) {
            quickFail = true;
            errorMsg = PNRStatus.ENTIRELY_CANCELLED.toString();
        }

        return Pair.with(quickFail, errorMsg);
    }

    public Pair<String, String> parsePnrContent(ParseConfBean parseConfBean, String pnrContent) throws UfisException {
        this.parseConfBean = parseConfBean;

        parseConfBean.needPnrRet = true;

        MDC.put("traceId", parseConfBean.getTraceId());

        try {
            StaxDriver sd = new StaxDriver(new NoNameCoder());
            XStream xstream = new XStream(sd);
            xstream.processAnnotations(PnrRet.class);
            xstream.processAnnotations(MakeFareByPNR.class);

            boolean quickFail = false;
            String errorMsg = "";

            Pair<Boolean, String> quickFailMsg = checkQuickFail(pnrContent);
            quickFail = quickFailMsg.getValue0();
            errorMsg = quickFailMsg.getValue1();

            Pair<PNRStatus, String> pnrStatus = PNRStatusParser.checkPNRStatus(pnrContent);
            PNRStatus status = pnrStatus.getValue0();

            if (status == PNRStatus.ENTIRELY_CANCELLED || status == PNRStatus.UNAUTHORIZED || status == PNRStatus.NO_PNR) {
                quickFail = true;
            }

            pnrRet.PNR_STATUS = status.toString();

            ReturnClass<String> rtRet = new ReturnClass<>();
            rtRet.setObject(pnrContent);

            // begin QuickFail
            if (quickFail) {
                pnrRet.ERRORS = errorMsg;

                StringWriter writer = new StringWriter();
                xstream.marshal(pnrRet, new PrettyPrintWriter(writer, new NoNameCoder()));

                String xml = writer.toString();

                fare.Error = errorMsg;

                writer = new StringWriter();
                xstream.marshal(fare, new PrettyPrintWriter(writer, new NoNameCoder()));
                String xml2 = writer.toString();

                return Pair.with(xml, xml2);
            }

            RtInfoBean rtInfoBean = new RtInfoBean();

            rtInfoBean.rt = pnrContent;
            rtInfoBean.isParsePnrContent = true;

            boolean isGroup = parseJudgeIfGroupRT(rtInfoBean, true);

            rtInfoBean.isGroup = isGroup;

            if (StringUtils.isEmpty(PNR)) {
                ReturnClass<String> pnrRc = extractPnrFromPnrContent(pnrContent, isGroup);

                if (pnrRc.isSuccess()) {
                    PNR = pnrRc.getObject();
                } else {
                    return opfail(xstream, "fail to extractPnrFromPnrContent");
                }
            }

            ParsePnrPayload pp = new ParsePnrPayload();
            pp.parseConfBean = parseConfBean;
            prepareParsePnr(pp);

            RtInterceptorBean rtInterceptorBean = parseConfBean.rtInterceptorBean;

            rtInfoBean.isDomestic = parseConfBean.isDomestic;
            rtInfoBean.etermWebClient = this.client;
            rtInfoBean.pnrRet = this.pnrRet;

            flightResult = this.parseRT(rtInfoBean, rtInterceptorBean);

            // op failed
            if (null == flightResult) {
                return opfail(xstream, this.pnrStatus.toString());
            }

            try {
                Optional<Pair<String, String>> chkFailResult = null;
                String ret = null;

                client = new EtermUfisClient(parseConfBean.getOfficeId(), 30);

                if (PnrParserConstant.SOURCE_UAPI.equals(parseConfBean.source) || PnrParserConstant.SOURCE_DISTRIBUTOR.equals(parseConfBean.source)) {
                    chkFailResult = Optional.absent();
                } else {

                    ret = client.execRt(PNR, true);
                    chkFailResult = checkRt(ret, pp, CheckRtType.DISALLOW_UNAUTHORIZED);
                }

                if (!chkFailResult.isPresent()) {
                    if (parseConfBean.needAccurateCodeShare) {
                        processDistanceAndSharecode(flightResult.pnrRetFlights);
                    }

                    if (parseConfBean.needFare) {
                        List<String> carrier = buildCarrier();
                        processPnrFare(parseConfBean, carrier, rtInfoBean);
                    }

                    if (parseConfBean.needERRule) {
                        processERRule(pnrRet);
                    }
                }
            } catch (UfisException e) {
                logger.error("err:{}", e.getMessage());
                pnrRet.ERRORS += e.getMessage();
                return opfail(xstream, " error:" + e.getMessage());
            } finally {
                client.close();
            }
            // endPAT

            pnrRet.Flights = flightResult.pnrRetFlights;
            pnrRet.PassengerInfo = this.passengers;

            for (Passenger psg : pnrRet.PassengerInfo.Passengers) {
                if (psg.PsgType.equalsIgnoreCase("ADT")) {
                    this.hasAdt = true;
                }
            }

            pnrRet.PNR = PNR;

            String xmlPnrRet = "";
            StringWriter writer = new StringWriter();

            xstream.marshal(pnrRet, new PrettyPrintWriter(writer, new NoNameCoder()));
            xmlPnrRet = writer.toString();

            return Pair.with(xmlPnrRet, "");
        } catch (Exception e) {
            logger.error(TZUtil.stringifyException(e));
        } finally {
        }
        return Pair.with("", "");
    }

    /**
     * pnr解析入口函数
     *
     * @param parseConfBean
     * @param pnr
     * @return
     */
    public Pair<String, String> parsePnrWithRetryOnceOnSessionExpire(ParseConfBean parseConfBean, String pnr) {
        Pair<String, String> ret = null;
        try {
            parseConfBean.needPnrRet = true;
            return parsePnrForTops(parseConfBean, pnr);
        } catch (BizException e) {
            logger.error(" error:{}", TZUtil.stringifyException(e));
            return ret;
        } catch (UfisException see) {
            logger.error(" UfisException 1st time :{}", TZUtil.stringifyException(see));
            if (!parseConfBean.isDomestic) {
                parseConfBean.needFare = false;
                parseConfBean.needERRule = false;
                parseConfBean.needXsfsm = false;
                parseConfBean.needAccurateCodeShare = false;
                parseConfBean.needIssueTktOffice = false;
            }
            try {
                parseConfBean.needPnrRet = true;
                return parsePnrForTops(parseConfBean, pnr);
            } catch (UfisException e) {
                logger.error("UfisException 2nd time error:{}\n{}", TZUtil.stringifyException(see), see.getMessage());
            }
        }
        return ret;
    }

    //	AV:AF3765/22JAN
    //	DEP TIME   ARR TIME   WEEK FLY   GROUND TERM  TYPE MEAL  DISTANCE OPE           
    //	PVG 2355   CDG 0530+1 WED  12:35        T1/2E 332        9262     MU553         
    //	TOTAL JOURNEY TIME  12:35                                                       
    //	PVGCDG JA CA DA IA ZA YA BA MA UA KA HA LA QA TA NA RA VA                       
    //	MEMBER OF SKYTEAM        

    static final String DEP = "DEP";
    static final String ARR = "ARR";
    static final String DISTANCE = "DISTANCE";
    static final String OPE = "OPE";

    private void processAv4SharecodeAndDistance(ParseConfBean parseConfBean) throws UfisException {
        NEXT_FLT:
        for (com.travelzen.etermface.service.entity.PnrRet.Flight flt : pnrRet.Flights) {

            String fltStr = flt.Carrier + flt.Flight;

            String ret = client.execCmd("AV: " + fltStr, false);

            String content = ret;

            if (StringUtils.isBlank(content)) {
                return;
            }

            String[] lines = content.split("(\r|\n)+");
            if (lines.length < 3) {
                logger.warn("av:fltNumber->" + content);
                return;
            }

            Map<String, Integer> tagIdxMap = Maps.newLinkedHashMap();
            tagIdxMap.put(DEP, -1);
            tagIdxMap.put(ARR, -1);
            tagIdxMap.put(DISTANCE, -1);
            tagIdxMap.put(OPE, -1);

            int offset = 0;
            for (String tag : tagIdxMap.keySet()) {
                int idx = lines[1].indexOf(tag, offset);
                if (idx >= 0) {
                    tagIdxMap.put(tag, idx);
                    offset = idx;
                }
            }

            Map<String, String> tagValueMap = Maps.newHashMap();

            for (String tag : tagIdxMap.keySet()) {
                if (tagIdxMap.get(tag) > -1) {
                    String rawdata = lines[2].substring(tagIdxMap.get(tag));
                    int blankIdx = rawdata.indexOf(" ");
                    String data = "";
                    if (blankIdx > 0) {
                        data = StringUtils.left(rawdata, blankIdx);
                    } else {
                        data = rawdata.trim();
                    }

                    tagValueMap.put(tag, data);
                }
            }

            com.travelzen.etermface.service.entity.PnrRet.XsfsmItem item = new com.travelzen.etermface.service.entity.PnrRet.XsfsmItem();

            item.TPM = tagValueMap.get(DISTANCE);
            item.CityPair = tagValueMap.get(DEP) + tagValueMap.get(ARR);
            pnrRet.XsfsmItems.add(item);

            if (!StringUtils.isBlank(flt.IsCodeShare)) {
                // has processed codeshare issue, needn't detail process
                // , just go next flt
                continue NEXT_FLT;
            }

            String sc = tagValueMap.get(OPE);
            if (!StringUtils.isBlank(sc)) {

                flt.ShareFlight = flt.Flight;
                flt.ShareCarrier = flt.Carrier;

                flt.Flight = StringUtils.substring(sc, 2);
                flt.Carrier = StringUtils.substring(sc, 0, 2);
            }
        }
    }

    private void processPnrXsfsm(ParseConfBean parseConfBean) throws UfisException {
        for (com.travelzen.etermface.service.entity.PnrRet.Flight flt : pnrRet.Flights) {
            String citypair = flt.BoardPoint + flt.OffPoint;

            String ret = client.execCmd("XS FSM " + citypair, false);

            if (null != ret) {
                logger.debug("XS FSM " + citypair + "\n" + ret);
                com.travelzen.etermface.service.entity.PnrRet.XsfsmItem item = new com.travelzen.etermface.service.entity.PnrRet.XsfsmItem();

                item.CityPair = citypair;
                item.TPM = getDistance(ret);
                pnrRet.XsfsmItems.add(item);
            }
            logger.debug(citypair);
        }
    }

    private void processPnrFare4uapi(ParseConfBean parseConfBean, RtInfoBean rtInfoBean) throws UfisException {
        String pnrContent = rtInfoBean.rt;

        String[] lines = StringUtils.trimToEmpty(pnrContent).split("(\r|\n)+");

        int adtStart = 0, chdStart = 0, infStart = 0;

        for (int i = 0; i < lines.length; i++) {
            if (StringUtils.containsIgnoreCase(lines[i], "PAT:A") && adtStart == 0) {
                adtStart = i;
                continue;
            }

            if (StringUtils.containsIgnoreCase(lines[i], "PAT:A*CH") && chdStart == 0) {
                chdStart = i;
                continue;
            }

            if (StringUtils.containsIgnoreCase(lines[i], "PAT:A*IN") && infStart == 0) {
                infStart = i;
                continue;
            }
        }

        final MutableObject<String> adtPat = new MutableObject<>();
        final MutableObject<String> chdPat = new MutableObject<>();
        final MutableObject<String> infPat = new MutableObject<>();

        for (; ; ) {
            if (adtStart > 0 && chdStart == 0 && infStart == 0) {
                adtPat.setValue(StringUtils.join(Arrays.copyOfRange(lines, adtStart, lines.length), "\n"));
                break;
            }

            if (adtStart > 0 && chdStart > 0 && infStart > 0) {
                adtPat.setValue(StringUtils.join(Arrays.copyOfRange(lines, adtStart, chdStart), "\n"));
                chdPat.setValue(StringUtils.join(Arrays.copyOfRange(lines, chdStart, infStart), "\n"));
                infPat.setValue(StringUtils.join(Arrays.copyOfRange(lines, infStart, lines.length), "\n"));
                break;
            }

            if (adtStart > 0 && chdStart > 0) {
                adtPat.setValue(StringUtils.join(Arrays.copyOfRange(lines, adtStart, chdStart), "\n"));
                chdPat.setValue(StringUtils.join(Arrays.copyOfRange(lines, chdStart, lines.length), "\n"));
                break;
            }

            if (adtStart > 0 && infStart > 0) {
                adtPat.setValue(StringUtils.join(Arrays.copyOfRange(lines, adtStart, infStart), "\n"));
                infPat.setValue(StringUtils.join(Arrays.copyOfRange(lines, infStart, lines.length), "\n"));
                break;
            }
            break;
        }

        commonProcessPatLogic(new IPatContentProvider() {
            @Override
            public String getAdtPat() {
                return adtPat.getValue();
            }

            @Override
            public String getChdPat() {
                return chdPat.getValue();
            }

            @Override
            public String getInfPat() {
                return infPat.getValue();
            }

        });
    }

    // 先RT判断三方，
    // 如果是三方，PAT*A#C，没有PAT价格，取FN，
    // 如果不是三方，PAT*A（正常PAT），没有PAT价格，取FN，
    // 如果没有FN，就是没有价格，创建订单失败。
    // 儿童和成人不会在一个PNR里， 根据当前PNR本身是儿童还是成人来判断 FN出来的价格的 psgType

    // 1、采购黑屏：仅通过PAT:A获取价格；
    // 2、采购白屏：仅通过PAT:A获取价格；
    // 3、运营商黑屏：优先PAT:A提取价格，异常时取PNR记录FN价格，最终无价格默认0；
    // 4、运营商白屏：仅通过PAT:A获取价格；
    // 5、采购三方协议黑屏：仅通过PAT*A#C获取价格；
    // 6、运营三方协议黑屏：优先通过PNR内FN/A获取价格，获取不到再通过PAT*A#C获取价格；
    //

    // 总结为：
    // 如果是采购：
    // ---- 如果有三方， 仅通过PAT*A#C获取价格；
    // ----如果无三方 仅通过PAT:A获取价格
    //
    // 如果是运营：
    // ----如果有三方： 优先通过PNR内FN/A获取价格，获取不到再通过PAT*A#C获取价格, 不处理 PAT:*CH和 PAT:*IN；
    // ----如果无三方：
    // --------黑屏： 优先PAT:A提取价格(处理PAT:*CH和 PAT:*IN)，异常时取PNR记录FN价格，最终无价格默认0；
    // --------白屏： 仅通过PAT:A获取价格 (处理PAT:*CH和 PAT:*IN)

    // ---- 如果有三方， 仅通过PAT*A#C获取价格；
    // ----如果无三方 仅通过PAT:A获取价格
    private void processPnrFare4Purchaser() throws UfisException {
        String patStr = "";

        if (this.hasTripartiteAgreementNum()) {
            patStr = client.execCmd("PAT:A#c" + pnrRet.TripartiteAgreementNum);
            ReturnClass<List<PnrRet.PatResult>> retFairs = parsePAT(patStr);

            if (retFairs.isSuccess()) {

                PnrRet.PatItem patItem = new PnrRet.PatItem();
                patItem.PsgType = "ADT";
                patItem.PatResults = retFairs.getObject();
                patItem.PatStr = patStr;
                pnrRet.PriceNodes.PatItem.add(patItem);
            }
            return;
        }
        processPnrFareNormalPat();
    }

    private void commonProcessPatLogic(IPatContentProvider provider) {
        String patStr = provider.getAdtPat();

        ReturnClass<List<PnrRet.PatResult>> retFairs = parsePAT(patStr);

        // PAT 价格解析失败， 则取 FN里的价格
        if (retFairs.isError()) {
            pnrRet.ERRORS = "no PAT price , no FN price";
            return;
        }

        // 走到 正常 PAT 解析价格成功的情况， 正常

        PnrRet.PatItem patItem = new PnrRet.PatItem();
        patItem.PsgType = "ADT";
        patItem.PatResults = retFairs.getObject();
        patItem.PatStr = patStr;
        pnrRet.PriceNodes.PatItem.add(patItem);

        // 继续处理 儿童和婴儿的价格

        if (retFairs.isSuccess()) {
            try {
                if (this.hasChild) {
                    String patStrCh = provider.getChdPat();
                    retFairs = parsePAT(patStrCh);

                    if (retFairs.isSuccess()) {
                        patItem = new PnrRet.PatItem();
                        patItem.PsgType = "CHD";
                        patItem.PatResults = retFairs.getObject();
                        patItem.PatStr = patStrCh;
                        pnrRet.PriceNodes.PatItem.add(patItem);
                    }
                }

                if (this.hasInfant) {
                    String patIn = provider.getInfPat();
                    retFairs = parsePAT(patIn);
                    if (retFairs.isSuccess()) {
                        patItem = new PnrRet.PatItem();
                        patItem.PsgType = "INF";
                        patItem.PatResults = retFairs.getObject();
                        patItem.PatStr = patIn;
                        pnrRet.PriceNodes.PatItem.add(patItem);
                    }
                }
            } catch (BizException e) {
                logger.error("err:{}", TZUtil.stringifyException(e));
                pnrRet.ERRORS += e.getMessage();
            }
        } else {
            pnrRet.ERRORS += retFairs.getStatus().toString();
            pnrRet.ERRORS_DETAIL += retFairs.getMessage("err:%s", "\n");
        }
    }

    private void processPnrFareNormalPat() throws UfisException {
        // 如果不是三方，PAT*A（正常PAT），没有PAT价格，取FN，
        String patStr = client.execCmd("PAT:A");

        logger.info("\nBeginPAT>>>:" + PNR + "\n{}\n>>>EndPAT", patStr);

        ReturnClass<List<PnrRet.PatResult>> retFairs = parsePAT(patStr);

        // PAT 价格解析失败， 则取 FN里的价格
        if (retFairs.isError()) {
            pnrRet.ERRORS = "no PAT price , no FN price";
            return;
        }

        // 走到 正常 PAT 解析价格成功的情况， 正常

        PnrRet.PatItem patItem = new PnrRet.PatItem();
        patItem.PsgType = "ADT";
        patItem.PatResults = retFairs.getObject();
        patItem.PatStr = patStr;
        pnrRet.PriceNodes.PatItem.add(patItem);

        // 继续处理 儿童和婴儿的价格

        if (retFairs.isSuccess()) {
            try {
                if (this.hasChild) {
                    String patStrCh = client.execCmd("PAT:A*CH");
                    retFairs = parsePAT(patStrCh);

                    if (retFairs.isSuccess()) {
                        patItem = new PnrRet.PatItem();
                        patItem.PsgType = "CHD";
                        patItem.PatResults = retFairs.getObject();
                        patItem.PatStr = patStrCh;
                        pnrRet.PriceNodes.PatItem.add(patItem);
                    }
                }

                if (this.hasInfant) {
                    String patIn = client.execCmd("PAT:A*IN");
                    retFairs = parsePAT(patIn);
                    if (retFairs.isSuccess()) {
                        patItem = new PnrRet.PatItem();
                        patItem.PsgType = "INF";
                        patItem.PatResults = retFairs.getObject();
                        patItem.PatStr = patIn;
                        pnrRet.PriceNodes.PatItem.add(patItem);
                    }
                }
            } catch (BizException e) {
                if (e.getRetCode() == ReturnCode.E_SESSION_EXPIRED) {
                    throw e;
                } else {
                    logger.error("err:{}", TZUtil.stringifyException(e));
                }
                pnrRet.ERRORS = e.getMessage();
            }
        } else {
            pnrRet.ERRORS = retFairs.getStatus().toString();
            pnrRet.ERRORS_DETAIL = retFairs.getMessage("err:%s", "\n");
        }
    }

    private void processPnrFare4Eterm(ParseConfBean parseConfBean) throws UfisException {
        processPnrFareNormalPat();
    }

    // ----如果有三方： 优先通过PNR内FN/A获取价格，获取不到再通过PAT*A#C获取价格, 不处理 PAT:*CH和 PAT:*IN；
    // ----如果无三方：
    // --------黑屏： 优先PAT:A提取价格(处理PAT:*CH和 PAT:*IN)，异常时取PNR记录FN价格，最终无价格默认0；
    // --------白屏： 仅通过PAT:A获取价格 (处理PAT:*CH和 PAT:*IN)

    private void processPnrFare4Operator(ParseConfBean parseConfBean) throws UfisException {
        String patStr = "";

        if (this.hasTripartiteAgreementNum()) {
            if (patItemsFromFnA.size() > 0) {
                pnrRet.PriceNodes.PatItem.addAll(this.patItemsFromFnA);
            } else {
                patStr = client.execCmd("A#c" + pnrRet.TripartiteAgreementNum);
                ReturnClass<List<PnrRet.PatResult>> retFairs = parsePAT(patStr);

                if (retFairs.isSuccess()) {
                    PnrRet.PatItem patItem = new PnrRet.PatItem();
                    patItem.PsgType = "ADT";
                    patItem.PatResults = retFairs.getObject();
                    patItem.PatStr = patStr;
                    pnrRet.PriceNodes.PatItem.add(patItem);
                } else {
                    if (PnrParserConstant.SOURCE_ETERM.equals(parseConfBean.source)) {
                        pnrRet.PriceNodes.PatItem.addAll(this.patItemsFromFn);
                    }
                }
                return;
            }
        }
        processPnrFareNormalPat();
    }

    // 目前三方协议的PNR入口独立，
    // 1. 前台通过office号的选择进行解析不同office的PNR，后台区分不同office号配置调用，
    // 2.
    // 前台页面上输入PNR和三方协议编号（二者都是必填项），后台pnr解析时，代入三方协议编号，使用指令PAT：A#Cxxx（xxx为三方编号）获取价格，进入建单页面，暂时不进行政策匹配。
    // 3. 创建订单进入审核流程。
    // 4. 后面同价格申请的流程。
    // 注：针对PAT：A#Cxxx，与正常PNR的入口完全区分。正常单不能从三方入口进入。

    // return : success or fail
    private ReturnClass<String> processPnrFare4tripart(ParseConfBean parseConfBean, String carrier) throws UfisException {
        ReturnClass<String> ret = new ReturnClass<String>();

        String patStr = "";

        String tripartCode = parseConfBean.getTripartCode();

        String prefix = "A#C";
        if (StringUtils.equalsIgnoreCase("CZ", carrier)) {
            prefix = "A#CDK";
        }

        String patRet = client.execCmd(prefix + tripartCode);
        if (null == patRet) {
        	ret.setStatus(ReturnCode.ERROR);
            return ret;
        }

        patStr = patRet;

        ReturnClass<List<PnrRet.PatResult>> retFairs = parsePAT(patStr);

        if (retFairs.isSuccess()) {
            PnrRet.PatItem patItem = new PnrRet.PatItem();
            patItem.PsgType = "ADT";
            patItem.PatResults = retFairs.getObject();
            patItem.PatStr = patStr;
            pnrRet.PriceNodes.PatItem.add(patItem);

            ret.setStatus(ReturnCode.SUCCESS);
        } else {
            ret.setStatus(retFairs.getStatus());
            ret.setObjects(retFairs.getObjects());
        }
        return ret;
    }


    private boolean processPnrFare(ParseConfBean parseConfBean, List<String> carrier, RtInfoBean rtInfoBean)
            throws UfisException {
        //目前国内报价还是走此处获取报价
        if (parseConfBean.isDomestic) {
            if (StringUtils.isNotBlank(parseConfBean.getTripartCode())) {
                ReturnClass<String> ret = processPnrFare4tripart(parseConfBean, carrier.iterator().next());
                if (!ret.isSuccess()) {
                    pnrRet.ERRORS = ret.getStatus().getErrorCode();
                    pnrRet.ERRORS_DETAIL = ret.getMessage();
                    return false;
                }
                return true;
            }

            //override the following 2 branch
            if (PnrParserConstant.SOURCE_UAPI.equals(parseConfBean.source) || PnrParserConstant.SOURCE_DISTRIBUTOR.equals(parseConfBean.source)) {
                processPnrFare4uapi(parseConfBean, rtInfoBean);
                return true;
            }

            //只要是黑屏导入的， 不管是采购还是运营， 都不看pnr里的三方编号
            if (PnrParserConstant.SOURCE_ETERM.equals(parseConfBean.source)) {
                processPnrFare4Eterm(parseConfBean);
                return true;
            }

            //剩下只有白屏过来的报价请求
            if (PnrParserConstant.ROLE_OPERATOR.equals(parseConfBean.role)) {
                processPnrFare4Operator(parseConfBean);
                return true;
            }

            if (PnrParserConstant.ROLE_PURCHASER.equals(parseConfBean.role)) {
                processPnrFare4Purchaser();
                return true;
            }
        }
        return true;
    }

    private void processERRule(PnrRet pnrRet) throws UfisException {
        return;
    }

    private void processAccurateCodeShare(List<PnrRet.Flight> lsFlight) throws UfisException {
        // only support "parseConfBean.needPnrRet" scenario
        NEXT_FLT:
        for (PnrRet.Flight flt : lsFlight) {
            client.extendSessionExpire(PnrParserConstant.AVH_NEED_MILLSEC);

            String fullFltNumber = flt.Carrier + flt.Flight;

            if (!StringUtils.isBlank(flt.IsCodeShare)) {
                // has processed codeshare issue, needn't detail process
                // , just go next flt
                continue NEXT_FLT;
            }

            // need to judge if really no codeShare
            // avh/pvgnrt/28nov/mu
            String cmd = "avh/" + flt.BoardPoint + flt.OffPoint + "/" + PnrDateUtil.dateStrToIbeDateStr(flt.DepartureDate) + "/" + flt.Carrier;
            String ret = client.execCmd(cmd, false);

            if (null == ret) {
                continue NEXT_FLT;
            }

            Optional<String> shareCode = processAvh4CodeShare(ret, fullFltNumber);
            if (shareCode.isPresent()) {
                flt.ShareFlight = flt.Flight;
                flt.ShareCarrier = flt.Carrier;

                String sc = shareCode.get();
                flt.Flight = StringUtils.substring(sc, 2);
                flt.Carrier = StringUtils.substring(sc, 0, 2);
            }
        }
    }

    static class ParsePnrPayload {
        public String pnr;
        public ParseConfBean parseConfBean;
        public boolean needFair;
        public XStream xstream;
        public StringWriter writer;
        public boolean quickFail;
        public String errorMsg;
    }

    private void prepareParsePnr(ParsePnrPayload pp) {
        this.PNR = pp.pnr;
        this.pnrRet.PNR = pp.pnr;
        this.parseConfBean = pp.parseConfBean;

        pp.xstream = new XStream();
        pp.writer = new StringWriter();

        {
            pp.xstream.processAnnotations(PnrRet.class);
        }
    }

    enum CheckRtType {
        ALLOW_UNAUTHORIZED, DISALLOW_UNAUTHORIZED
    }

    /**
     * checkFail: present->return errorXml and
     * checkOK: absent
     *
     * @param rtRet
     * @param pp
     * @return
     */
    private Optional<Pair<String, String>> checkRt(String rtRet, ParsePnrPayload pp, CheckRtType checkRtType) {
        String rt = "";

        if (null == rtRet) {
            pp.quickFail = true;
            pp.errorMsg = "rtRet is null ";
        } else {
            rt = rtRet;
            if (null == rt) {
                logger.error("unknow error");
            }
            Pair<Boolean, String> quickFailMsg = checkQuickFail(rt);
            pp.quickFail = quickFailMsg.getValue0();
            pp.errorMsg = quickFailMsg.getValue1();

            Pair<PNRStatus, String> pnrStatus = PNRStatusParser.checkPNRStatus(rt);
            PNRStatus status = pnrStatus.getValue0();

            for (; ; ) {

                if (checkRtType == CheckRtType.ALLOW_UNAUTHORIZED) {
                    if (status == PNRStatus.ENTIRELY_CANCELLED || status == PNRStatus.NO_PNR) {
                        pp.quickFail = true;
                    } else {
                        pp.quickFail = false;
                    }
                    break;
                }

                if (checkRtType == CheckRtType.DISALLOW_UNAUTHORIZED) {
                    if (status == PNRStatus.ENTIRELY_CANCELLED || status == PNRStatus.UNAUTHORIZED || status == PNRStatus.NO_PNR) {
                        pp.quickFail = true;
                    } else {
                        pp.quickFail = false;
                    }
                    break;
                }
            }
            pnrRet.PNR_STATUS = status.toString();
        }

        // begin QuickFail
        if (pp.quickFail) {
            pnrRet.ERRORS = pp.errorMsg;

            pp.writer = new StringWriter();
            pp.xstream.marshal(pnrRet, new PrettyPrintWriter(pp.writer, new NoNameCoder()));

            String xml = pp.writer.toString();

            client.close();
            return Optional.of(Pair.with(xml, ""));

        } else {
            return Optional.absent();
        }
    }

    private List<String> buildCarrier() throws UfisException {
        List<String> carrier = Lists.newArrayList();

        if (parseConfBean.isDomestic) {
            if (this.carrierSet.size() > 0) {
                carrier.addAll(carrierSet);
            }
        } else {
            if (parseConfBean.useAv4SharecodeAndDistance) {
                carrier = choosAirline4QTE(CarrierChoosenMode.QUICK);
            } else {
                if (parseConfBean.needXsfsm) {
                    carrier = choosAirline4QTE(CarrierChoosenMode.NORMAL);
                } else {
                    carrier = choosAirline4QTE(CarrierChoosenMode.SIMPLE);
                }
            }
        }

        return carrier;
    }

    private void processDistanceAndSharecode(List<PnrRet.Flight> lsFlight) throws UfisException {
        if (parseConfBean.useAv4SharecodeAndDistance) {
            if (parseConfBean.needAccurateCodeShare || parseConfBean.needXsfsm) {
                processAv4SharecodeAndDistance(parseConfBean);
            }
        } else {
            if (!parseConfBean.isDomestic && parseConfBean.needXsfsm) {
                processPnrXsfsm(parseConfBean);
            }

            if (parseConfBean.needAccurateCodeShare) {
                processAccurateCodeShare(lsFlight);
            }
        }
    }

    /**
     * 检查pnr
     *
     * @param pnr
     * @return Optional
     */
    public static Optional<String> checkPnr(String pnr) {
        //PNR长度为6
        if (StringUtils.length(pnr) != PnrParserConstant.PNR_LENGTH) {
            return Optional.of("error pnr length,should be " + PnrParserConstant.PNR_LENGTH);
        }
        //PNR第一个字母不能为数字
        if (StringUtils.isNumeric(StringUtils.left(pnr, 1))) {
            return Optional.of("error pnr format:1st char can't be number");
        }

        return Optional.absent();
    }


    /**
     * NOTE! every operation must throw out E_SESSION_EXPIRE , including
     * execute_cmd
     * and execute_pn , etc,
     * <p/>
     * every function calling must halt on E_SESSION_EXPIRE exceptin, otherwise
     * subtle error will must occur
     * <p/>
     * NOTE: another better way to implment this behavior is:
     * define a UfisException extend Throwable
     * disallow catch throwale exception on all the pnrParserCode
     *
     * @param parseConfBean
     * @param pnr
     * @return
     * @throws UfisException
     */

    public Pair<String, String> parsePnrForTops(ParseConfBean parseConfBean, String pnr) throws UfisException {
        pnr = StringUtils.upperCase(pnr);
        this.PNR = pnr;
        this.parseConfBean = parseConfBean;
        MDC.put("traceId", parseConfBean.getTraceId());
        boolean needFare = parseConfBean.needFare;
        boolean needSleep = parseConfBean.needSleep;
        pnrRet.PNR = pnr;

        try {
            XStream xstream = new XStream();
            xstream.processAnnotations(PnrRet.class);
            xstream.processAnnotations(MakeFareByPNR.class);

            StringWriter writer = new StringWriter();
            boolean quickFail = false;
            String errorMsg = "";

            try {
                client = new EtermUfisClient(parseConfBean.getOfficeId(), 30);
            } catch (BizException be) {
                String err = TZUtil.stringifyException(be);
                logger.error("connect err:{}", err);
                return opfail(xstream, "connect error:" + be.getMessage());
            } catch (Exception e) {
                String err = TZUtil.stringifyException(e);
                logger.error("unknow:{}", err);
                return opfail(xstream, "unknow error:" + err);
            }

            // 先取出第一页的内容
            String rtRet = client.execRt(pnr, false);

            String rt = "";
            Optional<String> checkPnrRet = checkPnr(pnr);
            if (checkPnrRet.isPresent()) {
                quickFail = true;
                errorMsg = checkPnrRet.get();
            } else if (null == rtRet) {
                quickFail = true;
                errorMsg = "null == rtRet";
            } else {
                rt = rtRet;
                if (null == rt) {
                    logger.error("unknow error");
                }
                Pair<Boolean, String> quickFailMsg = checkQuickFail(rt);
                quickFail = quickFailMsg.getValue0();
                errorMsg = quickFailMsg.getValue1();

                Pair<PNRStatus, String> pnrStatus = PNRStatusParser.checkPNRStatus(rt);
                PNRStatus status = pnrStatus.getValue0();

                if (status == PNRStatus.ENTIRELY_CANCELLED || status == PNRStatus.UNAUTHORIZED || status == PNRStatus.NO_PNR) {
                    quickFail = true;
                }

                pnrRet.PNR_STATUS = status.toString();
            }

            // begin QuickFail
            if (quickFail) {
                pnrRet.ERRORS = errorMsg;
                writer = new StringWriter();
                xstream.marshal(pnrRet, new PrettyPrintWriter(writer, new NoNameCoder()));
                String xml = writer.toString();
                client.close();
                return Pair.with(xml, "");
            }

            logger.info("BeginRT-FirstPage>>>:" + PNR + "\n{}\n>>>EndRT-FirstPage", rt);

            RtInfoBean rtInfoBean = new RtInfoBean();

            rtInfoBean.rt = rt;
            rtInfoBean.pnr = pnr;
            rtInfoBean.isDomestic = parseConfBean.isDomestic;
            rtInfoBean.etermWebClient = this.client;

            boolean isGroup = parseJudgeIfGroupRT(rtInfoBean, true);

            if (!isGroup) {
                rtRet = client.execRt(pnr, true);
                if (null != rtRet) {
                    rt = rtRet;
                } else {
                    return opfail(xstream, rtRet);
                }
                logger.info("\nBeginRT>>>:" + pnr + "\n{}\n>>>EndRT", rt);
            } else {
                fare.IsGROUP = "true";
            }

            if (needSleep) {
                DateTimeUtil.SleepSec(3);
            }

            RtInterceptorBean rtInterceptorBean = parseConfBean.rtInterceptorBean;

            rtInfoBean.rt = rt;
            rtInfoBean.isGroup = isGroup;
            rtInfoBean.isDomestic = parseConfBean.isDomestic;
            rtInfoBean.pnrRet = this.pnrRet;

            flightResult = this.parseRT(rtInfoBean, rtInterceptorBean);

            // op failed
            if (null == flightResult) {
                return opfail(xstream, this.pnrStatus.toString());
            }

            List<PnrRet.Flight> lsFlight = flightResult.pnrRetFlights;

            processDistanceAndSharecode(lsFlight);

            List<String> carrier = buildCarrier();

            try {
                // 国内， 团体票 目前不处理 PAT
                if (carrier.size() > 0 && StringUtils.isNotBlank(carrier.get(0)) && needFare) {
                    processPnrFare(parseConfBean, carrier, rtInfoBean);
                }
                if (parseConfBean.needERRule) {
                    processERRule(pnrRet);
                }
            } catch (BizException e) {
                // we can continue other work on these error
                String err = TZUtil.stringifyException(e);
                logger.error("biz err:{}", err);
                pnrRet.ERRORS_DETAIL += err;
            }
            String xmlPnrRet = "";
            String xmlMakeFareByPnr = "";
            if (parseConfBean.needPnrRet) {
                pnrRet.Flights = lsFlight;
                pnrRet.PassengerInfo = this.passengers;

                writer = new StringWriter();
                xstream.marshal(pnrRet, new CDataPrettyPrintWriter(writer, new NoNameCoder()));
                xmlPnrRet = writer.toString();
            }

            if (parseConfBean.needMakeFareByPNR) {
                fare.flightInfo.flightSegments = flightResult.flightSegments;
                fare.getFareTax().Passenger = StringUtils.join(makeFareByPnrPassenger, ";");
                writer = new StringWriter();
                xstream.marshal(fare, new CDataPrettyPrintWriter(writer, new NoNameCoder()));
                xmlMakeFareByPnr = writer.toString();
            }
            client.close();
            return Pair.with(xmlPnrRet, xmlMakeFareByPnr);
        } catch (BizException e) {
            String err = TZUtil.stringifyException(e);
            if (e.getRetCode() == ReturnCode.E_SESSION_EXPIRED) {
                throw e;
            } else {
                logger.error(err);
            }
            return Pair.with(err, err);
        } catch (Exception e) {
            String err = TZUtil.stringifyException(e);
            logger.error(err);
            return Pair.with(err, err);
        } finally {
            client.close();
        }

    }

    // choose Carrier of QTE by such rule :
    // 如果在航段里， 某个航司出现的最多， 就用那个，
    //
    // 如果都一样多， 就选航程最长的，
    //
    // M TYONYC 代表较高点票价，里程制票价计算中，出现较高点票价。
    // 例如：BJS YY TYO YY NYC M TYONYC2154.49 1S84.85NUC2239.34END 在这个票价计算中 M
    // TYONYC 代表整个行程（BJS-TYO-NYC)使用的是TYO-NYC的票价。

    //	WARNING! if there's no internaional flightSet , this function will return a blank list
    private List<String> choosAirline4QTE(CarrierChoosenMode mode) throws UfisException {
        Map<String, List<String>> airlineSegmentMap = new HashMap<String, List<String>>();

        List<String> carrierList = Lists.newArrayList();

        if (pnrRet.Flights == null) {
            return carrierList;
        }

        for (PnrRet.Flight flt : pnrRet.Flights) {
            if ("ARNK".equals(flt.FltType) || !isInternationalAirseg(flt)) {
                continue;
            }

            carrierList.add(flt.Carrier);

            String segment = flt.BoardPoint + flt.OffPoint;
            List<String> segments = airlineSegmentMap.get(flt.Carrier);

            if (null == segments) {
                segments = new ArrayList<String>();
                airlineSegmentMap.put(flt.Carrier, segments);
            }
            segments.add(segment);
        }

        boolean simpeReturn = false;
        if (mode == CarrierChoosenMode.SIMPLE) {
            simpeReturn = true;
        } else if (mode == CarrierChoosenMode.QUICK) {
            if (pnrRet.XsfsmItems.size() == 0) {
                simpeReturn = true;
            }
        }

        if (simpeReturn) {
            List<String> uniqCarrierList = Lists.newArrayList();
            for (String cr : carrierList) {
                if (!uniqCarrierList.contains(cr)) {
                    uniqCarrierList.add(cr);
                }
            }
            return uniqCarrierList;
        }

        int maxNum = -1;
        List<String> maxAirLineList = new ArrayList<String>();

        for (Entry<String, List<String>> entry : airlineSegmentMap.entrySet()) {
            if (entry.getValue().size() > maxNum) {
                maxNum = entry.getValue().size();
                maxAirLineList.clear();
                maxAirLineList.add(entry.getKey());
            } else if (entry.getValue().size() == maxNum) {
                maxAirLineList.add(entry.getKey());
            }
        }

        boolean foundResult = false;

        String result = "";
        // 出现最多的航司只有一个
        if (maxAirLineList.size() == 1) {
            result = maxAirLineList.get(0);
            foundResult = true;
        }

        List<String> maxSegAirlines = new ArrayList<>();

        Map<String, Integer> cityDistanceMap = Maps.newHashMap();

        if (mode == CarrierChoosenMode.QUICK) {
            for (XsfsmItem item : pnrRet.XsfsmItems) {
                cityDistanceMap.put(item.CityPair, NumberUtils.toInt(item.TPM));
            }
        }

        if (!foundResult) {
            // 出现对多的航司不止一个，比较哪个航司对应的航段距离长
            int maxSegDistance = 0;
            for (String airline : maxAirLineList) {
                List<String> segs = airlineSegmentMap.get(airline);
                // 计算对应航段的总长度
                int segsUnitDistance = 0;
                for (String seg : segs) {
                    if (mode == CarrierChoosenMode.QUICK) {
                        segsUnitDistance += cityDistanceMap.get(seg);
                    } else {
                        List<CityDistance> cityDistances = CityDistanceParser.parserDistanceByUfis(client, seg);
                        for (CityDistance cityDistance : cityDistances) {
                            segsUnitDistance += cityDistance.getTpm();
                        }
                    }
                }
                if (segsUnitDistance > maxSegDistance) {
                    maxSegAirlines.clear();
                    maxSegAirlines.add(airline);
                }
            }

            if (maxSegAirlines.size() > 0) {
                result = maxSegAirlines.get(0);
            } else {
                result = pnrRet.Flights.get(0).Carrier;
            }
        }

        List<String> ret = Lists.newArrayList();
        ret.add(result);

        if (maxSegAirlines.size() > 1) {
            for (int i = 0; i < maxSegAirlines.size(); i++) {
                ret.add(maxSegAirlines.get(i));
            }
        }

        for (String carrier : carrierList) {
            if (!ret.contains(carrier)) {
                ret.add(carrier);
            }
        }
        return ret;
    }

    // <Passenger>LI/JUN（名字）,CN（发证国家）/1234567（证件号）/CN（国籍）/09SEP90（出生日期）/M（性别）/09SEP18（证件有效期）;LIDS/DSD,CN/7654321/CN/09SEP90/M/09SEP18
    // <Passenger>LI/JUN（名字）,CN（发证国家）/1234567（证件号）/CN（国籍）/09SEP90（出生日期）/M（性别）/09SEP18（证件有效期）;LIDS/DSD,CN/7654321/CN/09SEP90/M/09SEP18</Passenger>

    private static String pureSkipHeadIdx(String line) {
        int i = 0;

        // skip head space
        for (; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (Character.isSpaceChar(ch)) {
                continue;
            } else {
                break;
            }
        }

        boolean startWithNumberIndx = false;

        // match all digit, if exist
        for (; i < line.length(); i++) {
            if (Character.isDigit(line.charAt(i))) {
                startWithNumberIndx = true;
                continue;
            } else {

                // " 123."
                if (startWithNumberIndx && line.charAt(i) == '.') {
                    i++;
                    return line.substring(i);
                } else {
                    // " 123x" , combine to previous line
                    return "";
                }
            }
        }
        return "";
    }

    // warning! lines which not start with " idx." will be combine to previous
    // line
    private MutableString skipHeadIdxCombineNextline(String[] lines, int idx) throws BizException {
        if (idx == 0) {
            idx = 1;
        }

        MutableString line = new MutableString(lines[idx]);

        if (StringUtils.isBlank(line)) {
            return new MutableString();
        }

        int i = 0;

        // skip head space
        for (; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (Character.isSpaceChar(ch)) {
                continue;
            } else {
                break;
            }
        }

        boolean startWithNumberIndx = false;

        // match all digit, if exist
        for (; i < line.length(); i++) {
            if (Character.isDigit(line.charAt(i))) {
                startWithNumberIndx = true;
                continue;
            } else {
                // " 123."
                if (startWithNumberIndx && line.charAt(i) == '.') {
                    i++;
                    return line.substring(i);
                } else {
                    // " 123x" , combine to previous line
                    lines[idx - 1] = lines[idx - 1] + line;
                    lines[idx] = "";
                    // place a empty line
                    return new MutableString();
                }
            }
        }

        logger.warn("line all digit==>{}" + line);
        return new MutableString();
    }


    private String getPsgType(String ptype) {
        if (StringUtils.equalsIgnoreCase("MR", ptype) || StringUtils.equalsIgnoreCase("MS", ptype)) {
            return "ADT";
        }

        if (StringUtils.equalsIgnoreCase("MSTR", ptype) || StringUtils.equalsIgnoreCase("MISS", ptype) || StringUtils.equalsIgnoreCase("CHD", ptype)) {
            return "CHD";
        }

        throw BizException.instance("invalid ptype:" + ptype);
    }

    Pattern NAME_WITH_BIRTHDAY = Pattern.compile("(\\w+)\\((.*?)\\)");

    private PnrRet.PassengerInfo.Passenger makePnrRetPassenger(String name, String defaultType, String forceType) {
        PnrRet.PassengerInfo.Passenger psg = new PnrRet.PassengerInfo.Passenger();

        name = StringUtils.trim(name);
        String[] narray = SPACE_PATTERN.split(name);

        String lastTk = narray[narray.length - 1];

        if (StringUtils.endsWith(name, "INF")) {

            this.hasInfant = true;
            psg.PsgStyle = "INF";
            psg.PsgType = "INF";

            psg.Name = name;
        } else if (StringUtils.endsWith(name, "CHD")) {

            this.hasChild = true;
            psg.PsgType = "CHD";
            psg.PsgStyle = "CHD";
            psg.Name = name;
        } else if (StringUtils.endsWith(name, "MISS")) {

            this.hasChild = true;
            if (StringUtils.isNotBlank(forceType)) {
                psg.PsgType = forceType;
            } else {
                psg.PsgType = "CHD";
            }
            psg.PsgStyle = "MISS";
            psg.Name = name;
        } else if (StringUtils.endsWith(name, "MSTR")) {

            this.hasChild = true;

            if (StringUtils.isNotBlank(forceType)) {
                psg.PsgType = forceType;
            } else {
                psg.PsgType = "CHD";
            }
            psg.PsgStyle = "MSTR";
            psg.Name = name;
        }

        Matcher m = NAME_WITH_BIRTHDAY.matcher(lastTk);
        if (m.find()) {
            lastTk = m.group(1);
        }

        if (PnrPassengerUtil.isPtype(lastTk)) {
            if (StringUtils.isNotBlank(forceType)) {
                psg.PsgType = forceType;
            } else {
                psg.PsgType = PnrPassengerUtil.getPsgType(lastTk);
            }

            psg.PsgStyle = lastTk;

            psg.Name = name;

            if (PnrPassengerUtil.isChildPtype(psg.PsgStyle)) {
                this.hasChild = true;
            }

            if ("INF".equalsIgnoreCase(psg.PsgType)) {
                this.hasInfant = true;
            }

        }

        if (StringUtils.isBlank(psg.Name)) {
            if (StringUtils.isNotBlank(forceType)) {
                psg.PsgType = forceType;
            } else {
                psg.PsgType = defaultType;
            }
            psg.PsgStyle = "";
            psg.Name = name.trim();
        }

        return psg;
    }

    // 1.ZHANG/XINYI MISS(12AUG11)
    private List<Passenger> processPassenger(String line) {
        List<Passenger> lsPassenger = Lists.newArrayList();

        String[] names = line.split("\\d+\\.");
        for (String name : names) {
            if (StringUtils.isNotBlank(name)) {
                PnrRet.PassengerInfo.Passenger psg = makePnrRetPassenger(name, "ADT", "");
                lsPassenger.add(psg);
            }
        }

        return lsPassenger;
    }

    private List<Passenger> processGroupPassenger(String line) {
        List<Passenger> lsPassenger = Lists.newArrayList();
        line = StringUtils.trim(line);

        return processPassenger(line);
    }

    /**
     * MSTR 男孩 MR 男人 MS 女人 MRSS 女孩
     * <p/>
     * 如果末尾 PnrPassengerUtil.isPtype , 则放入 psgType, 否则, 作为姓名,
     * <p/>
     * 1.CHEN/CHANGLI MS 2.HOU/YI MS 3.REN/NIANJIA MR 4.SUN/JIWEI MR
     * 5.YANG/JINYU MR
     * <p/>
     * 1.ZHOU/ER CHD 2.ZHOU/YI JMD3MM
     * <p/>
     * r
     *
     * @param line
     * @return
     */
    private List<Passenger> processNormalPassenger(String line, String pnr) {
        List<Passenger> lsPassenger = Lists.newArrayList();

        line = StringUtils.trim(line);

        if (line.endsWith(pnr)) {
            // remove PNR seg(但是团体票里不带)
            line = line.substring(0, line.length() - PnrParserConstant.PNR_LENGTH);
        }

        return processPassenger(line);
    }

    /**
     * 判断是否是航线信息
     * <p/>
     * "  7.  MU2025 X   SA18MAY  KMGBKK RR6   0820 1020          E"
     *
     * @return
     */
    private boolean isFlightLine(String line, boolean isParsePnrContent, String pnr) {
        if (StringUtils.isNotBlank(pnr) && StringUtils.contains(line, pnr)) {
            return false;
        }

        int i = 0;

        // skip head space
        for (; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (Character.isSpaceChar(ch)) {
                continue;
            } else {
                break;
            }
        }

        boolean startWithNumberIndx = false;

        // match all digit, if exist
        for (; i < line.length(); i++) {
            if (Character.isDigit(line.charAt(i))) {
                startWithNumberIndx = true;
                continue;
            } else {

                // " 123."
                if (startWithNumberIndx && line.charAt(i) == '.') {
                    i++;
                    String fltLine = line.substring(i);
                    if (isParsePnrContent) {
                        return fltLine.startsWith(" ") || fltLine.startsWith(" *");
                    } else {
                        return fltLine.startsWith("  ") || fltLine.startsWith(" *");
                    }
                } else {
                    // " 123x" , combine to previous line
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否是一个团队PNR
     *
     * @param lines
     * @param startIdx
     * @param passengerNumber
     * @return
     */
    private boolean judgeGroup(String[] lines, int startIdx, MutableInt passengerNumber) {
        int nameEndLineIdx = 0;
        passengers = new PnrRet.PassengerInfo();

        logger.debug("judgeGroup : startIdx& lines :{}:{}", startIdx, Arrays.deepToString(lines));

        String firstPsgLine = pureSkipHeadIdx(lines[startIdx]);
        if (StringUtils.isBlank(firstPsgLine)) {
            logger.info("startIdx:{}  blank firstPsgLine :{}", startIdx, Arrays.deepToString(lines));
            return false;
        }
        String[] nameInfos = firstPsgLine.split("\\s+");
        if (Character.isDigit(firstPsgLine.charAt(0))) {
            for (int idx = 1; idx < nameInfos.length - 1; idx++) {
                String psgNumStr = StringUtils.substring(nameInfos[idx], 2);
                int psgNum = NumberUtils.toInt(psgNumStr);
                if (nameInfos[idx].startsWith("NM") && (psgNum > 0 || "0".equals(psgNumStr))) {
                    passengerNumber.setValue(psgNum);
                    isGroupTicket = true;
                    passengers.IsGroup = "true";
                    return isGroupTicket;
                }
            }
            return false;
        } else {
            isGroupTicket = false;
            return false;
        }
    }

    /**
     * 2 种情况： （1）个人：
     * <p/>
     * 1.ZHOU/ER CHD 2.ZHOU/YI JMD3MM
     * <p/>
     * （2）团体未出票：
     * <p/>
     * 0.12BYC NM12 JY7GCN 0.29SHADSW/ST NM29 HVQDS5
     *
     * @return nameEndLine(Exclude)
     */

    enum IsGroup {
        yes, no, unsure
    }

    class JudgeGroupBean {
        List<String> lineArrays;
        int startIdx;
        String pnr;
        boolean isParsePnrContent;
        IsGroup isGroup = IsGroup.unsure;

        public JudgeGroupBean(List<String> lineArrays, int startIdx, String pnr, boolean isParsePnrContent, IsGroup isGroup) {
            super();
            this.lineArrays = lineArrays;
            this.startIdx = startIdx;
            this.pnr = pnr;
            this.isParsePnrContent = isParsePnrContent;
            this.isGroup = isGroup;
        }
    }

    Map<String, Triplet<Integer, Integer, JudgeGroupBean>> judgeGroupMap = Maps.newHashMap();

    /**
     * 精确判断是否是航程
     *
     * @param line
     * @return
     */
    private boolean preciseJudgeFlightLine(String line) {
        String ln = pureSkipHeadIdx(line);
        if (ln.startsWith("  ") || ln.startsWith(" *")) {
            if (getCarrierCodeMap().containsKey(StringUtils.substring(ln, 2, 4))) {
                if (StringUtils.isNumeric(StringUtils.substring(ln, 4, 7)) || StringUtils.isNumeric(StringUtils.substring(ln, 4, 8))) {
                    return true;
                }
            }
        }
        return false;
    }

    private Triplet<Integer, Integer, JudgeGroupBean> processNameLineJudgeGroup(JudgeGroupBean conf) {
        if (judgeGroupMap.containsKey(conf.pnr)) {
            return judgeGroupMap.get(conf.pnr);
        }

        int nameEndLineIdx = -1;
        int flightBeginLineIdx = -1;

        // logger.debug("startIdx& lines :{}:{}", startIdx,
        // Arrays.deepToString(lineArrays));
        logger.debug("processNameLineJudgeGroup : startIdx& lines :{}:\n{}", conf.startIdx, StringUtil.join(conf.lineArrays, "\n"));

        int nameStartIdx = conf.startIdx;

        int grpPsgNum = 0;
        if (isGroupTicket) {

            nameStartIdx++;

            //	    0.20SHA/SEBGROUP/SRS NM20 HN6K28 
            Pattern gFirstline = Pattern.compile("0\\.(\\d+)(.+?)\\s+NM(\\d+)");
            Matcher m = gFirstline.matcher(conf.lineArrays.get(conf.startIdx));
            if (m.find()) {
                String d1 = StringUtils.trim(m.group(1));
                String d2 = StringUtils.trim(m.group(3));

                if (StringUtils.isNumeric(d1) && StringUtils.isNumeric(d2)) {
                    grpPsgNum = NumberUtils.toInt(d2);
                }
            }
        }

        int testIdx = conf.startIdx;

        for (; testIdx < conf.lineArrays.size(); testIdx++) {
            if (isFlightLine(conf.lineArrays.get(testIdx), conf.isParsePnrContent, conf.pnr)) {
                flightBeginLineIdx = testIdx;
                break;
            }
        }

        if (!isGroupTicket && nameEndLineIdx == -1) {
            for (testIdx = conf.startIdx; testIdx < conf.lineArrays.size(); testIdx++) {
                if (conf.lineArrays.get(testIdx).indexOf(PNR) > 0) {
                    nameEndLineIdx = testIdx + 1;
                    break;
                }
            }
        }

        // 0.18JB NM18 HYQ8K9
        // 1.丁建华 2.黄建英 3.廖莜蓉 4.李静 5.梅娟芳 6.秦德荣
        // 7.王徽 8.王亚芳 9.王云秀 10.万惠丽 11.吴军 12.谢虹
        // 13.许汝美 14.杨凤英 15.尹茹 16.张伦波 17.章燕 18.周哲人 19. MU5271 T TH19DEC SHASZX
        // RR18 1730 1950 E T2T3
        // 20.SZX/T SZX/T 0755-25155502/SZX JVBANG INTERNATIONAL TRAVEL TICKET
        // CENTRE/YU YAN LI ABCDEFG
        // 21.25155502
        // 22.BY OPT 904 2013/11/26 1603A
        // 23.TL/1800/10DEC/SZX290
        // 24.SSR FOID

        //	0.6QC NM6 JXNV2D   
        //	 1.蔡林飞 2.龚雪芳 3.倪伟芝 4.汤福仙 5.王飞凤 6.吴思澄   7. *MU9455 Q   TH06MAR  SHAKMG HK6   1510 1820          E T2-- OP-FM9455   
        //	 8.  MU9745 Q   MO10MAR  KMGJHG HK6   2210 2300          E  
        //	 9.  MU9746 Q   WE12MAR  JHGKMG HK6   2340 0030+1        E  
        //	10. *MU9456 Q   TH13MAR  KMGSHA HK6   1920 2220          E --T2 OP-FM9456   
        //	11.SHA/T SHA/T 021-55666666/SHA SHENG JIE TOUR TRADE CO.,LTD/NI QI ABCDEFG  

        if (nameEndLineIdx == -1) {
            int lineToInsert = 0;
            int offsetToBreak = 0;
            boolean needBreakAndInsert = false;

            FINISH:
            for (testIdx = conf.startIdx; testIdx < conf.lineArrays.size(); testIdx++) {

                int targetIdx = grpPsgNum + 1;
                Pattern fltStartIdxPattern = Pattern.compile(targetIdx + "\\.");
                Matcher m = fltStartIdxPattern.matcher(conf.lineArrays.get(testIdx));

                //		19.李珍珍 20.李忠 21.马烈 22.庞开国 23.潘振兴 24.彭波   25.  CA1831 V   FR28MAR 
                //		change to
                //		19.李珍珍 20.李忠 21.马烈 22.庞开国 23.潘振兴 24.彭波   
                //		25.  CA1831 V   FR28MAR 
                if (m.find()) {
                    if (m.start() > 0 && !StringUtils.isBlank(conf.lineArrays.get(testIdx).substring(0, m.start()))) {
                        needBreakAndInsert = true;
                        lineToInsert = testIdx;
                        nameEndLineIdx = testIdx + 1;
                        offsetToBreak = m.start();
                        break FINISH;
                    }
                }

                if (!needBreakAndInsert) {
                    // when met: "10. *MU9456 N TU04FEB KMGSHA RR9 1920 2220 E --T2"
                    // break at once
                    String ln = pureSkipHeadIdx(conf.lineArrays.get(testIdx));
                    if (ln.startsWith("  ") || ln.startsWith(" *")) {
                        if (getCarrierCodeMap().containsKey(StringUtils.substring(ln, 2, 4))) {
                            if (StringUtils.isNumeric(StringUtils.substring(ln, 4, 7)) || StringUtils.isNumeric(StringUtils.substring(ln, 4, 8))) {
                                nameEndLineIdx = testIdx;
                                break FINISH;
                            }
                        }
                    }
                }
            }

            if (needBreakAndInsert) {
                conf.lineArrays.add(lineToInsert + 1, conf.lineArrays.get(lineToInsert).substring(offsetToBreak));
                conf.lineArrays.set(lineToInsert, conf.lineArrays.get(lineToInsert).substring(0, offsetToBreak));
                flightBeginLineIdx = lineToInsert + 1;
            }
        }

        List<Passenger> lsPassenger = Lists.newArrayList();
        if (!isGroupTicket) {

            passengers = new PnrRet.PassengerInfo();

            //	    [1.CHAN/YECK LEE 2.CHUA/CHEE S, ENG 3.CHUA/HOW CHUAN 4.GOH/LEE HOON               ,

            for (int i = conf.startIdx; i < nameEndLineIdx - 1; i++) {
                if (Character.isAlphabetic(conf.lineArrays.get(i + 1).charAt(0))) {
                    conf.lineArrays.set(i, conf.lineArrays.get(i) + conf.lineArrays.get(i + 1));
                    conf.lineArrays.set(i + 1, "");
                }
            }

            for (int i = conf.startIdx; i < nameEndLineIdx; i++) {
                if (!StringUtils.isBlank(conf.lineArrays.get(i))) {
                    List<Passenger> psg = processNormalPassenger(conf.lineArrays.get(i), conf.pnr);
                    lsPassenger.addAll(psg);
                }
            }

            passengers.Passengers = lsPassenger;
        } else {
            for (int i = nameStartIdx; i < nameEndLineIdx; i++) {
                List<Passenger> psg = processGroupPassenger(conf.lineArrays.get(i));
                lsPassenger.addAll(psg);
            }

            List<Passenger> filteredPassengerList = Lists.newArrayList();
            for (Passenger psg : lsPassenger) {
                if (StringUtils.isNotBlank(psg.Name)) {
                    filteredPassengerList.add(psg);
                }
            }

            passengers.Passengers = filteredPassengerList;
        }
        judgeGroupMap.put(conf.pnr, Triplet.with(nameEndLineIdx, flightBeginLineIdx, conf));
        return judgeGroupMap.get(conf.pnr);
    }

    // 1- *MU8763 DS# JZ YA MZ HL NL PVGNRT 0850 1235 767 0 E ( JL872 T1 T2 2:45
    // 2 MU523 DS# JA CA D7 I5 YA BA MA EA HA KA PVGNRT 0910 1250 323 0^ E ( LA
    // NA RA SA V1 TS GQ ZA QS T1 T2 2:40
    static public Optional<String> processAvh4CodeShare(String content, String fullFltNumber) {
        if (StringUtils.isBlank(content)) {
            return Optional.absent();
        }

        String[] lines = content.split("(\r|\n)+");

        for (String line : lines) {
            int fullFltNumberIdx = StringUtils.indexOf(line, fullFltNumber);
            if (fullFltNumberIdx > 0) {
                char star = line.charAt(fullFltNumberIdx - 1);
                if (star == '*') {
                    int lparIdx = StringUtils.indexOf(line, "(");
                    String tail = StringUtils.substring(line, lparIdx);

                    Matcher m = AvhCodeSharePattern.matcher(tail);

                    if (m.find()) {
                        String codeShare = StringUtils.trim(m.group(0));
                        return Optional.of(codeShare);
                    } else {
                        logger.debug("fail to parser avh codeShare:{}", line);
                    }
                }
            }
        }
        return Optional.absent();
    }

    // 23.FN/FCNY3850.00/SCNY2550.00/C0.00/XCNY682.00/TCNY90.00CN/TCNY62.00TW/
    // TCNY530.00YQ/ACNY4532.00

    // FN/A/FCNY1460.00/SCNY1460.00/C3.00/XCNY170.00/TCNY50.00CN/TCNY120.00YQ/
    // ACNY1630.00

    // FN/A/FCNY950.00/SCNY950.00/C3.00/XCNY60.00/TEXEMPTCN/TCNY60.00YQ/ACNY1010.00

    final static String ALL_DIGIT = "0123456789";

    // can't idenitify ADT and child on this biz , so use fix ADT
    private void buildPatFromFn(String str) {
        String psgType = "ADT";

        String[] segs = StringUtils.split(str, "/");

        if (segs.length > 1 && "IN".equals(segs[1])) {
            psgType = "INF";
        }

        if (this.hasChild) {
            psgType = "CHD";
        }

        Map<String, List<String>> patMap = Maps.newHashMap();

        // build map
        for (String seg : segs) {
            int idx = StringUtils.indexOfAny(seg, ALL_DIGIT);

            String key = "";
            String value = "";
            if (idx > 0) {
                key = StringUtils.substring(seg, 0, idx).trim();
                value = StringUtils.substring(seg, idx).trim();
            } else {
                key = seg;
                value = "";
            }

            if (!patMap.containsKey(key)) {
                List<String> list = Lists.newArrayList();
                patMap.put(key, list);
            }

            patMap.get(key).add(value);
        }

        // judge the format, must has FCNY, TCNY
        if (!patMap.containsKey("FCNY")) {
            return;
        }

        PnrRet.PatItem patItem = new PnrRet.PatItem();

        patItem.PsgType = psgType;

        PatResult patResult = new PatResult();
        List<PatResult> lsPatResult = Lists.newArrayList();
        lsPatResult.add(patResult);

        patResult.patLineStr = str;

        patItem.PatStr = str;
        patItem.PatResults = lsPatResult;

        patResult.sfc = "01";
        patResult.fareBasis = "UNKNOWN";

        // 成人
        // FN/A/FCNY350.00/SCNY350.00/C3.00/XCNY170.00/TCNY50.00CN/TCNY120.00YQ/
        // -
        // ACNY520.00

        // 婴儿
        // FN/A/IN/FCNY180.00/SCNY180.00/C0.00/TEXEMPTCN/TEXEMPTYQ/ACNY180.00
        if (patMap.containsKey("TEXEMPTCN")) {
            patResult.tax = "0";
        }
        if (patMap.containsKey("TEXEMPTYQ")) {
            patResult.yq = "0";
        }

        boolean isVaildBuildPatFromFN = true;

        PROC:
        for (String key : patMap.keySet()) {
            // TCNY50.00CN/TCNY120.00YQ
            List<String> values = patMap.get(key);

            // process ACNY(adult)
            if (StringUtils.equalsIgnoreCase(key, "ACNY")) {
                for (String v : values) {
                    int idx = StringUtils.indexOfAny(v, ALL_DIGIT);
                    if (idx >= 0) {
                        String acny = StringUtils.substring(v, idx);
                        patResult.total = acny;
                    }
                }
            }

            // process FCNY
            if (StringUtils.equalsIgnoreCase(key, "FCNY")) {
                for (String v : values) {
                    int idx = StringUtils.indexOfAny(v, ALL_DIGIT);
                    if (idx >= 0) {
                        String fcny = StringUtils.substring(v, idx);
                        patResult.fare = fcny;
                    } else {
                        isVaildBuildPatFromFN = false;
                        break PROC;
                    }
                }
            }

            // process YQ(tax) AND CN(tax)
            if (StringUtils.equalsIgnoreCase(key, "TCNY")) {
                for (String v : values) {
                    // YQ
                    int idx = StringUtils.lastIndexOf(v, "YQ");
                    if (idx > 0) {
                        String suffix = StringUtils.substring(v, idx);
                        if (StringUtils.equalsIgnoreCase(suffix, "YQ")) {
                            patResult.yq = StringUtils.substring(v, 0, idx);
                        }
                    }

                    // CN
                    idx = StringUtils.lastIndexOf(v, "CN");
                    if (idx > 0) {
                        String suffix = StringUtils.substring(v, idx);
                        if (StringUtils.equalsIgnoreCase(suffix, "CN")) {
                            patResult.tax = StringUtils.substring(v, 0, idx);
                        }
                    }
                }
            }
        }

        if (patMap.containsKey("A")) {
            if (isVaildBuildPatFromFN) {
                patItemsFromFnA.add(patItem);
            }
        } else {
            patItemsFromFn.add(patItem);
        }
    }

    // 23.FN/FCNY3850.00/SCNY2550.00/C0.00/XCNY682.00/TCNY90.00CN/TCNY62.00TW/
    // TCNY530.00YQ/ACNY4532.00

    private void processFN(String str) {
        int fnIdx = str.indexOf("FN/");

        if (fnIdx >= 0) {
            String fnStr = StringUtils.substring(str, fnIdx + 3);

            pnrRet.FnStr = fnStr;
        }
    }

    // XN/IN/ZHOU/YI INF(MAR12)/P1 ==> ZHOU/YI INF, MAR12
    // XN/IN/XIE/HUI(MAY12)/P1
    private void processINF(MutableString ssr) {
        if (parseStatus.xninParsed) {
            return;
        }

        List<String> psgStrList = makeFareByPnrPassenger;

        PassengerInfo pnrRetPassengers = passengers;

        Pattern infpat = Pattern.compile("XN/IN/(.*?) INF\\s*\\((.+)\\)/P(\\d+)");
        // ZHOU/YI
        // MAR12
        // 1
        Matcher m = infpat.matcher(ssr);
        if (m.find()) {
            psgStrList.add(m.group(1) + " INF," + m.group(2));

            PnrRet.PassengerInfo.Passenger psg = makePnrRetPassenger(m.group(1), "INF", "INF");

            psg.FollowPsgId = m.group(3);

            pnrRetPassengers.Passengers.add(psg);
            return;
        }

        // XN/IN/XIE/HUI(MAY12)/P1
        infpat = Pattern.compile("XN/IN/(.*?)\\((.+)\\)/P(\\d+)");
        m = infpat.matcher(ssr);
        if (m.find()) {
            // for international pnr
            psgStrList.add(m.group(1) + " INF," + m.group(2));

            PnrRet.PassengerInfo.Passenger psg = makePnrRetPassenger(m.group(1), "INF", "INF");

            psg.FollowPsgId = m.group(3);

            pnrRetPassengers.Passengers.add(psg);
            return;
        }
    }

    private void processOSI(MutableString ssrStr) {
        if (parseStatus.osiParsed) {
            return;
        }

        pnrRet.OsiNodes.OsiStr.add(ssrStr.toString());

        if (ssrStr.indexOf("INF") > 0) {
            hasInfant = true;
        }

        String[] ssrArray = SPACE_PATTERN.split(ssrStr.trim());
        if (ssrArray.length > 2 && StringUtils.startsWith(ssrArray[2], "CTCT")) {
            processCTCT(ssrStr.trim());
        }
    }

    /**
     * 获取PNR大编码
     * <p/>
     * 逻辑是首先根据rt内容获取，如果获取不到，再用rtr获取，但是此处注意因为rtj进行了翻页操作，
     * rtr现实的是第二页的结果，所以要向前翻一页
     * <p/>
     * RMK CA/MJ6W7F
     * <p/>
     * *MU3837 T   FR24OCT  FUOPVG HK1   1200 1420          E --T1 OP-KN5869
     * -CA-NBGX6P
     *
     * @param ssr
     */
    private void processRMK(MutableString ssr) {
        if (parseStatus.rmkParsed) {
            return;
        }
        //RMK行去掉结尾的+或-
        ssr = removeSuffix(ssr);
        pnrRet.RmkNodes.RmdStr.add(ssr.toString());

        Matcher m = BIG_PNR_PATTERN.matcher(ssr);
        if (m.find()) {
            PnrRet.BIG_PNR bigPnr = new PnrRet.BIG_PNR();
            bigPnr.Carrier = m.group(1);
            bigPnr.PNR = m.group(2);

            pnrRet.BigPnr = bigPnr;
        }
    }

    private void processSSR(MutableString ssrStr) {
        if (parseStatus.ssrParsed) {
            return;
        }

        pnrRet.SsrNodes.SsrStr.add(ssrStr.toString());


        String[] ssrArray = SPACE_PATTERN.split(ssrStr.trim());

        //去掉最后一行的+-
        MutableString ssrFormatStr = removeSuffix(ssrStr);

        // SSR CHLD HO HK1 25FEB06/P1
        if (StringUtils.equalsIgnoreCase("CHLD", ssrArray[1])) {
            hasChild = true;
        }

        if (StringUtils.equalsIgnoreCase("OTHS", ssrArray[1])) {
            processOTHS(ssrFormatStr);
        }

        if (StringUtils.equalsIgnoreCase("ADTK", ssrArray[1])) {
            processADTK(ssrFormatStr);
        }

        if (StringUtils.equalsIgnoreCase("DOCS", ssrArray[1])) {
            processDOCS(ssrFormatStr);
        }

        if (StringUtils.equalsIgnoreCase("FOID", ssrArray[1])) {
            processFOID(ssrFormatStr);
        }

        if (StringUtils.equalsIgnoreCase("CKIN", ssrArray[1])) {
            processCKIN(ssrFormatStr);
        }
    }

    // 把额外信息attatch到 PnrRet.Passengers 和 MakeFareByPNR.FareTax.Passenger
    private void processDOCS(MutableString ssr) {
        if (parseStatus.docsParsed) {
            return;
        }

        if (ssr.equals("SSR DOCS")) {
            return;
        }

        List<Passenger> psgList = passengers.Passengers;
        List<String> psgStrList = makeFareByPnrPassenger;

        String str = ssr.toString();

        Pattern prefix = Pattern.compile("((\\w+\\s+){4}).*");

        // prefix = Pattern.compile("\\w+.*");

        // 11.SSR DOCS KA HK1 P/CN/232224231/CN/19APR13/M/19APR19/WANG/YI INF/P4
        // 12.SSR DOCS KA HK1 P/CN/232124231/CN/19APR12/F/19APR18/JIAN/YILING
        // CHD/P2
        // 13.SSR DOCS KA HK1 P/CN/232454231/CN/19APR90/M/19APR19/LIAO/YI/P3
        // 14.SSR DOCS KA HK1 P/CN/232322331/CN/19APR91/M/19APR18/HUA/YI/P1

        Matcher m = prefix.matcher(str);
        if (m.find()) {
            int i = m.end(1);
            String detail = str.substring(i);
            // P/CN/232224231/CN/19APR13/M/19APR19/WANG/YI INF/P4
            // 14.SSR DOCS AA HK1
            // P/CHN/E13115118/CHN/28APR92/F/20FEB23/ZHU/YENI/P2
            // 15.SSR DOCS AA HK1
            // P/CHN/E13096976/CHN/17APR93/M/15FEB23/WANG/HAO/P1

            // scene1: P/CN/10CY36445/CN/04AUG76/M/06NOV15/BOMBLED/
            String[] dsegs = detail.split("/");

            String[] restArray = new String[6];
            System.arraycopy(dsegs, 1, restArray, 0, 6);

            String dstr = StringUtils.join(restArray, "/");

            String[] nameArray = new String[2];
            System.arraycopy(dsegs, 7, nameArray, 0, 2);
            // String nameStr = StringUtils.join(nameArray, "/");

            boolean needAppendPsgInfo = false;

            int psgInfoIdx = 0;
            // /P4
            if (dsegs.length == 10 && dsegs[5].length() == 1) {
                psgInfoIdx = NumberUtils.toInt(dsegs[9].substring(1)) - 1;
                needAppendPsgInfo = true;
            }

            // /P4
            if (dsegs.length == 11 && dsegs[5].length() == 1) {
                psgInfoIdx = NumberUtils.toInt(dsegs[10].substring(1)) - 1;
                needAppendPsgInfo = true;
            }

            if (needAppendPsgInfo) {
                if (this.parseConfBean.needPnrRet && psgInfoIdx >= 0 && psgList.size() >= psgInfoIdx) {
                    // CN/G57976495/CN/01MAR93/F/20DEC21
                    psgList.get(psgInfoIdx).PsgInfo = dstr;
                }

                if (this.parseConfBean.needMakeFareByPNR && psgInfoIdx >= 0 && psgStrList.size() >= psgInfoIdx) {

                    if (!passengerHasAppendDetailInfo.containsKey(psgInfoIdx) || !passengerHasAppendDetailInfo.get(psgInfoIdx)) {

                        psgStrList.set(psgInfoIdx, makeFareByPnrPassenger.get(psgInfoIdx) + "," + dstr);
                        passengerHasAppendDetailInfo.put(psgInfoIdx, true);
                    }
                }
            }
        } else {
            logger.info("unknown DOCS FORMAT:{}", ssr.toString());
        }
    }

    final static Pattern CKIN_TRI_PROTO = Pattern.compile("VICO(DK(\\d+))");

    public boolean hasTripartiteAgreementNum() {
        return StringUtils.isNotBlank(pnrRet.TripartiteAgreementNum);
    }

    // SSR CKIN CZ HK3 VICODK0208130, use DK+\d+ as the characteristic
    private void processCKIN(MutableString ssr) {
        if (parseStatus.ckinParsed) {
            return;
        }

        String[] ckinTriProtocal = SPACE_PATTERN.split(ssr);
        if (ckinTriProtocal.length < 5) {
            return;
        }

        Matcher m = CKIN_TRI_PROTO.matcher(ckinTriProtocal[4]);
        if (m.find()) {

            pnrRet.TripartiteAgreementStrs.add(ssr.toString());

            pnrRet.TripartiteAgreementNum = m.group(1);
        }
    }

    private void processFOID(MutableString ssr) {
        if (parseStatus.foidParsed) {
            return;
        }

        Matcher m = FOID_PATTERN.matcher(ssr);
        if (m.matches()) {
            // SSR FOID MU HK1 NI370682198510290835/P1
            String foidStr = m.group(1);
            String psgID = m.group(2);

            PnrRet.SSR_NODES.SSR_FOID ssrFoid = new PnrRet.SSR_NODES.SSR_FOID();
            ssrFoid.FoidStr = foidStr;
            ssrFoid.PsgID = psgID;
            pnrRet.SsrNodes.SsrFoid.add(ssrFoid);
        }
    }

    Set<String> ctctSet = Sets.newHashSet();

    // 12.OSI YY CTCT58525587
    private void processCTCT(MutableString ssrStr) {

        Matcher m = CTCT_PATTERN.matcher(ssrStr);
        if (m.find()) {
            String carrier = m.group(G_CARRIER);
            String phoneNumber = m.group(G_NUMBER);
            PnrRet.OSI_NODES.OSI_CTCT osiCtct = new PnrRet.OSI_NODES.OSI_CTCT();

            osiCtct.Carrier = carrier;
            osiCtct.PhoneNumber = phoneNumber;
            String key = carrier + phoneNumber;
            if (!ctctSet.contains(key)) {
                pnrRet.OsiNodes.OsiCtct.add(osiCtct);
                ctctSet.add(key);
            }
        }
    }


    private void processOTHS(MutableString ssr) {
        if (parseStatus.othsParsed) {
            return;
        }
        if (!PnrDateUtil.isOTHS_TimeLimitStr(ssr.toString())) {
            return;
        }
        processADTK(ssr);
    }

    // 非南航
    // FC/A/SHA MU PEK 960.00YDM85 CNY960.00END **9932220
    // FP/CASH,CNY/*9932220

    // 南航
    // .FC/A/SHA B-20NOV13 A-20NOV13 CZ CAN 1060.00YDK83 CNY1060.00END
    // **DK1302350

    private void processFP_FC(String str) {
        String tagStr = "**";
        if (StringUtils.startsWith(str, "FP")) {
            tagStr = "*";
        }

        int numIdx = StringUtils.indexOf(str, tagStr);

        if (numIdx < 0)
            return;

        int numEnd = StringUtils.indexOf(str, " ", numIdx);

        String maybeTriAgrNum = "";
        if (numEnd > 0) {
            maybeTriAgrNum = StringUtils.substring(str, numIdx + tagStr.length(), numEnd);
        } else if (numEnd < 0) {
            maybeTriAgrNum = StringUtils.substring(str, numIdx + tagStr.length());
        }

        if (!"(IN)".equals(maybeTriAgrNum)) {
            pnrRet.TripartiteAgreementStrs.add(str);
            pnrRet.TripartiteAgreementNum = maybeTriAgrNum;
        }
    }

    // SSR ADTK
    // SSR OTHS
    private void processADTK(MutableString ssr) {
        if (parseStatus.adtkParsed) {
            return;
        }
        try {
            TimeLimitTokenRepo repo = new TimeLimitParser(ssr.toString()).parse();

            ticketIssueTimeLimitList.add(PnrDateUtil.getTimeLimitFromTokenRepo(repo));
            fare.addTimeLimitProceedStr(ssr.toString());
        } catch (RuntimeException e) {
            String err = TZUtil.stringifyException(e).substring(0, PnrParserConstant.MAX_TIME_LIMIT_ERROR_INFO_LENGTH);
            logger.warn("parse timeLimit err:{}", err);
            fare.debugInfo.TimeLimitError = err;
        }
    }

    private List<PnrRet.FN.Item> parseNFItems(String taxStr) {
        List<PnrRet.FN.Item> items = Lists.newArrayList();

        StringTokenizer st = new StringTokenizer(taxStr);

        // TAX CNY 90CN CNY 155TS CNY 950YQ

        final int INIT = 0;
        final int BEGIN_CURRENCY = 1;
        final int BEGIN_VALUE = 2;

        int status = INIT;

        String tk;
        String currency = "";
        String fullValue = "";
        while (st.hasMoreTokens()) {
            tk = st.nextToken();

            switch (status) {
                case INIT:
                    if (StringUtils.equals(tk, "TAX") || StringUtils.equals(tk, "XT")) {
                        status = BEGIN_CURRENCY;
                        continue;
                    }
                    break;
                case BEGIN_CURRENCY:
                    currency = tk;
                    status = BEGIN_VALUE;
                    break;
                case BEGIN_VALUE:
                    fullValue = tk;

                    /**
                     * on scene: 28XFORD4.5, how to process ?
                     *
                     * 初步结论： 数字 加上2位的税编码
                     */

                    try {
                        StreamTokenizer steam = new StreamTokenizer(new StringReader(fullValue));

                        String Value = "";
                        String ExtType = "";
                        int type = steam.nextToken();
                        if (type == StreamTokenizer.TT_NUMBER) {
                            Value = Integer.toString((int) steam.nval);
                        }

                        type = steam.nextToken();
                        if (type == StreamTokenizer.TT_WORD) {
                            ExtType = steam.sval;
                        }

                        if (ExtType.length() > 2) {
                            ExtType = ExtType.substring(0, 2);
                        }

                        // String ExtType = fullValue.substring(fullValue.length() -
                        // 2,
                        // fullValue.length());
                        // String Value = fullValue.substring(0, fullValue.length()
                        // -
                        // 2);

                        PnrRet.FN.Item item = new PnrRet.FN.Item();
                        item.Type = "T";
                        item.Currency = currency;
                        item.Value = Value;
                        item.ExtType = ExtType;

                        items.add(item);
                    } catch (IOException e) {
                        logger.error("{}", e);
                    }

                    status = BEGIN_CURRENCY;
                    break;
            }
        }
        return items;
    }

    /**
     * return 0 when exception
     *
     * @param qvalueStr
     * @return
     */
    private double getQValueFromString(String qvalueStr) {
        StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(qvalueStr));

        try {
            while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {

                if (tokenizer.ttype == StreamTokenizer.TT_WORD) {
                    continue;
                } else if (tokenizer.ttype == StreamTokenizer.TT_NUMBER) {
                    return tokenizer.nval;
                } else if (tokenizer.ttype == StreamTokenizer.TT_EOL) {
                    return 0;
                }

            }
        } catch (IOException e) {
            logger.error(TZUtil.stringifyException(e));
        }
        return 0;
    }

    /**
     * <Item Type="A" Currency="CNY" Value="4874.00" ExtType=""/>
     * <p/>
     * <p/>
     * scene1: FARE CNY 2840 *IT FARE* TAX CNY 90CN CNY 96HK CNY 404YR TOTAL
     * 25APR13SHA CX HKG M/IT CX SHA Q4.25M/IT END ROE6.219820
     *
     * @param fareSegList
     * @return
     */
    public PnrRet.FN processFair(Map<String, MutableString[]> fareSegList, String psgType, String FNNo) {
        MutableString[] nucStrList = fareSegList.get(PnrParserConstant.QTE_KEY_QVALUE);

        SegPriceSyntaxTree segPrice = new SegPriceSyntaxTree();
        if (nucStrList != null) {
            try {
                segPrice = SegPriceParser.segmentsPriceParser(nucStrList);
                segPrice.psgType = psgType;
                pnrRet.segPrices.add(segPrice);
            } catch (Exception e) {
                logger.error("err in nuc caculate:{}", TZUtil.stringifyException(e));
                pnrRet.ERRORS_DETAIL += TZUtil.stringifyException(e);
            }
        }

        PnrRet.FN fn = new PnrRet.FN();

        fn.PsgType = psgType;
        fn.FNNo = FNNo;

        List<PnrRet.FN.Item> items = Lists.newArrayList();
        fn.items = items;

        PnrRet.FN.Item itemA = new PnrRet.FN.Item();

        /**
         * 在右上角有个"IT FARE" 这个面价在开票的时候是手工填入的 或者可以开具无面价票
         */

        boolean isIT_FARE = false;

        MutableString[] fareLineArray = fareSegList.get(PnrParserConstant.QTE_KEY_FARE);

        if (fareLineArray == null || fareLineArray.length == 0) {
            throw BizException.instance("no Fare seg found");
        }

        MutableString fareLine = fareLineArray[0];

        // 总价
        String totalFareStr = "";

        if (fareLine.trim().endsWith("*IT FARE*")) {
            isIT_FARE = true;

            itemA.Type = "IT_FARE";
            itemA.Value = "IT FARE";
        }

        // FARE HKD 1570 EQUIV CNY 1260
        // ==>1260
        String fairAmountStr = "";

        // ==>1570
        String orignalFairAmountStr = "";

        if (!isIT_FARE) {
            /**
             * TOTAL CNY 7635
             */
            MutableString[] fairStrArray = fareSegList.get(PnrParserConstant.QTE_KEY_FARE);

            //			FARE  CNY    5700                                                               
            //			TAX   CNY     90CN CNY     46DE CNY   3376XT                                    
            //			TOTAL CNY    9212                                    
            if (fairStrArray.length != 3 && fairStrArray.length != 1) {
                throw BizException.instance(ReturnCode.ERROR, "invalid Fare seg");
            }

            MutableString fairStr = fairStrArray[0];
            String[] fair = SPACE_PATTERN.split(fairStr);

            if (fair.length == 3) {
                fairAmountStr = fair[2];

                itemA = new PnrRet.FN.Item();
                itemA.Value = fairAmountStr;
                itemA.Type = fair[0];
                itemA.Currency = fair[1];
                items.add(itemA);

            } else if (fair.length == 6) {
                fairAmountStr = fair[5];
                orignalFairAmountStr = fair[2];

                itemA = new PnrRet.FN.Item();
                itemA.Value = fairAmountStr;
                itemA.Type = fair[0];
                itemA.Currency = fair[4];
                items.add(itemA);
            }

            if (fairStrArray.length >= 3) {

                // 总价
                String totalLineStr = fairStrArray[2].toString();
                String[] total = SPACE_PATTERN.split(totalLineStr);

                itemA = new PnrRet.FN.Item();
                itemA.Type = "A";

                // TOTAL CNY 7635
                if (total.length == 3) {
                    itemA.Value = total[2];
                    itemA.Currency = total[1];
                } else if (total.length == 6) {
                    itemA.Value = total[4];
                    itemA.Currency = total[5];
                } else {
                    throw BizException.instance("invalid Fare seg");
                }
                totalFareStr = itemA.Value;
                items.add(itemA);
            }
        } else { // IT*FARE
            MutableString[] fairStrArray = fareSegList.get(PnrParserConstant.QTE_KEY_FARE);

            if (fairStrArray.length != 3 && fairStrArray.length != 1) {
                throw BizException.instance("invalid Fare seg");
            }

            MutableString fairStr = fairStrArray[0];
            String[] fair = SPACE_PATTERN.split(fairStr);

            if (fair.length == 5) {
                itemA = new PnrRet.FN.Item();
                itemA.Value = fair[2];
                itemA.Type = fair[0];
                itemA.Currency = fair[1];
                items.add(itemA);
            } else {
                throw BizException.instance("invalid Fare seg");
            }
        }

        if (fareSegList.get(PnrParserConstant.QTE_KEY_FARE).length > 1) {
            // tax line
            List<PnrRet.FN.Item> taxItem = parseNFItems(fareSegList.get(PnrParserConstant.QTE_KEY_FARE)[1].toString());
            items.addAll(taxItem);
        }

        MutableString[] qvs = fareSegList.get(PnrParserConstant.QTE_KEY_QVALUE);

        String qvalueStr = "";
        double roe = 1;

        if (null != qvs) {
            for (MutableString qv : qvs) {
                String[] values = SPACE_PATTERN.split(qv);
                for (String v : values) {
                    if (v.startsWith("Q")) {
                        qvalueStr = v.substring(1);
                    }

                    if (v.startsWith("ROE")) {
                        SegpriceLexer lexer = new SegpriceLexer(v.substring(3));

                        SPToken tk = lexer.nextNumber();
                        if (tk != null) {
                            roe = tk.tkImgNumberValue;
                        }
                    }
                }
            }
        }

        String rateStr = "";

        // RATE USED 1HKD=0.794700CNY
        MutableString[] rates = fareSegList.get(PnrParserConstant.QTE_KEY_RATE);
        if (rates != null && rates.length == 1) {
            if (rates.length > 1) {
                throw BizException.instance("more than 1 rate line");
            }
            rateStr = rates[0].toString();

            Matcher m = RATE_PATTERN.matcher(rateStr);
            if (m.matches()) {
                rateStr = m.group(1);
            }
        }

        int totalFareInt = NumberUtils.toInt(totalFareStr);

        double fairAmount = NumberUtils.toDouble(fairAmountStr);
        double orignalFaireAmount = NumberUtils.toDouble(orignalFairAmountStr);

        double rate = NumberUtils.toDouble(rateStr, 1);
        double qvalue = getQValueFromString(qvalueStr);

        segPrice.rateValue = rate;

        double qmount = 0;
        for (SegPrice seg : segPrice.segmentsPrices) {
            for (Double q : seg.qvalue) {
                qmount += q * segPrice.rateValue * segPrice.roeValue;
            }
        }
        segPrice.qmount = qmount;

        int qmountInt10ceil = (int) PnrNumerUtil.up10ceil(segPrice.qmount);
        segPrice.qvalue = qmountInt10ceil;

        int tax = totalFareInt - (int) fairAmount;

        // force set to 1
        fare.getFareTax().StandPrice = "1";

        fare.getFareTax().QValue = Integer.toString(qmountInt10ceil);

        int standPrice = NumberUtils.toInt(fare.getFareTax().StandPrice);
        int salePrice = (int) PnrNumerUtil.up10ceil(fairAmount - qmount);

        String baseFareStr = (fairAmount - qmount) + "";

        if (psgType.equalsIgnoreCase(PnrParserConstant.PASSENGER_TYPE_ADT)) {
            // <StandPrice> 1 代表票面价 ， 0代表纸质文件价（代表BasePrice都是错的）

            // < BasePrice> 成人净价 String 不含税，不含Q，含代理费( 不进整) ==> 总价 - q值 - 税
            // < SalePrice > 成人净价 不含税， 不含Q，含代理费( 进整) ==> 总价 - q值- 税

            // < Tax> 成人税 String 不含Q值
            // < PriceIncludeCommision > 成人总价 String 含税含代理费不含Q值 ===> 总价 - q值
            // < QValue > 成人Q值 String

            fare.getFareTax().SalePrice = Integer.toString(salePrice);
            fare.getFareTax().BasePrice = baseFareStr;
            fare.getFareTax().TotalFare = totalFareStr;
            fare.getFareTax().Tax = Integer.toString(tax);

        } else if (psgType.equalsIgnoreCase(PnrParserConstant.PASSENGER_TYPE_CHD)) {
            // CHDBaseFare> 儿童净价 String 不含税，不含Q，含代理费
            // < CHDTax> 儿童税 String 不含Q值
            // < CHDTotalFare> 儿童总价 String 含税含代理费不含Q值
            // < QCHDValue> 儿童Q值 String

            fare.getFareTax().CHDBaseFare = baseFareStr;
            fare.getFareTax().CHDTotalFare = totalFareStr;
            fare.getFareTax().QCHDValue = Integer.toString(qmountInt10ceil);
            fare.getFareTax().CHDTax = Integer.toString(tax);
        } else if (psgType.equalsIgnoreCase(PnrParserConstant.PASSENGER_TYPE_INF)) {
            // INFBaseFare> 婴儿净价 String 不含税，不含Q，含代理费
            // < INFTax> 婴儿税 String 不含Q值
            // < INFTotalFare> 婴儿总价 String 含税含代理费不含Q值
            // < QINFValue> 婴儿Q值 String

            fare.getFareTax().INFBaseFare = baseFareStr;
            fare.getFareTax().INFTotalFare = totalFareStr;
            fare.getFareTax().QINFValue = Integer.toString(qmountInt10ceil);
            fare.getFareTax().INFTax = Integer.toString(tax);
        }

        // has xt seg?
        // xt段有几行？
        MutableString[] xts = fareSegList.get(PnrParserConstant.QTE_KEY_XT);
        if (null != xts) {
            for (MutableString xt : xts) {
                List<PnrRet.FN.Item> xtItem = parseNFItems(xt.toString());
                items.addAll(xtItem);
            }
        }

        // fareTax.BasePrice = itemA.Value;

        return fn;
    }

    static class FlightRawInfo {
        public String departureDateStr;
        public String departTimeStr;
        public String arrivalTimeStr;
        public String carrierFlightStr;
        public String classStr;
        public String boardOffPointStr;
        public String boardOffPointATStr;
        public String actionCodeSeatStr;

        // <IsCodeShare> 航班共享 String 0为普通航班，1为共享航班
        public String isCodeShare = "0";

        public String realFlightNumberStr;

        public String departYearStr;

        public String statusStr;

        public String sfc;

        public String subCabinCodeStr;
    }

    private MutableDateTime procescFlightRawInfoDepartureDateStr(PnrRet.Flight flt, FlightRawInfo flightRawInfo) {
        flt.Week = flightRawInfo.departureDateStr.substring(0, 2);

        MutableDateTime departDatetime = new MutableDateTime();
        
        int month = PnrDateUtil.MonthStr2Number(flightRawInfo.departureDateStr.substring(4, 7));
        departDatetime.setMonthOfYear(month);
        
        int dayOfMonth = NumberUtils.toInt(flightRawInfo.departureDateStr.substring(2, 4));
        if (flightRawInfo.departYearStr != null) {
        	departDatetime.setYear(Integer.parseInt("20" + flightRawInfo.departYearStr));
        	departDatetime.setDayOfMonth(dayOfMonth);
        } else {
        	try {
            	departDatetime.setDayOfMonth(dayOfMonth);
            	if (departDatetime.isBeforeNow())
            		departDatetime.setYear(DateTime.now().getYear() + 1);
            } catch (IllegalFieldValueException e) {
            	departDatetime.setYear(DateTime.now().getYear() + 1);
            	departDatetime.setDayOfMonth(dayOfMonth);
            }
        }
        
        flt.DepartureDate = DateTimeUtil.date10(departDatetime.toDateTime());
        return departDatetime;
    }

    private void processFlightsOpen(PnrRet.Flight flt, MakeFareByPNR.FlightInfo.FlightSegment fls, FlightRawInfo flightRawInfo, MutableInt idx) {
        String t1 = flightRawInfo.carrierFlightStr;

        flt.Carrier = t1.substring(0, 2);

        flt.Class = flightRawInfo.classStr;

        flt.ActionCode = flightRawInfo.actionCodeSeatStr;

        flt.BoardPoint = flightRawInfo.boardOffPointStr.substring(0, 3);
        flt.OffPoint = flightRawInfo.boardOffPointStr.substring(3, 6);

        fls.CW = flt.Class;
        fls.ActionCode = flightRawInfo.actionCodeSeatStr;
        fls.DestCity = flt.OffPoint;
        // 航班号 = 航空公司+航班编号？
        fls.FlightNumber = flt.Carrier;

        fls.FromCity = flt.BoardPoint;
        fls.number = idx.toString();

        if (StringUtils.isNotBlank(flightRawInfo.departureDateStr)) {
            procescFlightRawInfoDepartureDateStr(flt, flightRawInfo);
        }
    }

    private void processFlightsWithoutDateTime(PnrRet.Flight flt, MakeFareByPNR.FlightInfo.FlightSegment fls, FlightRawInfo flightRawInfo, MutableInt idx) {
        String t1 = flightRawInfo.carrierFlightStr;

        flt.Carrier = t1.substring(0, 2);
        flt.Flight = t1.substring(2);

        flt.Class = flightRawInfo.classStr;

        flt.ActionCode = flightRawInfo.actionCodeSeatStr.substring(0, 2);
        flt.Seats = flightRawInfo.actionCodeSeatStr.substring(2);

        flt.BoardPoint = flightRawInfo.boardOffPointStr.substring(0, 3);
        flt.OffPoint = flightRawInfo.boardOffPointStr.substring(3, 6);

        fls.CW = flt.Class;
        fls.ActionCode = flightRawInfo.actionCodeSeatStr;
        fls.DestCity = flt.OffPoint;
        fls.DestDate = flt.ArriveDate;
        fls.DestTime = flt.ArriveTime;
        // 航班号 = 航空公司+航班编号？
        fls.FlightNumber = flt.Carrier + flt.Flight;

        fls.FromCity = flt.BoardPoint;
        fls.IsCodeShare = flightRawInfo.isCodeShare;

        fls.Status = flightRawInfo.statusStr;

        fls.number = idx.toString();
    }

    // BR771 U SA11MAY SHATSA RR1 1950 2145 SEAME
    private void processFlightsAbstract(PnrRet.Flight flt, MakeFareByPNR.FlightInfo.FlightSegment fls, FlightRawInfo flightRawInfo, MutableInt idx) {
        String t1 = flightRawInfo.carrierFlightStr;

        flt.Carrier = t1.substring(0, 2);
        flt.Flight = t1.substring(2);

        flt.Class = flightRawInfo.classStr;

        MutableDateTime departDatetime = procescFlightRawInfoDepartureDateStr(flt, flightRawInfo);

        if (null == departDatetime) {
            departDatetime = new MutableDateTime();
        }

        // 3. CZ381 B WE04SEP CANBNE HK1 2115 0825+1 E
        String departTimeStr = flightRawInfo.departTimeStr;
        String arrivalTimeStr = flightRawInfo.arrivalTimeStr;

        departDatetime.setHourOfDay(NumberUtils.toInt(departTimeStr.substring(0, 2)));
        departDatetime.setMinuteOfHour(NumberUtils.toInt(departTimeStr.substring(2, 4)));

        // TU22NOV13PVGXIY -> 13 is suffix of year
        if (StringUtils.isNotEmpty(flightRawInfo.departYearStr)) {
            int curYear = (new DateTime()).getYearOfCentury();
            int year = NumberUtils.toInt(flightRawInfo.departYearStr);
            int diff = year - curYear;
            // departDatetime.setMinuteOfHour(NumberUtils.toInt(flightRawInfo.departYearStr));
            departDatetime.setYear((new DateTime()).getYear() + diff);
        }

        flt.DepartureTime = DateTimeUtil.time5(departDatetime.toDateTime());

        MutableDateTime arriveDatetime = departDatetime.copy();
        arriveDatetime.setHourOfDay(NumberUtils.toInt(arrivalTimeStr.substring(0, 2)));
        arriveDatetime.setMinuteOfHour(NumberUtils.toInt(arrivalTimeStr.substring(2, 4)));

        if (arrivalTimeStr.length() > 4) {
            int plusDays = NumberUtils.toInt(arrivalTimeStr.substring(5));
            arriveDatetime.addDays(plusDays);
        }

        flt.ArriveDate = DateTimeUtil.date10(arriveDatetime.toDateTime());
        flt.ArriveTime = DateTimeUtil.time5(arriveDatetime.toDateTime());

        flt.ActionCode = flightRawInfo.actionCodeSeatStr.substring(0, 2);
        flt.Seats = flightRawInfo.actionCodeSeatStr.substring(2);

        flt.BoardPoint = flightRawInfo.boardOffPointStr.substring(0, 3);
        flt.OffPoint = flightRawInfo.boardOffPointStr.substring(3, 6);

        // 去掉了 ShareCarrier 这个字段，
        // 这个字段只在 共享代码的时候， 代表那个共享航班号
        if (PnrParserConstant.IS_CODESHARE.equals(flightRawInfo.isCodeShare) && StringUtils.isNotBlank(flightRawInfo.realFlightNumberStr)) {
            flt.ShareCarrier = flightRawInfo.carrierFlightStr.substring(0, 2);
            flt.ShareFlight = flightRawInfo.carrierFlightStr.substring(2);

            flt.Carrier = flightRawInfo.realFlightNumberStr.substring(0, 2);
            flt.Flight = flightRawInfo.realFlightNumberStr.substring(2);

            flt.IsCodeShare = "true";
        }

        String at = flightRawInfo.boardOffPointATStr;
        if (StringUtils.length(at) == 4 || StringUtils.length(at) == 3) {
            String boardPointAT = at.substring(0, 2).trim();
            String offPointAT = at.substring(2).trim();
            if (!boardPointAT.equals("--") && !boardPointAT.equals("  ")) {
                flt.BoardPointAT = boardPointAT;
            }

            if (!offPointAT.equals("--") && !offPointAT.equals("  ")) {
                flt.OffpointAT = offPointAT;
            }
        }
        String subCabinCode = flightRawInfo.subCabinCodeStr;
        if (StringUtils.length(subCabinCode) == 2) {
            flt.subCabinCode = subCabinCode;
        }

        if (StringUtils.isNotBlank(flightRawInfo.sfc)) {
            flt.SFC = flightRawInfo.sfc;
        }

        fls.CW = flt.Class;
        fls.ActionCode = flightRawInfo.actionCodeSeatStr;
        fls.DestCity = flt.OffPoint;
        fls.DestDate = flt.ArriveDate;
        fls.DestTime = flt.ArriveTime;

        fls.FromCity = flt.BoardPoint;
        fls.FromDate = flt.DepartureDate;
        fls.FromTime = flt.DepartureTime;
        fls.IsCodeShare = flightRawInfo.isCodeShare;

        fls.RealFlightNumber = flightRawInfo.realFlightNumberStr;

        fls.Status = flightRawInfo.statusStr;

        fls.number = idx.toString();

        if (PnrParserConstant.IS_CODESHARE.equals(flightRawInfo.isCodeShare)) {
            fls.FlightNumber = flightRawInfo.carrierFlightStr;
        } else {
            // 航班号 = 航空公司+航班编号？
            fls.FlightNumber = flt.Carrier + flt.Flight;
        }
    }

    private void processFlightsNormal(PnrRet.Flight flt, MakeFareByPNR.FlightInfo.FlightSegment fls, FlightRawInfo flightRawInfo, MutableInt idx) {
        processFlightsAbstract(flt, fls, flightRawInfo, idx);
        flt.Type = "0";
    }

    private void processShareCode(FlightRawInfo flightRawInfo, String[] tks, int startIdx) {

        if (startIdx > tks.length)
            return;

        Pattern p = Pattern.compile("OP-(\\w{2}\\d+)");

        FIND_OP:
        for (int i = startIdx; i < tks.length; i++) {
            Matcher m = p.matcher(tks[i]);
            if (m.find()) {
                flightRawInfo.realFlightNumberStr = m.group(1);
                break FIND_OP;
            }
        }

    }

    /**
     * BR771 U SA11MAY SHATSA RR1 1950 2145 SEAME
     * <p/>
     * <p/>
     * <p/>
     * <Flight ElementNo="2" ID="1" Type="0" FltType="" Carrier="QR"
     * Flight="511" ShareCarrier="" ShareFlight="" BoardPoint="HBE"
     * OffPoint="DOH" Week="TU" DepartureDate="2013-04-16" DepartureTime="18:45"
     * ArriveDate="2013-04-16" ArriveTime="23:00" Class="K1" ActionCode="RR"
     * Seats="1" Meal="" Stops="" Avail="" Night="" ETKT="E" Changed=""
     * LinkLevel="" Allow="20K" BoardPointAT="" OffpointAT="" />
     *
     * @param flightStrList
     * @return
     */
    // @SuppressWarnings("unused")
    private FlightResult processFlights(List<Pair<FlightType, MutableString>> flightStrList) {

        List<PnrRet.Flight> pnrRetFlights = Lists.newArrayList();
        List<MakeFareByPNR.FlightInfo.FlightSegment> flightSegments = Lists.newArrayList();

        flightResult.flightSegments = flightSegments;
        flightResult.pnrRetFlights = pnrRetFlights;

        MutableInt idx = new MutableInt(0);

        NEXT_FLIGHT:
        for (Pair<FlightType, MutableString> fltStr : flightStrList) {

            idx.increment();

            PnrRet.Flight flt = new PnrRet.Flight();
            MakeFareByPNR.FlightInfo.FlightSegment fls = new MakeFareByPNR.FlightInfo.FlightSegment();

            // for BR771 U SA11MAY SHATSANO1 1950 2145 SEAME, add SPACE between SHATSA and NO1
            MutableString fltStrr = new MutableString();
            Matcher matcher = Pattern.compile("(.+? [A-Z] [A-Z]{2}\\d{2}[A-Z]{3} [A-Z]{6})([A-Z]{2}\\d \\d{4} .+)").matcher(fltStr.getValue1());
            if (matcher.find())
            	fltStrr.append(matcher.group(1)).append(" ").append(matcher.group(2));
            else
            	fltStrr = fltStr.getValue1();
            
            String[] tks = SPACE_PATTERN.split(fltStrr);

            // HE4EJH
            // BR771 U SA11MAY SHATSA RR1 1950 2145 SEAME

            // HD3W4F
            // 3. UA088 V TU16JUL PEKEWR*HX2* 1545 1730 SEAME
            // 4. UA089 W WE31JUL EWRPEK*HX2* 1150 1345+1 SEAME
            // tks[3] = PEKEWR*HX2*

            // 2. *MU8984 Y SU01SEP PVGHKG HK1 1150 1435 E T2-- OP-HX237

            int mode = 0;
            final int NORMAL_MODE = 1;
            final int WITHYEAR_MODE = 2;

            FlightRawInfo flightRawInfo = new FlightRawInfo();

            if (fltStr.getValue0() == FlightType.CODE_SHARE_FLY) {
                flightRawInfo.isCodeShare = PnrParserConstant.IS_CODESHARE;
            }

            if (fltStr.getValue0() == FlightType.LAND) {
                // FltType="ARNK" BoardPoint="BNE" OffPoint="MEL"/>
                // 4. ARNK BNEMEL

                if (tks.length == 1) {
                    flt.FltType = tks[0];
                } else if (tks.length > 1) {
                    flt.FltType = tks[0];
                    flt.BoardPoint = tks[1].substring(0, 3);
                    flt.OffPoint = tks[1].substring(3, 6);
                    fls.FromCity = flt.BoardPoint;
                    fls.DestCity = flt.OffPoint;
                } else {
                    throw BizException.instance("invalid ARNK format:" + (fltStr.getValue1()));
                }
                flt.Type = "1";

                pnrRetFlights.add(flt);
                flightSegments.add(fls);

                continue NEXT_FLIGHT;

            } else if (tks[0].length() == 6 && StringUtils.equalsIgnoreCase("OPEN", tks[0].subSequence(2, 6))) {
                // 第二段是OPEN票，要解析的
                // CZOPEN H KIXHRB
                // 航司、舱位、起飞地、目的地、
                // 状态：OPEN

                //MUOPEN Y   TU15APR  HKGPVG   
                int segcnt = tks.length;
                if (tks.length >= 3) {
                    flightRawInfo.carrierFlightStr = tks[0].substring(0, 2);
                    flightRawInfo.actionCodeSeatStr = "OPEN";
                    flightRawInfo.classStr = tks[1];

                    if (tks[segcnt - 2].length() > 2) {
                        flightRawInfo.departureDateStr = tks[segcnt - 2];
                    }

                    flightRawInfo.boardOffPointStr = tks[segcnt - 1];
                }

                flt.Type = "0";

                processShareCode(flightRawInfo, tks, 3);

                processFlightsOpen(flt, fls, flightRawInfo, idx);

                pnrRetFlights.add(flt);
                flightSegments.add(fls);

                continue NEXT_FLIGHT;

            } else {

                if (fltStr.getValue1().indexOf("OPEN") > 0) {
                    throw BizException.instance("special open format:" + (fltStr.getValue1()));
                }

                int departTimeIdx = -1;

                // CA929 K TU07MAY13PVGNRT HK1 1000 1350 E T2T1
                // find idx of the "1000"
                Pattern d4 = Pattern.compile("\\d{4}");
                for (int fidx = 0; fidx < tks.length; fidx++) {
                    if (d4.matcher(tks[fidx]).matches()) {
                        departTimeIdx = fidx;
                        break;
                    }
                }

                if (departTimeIdx == -1) {
                    // 这个是位OK了还没接收下来的，等接收好就可以看到时间了
                    // HDLG2D
                    // 2. TK1882 Q MO13MAY SKGIST HX1 1540 1700 SEAME
                    // 3. TK020 Q TU14MAY ISTPEK HX1 0035 1505 SEAME
                    // 4. TK020 Q1 SA18MAY ISTPEK KK1 E

                    flightRawInfo.carrierFlightStr = tks[0];
                    flightRawInfo.classStr = tks[1];
                    flightRawInfo.actionCodeSeatStr = tks[4];

                    flightRawInfo.statusStr = "SeatOK, Not ready:" + fltStr.getValue1();

                    Pattern actionCodePattern = Pattern.compile("\\w{2}\\d{1}");
                    int actionCodeIdx = -1;
                    for (int fidx = 0; fidx < tks.length; fidx++) {
                        if (actionCodePattern.matcher(tks[fidx]).matches()) {
                            actionCodeIdx = fidx;
                            break;
                        }
                    }
                    if (actionCodeIdx != -1) {

                        flightRawInfo.actionCodeSeatStr = tks[actionCodeIdx];

                        Matcher m = DepartureDateStrPattern1.matcher(fltStr.getValue1());
                        if (m.find()) {
                            String str = m.group(0);
                            flightRawInfo.departureDateStr = str.substring(0, 7);
                            flightRawInfo.departYearStr = str.substring(7, 9);
                            flightRawInfo.boardOffPointStr = str.substring(9);
                        } else {
                            flightRawInfo.boardOffPointStr = tks[actionCodeIdx - 1];
                        }

                        processShareCode(flightRawInfo, tks, actionCodeIdx);

                        processFlightsWithoutDateTime(flt, fls, flightRawInfo, idx);
                    } else {
                        flightRawInfo.boardOffPointStr = "";
                    }

                    pnrRetFlights.add(flt);
                    flightSegments.add(fls);

                    continue NEXT_FLIGHT;

                } else {
                    flightRawInfo.carrierFlightStr = tks[0];
                    flightRawInfo.classStr = tks[1];
                    flightRawInfo.actionCodeSeatStr = tks[departTimeIdx - 1];

                    flightRawInfo.departTimeStr = tks[departTimeIdx];
                    flightRawInfo.arrivalTimeStr = tks[departTimeIdx + 1];

                    processShareCode(flightRawInfo, tks, departTimeIdx);

                    // [MU2409, R, TU25JUN13SHATYN, RR2, 2135, 2345, E, T2--]
                    // S7519 N MO22APR13SVXPEK RR1 1805 0215+1 E
                    if (departTimeIdx == 4) {
                        // 2 reason:
                        // 1 : S7519 N MO22APR13SVXPEK RR1 1805
                        // 2: UA089 W WE31JUL EWRPEK*HX2* 1150

                        // String departTimeYearBoardOffPoint = tks[2];

                        if (Pattern.compile("\\w{6}\\*\\w{2}\\d+\\*").matcher(tks[3]).find()) {
                            // reason 2
                            flightRawInfo.boardOffPointStr = tks[3].substring(0, 6);
                            flightRawInfo.actionCodeSeatStr = tks[3].substring(7, tks[3].length() - 1);
                            flightRawInfo.departureDateStr = tks[2];

                        } else if (Pattern.compile("\\w{6}\\w{2}\\d+").matcher(tks[3]).find()) {
                            // reason 2
                            flightRawInfo.boardOffPointStr = tks[3].substring(0, 6);
                            flightRawInfo.actionCodeSeatStr = tks[3].substring(6, tks[3].length());
                            flightRawInfo.departureDateStr = tks[2];

                        } else if (Pattern.compile("\\w{2}\\d{2}\\w{3}\\d{2}\\w{6}").matcher(tks[2]).find()) {
                            // reason 1 : FR28JUN13PVGXIY
                            flightRawInfo.departureDateStr = tks[2].substring(0, 7);
                            flightRawInfo.departYearStr = tks[2].substring(7, 9);
                            flightRawInfo.boardOffPointStr = tks[2].substring(9);
                        } else {
                            if (StringUtils.isAlpha(StringUtils.right(tks[0], 2))) {
                                flightRawInfo.carrierFlightStr = StringUtils.substring(tks[0], 0, tks[0].length() - 1);
                                flightRawInfo.classStr = StringUtils.right(tks[0], 1);
                                flightRawInfo.boardOffPointStr = tks[2].substring(0, 6);
                                flightRawInfo.departureDateStr = tks[1];
                            } else {
                                throw BizException.instance("unknow flight format:" + fltStr);
                            }
                        }

                    } else if (departTimeIdx == 5) {
                        // BR771 U SA11MAY SHATSA RR1 1950 2145 SEAME

                        flightRawInfo.departureDateStr = tks[2];
                        flightRawInfo.boardOffPointStr = tks[3];

                        // FM9347 E WE03JUL XMNCAN RR1 2200 2310 E

                    } else {
                        throw BizException.instance("unknow flight format:" + fltStr);
                    }

                    // 航班信息解析航站楼解析错误，在此老PNRParser逻辑下无法解决，
                    // 故用PnrParserNew的写法解析航站楼信息。
                    Matcher matcher_terminal = Pattern.compile(
                    		"\\d{4} \\d{4}(?:\\+\\d)? +"
                    		+ "(?:E|[A-Z]+) "
                    		+ "(?<deptTerminal>[A-Z0-9 -]{2})(?<arrTerminal>[A-Z0-9 -]{2}) ?"
                    		+ "(?<subCabin>[A-Z][0-9])?"
                    		).matcher(fltStr.getValue1());
                    if (matcher_terminal.find()) {
                    	flightRawInfo.boardOffPointATStr = matcher_terminal.group("deptTerminal") 
                    			+ matcher_terminal.group("arrTerminal");
            			if (matcher_terminal.group("subCabin") != null)
            				flightRawInfo.subCabinCodeStr = matcher_terminal.group("subCabin");
                    }
/*
                    // 3. MU2409 R TU25JUN SHATYN RR2 2135 2345 E T2--
                    // 4. MU2410 T WE26JUN TYNSHA RR2 2135 2320 E --T2
                    // MU2409 R TU25JUN13SHATYN RR2 2135 2345 E T2--
                    // 暂时只处理了这一种 航站台的情况， 其他的情况， 遇到之后再处理， 情况太多， 没时间详细分析
                    if (tks.length >= departTimeIdx + 4) {
                        String tmp1 = tks[departTimeIdx + 3];
                        if (tmp1.length() == 4 || tmp1.length() == 3) {
                            flightRawInfo.boardOffPointATStr = tmp1;
                        } else if (tmp1.length() == 2) {
                            flightRawInfo.subCabinCodeStr = tmp1;
                        }
                    }

                    if (tks.length >= departTimeIdx + 5) {
                        String str = tks[departTimeIdx + 4];
                        if (!str.startsWith("OP")) {
                            flightRawInfo.sfc = tks[departTimeIdx + 4];
                            flightRawInfo.subCabinCodeStr = flightRawInfo.sfc;
                        }
                    }
*/
                    processFlightsNormal(flt, fls, flightRawInfo, idx);

                    pnrRetFlights.add(flt);
                    flightSegments.add(fls);

                    continue NEXT_FLIGHT;

                }

            }
        }

        return flightResult;
    }

    //	01 Y FARE:CNY1130.00 TAX:CNY50.00 YQ:CNY120.00  TOTAL:1300.00
    public static Pattern PAT_HEAD_PATTERN = Pattern.compile("^(\\d{2}) (.*)");

    // (PAT:A
    // 01 RT/R+RT/T FARE:CNY1240.00 TAX:CNY100.00 YQ:CNY220.00 TOTAL:1560.00
    // SFC:01
    // 02 R+T FARE:CNY1380.00 TAX:CNY100.00 YQ:CNY220.00 TOTAL:1700.00
    // SFC:02

    public ReturnClass<List<PnrRet.PatResult>> parsePAT(String rt) throws BizException {

        ReturnClass<List<PnrRet.PatResult>> ret = new ReturnClass<>();

        List<PnrRet.PatResult> patResultList = Lists.newArrayList();

        if (StringUtils.isBlank(rt)) {
            return ret;
        }

        String[] lines = rt.trim().split("(\r|\n)+");

        NEXT_LINE:
        for (String line : lines) {

            if (StringUtils.isBlank(line)) {
                continue NEXT_LINE;
            }

            // compliant to  this scenario :
            //PAT:A                                                                             01 S FARE:CNY640.00 TAX:CNY50.00 YQ:CNY120.00  TOTAL:810.00                       SFC:01

            String ln = line.trim();
            //replace "PAT:xxx" to ""
            if (ln.startsWith("PAT:") || ln.startsWith(">PAT:")) {
                int idxSpace = ln.indexOf(" ");
                if (idxSpace < 0) {
                    continue NEXT_LINE;
                }
                ln = ln.replace(ln.substring(0, idxSpace), "").trim();
            }

            Matcher m = PAT_HEAD_PATTERN.matcher(ln);

            if (!m.find()) {
                continue NEXT_LINE;
            }

            String flgSegIdx = m.group(1);
            String patLineStr = m.group(2);

            PnrRet.PatResult patResult = new PnrRet.PatResult();

            patResult.patLineStr = patLineStr;
            patResult.sfc = flgSegIdx;

            String[] items = SPACE_PATTERN.split(patLineStr.trim());

            if (items.length < 5) {
                logger.warn("invlid pat format:{}", line);
                continue NEXT_LINE;
            }

            String fareBasis = items[0];

            patResult.fareBasis = fareBasis;

            NEXT_ITEM:
            for (int i = 1; i < items.length; i++) {
                String item = items[i];
                int idx = item.indexOf(":");

                if (idx <= 0) {
                    logger.warn("invalid fare format:{}", items[i]);
                    continue NEXT_ITEM;
                }

                String left = item.substring(0, idx);
                String right = item.substring(idx + 1);

                switch (left) {
                    case "FARE":
                        patResult.fare = right;
                        break;

                    case "TAX":
                        patResult.tax = right;
                        break;

                    case "YQ":
                        patResult.yq = right;
                        break;

                    case "TOTAL":
                        patResult.total = right;
                        break;
                }

            }

            patResultList.add(patResult);
        }
        ret.setObject(patResultList);
        return ret;
    }

    Map<String, Pair<Integer, String[]>> rtnInfoMap = Maps.newHashMap();

    public Pair<Integer, String[]> processRTN(String rtn, MutableInt passengerNumber, boolean isParsePnrContent) {

        if (rtnInfoMap.containsKey(rtn)) {
            return rtnInfoMap.get(rtn);
        } else {

            String[] lines = rtn.trim().replaceAll("\r", "\n").split("\n+");

            int startLineIdx = 0;
            //
            if (lines[0].trim().startsWith("**")) {
                startLineIdx = 1;
            }

            startLineIdx = skipPNRHeadLines(lines);

            List<String> lineArrays = Lists.newArrayList(lines);

            JudgeGroupBean gcBean = new JudgeGroupBean(lineArrays, startLineIdx, this.PNR, isParsePnrContent, IsGroup.yes);

            Triplet<Integer, Integer, JudgeGroupBean> namEndFlightBeginPair = processNameLineJudgeGroup(gcBean);
            lines = namEndFlightBeginPair.getValue2().lineArrays.toArray(new String[0]);

            if (isParsePnrContent) {
                processPnrContentAgentOffice(lines);
            } else {
                processPnrAgentOffice(lines);
            }
            rtnInfoMap.put(rtn, Pair.with(namEndFlightBeginPair.getValue0(), lines));
            return rtnInfoMap.get(rtn);
        }
    }

    private int skipPNRHeadLines(String[] lines) {
        String married = "MARRIED SEGMENT EXIST IN THE PNR";
        int startLineIdx = 0;

        boolean findEndTag = false;

        while (true) {
            if (lines[startLineIdx].trim().endsWith(married) || lines[startLineIdx].trim().startsWith(married)) {
                startLineIdx++;
            }
            if (lines[startLineIdx].trim().equalsIgnoreCase("**ELECTRONIC TICKET PNR**")) {
                startLineIdx++;
                findEndTag = true;
            }

            // skip empty lines
            while (StringUtils.isBlank(lines[startLineIdx])) {
                startLineIdx++;
            }

            if (findEndTag || startLineIdx == lines.length - 1)
                break;

            // jump out when meet first ' 1.name pnr' line
            Matcher m = HEAD_INDEX_PATTERN.matcher(lines[startLineIdx]);
            if (m.find()) {
                break;
            }

            startLineIdx++;
            if (startLineIdx == lines.length) {
                break;
            }

        }

        return startLineIdx;
    }

    /**
     * 如果RT之前需要先判断是否团的票， 则用先调用这个函数
     *
     * @param rtInfoBean
     * @param needGetRTN
     * @return
     * @throws BizException
     * @throws UfisException
     */
    public boolean parseJudgeIfGroupRT(RtInfoBean rtInfoBean, boolean needGetRTN) throws UfisException {
        logger.debug("parseJudgeIfGroupRT:{}", rtInfoBean.toString());

        String rt = rtInfoBean.rt;
        Pair<Boolean, String> ret = Pair.with(false, "");

        FlightResult flightResult = new FlightResult();

        String[] lines = rt.trim().replaceAll("\r", "\n").split("\n+");

        if (lines.length < 2) {
            throw BizException.instance("lines.length<2" + "\n" + rt);
        }

        int startLineIdx = skipPNRHeadLines(lines);

        MutableString nameLine = new MutableString(lines[startLineIdx]);

        MutableInt passengerNumber = new MutableInt(0);

        String rtn = "";
        if (judgeGroup(lines, startLineIdx, passengerNumber) && needGetRTN) {

            if (rtInfoBean.isParsePnrContent) {
                if (StringUtils.isBlank(rtInfoBean.pnr)) {
                    ReturnClass<String> pnrRc = extractPnrFromPnrContent(rt, true);
                    if (pnrRc.isSuccess()) {
                        PNR = pnrRc.getObject();
                        rtInfoBean.pnr = PNR;
                    } else {
                        throw new BizException("fail to extractPnrFromPnrContent");
                    }
                }
                rtn = rt;
            } else {
                // when process group Ticket, we need more session Time
                client.extendSessionExpire(CdxgConstant.BASE_LOCK_KEEP_MILLSEC);

                client.execRt(rtInfoBean.pnr, false);
                rtn = client.execRtn(true);
            }

            rtInfoBean.rtn = rtn.replaceAll("\r", "\n");

            logger.info("BeginRTN>>>:" + PNR + "\n{}\n>>>EndRTN", rtn);

            lines = rtn.trim().replaceAll("\r", "\n").split("\n+");

            Pair<Integer, String[]> retPair = this.processRTN(rtn, passengerNumber, rtInfoBean.isParsePnrContent);

            // maybe the user could submit a original grpPnr output, etc:
            // 0.4XKH/IN NM4 HE4VG4
            // 5. OZ362 V TU04FEB PVGICN HK4 1205 1500 CLAME 
            // 6. OZ202 V TU04FEB ICNLAX HK4 1630 1010 CLAME		 
            // then we need to manually do rtn
            if (passengers.Passengers.size() == 0 && rtInfoBean.isParsePnrContent) {
                client.execRt(rtInfoBean.pnr, false);
                rtn = client.execRtn(true);
                retPair = this.processRTN(rtn, passengerNumber, rtInfoBean.isParsePnrContent);
            }

            // make makeFareByPnrPassenger
            if (makeFareByPnrPassenger.size() != passengers.Passengers.size()) {
                for (Passenger psg : passengers.Passengers) {
                    String nameField = psg.Name;
                    if (StringUtils.isNotEmpty(psg.PsgStyle)) {
                        nameField = psg.Name;
                    }
                    makeFareByPnrPassenger.add(nameField);
                }
            }

            int nameEndLineExclude = retPair.getValue0();
            lines = retPair.getValue1();

            int tmpfltIdx = nameEndLineExclude;
            while (tmpfltIdx < lines.length) {
                MutableString line = skipHeadIdxCombineNextline(lines, tmpfltIdx);
                lines[tmpfltIdx] = line.toString();
                tmpfltIdx++;
            }

            for (int lineIdx = nameEndLineExclude; lineIdx < lines.length; lineIdx++) {
                MutableString line = new MutableString(lines[lineIdx]);

                if (line.startsWith("SSR")) {
                    processSSR(line);
                }

                if (line.startsWith("OSI")) {
                    processOSI(line);
                }

                if (line.startsWith("RMK")) {
                    processRMK(line);
                }

                if (hasInfant && line.startsWith("XN/IN/")) {
                    processINF(line);
                }
            }

            parseStatus.ssrParsed = true;
            parseStatus.osiParsed = true;
            parseStatus.rmkParsed = true;
            parseStatus.xninParsed = true;

            return true;
        }
        return false;
    }

    static public class RtInfoBean {
        public boolean isDomestic() {
            return isDomestic;
        }

        public void setDomestic(boolean isDomestic) {
            this.isDomestic = isDomestic;
        }

        public PnrRet pnrRet;
        public EtermUfisClient etermWebClient;
        public boolean isGroup;
        public String rt;
        public String rtn;
        public String pnr;
        public boolean isParsePnrContent = false;
        public boolean isDomestic;
    }

    private void stripStartIdx(String[] lines, int startIdx) {
        int tmpfltIdx = startIdx;
        while (tmpfltIdx < lines.length) {
            MutableString line = skipHeadIdxCombineNextline(lines, tmpfltIdx);
            lines[tmpfltIdx] = line.toString();
            tmpfltIdx++;
        }
    }

    private String removeSuffix(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }

        str = str.trim();
        if (str.endsWith("-") || str.endsWith("+")) {
            str = str.substring(0, str.length() - 1).trim();
        }
        return str;
    }

    private MutableString removeSuffix(MutableString str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }

        str = str.trim();
        if (str.endsWith("-") || str.endsWith("+")) {
            str = str.substring(0, str.length() - 1).trim();
        }
        return str;
    }

    public FlightResult parseFlight(RtInfoBean rtInfoBean, RtInterceptorBean rtInterceptorBean) throws BizException {
        FlightResult flightResult = new FlightResult();

        if (StringUtils.isBlank(PNR)) {
            ReturnClass<String> pnrRc = extractPnrFromPnrContent(rtInfoBean.rt, rtInfoBean.isGroup);
            if (pnrRc.isSuccess()) {
                PNR = pnrRc.getObject();
            } else {
                throw new BizException("fail to extractPnrFromPnrContent");
            }
        }
        rtInfoBean.pnr = PNR;

        String[] lines = rtInfoBean.rt.split("(\r|\n)+");

        int startLineIdx = skipPNRHeadLines(lines);

        for (RtBeforeParserHandler handler : rtInterceptorBean.rtParserBeforeHandlerList) {
            handler.handle(lines, startLineIdx);
        }

        MutableString nameLine = new MutableString(lines[startLineIdx]);

        List<String> lineArrays = Lists.newArrayList(lines);

        JudgeGroupBean gcBean = new JudgeGroupBean(lineArrays, startLineIdx, rtInfoBean.pnr, rtInfoBean.isParsePnrContent, IsGroup.unsure);

        Triplet<Integer, Integer, JudgeGroupBean> namEndFlightBeginPair = processNameLineJudgeGroup(gcBean);
        lines = namEndFlightBeginPair.getValue2().lineArrays.toArray(new String[0]);

        int nameEndLineExclude = namEndFlightBeginPair.getValue0();
        int flightBeginLineIdx = namEndFlightBeginPair.getValue1();

        // no flight seg
        if (flightBeginLineIdx == -1) {
            flightResult = new FlightResult();
            return flightResult;
        }

        List<Pair<FlightType, MutableString>> flights = Lists.newArrayList();

        int flightLineIdx = flightBeginLineIdx;

        int tmpfltIdx = flightLineIdx;
        while (tmpfltIdx < lines.length) {
            MutableString line = skipHeadIdxCombineNextline(lines, tmpfltIdx);
            lines[tmpfltIdx] = line.toString();
            tmpfltIdx++;
        }

        for (; flightLineIdx < lines.length; flightLineIdx++) {
            MutableString line = new MutableString(lines[flightLineIdx]);

            if (line.toString().trim().startsWith("ARNK")) {
                flights.add(Pair.with(FlightType.LAND, line));
                continue;
            }

            if (line.startsWith("  ") || (rtInfoBean.isParsePnrContent && line.startsWith(" "))) {

                line = line.trimLeft();

                flights.add(Pair.with(FlightType.FLY, line));
            } else if (line.startsWith(" *") || (rtInfoBean.isParsePnrContent && line.startsWith(" *"))) {

                line = line.trimLeft();

                flights.add(Pair.with(FlightType.CODE_SHARE_FLY, line.substring(1)));
            } else {
                break;
            }
        }

        for (Pair<FlightType, MutableString> flight : flights) {
            String flightNumber = flight.getValue1().trimLeft().toString();

            String head4 = flightNumber.substring(0, 4);

            if (!"ARNK".equalsIgnoreCase(head4)) {
                String carrier2charCode = flight.getValue1().trimLeft().substring(0, 2).toString();
                carrierSet.add(carrier2charCode);
            }
        }

        flightResult = processFlights(flights);

        while (flightLineIdx < lines.length) {

            MutableString line = new MutableString(lines[flightLineIdx]);

            if (line.startsWith("OSI")) {
                processOSI(line);
            }

            if (line.startsWith("SSR")) {
                processSSR(line);
            }

            if (line.startsWith("RMK")) {
                processRMK(line);
            }

            if (line.startsWith("XN/IN/")) {
                processINF(line);
            }

            flightLineIdx++;
        }

        return flightResult;
    }

    /**
     * alse replace "^M" in PnrContent
     *
     * @param lineArrays
     * @return
     */
    public List<String> processLineLongerThan80andTwoDigit(List<String> lineArrays) {

        List<String> res = Lists.newArrayList();

        /*
         * 针对乘客信息和航班信息出现在同一行的情况：
         *  1.黄文 2.江雪梅 3.刘莹 4.王博 5.韦延芬 6.张青 KN76W2    7.  CZ3768 E   MO01JUN  CTUZUH RR6   1250 1635 
         * 切割该行
         * 共享航班前面带星号
         */
        Pattern pattern = Pattern.compile("(.+? [A-Z0-9]{6}) +(\\d+\\. [ \\*][A-Z0-9]{2}\\d+ .+)");
        for (String ln : lineArrays) {
            if (ln.length() > PnrParserConstant.LINE_WIDTH) {
                ln = ln.replace("^M", "");
                Matcher matcher = pattern.matcher(ln);
                if (matcher.find()) {
                    res.add(matcher.group(1));
                    res.add(matcher.group(2));
                } else {
                    res.add(ln);
                }
            } else {
                res.add(ln);
            }
        }
        /*
        for (String ln : lineArrays) {
            ln = ln.replace("^M", "");
            int cutoffset = EtermUfisClient.foundAscend2digitPointPattern(ln);
            if (PnrParserConstant.LINE_WIDTH + 1 == cutoffset || PnrParserConstant.LINE_WIDTH == cutoffset) {
                res.add(ln.substring(0, cutoffset - 1));
                res.add(ln.substring(cutoffset - 1));
            } else {
                res.add(ln);
            }
        }
        */

        return res;
    }

    private void processPnrAgentOffice(String[] lines) {
        FIND_OFFICE:
        for (int lastLineIdx = lines.length - 1; lastLineIdx > 0; lastLineIdx--) {
            if (StringUtils.isNotEmpty(lines[lastLineIdx])) {
                int idx = lines[lastLineIdx].indexOf(".");
                if (idx > 0) {
                    fare.AgentOffice = lines[lastLineIdx].substring(idx + 1);
                    break FIND_OFFICE;
                }
            }
        }
    }

    //	rt JTMFZ5 
    //				MARRIED SEGMENT EXIST IN THE PNR 
    //				....
    //				10.SHA697
    //				qte:/lh 
    //				FSI/LH 
    //				S LH 729W11JAN PVG1350 1905FRA0X 388 
    private void processPnrContentAgentOffice(String[] lines) {

        FIND_OFFICE:
        for (int idx = 0; idx < lines.length; idx++) {
            if (StringUtils.isNotEmpty(lines[idx])) {
                int ix = lines[idx].indexOf(".");
                if (ix > 0) {
                    String content = StringUtils.trim(lines[idx].substring(ix + 1));
                    content = removeSuffix(content);
                    final int AgentOfficeLength = 6;
                    if (content.length() == AgentOfficeLength && StringUtils.isAlpha(StringUtils.left(content, 3))
                            && StringUtils.isNumeric(StringUtils.right(content, 3))) {
                        fare.AgentOffice = content;
                        break FIND_OFFICE;
                    }
                }
            }
        }
    }

    /**
     * 解析RT的结果
     *
     * @param rtInfoBean
     * @param rtInterceptorBean
     * @return
     * @throws BizException
     */
    public FlightResult parseRT(RtInfoBean rtInfoBean, RtInterceptorBean rtInterceptorBean) throws BizException {
        String rt = rtInfoBean.rt;

        IsGroup isGrp = IsGroup.unsure;

        if (rtInfoBean.isGroup) {
            rt = rtInfoBean.rtn;
            isGrp = IsGroup.yes;
        }

        if (StringUtils.isBlank(PNR)) {
            ReturnClass<String> pnrRc = extractPnrFromPnrContent(rt, rtInfoBean.isGroup);
            if (pnrRc.isSuccess()) {
                PNR = pnrRc.getObject();
            } else {
                throw new BizException("fail to extractPnrFromPnrContent");
            }
        }

        rtInfoBean.pnr = PNR;

        FlightResult flightResult = new FlightResult();

        String[] lines = rt.trim().replaceAll("\r", "\n").split("\n+");

        if (lines.length < 2) {
            throw BizException.instance("lines.length<2 " + Arrays.deepToString(lines));
        }

        if (!StringUtils.equalsIgnoreCase(fare.IsGROUP, "true")) {
            if (!rtInfoBean.isParsePnrContent) {
                processPnrAgentOffice(lines);
            } else {
                processPnrContentAgentOffice(lines);
            }
        }

        if (lines[0].trim().equalsIgnoreCase("*THIS PNR WAS ENTIRELY CANCELLED*")) {
            this.pnrStatus = PNRStatus.ENTIRELY_CANCELLED;
            return null;

        }

        int startLineIdx = skipPNRHeadLines(lines);

        // before-parser-interceptor-point
        for (RtBeforeParserHandler handler : rtInterceptorBean.rtParserBeforeHandlerList) {
            handler.handle(lines, startLineIdx);
        }

        MutableString nameLine = new MutableString(lines[startLineIdx]);

        List<String> lineArrays = Lists.newArrayList(lines);

        lineArrays = processLineLongerThan80andTwoDigit(lineArrays);

        JudgeGroupBean gcBean = new JudgeGroupBean(lineArrays, startLineIdx, rtInfoBean.pnr, rtInfoBean.isParsePnrContent, isGrp);

        Triplet<Integer, Integer, JudgeGroupBean> namEndFlightBeginPair = processNameLineJudgeGroup(gcBean);

        lines = namEndFlightBeginPair.getValue2().lineArrays.toArray(new String[0]);

        int nameEndLineExclude = namEndFlightBeginPair.getValue0();
        int flightBeginLineIdx = namEndFlightBeginPair.getValue1();

        // on international tkt need to process 'makeFareByPnrPassenger'
        if (parseConfBean.needMakeFareByPNR) {

            if (makeFareByPnrPassenger.size() != passengers.Passengers.size()) {

                for (Passenger psg : passengers.Passengers) {

                    String nameField = psg.Name;
                    // if (StringUtils.isNotEmpty(psg.PsgStyle)) {
                    // nameField = psg.Name + " " + psg.PsgStyle;
                    // }
                    makeFareByPnrPassenger.add(nameField);
                }
            }

            fare.getFareTax().Passenger = StringUtils.join(makeFareByPnrPassenger, ";");
        }

        // no flight seg
        if (flightBeginLineIdx == -1) {
            flightResult = new FlightResult();
            return flightResult;
        }

        List<Pair<FlightType, MutableString>> flights = Lists.newArrayList();

        int flightLineIdx = flightBeginLineIdx;

        // ofter the following process, the 'head idx' will be removed
        // If the full line (with head idx) is needed
        // we should clone the 'lines' first
        int tmpfltIdx = flightLineIdx;
        while (tmpfltIdx < lines.length) {
            MutableString line = skipHeadIdxCombineNextline(lines, tmpfltIdx);
            lines[tmpfltIdx] = line.toString();
            tmpfltIdx++;
        }

        for (; flightLineIdx < lines.length; flightLineIdx++) {
            MutableString line = new MutableString(lines[flightLineIdx]);
            // skipHeadIdxCombineNextline(lines, flightLineIdx);
            // 4 space ,
            // 4. ARNK BNEMEL
            // do nothing
            // 5. ARNK HKTBKK
            if (line.toString().trim().startsWith("ARNK")) {
                flights.add(Pair.with(FlightType.LAND, line));
                continue;
            }

            // rt HYFMD6
            // **ELECTRONIC TICKET PNR**
            // 1.CHEN/MEIXUAN 2.ZUO/WEI HYFMD6
            // 3. MU5654 Y TU30JUL HRBPVG RR2 1150 1435 E --T1
            // 4. FM831 Z TU30JUL PVGHKT HK2 2115 0105+1 E T1--
            // 5. ARNK HKTBKK
            // 6. *MU9854 Z TU06AUG BKKPVG HK2 0120 0630 E --T1 OP-FM854
            // 7. MU5653 Y TU06AUG PVGHRB HK2 0810 1055 E T1--
            // 8.HAK/T HAK/T 0898-66192346/HAI NAN SHEN LU AIR AGENCY
            // CO.,LTD/ZENG PING
            // ABCDEFG
            // 9.*
            // 10.T
            // 11.SSR ADTK 1E BY HAK03MAY13/1137 OR CXL MU5654 Y30JUL +

            final int MIN_FLT_LENGTH = 4;
            String linetmp = line.toString();
            if (StringUtils.trim(linetmp).startsWith("*")) {
                String fltline = StringUtils.trimToEmpty(line.trimLeft().substring(1).toString());
                if (fltline.length() > MIN_FLT_LENGTH) {
                    flights.add(Pair.with(FlightType.CODE_SHARE_FLY, line.trimLeft().substring(1)));
                }
            } else if (linetmp.startsWith("  ") || (rtInfoBean.isParsePnrContent && line.startsWith(" "))) {
                flights.add(Pair.with(FlightType.CODE_SHARE_FLY, line.substring(1)));
            } else {
                break;
            }
        }

        for (Pair<FlightType, MutableString> flight : flights) {
            String flightNumber = flight.getValue1().trimLeft().toString();

            String head4 = flightNumber.substring(0, 4);

            if (!"ARNK".equalsIgnoreCase(head4)) {
                String carrier2charCode = flight.getValue1().trimLeft().substring(0, 2).toString();
                carrierSet.add(carrier2charCode);
            }
        }

        // processFlight before process other OSI, SSR RMK
        flightResult = processFlights(flights);

        while (flightLineIdx < lines.length) {
            MutableString line = new MutableString(lines[flightLineIdx]);
            // these line will not span over two lines
            // OSI YY 1INF XIE/HUI/P1
            if (line.startsWith("OSI")) {
                processOSI(line);
            }
            if (line.startsWith("SSR")) {
                processSSR(line);
            }
            if (line.startsWith("RMK")) {
                processRMK(line);
            }
            if (line.indexOf("XN/IN/") >= 0) {
                processINF(line);
            }
            flightLineIdx++;
        }

        parseStatus.ssrParsed = true;
        parseStatus.osiParsed = true;
        parseStatus.rmkParsed = true;
        parseStatus.xninParsed = true;

        if (ticketIssueTimeLimitList.size() > 0) {
            DateTime earliest = ticketIssueTimeLimitList.get(0);
            int earliestIdx = 0;
            DateTime currentTime = new DateTime();//当前时间

            for (int i = 1; i < ticketIssueTimeLimitList.size(); i++) {
                DateTime tmpt = ticketIssueTimeLimitList.get(i);
                //并且大于当前时间
                if (tmpt.isBefore(earliest) && tmpt.isAfter(currentTime)) {
                    earliest = tmpt;
                    earliestIdx = i;
                }
            }

            fare.Ticket_Limit = PnrDateUtil.getTimeLimitStr(earliest);
            if (fare.debugInfo.timeLimitProceedStrsList.timeLimitProceedStrs.size() > earliestIdx) {
                fare.Ticket_Limit_Str = fare.debugInfo.timeLimitProceedStrsList.timeLimitProceedStrs.get(earliestIdx);
            }

            pnrRet.TicketLimit = fare.Ticket_Limit;
            pnrRet.TicketLimitStr = fare.Ticket_Limit_Str;

            // remove error debug info
            if (this.parseConfBean.debugLevel == 0) {
                fare.debugInfo.TimeLimitError = "";
            }
        } else {
            // do some error process
        }

        // FN will span two lines
        END_PROCESS:
        for (String line : lines) {
            if (line.startsWith("FN/")) {
                processFN(line);
                // break END_PROCESS;
            }
        }

        // FN will span two lines
        END_PROCESS:
        for (String line : lines) {
            if (line.startsWith("FC/") || line.startsWith("FP/")) {
                processFP_FC(line);
            }
        }

        // after-parser-interceptor-point
        {
            // this two lines can't be removed, because the 'pnrRet' will be
            // used in 'rtParserAfterHandlerList'
            pnrRet.PassengerInfo = passengers;
            pnrRet.Flights = flightResult.pnrRetFlights;

            if (!rtInfoBean.isGroup) {
                for (RtAfterParserHandler handler : rtInterceptorBean.rtParserAfterHandlerList) {
                    handler.handle(rtInfoBean, lines, startLineIdx, pnrRet);
                }

            } else {
                lines = rtInfoBean.rtn.split("\n+");
                stripStartIdx(lines, 1);
                for (RtAfterParserHandler handler : rtInterceptorBean.rtParserAfterHandlerList) {
                    handler.handle(rtInfoBean, lines, startLineIdx, pnrRet);
                }
            }
        }

        pnrRet.AgentOffice = fare.AgentOffice;

        return flightResult;
    }

    private String getDistance(String rtStr) {
        if (null == rtStr) {
            return null;
        }
        String[] lines = rtStr.replaceAll("\r", "\n").split("\n+");

        int index = 0;
        for (index = 0; index < lines.length - 2; index++) {
            if (lines[index].trim().startsWith(description)) {
                break;
            }
        }

        index = index + 2;
        if (index < lines.length) {
            String[] strs = lines[index].trim().split("\\s+");
            if (null != strs && strs.length > 1 && strs[0].trim().length() == 3) {
                String tpm = strs[1].trim();
                if (tpm.matches("\\d+")) {
                    return tpm;
                } else if (tpm.matches("\\*\\d+")) {
                    return tpm.substring(1);
                }
            }
        }
        return null;
    }

    public ParseConfBean getParseConfBean() {
        return parseConfBean;
    }

    public void setParseConfBean(ParseConfBean parseConfBean) {
        this.parseConfBean = parseConfBean;
    }
}