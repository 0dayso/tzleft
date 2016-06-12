package com.travelzen.rosetta.eterm.common.pojo.rt;

import java.util.List;


/**
 * 航班信息
 *
 * @author yiming.yan
 */
public class FlightInfo {
    /**
     * 航司列表
     */
    private List<String> carriers;
    /**
     * 航班列表
     */
    private List<Flight> flights;

    public List<String> getCarriers() {
        return carriers;
    }

    public void setCarriers(List<String> carriers) {
        this.carriers = carriers;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

    @Override
    public String toString() {
        return "FlightInfo [carriers=" + carriers + ", flights=" + flights
                + "]";
    }

    /**
     * 单个航班信息
     *
     * @author yiming.yan
     */
    public static class Flight {
        /**
         * 航班类型
         */
        private FlightType type = FlightType.NORMAL;

        public FlightType getType() {
            return type;
        }

        public void setType(FlightType type) {
            this.type = type;
        }

        /**
         * 航司
         */
        private String carrier;
        /**
         * 航班号
         */
        private String flightNo;
        /**
         * 舱位
         */
        private String cabin;
        /**
         * 周几
         */
        private String weekday;
        /**
         * 出发日期
         */
        private String deptDate;
        /**
         * 到达日期
         */
        private String arrDate;
        /**
         * 出发机场
         */
        private String deptAirport;
        /**
         * 到达机场
         */
        private String arrAirport;
        /**
         * 客票状态
         */
        private String status;
        /**
         * 乘客人数
         */
        private String passengerNum;
        /**
         * 出发时间
         */
        private String deptTime;
        /**
         * 到达时间
         */
        private String arrTime;
        /**
         * 跨几天
         */
        private String nights;
        /**
         * 出发航站楼
         */
        private String deptTerminal;
        /**
         * 到达航站楼
         */
        private String arrTerminal;
        /**
         * 子舱位
         */
        private String subCabin;
        /**
         * 是否共享航班
         */
        private boolean isShared = false;
        /**
         * 实际承运航司
         */
        private String opCarrier;
        /**
         * 实际承运航班号
         */
        private String opFlightNo;

        public String getCarrier() {
            return carrier;
        }

        public void setCarrier(String carrier) {
            this.carrier = carrier;
        }

        public String getFlightNo() {
            return flightNo;
        }

        public void setFlightNo(String flightNo) {
            this.flightNo = flightNo;
        }

        public String getCabin() {
            return cabin;
        }

        public void setCabin(String cabin) {
            this.cabin = cabin;
        }

        public String getWeekday() {
            return weekday;
        }

        public void setWeekday(String weekday) {
            this.weekday = weekday;
        }

        public String getDeptDate() {
            return deptDate;
        }

        public void setDeptDate(String deptDate) {
            this.deptDate = deptDate;
        }

        public String getArrDate() {
            return arrDate;
        }

        public void setArrDate(String arrDate) {
            this.arrDate = arrDate;
        }

        public String getDeptAirport() {
            return deptAirport;
        }

        public void setDeptAirport(String deptAirport) {
            this.deptAirport = deptAirport;
        }

        public String getArrAirport() {
            return arrAirport;
        }

        public void setArrAirport(String arrAirport) {
            this.arrAirport = arrAirport;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getPassengerNum() {
            return passengerNum;
        }

        public void setPassengerNum(String passengerNum) {
            this.passengerNum = passengerNum;
        }

        public String getDeptTime() {
            return deptTime;
        }

        public void setDeptTime(String deptTime) {
            this.deptTime = deptTime;
        }

        public String getArrTime() {
            return arrTime;
        }

        public void setArrTime(String arrTime) {
            this.arrTime = arrTime;
        }

        public String getNights() {
            return nights;
        }

        public void setNights(String nights) {
            this.nights = nights;
        }

        public String getDeptTerminal() {
            return deptTerminal;
        }

        public void setDeptTerminal(String deptTerminal) {
            this.deptTerminal = deptTerminal;
        }

        public String getArrTerminal() {
            return arrTerminal;
        }

        public void setArrTerminal(String arrTerminal) {
            this.arrTerminal = arrTerminal;
        }

        public String getSubCabin() {
            return subCabin;
        }

        public void setSubCabin(String subCabin) {
            this.subCabin = subCabin;
        }

        public boolean isShared() {
            return isShared;
        }

        public void setShared(boolean isShared) {
            this.isShared = isShared;
        }

        public String getOpCarrier() {
            return opCarrier;
        }

        public void setOpCarrier(String opCarrier) {
            this.opCarrier = opCarrier;
        }

        public String getOpFlightNo() {
            return opFlightNo;
        }

        public void setOpFlightNo(String opFlightNo) {
            this.opFlightNo = opFlightNo;
        }

        @Override
        public String toString() {
            return "Flight [type=" + type + ", carrier=" + carrier + ", flightNo="
                    + flightNo + ", cabin=" + cabin + ", weekday=" + weekday
                    + ", deptDate=" + deptDate + ", arrDate=" + arrDate
                    + ", deptAirport=" + deptAirport + ", arrAirport=" + arrAirport
                    + ", status=" + status + ", passengerNum=" + passengerNum
                    + ", deptTime=" + deptTime + ", arrTime=" + arrTime
                    + ", nights=" + nights + ", deptTerminal=" + deptTerminal
                    + ", arrTerminal=" + arrTerminal + ", subCabin=" + subCabin
                    + ", isShared=" + isShared + ", opCarrier=" + opCarrier
                    + ", opFlightNo=" + opFlightNo + "]";
        }

    }

    /**
     * 航班类型
     *
     * @author yiming.yan
     */
    public static enum FlightType {
        /**
         * 普通航班
         */
        NORMAL,
        /**
         * 占位航班
         */
        ARNK,
        /**
         * open航班
         */
        OPEN;
    }
}
