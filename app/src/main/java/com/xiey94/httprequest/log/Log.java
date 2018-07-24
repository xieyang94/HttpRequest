package com.xiey94.httprequest.log;

import android.text.TextUtils;

/**
 * @author : xiey
 * @project name : HttpRequest.
 * @package name  : com.xiey94.httprequest.log.
 * @date : 2018/7/23.
 * @signature : do my best.
 * @explain :
 */
public class Log {
    public static final String TAG = "cctw";

    public static void i(String message) {
        if (!TextUtils.isEmpty(message)) {
            android.util.Log.i(TAG, message);
        }
    }

    public static void e(String message) {
        if (!TextUtils.isEmpty(message)) {
            android.util.Log.e(TAG, message);
        }
    }

    public static void d(String message) {
        if (!TextUtils.isEmpty(message)) {
            android.util.Log.d(TAG, message);
        }
    }

    public static void v(String message) {
        if (!TextUtils.isEmpty(message)) {
            android.util.Log.v(TAG, message);
        }
    }

    public static void w(String message) {
        if (!TextUtils.isEmpty(message)) {
            android.util.Log.w(TAG, message);
        }
    }
}
