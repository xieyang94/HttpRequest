package com.xiey94.httprequest;

import android.os.Environment;
import android.text.TextUtils;

import com.xiey94.httprequest.callback.CallBack;
import com.xiey94.httprequest.exception.WrongUrlException;
import com.xiey94.httprequest.log.Log;
import com.xiey94.httprequest.utils.FileParams;
import com.xiey94.httprequest.bean.Response;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : xiey
 * @project name : HttpRequest.
 * @package name  : com.xiey94.httprequest.
 * @date : 2018/7/23.
 * @signature : do my best.
 * @explain :
 */
public class HttpURLConnectionManager {
    private final String START = "--";
    private final String END = "\r\n";
    private final String BOUNDARY = "jtdssgthsnsgshtsfvsrhhfbfsbvbsdhs";
    private final String CONTENT_TYPE_JSON = "application/x-javascript->json";
    private final String CONTENT_TYPE_DEFAULT = "application/x-www-form-urlencoded";
    private final String CONTENT_TYPE_POST = "multipart/form-data;boundary=" + BOUNDARY;
    private final String CONTENT_TYPE_SERIALIZED = "application/x-java-serialized-object";
    private final int RESULT_200 = 200;
    private final int RESULT_302 = 302;
    private final int FAILED = -1;
    private Map<String, String> header;
    private volatile static HttpURLConnectionManager manager;
    private boolean useCache;

    private HttpURLConnectionManager() {
    }

    public static HttpURLConnectionManager getInstance() {
        if (manager == null) {
            synchronized (HttpURLConnectionManager.class) {
                if (manager == null) {
                    manager = new HttpURLConnectionManager();
                }
            }
        }
        return manager;
    }

    public void addHeader(Map<String, String> headerParams) {
        if (header == null) {
            header = new HashMap<String, String>();
        }
        if (headerParams == null || headerParams.size() == 0) {
            return;
        }

        for (Map.Entry<String, String> entry : headerParams.entrySet()) {
            if (header.containsKey(entry.getKey())) {
                header.remove(entry.getKey());
            }
            header.put(entry.getKey(), entry.getValue());
        }
    }

