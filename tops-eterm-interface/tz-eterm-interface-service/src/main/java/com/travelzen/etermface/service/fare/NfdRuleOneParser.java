package com.travelzen.etermface.service.fare;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.etermface.service.abe_imitator.fare.pojo.CabinInfo;

/**
 * author:yangguo
 * description:
 * <p/>
 * 解析nfn01//01的规则
 */
public class NfdRuleOneParser {

    private static Logger logger = LoggerFactory.getLogger(NfdRuleOneParser.class);

    static Pattern preorderPattern = Pattern.compile("提前预订: (.+)$");

    static Pattern preorderPatternEearliest = Pattern.compile("最早(.+?)天");
    static Pattern preorderPatternLastest = Pattern.compile("最晚(.+?)天");

    static Pattern weekdayPattern = Pattern.compile("去程适用星期限制: (.+)$");

    static Pattern tktIssueDatePattern = Pattern.compile("最(.?)出票日期: \\s+(\\d{2}\\w{3}\\d{4})");

    static public CabinInfo parse(String str) {

        String[] lines = str.split("(\r|\n)+");

        //	List<String> newLines = Lists.newArrayList();
        //
        //	for (String line : lines) {
        //
        //	    int idxWeek = line.indexOf("去程适用星期限制:");
        //	    if (idxWeek > 0) {
        //		newLines.add(line.substring(0, idxWeek));
        //		line = line.substring(idxWeek);
        //		newLines.add(line);
        //	    } else {
        //		newLines.add(line);
        //	    }
        //
        //	    int idxTime = line.indexOf("去程适用时刻范围:");
        //	    if (idxTime > 0) {
        //		newLines.add(line.substring(0, idxTime));
        //		line = line.substring(idxTime);
        //		newLines.add(line);
        //	    } else {
        //		newLines.add(line);
        //	    }
        //
        //	}

        //	lines = newLines.toArray(new String[0]);

        logger.debug(str);

        CabinInfo cb = new CabinInfo();

        NXT_LN:
        for (int i = 0; i < lines.length; i++) {

            String line = lines[i];

            if (line.contains("提前预订:")) {
                cb.setHasPreorder(true);
            }

            Matcher m = preorderPattern.matcher(line);
            if (m.find()) {

                String cont = StringUtils.trim(m.group(1));

                Matcher mPreorderEearliest = preorderPatternEearliest.matcher(cont);
                if (mPreorderEearliest.find()) {
                    cb.setPrebookEarliestDay(mPreorderEearliest.group(1));
                }

                Matcher mPreorderLastest = preorderPatternLastest.matcher(cont);
                if (mPreorderLastest.find()) {
                    cb.setPrebookLatestDay(mPreorderLastest.group(1));
                }

            }

            Matcher mWeekday = weekdayPattern.matcher(line);
            if (mWeekday.find()) {
                String weekdays = StringUtils.trim(mWeekday.group(1));
                cb.setSuitableWeekdays(weekdays);
            }

            if (line.startsWith("去程适用时刻范围:  ")) {
                if (i + 1 < lines.length) {
                    cb.setSuitableTimeRange(StringUtils.trim(lines[i + 1]));
                }
            }

            final String suitableflt = "去程适用航班号范围: ";
            if (line.startsWith(suitableflt)) {

                StringBuffer fltStr = new StringBuffer(lines[i].substring(suitableflt.length()));

                int fltEndLineIdx = i;
                FIND_END_FLT:
                for (; fltEndLineIdx < lines.length; fltEndLineIdx++) {
                    String flt = lines[fltEndLineIdx];
                    //find new tag that is different with the  main tag : suitableflt
                    if (StringUtils.isBlank(flt) || //
                            StringUtils.startsWith(flt, "去程适用时刻范围:") || //
                            StringUtils.startsWith(flt, "去程适用星期限制:") || //
                            StringUtils.startsWith(flt, "去程除外航班号范围:")) {
                        break FIND_END_FLT;
                    }
                }

                for (int fltIdx = i + 1; fltIdx < fltEndLineIdx; fltIdx++) {
                    fltStr.append(lines[fltIdx]);
                }

                cb.setSuitableFltNumber(fltStr.toString());
            }

            final String outwardExcludeFlt = "去程除外航班号范围: ";
            if (line.startsWith(outwardExcludeFlt)) {

                StringBuffer fltStr = new StringBuffer(lines[i].substring(outwardExcludeFlt.length()));

                int fltEndLineIdx = i;
                FIND_END_FLT:
                for (; fltEndLineIdx < lines.length; fltEndLineIdx++) {
                    String flt = lines[fltEndLineIdx];
                    if (StringUtils.isBlank(flt) || StringUtils.startsWith(flt, "去程适用时刻范围:") || StringUtils.startsWith(flt, "去程适用星期限制:")) {
                        break FIND_END_FLT;
                    }
                }

                for (int fltIdx = i + 1; fltIdx < fltEndLineIdx; fltIdx++) {
                    fltStr.append(lines[fltIdx]);
                }

                cb.setOutwardExcludeFltNumber(fltStr.toString());
            }

            Matcher mTktIssueDate = tktIssueDatePattern.matcher(line);
            while (mTktIssueDate.find()) {
                String earlyLate = StringUtils.trim(mTktIssueDate.group(1));
                String date = StringUtils.trim(mTktIssueDate.group(2));
                if ("早".equals(earlyLate)) {
                    cb.setEarliestIssuteTktDate(date);
                } else if ("晚".equals(earlyLate)) {
                    cb.setLatestIssuteTktDate(date);
                } else {
                    continue NXT_LN;
                }
            }

        }

        //	去程/回程 NFN:10//01 适用规定            乘机者
        //	预订规定             运价组合 NFN:10//05 团队规定
        //	支付/出票 NFN:10//07 退票规定 NFN:10//08 变更规定 NFN:10//09
        //	扩充规定             其他规定 NFN:10//11 全部规则 NFN:10
        //
        //	***去程回程***
        //	提前预订: 最晚 1天
        //
        //	儿  运价:          800.00
        //
        //	去程航班季节:
        //	27FEB14-29MAR14
        //	去程适用航班号范围: 3970-3970 3968-3968 3948-3948 3598-3598 0-0
        //
        //	回程航班季节:
        //	27FEB14-29MAR14
        //
        //	最早出票日期:      25FEB2014          最晚出票日期:      29MAR2014

        logger.debug("hasPreorder:" + cb.isHasPreorder() + " " + cb.getPrebookEarliestDay() + " " + cb.getPrebookLatestDay() + " FLT: "
                + cb.getSuitableFltNumber());

        return cb;
    }

    public static void main(String[] args) {
        Matcher mTktIssueDate = tktIssueDatePattern.matcher("最早出票日期:      26FEB2014          最晚出票日期:      29MAR2014  ");

        while (mTktIssueDate.find()) {

            String earlyLate = StringUtils.trim(mTktIssueDate.group(1));
            String date = StringUtils.trim(mTktIssueDate.group(2));

        }
    }

}
