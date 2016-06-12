package com.travelzen.etermface.common.config.cdxg.pojo;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

@XStreamConverter(value = ToAttributedValueConverter.class, strings = "rsValue")
public class Ins implements Serializable {
    private static final long serialVersionUID = 392177137822170752L;

    @XStreamAsAttribute
    @XStreamAlias("ret_value")
    private String retValue;

    private String rsValue;

    public Ins() {
        super();
    }

    public Ins(String retValue, String rsValue) {
        super();
        this.retValue = retValue;
        this.rsValue = rsValue;
    }

    public String getRetValue() {
        return retValue;
    }

    public void setRetValue(String retValue) {
        this.retValue = retValue;
    }

    public String getRsValue() {
        return rsValue;
    }

    public void setRsValue(String rsValue) {
        this.rsValue = rsValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((retValue == null) ? 0 : retValue.hashCode());
        result = prime * result + ((rsValue == null) ? 0 : rsValue.hashCode());
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
        Ins other = (Ins) obj;
        if (retValue == null) {
            if (other.retValue != null)
                return false;
        } else if (!retValue.equals(other.retValue))
            return false;
        if (rsValue == null) {
            if (other.rsValue != null)
                return false;
        } else if (!rsValue.equals(other.rsValue))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "InsResponse [ret_value=" + retValue + ", rsValue=" + rsValue + "]";
    }
}
