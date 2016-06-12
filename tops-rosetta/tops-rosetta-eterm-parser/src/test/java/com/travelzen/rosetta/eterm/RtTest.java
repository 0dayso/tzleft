package com.travelzen.rosetta.eterm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.travelzen.rosetta.eterm.common.pojo.EtermRtResponse;
import com.travelzen.rosetta.eterm.common.pojo.enums.EtermCmdSource;
import com.travelzen.rosetta.eterm.parser.EtermRtParser;

/**
 * @author yiming.yan
 * @Date Dec 04, 2015
 */
public class RtTest {
	
	public static void main(String[] args) {
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("src/test/resources/text/rtText.txt")));
			while ((line = reader.readLine()) != null)
				sb.append(line).append("\n");
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		EtermRtResponse response = EtermRtParser.parse(sb.toString(), true, EtermCmdSource.UFIS);
		System.out.println(response);
	}

}
