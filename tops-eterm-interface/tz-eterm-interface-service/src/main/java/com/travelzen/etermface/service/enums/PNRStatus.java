package com.travelzen.etermface.service.enums;

public enum PNRStatus {
    //正常态
    NORNAL,
    //pnr已取消
    ENTIRELY_CANCELLED,
    UNAUTHORIZED,
    //已出电子票
    ELECTRONIC_TICKET,
    //没有pnr
    NO_PNR,
    //未知态
    UNKOWN_STATUS
}
