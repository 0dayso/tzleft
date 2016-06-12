package com.travelzen.farerule.jpecker.client;

import java.util.List;

import com.travelzen.farerule.jpecker.server.JpeckerService;
import com.travelzen.framework.thrift.client.balancing.Client;
import com.travelzen.framework.thrift.client.balancing.LoadBalancingChannelNoProperty;
import com.travelzen.framework.thrift.client.balancing.WjThriftClient;

public class JpeckerClientLoadBalancingChannel extends LoadBalancingChannelNoProperty<JpeckerService.Iface> {

    public JpeckerClientLoadBalancingChannel(List<String> list, String prefix, String servcieName, boolean needCache) {
        super(list, prefix, servcieName, needCache);
    }

    @Override
    public Client<JpeckerService.Iface> createThriftCient(String ip, int port) {
        Client<JpeckerService.Iface> tc = new WjThriftClient<JpeckerService.Iface, JpeckerService.Client>(ip, port, true, JpeckerService.Client.class);
        return tc;
    }
}
