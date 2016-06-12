package com.travelzen.etermface.service.entity.abe;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("CRS.CommandSet.AV1.2")
public class YeeskyAoisAv {

	@XStreamAlias("Item")
	public static class Line {

		@XStreamAlias("ID")
		public String ID;

		@XStreamAlias("FlightID")
		public String FlightID;

		@XStreamAlias("ElementNo")
		public String ElementNo;

		// <FlightType>S</FlightType>
		@XStreamAlias("FlightType")
		public String planeMode;

		// <Carrier>MU</Carrier>
		@XStreamAlias("Carrier")
		public String company;

		// <FlightNo>3929</FlightNo>
		@XStreamAlias("FlightNo")
		public String fNo;

		// 共享航司
		@XStreamAlias("ShareCarrier")
		public String ShareCarrier;

		// 共享航班号
		@XStreamAlias("ShareFlight")
		public String ShareFlight;

		// 出发机场名称
		@XStreamAlias("BoardPoint")
		public String oa;

		// 到达机场名称
		@XStreamAlias("OffPoint")
		public String aa;

		// 出发航站楼
		@XStreamAlias("BoardPointAT")
		public String ot;

		// 到达航站楼
		@XStreamAlias("OffPointAT")
		public String at;

		// 航行时长
		@XStreamAlias("FlightTime")
		public String flightTime;

		// 出发日期
		@XStreamAlias("DepartureDate")
		public String fDate;

		// 出发时间
		@XStreamAlias("DepartureTime")
		public String fTime;

		// 到达日期
		@XStreamAlias("ArrivalDate")
		public String aDate;

		// 到达时间
		@XStreamAlias("ArrivalTime")
		public String aTime;

		@XStreamAlias("Aircraft")
		public String craft;

		// 机场建设费
		@XStreamAlias("AirportTax")
		public String apt;

		@XStreamAlias("Meal")
		public String meal;

		//
		@XStreamAlias("ViaPort")
		public String stop;

		// 是否电子客票
		@XStreamAlias("ETicket")
		public String eticket;

		@XStreamAlias("ASR")
		public String ASR = "^";

		@XStreamAlias("LinkLevel")
		public String LinkLevel = "DS#";

		// 舱位列表
		@XStreamAlias("Classes")
		public Plist plist;

		@XStreamAlias("YClassPrice")
		public String YClassPrice;

		// 燃油费
		@XStreamAlias("FuelSurTax")
		public String aot;

		// 公里数
		@XStreamAlias("TPM")
		public String Mileage;

	}

	@XStreamAlias("Classes")
	public static class Plist {

		@XStreamAlias("Class")
		public static class Position {

			// <Class Code="Y" Ext=" " Seat="A" Price="0" />
			// <Class Code="B" Ext=" " Seat="S" Price="0" />
			// <Class Code="M" Ext=" " Seat="S" Price="0" />
			// <Class Code="E" Ext=" " Seat="S" Price="0" />
			// <Class Code="H" Ext=" " Seat="S" Price="0" />
			// <Class Code="K" Ext=" " Seat="S" Price="0" />

			// <room>经济舱</room>
			// <classStep>Y</classStep>
			// <classCode>Y</classCode>
			// <sysPrice>890.0</sysPrice>
			// <rebate>10.0</rebate>
			// <last>A</last>
			// <rule>
			// <refund>8</refund>
			// <cDate>7</cDate>
			// <turn>略</turn>
			// </rule>

			@XStreamAsAttribute
			@XStreamAlias("Code")
			public String classCode;

			@XStreamAsAttribute
			@XStreamAlias("Ext")
			public String ext;

			@XStreamAsAttribute
			@XStreamAlias("Seat")
			public String last;

			@XStreamAsAttribute
			@XStreamAlias("Price")
			public String sysPrice;

			// @XStreamAlias("classStep")
			// public String classStep;

			// @XStreamAlias("rebate")
			// public String rebate;
			//
			// @XStreamAlias("rule")
			// public Rule rule;
		}

		@XStreamImplicit(itemFieldName = "Class")
		public List<Position> position;

		public List<Position> getPosition() {
			return position;
		}

		public void setPosition(List<Position> position) {
			this.position = position;
		}
	}

	@XStreamImplicit(itemFieldName = "Item")
	public List<Line> line;

}
