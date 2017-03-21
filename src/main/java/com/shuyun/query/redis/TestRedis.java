package com.shuyun.query.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

public class TestRedis {
    public static void main(String[] args){
        JedisPoolConfig jConf = new JedisPoolConfig();
        HAJedisClient haClient = new HAJedisClient(1, "127.0.0.1", 6379, null, jConf);
        haClient.del("wang");
        Jedis jedis = haClient.getJedisResources();
//        jedis.set("wang", "123");
//        jedis.lpush("wang", "a", "b", "c", "d", "e");
//        jedis.get

        jedis.del("java framework");
        jedis.lpush("java framework", "a");
        jedis.lpush("java framework","struts");
        jedis.lpush("java framework","hibernate");
        //再取出所有数据jedis.lrange是按范围取出，
        // 第一个是key，第二个是起始位置，第三个是结束位置，jedis.llen获取长度 -1表示取得所有
        System.out.println(jedis.lrange("java framework",0,-1));
//        jedis.del("java framework");
        jedis.rpush("java framework","spring");
//        jedis.rpush("java framework","struts");
//        jedis.rpush("java framework","hibernate");
//        System.out.println(jedis.lrange("java framework",0,-1));
//        System.out.print(haClient.getValue("wang"));
    }
}
