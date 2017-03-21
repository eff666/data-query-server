/*
package com.shuyun.query.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Response;
import com.shuyun.query.meta.DownloadFileConf;
import com.shuyun.query.meta.FileDownload;
import com.shuyun.query.meta.FileType;
import com.shuyun.query.parser.UpdateParser;
import com.shuyun.query.parser.sql.ParserSql;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.InternalAggregations;
import org.elasticsearch.search.aggregations.bucket.terms.InternalTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.aggregations.metrics.InternalNumericMetricsAggregation;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.internal.InternalSearchHits;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

*/
/*

public class Test5 {

    private static void toAggsMap(List<Map<String, Object>> subresults, Terms.Bucket bucket) throws Exception {
        Map<String, Aggregation> aggregations = bucket.getAggregations().getAsMap();
        if (aggregations.size() == 0){

        } else{
            Map<String, Object> subresultforMetricsAggregation = null;
            int index = 0;
            for (String key : aggregations.keySet()){
                if(aggregations.get(key) instanceof InternalNumericMetricsAggregation.SingleValue){
                    InternalNumericMetricsAggregation.SingleValue singleValue = (InternalNumericMetricsAggregation.SingleValue)aggregations.get(key);
                    if(subresultforMetricsAggregation == null) subresultforMetricsAggregation = new HashMap<>();
                    subresultforMetricsAggregation.put(key, singleValue.getValueAsString());
                    if (aggregations.keySet().size() == ++index) subresults.add(subresultforMetricsAggregation);
                    continue;
                }
                Terms terms = (Terms)aggregations.get(key);
                Collection<Terms.Bucket> buckets = terms.getBuckets();
                if(buckets.iterator().next().getAggregations() == null || buckets.iterator().next().getAggregations().getAsMap().size() < 1){
                    Iterator<Terms.Bucket> bucketIterator = buckets.iterator();
                    while(bucketIterator.hasNext()){
                        Map<String, Object> subresult = new HashMap<>();
                        subresult.put(key, bucketIterator.next().getKey());
                        subresults.add(subresult);
                    }

                } else {
                    for (Terms.Bucket bucket1 : buckets) {
                        toAggsMap(subresults, bucket1);
                        for (Map<String, Object> subresult : subresults){
                            subresult.put(key, bucket1.getKey());
                        }
                    }
                }
            }
        }

    }

    public static void main(String[] agrs) throws Exception{
        SortBuilder sortBuilder = SortBuilders.fieldSort("aa").order(SortOrder.ASC);
        int index = sortBuilder.toString().indexOf("{");
        String json = "{" + sortBuilder.toString().substring(0, index) + ":" + sortBuilder.toString().substring(index) + "}";
        System.out.println(json);
//        System.out.println(AggregationBuilders.terms("1").field("type").getName());
        */
/*Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", "elasticsearch.cluster").build();
        Client client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress("172.29.1.6", 9300));
        String aa = "{\"from\":0,\"size\":0,\"_source\":{\"includes\":[\"shop_id\",\"is_fenxiao\",\"AVG\",\"MIN\"],\"excludes\":[]},\"fields\":[\"shop_id\",\"is_fenxiao\"],\"aggregations\":{\"shop_id\":{\"terms\":{\"field\":\"shop_id\",\"size\":2},\"aggregations\":{\"is_fenxiao\":{\"terms\":{\"field\":\"is_fenxiao\",\"size\":0},\"aggregations\":{\"AVG(price)\":{\"avg\":{\"field\":\"price\"}},\"MIN(price)\":{\"min\":{\"field\":\"price\"}}}}}}}}";

        TermsBuilder termsBuilder = AggregationBuilders.terms("shop_id").field("shop_id");
        TermsBuilder termsBuilder1 = AggregationBuilders.terms("is_fenxiao").field("is_fenxiao");
//        TermsBuilder termsBuilder2 = AggregationBuilders.terms("price").field("price");
//        termsBuilder1.subAggregation(termsBuilder2);
//        termsBuilder1.subAggregation(AggregationBuilders.avg("price").field("price"));
//        termsBuilder1.subAggregation(AggregationBuilders.count("num_iid").field("num_iid"));
        termsBuilder.subAggregation(termsBuilder1);
        SearchResponse sr = client.prepareSearch()
                .setSearchType(SearchType.COUNT)
                .setIndices("taobao")
                .setTypes("item")
//                .setFrom(0)
//                .setSize(0)
//                .setQuery(QueryBuilders.matchAllQuery())
                .addAggregation(AggregationBuilders.count("num_iid").field("num_iid"))
                .execute().actionGet();
        InternalAggregations aggregations = (InternalAggregations)sr.getAggregations();
        InternalSearchHits internalSearchHits = (InternalSearchHits)sr.getHits();
        List<Map<String, Object>> results = new ArrayList<>();
        Map<String, Object> subresultforMetricsAggregation = null;
        int index = 0;
        for (String key : aggregations.getAsMap().keySet()){
            if(aggregations.get(key) instanceof InternalNumericMetricsAggregation.SingleValue){
                InternalNumericMetricsAggregation.SingleValue singleValue = (InternalNumericMetricsAggregation.SingleValue)aggregations.get(key);
                if(subresultforMetricsAggregation == null) subresultforMetricsAggregation = new HashMap<>();
                subresultforMetricsAggregation.put(key, singleValue.getValueAsString());
                if (aggregations.getAsMap().size() == ++index) results.add(subresultforMetricsAggregation);
                continue;
            }
            InternalTerms terms = aggregations.get(key);
            Collection<Terms.Bucket> buckets = terms.getBuckets();
            for (Terms.Bucket bucket : buckets) {
                String value = bucket.getKey();
                List<Map<String, Object>> subresults = new ArrayList<>();
                toAggsMap(subresults, bucket);
                if (subresults.size() == 0){
                    Map<String, Object> subresult = new HashMap<>();
                    subresult.put(key, value);
                    subresults.add(subresult);
                } else {
                    for (Map<String, Object> subresult : subresults){
                        subresult.put(key, value);
                    }
                }
                results.addAll(subresults);
            }
        }
//        Aggregations aggregations = sr.getAggregations();
        System.out.println(1124);
*//*


        */
