package com.travelzen.etermface.service.segprice;

import it.unimi.dsi.lang.MutableString;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jodd.util.StringUtil;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.travelzen.etermface.service.PNRParser;
import com.travelzen.etermface.service.constant.PnrParserConstant;
import com.travelzen.etermface.service.entity.SegPrice;
import com.travelzen.etermface.service.entity.SegPriceSyntaxTree;
import com.travelzen.framework.core.util.TZUtil;

public class SegPriceParser {

    private static Logger logger = LoggerFactory.getLogger(SegPriceParser.class);

    final static String SVALUE = "S";
    final static String ONE_S = "1S";

    final static String NUC = "NUC";
    final static String END = "END";

    final static String E_TYPE = "E/";
    final static String X_TYPE = "X/";
    final static int PAR_TYPE_LENGTH = 1;

    static public Map<String, String> currencyMap;

    static public Pattern END_PATTETN = Pattern.compile("END");

    static private Pattern CURRENCY_PATTETN;
    static private Pattern CURRENCY_PATTETN_LINE_START;

    static public Pattern CARRIER_PATTETN;

    static {
        List<String> currencys = Lists.newArrayList(getCurrencyCodeMap().keySet());

        String curPatternStr = StringUtils.join(currencys, "|");

        CURRENCY_PATTETN = Pattern.compile(curPatternStr);
        CURRENCY_PATTETN_LINE_START = Pattern.compile("^" + "(" + curPatternStr + ")");

        List<String> carriers = Lists.newArrayList(PNRParser.getCarrierCodeMap().keySet());
        carriers.add(0, NUC);
        String carriersPatternStr = StringUtils.join(carriers, "|");

        CARRIER_PATTETN = Pattern.compile(carriersPatternStr);

    }

    public synchronized static Map<String, String> getCurrencyCodeMap() {

        if (currencyMap == null) {

            currencyMap = Maps.newHashMap();

            ClassPathResource resource = new ClassPathResource("currency/currency.txt");

            try {
                Preconditions.checkNotNull(resource.getInputStream(), "currency-code file not existing");

                BufferedReader dr = new BufferedReader(new InputStreamReader(resource.getInputStream()));

                String currencyLine = "";
                while ((currencyLine = dr.readLine()) != null) {

                    if (currencyLine.trim().startsWith("//")) {
                        continue;
                    }

                    currencyLine = currencyLine.trim();
                    int idx = StringUtils.indexOf(currencyLine, '-');
                    String currency = StringUtils.substring(currencyLine, idx + 1).trim();
                    currencyMap.put(currency, StringUtils.substring(currency, 0, idx));
                }
            } catch (IOException e) {
                logger.error(TZUtil.stringifyException(e));
            }

            currencyMap.put(NUC, NUC);
        }

        return currencyMap;
    }

