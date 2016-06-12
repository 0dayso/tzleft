package com.travelzen.etermface.common.pojo.fare;

import com.travelzen.framework.core.time.DateTimeUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:15/4/13
 * Time:下午2:49
 * <p/>
 * Description:
 * <p/>
 * Nfd半结构化的数据解析成结构化数据对应的类
 */
public class NfdAllAnalysisResult implements Serializable {
    private static final long serialVersionUID = 8370802389680852051L;
    private static Logger logger = LoggerFactory.getLogger(NfdAnalysisResult.class);

    public NfdAnalysisResult nfdAnalysisResult = new NfdAnalysisResult();
    public Nfn01AnalysisResult nfn01AnalysisResult = new Nfn01AnalysisResult();

    /**
     * 描述nfd结果中一行的数据结构
     */
    public static class NfdAnalysisResult {
        /**
         * 舱位等级：Y(经济舱)，C(公务舱)，F(头等舱)
         */
        public String cabinLevel;

        /**
         * 舱位代码:Q,T等
         */
        public String cabinCode;

        /**
         * 运价基础
         */
        public String fareBasis;

        /**
         * 单程价格
         */
        public float owFare;

        /**
         * 往返
         */
        public float rtFare;

        /**
         * 价格开始有效时间
         */
        public String effectiveStartDate;

        /**
         * 价格过期时间
         */
        public String effectiveEndDate;

        /**
         * 出票时间限定
         */
        public String ticketTimeLimit;

        /**
         * 最短停留
         */
        public String minStaty;

        /**
         * 最长停留
         */
        public String maxStaty;

        public float getOwFare() {
            return owFare;
        }

        public void setOwFare(String owFare) {
            float rs = Float.parseFloat(owFare);
            this.owFare = rs;
        }

        public float getRtFare() {
            return rtFare;
        }

        public void setRtFare(String rtFare) {
            float rs = Float.parseFloat(rtFare);
            this.rtFare = rs;
        }

        public String getEffectiveStartDate() {
            return effectiveStartDate;
        }

