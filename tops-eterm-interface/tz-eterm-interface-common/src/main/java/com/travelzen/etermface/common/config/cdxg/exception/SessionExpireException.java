package com.travelzen.etermface.common.config.cdxg.exception;

/**
 * 所有 etermface-interfaces 里的业务逻辑代码不允许  catch  Throwable， 必须把SessionExpireException  抛到最外面 （重新进入的时候，必须再调用 openSession）。
 * all code in etermface-interfaces  is disallowed to "catch  Throwable",  SessionExpireException must be throwed to the most outside caller,
 * openSession must be recall when reenter the  function
 */
public class SessionExpireException extends Throwable {
    private static final long serialVersionUID = 1L;

    public SessionExpireException() {
        super();
    }
}