    // BJS YY X/TYO YY OSA25M892.25NUC892.25END 这个
    // 3 行程的票价，使用的是BJS到OSA的票价的1.25倍。原因是 BJS-X/TYO-OSA 的 TPM 和比 JBS-OSA 的 MPM 多，
    // {TPM(BJS-TYO)+TPM(TYO-OSA)}/MPM(BJS-OSA) 的值小于1.25000，
    // 并且大于1.20000。根据里程制计算的原则，高于1.20000的就使用25M。
    /**
     * @param args
     */
    public static void main(String[] args) {

        String nuc = "";

        // 2. MH381 L WE29JAN CANKUL RR1 0100 0505 E
        // 3. MH141 L WE29JAN KULSYD RR1 0900 2015 E
        // 4. ARNK SYDMEL
        // 5. MH128 V MO10FEB MELKUL HK1 0045 0605 E
        // 6. MH376 V MO10FEB KULCAN HK1 0930 1335 E

        // nuc =
        // "24NOV13SHA MU HKG130.71NUC130.71END ROE6.120290ENDOS *NON-END/RER/RFD AFT DEPT";

        // more than 1 Q seg
        // nuc =
        // "22MAY14HKG KE SEL Q4.25Q29.52 487.40KE OSA Q25.00 253.87KE SEL Q25.00 253.87NUC1078.91END ROE7.755300";

        // nuc =
        // "23DEC13NGB MU HKG65.35MU NGB Q4.25 65.35NUC134.95END ROE6.120290";

        // nuc =
        // "25NOV13SHA MU X/PAR AF FRA M800.61MU SHA800.61NUC1601.22END  ROE6.120290";

        // nuc = "25NOV13SHA MU TYO563.69MU SHA563.69NUC1127.38END ROE6.120290";

        // nuc = "01NOV13BJS SU X/MOW SU FRA343.12NUC343.12END ROE6.120290 ";

        // nuc =
        // "08DEC13SHA UA NYC7725.12/-CHI UA SHA7608.29NUC15333.41END ROE6.120290";

        // nuc =
        // "03NOV13SHA EK X/DXB EK FRA Q SHAFRA85.00 642.12EK X/DXB EK SHA Q FRASHA85.00 715.65NUC1527.77END ROE6.120290    ";

        // nuc =
        // "30JAN14SHA CZ X/CAN CZ REP401.94CZ X/CAN CZ SHA401.94NUC803.88END ROE6.120290                                          ";

        // nuc =
        // "03NOV13BJS TK X/IST TK GVA1780.96TK X/IST TK BJS318.61NUC2099.57END ROE6.120290                                            ";

        // nuc =
        // "01NOV13SHA FM OSA229.56/-SPK MU SHA392.13NUC621.69END ROE6.120290";
        // nuc =
        // "29JAN14CAN MH X/KUL MH SYD593.92/-MEL MH X/KUL MH CAN536.73NUC1130.65END ROE6.120290 ";

        // nuc = "07DEC13SHA BR TPE M/IT BR SHA M/IT END ROE6.120290 ";

        // when UA SFO and (no fare seg and met carrier again, just put the seg
        // on curCodeList)
        // nuc =
        // "14DEC13SHA UA X/CHI UA CMH725.45/-LAX UA SFO UA SHA1077.56NUC1803.01END ROE6.120290  ";

        // nuc =
        // "30OCT13SHA GA X/JKT GA X/PKU402.75GA X/JKT GA SHA402.75NUC805.50END ROE6.120290";
        //
        nuc = "29JAN14CAN MH X/KUL610.26MH SYD//MEL M1367.37MH X/KUL MH CAN                    M1912.89NUC3890.52END ROE6.120290";

        // nuc =
        // "04NOV13SHA CA X/FRA LH STR1552.21/-LON CA SHA3431.20NUC4983.41END ROE6.120290  ";

        // nuc =
        // "25NOV13BJS UA X/CHI285.93UA CHS247.90UA X/WAS UA CHI352.56UA  BJS490.17NUC1376.56END ROE6.120290   ";

        // nuc =
        // "30OCT13SHA GA X/JKT GA X/PKU402.75GA X/JKT GA SHA402.75NUC805.50END ROE6.120290     ";

        // nuc =
        // "13FEB14SHA MU X/NYC AA LAS25M4006.13MU X/NYC MU SHA377.43NUC 4383.56END ROE6.120290    ";

        // nuc =
        // "05DEC13KHN MU X/SHA MU NGO M229.56/-OSA MU X/SHA MU KHN M229.56NUC459.12END ROE6.120290";
        // nuc ="20DEC13KMG MU SHA310.44MU HKG104.57NUC415.01END ROE6.120290  ";

        // nuc
        // ="02FEB14SHA SQ SIN M/IT SQ SHA M/IT Q SHASHA196.06END ROE6.12                    \n0290      ";

        // nuc =
        // "31OCT13SHA CX X/HKG CX PAR Q4.25M/IT CX X/HKG KA SHA Q4.25M/IT END ROE6.120290";

        // nuc =
        // "01FEB14CKG QR X/DOH QR DXB374.16/-AUH QR X/DOH QR CKG487.72N  UC861.88END ROE6.120290";
        // nuc =
        // "01FEB14CKG QR X/DOH QR DXB374.16/-AUH QR X/DOH QR CKG487.72NUC861.88END ROE6.120290";

        // nuc =
        // "12JUN14WNZ KA X/HKG CX BKK Q4.25M/IT CX X/HKG KA WNZ Q4.25M/     IT END ROE6.120290";

        // nuc = "29DEC13VCE AZ ROM119.00EUR119.00END";
        // nuc = "04JAN14GAY AI DEL4820INR4820END";

        // nuc ="12JUN14WNZ  KA WNZ Q4.25M/     IT END ROE6.120290";

        // 31JAN14TAO KE X/SEL Q57.55 131.54KE HNL Q154.00 449.48KE X/S
        // EL Q154.00 449.48KE TAO Q57.55 131.54NUC1585.14END ROE6.0815
        // 90
        // String[] nucs = new String[] {
        // "31JAN14TAO KE X/SEL Q57.55 131.54KE HNL Q154.00 449.48KE X/S ",
        // "EL Q154.00 449.48KE TAO Q57.55 131.54NUC1585.14END ROE6.0815", "90 "
        // };

        SegPriceSyntaxTree SegPriceSyntaxTree = SegPriceParser.segmentsPriceParser("19JUN15SHA FM BKK129.69MU SHA129.69NUC259.38END ROE6.168420");
        System.out.print(SegPriceSyntaxTree);
        // segmentsPriceParser(nuc);
        // segmentsPriceParser(nucs);
        // TODO Auto-generated method stub

    }

    static MutableString skipHeadSpace(MutableString str) {

        if (str.startsWith(" ")) {
            int spaceIdx = 1;
            FIND_NONE_CHAR: while (true) {
                if (Character.isWhitespace(str.charAt(spaceIdx))) {
                    spaceIdx++;
                }
                break FIND_NONE_CHAR;
            }
            return str.substring(spaceIdx);
        } else {
            return str;
        }
    }

    // two type :
    // X/PAR AF FRA M800.61MU
    // X/KUL610.26
    // X/JKT GA X/PKU402.75
    final static String TRANSFER_TAG = "X/";

