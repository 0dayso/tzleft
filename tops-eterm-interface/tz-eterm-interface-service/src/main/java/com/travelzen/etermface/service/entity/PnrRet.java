package com.travelzen.etermface.service.entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.google.common.collect.Lists;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("CRS.CommandSet.PNR")
public class PnrRet {
    public String PNR;
    public String ERRORS;
    public String ERRORS_DETAIL;
    public String PNR_STATUS;
    public String Success;
    public String FlightType;
    public String TicketType;
    public String AgentOffice;
    public String IssueTktOffice;
    public String ERRule;
    public String TicketLimit;
    public String TicketLimitStr;
    public List<String> TripartiteAgreementStrs = Lists.newArrayList();
    public String TripartiteAgreementNum;
    @XStreamAlias("SegPriceInfos")
    public List<SegPriceSyntaxTree> segPrices = Lists.newArrayList();

    public static class AuthOffice {
        public String ElementNo;
        public String No;
    }

    public List<AuthOffice> AuthOffices;

    @XStreamAlias("PassengerInfo")
    public static class PassengerInfo {
        @XStreamAsAttribute
        public String IsGroup = "false";

        public static class Passenger {
            @XStreamAsAttribute
            public String ElementNo;
            @XStreamAsAttribute
            public String PsgID;
            @XStreamAsAttribute
            public String Name;
            @XStreamAsAttribute
            public String Type;
            @XStreamAsAttribute
            public String PsgType;
            @XStreamAsAttribute
            public String CardType;
            @XStreamAsAttribute
            public String CardNo;
            @XStreamAsAttribute
            public String Gender;
            @XStreamAsAttribute
            public String CarrierCard;
            @XStreamAsAttribute
            public String BirthDay;
            @XStreamAsAttribute
            public String CarrierPsgID;
            @XStreamAsAttribute
            public String Country;
            @XStreamAsAttribute
            public String FNNo;
            @XStreamAsAttribute
            public String TicketNo;
            @XStreamAsAttribute
            public String MobilePhone;
            // 称呼
            @XStreamAsAttribute
            public String PsgStyle;
            // 称呼
            @XStreamAsAttribute
            public String PsgInfo;
            @XStreamAsAttribute
            public String FollowPsgId;
        }

        @XStreamImplicit(itemFieldName = "Passenger")
        public List<Passenger> Passengers;
    }

    @XStreamAlias("PassengerInfo")
    public PassengerInfo PassengerInfo;

    // public List<Passenger> Passengers;

    @XStreamAlias("Flight")
    public static class Flight {
        @XStreamAsAttribute
        public String ElementNo;
        @XStreamAsAttribute
        public String ID;
        @XStreamAsAttribute
        public String Type;
        @XStreamAsAttribute
        public String FltType;
        @XStreamAsAttribute
        public String Carrier;
        @XStreamAsAttribute
        public String Flight;
        @XStreamAsAttribute
        public String ShareCarrier;
        @XStreamAsAttribute
        public String ShareFlight;
        @XStreamAsAttribute
        public String BoardPoint;
        @XStreamAsAttribute
        public String OffPoint;
        @XStreamAsAttribute
        public String Week;
        @XStreamAsAttribute
        public String DepartureDate;
        @XStreamAsAttribute
        public String DepartureTime;
        @XStreamAsAttribute
        public String ArriveDate;
        @XStreamAsAttribute
        public String ArriveTime;
        @XStreamAsAttribute
        public String Class;
        @XStreamAsAttribute
        public String ActionCode;
        @XStreamAsAttribute
        public String Seats;
        @XStreamAsAttribute
        public String Meal;
        @XStreamAsAttribute
        public String Stops;
        @XStreamAsAttribute
        public String Avail;
        @XStreamAsAttribute
        public String Night;
        @XStreamAsAttribute
        public String ETKT;
        @XStreamAsAttribute
        public String Changed;
        @XStreamAsAttribute
        public String LinkLevel;
        @XStreamAsAttribute
        public String Allow;
        @XStreamAsAttribute
        public String BoardPointAT;
        @XStreamAsAttribute
        public String OffpointAT;
        @XStreamAsAttribute
        public String SFC;
        @XStreamAsAttribute
        public String IsCodeShare;
        @XStreamAsAttribute
        public String subCabinCode;
    }

    public List<Flight> Flights;

    public static class Contacts {
        @XStreamAsAttribute
        public String No;
    }

    public List<Contacts> Contacts;

    public static class PNR {
        @XStreamAsAttribute
        public String No;
        @XStreamAsAttribute
        public String GDS;
    }

    public List<PNR> PNRs;

    @XStreamAlias("TickeInfo")
    public static class TicketInfo {
        public TicketInfo(String ticketNo, List<String> fltsegIDs, String psgID) {
            super();
            this.ticketNo = ticketNo;
            this.fltsegIDs = fltsegIDs;
            this.psgID = psgID;
            this.isAccurate = false;
        }

        public TicketInfo(String ticketNo, List<String> fltsegIDs, String psgID, boolean isAccurate) {
            super();
            this.ticketNo = ticketNo;
            this.fltsegIDs = fltsegIDs;
            this.psgID = psgID;
            this.isAccurate = isAccurate;
        }

