package com.shuyun.query.process;


import com.shuyun.query.result.EsResultSet;
import com.google.common.base.Function;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class SortTransformer implements Function<String, Object> {

    public SortTransformer(EsResultSet row, LinkedHashSet<String> sortFields) {
        this.row = row;
        this.sortFields = sortFields;

    }

    final EsResultSet row;
    final LinkedHashSet<String> sortFields;

    @Override
    public Object apply( String input) {
        boolean a = sortFields.contains(input);
        int i = 0;
        for(String str : sortFields){
            if(str.equalsIgnoreCase(input)){
                break;
            }else{
                i++;
            }
        }

        return row.getSort()[i];
    }
}