    final static Pattern digitPattern = Pattern.compile("\\d");
    final static Pattern doubleSlashPattern = Pattern.compile("//");

    static class CombineCityBean {
        String cityCode = "";
        String partialCityCode = "";
        boolean needCombineCityCode = false;
    }

    private void tryCombineCity(String tknStr, SegpriceLexer lexer, SegPriceSyntaxTree synTree, CombineCityBean combineCityBean) {

        combineCityBean.cityCode = tknStr;

        // '''31JAN14CAN QR X/DOH QR DXB Q CANDXB50.97 429.98QR X/DOH QR C
        // AN517.13NUC998.08END ROE6.081590 '''
        // begin to process a very special case :
        // city code is separate , and the city code is combine with a number,
        // such as "QR C  		AN517.13"
        // we will judge and combine this case in the parsing phrase instead of
        // the preprocessing phrase
        // because we have more infomation in the parsing phrase

        if (combineCityBean.cityCode.length() < PnrParserConstant.CITY_CODE_LENGTH) {
            int partialCityCodeOffset = PnrParserConstant.CITY_CODE_LENGTH - combineCityBean.cityCode.length();
            // C AN
            combineCityBean.partialCityCode = lexer.getRestStr().trim().substring(0, partialCityCodeOffset);

            String charAtferPartialCityCode = lexer.getRestStr().trim().substring(partialCityCodeOffset, partialCityCodeOffset + 1);

            // C AN1234

            // CAN
            String maybe3CityCode = combineCityBean.cityCode + combineCityBean.partialCityCode;

            if (isCity3Code(maybe3CityCode) && !StringUtils.isAlpha(charAtferPartialCityCode)) {

                // tkn.tkImg = new MutableString(combineCityBean.cityCode +
                // lexer.getRestStr().trim());

                combineCityBean.cityCode = maybe3CityCode;
                combineCityBean.needCombineCityCode = true;

            }
        }
    }

    ProcResult procCity(SegpriceLexer lexer, SegPriceSyntaxTree synTree) {

        SPTokenType tk = lexer.peekTokenType();

        if (tk != null && tk == SPTokenType.E) {
            lexer.nextEToken();
        }

        SPToken tkn = lexer.nextCityToken();

        if (tkn.tkImg == null || tkn.tkImg.trim().length() == 0) {
            return new ProcResult(false);
        }

        CombineCityBean combineCityBean = new CombineCityBean();
        if (!isCity3Code(tkn.getTkImgStr())) {
            tryCombineCity(tkn.getTkImgStr(), lexer, synTree, combineCityBean);
        } else {
            combineCityBean.cityCode = tkn.getTkImgStr();
        }

        synTree.curCityCodeList.add(combineCityBean.cityCode);

        if (StringUtils.isAlpha(combineCityBean.cityCode)) {

            // scenarios 1:
            // CA N//MEL
            // lexer.skipToken(1)

            // scenarios 2:
            // CAN//M EL 123 scenarios 21
            // CAN//M EL M123

            // CAN//M EL123 scenarios22

            // lexer.skipToken(1)

            // scenarios 3:
            // CAN//MEL
            // no nothing

            // scenarios 4:
            // PAR345.30/-BRU
            // ==> pushback(1) -> nextCityToken

            // scenarios 5:
            // PA R345.30/-BRU

            // scenarios 6:
            // PA R M345.30/-BRU

            // scene 7,8
            // PAR600 , PAR 600

            final String DOUBLE_SLASH = "//";
            // MH SYD//MEL M1367.37MH
            // QR C AN//MEL 517.13NUC998.08

            SegpriceLexer arnkLexer = lexer;

            if (combineCityBean.needCombineCityCode) {
                int partialCityCodeEndIdx = lexer.getRestStr().indexOf(combineCityBean.partialCityCode) + combineCityBean.partialCityCode.length();
                String maybedoubleSlashCityCode = lexer.getRestStr().substring(partialCityCodeEndIdx);

                if (maybedoubleSlashCityCode.startsWith(DOUBLE_SLASH)) {
                    // sc1
                    arnkLexer = new SegpriceLexer(StringUtils.trim(arnkLexer.getRestStr().substring(partialCityCodeEndIdx)));
                    arnkLexer.moveAhead(PnrParserConstant.DOUBLE_SLASH_LENGTH);
                    SPToken arnkTk = arnkLexer.nextToken();
                    procARNK_Double_Slash(combineCityBean.cityCode + "//" + arnkTk.tkImg.toString(), synTree);
                    lexer.skipToken(1);
                } else {
                    // sc5
                    Matcher m = digitPattern.matcher(lexer.getRestStr());
                    if (m.find()) {
                        lexer.moveAhead(m.start());
                    } else {
                        // ? unknow(impossible)
                    }
                }

            } else {

                // pushback CAN//MEL, prepare to process following fare seg
                // if CAN //MEL ?
                arnkLexer.pushBackTk(1);
                arnkLexer.nextCityToken();

                if (arnkLexer.peekTokenType() == SPTokenType.DOUBLE_SLASH) {

                    Matcher m = doubleSlashPattern.matcher(arnkLexer.getRestStr());
                    if (m.find()) {
                        arnkLexer.moveAhead(m.end());
                    }

                    SPToken arnkTk = arnkLexer.nextCityToken();
                    String nxtCity = arnkTk.tkImg.toString();
                    if (arnkTk.tkImg.length() < PnrParserConstant.CITY_CODE_LENGTH) {
                        // scenarios 2:
                        SPToken arnkTkNext = arnkLexer.nextToken();
                        if (arnkTkNext.tkImg.length() == PnrParserConstant.CITY_CODE_LENGTH) {
                            nxtCity += arnkTkNext.tkImg.toString();
                            lexer.skipToken(1);
                        } else {
                            // scenarios 21:
                            String restCity = arnkTkNext.tkImg.substring(0, PnrParserConstant.CITY_CODE_LENGTH - arnkTk.tkImg.length()).toString();
                            nxtCity += restCity;
                            lexer.pushBackTk(1);
                            int idx = lexer.getRestStr().indexOf(restCity);
                            lexer.moveAhead(idx + restCity.length());
                        }
                    } else {

                        // do nothing , impossible
                    }
                    procARNK_Double_Slash(combineCityBean.cityCode + "//" + nxtCity, synTree);
                } else {
                    // scene 7,8
                    lexer.pushBackTk(1);
                    lexer.nextCityToken();
                }
            }

            // UA SFO UA SHA1077.56

            return procFare(lexer, synTree);
            // procFare(lexer, synTree);
            // return new ProcResult(true);

        } else {
            logger.warn("format error , expect city_code , got:{}", combineCityBean.cityCode);
        }

        return new ProcResult(true);
    }

