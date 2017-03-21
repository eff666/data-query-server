package com.shuyun.query.jersey.context;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.snakeyaml.nodes.Tag;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;
import com.shuyun.query.exceptions.TagServiceException;
import com.shuyun.query.meta.TagseviceConf;
import com.shuyun.query.parser.TagParser;
import com.shuyun.query.result.EsResultSetForSource;
import com.shuyun.query.util.HttpConnectionManager;
import com.shuyun.query.util.ShuyunCryptUtil;
import net.sf.json.JSONObject;
import org.apache.htrace.fasterxml.jackson.databind.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by wanghaiwei on 2016/5/10.
 */

@Path( "/tags")
public class TagService {
    public static final String QUERYURL = "http://%s/%s/%s/_search";
    //public static final String QUERY = "{\"from\":0,\"size\":1,\"query\":{\"filtered\":{\"filter\":{\"bool\":{\"must\":{\"query\":{\"match\":{\"%s\":{\"query\":\"%s\",\"type\":\"phrase\"}}}}}}}},\"_source\":{\"includes\":[\"%s\"],\"excludes\":[]}}";
    public static final String SHUYUNID_QUERY = "{\"from\": 0,\"size\": 1,\"query\":{\"bool\":{\"must\":[{\"match\":{\"%s\":{\"query\":\"%s\",\"type\":\"phrase\"}}}]}},\"_source\":{\"includes\":[\"buyer_nick\"]}}";
    public static final String TAG_QUERY = "{\"from\": 0,\"size\": 1,\"query\":{\"bool\":{\"must\":[{\"match\":{\"buyer_nick\":{\"query\":\"%s\",\"type\":\"phrase\"}}}]}},\"_source\":{\"excludes\":[\"buyer_nick\"]}}";
    public static final String SCORE_QUERY = "{\"from\": 0,\"size\": 1,\"query\":{\"bool\":{\"must\":[{\"match\":{\"buyer_nick\":{\"query\":\"%s\",\"type\":\"phrase\"}}},{\"match\":{\"dp_id\":{\"query\":\"%s\",\"type\":\"phrase\"}}}]}},\"_source\":{\"includes\":[\"score\"]}}";
    private static Logger logger = Logger.getLogger(TagService.class);

    private static final String SHUYUNID = "shuyun_id";
    private static final String TAG = "tag";
    private static final String SCORE = "score";

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response doGet (@QueryParam("param") String tagParam) throws JsonProcessingException {
        if(Strings.isNullOrEmpty(tagParam)){
            return Response.status(201).entity("{\"message\":\"param must not null.\"}").build();
        }

        TagParser tagParser = null;
        try {
            tagParser = new ObjectMapper().readValue(tagParam, new TypeReference<TagParser>() {});
        } catch (IOException e) {
            return Response.status(201).entity("{\"message\":\"Sorry, maybe the param is not match, please check.\"}").build();
        }

        String type = tagParser.getType();
        String value = tagParser.getValue();
        if(Strings.isNullOrEmpty(type) || Strings.isNullOrEmpty(value)){
            return Response.status(201).entity("{\"message\":\"type or value must not null.\"}").build();
        }

        if(!TagseviceConf.getInstance().getTypes().contains(type)){
            return Response.status(201).entity("{\"message\":\"this type do not exist. support types " + TagseviceConf.getInstance().getTypes() + "\"}.").build();
        }

        final Stopwatch stopwatch = new Stopwatch().start();
        logger.info("query begin [" +  tagParser.toString() + "]");

        Map<String, Object> immutableMap = null;
        ObjectMapper objectMapper = new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);

