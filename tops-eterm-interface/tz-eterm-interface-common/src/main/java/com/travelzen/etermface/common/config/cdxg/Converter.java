/*
 * Copyright 2013 Travelzen Inc. All Rights Reserved.
 * Author: TBD
 */
package com.travelzen.etermface.common.config.cdxg;

import java.io.UnsupportedEncodingException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.travelzen.etermface.common.config.cdxg.pojo.Ins;
import com.travelzen.framework.core.common.ReturnCode;
import com.travelzen.framework.core.exception.BizException;

public class Converter {

	public static String hexToString(String hex) {
		if (hex == null || (hex.length() % 2 != 0)) {
			return null;
		}
		char[] hex2char = hex.toCharArray();
		byte[] bytes = new byte[hex.length() / 2];
		int temp;
		for (int i = 0; i < bytes.length; i++) {
			String value = hex.substring(2 * i, 2 * i + 2);
			temp = Integer.valueOf(value, 16);
			bytes[i] = (byte) (temp & 0xff);
		}
		String rs = null;
		try {
			// rs = new String(bytes,"ISO646-US");
			rs = new String(bytes, "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return rs;

	}

	public static String stringToHex(String cmd) {
		if (cmd == null) {
			return null;
		}
		byte[] bytes = null;
		try {
			// bytes = cmd.getBytes("ISO646-US");
			bytes = cmd.getBytes("GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = "";
		for (int i = 0; i < bytes.length; i++) {
			result += Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

	public static Ins generateIns(String str, boolean isIns) {
		Ins ins = new Ins();
		XStream xstream = new XStream(new StaxDriver());
		xstream.processAnnotations(Ins.class);
		xstream.alias("ins", Ins.class);
		if (str.contains("<cmd ")) {
			str = str.replaceAll("<cmd ", "<ins ");
			str = str.replaceAll("</cmd>", "</ins>");
		}
		str = str.replaceAll("ret_value", "retValue");
		ins = new Ins();
		try {
			ins = (Ins) xstream.fromXML(str);
		} catch (StreamException e) {
			throw BizException.instance(ReturnCode.E_DATA_VALIDATION_ERROR, str);
		}

		String rs = ins.getRsValue();
		if (isIns) {
			rs = hexToString(rs);
		}
		ins.setRsValue(rs);
		return ins;
	}
}
