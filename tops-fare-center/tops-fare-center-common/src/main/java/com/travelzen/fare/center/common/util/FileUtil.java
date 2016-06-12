package com.travelzen.fare.center.common.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 文件读写工具类
 * <p>
 * @author yiming.yan
 * @Date Oct 21, 2015
 */
public enum FileUtil {
	
	;
	
	public static String read(String path) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(path));
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line).append("\n");
		}
		reader.close();
		return sb.toString();
	}
	
	public static void write(String input, String path) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
		writer.write(input);
		writer.close();
	}

}
