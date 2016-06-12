package com.travelzen.etermface.common.pojo.fare;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:15/4/13
 * Time:下午2:49
 * <p/>
 * Description:
 * <p/>
 * Nfd请求结果所对应的类
 */
public class NfdFareResponse implements Serializable {
    private static final long serialVersionUID = 3353595565668716619L;
    /**
     * 是否成功执行
     */
    public boolean success;
    /**
     * 失败的原因
     */
    public String errorInfo;
    /**
     * 出发机场三字码
     */
    public String deptAirport;
    /**
     * 到达机场三字码
     */
    public String arrAirport;
    /**
     * 出发日期(yyyy-mm-dd)
     */
    public String deptDate;
    /**
     * 航司二字码
     */
    public String carrier;
    /**
     * 舱位
     */
    public String cabin;
    /**
     * nfd具体信息
     */
    public List<NfdInfo> nfdInfos;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public String getDeptAirport() {
        return deptAirport;
    }

    public void setDeptAirport(String deptAirport) {
        this.deptAirport = deptAirport;
    }

    public String getArrAirport() {
        return arrAirport;
    }

    public void setArrAirport(String arrAirport) {
        this.arrAirport = arrAirport;
    }

    public String getDeptDate() {
        return deptDate;
    }

    public void setDeptDate(String deptDate) {
        this.deptDate = deptDate;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getCabin() {
        return cabin;
    }

    public void setCabin(String cabin) {
        this.cabin = cabin;
    }

    public List<NfdInfo> getNfdInfos() {
        return nfdInfos;
    }

    public void setNfdInfos(List<NfdInfo> nfdInfos) {
        this.nfdInfos = nfdInfos;
    }
    
    @Override
	public String toString() {
		return "NfdFareResponse [success=" + success + ", errorInfo="
				+ errorInfo + ", deptAirport=" + deptAirport + ", arrAirport="
				+ arrAirport + ", deptDate=" + deptDate + ", carrier="
				+ carrier + ", cabin=" + cabin + ", nfdInfos=" + nfdInfos + "]";
	}

	/**
     * 描述nfd具体信息的静态内部类
     */
    public static class NfdInfo {
        /**
         * nfd文本
         */
        public String nfdStr;
        /**
         * 去程／回程文本
         */
        public String nfn01Str;
        /**
         * 适用规定文本
         */
        public String nfn02Str;
        /**
         * 预订规定文本
         */
        public String nfn04Str;
        /**
         * 运价组合文本
         */
        public String nfn05Str;
        /**
         * 团队规定文本
         */
        public String nfn06Str;
        /**
         * 退票规定文本
         */
        public String nfn08Str;
        /**
         * 变更规定文本
         */
        public String nfn09Str;
        /**
         * 其他规定文本
         */
        public String nfn11Str;

        public String getNfdStr() {
            return nfdStr;
        }

        public void setNfdStr(String nfdStr) {
            this.nfdStr = nfdStr;
        }

        public String getNfn01Str() {
            return nfn01Str;
        }

        public void setNfn01Str(String nfn01Str) {
            this.nfn01Str = nfn01Str;
        }

        public String getNfn02Str() {
            return nfn02Str;
        }

        public void setNfn02Str(String nfn02Str) {
            this.nfn02Str = nfn02Str;
        }

        public String getNfn04Str() {
            return nfn04Str;
        }

        public void setNfn04Str(String nfn04Str) {
            this.nfn04Str = nfn04Str;
        }

        public String getNfn05Str() {
            return nfn05Str;
        }

        public void setNfn05Str(String nfn05Str) {
            this.nfn05Str = nfn05Str;
        }

        public String getNfn06Str() {
            return nfn06Str;
        }

        public void setNfn06Str(String nfn06Str) {
            this.nfn06Str = nfn06Str;
        }

        public String getNfn08Str() {
            return nfn08Str;
        }

        public void setNfn08Str(String nfn08Str) {
            this.nfn08Str = nfn08Str;
        }

        public String getNfn09Str() {
            return nfn09Str;
        }

        public void setNfn09Str(String nfn09Str) {
            this.nfn09Str = nfn09Str;
        }

        public String getNfn11Str() {
            return nfn11Str;
        }

        public void setNfn11Str(String nfn11Str) {
            this.nfn11Str = nfn11Str;
        }

		@Override
		public String toString() {
			return "NfdInfo [nfdStr=" + nfdStr + ", nfn01Str=" + nfn01Str
					+ ", nfn02Str=" + nfn02Str + ", nfn04Str=" + nfn04Str
					+ ", nfn05Str=" + nfn05Str + ", nfn06Str=" + nfn06Str
					+ ", nfn08Str=" + nfn08Str + ", nfn09Str=" + nfn09Str
					+ ", nfn11Str=" + nfn11Str + "]";
		}
        
    }
}
