package com.travelzen.etermface.service.entity;

import java.util.List;

import com.google.common.collect.Lists;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

///**
// * <?xml version="1.0" encoding="utf-8"?>
//
////<Unilink_MakeFareByPNR> 
////  <PNR>HGY2G6/byc1;byc11234</PNR> -
////  <Fare_Tax> 
////    <StandPrice>1</StandPrice>  
////    <BasePrice>2840</BasePrice>  
////    <Tax>995</Tax>  
////    <SalePrice>2840</SalePrice>  
////    <PriceIncludeCommision>3835</PriceIncludeCommision>  
////    <QValue>0</QValue>  
////    <CHDBaseFare>2330</CHDBaseFare>  
////    <CHDTax>905</CHDTax>  
////    <CHDTotalFare>3235</CHDTotalFare>  
////    <QCHDValue>0</QCHDValue>  
////    <INFBaseFare>2840</INFBaseFare>  
////    <INFTax>50</INFTax>  
////    <INFTotalFare>2890</INFTotalFare>  
////    <QINFValue>0</QINFValue>  
////    <Passenger>YANG/XIUJUN,CN/E05727254/CN/01NOV58/F/07NOV22</Passenger>  
////    <AirCo>CZ</AirCo>  
////    <IsSp>0</IsSp>  
////    <SpNo/> 
////  </Fare_Tax>  
////  <Ticket_Limit>2013-03-26 23:00</Ticket_Limit> -
////  <FlightInfo>-
////    <FlightSegment number="1"> 
////      <FromCity>SHE</FromCity>  
////      <DestCity>PUS</DestCity>  
////      <FromDate>2013-03-29</FromDate>  
////      <FromTime>0740</FromTime>  
////      <DestDate>2013-03-29</DestDate>  
////      <DestTime>1030</DestTime>  
////      <FlightNumber>CZ665</FlightNumber>  
////      <CW>T</CW>  
////      <IsCodeShare>0</IsCodeShare> 
////    </FlightSegment> -
////    <FlightSegment number="2"> 
////      <FromCity>PUS</FromCity>  
////      <DestCity>SHE</DestCity>  
////      <FromDate>2013-04-08</FromDate>  
////      <FromTime>1130</FromTime>  
////      <DestDate>2013-04-08</DestDate>  
////      <DestTime>1235</DestTime>  
////      <FlightNumber>CZ666</FlightNumber>  
////      <CW>E</CW>  
////      <IsCodeShare>0</IsCodeShare> 
////    </FlightSegment> 
////  </FlightInfo> 
////</Unilink_MakeFareByPNR>
//
//
// * @author liang.wang
// *
// */

//
//XML节点名称	中文名称	参数类型	是否必须	描述
//<Unilink_MakeFareByPNR >			   	
//<PNR>	Pnr和eterm的用户名和密码	String	   	HX775S是pnr号，byc1;byc11234是eterm的用户和密码
//<Fare_Tax>				
//<StandPrice>	标识	String		票面价标识
//< BasePrice>	成人净价	String		不含税，不含Q，含代理费
//< SalePrice >	成人净价			不含税，不含Q，含代理费
//< Tax>	成人税	String		不含Q值
//< PriceIncludeCommision >	成人总价	String		含税含代理费不含Q值
//< QValue >	成人Q值	String		
//< CHDBaseFare>	儿童净价	String		不含税，不含Q，含代理费
//< CHDTax>	儿童税	String		不含Q值
//< CHDTotalFare>	儿童总价	String		含税含代理费不含Q值
//< QCHDValue>	儿童Q值	String		
//< INFBaseFare>	婴儿净价	String		不含税，不含Q，含代理费
//< INFTax>	婴儿税	String		不含Q值
//< INFTotalFare>	婴儿总价	String		含税含代理费不含Q值
//< QINFValue>	婴儿Q值	String		
//<Passenger>	人名格式和旅客信息	String		<Passenger>LI/JUN（名字）,CN（发证国家）/1234567（证件号）/CN（国籍）/09SEP90（出生日期）/M（性别）/09SEP18（证件有效期）;LIDS/DSD,CN/7654321/CN/09SEP90/M/09SEP18</Passenger>
//<AirCo>	主航空公司	String		
//<IsSp>	是否特价	String		Pnr 导出价格里没有特价之分，飞狗备用数据
//<SpNo>	特价编号	String		飞狗备用数据
//</Fare_Tax>				
//<Ticket_Limit>	出票时限			格式如：
//2012-12-12 14:34
//<FlightInfo>				
//<FlightSegment number="">	number是航段数			
//<FromCity>	出发城市	String		
//<DestCity>	到达城市	String		
//<FromDate>	出发日期	String		
//<FromTime>	出发时间	String		
//<DestTime>	达到时间	String		
//<FlightNumber>	航班号	String		
//<CW>	舱位	String		
//<IsCodeShare>	航班共享	String		0为普通航班，1为共享航班
//</FlightSegment>				
//…….				
//</FlightInfo>				
//</Unilink_MakeFareByPNR>	

