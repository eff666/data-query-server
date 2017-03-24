package com.shuyun.query.jersey.context;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.ListenableFuture;
import com.shuyun.query.meta.*;
import com.shuyun.query.parser.AddressParser;
import org.apache.log4j.Logger;
import com.google.common.base.Stopwatch;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.glassfish.jersey.server.ManagedAsync;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by xxx on 2016/8/3.
 */
@Path("/address")
public class AddressService {
    private static Logger logger = Logger.getLogger(AddressService.class);
    private static final double EARTH_RADIUS = 6378.137;
//    @Context
//    ActorSystem actorSystem;

    @GET
    @ManagedAsync
    @Produces(MediaType.APPLICATION_JSON)
    public void doGet(@QueryParam("address") final String addressParam, @Suspended final AsyncResponse res){
        doPost(addressParam, res);
    }

    @POST
    @ManagedAsync
    @Produces(MediaType.APPLICATION_JSON)
    public void doPost(@FormParam("address") final String addressParam, @Suspended final AsyncResponse res){
        try {
            if (Strings.isNullOrEmpty(addressParam)) {
                res.resume(Response.status(201).entity("address can not blank").build());
            }
            AddressParser addressParser = null;
            try {
                addressParser = new ObjectMapper().readValue(addressParam, new TypeReference<AddressParser>() {
                });
            } catch (IOException e) {
                throw new IOException("Sorry, maybe the param is not match, please check.");
            }

            addressValidate(addressParser);

            final Stopwatch stopWatch = new Stopwatch().start();
            logger.info("address validate begin, validate is:[" + addressParam + "]");
            sendRequestEs(addressParser, res);
            logger.info(("address validate end, validate cost: " + stopWatch.elapsedTime(TimeUnit.MILLISECONDS)));

        } catch(Exception e) {
//            String msg = "Error occured: " + e.getClass().getName() + " " + e.getMessage();
            Message errorResult = new Message();
            String msg = "Error occured: " + e.getMessage();
            errorResult.setFlag("fail");
            errorResult.setMsg(msg);
            logger.error(msg, e);
            res.resume(Response.status(201).entity(errorResult).build());
        }
    }

    private void sendRequestEs(AddressParser addressParser, AsyncResponse res) throws Exception{
        AsyncHttpClient client = null;
        try {
            AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder();
            builder.setCompressionEnabled(true).setAllowPoolingConnection(true);
            builder.setRequestTimeoutInMs((int) TimeUnit.MINUTES.toMillis(1));
            builder.setIdleConnectionTimeoutInMs((int) TimeUnit.MINUTES.toMillis(1));
            client = new AsyncHttpClient(builder.build());

            String url = String.format("http://%s/%s/%s/_search", ShuyunQueryConf.getInstance().getElasticSearchUrl(),
                    ShuyunQueryConf.getInstance().getAddressIndex(), ShuyunQueryConf.getInstance().getAddressType());

            QueryBuilder queryBuilder = QueryBuilders.boolQuery();
            ((BoolQueryBuilder)queryBuilder).must(QueryBuilders.matchPhraseQuery("buyer_nick", addressParser.getBuyer_nick()));
            SearchSourceBuilder searchSource = SearchSourceBuilder.searchSource();
            searchSource.query(queryBuilder);
            searchSource.from(0);
            searchSource.size(1);
            String queryStr = searchSource.toString();
            logger.debug("query is: [ " + queryStr + " ]");
            logger.debug("url is: [ " + url + " ]");

            validateByEs(client, url, queryStr, addressParser, res);

        } finally {
            if (null != client) {
                client.close();
            }
        }
    }

