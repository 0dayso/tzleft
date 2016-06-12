package com.travelzen.etermface.service.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * @author hetao
 */
public class XmlUtils {

	/**
	 * @param obj <T>
	 * @param clazz <T>
	 * @return
	 * when obj or clazz is null return ""<br>
	 * when obj is not a type of clazz return ""<br>
	 */
	public static <T> String toXML(T obj, Class<T> clazz) {
		
		if(null == obj || null == clazz || !obj.getClass().equals(clazz)) {
			return "";
		}
		
		StaxDriver sd = new StaxDriver(new NoNameCoder());
		XStream xstream = new XStream(sd);
//		XStream xstream = new XStream();
		xstream.processAnnotations(clazz);
		
		return xstream.toXML(obj);
	}
	
	/**
	 * @param xml 
	 * @param clazz <T>
	 * @return <T>
	 * when xml error return null<br>
	 * return T
	 */
	@SuppressWarnings("unchecked")
	public static <T> T fromXml(String xml, Class<T> clazz) {
		
		if(null == xml || !xml.startsWith("<") || !xml.endsWith(">") || null == clazz){
			return null;
		}
		
		XStream xstream = new XStream(new DomDriver());
		xstream.processAnnotations(clazz);
		xstream.autodetectAnnotations(true);
		
		return (T) xstream.fromXML(xml);
	}
}
