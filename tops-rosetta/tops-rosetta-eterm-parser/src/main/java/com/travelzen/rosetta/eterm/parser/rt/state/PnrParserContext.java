package com.travelzen.rosetta.eterm.parser.rt.state;

import java.util.Arrays;

import com.travelzen.rosetta.eterm.common.pojo.EtermRtResponse;

public class PnrParserContext {

	private PnrParserState state;
	private boolean isDomestic;
	private String[] pnrLines;
	private int node;
	private EtermRtResponse etermRtResponse;
	
	public PnrParserState getState() {
		return state;
	}

	public void setState(PnrParserState state) {
		this.state = state;
	}

	public boolean isDomestic() {
		return isDomestic;
	}

	public void setDomestic(boolean isDomestic) {
		this.isDomestic = isDomestic;
	}

	public String[] getPnrLines() {
		return pnrLines;
	}

	public void setPnrLines(String[] pnrLines) {
		this.pnrLines = pnrLines;
	}

	public int getNode() {
		return node;
	}

	public void setNode(int node) {
		this.node = node;
	}

	public EtermRtResponse getEtermRtResponse() {
		return etermRtResponse;
	}

	public void setEtermRtResponse(EtermRtResponse etermRtResponse) {
		this.etermRtResponse = etermRtResponse;
	}

	@Override
	public String toString() {
		return "PnrParserContext [state=" + state + ", isDomestic="
				+ isDomestic + ", pnrLines=" + Arrays.toString(pnrLines)
				+ ", node=" + node + ", etermRtResponse=" + etermRtResponse
				+ "]";
	}
	
}
