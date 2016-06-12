package com.travelzen.etermface.service.entity;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.StaxDriver;
@XStreamAlias("PnrContentResponse")
public class PnrContentResponse {
	private String returnCode;
	private String content;
	public String getReturnCode() {
		return returnCode;
	}
	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@Override
	public String toString() {
		return "PnrContentResponse [returnCode=" + returnCode + ", content=" + content + "]";
	}
	 /**
     * 从xml转化为object
     * 
     * @param pXml
     * @return
     */
    public static PnrContentResponse convertFromXML(String pXml) {
        if (null == pXml) {
            return null;
        }
        StaxDriver sd = new StaxDriver(new NoNameCoder());
        XStream xstream = new XStream(sd);
        xstream.processAnnotations(PnrContentResponse.class);
        PnrContentResponse response = null;
        
        try {
			response = (PnrContentResponse) xstream.fromXML(pXml);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        return response;
    }

    /**
     * 将对象转化为XML
     * 
     * @param pPatParams
     * @return
     */
    public static String convertToXML(PnrContentResponse response) {
    	if(null == response){
    		return null;
    	}
        StaxDriver sd = new StaxDriver(new NoNameCoder());
        XStream xstream = new XStream(sd);
        xstream.processAnnotations(PnrContentResponse.class);
        return xstream.toXML(response);
    }
}
