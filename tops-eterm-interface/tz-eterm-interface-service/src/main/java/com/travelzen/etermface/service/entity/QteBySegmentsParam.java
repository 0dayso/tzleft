package com.travelzen.etermface.service.entity;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.StaxDriver;

@XStreamAlias("QteBySegmentsParam")
public class QteBySegmentsParam {

    /**
     * 航班信息
     * 
     */
    private List<FlightParam> qteParams;
    /**
     * 出票航司
     */
    private List<String> ticketingAirlineCompany;

    /**
     * 乘客类型；成人：ADT；儿童：CHD；婴儿：INF；
     */
    private List<String> passengers;
    private String qteBySegmentsParamKey;

    public List<FlightParam> getQteParams() {
        return qteParams;
    }

    public void setQteParams(List<FlightParam> qteParams) {
        this.qteParams = qteParams;
    }

    public List<String> getTicketingAirlineCompany() {
        if (ticketingAirlineCompany == null) {
            ticketingAirlineCompany = new ArrayList<String>();
        }
        return ticketingAirlineCompany;
    }

    public void setTicketingAirlineCompany(List<String> ticketingAirlineCompany) {
        this.ticketingAirlineCompany = ticketingAirlineCompany;
    }

    public List<String> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<String> passengers) {
        this.passengers = passengers;
    }

    public String getQteBySegmentsParamKey() {
        return qteBySegmentsParamKey;
    }

    public void setQteBySegmentsParamKey(String qteBySegmentsParamKey) {
        this.qteBySegmentsParamKey = qteBySegmentsParamKey;
    }

    @Override
    public String toString() {
        return "QteBySegmentsParam [qteParams=" + qteParams + ", ticketingAirlineCompany=" + ticketingAirlineCompany + ", passengers=" + passengers + ", qteBySegmentsParamKey="
                + qteBySegmentsParamKey + "]";
    }

    /**
     * 从xml转化为object
     * 
     * @param pXml
     * @return
     */
    @SuppressWarnings("unchecked")
    public static QteBySegmentsParam convertFromXML(String pXml) {
        if (null == pXml) {
            return null;
        }
        StaxDriver sd = new StaxDriver(new NoNameCoder());
        XStream xstream = new XStream(sd);
        xstream.processAnnotations(QteBySegmentsParam.class);
        xstream.processAnnotations(List.class);
        xstream.processAnnotations(FlightParam.class);
        xstream.alias("qteParams", List.class);
        xstream.alias("passengers", List.class);
        xstream.alias("ticketingAirlineCompany", List.class);
        return (QteBySegmentsParam) xstream.fromXML(pXml);
    }

    /**
     * 将对象转化为XML
     * 
     * @param pPatParams
     * @return
     */
    public static String convertToXML(QteBySegmentsParam qteBySegmentsParam) {
        StaxDriver sd = new StaxDriver(new NoNameCoder());
        XStream xstream = new XStream(sd);
        xstream.processAnnotations(QteBySegmentsParam.class);
        xstream.processAnnotations(List.class);
        xstream.processAnnotations(FlightParam.class);
        xstream.alias("qteParams", List.class);
        xstream.alias("passengers", List.class);
        xstream.alias("ticketingAirlineCompany", List.class);
        return xstream.toXML(qteBySegmentsParam);
    }
}
