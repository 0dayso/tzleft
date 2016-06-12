package com.travelzen.etermface.service.util;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * XML工具类
 * <p>
 * @author yiming.yan
 * @Date Dec 14, 2015
 */
public enum XmlUtil {
	
	;
	
	/**
     * 将Java对象序列化为XML字符串
     *
     * @param pojo 待序列化的Java对象
     * @throws JAXBException
     * @return
     */
	public static String toXml(Object pojo) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(pojo.getClass());
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8"); //编码格式
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); //是否格式化生成的XML
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false); // 是否省略XML头声明信息
		StringWriter stringWriter = new StringWriter();
		marshaller.marshal(pojo, stringWriter);
		return stringWriter.toString();
	}
	
	/**
     * 将XML字符串反序列化为Java对象
     *
     * @param xmlStr 待反序列化的XML字符串
     * @param pojoClass　需要反序列化的类型
     * @throws JAXBException
     * @return
     */
	public static <T> Object fromXml(String xmlStr, Class<T> pojoClass) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(pojoClass);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		Object object = unmarshaller.unmarshal(new ByteArrayInputStream(xmlStr.getBytes()));
		return object;
	}

}