@XStreamAlias("Unilink_MakeFareByPNR")
public class MakeFareByPNR {

    public String PNR;

    public String Error;
    public String ErrorDetail;
    public String AgentOffice;
    public String IsGROUP;

    public static class DebugInfo {
	// debugLevel=1
	public String EtermfaceTid;

	// debugLevel=2
	public String RawRtInfo;
	public String RtInfo;

	// debugLevel=3
	public String RawQteInfo;
	public String QteInfo;

	public String RawPatInfo;
	public String PatInfo;

	public String TimeLimitError;

	public static class TimeLimitProceedStrsList {
	    @XStreamImplicit(itemFieldName = "TimeLimitProceedStrs")
	    public List<String> timeLimitProceedStrs = Lists.newArrayList();
	}

	@XStreamAlias("TimeLimitProceedStrsList")
	public TimeLimitProceedStrsList timeLimitProceedStrsList = new TimeLimitProceedStrsList();

    }

    public static class FareTax {
	public String StandPrice;
	public String BasePrice;
	public String SalePrice;
	public String TotalFare;

	public String Tax;
	public String PriceIncludeCommision;
	public String QValue;

	public String CHDBaseFare;
	public String CHDTax;
	public String CHDTotalFare;
	// public String CHDFareIncludeCommision;

	public String QCHDValue;
	public String INFBaseFare;
	public String INFTax;
	public String INFTotalFare;
	public String QINFValue;

	// 多个乘客
	// @XStreamImplicit(itemFieldName="Passenger")
	// public List<String> Passengers = Lists.newArrayList();
	public String Passenger;
	// >YANG/XIUJUN,CN/E05727254/CN/01NOV58/F/07NOV22</Passenger>

	public String AirCo;
	public String IsSp;
	public String SpNo;
    }

    @XStreamAlias("Fare_Tax")
    private FareTax fareTax = new FareTax();

    public FareTax getFareTax() {
	return fareTax;
    }

    public void setFareTax(FareTax fareTax) {
	this.fareTax = fareTax;
    }

    @XStreamAlias("Ticket_Limit")
    public String Ticket_Limit;
    @XStreamAlias("Ticket_Limit_Str")
    public String Ticket_Limit_Str;

    public static class FlightInfo {

	public static class FlightSegment {
	    @XStreamAsAttribute
	    public String number;

	    public String FromCity;
	    public String DestCity;
	    public String FromDate;
	    public String FromTime;

	    public String DestDate;
	    public String DestTime;
	    public String FlightNumber;
	    public String RealFlightNumber;
	    public String CW;
	    public String IsCodeShare;

	    public String ActionCode;

	    public String Status;

	}

	@XStreamImplicit(itemFieldName = "FlightSegment")
	public List<FlightSegment> flightSegments;

    }

    @XStreamAlias("FlightInfo")
    public FlightInfo flightInfo = new FlightInfo();

    @XStreamAlias("DebugInfo")
    public DebugInfo debugInfo = new DebugInfo();

    public void addTimeLimitProceedStr(String str) {
	debugInfo.timeLimitProceedStrsList.timeLimitProceedStrs.add(str);
    }

}
