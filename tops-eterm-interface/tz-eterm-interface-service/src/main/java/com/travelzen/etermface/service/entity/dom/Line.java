package com.travelzen.etermface.service.entity.dom;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("line")
public class Line {
	@XStreamAlias("oa")
	private String oa;
	@XStreamAlias("ot")
	private String ot;
	@XStreamAlias("aa")
	private String aa;
	@XStreamAlias("at")
	private String at;
	@XStreamAlias("fDate")
	private String fDate;
	@XStreamAlias("fTime")
	private String fTime;
	@XStreamAlias("aDate")
	private String aDate;
	@XStreamAlias("aTime")
	private String aTime;
	@XStreamAlias("fNo")
	private String fNo;
	@XStreamAlias("craft")
	private String craft;
	@XStreamAlias("planeName")
	private String planeName;
	@XStreamAlias("leastSeats")
	private String leastSeats;
	@XStreamAlias("maxSeats")
	private String maxSeats;
	@XStreamAlias("planeMode")
	private String planeMode;
	@XStreamAlias("company")
	private String company;
	@XStreamAlias("companyName")
	private String companyName;
	@XStreamAlias("fromName")
	private String fromName;
	@XStreamAlias("fromAirPortName")
	private String fromAirPortName;
	@XStreamAlias("arrivalName")
	private String arrivalName;
	@XStreamAlias("arrivalAirPortName")
	private String arrivalAirPortName;
	@XStreamAlias("apt")
	private String apt;
	@XStreamAlias("aot")
	private String aot;
	@XStreamAlias("stop")
	private String stop;
	@XStreamAlias("yPrice")
	private String yPrice;
	@XStreamAlias("fPrice")
	private String fPrice;
	@XStreamAlias("cPrice")
	private String cPrice;
	@XStreamAlias("mileage")
	private String mileage;
	@XStreamAlias("eticket")
	private String eticket;
	@XStreamAlias("meal")
	private String meal;
	@XStreamAlias("pList")
	private Plist plist;

	public String getOa() {
		return oa;
	}

	public void setOa(String oa) {
		this.oa = oa;
	}

	public String getOt() {
		return ot;
	}

	public void setOt(String ot) {
		this.ot = ot;
	}

	public String getAa() {
		return aa;
	}

	public void setAa(String aa) {
		this.aa = aa;
	}

	public String getAt() {
		return at;
	}

	public void setAt(String at) {
		this.at = at;
	}

	public String getfDate() {
		return fDate;
	}

	public void setfDate(String fDate) {
		this.fDate = fDate;
	}

	public String getaDate() {
		return aDate;
	}

	public void setaDate(String aDate) {
		this.aDate = aDate;
	}

	public String getaTime() {
		return aTime;
	}

	public void setaTime(String aTime) {
		this.aTime = aTime;
	}

	public String getfNo() {
		return fNo;
	}

	public void setfNo(String fNo) {
		this.fNo = fNo;
	}

	public String getCraft() {
		return craft;
	}

	public void setCraft(String craft) {
		this.craft = craft;
	}

	public String getPlaneName() {
		return planeName;
	}

	public void setPlaneName(String planeName) {
		this.planeName = planeName;
	}

	public String getLeastSeats() {
		return leastSeats;
	}

	public void setLeastSeats(String leastSeats) {
		this.leastSeats = leastSeats;
	}

	public String getMaxSeats() {
		return maxSeats;
	}

	public void setMaxSeats(String maxSeats) {
		this.maxSeats = maxSeats;
	}

	public String getPlaneMode() {
		return planeMode;
	}

	public void setPlaneMode(String planeMode) {
		this.planeMode = planeMode;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public String getFromAirPortName() {
		return fromAirPortName;
	}

	public void setFromAirPortName(String fromAirPortName) {
		this.fromAirPortName = fromAirPortName;
	}

	public String getArrivalName() {
		return arrivalName;
	}

	public void setArrivalName(String arrivalName) {
		this.arrivalName = arrivalName;
	}

	public String getArrivalAirPortName() {
		return arrivalAirPortName;
	}

	public void setArrivalAirPortName(String arrivalAirPortName) {
		this.arrivalAirPortName = arrivalAirPortName;
	}

	public String getApt() {
		return apt;
	}

	public void setApt(String apt) {
		this.apt = apt;
	}

	public String getAot() {
		return aot;
	}

	public void setAot(String aot) {
		this.aot = aot;
	}

	public String getStop() {
		return stop;
	}

	public void setStop(String stop) {
		this.stop = stop;
	}

	public String getyPrice() {
		return yPrice;
	}

	public void setyPrice(String yPrice) {
		this.yPrice = yPrice;
	}

	public String getfPrice() {
		return fPrice;
	}

	public void setfPrice(String fPrice) {
		this.fPrice = fPrice;
	}

	public String getcPrice() {
		return cPrice;
	}

	public void setcPrice(String cPrice) {
		this.cPrice = cPrice;
	}

	public String getMileage() {
		return mileage;
	}

	public void setMileage(String mileage) {
		this.mileage = mileage;
	}

	public String getEticket() {
		return eticket;
	}

	public void setEticket(String eticket) {
		this.eticket = eticket;
	}

	public String getMeal() {
		return meal;
	}

	public String getfTime() {
		return fTime;
	}

	public void setfTime(String fTime) {
		this.fTime = fTime;
	}

	public void setMeal(String meal) {
		this.meal = meal;
	}

	public Plist getPlist() {
		return plist;
	}

	public void setPlist(Plist plist) {
		this.plist = plist;
	}

	// private String date;
	//
	// public String getDate() {
	// SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
	// this.date = sf.format(new Date());
	// return date;
	// }

}
