package com.travelzen.pnr;

import org.junit.Ignore;
import org.junit.Test;

import com.travelzen.etermface.common.config.cdxg.exception.SessionExpireException;
import com.travelzen.etermface.service.EtermWebClient;

public class EtermClientTest {

	@Ignore
	@Test
	public void rtTest() {
		@SuppressWarnings("resource")
		EtermWebClient client = new EtermWebClient();
		client.connect();
		try {
			String pnrContent = client.getRT("PNRNUM", false).getObject();
			System.out.println(pnrContent);
			System.out.println("---------");
			System.out.println(client.getRTN(false));
			System.out.println("---------");
//			System.out.println(client.getPAT("A").getObject());
//			System.out.println(client.getPAT("A*CH").getObject());
//			System.out.println(client.getPAT("A*IN").getObject());
//			System.out.println(client.getPAT("A#CGP/CC").getObject());
//			System.out.println(client.getQTE(Arrays.asList("MU"), ""));
//			System.out.println(client.getQTE(Arrays.asList("MU"), "CHD"));
//			System.out.println(client.getQTE(Arrays.asList("MU"), "INF"));
//			System.out.println("---------");
		} catch (SessionExpireException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void cmdTest() {
		@SuppressWarnings("resource")
		EtermWebClient client = new EtermWebClient();
		client.connect();
		try {
			String s = client.executeCmdWithRetry("XS FSM SHA DFW SYD", false).getObject();
			System.out.println(s);
		} catch (SessionExpireException e) {
			e.printStackTrace();
		}
	}
}
