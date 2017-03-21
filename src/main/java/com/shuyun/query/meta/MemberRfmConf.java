package com.shuyun.query.meta;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuyun.query.serializer.ObjectSerializer;

import java.util.Arrays;
import java.util.List;

/**
 * Created by shuyun on 2016/9/23.
 *
 * 位index_rfm和index_member拆分
 */
public class MemberRfmConf {
    private String elasticSearchUrl = "es3.intraweb.shuyun.com";
    private int port = 9400;

    //为迁移到新的es集群
    private String rfmType = "rfm";
    private String memberType = "member";
    private String memberIndex = "index_member_*";
    private String rfmIndex = "index_rfm_*";

    private List<String> rfm_index_1 = Arrays.asList();
    private List<String> rfm_index_2 = Arrays.asList();
    private List<String> rfm_index_3 = Arrays.asList();
    private List<String> rfm_index_4 = Arrays.asList();
    private List<String> rfm_index_5 = Arrays.asList();
    private List<String> rfm_index_6 = Arrays.asList();
    private List<String> rfm_index_7 = Arrays.asList();
    private List<String> rfm_index_8 = Arrays.asList();
    private List<String> rfm_index_9 = Arrays.asList();

    private List<String> member_index_1 = Arrays.asList();
    private List<String> member_index_2 = Arrays.asList();
    private List<String> member_index_3 = Arrays.asList();
    private List<String> member_index_4 = Arrays.asList();
    private List<String> member_index_5 = Arrays.asList();
    private List<String> member_index_6 = Arrays.asList();
    private List<String> member_index_7 = Arrays.asList();
    private List<String> member_index_8 = Arrays.asList();

    public String getElasticSearchUrl() {
        return elasticSearchUrl;
    }

    public void setElasticSearchUrl(String elasticSearchUrl) {
        this.elasticSearchUrl = elasticSearchUrl;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getRfmType() {
        return rfmType;
    }

    public void setRfmType(String rfmType) {
        this.rfmType = rfmType;
    }

    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public String getMemberIndex() {
        return memberIndex;
    }

    public void setMemberIndex(String memberIndex) {
        this.memberIndex = memberIndex;
    }

    public String getRfmIndex() {
        return rfmIndex;
    }

    public void setRfmIndex(String rfmIndex) {
        this.rfmIndex = rfmIndex;
    }

    public List<String> getRfm_index_1() {
        return rfm_index_1;
    }

    public void setRfm_index_1(List<String> rfm_index_1) {
        this.rfm_index_1 = rfm_index_1;
    }

    public List<String> getRfm_index_2() {
        return rfm_index_2;
    }

    public void setRfm_index_2(List<String> rfm_index_2) {
        this.rfm_index_2 = rfm_index_2;
    }

    public List<String> getRfm_index_3() {
        return rfm_index_3;
    }

    public void setRfm_index_3(List<String> rfm_index_3) {
        this.rfm_index_3 = rfm_index_3;
    }

    public List<String> getRfm_index_4() {
        return rfm_index_4;
    }

    public void setRfm_index_4(List<String> rfm_index_4) {
        this.rfm_index_4 = rfm_index_4;
    }

    public List<String> getRfm_index_5() {
        return rfm_index_5;
    }

    public void setRfm_index_5(List<String> rfm_index_5) {
        this.rfm_index_5 = rfm_index_5;
    }

    public List<String> getRfm_index_6() {
        return rfm_index_6;
    }

    public void setRfm_index_6(List<String> rfm_index_6) {
        this.rfm_index_6 = rfm_index_6;
    }

    public List<String> getRfm_index_7() {
        return rfm_index_7;
    }

    public void setRfm_index_7(List<String> rfm_index_7) {
        this.rfm_index_7 = rfm_index_7;
    }

    public List<String> getRfm_index_8() {
        return rfm_index_8;
    }

    public void setRfm_index_8(List<String> rfm_index_8) {
        this.rfm_index_8 = rfm_index_8;
    }

    public List<String> getRfm_index_9() {
        return rfm_index_9;
    }

    public void setRfm_index_9(List<String> rfm_index_9) {
        this.rfm_index_9 = rfm_index_9;
    }

    public List<String> getMember_index_1() {
        return member_index_1;
    }

    public void setMember_index_1(List<String> member_index_1) {
        this.member_index_1 = member_index_1;
    }

    public List<String> getMember_index_2() {
        return member_index_2;
    }

    public void setMember_index_2(List<String> member_index_2) {
        this.member_index_2 = member_index_2;
    }

    public List<String> getMember_index_3() {
        return member_index_3;
    }

    public void setMember_index_3(List<String> member_index_3) {
        this.member_index_3 = member_index_3;
    }

    public List<String> getMember_index_4() {
        return member_index_4;
    }

    public void setMember_index_4(List<String> member_index_4) {
        this.member_index_4 = member_index_4;
    }

    public List<String> getMember_index_5() {
        return member_index_5;
    }

    public void setMember_index_5(List<String> member_index_5) {
        this.member_index_5 = member_index_5;
    }

    public List<String> getMember_index_6() {
        return member_index_6;
    }

    public void setMember_index_6(List<String> member_index_6) {
        this.member_index_6 = member_index_6;
    }

    public List<String> getMember_index_7() {
        return member_index_7;
    }

    public void setMember_index_7(List<String> member_index_7) {
        this.member_index_7 = member_index_7;
    }

    public List<String> getMember_index_8() {
        return member_index_8;
    }

    public void setMember_index_8(List<String> member_index_8) {
        this.member_index_8 = member_index_8;
    }

    private static MemberRfmConf instance = null;
    public static MemberRfmConf getInstance(){
        return instance;
    }

    static {
        instance = ObjectSerializer.read("memberrfm.json", new TypeReference<MemberRfmConf>() {
        }, MemberRfmConf.class.getClassLoader());
        if(null == instance){
            throw new RuntimeException("can not load memberrfm.json");
        }
    }
}
