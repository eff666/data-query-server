package com.shuyun.query.result;

import java.util.List;
import java.util.Map;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHitField;

/**
 * Created by wanghaiwei on 2015/8/21.
 */
public class EsResultSet {
    private String _id;
    private String _type;
    private String _index;
    private Integer _score;
    private Map<String, List<Object>> fields;
    private Object[] sort;

    public Integer get_score() {
        return _score;
    }

    public void set_score(Integer _score) {
        this._score = _score;
    }

    public Object[] getSort() {
        return sort;
    }

    public void setSort(Object[] sort) {
        this.sort = sort;
    }

    public Map<String, List<Object>> getFields() {
        return fields;
    }

    public void setFields(Map<String, List<Object>> fields) {
        this.fields = fields;
    }

    public String get_index() {
        return _index;
    }

    public void set_index(String _index) {
        this._index = _index;
    }

    public String get_type() {
        return _type;
    }

    public void set_type(String _type) {
        this._type = _type;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

}