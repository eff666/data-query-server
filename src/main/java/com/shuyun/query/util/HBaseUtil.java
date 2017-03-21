package com.shuyun.query.util;

/**
 * Created by wanghaiwei on 2016/5/10.
 */
import java.io.File;
import java.io.IOException;

import com.shuyun.query.meta.TagseviceConf;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;


public class HBaseUtil {
//    private static Configuration conf;
////    private static Connection hConnection;
//    static {
//        //System.setProperty("hadoop.home.dir", Thread.currentThread().getContextClassLoader().getResource("hadoop-common-220-bin-master").getPath());
//
//        HBaseHosts.getJavaHost();
//
//        conf = HBaseConfiguration.create();
//        conf.set("hbase.zookeeper.quorum", TagseviceConf.getInstance().getHbase_zookeeper_quorum());
//        conf.set("hbase.zookeeper.property.clientPort", TagseviceConf.getInstance().getHbase_zookeeper_property_clientPort());
//        conf.set("zookeeper.znode.parent", TagseviceConf.getInstance().getZookeeper_znode_parent());
//
//    //        conf.set("hbase.client.retries.number", "3");//default 31,调用异常重试次数
//    //        conf.set("hbase.rpc.timeout", "60000");//rpc的超时时间,default 60000(1min)
//    //        conf.set("hbase.rpc.shortoperation.timeout", "3000");
//
//          //超时
//          conf.set("hbase.rpc.timeout", "60000");//rpc的超时时间,default 60000(1min)
//          conf.set("hbase.rpc.shortoperation.timeout", "10000");//配置rpc的超时时间,另一个版本的hbase.rpc.timeout，default 10000=10s
//          conf.set("ipc.socket.timeout", "20000");//socket建立链接的超时时间,default 20s,应该小于或者等于hbase.rpc.timeout的超时时
//
//         conf.set("hbase.client.retries.number", "3");//default 31,调用异常重试次数
//         conf.set("hbase.client.pause", "1000");//重试的休眠时间,default 1s
//         conf.set("zookeeper.recovery.retry", "3");//zk的重试次数,且如果hbase集群出问题了，每次重试均会对zk进行重试操作，zk的重试总次数是：hbase.client.retries.number * zookeeper.recovery.retry
//         conf.set("zookeeper.recovery.retry.intervalmill", "1000");//zk重试的休眠时间，默认为1s
//    }
//
//    public static Configuration getHbaseConnection() {
//        return conf;
//    }
//
//    /*public static Table getHTable(String tableName) throws IOException {
//        return hConnection.getTable(TableName.valueOf(tableName));
//    }*/
//
//    /*public static void main(String[] agrs) throws Exception{
//        HBaseUtil
//    }*/
}
