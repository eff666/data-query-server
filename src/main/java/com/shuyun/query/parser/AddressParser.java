package com.shuyun.query.parser;

import java.util.List;

public class AddressParser {
    private String buyer_nick;
    private String address_data;
    private List<String> address_type;
    private String context;

    public String getBuyer_nick() {
        return buyer_nick;
    }

    public void setBuyer_nick(String buyer_nick) {
        this.buyer_nick = buyer_nick;
    }


    public String getAddress_data() {
        return address_data;
    }

    public void setAddress_data(String address_data) {
        this.address_data = address_data;
    }

    public List<String> getAddress_type() {
        return address_type;
    }

    public void setAddress_type(List<String> address_type) {
        this.address_type = address_type;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
