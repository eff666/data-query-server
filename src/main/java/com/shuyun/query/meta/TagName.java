package com.shuyun.query.meta;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableMap;
import com.shuyun.query.serializer.ObjectSerializer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by wanghaiwei on 2015/8/31.
 */
public class TagName {

    private static ImmutableMap immutableMap;
    public static ImmutableMap getInstance(){
        return immutableMap;
    }

    static{
        Map map = ObjectSerializer.read("tagname.json", new TypeReference<Map<String, String>>() {
        });
        if(null == map){
            throw new RuntimeException("can not load esquery.json");
        }else{
            ImmutableMap.Builder builder = ImmutableMap.builder();
            builder.putAll(map);
            immutableMap = builder.build();
        }
    }

}

