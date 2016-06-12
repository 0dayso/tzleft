package com.travelzen.etermface.service.entity;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Eterm三合一插件DT返回结果类
 * <p>
 * @author yiming.yan
 * @Date Dec 10, 2015
 */
@XmlRootElement(name = "Eterm3in1DtResponse")
public class Eterm3in1DtResponse {
	
	@XmlElement
	public String ErrorReason;
	
	@XmlElement
	public String PNRNO;
	@XmlElement
	public String AIRLINEPNRNO;
	@XmlElement
	public String SYSTEM;
	@XmlElement
	public Tickets TICKETS;
	@XmlElement
	public PassengerList PASSENGERLIST;
	@XmlElement
	public Segments SEGMENTS;
	@XmlElement
	public Agentinfo AGENTINFO;
	@XmlElement
	public Fcs FCS;
	@XmlElement
	public Fares FARES;
	@XmlElement
	public Fps FPS;
	@XmlElement
	public String LANGUAGE;
	@XmlElement
	public Notice NOTICE;
	@XmlElement
	public Advert ADVERT;

	@Override
	public String toString() {
		return "Eterm3in1Response [ErrorReason=" + ErrorReason + ", PNRNO=" + PNRNO
				+ ", AIRLINEPNRNO=" + AIRLINEPNRNO + ", SYSTEM=" + SYSTEM
				+ ", TICKETS=" + TICKETS + ", PASSENGERLIST=" + PASSENGERLIST
				+ ", SEGMENTS=" + SEGMENTS + ", AGENTINFO=" + AGENTINFO
				+ ", FCS=" + FCS + ", FARES=" + FARES + ", FPS=" + FPS
				+ ", LANGUAGE=" + LANGUAGE + ", NOTICE=" + NOTICE + ", ADVERT="
				+ ADVERT + "]";
	}

	@XmlRootElement
	public static class Tickets {
		@XmlElement
		public TicketInfo[] TICKETINFO;

		@Override
		public String toString() {
			return "Tickets [TicketInfo=" + Arrays.toString(TICKETINFO) + "]";
		}
		
		@XmlRootElement
		public static class TicketInfo {
			@XmlElement
			public String TKTN;
			@XmlElement
			public String EXCHTKT;
			@XmlElement
			public String ET;
			@XmlElement
			public String RECEIPTPNT;
			@XmlElement
			public String CONJTKT;
			@XmlElement
			public String TKTSTATUS;
			@XmlElement
			public String ISSUEDBY;
			@XmlElement
			public String ISSUEDDATE;
			@XmlElement
			public String ER;
			
			@Override
			public String toString() {
				return "TicketInfo [TKTN=" + TKTN + ", EXCHTKT=" + EXCHTKT
						+ ", ET=" + ET + ", RECEIPTPNT=" + RECEIPTPNT
						+ ", CONJTKT=" + CONJTKT + ", TKTSTATUS=" + TKTSTATUS
						+ ", ISSUEDBY=" + ISSUEDBY + ", ISSUEDDATE=" + ISSUEDDATE
						+ ", ER=" + ER + "]";
			}
		}
	}
	
	@XmlRootElement
	public static class PassengerList {
		@XmlElement
		public Passenger[] PASSENGER;

		@Override
		public String toString() {
			return "PassengerList [PASSENGER=" + Arrays.toString(PASSENGER)
					+ "]";
		}
		
		@XmlRootElement
		public static class Passenger {
			@XmlElement
			public String NAME;
			@XmlElement
			public IdCard IDCARD;
			
			@Override
			public String toString() {
				return "Passenger [NAME=" + NAME + ", IDCARD=" + IDCARD + "]";
			}

			@XmlRootElement
			public static class IdCard {
				@XmlElement
				public String CARDTYPE;
				@XmlElement
				public String CARDNUMBER;
				
