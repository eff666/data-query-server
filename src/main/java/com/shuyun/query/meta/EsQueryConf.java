package com.shuyun.query.meta;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.shuyun.query.serializer.ObjectSerializer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EsQueryConf {
    public static final String UPDATEURL = "http://%s:%s/%s/%s/%s/_update";
    public static final String DELETEURL = "http://%s:%s/%s/%s/_query?pretty";
    public static final String INSERTURL = "http://%s:%s/%s/%s";

    private Map<String, DataSourceConf> dataSources = null;
    private List<String> elasticSearchUrl = Arrays.asList("10.10.128.242", "10.10.138.183");
    private int port = 9200;
    private String clusterName = "elasticsearch.cluster";
    //为数尊api接口
    private String tradeFeature = "trade_features";
    //为迁移到新的es集群
    private String moveElasticSearchUrl = "es1.intraweb.shuyun.com";
    private List<String> needMoveIndex = Arrays.asList();
    //为加密
    private List<String> needCryptIndex = Arrays.asList();
    private List<String> needCryptPhone = Arrays.asList();
    private List<String> needCryptNick = Arrays.asList();
    private List<String> needCryptName = Arrays.asList();

    public EsQueryConf(String clusterName, List<String> elasticSearchUrl, int port, Map<String, DataSourceConf> dataSources) {
        this.clusterName = clusterName;
        this.elasticSearchUrl = elasticSearchUrl;
        this.port = port;
        this.dataSources = dataSources;
    }

    public EsQueryConf(){
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public List<String> getElasticSearchUrl() {
        return elasticSearchUrl;
    }

    public void setElasticSearchUrl(List<String> elasticSearchUrl) {
        this.elasticSearchUrl = elasticSearchUrl;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Map<String, DataSourceConf> getDataSources() {
        return dataSources;
    }

    public void setDataSources(Map<String, DataSourceConf> dataSources) {
        this.dataSources = dataSources;
    }

    public String getTradeFeature() {
        return tradeFeature;
    }

    public void setTradeFeature(String tradeFeature) {
        this.tradeFeature = tradeFeature;
    }

    public List<String> getNeedCryptIndex() {
        return needCryptIndex;
    }

    public void setNeedCryptIndex(List<String> needCryptIndex) {
        this.needCryptIndex = needCryptIndex;
    }

    public List<String> getNeedCryptPhone() {
        return needCryptPhone;
    }

    public void setNeedCryptPhone(List<String> needCryptPhone) {
        this.needCryptPhone = needCryptPhone;
    }

    public List<String> getNeedCryptNick() {
        return needCryptNick;
    }

    public void setNeedCryptNick(List<String> needCryptNick) {
        this.needCryptNick = needCryptNick;
    }

    public List<String> getNeedCryptName() {
        return needCryptName;
    }

    public void setNeedCryptName(List<String> needCryptName) {
        this.needCryptName = needCryptName;
    }

    public List<String> getNeedMoveIndex() {
        return needMoveIndex;
    }

    public void setNeedMoveIndex(List<String> needMoveIndex) {
        this.needMoveIndex = needMoveIndex;
    }

    public String getMoveElasticSearchUrl() {
        return moveElasticSearchUrl;
    }

    public void setMoveElasticSearchUrl(String moveElasticSearchUrl) {
        this.moveElasticSearchUrl = moveElasticSearchUrl;
    }

    public static void setInstance(EsQueryConf instance) {
        EsQueryConf.instance = instance;
    }

    private static EsQueryConf instance = null;
    public static EsQueryConf getInstance(){
        return instance;
    }

    public static DataSourceConf getDataSourceInstance(String dataSource){
        return instance.getDataSources().get(dataSource);
    }

    static{
        instance = ObjectSerializer.read("esquery.json", new TypeReference<EsQueryConf>() {
        }, EsQueryConf.class.getClassLoader());
        if(null == instance){
            throw new RuntimeException("can not load esquery.json");
        }
    }

}

