package com.shuyun.query.util;


import com.shuyun.query.meta.EsQueryConf;
import org.elasticsearch.client.transport.TransportClient;
//import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class TransportClientUtil {

    //    static Map<String, String> m = new HashMap<String, String>();
   /* static Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", EsQueryConf.getInstance().getClusterName()).put("client.transport.sniff", true).build();

    // 创建私有对象
    private static TransportClient client;

    static {
        try {
            Class<?> clazz = Class.forName(TransportClient.class.getName());
            Constructor<?> constructor = clazz.getDeclaredConstructor(Settings.class);
            constructor.setAccessible(true);
            client = (TransportClient) constructor.newInstance(settings);
            for(String url :EsQueryConf.getInstance().getElasticSearchUrl()){
                client.addTransportAddress(new InetSocketTransportAddress(url, 9300));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 取得实例
    public static synchronized TransportClient getTransportClient() {
        return client;
    }*/
}
