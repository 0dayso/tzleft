package com.travelzen.rosetta.eterm.common.pojo.rt;

/**
 * 部分PNR原始文本
 *
 * @author yiming.yan
 */
public class OriginalText {
	
	private String psg;
	private String flt;
    private String ssrFoid;
    private String ssrFqtv;
    private String ssrAdtk;
    private String ssrTkne;
    private String ssrDocs;
    private String ssrInft;
    private String ssrChld;
    private String osi;
    private String rmk;
    private String xn;
    private String pat;
    private String office;
    
	public String getPsg() {
		return psg;
	}

	public void setPsg(String psg) {
		this.psg = psg;
	}

	public String getFlt() {
		return flt;
	}

	public void setFlt(String flt) {
		this.flt = flt;
	}

	public String getSsrFoid() {
		return ssrFoid;
	}
	
	public void setSsrFoid(String ssrFoid) {
		this.ssrFoid = ssrFoid;
	}
	
	public String getSsrFqtv() {
		return ssrFqtv;
	}
	
	public void setSsrFqtv(String ssrFqtv) {
		this.ssrFqtv = ssrFqtv;
	}
	
	public String getSsrAdtk() {
		return ssrAdtk;
	}
	
	public void setSsrAdtk(String ssrAdtk) {
		this.ssrAdtk = ssrAdtk;
	}
	
	public String getSsrTkne() {
		return ssrTkne;
	}
	
	public void setSsrTkne(String ssrTkne) {
		this.ssrTkne = ssrTkne;
	}
	
	public String getSsrDocs() {
		return ssrDocs;
	}
	
	public void setSsrDocs(String ssrDocs) {
		this.ssrDocs = ssrDocs;
	}
	
	public String getSsrInft() {
		return ssrInft;
	}
	
	public void setSsrInft(String ssrInft) {
		this.ssrInft = ssrInft;
	}
	
	public String getSsrChld() {
		return ssrChld;
	}
	
	public void setSsrChld(String ssrChld) {
		this.ssrChld = ssrChld;
	}
	
	public String getOsi() {
		return osi;
	}
	
	public void setOsi(String osi) {
		this.osi = osi;
	}
	
	public String getRmk() {
		return rmk;
	}
	
	public void setRmk(String rmk) {
		this.rmk = rmk;
	}
	
	public String getXn() {
		return xn;
	}
	
	public void setXn(String xn) {
		this.xn = xn;
	}
	
	public String getPat() {
		return pat;
	}
	
	public void setPat(String pat) {
		this.pat = pat;
	}

	public String getOffice() {
		return office;
	}

	public void setOffice(String office) {
		this.office = office;
	}

	@Override
	public String toString() {
		return "OriginalText [psg=" + psg + ", flt=" + flt + ", ssrFoid="
				+ ssrFoid + ", ssrFqtv=" + ssrFqtv + ", ssrAdtk=" + ssrAdtk
				+ ", ssrTkne=" + ssrTkne + ", ssrDocs=" + ssrDocs
				+ ", ssrInft=" + ssrInft + ", ssrChld=" + ssrChld + ", osi="
				+ osi + ", rmk=" + rmk + ", xn=" + xn + ", pat=" + pat
				+ ", office=" + office + "]";
	}

}