    /**
     * ever call on procCarrier , just put the CITY in synTree.curCodeList
     * 
     * @param lexer
     * @param synTree
     * @return
     */
    ProcResult procCarrier(SegpriceLexer lexer, SegPriceSyntaxTree synTree) {

        SPTokenType tk = lexer.peekTokenType();
        SPToken tkn;
        // MU X/PAR AF FRA M800.61MU
        if (tk != null && tk == SPTokenType.TRANSFER) {
            lexer.nextXToken();

            ProcResult procResult = procCity(lexer, synTree);
            if (procResult.isNeedMakePrice()) {
                // after do all procTransfer, build the finally segPrice
                makePriceSeg(lexer, synTree);
            }

            if (SPTokenType.PAR == lexer.peekTokenType()) {
                lexer.nextParToken();
            }

            // process next carrierToken
            tkn = lexer.nextToken();
            if (PNRParser.getCarrierCodeMap().containsKey(tkn.tkImg.trim().toString())) {
                return procCarrier(lexer, synTree);
            } else {
                lexer.pushBackTk(1);
                return new ProcResult(false);
            }

        }

        return procCity(lexer, synTree);

    }

    private boolean isFareType(SPTokenType tktp) {
        return tktp == SPTokenType.M || tktp == SPTokenType.Q || tktp == SPTokenType.NUMBER || tktp == SPTokenType.ONE_S || tktp == SPTokenType.SVALUE;
    }

    @Deprecated
    ProcResult procTransfer(SegpriceLexer lexer, SegPriceSyntaxTree synTree) {
        String originStr = lexer.getRestStr();
        String restStr = lexer.getRestStr().trim();

        int tagCityCodeLength = TRANSFER_TAG.length() + PnrParserConstant.CITY_CODE_LENGTH;
        String city1 = StringUtils.substring(restStr, TRANSFER_TAG.length(), tagCityCodeLength).trim();

        SPToken tkn = lexer.nextToken();
        if (tkn.tkImg == null || tkn.tkImg.trim().length() == 0) {
            return new ProcResult(false);
        }

        restStr = StringUtils.substring(restStr, tagCityCodeLength);

        CombineCityBean combineCityBean = new CombineCityBean();

        tryCombineCity(tkn.getTkImgStr(), lexer, synTree, combineCityBean);

        int aheadStep = 0;

        synTree.curCityCodeList.add(combineCityBean.cityCode);

        lexer.moveAhead(originStr.indexOf(city1) + city1.length());

        // X/SEL160.86OZ BJS M/IT
        // 11JAN14SHA AF PAR345.30/-BRU AF X/PAR S13.66AF SHA Q BRUSHA10.93
        // 604.28NUC974.17END ROE6.081590
        // break the line to 2 seg: "AF PAR345.30/-BRU" ,
        // "AF X/PAR S13.66AF SHA Q BRUSHA10.93 604.28"
        PROC_X: while (true) {

            SPTokenType tktp = lexer.peekTokenType();
            if (isFareType(tktp)) {
                return procFare(lexer, synTree);
            } else {

                SPToken spt = lexer.nextToken();
                if (PNRParser.getCarrierCodeMap().containsKey(spt.tkImg.toString())) {
                    return procCarrier(lexer, synTree);
                } else {
                    break PROC_X;
                }
            }

        }

        synTree.curSegmentsPrice.cityCodeList.addAll(synTree.curCityCodeList);

        SegPrice segprice = new SegPrice();

        SPTokenType tktp = lexer.peekTokenType();

        // when procFranser, will get next cityCode
        // X/CHI UA CMH

        // X/SEL160.86OZ BJS M/IT

        // scene2
        // before comineLine :
        // 21JAN14SHA MU TPE164.43CI DEL975.73//KTM KA X/HKG M KTMHKG82
        // 3.15MU SHA Q4.25 98.65NUC2066.21END ROE6.081590
        // after combineLine:
        // 21JAN14SHA MU TPE164.43CI DEL975.73//KTM KA X/HKG M KTMHKG823.15MU
        // SHA Q4.25 98.65NUC2066.21END ROE6.081590
        if (tktp == SPTokenType.WORD) {
            lexer.skipToken(1);
            SPToken tk = lexer.nextCityToken();

            if (tk.ttype == SPTokenType.WORD) {
                MutableString city2 = tk.tkImg.trim();
                segprice.cityCodeList.add(city2.toString());

                aheadStep = originStr.indexOf(city2.toString());
                lexer.moveAhead(aheadStep + city2.length());
            } else {
                logger.warn("format error, expect cityCode, met:{}", tk.tkImg);
            }
        } else {

            aheadStep = originStr.indexOf(city1);
            lexer.moveAhead(aheadStep + city1.length());
        }

        synTree.curSegmentsPrice = segprice;

        return procFare(lexer, synTree);

    }

