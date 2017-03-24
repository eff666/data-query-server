package com.shuyun.query.util;

import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

public class CountNumBySecond {

    public static void main(String[] agrs) throws Exception{

       /* Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", "elasticsearch.cluster").build();
        Client client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress("172.29.1.4", 9300));

        long i = 0;
        long count = 0;

        do {
            i = count;
            count = client.prepareCount("taobao_v5")
                    .setTypes("lala")
                    .execute()
                    .actionGet().getCount();
            System.out.print(count - i);
            Thread.currentThread().sleep(1000);
        } while(count - i > 0);*/



    }

}