    private void validateByEs(AsyncHttpClient client,String url, String queryStr, AddressParser addressParser, AsyncResponse res) throws Exception{
        try {
            ListenableFuture<com.ning.http.client.Response> future = client.preparePost(url).addHeader("content-type", "application/json")
                    .setBody(queryStr.getBytes("UTF-8")).execute();

            if(future.get().getStatusCode() != 200){
                logger.error("some error may occur from address validate es! query is [" + queryStr + "]");
                throw new RuntimeException("some error may occur from address validate es!");
            } else {
                JsonNode jsonNode = new com.fasterxml.jackson.databind.ObjectMapper().readTree(future.get().getResponseBody());
                if(jsonNode.get("hits").get("hits").size() > 0) {
                    JsonNode listSource = jsonNode.get("hits").get("hits").get(0).get("_source");
                    Map<String, String> addressMap = new LinkedHashMap<String, String>();
                    String result = "";
                    List<String> list1 = new ArrayList<String>();
                    for(String address : addressParser.getAddress_type()){
                        try{
                            String validateAddress = listSource.get(address).textValue();
                            List<String>  listLocation = new ArrayList<String>();
                            if(!Strings.isNullOrEmpty(validateAddress)) {
                                double distance = -1;
                                for (String addr : validateAddress.split("#")) {
                                    String location = getGeoLocation(client, addr);
                                    listLocation.add(location);
                                }

                                if("reciev_validate_h".equalsIgnoreCase(address)){
                                    List<String>  listDistance = new ArrayList<String>();
                                    for(String list : listLocation){
                                        distance = getDistances(addressParser.getAddress_data(), list);
                                        listDistance.add(String.valueOf(distance));
                                    }
                                    result = Joiner.on(",").join(listDistance);
                                } else {
                                    distance = getDistances(addressParser.getAddress_data(), listLocation.get(0));
                                    result = getReturnData(distance);
                                }
                            } else {
                                result = "-1";
                            }

                            String columnName = address + "_location";
                            StringBuffer columnValue = new StringBuffer();
                            for(int i = 0; i < listLocation.size(); i++){
                                columnValue.append(listLocation.get(i));
                                if(i < listLocation.size() - 1){
                                    columnValue.append("#");
                                }
                            }
                            String columnLocation = "\"" + columnName + "\":\"" + columnValue + "\"";
                            list1.add(columnLocation);

                        } catch (NullPointerException nu){
                            result = "-1";
                        }
                        addressMap.put(address, result);
                    }

                    //将得到的经纬度信息和用户数据组合写入到文件中
                    String jsons = "{" +
                    "\"buyer_nick\":" + listSource.get("buyer_nick") +
                    ",\"reciev_validate_h\":" + listSource.get("reciev_validate_h") +
                    ",\"reciev_validate_l\":" + listSource.get("reciev_validate_l") +
                    ",\"reciev_validate_c\":" + listSource.get("reciev_validate_c") +
                    "," + Joiner.on(",").join(list1) +
                    '}';
                    writeFile(jsons);

                    AddressValidateResult validateResult = new AddressValidateResult();
                    validateResult.setFlag("success");
                    validateResult.setMessage("success");
                    validateResult.setResult(addressMap);
                    res.resume(Response.status(200).entity(validateResult).build());
                } else {
                    //logger.error("the " + addressParser.getBuyer_nick() + " is not exist from es.");
                    throw new RuntimeException("the " + addressParser.getBuyer_nick() + " is not exist from es.");
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new IOException(e.getMessage());
        }
    }

    //get location from geo
    private String getGeoLocation(AsyncHttpClient client, String address) throws Exception{
        if(address.indexOf("&") > -1){
            address = address.replaceAll("&", "");
        }
        String location = "";
        String geoUrl = String.format("http://restapi.amap.com/v3/geocode/geo?address=%s&output=%s&key=%s", address, "JSON", PermissionConf.getInstance().getAddressGeoKey());
        ListenableFuture<com.ning.http.client.Response> getFuture = client.prepareGet(geoUrl).execute();
        JsonNode geoJsonNode = new com.fasterxml.jackson.databind.ObjectMapper().readTree(getFuture.get().getResponseBody());
        if(geoJsonNode.findValue("infocode").textValue().equals("10000")) {//正确
            location = geoJsonNode.findValue("location").textValue();
        } else if(geoJsonNode.findValue("infocode").textValue().equals("10001")){//key 过期
            throw new RuntimeException("Sorry, the geo key already expired, please contact the administrator.");
        } else if(geoJsonNode.findValue("infocode").textValue().equals("10003")){//超过单日访问量限制
            throw new RuntimeException("Sorry, you have exceeded the maximum number of visits today, please come back tomorrow.");
        }  else if(geoJsonNode.findValue("infocode").textValue().equals("10004")){//超过一分钟访问限制
            throw new RuntimeException("Sorry, your access too often, please try again in a minute.");
        }
        return location;
    }

    private void addressValidate(AddressParser addressParser){
        if(Strings.isNullOrEmpty(addressParser.getBuyer_nick())){
            throw new RuntimeException("buyer_nick can not be blank!");
        }
        if(Strings.isNullOrEmpty(addressParser.getAddress_data())){
            throw new RuntimeException("address_data can not be blank!");
        }
        if(addressParser.getAddress_type().size() == 0){
            throw new RuntimeException("address_type can not be blank!");
        }

        //权限验证
        if(Strings.isNullOrEmpty(addressParser.getContext()) || !PermissionConf.getInstance().getAddressContext().contains(addressParser.getContext())){
            throw new RuntimeException("Sorry, you do not have permission to obtain this data, contact the administrator to register as soon as possible to become a member of our bar!");
        }
        //address_type验证
        for(String type : addressParser.getAddress_type()){
            if(!PermissionConf.getInstance().getAddressValidate().contains(type)){
                throw new RuntimeException("Sorry, address_type is not exist.");
            }
        }
        //address_data格式验证，符合"113.753602,34.765515"
        if(addressParser.getAddress_data().indexOf(",") > -1){
            String[] datas = addressParser.getAddress_data().split(",");
            if(datas.length == 2){
                for(String data : datas){
                    try {//不能[fff,bbb],判断是否是数字
                        Double.valueOf(data);
                    } catch (Exception e){
                        throw new RuntimeException("address_type format is not correct, please check. eg:[121.445140,31.177779]");
                    }
                }
            } else {//必须是2组
                throw new RuntimeException("address_type format is not correct, please check. eg:[121.445140,31.177779]");
            }
        } else {//必须有逗号
            throw new RuntimeException("address_type format is not correct, please check. eg:[121.445140,31.177779]");
        }
    }

    private  double rad(double d){
        return d * Math.PI / 180.0;
    }

    private double getDistances(String location1, String location2) throws Exception{
        List<String> list1 = getSplitLocation(location1);
        List<String> list2 = getSplitLocation(location2);

        double lat1 = rad(Double.valueOf(list1.get(1)));
        double lat2 = rad(Double.valueOf(list2.get(1)));
        double a = lat1 - lat2;
        double b = rad(Double.valueOf(list1.get(0)) - Double.valueOf(list2.get(0)));

        double sa2 = Math.sin(a / 2.0);
        double sb2 = Math.sin(b / 2.0);
        return 2 * EARTH_RADIUS
                * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)
                * Math.cos(lat2) * sb2 * sb2));
     }

    private List<String> getSplitLocation(String location){
        List<String> list = new ArrayList<String>();
        if(Strings.isNullOrEmpty(location)) {
            logger.error("get location error from geo.");
            throw new RuntimeException("get location error from geo.");
        } else {
            for (String loca : location.split(",")) {
                list.add(loca);
            }
        }
        return list;
    }

    //单位：km
    private String getReturnData(double distance){
        String returnData;
        if(distance >= 0 && distance <= 1){
            returnData = "1";
        } else if(distance > 1 && distance <= 3){
            returnData = "2";
        } else if(distance > 3 && distance <= 10){
            returnData = "3";
        } else if(distance > 10 && distance <= 20){
            returnData = "4";
        } else if(distance > 20){
            returnData = "5";
        } else {
            returnData = "-1";
        }
        return returnData;
    }

    private void writeFile(String context){
        File file = new File("/var/log/shuyunsearch-api/1.0.0/index_address_location.txt");
        FileOutputStream fileOutputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            fileOutputStream = new FileOutputStream(file, true);
            outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF-8");
            bufferedWriter = new BufferedWriter(outputStreamWriter);

            bufferedWriter.write(context);
            bufferedWriter.newLine();
        } catch (FileNotFoundException e) {
            logger.error("index_address_location.txt file not exist.");
            e.printStackTrace();
        } catch (IOException e) {
            logger.error("write index_address_location.txt data occur error.");
            e.printStackTrace();
        } finally {
            try {
                bufferedWriter.close();
                outputStreamWriter.close();
                fileOutputStream.close();
            } catch (IOException e) {
                logger.error("close file occur error.");
                e.printStackTrace();
            }
        }
    }
}
