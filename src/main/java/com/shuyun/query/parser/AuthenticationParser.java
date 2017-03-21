package com.shuyun.query.parser;

import java.util.ArrayList;
import java.util.List;

public class AuthenticationParser {
    private List<FiledDataParser> fileds = new ArrayList<FiledDataParser>();
    private String context;

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public List<FiledDataParser> getFileds() {
        return fileds;
    }

    public void setFileds(List<FiledDataParser> fileds) {
        this.fileds = fileds;
    }
}
