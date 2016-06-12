package com.travelzen.etermface.service.util;

import java.io.Writer;
import java.util.Arrays;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Sets;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;

public class CDataPrettyPrintWriter extends PrettyPrintWriter {
	public CDataPrettyPrintWriter(Writer writer, NameCoder replacer) {
		super(writer, replacer);
	}

	final Set<String> cdateFieldSet = Sets.newHashSet();

	{
		cdateFieldSet.addAll(Arrays.asList("rawRtInfo", "rtInfo", "rawQteInfo", "qteInfo", "rawPatInfo", "patInfo"));
	}

	boolean cdata = false;

	public void startNode(String name, Class clazz) {
		super.startNode(name, clazz);
		cdata = cdateFieldSet.contains(name);
	}

	protected void writeText(QuickWriter writer, String text) {
		if (cdata && StringUtils.isNotEmpty(text)) {
			writer.write("<![CDATA[");
			writer.write(text);
			writer.write("]]>");
		} else {
			writer.write(text);
		}
	}
};
