package com.shuyun.query.meta;

import java.util.Arrays;
import java.util.List;

public class DataSourceConf{


    private String index = "taobao";
    private String table = "item";
    private int limit = 200;
    private String routing = "shop_id";
    private List<String> needSource = Arrays.asList();
    private List<String> numericalDimensions = Arrays.asList();
    private List<String> decimalDimensions = Arrays.asList();
    private List<String> date = Arrays.asList();
    private List<String> stringDimensions = Arrays.asList();
    private List<String> needCrypt = Arrays.asList();


    public DataSourceConf(){
    }
    public DataSourceConf(String index, String table, List<String> needSource, int limit, List<String> numericalDimensions,
                          List<String> decimalDimensions, List<String> date, List<String> stringDimensions) {
        this.index = index;
        this.table = table;
        this.needSource = needSource;
        this.limit = limit;
        this.numericalDimensions = numericalDimensions;
        this.decimalDimensions = decimalDimensions;
        this.date = date;
        this.stringDimensions = stringDimensions;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public List<String> getNeedSource() {
        return needSource;
    }

    public void setNeedSource(List<String> needSource) {
        this.needSource = needSource;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public List<String> getDate() {
        return date;
    }

    public void setDate(List<String> date) {
        this.date = date;
    }

    public List<String> getDecimalDimensions() {
        return decimalDimensions;
    }

    public void setDecimalDimensions(List<String> decimalDimensions) {
        this.decimalDimensions = decimalDimensions;
    }

    public List<String> getNumericalDimensions() {
        return numericalDimensions;
    }

    public void setNumericalDimensions(List<String> numericalDimensions) {
        this.numericalDimensions = numericalDimensions;
    }

    public List<String> getStringDimensions() {
        return stringDimensions;
    }

    public void setStringDimensions(List<String> stringDimensions) {
        this.stringDimensions = stringDimensions;
    }

    public String getRouting() {
        return routing;
    }

    public void setRouting(String routing) {
        this.routing = routing;
    }

    public List<String> getNeedCrypt() {
        return needCrypt;
    }

    public void setNeedCrypt(List<String> needCrypt) {
        this.needCrypt = needCrypt;
    }
}

