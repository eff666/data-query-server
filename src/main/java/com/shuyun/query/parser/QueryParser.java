package com.shuyun.query.parser;

/**
 * Created by shuyun on 2016/7/14.
 */
public class QueryParser {
    private String query_column;
    private String[] query_column_value;
    private String return_column;
    private String context;

    public String getQuery_column() {
        return query_column;
    }

    public void setQuery_column(String query_column) {
        this.query_column = query_column;
    }

    public String[] getQuery_column_value() {
        return query_column_value;
    }

    public void setQuery_column_value(String[] query_column_value) {
        this.query_column_value = query_column_value;
    }

    public String getReturn_column() {
        return return_column;
    }

    public void setReturn_column(String return_column) {
        this.return_column = return_column;
    }

    public String getReturn_format() {
        return return_format;
    }

    public void setReturn_format(String return_format) {
        this.return_format = return_format;
    }

    private String return_format = "json";

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
