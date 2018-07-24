package com.xiey94.httprequest.base;

import android.app.Application;

import com.xiey94.httprequest.HttpURLConnectionManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : xiey
 * @project name : HttpRequest.
 * @package name  : com.xiey94.httprequest.base.
 * @date : 2018/7/24.
 * @signature : do my best.
 * @explain :
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initHttp();

    }

    private void initHttp() {
        Map<String, String> header = new HashMap<String, String>();
        header.put("test1", "1111");
        header.put("test2", "2222");
        header.put("test3", "3333");
        header.put("test3", "333333");
        HttpURLConnectionManager.getInstance().addHeader(header);
    }
}
