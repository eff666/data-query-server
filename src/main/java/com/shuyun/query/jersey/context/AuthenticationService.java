package com.shuyun.query.jersey.context;

import akka.actor.ActorSystem;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.ListenableFuture;
import com.shuyun.query.meta.Message;
import com.shuyun.query.meta.PermissionConf;
import com.shuyun.query.meta.ShuyunQueryConf;
import com.shuyun.query.parser.AuthenticationParser;
import com.shuyun.query.parser.FiledDataParser;
import com.shuyun.query.meta.ValidateResult;
import org.apache.log4j.Logger;
import org.elasticsearch.client.Client;
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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by xxx on 2016/7/21.
 * null 201
 * fail 202
 */

@Path("/validate")
public class AuthenticationService {
    private static Logger logger = Logger.getLogger(AuthenticationService.class);
    Message errorResult = new Message();

    private static Client client = null;

    @Context
    ActorSystem actorSystem;

    @GET
    @ManagedAsync
    @Produces(MediaType.APPLICATION_JSON)
    public void doGet(@QueryParam("validate") final String validateDate, @Suspended final AsyncResponse res){
//        try {
//            doPost(validateDate, res);
//        } catch (Exception e){
//            String msg = "Error occured: " + e.getClass().getName() + " " + e.getMessage();
//            errorResult.setFlag("fail");
//            errorResult.setMsg(msg);
//            logger.error(msg, e);
//            res.resume(Response.status(202).entity(errorResult).build());
//            return;
//        }
        doPost(validateDate, res);
    }

    @POST
    @ManagedAsync
    @Produces(MediaType.APPLICATION_JSON)
    public void doPost(@QueryParam("validate") final String validateDate, @Suspended final AsyncResponse res){
        try {
            if (Strings.isNullOrEmpty(validateDate)) {
                errorResult.setFlag("fail");
                errorResult.setMsg("validate can not be blank.");
                res.resume(Response.status(202).entity(errorResult).build());
                return;
            }
            logger.info("query begin! query is: [" + validateDate + "]");

            AuthenticationParser authenticationParser = null;
            try {
                authenticationParser = new ObjectMapper().readValue(validateDate, new TypeReference<AuthenticationParser>() {
                });
            } catch (IOException e) {
                logger.error("parser fail.");
                errorResult.setFlag("fail");
                errorResult.setMsg("Sorry, there may be problems with your validate data format, please check!");
                res.resume(Response.status(202).entity(errorResult).build());
                return;
            }

            Map<String, String> filedDataMap = new LinkedHashMap<String, String>();
            validate(authenticationParser, filedDataMap);
            settingForEs(authenticationParser, res, filedDataMap);
        } catch (Exception e){
            String msg = "Error occured: " + e.getClass().getName() + " " + e.getMessage();
            errorResult.setFlag("fail");
            errorResult.setMsg(msg);
            logger.error(msg, e);
            res.resume(Response.status(202).entity(errorResult).build());
            return;
        }
    }

