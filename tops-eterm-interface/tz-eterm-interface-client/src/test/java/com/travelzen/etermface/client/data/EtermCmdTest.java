package com.travelzen.etermface.client.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.travelzen.rosetta.eterm.common.pojo.EtermRtktResponse;
import com.travelzen.rosetta.eterm.common.pojo.EtermXsFsmResponse;

/**
 * Description
 * <p>
 * @author yiming.yan
 * @Date Mar 3, 2016
 */
public class EtermCmdTest {

	@Test
	public void rtkt() {
		List<List<String>> tktPacks = new ArrayList<List<String>>();
		List<String> tktPack = new ArrayList<String>();
		tktPack.add("0011789661250");
		tktPack.add("0011789661251");
		tktPacks.add(tktPack);
//		tktPacks.add(new ArrayList<String>());
		List<EtermRtktResponse> responses = EtermRtktClient.rtkt("SHA255", false, tktPacks);
		System.out.println(responses);
	}
	
	@Ignore
	@Test
	public void xsfsm() {
		List<String> cities = Arrays.asList("SHA", "DFW", "SHA");
		EtermXsFsmResponse miles = EtermCmdClient.xsfsm(cities);
		System.out.println(miles);
	}

}
