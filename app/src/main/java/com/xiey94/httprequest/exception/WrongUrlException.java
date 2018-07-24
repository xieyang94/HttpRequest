package com.xiey94.httprequest.exception;

import android.annotation.TargetApi;

/**
 * @author : xiey
 * @project name : HttpRequest.
 * @package name  : com.xiey94.httprequest.exception.
 * @date : 2018/7/24.
 * @signature : do my best.
 * @explain :
 */
public class WrongUrlException extends Exception {
    public WrongUrlException() {
        super();
    }

    public WrongUrlException(String message) {
        super(message);
    }

    public WrongUrlException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongUrlException(Throwable cause) {
        super(cause);
    }

    @TargetApi(24)
    public WrongUrlException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
