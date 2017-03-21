package com.shuyun.query.jersey.context;

import akka.actor.ActorSystem;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.ListenableFuture;
import com.shuyun.query.meta.*;
import com.shuyun.query.parser.QueryParser;
import com.shuyun.query.util.DESCoderUtil;
import org.apache.log4j.Logger;
import org.elasticsearch.common.Strings;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.glassfish.jersey.server.ManagedAsync;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by shuyun on 2016/7/14.
 */
@Path("/shuyunsearch")
public class ShuyunQueryService {
    private static Logger logger = Logger.getLogger(ShuyunQueryService.class);
    private static AsyncHttpClient client = null;
    Message errorResult = new Message();
    ReportResultForEs reportResult = new ReportResultForEs();

    @Context
    ActorSystem actorSystem;

    @GET
    @ManagedAsync
    @Produces(MediaType.APPLICATION_JSON)
    public void doGet(@QueryParam("query") final String queryData, @Suspended final AsyncResponse res) {
        try {
            doPost(queryData, res);
        } catch (Exception e){
            String msg = "Error occured: " + e.getClass().getName() + " " + e.getMessage();
            errorResult.setFlag("fail");
            errorResult.setMsg(msg);
            logger.error(msg, e);
            res.resume(Response.status(201).entity(errorResult).build());
            return;
        }
    }

    @POST
    @ManagedAsync
    @Produces(MediaType.APPLICATION_JSON)
    public void doPost(@FormParam("query") String queryData, @Suspended final AsyncResponse res) throws Exception {
        if (Strings.isNullOrEmpty(queryData)) {
            errorResult.setFlag("fail");
            errorResult.setMsg("query can not be blank.");
            res.resume(Response.status(201).entity(errorResult).build());
            return;
        }
        if(queryData.indexOf(" ") > -1){
            queryData = queryData.replaceAll(" ", "+");
        }
        final Stopwatch stopwatch = new Stopwatch().start();
        logger.info("query begin! query is: [" + queryData + "]");

        QueryParser queryParser = null;
        try {
            queryParser = new ObjectMapper().readValue(queryData, new TypeReference<QueryParser>() {});
        } catch (IOException e) {
            errorResult.setFlag("fail");
            errorResult.setMsg("parser fail.");
            res.resume(Response.status(201).entity(errorResult).build());
            return;
        }

        if(null == queryParser){
            logger.error("parser fail.");
            errorResult.setFlag("fail");
            errorResult.setMsg("parser fail.");
            res.resume(Response.status(201).entity(errorResult).build());
            return;
        }

        //validate
        validate(queryParser);

        requestEs(queryParser, res);

        stopwatch.stop();
        logger.info("query end! query cost: " + stopwatch.elapsedTime(TimeUnit.MILLISECONDS));
    }

