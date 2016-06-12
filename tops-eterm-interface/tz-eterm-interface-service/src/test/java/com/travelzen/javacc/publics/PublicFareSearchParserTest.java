package com.travelzen.javacc.publics;

import com.thoughtworks.xstream.XStream;
import com.travelzen.etermface.service.abe_imitator.fare.bargains.BargainFareSearchParser;
import com.travelzen.etermface.service.abe_imitator.fare.pojo.FareSearchResponse;
import org.junit.Test;

import java.io.*;

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:14/12/19
 * Time:下午2:36
 * <p/>
 * Description:
 */
public class PublicFareSearchParserTest {
    @Test
    public void analysisTest() throws Exception {
        String path1=BargainFareSearchParser.class.getClassLoader().getResource("").getPath().replaceAll("/classes/test","");
        BufferedReader reader = new BufferedReader(new FileReader(new File(path1 + "resources/test/javacc/publics/test.txt")));

        String line;
        String rs = "";
        while ((line = reader.readLine()) != null) {
            line=line.replaceAll("\\u0010","");
            rs = rs + line + "\u005cr";
        }

        InputStream in = new ByteArrayInputStream(rs.getBytes("UTF-8"));
        PublicFareSearchParser parser = new PublicFareSearchParser(in);
        FareSearchResponse fareSearchResponse = parser.analysis();
        XStream xstream = new XStream();
        System.out.println(xstream.toXML(fareSearchResponse));
    }
}
