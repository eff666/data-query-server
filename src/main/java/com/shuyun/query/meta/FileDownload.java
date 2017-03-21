package com.shuyun.query.meta;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by wanghaiwei on 2016/1/12.
 */
public class FileDownload {

    private Verificate verificate;
    private Content content;
    @JsonIgnore
    private String filename;

    public Verificate getVerificate() {
        return verificate;
    }

    public void setVerificate(Verificate verificate) {
        this.verificate = verificate;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public String toString() {
        return "FileDownload{" +
                "verificate=" + verificate +
                ", content=" + content +
                ", filename='" + filename + '\'' +
                '}';
    }

    public static class Content{
        private String shop_id;
        private String time;
        private String type;

        public String getShop_id() {
            return shop_id;
        }

        public void setShop_id(String shop_id) {
            this.shop_id = shop_id;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "Content{" +
                    "shop_id='" + shop_id + '\'' +
                    ", time='" + time + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }

    public static class Verificate{
        private String key;
        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Verificate{" +
                    "key='" + key + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

}