        try{
            //修改tag接口，从es查询
            if(TagseviceConf.getInstance().getPrimarykey().equalsIgnoreCase(type)){
                immutableMap = doEsRequest(type, value, TAG);
            } else {
                immutableMap = doEsRequest(type, value, SHUYUNID);
                String buyerNick = immutableMap.get(TagseviceConf.getInstance().getPrimarykey()).toString();
                if(Strings.isNullOrEmpty(buyerNick)) {
                    return Response.status(201).entity("{\"message\":\"buyer_nick is blank from es\"}").build();
                }
                immutableMap = doEsRequest(TagseviceConf.getInstance().getPrimarykey(), buyerNick, TAG);
            }
            if(immutableMap.size() == 0) return Response.status(201).entity("{\"message\": \"not find tags or value is null\"}").build();

            //add market score
            if(Strings.isNullOrEmpty(tagParser.getDp_id()) || Strings.isNullOrEmpty(tagParser.getBuyer_nick())){
                //return Response.status(201).entity("{\"message\":\"dp_id or buyer_nick must not null.\"}").build();
                logger.warn("dp_id or buyer_nick is blank.");
            } else {
                Map<String, Object> scoreMap = doEsRequest(tagParser.getBuyer_nick(), tagParser.getDp_id(), SCORE);
                if(scoreMap.size() > 0) {
                    if(scoreMap.get(SCORE) != null) {
                        immutableMap.put(SCORE, scoreMap.get(SCORE));
                    } else {
                        return Response.status(201).entity("{\"message\": \"not find score or value is null\"}").build();
                    }
                } else {
                    return Response.status(201).entity("{\"message\": \"according to the dp_id and buyer_nick is not match result\"}").build();
                }
            }
        } catch (Exception e){
            logger.error(e.getMessage(), e);
            return Response.status(201).entity("{\"message\":" + e.getMessage() + "}").build();
        }
        logger.info("query cost " + stopwatch.elapsedTime(TimeUnit.MILLISECONDS) + " query is " + "[" + tagParser.toString() + "]");

        return Response.status(200).encoding("utf-8").entity("{\"result\":" + objectMapper.writeValueAsString(immutableMap) + "}").build();
    }

//    private ImmutableMap converseResult(ImmutableMap map){
//        ImmutableMap.Builder immutableMap = ImmutableMap.builder();
//        for(Object key: map.keySet()){
//            immutableMap.put(TagName.getInstance().get(key), map.get(key));
//        }
//        return immutableMap.build();
//    }

    private Map<String, Object> doEsRequest(String type, String value, String flag){
        String query = "";
        String url = "";
        if("tag".equalsIgnoreCase(flag)) {
            //index_tag
            //对buyer_nick加密
            value = ShuyunCryptUtil.encrypt("buyer_nick", value);
            query = String.format(TAG_QUERY, value);
            url = String.format(QUERYURL, TagseviceConf.getInstance().getElasticSearchUrl().get(0), TagseviceConf.getInstance().getTagIndex(), TagseviceConf.getInstance().getTagType());
        } else if ("shuyun_id".equalsIgnoreCase(flag)){
            //shuyun_id
            query = String.format(SHUYUNID_QUERY, type, value);
            url = String.format(QUERYURL, TagseviceConf.getInstance().getElasticSearchUrl().get(0), TagseviceConf.getInstance().getIndex(), TagseviceConf.getInstance().getMapping());
        } else if("score".equalsIgnoreCase(flag)){
            //index_score
            //对buyer_nick加密
            type = ShuyunCryptUtil.encrypt("buyer_nick", type);
            query = String.format(SCORE_QUERY, type, value);
            url = String.format(QUERYURL, TagseviceConf.getInstance().getElasticSearchUrl().get(0), TagseviceConf.getInstance().getScoreIndex(), TagseviceConf.getInstance().getScoreType());
        }
        logger.debug("[tag] es query  is " + query);
        logger.debug("[tag] es url is " + url);
        HttpPost httppost = new HttpPost(url);
        HttpClient routerClient = null;
        Map<String, Object> mapSource = null;
        try {
            routerClient = HttpConnectionManager.getHttpClient();
            httppost.setHeader("content-type", "application/json");
            httppost.setEntity(new StringEntity(query, "utf-8"));
            HttpResponse response = routerClient.execute(httppost);
            if(response.getStatusLine().getStatusCode()/10 == 20){
                JsonNode jsonNode = new ObjectMapper().readTree(ByteStreams.toByteArray(response.getEntity().getContent()));
                if(jsonNode.get("hits").get("hits").size() > 0){
                    String result = jsonNode.get("hits").get("hits").get(0).toString();
                    EsResultSetForSource source = new org.apache.htrace.fasterxml.jackson.databind.ObjectMapper().readValue(result, new org.apache.htrace.fasterxml.jackson.core.type.TypeReference<EsResultSetForSource>() {
                    });
                    mapSource = source.get_source();
                } else {
                    if("score".equalsIgnoreCase(flag)){
                        mapSource = new HashMap();
                        mapSource.put(SCORE, "0.0");
                    }
                    else
                        throw new RuntimeException("the "+ type + " is " + value + " not exist from es");

                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            httppost.abort();
            throw new RuntimeException(e.getMessage());
        } finally {
            if(routerClient != null){
                routerClient = null;
            }
        }
        return mapSource;
    }
}
