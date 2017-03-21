package com.shuyun.query.meta;

import java.util.HashMap;
import java.util.Map;

public class AddressValidateResult {
    private String flag;
    private String message;
    private Map<String, String> result = new HashMap<String, String>();

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getResult() {
        return result;
    }

    public void setResult(Map<String, String> result) {
        this.result = result;
    }
}
