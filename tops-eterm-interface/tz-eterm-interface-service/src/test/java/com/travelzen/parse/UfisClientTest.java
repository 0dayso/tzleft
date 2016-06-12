package com.travelzen.parse;

import com.common.ufis.util.UfisException;
import com.travelzen.etermface.service.EtermUfisClient;

/**
 * Description
 * <p>
 * @author yiming.yan
 * @Date Mar 11, 2016
 */
public class UfisClientTest {
	
	public static void main(String[] args) {
		EtermUfisClient client = null;
		String result = null;
		try {
			client = new EtermUfisClient();
			result = client.execAv("AV:SHALON/28AUG", 3);
		} catch (UfisException e) {
			e.printStackTrace();
		} finally {
			client.close();
		}
		System.out.println(result);
	}

}
