package com.shuyun.query.jersey.context;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.shuyun.query.meta.EsQueryConf;
import com.shuyun.query.meta.Results;
import com.shuyun.query.parser.UpdateParser;
import com.shuyun.query.util.HttpConnectionManager;
import com.shuyun.query.util.LoadBalanceUtil;
import com.shuyun.query.util.TransportClientUtil;
import com.shuyun.query.util.YeahmobiUtils;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by wanghaiwei on 2015/8/24.
 */
@Path("/update")
public class UpdateService {

    private static Logger logger = Logger.getLogger(UpdateService.class);

//    public static final String UPDATEURL = "http://%s:%s/%s/%s/%s/_update";
    public static final String UPDATEURL = "http://%s/%s/%s/%s/_update";
    public static final String allUpdate = "{\"script\" : \"def aa = ctx._source.tagId;if(aa){if(lo1.toInteger() > 0){Set ff = new HashSet();for(it in aa){if(!it.startsWith(lo)){ff.add(it);};};for(it in tags){ff.add(it);};ctx._source.tagId = ff;};else{ctx._source.tagId = \\\"\\\";};};else{if(lo1.toInteger() > 0){def bb = new String[lo1.toInteger()];def cc = 0;for(it in tags){bb[cc] = it;cc += 1;};Set ff = bb as Set;ctx._source.tagId = ff;};else{ctx._source.tagId = \\\"\\\";};};\",\"params\" :";
    public static final String updatePlusJson = "def aa = ctx._source.tagId;def bb;if(aa){if(aa instanceof Integer || aa instanceof String){bb = new String[1+lo.toInteger()];};else{bb = new String[aa.size+lo.toInteger()];};};else{bb = new String[lo.toInteger()];};def cc = 0;for(it in aa){bb[cc] = it;cc += 1;};for(it in tags){bb[cc] = it;cc += 1;};Set ff = bb as Set;ctx._source.tagId = ff;";
    public static final String updateMinusJson = "def aa = ctx._source.tagId;if(aa){if(aa instanceof Integer || aa instanceof String){def bb = 0;for(it in tags){if(it == aa){bb = 1;break;};};if(bb == 0){ctx._source.tagId = aa;};else{ctx._source.tagId = \"\";};};else{Set ff = aa as Set;Set hh = tags as Set;ff.removeAll(hh);if(ff.size() > 0){ctx._source.tagId = ff;};else{ctx._source.tagId = \"\";};};};else{ctx._source.tagId = \"\";};";

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response doGet(@DefaultValue("false") @QueryParam("test") boolean test, @DefaultValue("plain") @QueryParam("style") String style,
                      @QueryParam("param") String param) {

        if (Strings.isNullOrEmpty(param)) {
            return Response.ok().entity(Results.NullParam).build();
        }

        final Stopwatch stopwatch = new Stopwatch().start();
        //final Stopwatch stopwatch = Stopwatch.createUnstarted().start();
        logger.info("update begin [" + param + "]");

        UpdateParser updateParser = null;
        // parser update json
        try {
            updateParser = new ObjectMapper().readValue(param, new TypeReference<UpdateParser>() {
            });
        } catch(Exception e){
            return Response.status(200).entity("parser fail.").build();
        }

        // validate
        if(!EsQueryConf.getInstance().getDataSources().keySet().contains(updateParser.getSettings().getData_source()))
            return Response.status(200).entity("do not has this table.").build();
        /*if(updateParser.getTagIds().size() == 0)
            return Response.status(200).entity("tagId must have value.").build();*/

        // get update json
        Map<String, Object> scriptParams = new HashMap<>();
        scriptParams.put("tags", updateParser.getTagIds());
        scriptParams.put("lo1", updateParser.getTagIds().size());
        scriptParams.put("lo", updateParser.getTagIds().size() > 0? updateParser.getTagIds().get(0).substring(0, updateParser.getTagIds().get(0).indexOf("##")) : null);
        String updateJson = allUpdate + JSONObject.fromObject(scriptParams).toString() + ",\"retry_on_conflict\":3}";
        logger.debug("update json is " + updateJson);

        // emit request to es
//        String url = String.format(UPDATEURL,
//                LoadBalanceUtil.getPrimary(YeahmobiUtils.getRandomString(10)),
//                EsQueryConf.getInstance().getPort(), EsQueryConf.getInstance().getDataSources().get(updateParser.getSettings().getData_source()).getIndex(),
//                updateParser.getSettings().getData_source(), updateParser.getNum_iids().get(0)) + "?routing="+updateParser.getShop_id();
        String url = String.format(UPDATEURL,
                LoadBalanceUtil.getPrimary(YeahmobiUtils.getRandomString(10)),
                EsQueryConf.getInstance().getDataSources().get(updateParser.getSettings().getData_source()).getIndex(),
                updateParser.getSettings().getData_source(), updateParser.getNum_iids().get(0)) + "?routing="+updateParser.getShop_id();
        logger.debug("update url is " + url);

        HttpPost httppost = new HttpPost(url);
        HttpClient routerClient = null;

        try {
            routerClient = HttpConnectionManager.getHttpClient();
            httppost.setHeader("content-type", "application/json");
            httppost.setEntity(new StringEntity(updateJson, "utf-8"));
            HttpResponse response = routerClient.execute(httppost);
            if(response.getStatusLine().getStatusCode()/10 == 20){
                logger.info("update cost " + stopwatch.elapsedTime(TimeUnit.MILLISECONDS) + " update is " + param);
                //logger.info("update cost " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " update is " + param);
                return Response.status(200).entity("ok").build();
            }
        } catch (Exception e) {
            httppost.abort();
            return Response.status(200).entity(e.getMessage()).build();
        } finally {
            if(routerClient != null){
                routerClient = null;
            }
        }
        logger.info("update cost " + stopwatch.elapsedTime(TimeUnit.MILLISECONDS) + " update is " + param);
        return Response.status(200).entity("request es fail").build();

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response doPost(@DefaultValue("false") @QueryParam("isplus") boolean isplus, @DefaultValue("plain") @QueryParam("style") final String style
                       , final String param) {

        if (Strings.isNullOrEmpty(param)) {
            return Response.ok().entity(Results.NullParam).build();
        }

        final Stopwatch stopwatch = new Stopwatch().start();
        //final Stopwatch stopwatch = Stopwatch.createUnstarted().start();
        logger.info("update begin [" + param + "]");

        UpdateParser updateParser = null;
        // parser update json
        try {
            updateParser = new ObjectMapper().readValue(param, new TypeReference<UpdateParser>() {
            });
        } catch(Exception e){
            return Response.status(200).entity("parser fail.").build();
        }

        if(!EsQueryConf.getInstance().getDataSources().keySet().contains(updateParser.getSettings().getData_source()))
            return Response.status(200).entity("do not has this table.").build();
        if(updateParser.getTagIds().size() == 0)
            return Response.status(200).entity("tagId must have value.").build();

        String updateJson;

        if(updateParser.getPlus().equalsIgnoreCase("true")) {
            updateJson = updatePlusJson;
        } else{
            updateJson = updateMinusJson;
        }
        Map<String, Object> scriptParams = new HashMap<>();
        scriptParams.put("tags", updateParser.getTagIds());
        scriptParams.put("lo", updateParser.getTagIds().size());
        logger.debug(scriptParams.toString());

        TransportClient client = null;
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
                        logger.error(failure.getMessage());
                    }
                })
                .setBulkActions(100)
                .setBulkSize(new ByteSizeValue(1, ByteSizeUnit.GB))
                .setFlushInterval(TimeValue.timeValueSeconds(5))
                .setConcurrentRequests(1)
                .build();

        String index = EsQueryConf.getInstance().getDataSources().get(updateParser.getSettings().getData_source()).getIndex();
        String table = updateParser.getSettings().getData_source();
        String routing = updateParser.getShop_id();
        logger.debug("num_iid number is [" + updateParser.getNum_iids().size() + "]");
        for(String num_iid : updateParser.getNum_iids()){
            bulkProcessor.add(new UpdateRequest(index, table, num_iid)
                    .routing(routing).script(updateJson).scriptParams(scriptParams).retryOnConflict(3));
        }

        bulkProcessor.flush();
        bulkProcessor.close();

        logger.info("update cost " + stopwatch.elapsedTime(TimeUnit.MILLISECONDS) + " update is " + param);

        return Response.status(200).entity("ok").build();
    }
}
