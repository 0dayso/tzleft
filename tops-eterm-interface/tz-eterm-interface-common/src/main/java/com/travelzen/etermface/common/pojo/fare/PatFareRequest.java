package com.travelzen.etermface.common.pojo.fare;

import com.travelzen.etermface.common.config.cdxg.CdxgConstant;
import com.travelzen.etermface.common.pojo.PassengerType;

import java.util.List;

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:15/4/15
 * Time:下午2:23
 * <p/>
 * Description:
 * <p/>
 * 根据PNR或航段获取PAT报价请求
 */
public class PatFareRequest {
	
    /**
     * Pat通过Pnr获取报价请求Pnr
     */
    public String pnr;
    /**
     * Pat通过航段获取报价请求参数
     */
    public PatFareBySegmentParams patFareBySegmentParams;
    // pnr与patFareBySegmentParams互斥
    
    /**
     * 解析需要用到的office,默认值为SHA255
     */
    public String office = CdxgConstant.DEFAULT_OFFICE;
    /**
     * 三方协议号
     */
    public String triPartiteNo;
    /**
     * 乘客类型
     */
    public List<PassengerType> passengerTypes;

    public String getPnr() {
        return pnr;
    }

    public void setPnr(String pnr) {
        this.pnr = pnr;
    }

    public PatFareBySegmentParams getPatFareBySegmentParams() {
		return patFareBySegmentParams;
	}

	public void setPatFareBySegmentParams(
			PatFareBySegmentParams patFareBySegmentParams) {
		this.patFareBySegmentParams = patFareBySegmentParams;
	}

	public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getTriPartiteNo() {
		return triPartiteNo;
	}

	public void setTriPartiteNo(String triPartiteNo) {
		this.triPartiteNo = triPartiteNo;
	}

	public List<PassengerType> getPassengerTypes() {
        return passengerTypes;
    }

    public void setPassengerTypes(List<PassengerType> passengerTypes) {
        this.passengerTypes = passengerTypes;
    }

	@Override
	public String toString() {
		return "PatFareRequest [pnr=" + pnr + ", patFareBySegmentParams="
				+ patFareBySegmentParams + ", office=" + office
				+ ", triPartiteNo=" + triPartiteNo + ", passengerTypes="
				+ passengerTypes + "]";
	}

}