    private void settingForEs(AuthenticationParser parser, final AsyncResponse res, Map<String, String> filedDataMap) throws Exception {
        try {
            AsyncHttpClient client = null;
            AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder();
            builder.setCompressionEnabled(true).setAllowPoolingConnection(true);
            builder.setRequestTimeoutInMs((int) TimeUnit.MINUTES.toMillis(1));
            builder.setIdleConnectionTimeoutInMs((int) TimeUnit.MINUTES.toMillis(1));
            client = new AsyncHttpClient(builder.build());

            QueryBuilder queryBuilder = QueryBuilders.boolQuery();
            Map<String, String> thirdMap = new LinkedHashMap<>();
            Iterator<Map.Entry<String, String>> it = filedDataMap.entrySet().iterator();
            int size = 0;
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                if(size < 2) {
                    ((BoolQueryBuilder) queryBuilder).must(QueryBuilders.matchPhraseQuery(entry.getKey(), entry.getValue()));
                    size++;
                } else {
                    thirdMap.put(entry.getKey(), entry.getValue());
                }
            }

            SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource();
            sourceBuilder.query(queryBuilder);
            sourceBuilder.from(0);
            sourceBuilder.size(1);
            String queryStr = sourceBuilder.toString();

            String url = String.format("http://%s/%s/%s/_search", ShuyunQueryConf.getInstance().getElasticSearchUrl(),
                    ShuyunQueryConf.getInstance().getIndex(), ShuyunQueryConf.getInstance().getType());

            logger.debug("query is: [ " + queryStr + " ]");
            logger.debug("url is: [ " + url + " ]");
            requestForEs(client, parser, res, url, queryStr,thirdMap);

        } finally {
            if(client != null){
                client.close();
            }
        }
    }

    private void requestForEs(AsyncHttpClient client, AuthenticationParser parser, final AsyncResponse res, String url, String queryStr, Map<String, String> thirdMap) throws  Exception{
        try{
            ListenableFuture<com.ning.http.client.Response> future = client.preparePost(url).addHeader("content-type", "application/json")
                    .setBody(queryStr.getBytes("UTF-8")).execute();

            ValidateResult validateResult = new ValidateResult();
            if (future.get().getStatusCode() != 200) {
                logger.error("some error may occur from es!");
                throw new RuntimeException("some error may occur from es!");
            } else {
                JsonNode jsonNode = new ObjectMapper().readTree(future.get().getResponseBody());
                List<JsonNode> listSource = jsonNode.findValues("_source");

                if(parser.getFileds().size() == 2){
                    if(listSource.size() > 0) {
                        //账号手机号一致
                        validateResult.setFlag("success");
                        validateResult.setResult("0");
                        res.resume(Response.status(200).entity(validateResult).build());
                        return;
                    }else {
                        //账号手机号不一致
                        validateResult.setFlag("success");
                        validateResult.setResult("1");
                        res.resume(Response.status(200).entity(validateResult).build());
                        return;
                    }
                } else if(parser.getFileds().size() ==3){
                    if(listSource.size() > 0) {
                        String thirdKey = "";
                        String thirdValue = "";
                        for(Map.Entry<String, String> en : thirdMap.entrySet()){
                            thirdKey = en.getKey();
                            thirdValue = en.getValue();
                        }
                        //判断账号和第三个条件是否一致，如姓名
                        String validateValue = listSource.get(0).get(thirdKey).textValue();
                        if(!Strings.isNullOrEmpty(validateValue) && validateValue.equalsIgnoreCase(thirdValue)) {
                            //账号手机号和姓名一致
                            validateResult.setFlag("success");
                            validateResult.setResult("0");
                            res.resume(Response.status(200).entity(validateResult).build());
                            return;
                        }else {
                            //账号手机号和姓名不一致，判断账号和姓名是否一致
                            validateResult.setFlag("success");
                            validateResult.setResult("2");
                            res.resume(Response.status(200).entity(validateResult).build());
                        }
                    } else {
                        //账号手机号不一致
                        validateResult.setFlag("success");
                        validateResult.setResult("1");
                        res.resume(Response.status(200).entity(validateResult).build());
                        return;
                    }
                } else {
                    logger.error("Error occur at validate result");
                    throw  new RuntimeException("Error occur at validate result");
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private void validate(AuthenticationParser dateParser, Map<String, String> filedDataMap){
        List<FiledDataParser> dataFileds = dateParser.getFileds();
        String context = dateParser.getContext();

        PermissionConf permissionConf = PermissionConf.getInstance();
        List<String> validateName = permissionConf.getMemberService();
        int dataFiledSize = dataFileds.size();

        if(dataFiledSize > 0){
            if(dataFiledSize == 1){
                throw new RuntimeException("Sorry, please select at least two sets of data to validate.");
            }
            if(dataFiledSize > 3){
                throw new RuntimeException("Sorry, currently support most of the validation of the three sets of data.");
            }
            for(FiledDataParser fileds : dataFileds){
                String key = fileds.getValidate_name();
                String value = fileds.getValidate_value();
                if(Strings.isNullOrEmpty(key) || Strings.isNullOrEmpty(value)){
                    throw new RuntimeException("Sorry, validate_name or validate_value can not be blank!");
                }
                if(validateName.contains(key)){
                    if(!filedDataMap.keySet().contains(key)) {
                        filedDataMap.put(key, value);
                    } else {
                        throw new RuntimeException("Sorry, the validate_name is the same, please check.");
                    }
                } else {
                    throw new RuntimeException("Sorry, the " +  key + " is not exist, please check.");
                }
            }
        } else {
            throw new RuntimeException("fileds can not be blank!");
        }

        if(context != null){
            //if是member
            if(!permissionConf.getMemberId().contains(context)){
                throw new RuntimeException("Sorry, you do not have permission to obtain this data, contact the administrator to register as soon as possible to become a member of our bar!");
            }
        } else {
            //if context没有就不给任何权限
            throw new RuntimeException("Sorry, you do not have permission to obtain this data, contact the administrator to register as soon as possible to become a member of our bar!");
        }
    }
}
