package com.travelzen.rosetta.eterm.parser.rt.state;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travelzen.rosetta.eterm.common.pojo.rt.*;
import com.travelzen.rosetta.eterm.parser.rt.subparser.*;

public enum PnrParserStateMachine implements PnrParserState {
	
	// 客票状态
	STATUS {
		@Override
		public boolean process(PnrParserContext context) {
			int newNode = StateMachineUtil.makeNode(context, STATUS);
			StringBuilder text = new StringBuilder();
			for (int i = context.getNode(); i < newNode; i++)
				text.append(context.getPnrLines()[i]).append("\n");
			logger.info("PNR状态解析文本：{}", text);
			PnrStatus status = PnrStatusParser.parse(text.toString());
			logger.info("PNR状态解析结果：{}", status);
			context.getEtermRtResponse().setStatus(status);
			if (context.getEtermRtResponse().getStatus() == PnrStatus.NO_PNR || 
					context.getEtermRtResponse().getStatus() == PnrStatus.UNAUTHORIZED || 
					context.getEtermRtResponse().getStatus() == PnrStatus.CANCELLED)
				return false;
			context.setNode(newNode);
			context.setState(PASSENGER);
			return true;
		}
	},
	// 乘客信息
	PASSENGER {
		@Override
		public boolean process(PnrParserContext context) {
			int newNode = StateMachineUtil.makeNode(context, PASSENGER);
			StringBuilder text = new StringBuilder();
			for (int i = context.getNode(); i < newNode; i++)
				text.append(context.getPnrLines()[i]).append("\n");
			context.getEtermRtResponse().getOriginalText().setPsg(text.toString());
			logger.info("乘客信息解析文本：{}", text);
			if (context.getEtermRtResponse().getPnr() == null) {
				String pnrNo = PnrNoParser.parse(text.toString());
				if (pnrNo != null)
					context.getEtermRtResponse().setPnr(pnrNo);
			}
			logger.info("PNR号解析结果：{}", context.getEtermRtResponse().getPnr());
			PassengerInfo passengerInfo = PassengerInfoParser.parse(
					text.toString(), context.isDomestic());
			if (passengerInfo.getPassengers().size() == 0) {
				context.getEtermRtResponse().setError("乘客信息解析异常！");
				logger.error("乘客信息解析异常：{}", text.toString());
				return false;
			} else {
				context.getEtermRtResponse().setPassengerInfo(passengerInfo);
			}
			logger.info("乘客信息解析结果：{}", passengerInfo);
			context.setNode(newNode);
			context.setState(FLIGHT);
			return true;
		}
	},
	// 航班信息
	FLIGHT {
		@Override
		public boolean process(PnrParserContext context) {
			int newNode = StateMachineUtil.makeNode(context, FLIGHT);
			StringBuilder text = new StringBuilder();
			for (int i = context.getNode(); i < newNode; i++)
				text.append(context.getPnrLines()[i]).append("\n");
			context.getEtermRtResponse().getOriginalText().setFlt(text.toString());
			logger.info("航班信息解析文本：{}", text);
			FlightInfo flightInfo = FlightInfoParser.parse(
					text.toString(), context.isDomestic());
			logger.info("航班信息解析结果：{}", flightInfo);
			if (flightInfo.getFlights().size() != 0)
				context.getEtermRtResponse().setFlightInfo(flightInfo);
			context.setNode(newNode);
			context.setState(AGENT);
			return true;
		}
	},
	AGENT {
		@Override
		public boolean process(PnrParserContext context) {
			int newNode = StateMachineUtil.makeNode(context, AGENT);
			StringBuilder text = new StringBuilder();
			for (int i = context.getNode(); i < newNode; i++)
				text.append(context.getPnrLines()[i]).append("\n");
//			System.out.println(text);
			// XXX not sure whether needed?
			context.setNode(newNode);
			context.setState(CONTACT);
			return true;
		}
	},
	// 乘客联系信息
	CONTACT {
		@Override
		public boolean process(PnrParserContext context) {
			int newNode = StateMachineUtil.makeNode(context, CONTACT);
			/*
			Set<String> contacts = new HashSet<String>();
			for (int i = context.getNode(); i < newNode; i++) {
				String text = context.getPnrLines()[i];
				logger.info("联系信息解析文本：{}", text);
				String contact = ContactInfoParser.parse(text);
				if (contact != null && !contacts.contains(contact)) {
					contacts.add(contact);
				}
			}
			logger.info("联系信息解析结果：{}", contacts);
			if (contacts.size() != 0)
				context.getEtermRtResponse().setContacts(contacts);
			*/
			context.setNode(newNode);
			context.setState(SSR);
			return true;
		}
	},
	SSR {
		@Override
		public boolean process(PnrParserContext context) {
			int newNode = StateMachineUtil.makeNode(context, SSR);
			if (newNode == context.getNode()) {
				context.setState(OSI);
				return true;
			}
			StringBuilder text = new StringBuilder();
			for (int i = context.getNode(); i < newNode; i++)
				text.append(context.getPnrLines()[i]).append("\n");
			OriginalText originalText = SsrParser.parse(
					context.getEtermRtResponse().getOriginalText(), text.toString());
			context.getEtermRtResponse().setOriginalText(originalText);
			context.setNode(newNode);
			context.setState(SSR_FOID);
			return true;
		}
	},
	// SSR FOID 证件信息
	SSR_FOID {
		@Override
		public boolean process(PnrParserContext context) {
			if (context.getEtermRtResponse().getOriginalText().getSsrFoid() != null) {
				String text = context.getEtermRtResponse().getOriginalText().getSsrFoid();
				logger.info("SSR FOID 证件信息解析文本：{}", text);
				PassengerInfo passengerInfo = SsrFoidParser.parse(
						text, context.getEtermRtResponse().getPassengerInfo());
				logger.info("SSR FOID 证件信息解析结果：{}", passengerInfo);
				context.getEtermRtResponse().setPassengerInfo(passengerInfo);
			}
			context.setState(SSR_FQTV);
			return true;
		}
	},
	// SSR　里程卡信息
	SSR_FQTV {
		@Override
		public boolean process(PnrParserContext context) {
			if (context.getEtermRtResponse().getOriginalText().getSsrFqtv() != null) {
				String text = context.getEtermRtResponse().getOriginalText().getSsrFqtv();
				logger.info("SSR　里程卡信息解析文本：{}", text);
				Set<MileageCardInfo> mileageCardInfos = SsrFqtvParser.parse(
						text.toString(), context.isDomestic());
				logger.info("SSR　里程卡信息解析结果：{}", mileageCardInfos);
				if (mileageCardInfos != null)
					context.getEtermRtResponse().setMileageCardInfos(mileageCardInfos);
			}
			context.setState(SSR_ADTK);
			return true;
		}
	},
	// SSR 出票时间限制
	SSR_ADTK {
		@Override
		public boolean process(PnrParserContext context) {
			if (context.getEtermRtResponse().getOriginalText().getSsrAdtk() != null) {
				String text = context.getEtermRtResponse().getOriginalText().getSsrAdtk();
				logger.info("SSR 出票时间限制解析文本：{}", text);
				String ticketLimit = null;
				try {
					ticketLimit = SsrAdtkParser.parse(
							text.toString(), context.isDomestic());
				} catch (Exception e) {
					context.getEtermRtResponse().setError("出票时间限制解析异常！");
					logger.error("出票时间限制解析异常：{}" + text.toString(), e);
				}
				logger.info("SSR 出票时间限制解析结果：{}", ticketLimit);
				if (ticketLimit != null)
					context.getEtermRtResponse().setTicketLimit(ticketLimit);
			}
			context.setState(SSR_TKNE);
			return true;
		}
	},
	// SSR 票号信息
	SSR_TKNE {
		@Override
		public boolean process(PnrParserContext context) {
			if (context.getEtermRtResponse().getOriginalText().getSsrTkne() != null) {
				String text = context.getEtermRtResponse().getOriginalText().getSsrTkne();
				logger.info("SSR 票号信息解析文本：{}", text);
				PassengerInfo passengerInfo = SsrTkneParser.parse(
						text.toString(), context.getEtermRtResponse().getPassengerInfo());
				// 二次判断PNR status，若有票号，则已出票
				if (passengerInfo.getTicketInfos() != null && 
						passengerInfo.getTicketInfos().size() != 0 && 
						context.getEtermRtResponse().getStatus() == PnrStatus.RAW_TICKET)
					context.getEtermRtResponse().setStatus(PnrStatus.ELECTRONIC_TICKET);
				logger.info("SSR 票号信息解析结果：{}", passengerInfo);
				context.getEtermRtResponse().setPassengerInfo(passengerInfo);
			}
			context.setState(SSR_DOCS);
			return true;
		}
	},
	// SSR DOCS　证件信息
	SSR_DOCS {
		@Override
		public boolean process(PnrParserContext context) {
			if (context.getEtermRtResponse().getOriginalText().getSsrDocs() != null) {
				String text = context.getEtermRtResponse().getOriginalText().getSsrDocs();
				logger.info("SSR DOCS　证件信息解析文本：{}", text);
				PassengerInfo passengerInfo = SsrDocsParser.parse(
						text.toString(), context.getEtermRtResponse().getPassengerInfo());
				logger.info("SSR DOCS　证件信息解析结果：{}", passengerInfo);
				context.getEtermRtResponse().setPassengerInfo(passengerInfo);
			}
			context.setState(SSR_INFT);
			return true;
		}
	},
	// SSR INFT　婴儿信息
	SSR_INFT {
		@Override
		public boolean process(PnrParserContext context) {
			if (context.getEtermRtResponse().getOriginalText().getSsrInft() != null) {
				String text = context.getEtermRtResponse().getOriginalText().getSsrInft();
				logger.info("SSR INFT　婴儿信息解析文本：{}", text);
				PassengerInfo passengerInfo = SsrInftParser.parse(
						text.toString(), context.getEtermRtResponse().getPassengerInfo());
				logger.info("SSR INFT　婴儿信息解析结果：{}", passengerInfo);
				context.getEtermRtResponse().setPassengerInfo(passengerInfo);
			}
			context.setState(SSR_CHLD);
			return true;
		}
	},
	// SSR CHLD　儿童信息
	SSR_CHLD {
		@Override
		public boolean process(PnrParserContext context) {
			if (context.getEtermRtResponse().getOriginalText().getSsrChld() != null) {
				String text = context.getEtermRtResponse().getOriginalText().getSsrChld();
				logger.info("SSR CHLD　儿童信息解析文本：{}", text);
				PassengerInfo passengerInfo = SsrChldParser.parse(
						text.toString(), context.getEtermRtResponse().getPassengerInfo());
				logger.info("SSR CHLD　儿童信息解析结果：{}", passengerInfo);
				context.getEtermRtResponse().setPassengerInfo(passengerInfo);
			}
			context.setState(OSI);
			return true;
		}
	},
	// OSI　信息
	OSI {
		@Override
		public boolean process(PnrParserContext context) {
			if (context.getNode() >= context.getPnrLines().length)
				return false;
			int newNode = StateMachineUtil.makeNode(context, OSI);
			if (newNode != context.getNode()) {
				StringBuilder text = new StringBuilder();
				for (int i = context.getNode(); i < newNode; i++)
					text.append(context.getPnrLines()[i]).append("\n");
				context.getEtermRtResponse().getOriginalText().setOsi(text.toString());
				logger.info("OSI　信息解析文本：{}", text);
				// OSI　联系信息
				Set<String> contacts = OsiCtctParser.parse(
						text.toString(), context.isDomestic());
				logger.info("OSI　联系信息解析结果：{}", contacts);
				if (contacts.size() != 0 && context.getEtermRtResponse().getContacts() == null)
					context.getEtermRtResponse().setContacts(contacts);
				// OSI　票号信息 (仅在SSR TKNE无票号信息时解析)
				PassengerInfo passengerInfo = OsiTnParser.parse(
						text.toString(), context.getEtermRtResponse().getPassengerInfo());
				logger.info("OSI　票号信息解析结果：{}", passengerInfo);
				context.getEtermRtResponse().setPassengerInfo(passengerInfo);
			}
			context.setNode(newNode);
			context.setState(RMK);
			return true;
		}
	},
	// RMK 信息
	RMK {
		@Override
		public boolean process(PnrParserContext context) {
			if (context.getNode() >= context.getPnrLines().length)
				return false;
			int newNode = StateMachineUtil.makeNode(context, RMK);
			if (newNode != context.getNode()) {
				StringBuilder text = new StringBuilder();
				for (int i = context.getNode(); i < newNode; i++)
					text.append(context.getPnrLines()[i]).append("\n");
				context.getEtermRtResponse().getOriginalText().setRmk(text.toString());
				logger.info("RMK 信息解析文本：{}", text);
				// RMK 联系信息
				/*
				Set<String> contacts = ContactInfoParser.parseRmk(
					text.toString(), context.getEtermRtResponse().getContacts());
				logger.info("RMK 联系信息解析结果：{}", contacts);
				if (contacts.size() != 0 && context.getEtermRtResponse().getContacts() == null)
					context.getEtermRtResponse().setContacts(contacts);
				*/
				// RMK　授权航司
				Set<String> authOffices = AuthOfficeParser.parse(text.toString());
				logger.info("RMK　授权航司解析结果：{}", authOffices);
				if (authOffices.size() != 0)
					context.getEtermRtResponse().setAuthOffices(authOffices);
				// RMK　PNR大编码
				// XXX what if flightInfo is null？
				if (context.getEtermRtResponse().getFlightInfo() == null) {
					context.setNode(newNode);
					context.setState(FN);
					return true;
				}
				Set<BigPnr> bigPnrs = BigPnrParser.parse(
					text.toString(), context.getEtermRtResponse().getFlightInfo().getCarriers());
				logger.info("RMK　PNR大编码解析结果：{}", bigPnrs);
				if (bigPnrs.size() != 0)
					context.getEtermRtResponse().setBigPnrs(bigPnrs);
			}
			context.setNode(newNode);
			context.setState(FN);
			return true;
		}
	},
	FN {
		@Override
		public boolean process(PnrParserContext context) {
			if (context.getNode() >= context.getPnrLines().length)
				return false;
			int newNode = StateMachineUtil.makeNode(context, FN);
			// No need for FN/A price now
			/*
			if (newNode != context.getNode()) {
				StringBuilder text = new StringBuilder();
				for (int i = context.getNode(); i < newNode; i++)
					text.append(context.getPnrLines()[i]).append("\n");
//				logger.info("FN价格解析文本：{}", text);
				// FN/A 价格信息
				FnInfo fnInfo = FnInfoParser.parse(
						text.toString(), context.isDomestic(), context.getEtermRtResponse().getPassengerInfo());
//				logger.info("FN价格解析结果：{}", fnInfo);
				if (fnInfo != null)
					context.getEtermRtResponse().setFnInfo(fnInfo);
			}
			*/
			context.setNode(newNode);
			context.setState(XN);
			return true;
		}
	},
	// XN 婴儿信息
	XN {
		@Override
		public boolean process(PnrParserContext context) {
			if (context.getNode() >= context.getPnrLines().length)
				return false;
			int newNode = StateMachineUtil.makeNode(context, XN);
			if (newNode != context.getNode()) {
				StringBuilder text = new StringBuilder();
				for (int i = context.getNode(); i < newNode; i++)
					text.append(context.getPnrLines()[i]).append("\n");
				context.getEtermRtResponse().getOriginalText().setXn(text.toString());
				logger.info("XN 婴儿信息解析文本：{}", text);
				PassengerInfo passengerInfo = XnInfoParser.parse(
						text.toString(), context.getEtermRtResponse().getPassengerInfo(), context.isDomestic());
				logger.info("XN 婴儿信息解析结果：{}", passengerInfo);
				context.getEtermRtResponse().setPassengerInfo(passengerInfo);
			}
			context.setNode(newNode);
			context.setState(OFFICE);
			return true;
		}
	},
	// OFFICE号
	OFFICE {
		@Override
		public boolean process(PnrParserContext context) {
			if (context.getNode() >= context.getPnrLines().length)
				return false;
			int newNode = StateMachineUtil.makeNode(context, OFFICE);
			if (newNode != context.getNode()) {
				String text = context.getPnrLines()[context.getNode()];
				context.getEtermRtResponse().getOriginalText().setOffice(text);
				logger.info("OFFICE号解析文本：{}", text);
				String officeId = OfficeIdParser.parse(text);
				logger.info("OFFICE号解析结果：{}", officeId);
				if (officeId != null)
					context.getEtermRtResponse().setOfficeId(officeId);
			}
			if (context.isDomestic()) {
				context.setNode(newNode);
				context.setState(PAT);
				return true;
			}
			return false;
		}
	},
	// PAT 报价信息
	PAT {
		@Override
		public boolean process(PnrParserContext context) {
			if (context.getNode() >= context.getPnrLines().length)
				return false;
			StringBuilder text = new StringBuilder();
			for (int i = context.getNode(); i < context.getPnrLines().length; i++)
				text.append(context.getPnrLines()[i]).append("\n");
			context.getEtermRtResponse().getOriginalText().setPat(text.toString());
			logger.info("PAT 报价信息解析文本：{}", text);
			PatInfo patInfo = PatInfoParser.parse(text.toString());
			logger.info("PAT 报价信息解析结果：{}", patInfo);
			if (null != patInfo)
				context.getEtermRtResponse().setPatInfo(patInfo);
			return false;
		}
	};
	
