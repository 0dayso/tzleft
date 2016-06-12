package com.travelzen.etermface.service.entity.pnr.convertor;

import java.io.Serializable;

import com.thoughtworks.xstream.XStream;
import com.travelzen.etermface.service.createpnr.CreatePnrReturnClass;

/**
 * @author hongqiang.mao
 * 
 * @date 2013-5-7 下午6:45:10
 * 
 * @description PNR状态
 */
public class PnrStatus implements Serializable {
	private static final long serialVersionUID = -8672574897454941902L;
	/**
	 * 错误编号
	 */
	private String errorIndex;
	/**
	 * 是否成功，1为成功，0为失败
	 */
	private String isMakeOK;
	/**
	 * PNR编码
	 */
	private String pnr;
	/**
	 * 生成PNR返回的字符串，可由业务员进行判断
	 */
	private String status;

	public String getErrorIndex() {
		return errorIndex;
	}

	public void setErrorIndex(String errorIndex) {
		this.errorIndex = errorIndex;
	}

	public String getIsMakeOK() {
		return isMakeOK;
	}

	public void setIsMakeOK(String isMakeOK) {
		this.isMakeOK = isMakeOK;
	}

	public String getPnr() {
		return pnr;
	}

	public void setPnr(String pnr) {
		this.pnr = pnr;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public static String convertToXML(PnrStatus pnrStatus) {
		if (null == pnrStatus) {
			pnrStatus = new PnrStatus();
			pnrStatus.setErrorIndex("0");
			pnrStatus.setIsMakeOK("0");
			pnrStatus.setPnr("");
			pnrStatus.setStatus("生成PNR出错");
		}
		XStream xstream = new XStream();
		xstream.processAnnotations(PnrStatus.class);
		xstream.alias("PNRStatus", PnrStatus.class);
		xstream.aliasField("ErrorIndex", PnrStatus.class, "errorIndex");
		xstream.aliasField("IsMakeOK", PnrStatus.class, "isMakeOK");
		xstream.aliasField("PNR", PnrStatus.class, "pnr");
		xstream.aliasField("Status", PnrStatus.class, "status");
		return xstream.toXML(pnrStatus);
	}

	public static PnrStatus convertFromPNRRt(CreatePnrReturnClass<String> rtClass) {
		if (null == rtClass) {
			return null;
		}

		PnrStatus pnrStatus = new PnrStatus();
		pnrStatus.setErrorIndex(String.valueOf(rtClass.getReturnCode().getErrorCode()));
		pnrStatus.setStatus(rtClass.getObject());
		pnrStatus.setPnr(rtClass.getPnr());
		if (null == pnrStatus.getPnr()) {
			pnrStatus.setIsMakeOK("0");
		} else {
			pnrStatus.setIsMakeOK("1");
		}

		if(null == pnrStatus.getStatus()){
			pnrStatus.setStatus(rtClass.getReturnCode().getErrorDetail());
		}
		return pnrStatus;
	}

	@Override
	public String toString() {
		return "PNRStatus [errorIndex=" + errorIndex + ", isMakeOK=" + isMakeOK + ", pnr=" + pnr + ", status=" + status
				+ "]";
	}

	public static void main(String[] args) {
		System.out.println(convertToXML(null));
	}
}