    private ProcResult procCarrierAfterQS(SegpriceLexer lexer, SegPriceSyntaxTree synTree, List<Double> vs) {

        SPToken qTk = lexer.nextNumber();

        boolean qProcessed = false;

        // 3 scenarios:
        // Q SHADEL97.86END ROE6.130990
        // Q4.2 5 205.53NUC419.56END
        // Q4.2 5 205.53 9W
        // Q4.2 205.53 9W
        String res = lexer.getRestStr();
        Matcher m = CARRIER_PATTETN.matcher(res);
        if (m.find()) {
            int idx = m.start();
            String intervelStr = res.substring(0, idx);
            String[] strArray = intervelStr.trim().split("\\s+");
            int numCnt = 0;
            for (String str : strArray) {
                if (NumberUtils.isNumber(str)) {
                    numCnt++;
                }
            }
            if (numCnt >= 2) {
                vs.add(NumberUtils.toDouble(qTk.getTkImgStr() + strArray[0]));
                qProcessed = true;
                // skip the combined number
                lexer.nextNumber();
            }
        }

        if (!qProcessed) {
            if (qTk != null && qTk.ttype == SPTokenType.NUMBER) {
                vs.add(qTk.tkImgNumberValue);
            }
        }
        return procFare(lexer, synTree);

    }

    // SHA SQ X/SIN SQ DEL M/IT Q SHADEL97.86END ROE6.130990
    // CX PAR Q4.25M/IT
    private ProcResult procFare(SegpriceLexer lexer, SegPriceSyntaxTree synTree) {

        SegPrice sp = synTree.curSegmentsPrice;

        if (SPTokenType.PAR == lexer.peekTokenType()) {
            lexer.nextParToken();
        }

        SPToken token = lexer.nextToken();

        // X/HKG 9W
        if (PNRParser.isCarrierCode(token.tkImg)) {
            if (token != null && PNRParser.isCarrierCode(token.tkImg.trim())) {
                return procCarrier(lexer, synTree);
            }
        }

        lexer.pushBackTk(1);

        SPTokenType tk = lexer.peekTokenType();

        // M800.61MU
        if (tk == SPTokenType.WORD) {
            token = lexer.peekToken();
            if (token.tkImg.startsWith(END)) {
                return new ProcResult(true);
                // return new ProcResult(false);
            }
        }

        // M800.61MU
        if (tk == SPTokenType.M) {
            sp.pricetype = PnrParserConstant.CONST_PRICETYPE_M;
            lexer.nextMToken();

        }

        if (tk == SPTokenType.SVALUE || tk == SPTokenType.ONE_S) {
            return procCarrierAfterQS(lexer, synTree, sp.svalue);
        }

        // NGB Q4.25 65.35
        // only two digit following a Q is not combine, so we need to check if
        // we
        // should combine the two digit when processing Q value
        if (tk == SPTokenType.Q) {
            return procCarrierAfterQS(lexer, synTree, sp.qvalue);
        }

        if (SPTokenType.IT == lexer.peekTokenType()) {
            sp.pricetype = PnrParserConstant.CONST_PRICETYPE_IT;
            sp.price = -1;

            lexer.skipToken(1);

            tk = lexer.peekTokenType();
            if (tk == SPTokenType.Q) {
                return procFare(lexer, synTree);
            } else {
                return new ProcResult(true);
            }
        } else {

            SPToken numTk = lexer.nextNumber();

            if (numTk != null && numTk.ttype == SPTokenType.NUMBER) {
                sp.price = numTk.tkImgNumberValue;

                // 18JAN14MIL EK X/DXB EK SHA Q MILSHA101.11 1470.23Q
                // MILSHA34.15NUC1605.49END ROE0.731857
                // check if there's a Qvalue after a fare seg
                // on very rare scenarios, there's fare seg will following a
                // Qvalue

                // 11JAN14SHA AF PAR345.30/-BRU AF X/PAR S13.66AF SHA Q BRUSHA1
                // 0.93 604.28NUC974.17END ROE6.081590

                for (;;) {

                    SPTokenType spt = lexer.peekTokenType();
                    if (SPTokenType.Q == spt) {

                        SPToken qTk = lexer.nextNumber();

                        if (qTk != null && qTk.ttype == SPTokenType.NUMBER) {
                            sp.qvalue.add(qTk.tkImgNumberValue);
                        }
                    } else if (SPTokenType.ONE_S == spt) {

                        SPToken qTk = lexer.nextOneSToken();

                        if (qTk != null && (qTk.ttype == SPTokenType.ONE_S || qTk.ttype == SPTokenType.SVALUE)) {
                            SPToken onesValue = lexer.nextNumber();
                            sp.svalue.add(onesValue.tkImgNumberValue);
                        }
                    } else if (SPTokenType.WORD == spt) {
                        // 04FEB14SHA QF SYD QF HTI//MEL20M2809.78QF X/SYD QF
                        // SHA2958.10D SHASYD616.61NUC6384.49END ROE6.081590

                        // D SHASYD616.61 这个整个部分是运价的较高点比较价。你可以视为票价的一部分
                        // 2958.10 是MEL-SYD-SHA的总的价格

                        // D代表 DESTINATION 客票上航程终止的地点，也是票价计算点

                        SPToken dTk = lexer.nextToken();
                        if (dTk.tkImg.equals("D") && dTk.tkStartIdx == numTk.tkEndIdx) {
                            SPToken dvalueTk = lexer.nextNumber();
                            if (dvalueTk.ttype == SPTokenType.NUMBER) {
                                synTree.dvalue.add(dvalueTk.tkImgNumberValue);
                            }
                            break;
                        } else {
                            lexer.pushBackTk(1);
                            break;
                        }
                    } else {
                        break;
                    }
                }

            }

            else {
                logger.warn("format error , expect number  , got:{}", numTk.tkImg);
                return new ProcResult(false);
            }
        }

        synTree.curSegmentsPrice = sp;

        return new ProcResult(true);

    }

