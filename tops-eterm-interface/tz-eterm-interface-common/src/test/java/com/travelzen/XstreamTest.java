package com.travelzen;
import com.thoughtworks.xstream.XStream;
import com.travelzen.etermface.common.config.cdxg.Converter;
import com.travelzen.etermface.common.config.cdxg.pojo.Ins;

public class XstreamTest {

    public static void main(String[] args) {
        Ins res = new Ins();
        XStream xstream = new XStream();
        xstream.alias("ins", Ins.class);
        res.setRetValue("1");
        res.setRsValue("absdefg");
        String xml = xstream.toXML(res);
        System.out.println(xml);
        String fromXml = "<?xml version=\"1.0\" encoding=\"GB2312\"?><ins ret_value=\"1\">20204E45454420454F54202D2D20D0E8D2AAB7E2BFDA284029BBF2BBB9D4AD284947292020200D20202020</ins>";
        System.out.println(fromXml);
        res = Converter.generateIns(fromXml, true);
        System.out.println(res.getRetValue() + ">>>>" + res.getRsValue());
    }
}
