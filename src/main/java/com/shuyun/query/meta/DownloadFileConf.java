package com.shuyun.query.meta;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuyun.query.serializer.ObjectSerializer;

/**
 * Created by wanghaiwei on 2016/1/20.
 */
public class DownloadFileConf {
    private String path;
    private String extensionName;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getExtensionName() {
        return extensionName;
    }

    public void setExtensionName(String extensionName) {
        this.extensionName = extensionName;
    }

    public static DownloadFileConf getInstance(){
        return cfg;
    }

    private static DownloadFileConf cfg = null;
    static{
        cfg = ObjectSerializer.read("downloadfile.json", new TypeReference<DownloadFileConf>() {
        }, DownloadFileConf.class.getClassLoader());
    }

}
