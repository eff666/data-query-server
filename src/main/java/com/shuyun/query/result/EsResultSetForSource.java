package com.shuyun.query.result;

import java.util.Map;

/**
 * Created by wanghaiwei on 2015/8/21.
 */
public class EsResultSetForSource {
    private String _id;
    private String _type;
    private String _index;
    private Integer _score;
    private String _routing;
    private Map<String, Object> _source;
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

    public Map<String, Object> get_source() {
        return _source;
    }

    public void set_source(Map<String, Object> _source) {
        this._source = _source;
    }

    public String get_routing() {
        return _routing;
    }

    public void set_routing(String _routing) {
        this._routing = _routing;
    }
}