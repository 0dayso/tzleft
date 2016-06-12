package com.travelzen.etermface.service.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WriteFile {

	private static final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");
	private static String fileName;

//	 private static final String PATH = "C:\\tmp\\pnrs\\";
	private static final String PATH = "/data/pnr/";

	/**
	 * 
	 * @param message
	 */
	public synchronized static void write(String message) {
		Date date = new Date(System.currentTimeMillis());
		fileName = PATH + sdf.format(date).substring(0, 10) + ".txt";
		File f = new File(fileName);
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}// 不存在则创建
		}

		/**
		 * 默认增加方式
		 */
		BufferedWriter output = null;
		try {
			output = new BufferedWriter(new FileWriter(f, true));
			output.write(sdf.format(new Date()) + "\t");
			output.write(message);
			output.newLine();
			output.flush();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		write("abdfgr");
		write("123456");
	}
}
