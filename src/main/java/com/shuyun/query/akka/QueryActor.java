package com.shuyun.query.akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.ListenableFuture;
import com.shuyun.query.akka.http.DruidAsyncHandler;
import com.shuyun.query.akka.http.DruidBodyAsyncConsumer;
import com.shuyun.query.meta.EsQueryConf;
import com.shuyun.query.meta.MemberRfmConf;
import com.shuyun.query.meta.Message;
import com.shuyun.query.meta.ShuyunQueryConf;
import com.shuyun.query.parser.JsonParser;
import com.shuyun.query.parser.filter.*;
import com.shuyun.query.parser.search.*;
import com.shuyun.query.process.QueryContext;
import com.shuyun.query.process.QueryFactory;
import com.shuyun.query.util.LoadBalanceUtil;
import com.shuyun.query.util.ShuyunCryptUtil;
import com.shuyun.query.util.YeahmobiUtils;
import org.apache.log4j.Logger;
import com.ning.http.client.AsyncHandler;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.mortbay.util.UrlEncoded;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class QueryActor extends UntypedActor {

    @Override
    public void onReceive(Object message) {

        try {
            query(message, getSender(), getSelf());
        } catch (Exception e) {
            String msg = "Error occured: " + e.getClass().getName() + " " + e.getMessage();
            logger.error(msg, e);
            Message errorResult = new Message();
            errorResult.setFlag("fail");
            errorResult.setMsg(msg);
            getSender().tell(errorResult, getSelf());
        }
    }

    private void query(Object query, ActorRef sender, ActorRef receiver) throws Exception {
        String queryData = (String) query;
        JsonParser jsonParser = new ObjectMapper().readValue(queryData, new TypeReference<JsonParser>() {
        });
        String dataSource = jsonParser.getSettings().getData_source();
        //validate(dataSource, jsonParser.getSettings().getQuery_id());
        validate(dataSource, jsonParser);

        //需要加密的index
        if(EsQueryConf.getInstance().getNeedCryptIndex().contains(dataSource) && EsQueryConf.getDataSourceInstance(dataSource).getNeedCrypt() != null) {
            //需要加密的column
            if(jsonParser.getQuerys() != null){
                getEncryptDataQuerys(dataSource, jsonParser.getQuerys());
            }
            if(jsonParser.getFilters() != null) {
                getEncryptDataFilters(dataSource, jsonParser.getFilters());
            }
        }

        List<String> shop_ids = new ArrayList<>();
        QueryContext queryContext = QueryFactory.create(jsonParser, shop_ids);
        String requestStr = queryContext.getQuery().toString();

        String url = "";
        if(EsQueryConf.getInstance().getNeedMoveIndex().contains(dataSource)){
            //index_trade, index_order, index_customer_new, index_customer_account
            if("customer_account".equalsIgnoreCase(dataSource)){
                url = String.format("http://%s/%s/%s/_search", EsQueryConf.getInstance().getMoveElasticSearchUrl(),
                        EsQueryConf.getDataSourceInstance(dataSource).getIndex(), "customer");
            } else {
                url = String.format("http://%s/%s/%s/_search", EsQueryConf.getInstance().getMoveElasticSearchUrl(),
                        EsQueryConf.getDataSourceInstance(dataSource).getIndex(), dataSource);
            }
        } else if(MemberRfmConf.getInstance().getRfmType().equalsIgnoreCase(dataSource)){
            //index_rfm
            if(shop_ids.size() == 1){
                String indexRfm = "index_rfm_9";
                if(MemberRfmConf.getInstance().getRfm_index_1().contains(shop_ids.get(0))){
                    indexRfm = "index_rfm_1";
                } else if(MemberRfmConf.getInstance().getRfm_index_2().contains(shop_ids.get(0))) {
                    indexRfm = "index_rfm_2";
                } else if(MemberRfmConf.getInstance().getRfm_index_3().contains(shop_ids.get(0))){
                    indexRfm = "index_rfm_3";
                } else if(MemberRfmConf.getInstance().getRfm_index_4().contains(shop_ids.get(0))){
                    indexRfm = "index_rfm_4";
                } else if(MemberRfmConf.getInstance().getRfm_index_5().contains(shop_ids.get(0))){
                    indexRfm = "index_rfm_5";
                } else if(MemberRfmConf.getInstance().getRfm_index_6().contains(shop_ids.get(0))){
                    indexRfm = "index_rfm_6";
                } else if(MemberRfmConf.getInstance().getRfm_index_7().contains(shop_ids.get(0))){
                    indexRfm = "index_rfm_7";
                } else if(MemberRfmConf.getInstance().getRfm_index_8().contains(shop_ids.get(0))){
                    indexRfm = "index_rfm_8";
                } else if(MemberRfmConf.getInstance().getRfm_index_9().contains(shop_ids.get(0))){
                    indexRfm = "index_rfm_9";
                }
                url = String.format("http://%s/%s/%s/_search", MemberRfmConf.getInstance().getElasticSearchUrl(), indexRfm, dataSource);
            } else {
                url = String.format("http://%s/%s/%s/_search", MemberRfmConf.getInstance().getElasticSearchUrl(), MemberRfmConf.getInstance().getRfmIndex(), dataSource);
            }
        } else if(MemberRfmConf.getInstance().getMemberType().equalsIgnoreCase(dataSource)){
            //index_member
            if(shop_ids.size() == 1) {
                String indexMember = "index_member_8";
                if (MemberRfmConf.getInstance().getMember_index_1().contains(shop_ids.get(0))) {
                    indexMember = "index_member_1";
                } else if(MemberRfmConf.getInstance().getMember_index_2().contains(shop_ids.get(0))){
                    indexMember = "index_member_2";
                } else if(MemberRfmConf.getInstance().getMember_index_3().contains(shop_ids.get(0))){
                    indexMember = "index_member_3";
                } else if(MemberRfmConf.getInstance().getMember_index_4().contains(shop_ids.get(0))){
                    indexMember = "index_member_4";
                } else if(MemberRfmConf.getInstance().getMember_index_5().contains(shop_ids.get(0))){
                    indexMember = "index_member_5";
                } else if(MemberRfmConf.getInstance().getMember_index_6().contains(shop_ids.get(0))){
                    indexMember = "index_member_6";
                } else if(MemberRfmConf.getInstance().getMember_index_7().contains(shop_ids.get(0))){
                    indexMember = "index_member_7";
                } else if(MemberRfmConf.getInstance().getMember_index_8().contains(shop_ids.get(0))){
                    indexMember = "index_member_8";
                }
                url = String.format("http://%s/%s/%s/_search", MemberRfmConf.getInstance().getElasticSearchUrl(), indexMember, dataSource);
            } else {
                url = String.format("http://%s/%s/%s/_search", MemberRfmConf.getInstance().getElasticSearchUrl(), MemberRfmConf.getInstance().getMemberIndex(), dataSource);
            }

        }else {
            String routing = Joiner.on(",").join(shop_ids);
            if(routing.indexOf(",") > -1){
                routing = UrlEncoded.encodeString(routing.substring(1,routing.length()-1));
            }
            url = String.format("http://%s/%s/%s/_search%s", LoadBalanceUtil.getPrimary(YeahmobiUtils.getRandomString(10)),
                    EsQueryConf.getDataSourceInstance(dataSource).getIndex(), dataSource,
                    shop_ids.size() > 0 ? "?routing=" + routing : "");
        }

//        String url = "";
//        if(EsQueryConf.getInstance().getNeedMoveIndex().contains(dataSource)){
//            url = String.format("http://%s/%s/%s/_search", EsQueryConf.getInstance().getMoveElasticSearchUrl(),
//                     EsQueryConf.getDataSourceInstance(dataSource).getIndex(), dataSource);
//        } else {
//            url = String.format("http://%s/%s/%s/_search%s", LoadBalanceUtil.getPrimary(YeahmobiUtils.getRandomString(10)),
//                     EsQueryConf.getDataSourceInstance(dataSource).getIndex(), dataSource,
//                    shop_ids.size() > 0 ? "?routing=" + routing : "");
//        }

        if (shop_ids.size() == 0) {
            logger.warn("query json do not have shop_id");
        }

        logger.debug("request url is " + url);
        requestEs(sender, receiver, queryContext, url, requestStr);
    }

    @SuppressWarnings("resource")
    private void requestEs(ActorRef sender, ActorRef receiver, QueryContext queryContext, String url, String queryStr) throws Exception {
        AsyncHttpClient client = null;
        try {

            AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder();
            builder.setCompressionEnabled(true).setAllowPoolingConnection(true);
            builder.setRequestTimeoutInMs((int) TimeUnit.MINUTES.toMillis(1));
            builder.setIdleConnectionTimeoutInMs((int) TimeUnit.MINUTES.toMillis(1));

            client = new AsyncHttpClient(builder.build());

            requestDruidSync(sender, receiver, queryContext, queryStr, client, url);
        } finally {
            // 关闭资源
            if (null != client) {
                client.closeAsynchronously();
            }
        }
    }

    private void requestDruidSync(ActorRef sender, ActorRef receiver, QueryContext queryContext, String queryStr, AsyncHttpClient client, String url) throws Exception {

        // 会创建两种akka handler
        AsyncHandler<Boolean> akkaHandler = new DruidAsyncHandler(new DruidBodyAsyncConsumer(sender, receiver, queryContext));

        // 向druid集群发请求
        ListenableFuture<Boolean> future = client.preparePost(url).addHeader("content-type", "application/json")
                .setBody(queryStr.getBytes("UTF-8")).execute(akkaHandler);
        try {
            future.get();
        } catch (Exception e) {
            if (e instanceof java.util.concurrent.ExecutionException && null != e.getCause()) {
                //
                throw new RuntimeException(e.getCause().getMessage());
            } else {
                throw e;
            }
        }
    }

    /**
     * 校验请求
     *
     * @param dataSource
     * @param jsonParser
     */
    //private void validate(String dataSource, String report_id) {
    private void validate(String dataSource, JsonParser jsonParser) {

        if (Strings.isNullOrEmpty(jsonParser.getSettings().getQuery_id())) {
            throw new RuntimeException("report_id must not be blank");
        }

        if (Strings.isNullOrEmpty(dataSource)) {
            throw new RuntimeException("dataSource must not be blank");
        }

        if (!EsQueryConf.getInstance().getDataSources().keySet().contains(dataSource)) {
            throw new RuntimeException("this dataSource do not contain");
        }

        if (dataSource.equalsIgnoreCase(EsQueryConf.getInstance().getTradeFeature())) {
            List<String> tradeFeatures = jsonParser.getFields();
            if (tradeFeatures.size() > 0) {
                for (String trade : tradeFeatures) {
                    if (!EsQueryConf.getDataSourceInstance(dataSource).getStringDimensions().contains(trade)) {
                        throw new RuntimeException("Sorry, the " + trade + "is not exist.");
                    }
                }
            } else {
                throw new RuntimeException("return_trade_features must not be blank");
            }
        }
    }

    private static void getEncryptDataQuerys(String dataSource, DimSearch filter) {
        switch(filter.getClass().getSimpleName()){
            case "StrMatch":{
                StrMatch strSelector = (StrMatch)filter;
                if (EsQueryConf.getDataSourceInstance(dataSource).getNeedCrypt().contains(strSelector.getDimension())) {
                    //String encryptData = ShuyunCryptUtil.encrypt(strSelector.getDimension(), strSelector.getValue());
                    if(!ShuyunCryptUtil.isEncrypt(strSelector.getDimension(), strSelector.getValue())) {
                        strSelector.setValue(ShuyunCryptUtil.encrypt(strSelector.getDimension(), strSelector.getValue()));
                    } else {
                        throw new RuntimeException("query can not encrypt!");
                    }
                }
            }
            break;
            case "StrSearch":{
                StrSearch strSearch = (StrSearch)filter;
                if (EsQueryConf.getDataSourceInstance(dataSource).getNeedCrypt().contains(strSearch.getDimension())) {
                    if(!ShuyunCryptUtil.isEncrypt(strSearch.getDimension(), strSearch.getValue())) {
                        strSearch.setValue(ShuyunCryptUtil.encrypt(strSearch.getDimension(), strSearch.getValue()));
                    } else {
                        throw new RuntimeException("query can not encrypt!");
                    }
                }
            }
            break;
            case "AndDimSearch":{
                AndDimSearch andFilter = (AndDimSearch)filter;
                for (DimSearch filter1 : andFilter.getFields()) {
                    getEncryptDataQuerys(dataSource, filter1);
                }
            }
            break;
            case "OrDimSearch":{
                OrDimSearch orFilter = (OrDimSearch)filter;
                for (DimSearch filter1 : orFilter.getFields()) {
                    getEncryptDataQuerys(dataSource, filter1);
                }
            }
            break;
            default:{

            }
        }
    }

    private static void getEncryptDataFilters(String dataSource, DimFilter filter) {
        switch (filter.getClass().getSimpleName()) {
            case "StrMatchForFilter":{
                StrMatchForFilter strSelector = (StrMatchForFilter)filter;
                if (EsQueryConf.getDataSourceInstance(dataSource).getNeedCrypt().contains(strSelector.getDimension())) {
                    //String encryptData = ShuyunCryptUtil.encrypt(strSelector.getDimension(), strSelector.getValue());
                    if(!ShuyunCryptUtil.isEncrypt(strSelector.getDimension(), strSelector.getValue())) {
                        strSelector.setValue(ShuyunCryptUtil.encrypt(strSelector.getDimension(), strSelector.getValue()));
                    } else {
                        throw new RuntimeException("query can not encrypt!");
                    }
                }
            }
            break;
            case "StrSelector": {
                StrSelector strSelector = (StrSelector) filter;
                if (EsQueryConf.getDataSourceInstance(dataSource).getNeedCrypt().contains(strSelector.getDimension())) {
                    if(!ShuyunCryptUtil.isEncrypt(strSelector.getDimension(), strSelector.getValue())) {
                        //String encryptData = ShuyunCryptUtil.encrypt(strSelector.getDimension(), strSelector.getValue());
                        strSelector.setValue(ShuyunCryptUtil.encrypt(strSelector.getDimension(), strSelector.getValue()));
                    } else {
                        throw new RuntimeException("query can not encrypt!");
                    }
                }
            }
            break;
            case "StrNotSelector": {
                StrNotSelector strSelector = (StrNotSelector) filter;
                if (EsQueryConf.getDataSourceInstance(dataSource).getNeedCrypt().contains(strSelector.getDimension())) {
                    if(!ShuyunCryptUtil.isEncrypt(strSelector.getDimension(), strSelector.getValue())) {
                        strSelector.setValue(ShuyunCryptUtil.encrypt(strSelector.getDimension(), strSelector.getValue()));
                    } else {
                        throw new RuntimeException("query can not encrypt!");
                    }
                }
            }
            break;
            case "StrIn": {
                StrIn strIn = (StrIn) filter;
                if (EsQueryConf.getDataSourceInstance(dataSource).getNeedCrypt().contains(strIn.getDimension())) {
                    List<String> listValue = new ArrayList<String>();
                    for (String value : strIn.getValue()) {
                        listValue.add(ShuyunCryptUtil.encrypt(strIn.getDimension(), value));
                    }
                    strIn.setValue(listValue);
                }
            }
            break;
            case "StrNotIn": {
                StrNotIn strNotIn = (StrNotIn) filter;
                if (EsQueryConf.getDataSourceInstance(dataSource).getNeedCrypt().contains(strNotIn.getDimension())) {
                    List<String> listValue = new ArrayList<String>();
                    for (String value : strNotIn.getValue()) {
                        listValue.add(ShuyunCryptUtil.encrypt(strNotIn.getDimension(), value));
                    }
                    strNotIn.setValue(listValue);
                }
            }
            break;
            case "AndDimFilter": {
                AndDimFilter andFilter = (AndDimFilter) filter;
                for (DimFilter filter1 : andFilter.getFields()) {
                    getEncryptDataFilters(dataSource, filter1);
                }
            }
            break;
            case "OrDimFilter": {
                OrDimFilter orFilter = (OrDimFilter) filter;
                for (DimFilter filter1 : orFilter.getFields()) {
                    getEncryptDataFilters(dataSource, filter1);
                }
            }
            break;
            default: {

            }
        }
    }

    public static Props mkProps() {
        return Props.apply(QueryActor.class);
    }

    private static Logger logger = Logger.getLogger(QueryActor.class);

}