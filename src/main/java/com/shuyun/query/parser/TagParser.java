package com.shuyun.query.parser;

/**
 * Created by shuyun on 2016/8/1.
 */
public class TagParser {
    private String type;
    private String value;
    private String dp_id;
    private String buyer_nick;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDp_id() {
        return dp_id;
    }

    public void setDp_id(String dp_id) {
        this.dp_id = dp_id;
    }

    public String getBuyer_nick() {
        return buyer_nick;
    }

    public void setBuyer_nick(String buyer_nick) {
        this.buyer_nick = buyer_nick;
    }

    @Override
    public String toString(){
        return "TagParser{" +
                "type=" + type +
                ", value=" + value +
                ", dp_id=" + dp_id +
                ", buyer_nick=" + buyer_nick +
                '}';
    }
}

