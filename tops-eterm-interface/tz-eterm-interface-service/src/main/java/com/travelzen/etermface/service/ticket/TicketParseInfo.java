package com.travelzen.etermface.service.ticket;

public class TicketParseInfo {

    public String tktNum;
    public int psgIdx;

    // maybe 'IN'
    public String ticketType;
    
    // BSP B2B
    public String ticketMode;

    public String ticketStatus;
    public boolean isAccurate;
    
    public int fltsegIdx ;

    public TicketParseInfo(String ticketNumber, int psgIdx) {
	super();
	this.tktNum = ticketNumber;
	this.psgIdx = psgIdx;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
	// TODO Auto-generated method stub

    }

    @Override
    public String toString() {
	return "TicketParseInfo [tktNum=" + tktNum + ", psgIdx=" + psgIdx + ", ticketTtype=" + ticketType + ", ticketStatus=" + ticketStatus + ", isAccurate="
		+ isAccurate + "]";
    }

}
