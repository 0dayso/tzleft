package com.travelzen.eterm3in1;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.junit.Ignore;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.travelzen.etermface.common.pojo.eterm3in1.UfisDtResponse;
import com.travelzen.etermface.common.pojo.eterm3in1.UfisPidResponse;
import com.travelzen.etermface.common.pojo.eterm3in1.UfisVbiResponse;
import com.travelzen.etermface.common.pojo.eterm3in1.UfisViResponse;
import com.travelzen.etermface.service.entity.Eterm3in1DtResponse;
import com.travelzen.etermface.service.handler.Eterm3in1PrintHandler;
import com.travelzen.etermface.service.util.XmlUtil;
import com.travelzen.tops.ufis.database.entity.Pid;

/**
 * @author yiming.yan
 * @Date Dec 10, 2015
 */
public class XmlTest {
	
	@Ignore
	@Test
	public void Pid() {
		try {
			StringBuilder text = new StringBuilder();
			BufferedReader reader = new BufferedReader(new FileReader("src/test/resources/eterm3in1/pid"));
			String line;
			while ((line = reader.readLine()) != null) {
				text.append(line).append("\n");
			}
			reader.close();
			System.out.println(text);
			
			UfisPidResponse response = Eterm3in1PrintHandler.convPidResponse(text.toString());
			System.out.println(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//IT/RP DELETED SUCCESSFULLY! NUMBER: 4719297514
	//<ErrorReason>该发票号已经作废，无需再次作废！</ErrorReason>
	@Ignore
	@Test
	public void vi() {
		String text = "IT/RP DELETED SUCCESSFULLY! NUMBER: 4719297514";
		UfisViResponse response = Eterm3in1PrintHandler.convViResponse(text);
		System.out.println(response);
	}
	
	//Void Success!
	//<ErrorReason>该发票号已作废！</ErrorReason>
	@Ignore
	@Test
	public void vbi() {
		String text = "Void Success!";
		UfisVbiResponse response = Eterm3in1PrintHandler.convVbiResponse(text);
		System.out.println(response);
	}
	
//	@Ignore
	@Test
	public void dt() {
		try {
			StringBuilder text = new StringBuilder();
			text.append("<?xml version=\"1.0\" ?>\n");
			text.append("<Eterm3in1DtResponse>\n");
			BufferedReader reader = new BufferedReader(new FileReader("src/test/resources/eterm3in1/dt"));
			String line;
			while ((line = reader.readLine()) != null) {
				text.append(line).append("\n");
			}
			text.append("</Eterm3in1DtResponse>");
			reader.close();
//			System.out.println(text);
			
//			XStream xStream = new XStream(new DomDriver());
//			xStream.processAnnotations(Eterm3in1Response.class);
//			Eterm3in1Response response = (Eterm3in1Response) xStream.fromXML(text.toString());
//			System.out.println(response);
//			
//			String xml = xStream.toXML(response);
//			System.out.println(xml);
			
//			Document document = DocumentHelper.parseText(text.toString());
//			System.out.println(document.asXML());
			
			JAXBContext jaxbContext = JAXBContext.newInstance(Eterm3in1DtResponse.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			Eterm3in1DtResponse eterm3in1DtResponse = (Eterm3in1DtResponse) unmarshaller.unmarshal(new ByteArrayInputStream(text.toString().getBytes()));
			System.out.println(eterm3in1DtResponse);
			
			Eterm3in1DtResponse s = (Eterm3in1DtResponse) XmlUtil.fromXml(text.toString(), Eterm3in1DtResponse.class);
			System.out.println(s);
			
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8"); //编码格式
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); //是否格式化生成的XML
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false); // 是否省略XML头声明信息
			StringWriter stringWriter = new StringWriter();
			marshaller.marshal(eterm3in1DtResponse, stringWriter);
			System.out.println(stringWriter.toString());
			
			UfisDtResponse ufisDtResponse = Eterm3in1PrintHandler.convDtResponse(eterm3in1DtResponse);
			System.out.println(ufisDtResponse);
		} catch (IOException e) {
			e.printStackTrace();
//		} catch (DocumentException e) {
//			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

}