	private static Logger logger = LoggerFactory.getLogger(PnrParserStateMachine.class);

}

enum StateMachineUtil {
	
	;
	
	private static final Pattern pre_psg = Pattern.compile("^[01]\\.|^ *00\\d ");
	private static final Pattern pre_flt = Pattern.compile("^[0-9]{1,3}\\.(?: [ \\*](?:[A-Z0-9]{2}[A-Z0-9]| {2}ARNK|[A-Z0-9 ]{2}OPEN)|[A-Z]{3}/T )|^[>\\(]?PAT:A");
	private static final Pattern pre_agt = Pattern.compile("^[0-9]{1,3}\\.(?:[A-Z]{3}/T |[\\d-]{3}|SSR|OSI|RMK|FN|XN|[A-Z]{3}\\d{3}(?:\\n|$))|^[>\\(]?PAT:A");
	private static final Pattern pre_ctt = Pattern.compile("^[0-9]{1,3}\\.(?:[\\d-]{3}|SSR|OSI|RMK|FN|XN|[A-Z]{3}\\d{3}(?:\\n|$))|^[>\\(]?PAT:A");
	private static final Pattern pre_ssr = Pattern.compile("^[0-9]{1,3}\\.(?:SSR|OSI|RMK|FN|XN|[A-Z]{3}\\d{3}(?:\\n|$))|^[>\\(]?PAT:A");
	private static final Pattern pre_osi = Pattern.compile("^[0-9]{1,3}\\.(?:OSI|RMK|FN|XN|[A-Z]{3}\\d{3}(?:\\n|$))|^[>\\(]?PAT:A");
	private static final Pattern pre_rmk = Pattern.compile("^[0-9]{1,3}\\.(?:RMK|FN|XN|[A-Z]{3}\\d{3}(?:\\n|$))|^[>\\(]?PAT:A");
	private static final Pattern pre_fn = Pattern.compile("^[0-9]{1,3}\\.(?:FN|XN|[A-Z]{3}\\d{3}(?:\\n|$))|^[>\\(]?PAT:A");
	private static final Pattern pre_xn = Pattern.compile("^[0-9]{1,3}\\.(?:XN|[A-Z]{3}\\d{3}(?:\\n|$))|^[>\\(]?PAT:A");
	private static final Pattern pre_off = Pattern.compile("^[0-9]{1,3}\\.[A-Z]{3}\\d{3}(?:\\n|$)|^[>\\(]?PAT:A");
	private static final Pattern pre_pat = Pattern.compile("^[>\\(]?PAT:A");
	
	public static int makeNode(PnrParserContext context, PnrParserStateMachine state) {
		Pattern pattern = null;
		switch (state) {
		case STATUS:
			pattern = pre_psg;
			break;
		case PASSENGER:
			pattern = pre_flt;
			break;
		case FLIGHT:
			pattern = pre_agt;
			break;
		case AGENT:
			pattern = pre_ctt;
			break;
		case CONTACT:
			pattern = pre_ssr;
			break;
		case SSR:
			pattern = pre_osi;
			break;
		case OSI:
			pattern = pre_rmk;
			break;
		case RMK:
			pattern = pre_fn;
			break;
		case FN:
			pattern = pre_xn;
			break;
		case XN:
			pattern = pre_off;
			break;
		case OFFICE:
			pattern = pre_pat;
			break;
		default:
			return context.getNode();
		}
		int newNode;
		for (newNode = context.getNode(); newNode < context.getPnrLines().length; newNode++) {
			String line = context.getPnrLines()[newNode];
			Matcher matcher = pattern.matcher(line);
			if (matcher.find()) {
				break;
			}
		}
		return newNode;
	}
	
}
