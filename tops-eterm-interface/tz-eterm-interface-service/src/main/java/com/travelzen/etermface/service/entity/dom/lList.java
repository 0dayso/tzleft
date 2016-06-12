package com.travelzen.etermface.service.entity.dom;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("lList")
public class lList {
	@XStreamImplicit(itemFieldName = "line")
	private List<Line> line;

	public List<Line> getLine() {
		return line;
	}

	public void setLine(List<Line> line) {
		this.line = line;
	}

}