				@Override
				public String toString() {
					return "IdCard [CARDTYPE=" + CARDTYPE + ", CARDNUMBER="
							+ CARDNUMBER + "]";
				}
			}
		}
	}
	
	@XmlRootElement
	public static class Segments {
		@XmlElement
		public Segment[] SEGMENT;

		@Override
		public String toString() {
			return "Segments [SEGMENT=" + Arrays.toString(SEGMENT) + "]";
		}
		
		@XmlRootElement
		public static class Segment {
			@XmlElement
			public Normal NORMAL;
			@XmlElement
			public Open OPEN;
			@XmlElement
			public Arnk ARNK;

			@Override
			public String toString() {
				return "Segment [NORMAL=" + NORMAL + ", OPEN=" + OPEN + ", ARNK=" + ARNK + "]";
			}

			@XmlRootElement
			public static class Normal {
				@XmlElement
				public String AIRLINE;
				@XmlElement
				public String FLIGHTNO;
				@XmlElement
				public String DATE;
				@XmlElement
				public String CLASS;
				@XmlElement
				public String SEATSTATUS;
				@XmlElement
				public String SEGSTATUS;
				@XmlElement
				public String STOPS;
				@XmlElement
				public Legs LEGS;
				
				@Override
				public String toString() {
					return "Normal [AIRLINE=" + AIRLINE + ", FLIGHTNO="
							+ FLIGHTNO + ", DATE=" + DATE + ", CLASS=" + CLASS
							+ ", SEATSTATUS=" + SEATSTATUS + ", SEGSTATUS="
							+ SEGSTATUS + ", STOPS=" + STOPS + ", LEGS=" + LEGS
							+ "]";
				}
				
				@XmlRootElement
				public static class Legs {
					@XmlElement
					public Leg[] LEG;

					@Override
					public String toString() {
						return "Legs [LEG=" + Arrays.toString(LEG) + "]";
					}
					
					@XmlRootElement
					public static class Leg {
						@XmlElement
						public String ORI_TERMINAL;
						@XmlElement
						public String DEST_TERMINAL;
						@XmlElement
						public String ORIGIN;
						@XmlElement
						public String DEST;
						@XmlElement
						public String DEPTIME;
						@XmlElement
						public String DEPDATE;
						@XmlElement
						public String ARRTIME;
						@XmlElement
						public String BAGGAGE;
						@XmlElement
						public String FAREBASIS;
						@XmlElement
						public String NVA;
						@XmlElement
						public String NVB;
						
						@Override
						public String toString() {
							return "Leg [ORI_TERMINAL=" + ORI_TERMINAL
									+ ", DEST_TERMINAL=" + DEST_TERMINAL
									+ ", ORIGIN=" + ORIGIN + ", DEST=" + DEST
									+ ", DEPTIME=" + DEPTIME + ", DEPDATE="
									+ DEPDATE + ", ARRTIME=" + ARRTIME
									+ ", BAGGAGE=" + BAGGAGE + ", FAREBASIS="
									+ FAREBASIS + ", NVA=" + NVA + ", NVB=" + NVB
									+ "]";
						}
					}
				}
			}
			
			@XmlRootElement
			public static class Open {
				@XmlElement
				public String AIRLINE;
				@XmlElement
				public String CLASS;
				@XmlElement
				public String SEATSTATUS;
				@XmlElement
				public String SEGSTATUS;
				@XmlElement
				public String DATE;
				@XmlElement
				public String ORIGIN;
				@XmlElement
				public String DEST;
				@XmlElement
				public String DEPDATE;
				@XmlElement
				public String BAGGAGE;
				@XmlElement
				public String FAREBASIS;
				@XmlElement
				public String NVA;
				@XmlElement
				public String NVB;
				
