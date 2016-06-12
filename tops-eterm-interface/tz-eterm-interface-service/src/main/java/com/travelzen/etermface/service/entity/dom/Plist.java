package com.travelzen.etermface.service.entity.dom;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("pList")
public class Plist {
	@XStreamImplicit(itemFieldName = "position")
	private List<Position> position;

	public List<Position> getPosition() {
		return position;
	}

	public void setPosition(List<Position> position) {
		this.position = position;
	}

}
