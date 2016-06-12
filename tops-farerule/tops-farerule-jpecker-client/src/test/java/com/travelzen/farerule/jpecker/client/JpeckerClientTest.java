package com.travelzen.farerule.jpecker.client;

import java.util.ArrayList;
import java.util.List;

import com.travelzen.farerule.jpecker.server.JpeckerService;

public class JpeckerClientTest {

    static JpeckerClientLoadBalancingChannel loadBalancingChannel = null;

    static {
        String ip = "localhost";// "192.168.161.189";
        String port = "10077";
        String ipAndPort = ip + ":" + port;
        List<String> list = new ArrayList<String>();
        list.add(ipAndPort);

        loadBalancingChannel = new JpeckerClientLoadBalancingChannel(list, "/tzns/tz", "jpecker", false);
    }

    public static void main(String args[]) throws Exception {
        JpeckerService.Iface client = loadBalancingChannel.proxy();
        List<String> list = new ArrayList<String>();
        list.add("5827e05e1c6b720a7dd57ddcd86e3495");
        System.out.println(client.fareRulePeck(list));
    }
}