				@Override
				public String toString() {
					return "Open [AIRLINE=" + AIRLINE + ", CLASS=" + CLASS
							+ ", SEATSTATUS=" + SEATSTATUS + ", SEGSTATUS="
							+ SEGSTATUS + ", DATE=" + DATE + ", ORIGIN="
							+ ORIGIN + ", DEST=" + DEST + ", DEPDATE="
							+ DEPDATE + ", BAGGAGE=" + BAGGAGE + ", FAREBASIS="
							+ FAREBASIS + ", NVA=" + NVA + ", NVB=" + NVB
							+ "]";
				}
			}
			
			@XmlRootElement
			public static class Arnk {
				@XmlElement
				public String DATE;
				@XmlElement
				public String ORIGIN;
				@XmlElement
				public String DEST;
				
				@Override
				public String toString() {
					return "Arnk [DATE=" + DATE + ", ORIGIN=" + ORIGIN
							+ ", DEST=" + DEST + "]";
				}
			}
		}
	}
	
	@XmlRootElement
	public static class Agentinfo {
		@XmlElement
		public String IATANUMBER;
		@XmlElement
		public String NAME;
		@XmlElement
		public ContractInfo CONTACTINFO;

		@Override
		public String toString() {
			return "Agentinfo [IATANUMBER=" + IATANUMBER + ", NAME=" + NAME
					+ ", CONTACTINFO=" + CONTACTINFO + "]";
		}

		public static class ContractInfo {
			@XmlElement
			public String ADDRESS;
			@XmlElement
			public String PHONE;
			@XmlElement
			public String FAX;
			
			@Override
			public String toString() {
				return "ContractInfo [ADDRESS=" + ADDRESS + ", PHONE=" + PHONE
						+ ", FAX=" + FAX + "]";
			}
		}
	}
	
	@XmlRootElement
	public static class Fcs {
		@XmlElement
		public String[] FC;

		@Override
		public String toString() {
			return "Fcs [FC=" + Arrays.toString(FC) + "]";
		}
	}
	
	@XmlRootElement
	public static class Fares {
		@XmlElement
		public Fare FARE;
		@XmlElement
		public FareDetail SFARE;
		
		@Override
		public String toString() {
			return "Fares [FARE=" + FARE + ", SFARE=" + SFARE + "]";
		}

		@XmlRootElement
		public static class Fare {
			@XmlElement
			public FareDetail TICKETFARE;
			@XmlElement
			public FareDetail[] TAX;
			@XmlElement
			public FareDetail TOTALFARE;
			
			@Override
			public String toString() {
				return "Fare [TICKETFARE=" + TICKETFARE + ", TAX="
						+ Arrays.toString(TAX) + ", TOTALFARE=" + TOTALFARE
						+ "]";
			}
		}
		
		@XmlRootElement
		public static class FareDetail {
			@XmlElement
			public String AMOUNT;
			@XmlElement
			public String CURRENCY;
			
			@Override
			public String toString() {
				return "FareDetail [AMOUNT=" + AMOUNT + ", CURRENCY="
						+ CURRENCY + "]";
			}
		}
		
	}
	
	@XmlRootElement
	public static class Fps {
		@XmlElement
		public Fp[] FP;

		@Override
		public String toString() {
			return "Fps [FP=" + Arrays.toString(FP) + "]";
		}
		
		@XmlRootElement
		public static class Fp {
			@XmlElement
			public String PAYMENTTYPE;
			@XmlElement
			public String INFO;
			
			@Override
			public String toString() {
				return "Fp [PAYMENTTYPE=" + PAYMENTTYPE + ", INFO=" + INFO + "]";
			}
		}
	}
	
	@XmlRootElement
	public static class Notice {
		@XmlElement
		public String[] LINE;

		@Override
		public String toString() {
			return "Notice [LINE=" + Arrays.toString(LINE) + "]";
		}
	}
	
	@XmlRootElement
	public static class Advert {
		@XmlElement
		public String[] LINE;

		@Override
		public String toString() {
			return "Advert [LINE=" + Arrays.toString(LINE) + "]";
		}
	}
	
}
