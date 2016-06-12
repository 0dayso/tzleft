package com.travelzen.etermface.service.entity;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.thoughtworks.xstream.annotations.XStreamAlias;


@XStreamAlias("DetrCombineResult")
public class DetrCombineResult {

    public DetrfResult detrfResult;
    public DetrResult detrResult;
    public DetrsResult detrsResult;

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
