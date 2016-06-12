package com.travelzen.etermface.service.entity;

import java.util.List;

import com.google.common.collect.Lists;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("DetrFResult")
public class DetrfResult {

    public String STATUS;
    public String ERRORS;
    public String name;
    public List<String> rcpt = Lists.newArrayList();

    public String toString() {
        return "DetrfResult [name=" + name + ", \nrcpt=" + rcpt + "]";
    }
}
