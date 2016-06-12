package com.travelzen.farerule.jpecker.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;

import com.travelzen.farerule.jpecker.server.JpeckerService;

public class JpeckerClientTmp {

	public static void main(String[] args) {
		try {
			TTransport transport = new THttpClient("http://localhost:8080/tops-farerule-jpecker-server/jpecker");
			transport.open();
			
			TProtocol protocol = new TCompactProtocol(transport);
			JpeckerService.Client client = new JpeckerService.Client(protocol);
			
			System.out.println(client);
			List<String> list = new ArrayList<String>();
			list.add("5827e05e1c6b720a7dd57ddcd86e3495");
			System.out.println(client.fareRulePeck(list));
			
			transport.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
