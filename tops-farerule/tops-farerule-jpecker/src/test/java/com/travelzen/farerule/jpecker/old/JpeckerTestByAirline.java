/**
 * Copyright 2014 Travelzen Inc. All Rights Reserved.
 * Author: yiming.yan@travelzen.com (Yiming Yan)
 */

package com.travelzen.farerule.jpecker.old;

import java.io.*;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;

import com.travelzen.farerule.jpecker.Jpecker;

@RunWith(JUnit4.class)
public class JpeckerTestByAirline {

	@Test
	public void testFiles() {
		long startTime = System.nanoTime();
		
		File f = new File("data/SamplesByAirline/CA");  
        File[] files = f.listFiles();
        
        for (File file:files) {
    		System.out.println("\n" + file.getAbsolutePath());
			StringBuilder sb = new StringBuilder();
			String line = "";
			try {
				BufferedReader br = new BufferedReader(new FileReader(file.getAbsolutePath()));
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
//			Jpecker jpecker = new Jpecker("CA");
//			jpecker.parse(sb.toString());
		}
		
		long endTime = System.nanoTime();
		double runTime = (double)(endTime-startTime)/1000000000;
		System.out.println("程序运行时间： " + runTime + "s");
	}
}
