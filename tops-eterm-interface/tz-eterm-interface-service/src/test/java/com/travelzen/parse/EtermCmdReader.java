package com.travelzen.parse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.travelzen.etermface.common.config.cdxg.exception.SessionExpireException;
import com.travelzen.etermface.service.EtermWebClient;

@RunWith(JUnit4.class)
public class EtermCmdReader {
	
	@Ignore
	@Test
	public void read() {
		EtermWebClient client = new EtermWebClient();
		client.connect(60000);
		try {
			StringBuilder text = new StringBuilder();
			BufferedReader reader = new BufferedReader(new FileReader("src/test/resources/cmd/cmds.txt")); //输入地址
			int count = 0;
			String line;
			while ((line = reader.readLine()) != null) {
				client.extendSessionExpireMillsec(30000, "new cmd");
				text.append(line).append("\n~~~~~~~~~~~~\n");
				String result = client.executeCmd(line, false).getObject();
				text.append(result).append("\n~~~~~~~~~~~~\n");
				count++;
				System.out.print(".");
				if (count%100 == 0) {
					System.out.print("\n");
					Thread.sleep(3000);
				}
				if (count%500 == 0) {
					client.close();
					System.out.println("#");
					Thread.sleep(5000);
					client = new EtermWebClient();
					client.connect(60000);
				}
			}
			reader.close();
			
			BufferedWriter writer = new BufferedWriter(new FileWriter("src/test/resources/cmd/results.txt")); //输出地址
			writer.write(text.toString());
			writer.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			client.close();
		} catch (SessionExpireException see) {
			see.printStackTrace();
			client.close();
		} catch (InterruptedException ie) {
			ie.printStackTrace();
			client.close();
		}
		client.close();
	}

	@Ignore
	@Test
	public void breakLine() {
		try {
			StringBuilder text = new StringBuilder();
			BufferedReader reader = new BufferedReader(new FileReader("src/test/resources/cmd/results.txt"));
			String line;
			while ((line = reader.readLine()) != null) {
				text.append(line).append("\n");
			}
			reader.close();
			
			String t = text.toString().replaceAll("\\*([\\d ][\\+\\- ] | \\*\\*)", "\\*\n$1");
			
			BufferedWriter writer = new BufferedWriter(new FileWriter("src/test/resources/cmd/results_fixed.txt")); //输出地址
			writer.write(t);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
