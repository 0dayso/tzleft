package com.travelzen.javacc.bargains;

import com.thoughtworks.xstream.XStream;
import com.travelzen.etermface.service.abe_imitator.fare.bargains.BargainFareSearchParser;
import com.travelzen.etermface.service.abe_imitator.fare.pojo.FareSearchResponse;
import org.junit.Test;

import java.io.*;

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:14/12/5
 * Time:下午4:38
 * <p/>
 * Description:
 */
public class BargainFareSearchParserTest {
    @Test
    public void analysisTest() throws Exception {
        String path1=BargainFareSearchParser.class.getClassLoader().getResource("").getPath().replaceAll("/classes/test","");
        BufferedReader reader = new BufferedReader(new FileReader(new File(path1 + "resources/test/javacc/bargains/bargain.txt")));
        String line;
        String rs = "";
        while ((line = reader.readLine()) != null) {
            rs = rs + line + "\u005cr";
        }

        if (rs.startsWith(">")) {
            rs = rs.replaceFirst(">", "(");
            rs = rs.replaceAll("\\u001e", "");
        }
        InputStream in = new ByteArrayInputStream(rs.getBytes("UTF-8"));
        BargainFareSearchParser parser = new BargainFareSearchParser(in);
        FareSearchResponse fareSearchResponse = parser.analysis();
        XStream xstream = new XStream();
        System.out.println(xstream.toXML(fareSearchResponse));

    }
}
