package com.travelzen.rosetta.eterm.common.pojo;

import java.util.Set;

import com.travelzen.rosetta.eterm.common.pojo.rt.BigPnr;
import com.travelzen.rosetta.eterm.common.pojo.rt.FlightInfo;
import com.travelzen.rosetta.eterm.common.pojo.rt.MileageCardInfo;
import com.travelzen.rosetta.eterm.common.pojo.rt.OriginalText;
import com.travelzen.rosetta.eterm.common.pojo.rt.PassengerInfo;
import com.travelzen.rosetta.eterm.common.pojo.rt.PatInfo;
import com.travelzen.rosetta.eterm.common.pojo.rt.PnrStatus;

/**
 * Eterm RT　指令解析结果类
 * <p>
 * @author yiming.yan
 * @Date Dec 04, 2015
 */
public class EtermRtResponse {

	/**
	 * PNR状态
	 */
	private PnrStatus status;
	/**
	 * PNR号
	 */
	private String pnr;
	/**
	 * 乘客信息
	 */
	private PassengerInfo passengerInfo;
	/**
	 * 航班信息
	 */
	private FlightInfo flightInfo;
	/**
	 * 里程卡信息
	 */
	private Set<MileageCardInfo> mileageCardInfos;
	/**
	 * 出票时间限制（yyyy-MM-dd）
	 */
	private String ticketLimit;
	/**
	 * 联系信息
	 */
	private Set<String> contacts;
	/**
	 * 授权的office
	 */
	private Set<String> authOffices;
	/**
	 * 大编码
	 */
	private Set<BigPnr> bigPnrs;
	/**
	 * office号
	 */
	private String officeId;
	/**
	 * PAT报价
	 */
	private PatInfo patInfo;
	/**
	 * PNR文本
	 */
	private OriginalText originalText = new OriginalText();
	/**
	 * ERROR信息
	 */
	private String error;
	
	public EtermRtResponse() {
	}
	
	public EtermRtResponse(PnrStatus status) {
		this.status = status;
	}
	
	public EtermRtResponse(String pnr) {
		this.pnr = pnr;
	}
	
	public EtermRtResponse(PnrStatus status, String pnr) {
		this.status = status;
		this.pnr = pnr;
	}
	
	public PnrStatus getStatus() {
		return status;
	}

	public void setStatus(PnrStatus status) {
		this.status = status;
	}

	public String getPnr() {
		return pnr;
	}

	public void setPnr(String pnr) {
		this.pnr = pnr;
	}

	public Set<BigPnr> getBigPnrs() {
		return bigPnrs;
	}

	public void setBigPnrs(Set<BigPnr> bigPnrs) {
		this.bigPnrs = bigPnrs;
	}

	public PassengerInfo getPassengerInfo() {
		return passengerInfo;
	}

	public void setPassengerInfo(PassengerInfo passengerInfo) {
		this.passengerInfo = passengerInfo;
	}

	public FlightInfo getFlightInfo() {
		return flightInfo;
	}

	public void setFlightInfo(FlightInfo flightInfo) {
		this.flightInfo = flightInfo;
	}

	public Set<MileageCardInfo> getMileageCardInfos() {
		return mileageCardInfos;
	}

	public void setMileageCardInfos(Set<MileageCardInfo> mileageCardInfos) {
		this.mileageCardInfos = mileageCardInfos;
	}

	public String getTicketLimit() {
		return ticketLimit;
	}

	public void setTicketLimit(String ticketLimit) {
		this.ticketLimit = ticketLimit;
	}
	
	public Set<String> getContacts() {
		return contacts;
	}

	public void setContacts(Set<String> contacts) {
		this.contacts = contacts;
	}

	public Set<String> getAuthOffices() {
		return authOffices;
	}

	public void setAuthOffices(Set<String> authOffices) {
		this.authOffices = authOffices;
	}

	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	public PatInfo getPatInfo() {
		return patInfo;
	}

	public void setPatInfo(PatInfo patInfo) {
		this.patInfo = patInfo;
	}

	public OriginalText getOriginalText() {
		return originalText;
	}

	public void setOriginalText(OriginalText originalText) {
		this.originalText = originalText;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public String toString() {
		return "PnrInfo [status=" + status + ", pnr=" + pnr
				+ ", passengerInfo=" + passengerInfo + ", flightInfo="
				+ flightInfo + ", mileageCardInfos=" + mileageCardInfos
				+ ", ticketLimit=" + ticketLimit + ", contacts=" + contacts
				+ ", authOffices=" + authOffices + ", bigPnrs=" + bigPnrs
				+ ", officeId=" + officeId + ", patInfo=" + patInfo
				+ ", originalText=" + originalText + ", error=" + error + "]";
	}

}