/*.aggregations(AggregationBuilders.terms("shop_id").size(10).toString().getBytes())
        DefaultHttpClient routerClient = new DefaultHttpClient();
//        HttpPut httppost = new HttpPut("http://172.29.1.4:9200/taobao_v6/test/1");
        HttpPost httppost = new HttpPost("http://172.29.1.4:9200/taobao_v6/test/3");
        httppost.setHeader("content-type", "application/json");
        httppost.setEntity(new StringEntity("{\"name\" : \"svdgfs\"}"));
        HttpResponse response = routerClient.execute(httppost);
        System.out.println(response.getStatusLine().getStatusCode());

        BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        System.out.println(in.readLine());


        AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder();
        builder.setCompressionEnabled(true).setAllowPoolingConnection(true);

        AsyncHttpClient client = new AsyncHttpClient(builder.build());
        String query = SearchSourceBuilder.searchSource().query(QueryBuilders.termQuery("name", "testest")).toString();
        System.out.println(query);
        Future<Response> response = client.prepareDelete("http://172.29.1.4:9200/taobao_v6/test/_query?pretty").addHeader("content-type", "application/json").setBody(query).execute();
        System.out.println(response.get().getStatusCode());

        Request request = new RequestBuilder("DELETE")
                .setUrl("http://172.29.1.4:9200/taobao_v6/test")
                .setHeader()
                .setBody("")
                .build();*//*


        */
/*String insertSql = "select name from test where name = 'svdgfs' limit 10 offset 0";
        ParserSql parserSql = new ParserSql(insertSql);
        parserSql.parse();
        SearchSourceBuilder sb = SearchSourceBuilder.searchSource();
        if( parserSql.getColumns().size() > 0 ){
            sb.fields(parserSql.getColumns());
        }
        sb.from(parserSql.getOffset().intValue());
        sb.size(parserSql.getLimit().intValue() == 0 ? 50: parserSql.getLimit().intValue());
        if(parserSql.getFilters().size() > 0){
            QueryBuilder queryBuilder = QueryBuilders.boolQuery();
            for(ParserSql.Filter filter : parserSql.getFilters()){
                if(filter.getOp() == ParserSql.OP.EQUAL){
                    ((BoolQueryBuilder)queryBuilder).must(QueryBuilders.termQuery(filter.getCol(), filter.getValue()));
                }else{

                }

            }
            sb.query(queryBuilder);
        }else{
            sb.query(QueryBuilders.matchAllQuery());
        }

        System.out.print(sb.toString());
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", "elasticsearch.cluster").build();
        Client client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress("172.29.1.4", 9300))
                .addTransportAddress(new InetSocketTransportAddress("172.29.1.5", 9300));

        SearchResponse scrollResp = client.prepareSearch().internalBuilder(sb)
                .setSearchType(SearchType.SCAN)
                .setScroll(new TimeValue(60000))
                .setIndices("taobao_v6")
                .setTypes("test")
                .setSize(100).execute().actionGet();

        while (true) {
            if(scrollResp.getHits().getHits().length > 0){
                System.out.print(scrollResp.getHits().getHits()[0].fields().get("name").getValues().get(0));
            }
//            System.out.print(scrollResp.getHits().getHits()[0].fields().toString());
            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
            if (scrollResp.getHits().getHits().length == 0) {
                break;
            }
        }

        client.close();*//*

        */
/*Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", "elasticsearch.cluster").build();
        Client client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress("10.173.82.224", 8080));

        *//*
*/
/*SearchResponse scrollResp = client.prepareSearch()
                .setSearchType(SearchType.SCAN)
                .setScroll(new TimeValue(60000))
                .setIndices("taobao_v6")
                .setTypes("test")
                .setQuery(QueryBuilders.matchAllQuery())
                .setSize(100).execute().actionGet();

        while (true) {
            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
            if (scrollResp.getHits().getHits().length == 0) {

                break;
            }
        }

        client.close();
        System.out.println(System.currentTimeMillis());*//*
*/
/*

        CountResponse response = client.prepareCount("taobao")
                .setTypes("item")
                .execute()
                .actionGet();
        long count = response.getCount();

        System.out.print(count);*//*


        */
/*SearchSourceBuilder sb = SearchSourceBuilder.searchSource();

        FilterBuilders.missingFilter("sex_modified")
        QueryBuilder q = QueryBuilders.boolQuery().must(QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(), FilterBuilders.missingFilter("sex_modified"))).must(QueryBuilders.termQuery("sex", "f"));
        sb.query(QueryBuilders.boolQuery().should(QueryBuilders.termQuery("sex_modified", "f")).should(q));

        System.out.print(sb.toString());*//*

    }
}
*/
