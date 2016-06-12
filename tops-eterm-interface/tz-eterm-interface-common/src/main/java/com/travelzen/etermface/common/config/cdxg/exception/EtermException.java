package com.travelzen.etermface.common.config.cdxg.exception;

import com.travelzen.framework.core.common.ReturnCode;

public class EtermException extends RuntimeException {
    private static final long serialVersionUID = 430305575267731710L;
    private String retMsg = "";
    private ReturnCode retCode;
    private Class<?> cls;
    private Object object;

    public EtermException(ReturnCode retCode) {
        this(retCode, "");
    }

    public EtermException(ReturnCode retCode, String retMsg) {
        this(retCode, retMsg, null, null);
    }

    public EtermException(Throwable thr, Class<?> cls, Object object) {
        this(ReturnCode.ERROR, "", thr, cls, object);
    }

    public EtermException(ReturnCode retCode, Throwable thr, Class<?> cls, Object object) {
        this(retCode, "", thr, cls, object);
    }

    public EtermException(ReturnCode retCode, Class<?> cls, Object object) {
        this(retCode, "", cls, object);
    }

    public EtermException(ReturnCode retCode, String retMsg, Throwable thr, Class<?> cls, Object object) {
        super(String.format("[retCode=%s,retMsg=%s]", retCode.getErrorCode(), retMsg));
        this.retCode = retCode;
        this.retMsg = retMsg;
        this.cls = cls;
        if (cls != null && !cls.isInstance(object)) {
            throw new ClassCastException(cls.getCanonicalName() + ":" + object.getClass().getCanonicalName());
        }
        this.object = object;
        this.initCause(thr);
    }

    @SuppressWarnings("unchecked")
    public <T> T getObject(Class<T> cls) {
        if (!cls.equals(cls)) {
            throw new ClassCastException(this.cls.getCanonicalName() + ":" + cls.getCanonicalName());
        }
        return (T) object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public EtermException(ReturnCode retCode, String retMsg, Class<?> cls, Object object) {
        super(String.format("[retCode=%s,retMsg=%s]", retCode.getErrorCode(), retMsg));
        this.retCode = retCode;
        this.retMsg = retMsg;
        this.cls = cls;
        if (cls != null && !cls.isInstance(object)) {
            throw new ClassCastException(cls.getCanonicalName() + ":" + object.getClass().getCanonicalName());
        }
        this.object = object;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public void setRetMsg(String retMsg) {
        this.retMsg = retMsg;
    }

    public ReturnCode getRetCode() {
        return retCode;
    }

    @Override
    public String toString() {
        return getMessage();
    }

    @Override
    public String getMessage() {
        return String.format("[retCode=%s,retMsg=%s, object=%s]", this.retCode, this.retMsg, object);
    }
}
