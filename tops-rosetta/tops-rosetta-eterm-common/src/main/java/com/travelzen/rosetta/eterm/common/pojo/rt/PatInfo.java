package com.travelzen.rosetta.eterm.common.pojo.rt;

import java.util.List;

import com.travelzen.rosetta.eterm.common.pojo.enums.PassengerType;

/**
 * PAT报价结果
 *
 * @author yiming.yan
 */
public class PatInfo {
    /**
     * 付款方式 (仅用于FN/A价格)
     */
    private String method;
    /**
     * 货币 (仅用于FN/A价格)
     */
    private String currency;
    /**
     * 价格s
     */
    private List<PatFare> fares;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<PatFare> getFares() {
        return fares;
    }

    public void setFares(List<PatFare> fares) {
        this.fares = fares;
    }

    @Override
    public String toString() {
        return "PatFareResponse [method=" + method + ", currency=" + currency
                + ", fares=" + fares + "]";
    }


    /**
     * PAT报价单条结果（一种乘客类型，多个价格）
     *
     * @author yiming.yan
     */
    public static class PatFare {

        /**
         * 乘客类型
         */
        private PassengerType psgType = PassengerType.ADT;
        /**
         * 价格
         */
        private List<PatFareItem> fareItems;

        public PatFare() {
        }

        public PatFare(PassengerType psgType) {
            this.psgType = psgType;
        }

        public PassengerType getPsgType() {
            return psgType;
        }

        public void setPsgType(PassengerType psgType) {
            this.psgType = psgType;
        }

        public List<PatFareItem> getFareItems() {
            return fareItems;
        }

        public void setFareItems(List<PatFareItem> fareItems) {
            this.fareItems = fareItems;
        }

        @Override
        public String toString() {
            return "PatFare [psgType=" + psgType + ", fareItems=" + fareItems
                    + "]";
        }
    }

    /**
     * PAT报价单条结果项（一个价格）
     *
     * @author yiming.yan
     */
    public static class PatFareItem {
        /**
         * 运价基础
         */
        private String fareBasis;
        /**
         * 票面价
         */
        private String faceFare;
        /**
         * 现价
         */
        private String currentFare;
        /**
         * 代理费
         */
        private String commission;
        /**
         * 总税收
         */
        private String tax;
        /**
         * 机场建设费
         */
        private String cnTax;
        /**
         * 燃油附加费 (已取消)
         */
        private String yqTax;
        /**
         * 总价
         */
        private String totalFare;
        /**
         * SFC
         */
        private String sfc;

        public String getFareBasis() {
            return fareBasis;
        }

        public void setFareBasis(String fareBasis) {
            this.fareBasis = fareBasis;
        }

        public String getFaceFare() {
            return faceFare;
        }

        public void setFaceFare(String faceFare) {
            this.faceFare = faceFare;
        }

        public String getCurrentFare() {
            return currentFare;
        }

        public void setCurrentFare(String currentFare) {
            this.currentFare = currentFare;
        }

        public String getCommission() {
            return commission;
        }

        public void setCommission(String commission) {
            this.commission = commission;
        }

        public String getTax() {
            return tax;
        }

        public void setTax(String tax) {
            this.tax = tax;
        }

        public String getCnTax() {
            return cnTax;
        }

        public void setCnTax(String cnTax) {
            this.cnTax = cnTax;
        }

        public String getYqTax() {
            return yqTax;
        }

        public void setYqTax(String yqTax) {
            this.yqTax = yqTax;
        }

        public String getTotalFare() {
            return totalFare;
        }

        public void setTotalFare(String totalFare) {
            this.totalFare = totalFare;
        }

        public String getSfc() {
            return sfc;
        }

        public void setSfc(String sfc) {
            this.sfc = sfc;
        }

        @Override
        public String toString() {
            return "PatFareItem [fareBasis=" + fareBasis + ", faceFare="
                    + faceFare + ", currentFare=" + currentFare + ", commission="
                    + commission + ", tax=" + tax + ", cnTax=" + cnTax + ", yqTax="
                    + yqTax + ", totalFare=" + totalFare + ", sfc=" + sfc + "]";
        }
    }
}