        public void setEffectiveStartDate(String effectiveStartDate) {
            try {
                DateTime date = DateTimeUtil.getDate(effectiveStartDate, "ddMMMyy", Locale.US);
                String rs = DateTimeUtil.formatDate(date, "yyyyMMdd");
                if (rs.startsWith("19")) {
                    rs = rs.replaceFirst("19", "20");
                }
                this.effectiveStartDate = rs;
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        }

        public String getEffectiveEndDate() {
            return effectiveEndDate;
        }

        public void setEffectiveEndDate(String effectiveEndDate) {
            try {
                DateTime date = DateTimeUtil.getDate(effectiveEndDate, "ddMMMyy", Locale.US);
                String rs = DateTimeUtil.formatDate(date, "yyyyMMdd");
                if (rs.startsWith("19")) {
                    rs = rs.replaceFirst("19", "20");
                }
                this.effectiveEndDate = rs;
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        }


        public String getCabinLevel() {
            return cabinLevel;
        }

        public void setCabinLevel(String cabinLevel) {
            this.cabinLevel = cabinLevel;
        }

        public String getCabinCode() {
            return cabinCode;
        }

        public void setCabinCode(String cabinCode) {
            this.cabinCode = cabinCode;
        }

        public String getFareBasis() {
            return fareBasis;
        }

        public void setFareBasis(String fareBasis) {
            this.fareBasis = fareBasis;
        }

        public void setOwFare(float owFare) {
            this.owFare = owFare;
        }

        public void setRtFare(float rtFare) {
            this.rtFare = rtFare;
        }

        public String getTicketTimeLimit() {
            return ticketTimeLimit;
        }

        public void setTicketTimeLimit(String ticketTimeLimit) {
            this.ticketTimeLimit = ticketTimeLimit;
        }

        public String getMinStaty() {
            return minStaty;
        }

        public void setMinStaty(String minStaty) {
            this.minStaty = minStaty;
        }

        public String getMaxStaty() {
            return maxStaty;
        }

        public void setMaxStaty(String maxStaty) {
            this.maxStaty = maxStaty;
        }
    }

    /**
     * 描述去程/回程信息的静态内部类
     */
    public static class Nfn01AnalysisResult {
        /**
         * 最早提前预定天数
         */
        public String prebookEarliestDay;
        /**
         * 最晚提前预定天数
         */
        public String prebookLatestDay;
        /**
         * 去程适用星期限制
         */
        public String suitableWeekdays;
        /**
         * 去程适用时刻范围
         */
        public String suitableTimeRange;
        /**
         * 去程除外航班号范围
         */
        public String outwardExcludeFltNumber;
        /**
         * 适用的航班
         */
        public String suitableFltNumber;
        /**
         * 最早出票日期
         */
        public String earliestIssuteTktDate;
        /**
         * 最晚出票日期
         */
        public String latestIssuteTktDate;

        public String getPrebookEarliestDay() {
            return prebookEarliestDay;
        }

        public void setPrebookEarliestDay(String prebookEarliestDay) {
            this.prebookEarliestDay = prebookEarliestDay;
        }

        public String getPrebookLatestDay() {
            return prebookLatestDay;
        }

        public void setPrebookLatestDay(String prebookLatestDay) {
            this.prebookLatestDay = prebookLatestDay;
        }

        public String getSuitableWeekdays() {
            return suitableWeekdays;
        }

        public void setSuitableWeekdays(String suitableWeekdays) {
            this.suitableWeekdays = suitableWeekdays;
        }

        public String getSuitableTimeRange() {
            return suitableTimeRange;
        }

        public void setSuitableTimeRange(String suitableTimeRange) {
            this.suitableTimeRange = suitableTimeRange;
        }

        public String getOutwardExcludeFltNumber() {
            return outwardExcludeFltNumber;
        }

        public void setOutwardExcludeFltNumber(String outwardExcludeFltNumber) {
            this.outwardExcludeFltNumber = outwardExcludeFltNumber;
        }

        public String getSuitableFltNumber() {
            return suitableFltNumber;
        }

        public void setSuitableFltNumber(String suitableFltNumber) {
            this.suitableFltNumber = suitableFltNumber;
        }

        public String getEarliestIssuteTktDate() {
            return earliestIssuteTktDate;
        }

        public void setEarliestIssuteTktDate(String earliestIssuteTktDate) {
            this.earliestIssuteTktDate = earliestIssuteTktDate;
        }

        public String getLatestIssuteTktDate() {
            return latestIssuteTktDate;
        }

        public void setLatestIssuteTktDate(String latestIssuteTktDate) {
            this.latestIssuteTktDate = latestIssuteTktDate;
        }
    }

    /**
     * 描述适用规定信息的静态内部类
     */
    public static class Nfn02AnalysisResult {
    }

    /**
     * 描述预定规定信息的静态内部类
     */
    public static class Nfn04AnalysisResult {
    }

    /**
     * 描述运价组合信息的静态内部类
     */
    public static class Nfn05AnalysisResult {
    }

    /**
     * 描述团队规定信息的静态内部类
     */
    public static class Nfn06AnalysisResult {
    }

    /**
     * 描述退票规定信息的静态内部类
     */
    public static class Nfn08AnalysisResult {
    }

    /**
     * 描述变更规定信息的静态内部类
     */
    public static class Nfn09AnalysisResult {
    }

    /**
     * 描述其他规定信息的静态内部类
     */
    public static class Nfn11AnalysisResult {
    }

    public NfdAnalysisResult getNfdAnalysisResult() {
        return nfdAnalysisResult;
    }

    public void setNfdAnalysisResult(NfdAnalysisResult nfdAnalysisResult) {
        this.nfdAnalysisResult = nfdAnalysisResult;
    }

    public Nfn01AnalysisResult getNfn01AnalysisResult() {
        return nfn01AnalysisResult;
    }

    public void setNfn01AnalysisResult(Nfn01AnalysisResult nfn01AnalysisResult) {
        this.nfn01AnalysisResult = nfn01AnalysisResult;
    }
}