    // SYD//MEL M1367.37
    // HTI//MEL20M2809.78QF
    static private ProcResult procARNK_Double_Slash(String str, SegPriceSyntaxTree synTree) {
        String[] cityPair = StringUtils.split(str.trim().toString(), "//");

        if (cityPair.length == 2) {
            if (StringUtils.trim(cityPair[0]).length() == PnrParserConstant.CITY_CODE_LENGTH) {
                if (StringUtils.trim(cityPair[1]).length() == PnrParserConstant.CITY_CODE_LENGTH) {
                    synTree.curCityCodeList.add(cityPair[1].toString());
                } else {
                    synTree.curCityCodeList.add(StringUtils.left(cityPair[1], PnrParserConstant.CITY_CODE_LENGTH));
                }
            } else {
                logger.warn("err format:{}", str);
            }
        }

        return new ProcResult(false);
    }

    static private void procARNK_Hyphen_Slash(SegpriceLexer lexer, SegPriceSyntaxTree synTree) {

        // OSA229.56/-SPK
        SPTokenType tktp = lexer.peekTokenType();

        if (tktp == SPTokenType.ARNK) {
            SegPrice sp = new SegPrice();
            sp.segtype = PnrParserConstant.CONST_SEGTYPE_ARNK;

            SPToken cityTk = lexer.nextToken(lexer.getRestStr());
            if (null != cityTk) {
                sp.cityCodeList.add(cityTk.getTkImgStr().substring(2));
                synTree.segmentsPrices.add(sp);
            }
            // skip /-SPK
            lexer.skipToken(1);
        }

        // append the cityTk to a prior citylist
        if (tktp == SPTokenType.DOUBLE_SLASH) {
            SPToken cityTk = lexer.nextToken(lexer.getRestStr());
            if (null != cityTk) {
                Preconditions.checkArgument(synTree.segmentsPrices.size() > 0, "synTree.segmentsPrices.size()>0");
                SegPrice segPrice = synTree.segmentsPrices.get(synTree.segmentsPrices.size() - 1);
                segPrice.cityCodeList.add(cityTk.getTkImgStr().substring(2));
                lexer.skipToken(1);
            }
        }
    }

    private void makePriceSeg(SegpriceLexer lexer, SegPriceSyntaxTree synTree) {

        SegPrice segPrice = synTree.curSegmentsPrice.clone();

        segPrice.cleanAddAll(synTree.curCityCodeList);

        synTree.segmentsPrices.add(segPrice);

        synTree.curCityCodeList.clear();
        synTree.curSegmentsPrice.qvalue.clear();

        procARNK_Hyphen_Slash(lexer, synTree);
    }

