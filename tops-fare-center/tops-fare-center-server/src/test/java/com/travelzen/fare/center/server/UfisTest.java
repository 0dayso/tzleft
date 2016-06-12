package com.travelzen.fare.center.server;

import org.junit.Ignore;
import org.junit.Test;

import com.common.ufis.util.UfisException;
import com.travelzen.fare.center.server.EtermUfisClient;
import com.travelzen.rosetta.eterm.common.pojo.EtermAvResponse;
import com.travelzen.rosetta.eterm.common.pojo.enums.EtermCmdSource;
import com.travelzen.rosetta.eterm.parser.EtermAvParser;

/**
 * @author yiming.yan
 * @Date Nov 17, 2015
 */
public class UfisTest {

	@Ignore
	@Test
	public void avTest() {
		EtermUfisClient client = new EtermUfisClient();
		String result = null;
		try {
			result = client.executeCmdAv("AV:H/SHALAX/12JUN16/AA/D", 4);
		} catch (UfisException e) {
			e.printStackTrace();
		} finally {
			client.close();
		}
		System.out.println(result);
		EtermAvResponse response = EtermAvParser.parse(result, EtermCmdSource.UFIS);
		System.out.println(response);
	}
	
	@Ignore
	@Test
	public void rtTest() {
		EtermUfisClient client = new EtermUfisClient();
		String result = null;
		try {
			result = client.executeCmdRtAll("RT JQJKHQ");
		} catch (UfisException e) {
			e.printStackTrace();
		} finally {
			client.close();
		}
		System.out.println(result);
	}
	
//	@Ignore
	@Test
	public void xTest() {
		EtermUfisClient client = new EtermUfisClient();
		String result = null;
		try {
			result = client.executeCmd("dt 7849755214778 en");
		} catch (UfisException e) {
			e.printStackTrace();
		} finally {
			client.close();
		}
		System.out.println(result);
	}
	
}
