package com.travelzen.farerule.jpecker;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class JpeckerTest {

	@Test
	public void test() {
		List<String> list = new ArrayList<String>();
		list.add("5827e05e1c6b720a7dd57ddcd86e3495");
		list.add("bd9d18043a490b752fa3281d511ae42c");
		
		Jpecker jpecker = new Jpecker();
		jpecker.peck(list);
		System.out.println(jpecker.getTzRule());
		System.out.println(jpecker.getDisplayRule());
	}
}
