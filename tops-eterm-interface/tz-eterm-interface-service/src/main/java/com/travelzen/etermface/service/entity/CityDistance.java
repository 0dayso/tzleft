package com.travelzen.etermface.service.entity;

/**
 * @author hongqiang.mao
 *
 * @date 2013-6-14 下午4:14:10
 *
 * @description
 */
public class CityDistance {
	private String fromCity;
	private String toCity;
	/**
	 * 实际飞行距离
	 */
	private int tpm;
	/**
	 * 最大飞行距离
	 */
	private int mpm;
	
	/**
	 * 方向
	 */
	private String gi;

	public String getFromCity() {
		return fromCity;
	}

	public void setFromCity(String fromCity) {
		this.fromCity = fromCity;
	}

	public String getToCity() {
		return toCity;
	}

	public void setToCity(String toCity) {
		this.toCity = toCity;
	}

	public int getTpm() {
		return tpm;
	}

	public void setTpm(int tpm) {
		this.tpm = tpm;
	}

	public int getMpm() {
		return mpm;
	}

	public void setMpm(int mpm) {
		this.mpm = mpm;
	}

	public String getGi() {
		return gi;
	}

	public void setGi(String gi) {
		this.gi = gi;
	}

	@Override
	public String toString() {
		return "CityDistance [fromCity=" + fromCity + ", toCity=" + toCity
				+ ", tpm=" + tpm + ", mpm=" + mpm + ", gi=" + gi + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fromCity == null) ? 0 : fromCity.hashCode());
		result = prime * result + ((gi == null) ? 0 : gi.hashCode());
		result = prime * result + mpm;
		result = prime * result + ((toCity == null) ? 0 : toCity.hashCode());
		result = prime * result + tpm;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CityDistance other = (CityDistance) obj;
		if (fromCity == null) {
			if (other.fromCity != null)
				return false;
		} else if (!fromCity.equals(other.fromCity))
			return false;
		if (gi == null) {
			if (other.gi != null)
				return false;
		} else if (!gi.equals(other.gi))
			return false;
		if (mpm != other.mpm)
			return false;
		if (toCity == null) {
			if (other.toCity != null)
				return false;
		} else if (!toCity.equals(other.toCity))
			return false;
		if (tpm != other.tpm)
			return false;
		return true;
	}
}