    private String preprocess(String str) {

        // "/IT" is one token and current engine can't handle it when it's
        // "/  IT"
        String retStr = str.replaceAll("\\/\\s*IT ", "/IT ");
        retStr = retStr.replaceAll("\\/I\\s*T ", "/IT ");
        retStr = retStr.replaceAll("/\\s*/", "//");
        retStr = retStr.replaceAll("/\\s*-", "/-");
        retStr = retStr.replaceAll("X\\s*/", "X/");

        return retStr;
    }

    public static boolean isCity3Code(String s3) {
        return (StringUtils.isAlpha(s3) && s3.length() == 3);
    }

    public static String preprocessMultiLine(MutableString[] nucStrList) {

        logger.info("preprocess:<<<BEGIN\n{}\nEND", StringUtils.join(nucStrList, "\n"));

        for (int i = 0; i < nucStrList.length - 1; i++) {

            String str1 = nucStrList[i].toString();
            if (StringUtil.isBlank(str1)) {
                continue;
            }

            String[] ar1 = StringUtils.trim(str1).split("\\s+");

            String str2 = nucStrList[i + 1].toString();
            if (StringUtil.isBlank(str2)) {
                continue;
            }
            String[] ar2 = StringUtils.trim(str2).split("\\s+");

            if (ar1.length > 0 && ar2.length > 0) {

                String s1 = ar1[ar1.length - 1];
                String s2 = ar2[0];
                String s3 = s1 + s2;

                // Notice ! don't combine
                // Q4.25
                // 258.00NUC
                // if startWith Q, there must follows 2 numbers that shouldn't
                // be combine
                //
                // Q4.25M530.28EN
                // D

                String s1last = StringUtils.right(s1, 1);
                String s21st = StringUtils.left(s2, 1);

                // KE SHA
                if (PNRParser.isCarrierCode(s1) && s2.length() == PnrParserConstant.CITY_CODE_LENGTH) {
                    continue;
                }

                final Pattern startWithQdigit = Pattern.compile("Q\\d");
                if (startWithQdigit.matcher(s1).find()) {
                    // only is senarios should be let go：
                    // Q4.25
                    // 258.00NUC

                    // 09FEB14SHA AC X/YTO AC BOS853.39AC YTO Q7.50 99.00AC SHA
                    // Q14
                    // .08 499.04NUC1473.01END ROE6.081590
                    // if '.' is the begin of the next line , than combine it
                    if (StringUtils.isNumeric(s21st.trim())) {
                        continue;
                    }
                }

                // combinedigit :
                // 258.00NUC524.
                // 50END ROE1.000000

                // 258.00NUC524.50END ROE6.8
                // 000123

                // 30JAN14SHA MU BKK321.46HX HKG MU SHA Q4.25M530.28NUC855.99EN
                // D ROE6.081590

                boolean isNumber = false;

                if (StringUtils.isNumeric(s1last)) {
                    if (StringUtils.isNumeric(s21st) || ".".equals(s21st)) {
                        if (!SegPriceParser.ONE_S.equals(StringUtils.left(s2, 2))) {
                            // SHA3770.39
                            // 1S205.53
                            isNumber = true;
                        }
                    }
                } else if (".".equals(s1last)) {
                    if (StringUtils.isNumeric(s21st)) {
                        isNumber = true;
                    }
                }

                // 04JAN14WAW LH X/FRA LH X/SHA599.09LH X/MUC LH PRG M1878.56NU
                // C2477.65END ROE3.067150
                boolean containNUC = false;
                // KESHA
                Matcher m = CURRENCY_PATTETN.matcher(s3);
                if (m.find() && m.start() < s1.length() && m.end() > s1.length()) {
                    containNUC = true;
                }

                boolean containEND = false;
                m = END_PATTETN.matcher(s3);
                if (m.find() && m.start() < s1.length() && m.end() > s1.length()) {
                    containEND = true;
                }

                // X/S
                // EL
                // ==>X/SEL
                boolean isTrans = s3.startsWith("X/") && isCity3Code(StringUtils.substring(s3, 2));

                if (isNumber || containNUC || isCity3Code(s3) || PNRParser.isCarrierCode(s3) || isTrans || containEND) {
                    nucStrList[0] = nucStrList[0].trimRight().append(nucStrList[i + 1].trimLeft());
                    nucStrList[i + 1].setLength(0);
                }
            }

        }

        MutableString finalNucStr = new MutableString();
        for (MutableString str : nucStrList) {
            finalNucStr.append(str);
        }
        return finalNucStr.toString();
    }

