package com.shuyun.query.meta;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.shuyun.query.serializer.ObjectSerializer;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.FileInputStream;
import java.util.Properties;

public class QueryConfig {
    // min number actors for performing query
    @JsonProperty @Min(1) @Max(3600)
    private int lowerBound = 5;

    // max number actors for performing query
    @JsonProperty @Min(100) @Max(3600)
    private int upperBound = 1000;

    public int getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(int lowerBound) {
        this.lowerBound = lowerBound;
    }

    public int getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(int upperBound) {
        this.upperBound = upperBound;
    }

    public static QueryConfig getInstance(){
        return cfg;
    }

    private static QueryConfig cfg = null;
    static{
        cfg = ObjectSerializer.read("config.json", new TypeReference<QueryConfig>() {
        }, QueryConfig.class.getClassLoader());
        if(null == cfg){
            cfg = new QueryConfig();
        }
    }

}
