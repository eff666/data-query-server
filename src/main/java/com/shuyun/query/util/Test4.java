/*
package com.shuyun.query.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

*/
/**
 * Created by wanghaiwei on 2015/10/23.
 *//*

public class Test4 {

    static ConcurrentLinkedQueue<SearchHit> queues = new ConcurrentLinkedQueue<SearchHit>();
    static AtomicBoolean isInsert = new AtomicBoolean(true);

    public static void main(String[] agrs) throws Exception{
        final String[] shop_ids = {"121944829", "110107257", "106000574", "105970091", "104575496", "101810022", "101267276", "62405702", "61154158"};
        for(String shop_id:shop_ids){
            Settings settings = ImmutableSettings.settingsBuilder()
                    .put("cluster.name", "elasticsearch.cluster").build();
        */
/*Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", "item.search.prd").build();*//*

            Client client = new TransportClient(settings)
                    .addTransportAddress(new InetSocketTransportAddress("10.173.82.224", 8080));
        */
/*Client client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress("10.10.138.183", 9300));*//*


            File file = new File("g:/testdata/" + shop_id + ".txt");
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String json = null;
            int i = 0;
            while((json = bufferedReader.readLine()) != null){
                int index1 = json.indexOf("num_iid");
                int index2 = json.indexOf(",", index1);
                index1 += 9;
                String id = json.substring(index1, index2);
                client.prepareIndex("taobao", "item_test")
                        .setId(id)
                        .setRouting(shop_id)
                        .setSource(json)
                        .execute()
                        .actionGet();
                i++;
                System.out.println(i);
            }
            bufferedReader.close();
            inputStreamReader.close();
            fileInputStream.close();
            client.close();
//            PutMappingRequest mapping = Requests.putMappingRequest("").type("").source("");

        }



        */
/*new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    File file = new File("g:/testdata/" + shop_id + ".txt");
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    OutputStreamWriter outputStream = new OutputStreamWriter(fileOutputStream);
                    BufferedWriter bufferedWriter = new BufferedWriter(outputStream);
                    Thread.sleep(1000*10);
                    SearchHit searchHit = null;
                    while((searchHit = queues.poll()) != null){
                        bufferedWriter.write(searchHit.getSourceAsString());
                        bufferedWriter.newLine();
                    }
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                    fileOutputStream.close();
                }
                catch (Exception e){

                }

            }
        }).start();*//*


        */
/*for(int i = 0; i < 5; i++){
            new Thread(new Runnable(){
                @Override
                public void run(){
                    try {
                        Thread.currentThread().sleep(250 * 1000);
                    } catch(Exception e){

                    }
                    Settings settings = ImmutableSettings.settingsBuilder()
                            .put("cluster.name", "elasticsearch.cluster").build();
                    Client client = new TransportClient(settings)
                            .addTransportAddress(new InetSocketTransportAddress("10.173.82.224", 8080));
                    BulkProcessor bulkProcessor = BulkProcessor.builder(
                            client,
                            new BulkProcessor.Listener() {
                                @Override
                                public void afterBulk(long l, BulkRequest bulkRequest, BulkResponse bulkResponse) {
                                }
                                @Override
                                public void beforeBulk(long executionId,
                                                       BulkRequest request) {  }
                                @Override
                                public void afterBulk(long executionId,
                                                      BulkRequest request,
                                                      Throwable failure) {
                                    System.out.print("fail...................");
                                }
                            })
                            .setBulkActions(100)
                            .setBulkSize(new ByteSizeValue(1, ByteSizeUnit.GB))
                            .setFlushInterval(TimeValue.timeValueSeconds(5))
                            .setConcurrentRequests(1)
                            .build();
                    while (true){
                        if(!queues.isEmpty()) {
                            try{
                                SearchHit searchHit = queues.poll();
                                bulkProcessor.add(new IndexRequest("taobao", "item_test")
                                        .routing((String)searchHit.getSource().get("shop_id")).source(searchHit.getSource()));
                            }catch(Exception e){
                                System.out.print(e.getMessage());
                            }

                        }
                        if(queues.isEmpty() && !isInsert.get()){
                            bulkProcessor.flush();
                            bulkProcessor.close();
                            break;
                        }
                    }
                }
            }).start();
        }

        SearchResponse scrollResp = client.prepareSearch()
                .setSearchType(SearchType.SCAN)
                .setScroll(new TimeValue(60000))
                .setIndices("taobao")
                .setTypes("item")
                .setQuery(QueryBuilders.matchAllQuery())
                .execute().actionGet();

        long startTime = System.currentTimeMillis();
        System.out.println(startTime);
        while (true) {
            queues.addAll(Arrays.asList(scrollResp.getHits().getHits()));
            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
            System.out.println("use millis" + (startTime - System.currentTimeMillis()));
            if (scrollResp.getHits().getHits().length == 0) {
                isInsert = new AtomicBoolean(false);
                break;
            }
        }

        client.close();
        System.out.println(System.currentTimeMillis());*//*


    }
}
*/