    // 24NOV13
    private SegPriceSyntaxTree proc(String fullStr) {

        SegPriceSyntaxTree synTree = new SegPriceSyntaxTree();

        if (StringUtils.isBlank(fullStr)) {
            return synTree;
        }
        logger.info("nuc str:\n{}\n", fullStr);

        fullStr = preprocess(fullStr);

        logger.info(" final nuc str:\n{}\n", fullStr);

        SegpriceLexer lexer = new SegpriceLexer(fullStr);

        SegpriceLexInfo pi = new SegpriceLexInfo();

        @SuppressWarnings("unused")
        SPToken headTimeTk = lexer.nextToken(pi);

        // MutableString headTime = headTimeTk.tkImg;

        // expact : MU HKG130.71
        SPToken carrierTk;

        boolean shouldEndProcess = false;
        PROC_CARRIER: while (true) {

            if (lexer.peekTokenType() == SPTokenType.CARRIER) {
                carrierTk = lexer.nextCarrierToken();
            } else {
                carrierTk = lexer.nextToken(pi);
                carrierTk.tkImg.trim();
            }

            if (carrierTk.tkImg.startsWith(END)) {
                shouldEndProcess = true;
            }

            // some cityCode could same to nucCode, but this problem has no
            // affetct to the parser now.
            // Because the parser will judge if a token is a NUC only when the
            // parse is expacting a Carrier Token,
            // hence, we won't meet a sample that
            // "A city3code token followed by a fare seg" . etc, MU HGK 123CAX
            // only on this condition, a city will be miss-recognized as a NUC
            // and end the parsing to early.
            // so,we needn't prevent this problem now
            //
            // FIND_CURRENCY: for (String currency :
            // getCurrencyCodeMap().keySet()) {
            // if (carrierTk.tkImg.startsWith(currency)) {
            //
            // // to avoid the scenary that "NUC 123.4", we can use
            // // "lexer.nextNumber()" instead
            // lexer.pushBackTk(1);
            // SPToken nucTk = lexer.nextNumber();
            //
            // synTree.nucType =
            // StringUtils.substring(carrierTk.tkImg.toString(), 0,
            // currency.length());
            //
            // if (nucTk != null && nucTk.ttype == SPTokenType.NUMBER) {
            // synTree.nucValue = nucTk.tkImgNumberValue;
            // }
            //
            // shouldEndProcess = true;
            //
            // break FIND_CURRENCY;
            // }

            final int CURRENCY_LENGTH = 3;
            if (CURRENCY_PATTETN_LINE_START.matcher(carrierTk.tkImg.toString()).find()) {
                // to avoid the scenary that "NUC 123.4", we can use
                // "lexer.nextNumber()" instead
                lexer.pushBackTk(1);
                SPToken nucTk = lexer.nextNumber();

                synTree.nucType = StringUtils.substring(carrierTk.tkImg.toString(), 0, CURRENCY_LENGTH);

                if (nucTk != null && nucTk.ttype == SPTokenType.NUMBER) {
                    synTree.nucValue = nucTk.tkImgNumberValue;
                }

                shouldEndProcess = true;
            }

            // }

            if (shouldEndProcess) {

                SegpriceLexer nucSegpriceLexer = new SegpriceLexer(carrierTk.tkImg);

                String restStr = lexer.getRestStr();

                int roeIdx = restStr.indexOf("ROE");
                if (roeIdx >= 0) {
                    String roeStr = restStr.substring(roeIdx);
                    SPToken roeTk = nucSegpriceLexer.nextNumber(roeStr);
                    if (roeTk != null && roeTk.ttype == SPTokenType.NUMBER) {
                        synTree.roeValue = roeTk.tkImgNumberValue;
                    }
                }
            }

            if (shouldEndProcess) {
                break PROC_CARRIER;
            }

            boolean findCarrier = false;

            if (!PNRParser.isCarrierCode(carrierTk.tkImg.trim())) {

                FIND_CARRIER: while (true) {

                    SPToken nxtCarrierTk = lexer.nextToken(pi);

                    if (nxtCarrierTk == null) {
                        break PROC_CARRIER;
                    } else {
                        String combine = carrierTk.tkImg.trim().toString() + nxtCarrierTk.tkImg.trim();

                        if (PNRParser.isCarrierCode(combine)) {
                            findCarrier = true;
                            break FIND_CARRIER;
                        }
                        logger.warn("combine next token , got:{}", combine);
                    }
                    logger.warn("format error , expect CARRIER_CODE, got:{}", carrierTk.tkImg);

                    if (PNRParser.isCarrierCode(carrierTk.tkImg.trim())) {
                        findCarrier = true;
                        break FIND_CARRIER;
                    }
                }
            } else {
                findCarrier = true;
            }

            if (findCarrier) {

                ProcResult procResult = procCarrier(lexer, synTree);
                if (procResult.isNeedMakePrice()) {
                    makePriceSeg(lexer, synTree);
                }

            } else {
                break PROC_CARRIER;
            }

        }

        logger.info(synTree.toString());
        return synTree;
    }

    public static SegPriceSyntaxTree segmentsPriceParser(MutableString[] nucStrList) {
        String ret = preprocessMultiLine(nucStrList);
        return new SegPriceParser().proc(ret);
    }

    public static SegPriceSyntaxTree segmentsPriceParser(String[] strs) {

        List<MutableString> ls = Lists.newArrayList();
        for (String str : strs) {
            ls.add(new MutableString(str));
        }
        String ret = preprocessMultiLine(ls.toArray(new MutableString[0]));
        return new SegPriceParser().proc(ret);

    }

    public static SegPriceSyntaxTree segmentsPriceParser(String str) {
        String[] st = str.split("(\r|\n)+");
        return segmentsPriceParser(st);
    }

}
