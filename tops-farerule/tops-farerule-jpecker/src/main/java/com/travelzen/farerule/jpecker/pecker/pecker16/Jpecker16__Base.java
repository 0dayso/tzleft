/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.jpecker.pecker.pecker16;

import java.util.ArrayList;
import java.util.List;

import com.travelzen.farerule.Penalties;
import com.travelzen.farerule.jpecker.pecker.JpeckerBase;
import com.travelzen.farerule.rule.PenaltiesItem;

public class Jpecker16__Base extends JpeckerBase {
	
	protected Penalties penalties = new Penalties();
	
	public Penalties getPenalties() {
		return penalties;
	}
	
	protected List<PenaltiesItem> penaltiesItemList = new ArrayList<PenaltiesItem>();

}