    private void requestEs(QueryParser queryParser, final AsyncResponse res) throws Exception{
        try {
            AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder();
            builder.setCompressionEnabled(true).setAllowPoolingConnection(true);
            builder.setRequestTimeoutInMs((int) TimeUnit.MINUTES.toMillis(1));
            builder.setIdleConnectionTimeoutInMs((int) TimeUnit.MINUTES.toMillis(1));

            client = new AsyncHttpClient(builder.build());

            String url = String.format("http://%s/%s/%s/_search", ShuyunQueryConf.getInstance().getElasticSearchUrl(),
                    ShuyunQueryConf.getInstance().getIndex(), ShuyunQueryConf.getInstance().getType());

            String queryColumn = queryParser.getQuery_column();
            String[] queryColumnValue = queryParser.getQuery_column_value();
            QueryBuilder queryBuilder = QueryBuilders.boolQuery();
            for(String value : queryColumnValue){
                ((BoolQueryBuilder)queryBuilder).should(QueryBuilders.matchPhraseQuery(queryColumn, value));
            }
            SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource();
            sourceBuilder.query(queryBuilder);
            sourceBuilder.from(ShuyunQueryConf.getInstance().getFrom());
            sourceBuilder.size(ShuyunQueryConf.getInstance().getSize());
            String queryStr = sourceBuilder.toString();


            requestHttpAsync(queryParser, res, url, queryStr);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != client) {
                client.closeAsynchronously();
            }
        }
    }

    private void requestHttpAsync(QueryParser queryParser, final AsyncResponse res, String url, String queryStr) throws Exception{
        try {
            ListenableFuture<com.ning.http.client.Response> future = client.preparePost(url).addHeader("content-type", "application/json")
                    .setBody(queryStr.getBytes("UTF-8")).execute();

            String retureColumn = queryParser.getReturn_column();
            String queryColumn = queryParser.getQuery_column();
            String[] queryColumnValue = queryParser.getQuery_column_value();
            Map<Object, Object> maps = new HashMap<Object, Object>();

            if (future.get().getStatusCode() != 200) {
                logger.error("some error may occur from es! query is [" + queryStr + "]" );
                throw new RuntimeException("some error may occur from es!");
            } else {
                JsonNode jsonNode = new com.fasterxml.jackson.databind.ObjectMapper().readTree(future.get().getResponseBody());
                List<JsonNode> listSource = jsonNode.findValues("_source");

                if(listSource.size() > 0) {
                    String initKey = DESCoderUtil.initKey(PermissionConf.getInstance().getCoderKey());
                    for (JsonNode source : listSource) {
                        try {
                            String returnColumnValue = source.get(retureColumn).textValue();
                            if (returnColumnValue != null) {
                                byte[] encrypt = DESCoderUtil.encrypt(returnColumnValue.getBytes(), initKey);
                                maps.put(source.get(queryColumn).textValue(), encrypt);
                            } else {
                                maps.put(source.get(queryColumn).textValue(), returnColumnValue);
                            }
                        } catch (NullPointerException nu){
                            maps.put(source.get(queryColumn).textValue(), null);
                        }
                    }

                    Map<String, Object> linkedHashMap = new LinkedHashMap<String, Object>();
                    for (String key : queryColumnValue) {
                        linkedHashMap.put(key, maps.get(key));
                    }

                    reportResult.setFlag("success");
                    reportResult.setMsg("success");
                    reportResult.setPage(new ReportPage(listSource.size(), maps.size()));
                    reportResult.append(linkedHashMap);
                    res.resume(Response.status(200).entity(reportResult).build());
                    return;
                }else {
                    //logger.error("no match result from es. query is:[ " + queryStr + "]");
                    errorResult.setFlag("fail");
                    errorResult.setMsg("Sorry, the user is not exist from es.");
                    res.resume(Response.status(201).entity(errorResult).build());
                    return;
                }
            }
        } catch(Exception e){
            if(e instanceof java.util.concurrent.ExecutionException && null != e.getCause()){
                throw new RuntimeException(e.getCause().getMessage());
            }else{
                throw e;
            }
        }
    }

    public void validate(QueryParser jsonParser){
        //查询验证
        String queryColumn = jsonParser.getQuery_column();
        String returnColumn = jsonParser.getReturn_column();
        String[] query_column_value = jsonParser.getQuery_column_value();
        Set<String> queryColumns = EsColumnConf.getEsColumn();
        PermissionConf permissionConf = PermissionConf.getInstance();

        if(com.google.common.base.Strings.isNullOrEmpty(queryColumn)){
            throw new RuntimeException("query_column can not be blank!");
        }
        if(com.google.common.base.Strings.isNullOrEmpty(returnColumn)){
            throw new RuntimeException("return_column can not be blank!");
        }
        if (query_column_value.length == 0) {
            throw new RuntimeException("return_column_value can not be blank!");
        }
        if(query_column_value.length > ShuyunQueryConf.getInstance().getSize()){
            throw new RuntimeException("Sorry, you query_column_value exceeds the maximum limit, you can check up to " + ShuyunQueryConf.getInstance().getSize() + ".");
        }
        if(PermissionConf.getInstance().getInsignificantQueryColumn().contains(queryColumn)){
            throw new RuntimeException("Sorry, the query_column is not significant, please change your query_column.");
        }
        if(!queryColumns.contains(queryColumn)){
            throw new RuntimeException("Sorry, query_column is not exist or currently does not support multiple query_column, only support a single query_column.");
        }
        if(!queryColumns.contains(returnColumn)){
            throw new RuntimeException("Sorry, return_column is not exist or currently does not support multiple return_column, only support a single return_column.");
        }
        if(queryColumn.equals(returnColumn)){
            throw new RuntimeException("Sorry, your query_column and return_column they are the same, please check!");
        }

        //权限验证
        String context = jsonParser.getContext();
        if(context != null){
            //if是member
            if(permissionConf.getMemberId().contains(context)){
                if(!permissionConf.getMemberService().contains(returnColumn)){
                    throw new RuntimeException("Sorry, you do not have permission to obtain this data, contact the administrator to register as soon as possible to become a member of our bar!");
                }
            } else if(permissionConf.getAdminId().contains(context)){
                if(!permissionConf.getAdminService().contains(returnColumn)){
                    throw new RuntimeException("Sorry, return_column is not exist or currently does not support multiple return_column, only support a single return_column.");
                }
            } else if(permissionConf.getUserId().contains(context)){
                if(!permissionConf.getUserService().contains(returnColumn)){
                    throw new RuntimeException("Sorry, you do not have permission to obtain this data, contact the administrator to register as soon as possible to become a member of our bar!");
                }
            } else {
                throw new RuntimeException("Sorry, you do not have permission to obtain this data, contact the administrator to register as soon as possible to become a member of our bar!");
            }
        } else {
            //if context没有就不给任何权限
            throw new RuntimeException("Sorry, you do not have permission to obtain this data, contact the administrator to register as soon as possible to become a member of our bar!");
        }
    }
}
