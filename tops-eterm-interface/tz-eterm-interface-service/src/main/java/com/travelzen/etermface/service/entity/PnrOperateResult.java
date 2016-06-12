package com.travelzen.etermface.service.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("PnrOperateResult")
public class PnrOperateResult {

    public String status;
    public String error;
    public String errorDetail;

    public EtermSessionInfo etermSessionInfo;

    @Override
    public String toString() {
	return "PnrOperateResult [status=" + status + ", error=" + error + ", errorDetail=" + errorDetail + ", etermSessionInfo=" + etermSessionInfo + "]";
    }

}
