package com.travelzen.etermface.service.entity;

import java.util.List;

import com.google.common.collect.Lists;

public class EtermSessionInfo {

    public EtermSessionInfo() {
        super();
    }

    private String pnr;
    private String status;
    private List<String> statusLines = Lists.newArrayList();

    public void addStatusLine(String line) {
        statusLines.add(line);
        status = line;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getStatusLines() {
        return statusLines;
    }

    public void setStatusLines(List<String> statusLines) {
        this.statusLines = statusLines;
    }

    public String getPnr() {
        return pnr;
    }

    public void setPnr(String pnr) {
        this.pnr = pnr;
    }

	@Override
	public String toString() {
		return "EtermSessionInfo [pnr=" + pnr + ", status=" + status
				+ ", statusLines=" + statusLines + "]";
	}

}
