package com.xiey94.httprequest.ui;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.xiey94.httprequest.utils.FileParams;
import com.xiey94.httprequest.HttpURLConnectionManager;
import com.xiey94.httprequest.R;
import com.xiey94.httprequest.callback.CallBack;
import com.xiey94.httprequest.log.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private TextView resultTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultTV = findViewById(R.id.result);
    }

    public void get(View view) {
        String url = "https://www.baidu.com";
        HttpURLConnectionManager.getInstance().setUseCache(true).get(url, new CallBack() {
            @Override
            public void success(final String result, String msg) {
                Log.i(result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultTV.setText(result);
                    }
                });
            }

            @Override
            public void failed(final String msg) {
                Log.e(msg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultTV.setText(msg);
                    }
                });
            }
        });
    }

    public void post(View view) {
        String url = "http://hengwell.asuscomm.com:2480/sdchainWallet-webservice/resSDnWalt/user/login";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userName", "13921535626");
        params.put("password", "123456");
        params.put("languageType", "");
        HttpURLConnectionManager.getInstance().post(url, params, new CallBack() {
            @Override
            public void success(final String result, String msg) {
                Log.i(result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultTV.setText(result);
                    }
                });
            }

            @Override
            public void failed(final String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultTV.setText(msg);
                    }
                });
            }
        });
    }

    public void download(View view) {
        String url = "http://t1.mmonly.cc/uploads/150821/6-150R1101301M5.jpg";
        String url2 = "http://t2.hddhhn.com/uploads/tu/201312/257/1.jpg";
        HttpURLConnectionManager.getInstance().download(url, null, null, new CallBack() {
            @Override
            public void success(final String result, String msg) {
                Log.i(result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultTV.setText(result);
                    }
                });
            }

            @Override
            public void failed(final String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultTV.setText(msg);
                    }
                });
            }
        });
    }

    public void postFile(View view) {
        String url = "http://www.baidu.com";
        FileParams params = new FileParams();
        params.put("first", "value1");
        params.put("second", "value2");
        params.put("third", "value3");
        params.put("fileParams1", getFile("射雕英雄传", "射雕英雄传.txt"));
        params.put("fileParams2", getFile("降龙十八掌", "降龙十八掌.txt"));
        HttpURLConnectionManager.getInstance().postFile(url, params, new CallBack() {
            @Override
            public void success(final String result, String msg) {
                Log.i(result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultTV.setText(result);
                    }
                });
            }

            @Override
            public void failed(final String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultTV.setText(msg);
                    }
                });
            }
        });
    }

    public File getFile(String str, String fileName) {
        String filePath = null;
        File file = null;
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) {
            filePath = Environment.getExternalStorageDirectory().toString() + File.separator + fileName;
        } else {
            filePath = Environment.getDownloadCacheDirectory().toString() + File.separator + fileName;
        }
        try {
            file = new File(filePath);
            if (!file.exists()) {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }
            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(str.getBytes());
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
}
