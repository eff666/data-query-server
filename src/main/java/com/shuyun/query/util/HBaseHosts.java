package com.shuyun.query.util;

import io.leopard.javahost.JavaHost;
import org.apache.hadoop.conf.Configuration;

import java.util.Properties;

/**
 * Created by shuyun on 2016/7/28.
 */
public class HBaseHosts {

    private static Properties props = new Properties();
    static {
        props.put("spark-01", "10.10.122.65");
        props.put("spark-02", "10.10.144.247");
        props.put("spark-03", "10.10.118.28");
        props.put("spark-04", "10.10.125.8");
        props.put("spark-05", "10.10.142.250");
        props.put("spark-06", "10.10.143.157");
        props.put("spark-07", "10.10.141.159");
        props.put("spark-08", "10.10.121.29");
        props.put("spark-09", "10.10.128.137");
        props.put("spark-33", "10.10.14.123");
        props.put("spark-32", "10.10.2.64");
        props.put("spark-31", "10.10.50.244");
        props.put("spark-30", "10.10.15.77");
        props.put("spark-29", "10.10.2.5");
        props.put("spark-28", "10.10.3.244");
        props.put("spark-27", "10.10.221.169");
        props.put("spark-26", "10.10.240.85");
        props.put("spark-25", "10.10.222.57");
        props.put("spark-24", "10.10.212.183");
        props.put("spark-23", "10.10.217.212");
        props.put("spark-22", "10.10.221.23");
        props.put("spark-21", "10.10.247.165");
        props.put("spark-20", "10.10.153.161");
        props.put("spark-19", "10.10.193.174");
        props.put("spark-18", "10.10.198.111");
        props.put("spark-17", "10.10.186.253");
        props.put("spark-16", "10.10.165.136");
        props.put("spark-15", "10.10.128.3");
        props.put("spark-14", "10.10.129.197");
        props.put("spark-13", "10.10.135.179");
        props.put("spark-12", "10.10.115.202");
        props.put("spark-11", "10.10.121.11");
        props.put("spark-10", "10.10.125.12");
    }

    public static void getJavaHost(){
        JavaHost.updateVirtualDns(props);
    }
}
