package com.travelzen.rosetta.eterm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.travelzen.rosetta.eterm.common.pojo.EtermRtktResponse;
import com.travelzen.rosetta.eterm.parser.EtermRtktParser;

/**
 * @author yiming.yan
 * @Date Dec 04, 2015
 */
public class RtktTest {
	
	public static void main(String[] args) {
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("src/test/resources/text/rtktText.txt")));
			while ((line = reader.readLine()) != null)
				sb.append(line).append("\n");
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		EtermRtktResponse response = EtermRtktParser.parse(sb.toString(), false);
		System.out.println(response);
	}

}
