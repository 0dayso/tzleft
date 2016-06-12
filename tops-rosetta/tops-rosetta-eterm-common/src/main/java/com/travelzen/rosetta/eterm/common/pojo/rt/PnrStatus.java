package com.travelzen.rosetta.eterm.common.pojo.rt;

/**
 * PNR状态
 *
 * @author yiming.yan
 */
public enum PnrStatus {
    /**
     * 无此PNR
     */
    NO_PNR,
    /**
     * PNR未授权
     */
    UNAUTHORIZED,
    /**
     * 未出票PNR
     */
    RAW_TICKET,
    /**
     * 已出票PNR
     */
    ELECTRONIC_TICKET,
    /**
     * PNR已取消
     */
    CANCELLED;
}
