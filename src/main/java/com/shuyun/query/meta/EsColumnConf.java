package com.shuyun.query.meta;

import com.fasterxml.jackson.databind.JsonNode;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;
import com.shuyun.query.result.EsResultSetForSource;
import org.apache.htrace.fasterxml.jackson.core.type.TypeReference;
import org.apache.htrace.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by shuyun on 2016/7/14.
 */
public class EsColumnConf {
    private static Logger logger = Logger.getLogger(EsColumnConf.class);
    private static Set<String> esColumn = new HashSet<String>();

    public EsColumnConf(){}

    private static EsColumnConf instance = null;
    public static EsColumnConf getInstance(){
        return instance;
    }

    public static Set<String> getEsColumn() {
        return esColumn;
    }

    public static void setEsColumn(Set<String> esColumn) {
        EsColumnConf.esColumn = esColumn;
    }

    static {
        AsyncHttpClient client = null;
        try {
            AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder();
            builder.setCompressionEnabled(true).setAllowPoolingConnection(true);
            builder.setRequestTimeoutInMs((int) TimeUnit.MINUTES.toMillis(1));
            builder.setIdleConnectionTimeoutInMs((int) TimeUnit.MINUTES.toMillis(1));

            client = new AsyncHttpClient(builder.build());

            String queryStr = "{\"from\": 0,\"size\": 1,\"query\": {\"match_all\": {}}}";
            String url = String.format("http://%s/%s/%s/_search", ShuyunQueryConf.getInstance().getElasticSearchUrl(),
                    ShuyunQueryConf.getInstance().getIndex(), ShuyunQueryConf.getInstance().getType());

            ListenableFuture<Response> future = client.preparePost(url).addHeader("content-type", "application/json")
                    .setBody(queryStr.getBytes("UTF-8")).execute();

            if (future.get().getStatusCode() != 200) {
                logger.error("some error may occur from es!");
                //throw new RuntimeException("some error may occur from es!");
            } else {
                JsonNode jsonNode = new com.fasterxml.jackson.databind.ObjectMapper().readTree(future.get().getResponseBody());
                String result = jsonNode.get("hits").get("hits").get(0).toString();
                EsResultSetForSource source = new ObjectMapper().readValue(result, new TypeReference<EsResultSetForSource>() {
                });
                esColumn = source.get_source().keySet();
                if (null == esColumn && esColumn.isEmpty()) {
                    logger.error("can not load es column.");
                }
            }
        } catch (Exception e) {
            logger.error(e.getCause().getMessage());
        } finally {
            // 关闭资源
            if (null != client) {
                client.closeAsynchronously();
            }
        }
    }
}