        @XStreamAsAttribute
        public String ticketStatus;
        @XStreamAsAttribute
        public String ticketMode;
        @XStreamAsAttribute
        public String ticketNo;
        @XStreamImplicit(itemFieldName = "fltsegID")
        public List<String> fltsegIDs;
        @XStreamAsAttribute
        public String psgID;
        @XStreamAsAttribute
        public String ticketType;
        @XStreamAsAttribute
        public boolean isAccurate;

        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }

    public List<TicketInfo> TicketInfos = Lists.newArrayList();

    public static class TicketNos {
        public String From;

        public static class TicketNo {
            public String ElementNo;
            public String From;
            public String PsgType;
            public String PsgName;
            public String Remark;
            public String PsgID;
            public String No;
            public String OrdNo;
            public String TicketNo;
        }
    }

    public TicketNos TicketNos;

    @XStreamAlias("SSR")
    public static class SSR {
        public String ElementNo;
        public String Type;
        public String Carrier;
        public String PsgID;
        public String ActionCode;
        public String Seats;
        public String Text;
    }

    public List<SSR> SSRs = new ArrayList<>();

    public static class Segment {
        @XStreamAsAttribute
        public String ElementNo;
        @XStreamAsAttribute
        public String Type;
        @XStreamAsAttribute
        public String Text;
    }

    public List<Segment> Segments;

    @XStreamAlias("FN")
    public static class FN {
        @XStreamAsAttribute
        public String ElementNo;
        @XStreamAsAttribute
        public String PsgType;
        @XStreamAsAttribute
        public String FNNo;

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
        public List<Item> items;
    }

    public List<FN> FNs = new ArrayList<>();

    public String FnStr;

    // beginSSR
    @XStreamAlias("SSR_NODES")
    public static class SSR_NODES {
        public static class SSR_FOID {
            public String FoidStr;
            public String PsgID;
        }

        public static class SSR_FOID_NODE {
            @XStreamImplicit(itemFieldName = "FoidStr")
            public List<String> FoidStr = new ArrayList<>();
        }

        @XStreamImplicit(itemFieldName = "SsrStr")
        public List<String> SsrStr = new ArrayList<>();

        @XStreamImplicit(itemFieldName = "SsrFoid")
        public List<SSR_FOID> SsrFoid = new ArrayList<>();
    }

    // rootSsrNodes
    public SSR_NODES SsrNodes = new SSR_NODES();
    // endSSR


    // beginRMK
    @XStreamAlias("RMK_NODES")
    public static class RMK_NODES {
        public List<String> RmdStr = new ArrayList<>();
    }

    public RMK_NODES RmkNodes = new RMK_NODES();
    // endRMK

    //beginRMK
    @XStreamAlias("BIG_PNR")
    public static class BIG_PNR {
        @XStreamAsAttribute
        public String Carrier;
        @XStreamAsAttribute
        public String PNR;
    }

    public BIG_PNR BigPnr;

    // endRMK

    //beginOSI
    @XStreamAlias("OSI_NODES")
    public static class OSI_NODES {
        public static class OSI_CTCT {
            @XStreamAsAttribute
            public String Carrier;
            @XStreamAsAttribute
            public String PhoneNumber;
        }

        // beginRawStringField
        public static class OSI_CTCT_NODE {
            @XStreamImplicit(itemFieldName = "CTCT")
            public List<String> CtctStr = new ArrayList<>();
        }

        @XStreamImplicit(itemFieldName = "OsiStr")
        public List<String> OsiStr = new ArrayList<>();
        // endRawStringField
        public OSI_CTCT_NODE OsiCtctNode = new OSI_CTCT_NODE();
        @XStreamImplicit(itemFieldName = "OsiCtct")
        public List<OSI_CTCT> OsiCtct = new ArrayList<>();
    }

    // rootOsiNodes
    public OSI_NODES OsiNodes = new OSI_NODES();
    //endOSI


    // beginPriceNodes
    public static class PatResult {
        public String patLineStr;
        public String sfc;
        public String fareBasis;
        public String fare;
        public String tax;
        public String yq;
        public String total;
        public String ob;
        public String fc;
        public String internalTaxs = "";

        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }

    public static class PatItem {
        @XStreamAsAttribute
        public String PsgType;
        // ADT, CHD, INF
        public String PatStr;
        @XStreamImplicit(itemFieldName = "PatResult")
        public List<PatResult> PatResults;
    }

    @XStreamAlias("PRICE_NODES")
    public static class PRICE_NODES {
        @XStreamImplicit(itemFieldName = "PatItem")
        public List<PatItem> PatItem = new ArrayList<>();
    }

    public PRICE_NODES PriceNodes = new PRICE_NODES();
    // endPriceNodes


    // beginXSFSM_NODES
    @XStreamAlias("XsfsmItem")
    public static class XsfsmItem {
        public String CityPair;
        public String TPM;
    }

    public List<XsfsmItem> XsfsmItems = new ArrayList<>();
    // endXSFSM_NODES
}