/*
package com.shuyun.query.util;

import java.io.IOException;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

*/
/**
 * Created by wanghaiwei on 2015/8/17.
 *//*

public class Test3 {
    private Client client;

    public void init() {
        client = new TransportClient()
                .addTransportAddress(new InetSocketTransportAddress(
                        "localhost", 9300));

    }

    public void close() {
        client.close();
    }

    */
/**
     * index
     *//*

    public void createIndex() {
        for (int i = 0; i < 1000; i++) {
//            SearchResponse;
            */
/*User user = new User();
            user.setId(new Long(i));
            user.setName("huang fox " + i);
            user.setAge(i % 100);*//*

            client.prepareIndex("users", "user").setSource(generateJson(null))
                    .execute().actionGet();

        }
    }

    */
/**
     * 转换成json对象
     *
     * @param user
     * @return
     *//*

    private String generateJson(Object user) {
        String json = "";
        try {
            XContentBuilder contentBuilder = XContentFactory.jsonBuilder()
                    .startObject();
            */
/*contentBuilder.field("id", user.getId() + "");
            contentBuilder.field("name", user.getName());
            contentBuilder.field("age", user.getAge() + "");*//*

            json = contentBuilder.endObject().string();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    private String generateJson(){
        FilterBuilders.andFilter(FilterBuilders.termFilter("", ""), FilterBuilders.termFilter("", "")).toString();
        return null;
    }

    public static void main(String[] agrs) throws Exception{
        */
/*XContentBuilder contentBuilder = XContentFactory.jsonBuilder()
                .startObject();
//        System.out.print(FilterBuilders.andFilter(FilterBuilders.termFilter("test", "123"), FilterBuilders.termFilter("test2", "456")).toString());
//        System.out.print(FilterBuilders.idsFilter("id").toXContent(contentBuilder, null).endObject().string());
        FilterBuilder filter = FilterBuilders.andFilter(FilterBuilders.termFilter("test", "123"), FilterBuilders.termFilter("test2", "456"));
        System.out.print(new FilteredQueryBuilder(null, filter).toString());
        FilterBuilders.termsFilter("", Lists.newArrayList());
        FilterBuilders.andFilter(new FilterBuilder[2]);*//*

//            SearchResponse

       */
/* SearchSourceBuilder sb = SearchSourceBuilder.searchSource();

        System.out.print(sb.toString());*//*

            */
/*Node node = new NodeBuilder()
                    .clusterName("elasticsearch")
                    .data(true) //No shards allocated; or can set client to true
                    .client(true) //No shards allocated; or can set data to false
                    .node();

            Client client = new NodeBuilder()
                    .clusterName("elasticsearch")
                    .data(true) //No shards allocated; or can set client to true
                    .client(true) //No shards allocated; or can set data to false
                    .node().client();

            GetResponse response = client.prepareGet("indexname", "type", "id")
                    .execute()
                    .actionGet();*//*


//            Client client = new TransportClient()
//                    .addTransportAddress(new InetSocketTransportAddress("172.29.1.4", 9300));
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", "elasticsearch.cluster").build();
        Client client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress("172.29.1.4", 9300))
                .addTransportAddress(new InetSocketTransportAddress("172.29.1.5", 9300));

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
                                          Throwable failure) {  }
                })
                .setBulkActions(1000)
                .setBulkSize(new ByteSizeValue(1, ByteSizeUnit.GB))
                .setFlushInterval(TimeValue.timeValueSeconds(5))
                .setConcurrentRequests(1)
                .build();

        SearchResponse scrollResp = client.prepareSearch()
                .setSearchType(SearchType.SCAN)
                .setScroll(new TimeValue(60000))
                .setIndices("taobao")
                .setTypes("item")
                .setQuery(QueryBuilders.matchAllQuery())
                .setSize(100).execute().actionGet();

        long startTime = System.currentTimeMillis();
        System.out.println(startTime);
        while (true) {
            for (SearchHit hit : scrollResp.getHits().getHits()) {
//                    System.out.println(hit.getSource().toString());
                hit.getSource().put("tagId", "");
                bulkProcessor.add(new IndexRequest("taobao_v5", "item")
                        .source(hit.getSource()));
            }

            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
            System.out.println("use millis" + (startTime - System.currentTimeMillis()));
            //Break condition: No hits are returned
            if (scrollResp.getHits().getHits().length == 0) {
                break;
            }
        }

        bulkProcessor.close();
        client.close();
        System.out.println(System.currentTimeMillis());
    }
}*/
