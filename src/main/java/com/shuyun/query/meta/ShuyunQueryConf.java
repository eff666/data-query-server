package com.shuyun.query.meta;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuyun.query.serializer.ObjectSerializer;

import java.util.Arrays;
import java.util.List;

public class ShuyunQueryConf {
    private String elasticSearchUrl = "10.153.204.250";
    private int port = 9400;
    private String clusterName = "elasticsearch-cluster";
    private String index = "shuyun_id_1";
    private String type = "shuyun_id";
    private int from = 0;
    private int size = 200;
    private String addressIndex;
    private String addressType;

    public ShuyunQueryConf(){}

    public ShuyunQueryConf(String esUrl, int port, String clusterName, String index, String type, List<String> queryColumn){
        this.elasticSearchUrl = esUrl;
        this.port = port;
        this.clusterName = clusterName;
        this.index = index;
        this.type = type;
    }

    public String getElasticSearchUrl() {
        return elasticSearchUrl;
    }

    public void setElasticSearchUrl(String elasticSearchUrl) {
        this.elasticSearchUrl = elasticSearchUrl;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getAddressIndex() {
        return addressIndex;
    }

    public void setAddressIndex(String addressIndex) {
        this.addressIndex = addressIndex;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    private static ShuyunQueryConf instance = null;
    public static ShuyunQueryConf getInstance(){
        return instance;
    }

    static{
        instance = ObjectSerializer.read("shuyunquery.json", new TypeReference<ShuyunQueryConf>() {}, ShuyunQueryConf.class.getClassLoader());
        if(null == instance){
            throw new RuntimeException("can not load shuyunquery.json");
        }
    }
}