    private void addHeader(Map<String, String> headerParams, HttpURLConnection connection) {
        addHeader(headerParams);
        if (header != null) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                connection.addRequestProperty(entry.getKey(), entry.getValue());
            }
        }
    }

    public void get(final String url, final CallBack callBack) {
        get(url, null, null, callBack);
    }

    public void get(final String url, final Map<String, String> params, final CallBack callBack) {
        get(url, null, params, callBack);
    }

    public void get(final String url, final Map<String, String> header, final Map<String, String> params, final CallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = get(url, header, params);
                if (response != null && response.getCode() == RESULT_200) {
                    callBack.success(response.getResult(), "请求成功");
                } else {
                    Log.e("请求失败：返回码：" + response.getCode() + "\n" + response.getResult());
                    callBack.failed(response.getResult());
                }
            }
        }).start();
    }

    private Response get(String urlPath, Map<String, String> headerParams, Map<String, String> params) {
        String message = "";
        InputStream inputStream = null;
        int code = FAILED;
        try {
            URL url = new URL(urlWrapper(urlPath, params));
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection connection = (HttpURLConnection) urlConnection;
            //默认GET
            connection.setRequestMethod("GET");
            //设置连接超时
            connection.setConnectTimeout(30000);
            connection.setUseCaches(useCache);
            addHeader(headerParams, connection);
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();
                byte[] data = new byte[1024];
                StringBuffer sb = new StringBuffer();
                int length = 0;
                while ((length = inputStream.read(data)) != -1) {
                    String s = new String(data, Charset.forName("utf-8"));
                    sb.append(s);
                }
                message = sb.toString();
                code = RESULT_200;
            } else if (responseCode == RESULT_302) {
                //重定向
                String localUrl = connection.getHeaderField("Location");
                Log.i("重定向:" + localUrl);
                Response response = get(localUrl, headerParams, params);
                code = response.getCode();
                message = response.getResult();
            } else {
                code = responseCode;
                message = connection.getResponseMessage();
            }
            connection.disconnect();
        } catch (MalformedURLException e) {
            message = e.toString();
        } catch (IOException e) {
            message = e.toString();
        } catch (WrongUrlException e) {
            message = e.toString();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    message = e.toString();
                }
            }
        }
        return new Response(code, message);
    }


    public void post(final String url, final Map<String, String> params, final CallBack callBack) {
        post(url, null, params, callBack);
    }

    public void post(final String url, final Map<String, String> headerParams, final Map<String, String> params, final CallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = post(url, headerParams, params);
                if (response != null && response.getCode() == RESULT_200) {
                    callBack.success(response.getResult(), "提交成功");
                } else {
                    Log.e("请求失败：返回码：" + response.getCode() + "\n" + response.getResult());
                    callBack.failed(response.getResult());
                }
            }
        }).start();
    }

    private Response post(String urlPath, Map<String, String> headerParams, Map<String, String> params) {
        String message = "";
        InputStream inputStream = null;
        int code = FAILED;
        try {
            URL url = new URL(urlPath);
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection connection = (HttpURLConnection) urlConnection;

            //POST配置
            //设置是否从HttpURLConnection中读取，默认true
            connection.setDoInput(true);
            //设置是否向HTTPURLConnection中输出，默认false；这是post请求用到的，需要放到正文中
            connection.setDoOutput(true);
            //POST请求不能缓存
            connection.setUseCaches(false);

            //请求头
            //设置传送的内容类型是可序列化的Java对象
            //如果不设此项，在传送序列化对象时，当WEB服务器默认的不是这种类型时可能抛出java.io.EOFDException
            connection.setRequestProperty("Content-type", CONTENT_TYPE_DEFAULT);
            //设置与服务器保持连接
            connection.addRequestProperty("Connection", "Keep-Alive");
            //设置字符编码类型
            connection.addRequestProperty("Charset", "UTF-8");
            //设置请求的方法为POST，默认为GET
            connection.setRequestMethod("POST");
            //设置连接主机超时
            connection.setConnectTimeout(30000);
            //设置从主机读取数据超时
            connection.setReadTimeout(30000);
            addHeader(headerParams, connection);
            //连接，上述配置需要在连接前完成
            connection.connect();

            //连接问题(此处操作隐含进行连接(connect())，可省略上述connect()操作)
            OutputStream outputStream = connection.getOutputStream();

            //写数据和发送数据
            //通过输出流对象构建输出流对象，以实现输出可序列化的对象
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            //向对象输出流写出数据，这些数据将缓存到内存缓冲区
            objectOutputStream.writeObject(paramsWrapper(params));
            //刷新对象输出流，将任何字节都写入到潜在的流中
            objectOutputStream.flush();
            //关闭流对象；此时不能再向流中写入数据，先前写入的数据存在于内存缓冲区中
            objectOutputStream.close();

            //将内存缓冲区中封装好的完整的HTTP请求电文发送到服务端
            //实际发送请求的代码段就在这里
            Log.i("responseCode:" + connection.getResponseCode());
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();
                byte[] data = new byte[1024];
                StringBuffer sb1 = new StringBuffer();
                int length = 0;
                while ((length = inputStream.read(data)) != -1) {
                    String s = new String(data, Charset.forName("utf-8"));
                    sb1.append(s);
                }
                code = RESULT_200;
                message = sb1.toString();
            } else {
                code = responseCode;
                message = connection.getResponseMessage();
            }
            connection.disconnect();
        } catch (MalformedURLException e) {
            message = e.toString();
        } catch (IOException e) {
            message = e.toString();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    message = e.toString();
                }
            }
        }
        return new Response(code, message);
    }

    private Response dealResponse(InputStream inputStream) {
        return null;
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public void download(final String url, final Map<String, String> headerParams, final Map<String, String> params, final CallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = download(url, headerParams, params);
                if (response != null && response.getCode() == RESULT_200) {
                    callBack.success(response.getResult(), "请求成功");
                } else {
                    Log.e("请求失败：返回码：" + response.getCode() + "\n" + response.getResult());
                    callBack.failed(response.getResult());
                }
            }
        }).start();
    }

    private Response download(String urlPath, Map<String, String> headerParams, Map<String, String> params) {
        int code = FAILED;
        String message = "";
        if (isExternalStorageWritable()) {
            try {
                File parent = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath());
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                File downloadFile = new File(parent.getPath(), System.currentTimeMillis() + ".jpg");
                URL url = new URL(urlWrapper(urlPath, params));
                URLConnection urlConnection = url.openConnection();
                HttpURLConnection connection = (HttpURLConnection) urlConnection;
                addHeader(headerParams, connection);
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    FileOutputStream fileOutputStream = new FileOutputStream(downloadFile.getAbsolutePath());
                    byte[] data = new byte[1024];
                    int length = 0;
                    while ((length = inputStream.read(data)) != -1) {
                        fileOutputStream.write(data, 0, length);
                        fileOutputStream.flush();
                    }
                    code = RESULT_200;
                    message = "download success";

                    inputStream.close();
                    fileOutputStream.close();
                    connection.disconnect();
                }
            } catch (MalformedURLException e) {
                message = e.toString();
            } catch (IOException e) {
                message = e.toString();
            } catch (WrongUrlException e) {
                message = e.toString();
            }
        } else {
            code = FAILED;
            message = "没有内存卡";
        }
        return new Response(code, message);
    }

    private String urlWrapper(String url, Map<String, String> params) throws WrongUrlException {
        if (TextUtils.isEmpty(url)) {
            throw new WrongUrlException("url can't be empty");
        }
        StringBuilder sb = new StringBuilder();
        sb.append(url).append("?");
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            sb.delete(sb.length() - 1, sb.length());
        }
        return sb.toString();
    }

    private String paramsWrapper(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            sb.delete(sb.length() - 1, sb.length());
        }
        return sb.toString();
    }

    public void postFile(final String url, final FileParams params, final CallBack callBack) {
        postFile(url, null, params, callBack);
    }

    public void postFile(final String url, final Map<String, String> headParams, final FileParams params, final CallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = postFile(url, headParams, params);
                if (response != null && response.getCode() == RESULT_200) {
                    callBack.success(response.getResult(), "请求成功");
                } else {
                    Log.e("请求失败：返回码：" + response.getCode() + "\n" + response.getResult());
                    callBack.failed(response.getResult());
                }
            }
        }).start();
    }

    private Response postFile(String urlPath, Map<String, String> headParams, FileParams params) {
        int code = FAILED;
        String message = "";
        DataOutputStream dos = null;
        try {
            URL url = new URL(urlPath);
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection connection = (HttpURLConnection) urlConnection;
            connection.setRequestMethod("POST");

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Content-Type", CONTENT_TYPE_POST);
            connection.setConnectTimeout(20000);
            connection.setReadTimeout(20000);

            addHeader(headParams, connection);

            dos = new DataOutputStream(connection.getOutputStream());

            dos.writeBytes(START + BOUNDARY + END);

            //先urlParams
            if (params.getUrlParams() != null && params.getUrlParams().size() > 0) {
                for (Map.Entry<String, String> entry : params.getUrlParams().entrySet()) {
                    dos.writeBytes("Content-Disposition:form-data;name=\"" + encode(entry.getKey()) + "\"\r\n");
                    dos.writeBytes(END);
                    dos.writeBytes(encode(entry.getValue()) + END);
                    dos.writeBytes(START + BOUNDARY + END);
                }
            }

            //再fileParams
            int fileParamsNum = 0;
            if (params.getFileParams() != null && params.getFileParams().size() > 0) {
                for (Map.Entry<String, FileParams.FileWrapper> entry : params.getFileParams().entrySet()) {
                    FileInputStream fileInputStream = new FileInputStream(entry.getValue().getFile());
                    dos.writeBytes("Content-Disposition: form-data;name=\"" + entry.getKey() + "\";filename=\"" + URLEncoder.encode(entry.getValue().getFileName(), "UTF-8") + "\"\r\n");
                    dos.writeBytes("Content-Type: " + entry.getValue().getFileType() + END);
                    dos.writeBytes(END);
                    byte[] bytes = new byte[1024];
                    while (fileInputStream.read(bytes) != -1) {
                        dos.write(bytes);
                    }
                    dos.writeBytes(END);
                    fileParamsNum++;
                    if (fileParamsNum == params.getFileParams().size()) {
                        dos.writeBytes(START + BOUNDARY + START + END);
                    } else {
                        dos.writeBytes(START + BOUNDARY + END);
                    }
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                }
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                byte[] data = new byte[1024];
                StringBuffer sb = new StringBuffer();
                while (inputStream.read(data) != -1) {
                    String s = new String(data, Charset.forName("UTF-8"));
                    sb.append(s);
                }
                message = sb.toString();
                code = RESULT_200;
                inputStream.close();
            } else {
                code = responseCode;
                message = "请求失败";
            }

        } catch (MalformedURLException e) {
            message = e.toString();
        } catch (IOException e) {
            message = e.toString();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    message = e.toString();
                }
            }
        }
        return new Response(code, message);
    }

    private String encode(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, "UTF-8");
    }

    public boolean getUseCache() {
        return useCache;
    }

    public HttpURLConnectionManager setUseCache(boolean useCache) {
        this.useCache = useCache;
        return this;
    }


}
