package com.travelzen.rosetta.eterm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.travelzen.rosetta.eterm.common.pojo.EtermAvResponse;
import com.travelzen.rosetta.eterm.common.pojo.enums.EtermCmdSource;
import com.travelzen.rosetta.eterm.parser.EtermAvParser;

/**
 * @author yiming.yan
 * @Date Nov 17, 2015
 */
public class AvTest {
	
	public static void main(String[] args) {
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("src/test/resources/text/avhText.txt")));
			while ((line = reader.readLine()) != null)
				sb.append(line).append("\r");
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		EtermAvResponse response = EtermAvParser.parse(sb.toString(), true, EtermCmdSource.UFIS);
		System.out.println(response);
		System.out.println(response.getFlights().size());
	}

}
