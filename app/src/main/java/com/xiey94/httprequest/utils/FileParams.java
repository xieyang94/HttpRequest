package com.xiey94.httprequest.utils;

import android.text.TextUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : xiey
 * @project name : HttpRequest.
 * @package name  : com.xiey94.httprequest.
 * @date : 2018/7/24.
 * @signature : do my best.
 * @explain :
 */
public class FileParams {
    private Map<String, String> urlParams;
    private Map<String, FileWrapper> fileParams;

    public void put(String key, String value) {
        if (urlParams == null) {
            urlParams = new HashMap<String, String>();
        }
        urlParams.put(key, value);
    }

    public void put(String key, File value) {
        if (fileParams == null) {
            fileParams = new HashMap<String, FileWrapper>();
        }
        fileParams.put(key, new FileWrapper(value));
    }

    public Map<String, String> getUrlParams() {
        return urlParams;
    }

    public Map<String, FileWrapper> getFileParams() {
        return fileParams;
    }

    public class FileWrapper {
        private File file;
        private String fileType;

        public FileWrapper(File file) {
            this.file = file;
        }

        public File getFile() {
            return file;
        }

        public String getFileType() {
            if (TextUtils.isEmpty(fileType)) {
                return "image/jpeg";
            }
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public String getFileName() {
            return file.getName();
        }

        public String getFilePath() {
            return file.getPath();
        }
    }
}
