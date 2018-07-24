package com.xiey94.httprequest.callback;

/**
 * @author : xiey
 * @project name : HttpRequest.
 * @package name  : com.xiey94.httprequest.
 * @date : 2018/7/23.
 * @signature : do my best.
 * @explain :
 */
public interface CallBack {
    void success(String result,String msg);
    void failed(String msg);
}
