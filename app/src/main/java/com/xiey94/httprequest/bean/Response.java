package com.xiey94.httprequest.bean;

/**
 * @author : xiey
 * @project name : HttpRequest.
 * @package name  : com.xiey94.httprequest.
 * @date : 2018/7/23.
 * @signature : do my best.
 * @explain :
 */
public class Response {
    private int code;
    private String result;

    public Response() {
    }

    public Response(int code, String result) {
        this.code = code;
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
