package com.shuyun.query.meta;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuyun.query.serializer.ObjectSerializer;

import java.util.Arrays;
import java.util.List;

/**
 * Created by shuyun on 2016/7/14.
 */
public class PermissionConf {
    private List<String> needCoder = Arrays.asList();
    private String coderKey = "shuyun123";
    private List<String> userId = Arrays.asList();
    private List<String> userService = Arrays.asList();
    private List<String> memberId = Arrays.asList();
    private List<String> memberService = Arrays.asList();
    private List<String> adminId = Arrays.asList();
    private List<String> adminService = Arrays.asList();
    private List<String> insignificantQueryColumn = Arrays.asList();
    private List<String> addressValidate = Arrays.asList();
    private List<String> addressContext = Arrays.asList();
    private String addressGeoKey;


    public PermissionConf(){}

    public PermissionConf(List<String> needCoder, String coderKey, List<String> userId,
                          List<String> userService, List<String> memberId, List<String> memberService, List<String> adminId, List<String> adminService){
        this.needCoder = needCoder;
        this.coderKey = coderKey;
        this.userId = userId;
        this.userService = userService;
        this.memberId = memberId;
        this.memberService = memberService;
        this.adminId = adminId;
        this.adminService = adminService;
    }

    public List<String> getNeedCoder() {
        return needCoder;
    }

    public void setNeedCoder(List<String> needCoder) {
        this.needCoder = needCoder;
    }

    public String getCoderKey() {
        return coderKey;
    }

    public void setCoderKey(String coderKey) {
        this.coderKey = coderKey;
    }

    public List<String> getUserId() {
        return userId;
    }

    public void setUserId(List<String> userId) {
        this.userId = userId;
    }

    public List<String> getUserService() {
        return userService;
    }

    public void setUserService(List<String> userService) {
        this.userService = userService;
    }

    public List<String> getMemberId() {
        return memberId;
    }

    public void setMemberId(List<String> memberId) {
        this.memberId = memberId;
    }

    public List<String> getMemberService() {
        return memberService;
    }

    public void setMemberService(List<String> memberService) {
        this.memberService = memberService;
    }

    public List<String> getAdminId() {
        return adminId;
    }

    public void setAdminId(List<String> adminId) {
        this.adminId = adminId;
    }

    public List<String> getAdminService() {
        return adminService;
    }

    public void setAdminService(List<String> adminService) {
        this.adminService = adminService;
    }

    public List<String> getInsignificantQueryColumn() {
        return insignificantQueryColumn;
    }

    public void setInsignificantQueryColumn(List<String> insignificantQueryColumn) {
        this.insignificantQueryColumn = insignificantQueryColumn;
    }

    public List<String> getAddressValidate() {
        return addressValidate;
    }

    public void setAddressValidate(List<String> addressValidate) {
        this.addressValidate = addressValidate;
    }

    public List<String> getAddressContext() {
        return addressContext;
    }

    public void setAddressContext(List<String> addressContext) {
        this.addressContext = addressContext;
    }

    public String getAddressGeoKey() {
        return addressGeoKey;
    }

    public void setAddressGeoKey(String addressGeoKey) {
        this.addressGeoKey = addressGeoKey;
    }

    private static PermissionConf instance = null;
    public static PermissionConf getInstance(){
        return instance;
    }

    static{
        instance = ObjectSerializer.read("permission.json", new TypeReference<PermissionConf>() {}, PermissionConf.class.getClassLoader());
        if(null == instance){
            throw new RuntimeException("can not load permission.json");
        }
    }
}
