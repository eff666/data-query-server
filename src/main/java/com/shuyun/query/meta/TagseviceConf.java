package com.shuyun.query.meta;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuyun.query.serializer.ObjectSerializer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TagseviceConf {

    private List<String> elasticSearchUrl = Arrays.asList("10.10.128.242", "10.10.138.183");
    private int port = 9200;
    private String index;
    private String mapping;
    private String tagIndex;
    private String tagType;
    private String scoreIndex;
    private String scoreType;
    private String primarykey;
    private List<String> types;

//    private String hbase_tablename;
//    private String hbase_score_tablename;
//    private String hadoop_home_dir;
//    private String hbase_zookeeper_quorum;
//    private String hbase_zookeeper_property_clientPort;
//    private String zookeeper_znode_parent;

    public TagseviceConf(){
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

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getMapping() {
        return mapping;
    }

    public void setMapping(String mapping) {
        this.mapping = mapping;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public String getPrimarykey() {
        return primarykey;
    }

    public void setPrimarykey(String primarykey) {
        this.primarykey = primarykey;
    }

    public String getTagIndex() {
        return tagIndex;
    }

    public void setTagIndex(String tagIndex) {
        this.tagIndex = tagIndex;
    }

    public String getTagType() {
        return tagType;
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
    }

    public String getScoreIndex() {
        return scoreIndex;
    }

    public void setScoreIndex(String scoreIndex) {
        this.scoreIndex = scoreIndex;
    }

    public String getScoreType() {
        return scoreType;
    }

    public void setScoreType(String scoreType) {
        this.scoreType = scoreType;
    }

    private static TagseviceConf instance = null;
    public static TagseviceConf getInstance(){
        return instance;
    }

    static{
        instance = ObjectSerializer.read("tagservice.json", new TypeReference<TagseviceConf>() {
        }, TagseviceConf.class.getClassLoader());
        if(null == instance){
            throw new RuntimeException("can not load esquery.json");
        }
    }

}

