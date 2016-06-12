package com.travelzen.etermface.client.analysis.fare;

import com.travelzen.etermface.common.pojo.fare.NfdAllAnalysisResult;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 解析nfn01//01的规则
 * <p/>
 * author:杨果
 */
public class Nfn01Parser {
    private static Logger logger = LoggerFactory.getLogger(Nfn01Parser.class);

    static Pattern preorderPattern = Pattern.compile("提前预订: (.+)$");
    static Pattern preorderPatternEarliest = Pattern.compile("最早(.+?)天");
    static Pattern preorderPatternLastest = Pattern.compile("最晚(.+?)天");
    static Pattern weekdayPattern = Pattern.compile("去程适用星期限制: (.+)$");
    static Pattern tktIssueDatePattern = Pattern.compile("最(.?)出票日期: \\s+(\\d{2}\\w{3}\\d{4})");

    static public void parse(String str, NfdAllAnalysisResult nfdAllAnalysisResult) {
        String[] lines = str.split("(\r|\n)+");
        logger.debug(str);
        NXT_LN:
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            Matcher m = preorderPattern.matcher(line);
            if (m.find()) {
                String cont = StringUtils.trim(m.group(1));
                Matcher mPreorderEarliest = preorderPatternEarliest.matcher(cont);
                if (mPreorderEarliest.find()) {
                    nfdAllAnalysisResult.getNfn01AnalysisResult().setPrebookEarliestDay(mPreorderEarliest.group(1));
                }
                Matcher mPreorderLastest = preorderPatternLastest.matcher(cont);
                if (mPreorderLastest.find()) {
                    nfdAllAnalysisResult.getNfn01AnalysisResult().setPrebookLatestDay(mPreorderLastest.group(1));
                }
            }

            Matcher mWeekday = weekdayPattern.matcher(line);
            if (mWeekday.find()) {
                String weekdays = StringUtils.trim(mWeekday.group(1));
                nfdAllAnalysisResult.getNfn01AnalysisResult().setSuitableWeekdays(weekdays);
            }

            if (line.startsWith("去程适用时刻范围:  ")) {
                if (i + 1 < lines.length) {
                    nfdAllAnalysisResult.getNfn01AnalysisResult().setSuitableTimeRange(StringUtils.trim(lines[i + 1]));
                }
            }

            final String suitableflt = "去程适用航班号范围: ";
            if (line.startsWith(suitableflt)) {
                StringBuffer fltStr = new StringBuffer(lines[i].substring(suitableflt.length()));
                int fltEndLineIdx = i;
                FIND_END_FLT:
                for (; fltEndLineIdx < lines.length; fltEndLineIdx++) {
                    String flt = lines[fltEndLineIdx];
                    if (StringUtils.isBlank(flt) || StringUtils.startsWith(flt, "去程适用时刻范围:") || StringUtils.startsWith(flt, "去程适用星期限制:") || StringUtils.startsWith(flt, "去程除外航班号范围:")) {
                        break FIND_END_FLT;
                    }
                }

                for (int fltIdx = i + 1; fltIdx < fltEndLineIdx; fltIdx++) {
                    fltStr.append(lines[fltIdx]);
                }
                nfdAllAnalysisResult.getNfn01AnalysisResult().setSuitableFltNumber(fltStr.toString());
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
                nfdAllAnalysisResult.getNfn01AnalysisResult().setOutwardExcludeFltNumber(fltStr.toString());
            }

            Matcher mTktIssueDate = tktIssueDatePattern.matcher(line);
            while (mTktIssueDate.find()) {
                String earlyLate = StringUtils.trim(mTktIssueDate.group(1));
                String date = StringUtils.trim(mTktIssueDate.group(2));
                if ("早".equals(earlyLate)) {
                    nfdAllAnalysisResult.getNfn01AnalysisResult().setEarliestIssuteTktDate(date);
                } else if ("晚".equals(earlyLate)) {
                    nfdAllAnalysisResult.getNfn01AnalysisResult().setLatestIssuteTktDate(date);
                } else {
                    continue NXT_LN;
                }
            }
        }
    }
}
