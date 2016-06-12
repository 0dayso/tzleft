package com.travelzen.etermface.service.entity;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

//<?xml version="1.0" encoding="gb2312"?><CRS.CommandSet.PNR><Success></Success><Errors /><PNR></PNR><FlightType>D</FlightType>
//<TicketType>NONE</TicketType><AgentOffice></AgentOffice><AuthOffices /><Passengers>
//<Passenger ElementNo="1" PsgID="1" Name="蔡康" Type="2" PsgType="CHD" CardType="" CardNo="" Gender="" CarrierCard="" BirthDay="" CarrierPsgID="" Country="" FNNo="" TicketNo="" />
//</Passengers><Flights><Flight ElementNo="2" ID="1" Type="0" FltType="" Carrier="HO" Flight="1123" ShareCarrier="" ShareFlight="" BoardPoint="SHA" OffPoint="CSX" Week="WE" DepartureDate="2013-08-28" DepartureTime="07:40" ArriveDate="2013-08-28" ArriveTime="09:25" Class="Q" ActionCode="DK" Seats="1" Meal="" Stops="" Avail="" Night="" ETKT="3" Changed="" LinkLevel="" Allow="20K" BoardPointAT="" OffpointAT="" />
//</Flights><Contacts><Contacts No="SHA/T SHA/T021-62277798/BU YE CHENG BOOKING OFFICE/JINHUA ABCDEFG" /><Contacts No="SHA255" /></Contacts>
//<PNRs /><TicketNos /><RMKs /><SSRs /><OSIs /><Segments />

//<CRS.CommandSet.PNR>
//<FNs>
//<FN ElementNo="999" FNNo="1" PsgType="ADT" PriceType="" PriceNo="01" Farebasis="YQ" F="360.00" T="160" A="520.00">
//<Item Type="F" Currency="CNY" Value="360.00" ExtType="" />
//<Item Type="C" Currency="CNY" Value="0.00" ExtType="" />
//<Item Type="X" Currency="CNY" Value="160" ExtType="CN" />
//<Item Type="T" Currency="CNY" Value="50.00" ExtType="CN" />
//<Item Type="T" Currency="CNY" Value="110.00" ExtType="YQ" />
//<Item Type="A" Currency="CNY" Value="520.00" ExtType="" />
//</FN>
//<FN ElementNo="999" FNNo="1" PsgType="ADT" PriceType="" PriceNo="02" Farebasis="YQ" F="360.00" T="160" A="520.00">
//<Item Type="F" Currency="CNY" Value="360.00" ExtType="" />
//<Item Type="C" Currency="CNY" Value="0.00" ExtType="" />
//<Item Type="X" Currency="CNY" Value="160" ExtType="CN" />
//<Item Type="T" Currency="CNY" Value="50.00" ExtType="CN" />
//<Item Type="T" Currency="CNY" Value="110.00" ExtType="YQ" />
//<Item Type="A" Currency="CNY" Value="520.00" ExtType="" />
//</FN>
//<FN ElementNo="999" FNNo="3" PsgType="INF" PriceType="IN" PriceNo="01" Farebasis="YIN" F="90.00" T="0" A="90.00">
//<Item Type="F" Currency="CNY" Value="90.00" ExtType="" />
//<Item Type="C" Currency="CNY" Value="0.00" ExtType="" />
//<Item Type="X" Currency="CNY" Value="0" ExtType="CN" />
//<Item Type="T" Currency="CNY" Value="0" ExtType="CN" />
//<Item Type="T" Currency="CNY" Value="0" ExtType="YQ" />
//<Item Type="A" Currency="CNY" Value="90.00" ExtType="" />
//</FN>
//</FNs>
//</CRS.CommandSet.PNR>

@XStreamAlias("CRS.CommandSet.PNR")
public class PnrRet4Price {

    @XStreamAlias("FN")
    public static class FN {

	@XStreamAsAttribute
	public String ElementNo;
	@XStreamAsAttribute
	public String PsgType;
	@XStreamAsAttribute
	public String FNNo;

	@XStreamAsAttribute
	public String PriceType;

	@XStreamAsAttribute
	public String Farebasis;

	@XStreamAsAttribute
	public String F;
	@XStreamAsAttribute
	public String T;
	@XStreamAsAttribute
	public String A;

	// <Item Type="F" Currency="CNY" Value="2300.00" ExtType=""/>
	public static class Item {
	    @XStreamAsAttribute
	    public String Type;
	    @XStreamAsAttribute
	    public String Currency;
	    @XStreamAsAttribute
	    public String ExtType;

	    @XStreamAsAttribute
	    public String Value;
	}

	@XStreamImplicit(itemFieldName = "item")
	public List<Item> items = new ArrayList<>();
    }

    public List<FN> FNs = new ArrayList<>();

}
