package com.shuyun.query.util;

import com.google.common.collect.Lists;

import java.security.MessageDigest;
import java.util.*;

public class YeahmobiUtils {

    /*public final static String MD5(String arg0) {
        //用于加密的字符
        *//*char md5String[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F' };
        try {
            //使用平台的默认字符集将此 String 编码为 byte序列，并将结果存储到一个新的 byte数组中
            byte[] btInput = arg0.getBytes();
            
            // 获得指定摘要算法的 MessageDigest对象，此处为MD5
            //MessageDigest类为应用程序提供信息摘要算法的功能，如 MD5 或 SHA 算法。
            //信息摘要是安全的单向哈希函数，它接收任意大小的数据，并输出固定长度的哈希值。 
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            //System.out.println(mdInst);  
            //MD5 Message Digest from SUN, <initialized>
            
            //MessageDigest对象通过使用 update方法处理数据， 使用指定的byte数组更新摘要
            mdInst.update(btInput);
            //System.out.println(mdInst);  
            //MD5 Message Digest from SUN, <in progress>
            
            // 摘要更新之后，通过调用digest（）执行哈希计算，获得密文
            byte[] md = mdInst.digest();
            //System.out.println(md);
            
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            //System.out.println(j);
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {   //  i = 0
                byte byte0 = md[i];  //95
                str[k++] = md5String[byte0 >>> 4 & 0xf];    //    5  
                str[k++] = md5String[byte0 & 0xf];   //   F
            }
            
            //返回经过加密后的字符串
            return new String(str);
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }*//*
        return null;
    }*/


    public final static String getRandomString(int number){
        Random ran = new Random();
        int length = ran.nextInt(number);
        StringBuffer sb = new StringBuffer(length);
        for (int i = 0; i < length; i++) {
            sb.append((char) (ran.nextInt(95) + 32));
        }
        return sb.toString();
    }

    public final static String converseString(String id){
        int value = Math.abs(id.hashCode()) % 14;
        if(value < 10){
            return "0" + value + "-" + id;
        }
        return value + "-" + id;
    }
    
    public static void main(String[] args) throws Exception{

/*String callBackUrl = "http://api.yeahmobi.com/Report/setReferraFileReady?unique_key=%s&file_url=%s&verification_code=%s";
        // md5（unique_key+file_url+“Yeahmobif3899843bc09ff972ab6252ab3c3cac6”）
        String verificationCode = MD5("" + "" + "Yeahmobif3899843bc09ff972ab6252ab3c3cac6");
        System.out.println(String.format(callBackUrl, "111", "dfdf", verificationCode));*//*

//        byte[] digest= MD5("es1");
        List<Node> lists = new ArrayList<>();
        lists.add(new Node("123"));
        lists.add(new Node("456"));
        LoadBalanceUtil.produceMoreNodes(lists, 10);
        Node node = LoadBalanceUtil.getPrimary(getRandomString(20));

        System.out.print(node);*/
    }
}