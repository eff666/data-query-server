package com.shuyun.query.parser;

import java.util.List;

/**
 * Created by wanghaiwei on 2015/12/2.
 */
public class UpdateParser {

    private Settings settings;
    private List<String> num_iids;
    private List<String> tagIds;
    private String plus;
    private String shop_id;

    public String getPlus() {
        return plus;
    }

    public void setPlus(String plus) {
        this.plus = plus;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }



    public List<String> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<String> tagIds) {
        this.tagIds = tagIds;
    }

    public List<String> getNum_iids() {
        return num_iids;
    }

    public void setNum_iids(List<String> num_iids) {
        this.num_iids = num_iids;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    @Override
    public String toString() {
        return "UpdateParser{" +
                "settings=" + settings +
                ", num_iids=" + num_iids +
                ", tagIds=" + tagIds +
                ", plus='" + plus + '\'' +
                ", shop_id='" + shop_id + '\'' +
                '}';
    }
}
