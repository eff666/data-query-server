package com.shuyun.query.parser;

import com.shuyun.query.parser.filter.DimFilter;
import com.shuyun.query.parser.search.DimSearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonParser {

    private Settings settings;
    private List<String> fields;
    private DimSearch querys;
    private DimFilter filters;
    private List<Map<String, String>> sort = new ArrayList<Map<String, String>>();

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public DimFilter getFilters() {
        return filters;
    }

    public void setFilters(DimFilter filters) {
        this.filters = filters;
    }

    public List<Map<String, String>> getSort() {
        return sort;
    }

    public void setSort(List<Map<String, String>> sort) {
        this.sort = sort;
    }

    public DimSearch getQuerys() {
        return querys;
    }

    public void setQuerys(DimSearch querys) {
        this.querys = querys;
    }

    @Override
    public String toString() {
        return "JsonParser{" +
                "settings=" + settings +
                ", fields=" + fields +
                ", filters=" + filters +
                ", sort=" + sort +
                '}';
    }
}
