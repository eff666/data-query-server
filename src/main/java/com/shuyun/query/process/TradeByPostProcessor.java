package com.shuyun.query.process;

import com.shuyun.query.meta.EsQueryConf;
import com.shuyun.query.meta.ReportPage;
import com.shuyun.query.meta.ReportResultForEs;
import com.shuyun.query.parser.JsonParser;
import com.shuyun.query.result.EsResultSetForSource;
import org.elasticsearch.common.Strings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TradeByPostProcessor extends PostProcessor  {

    public TradeByPostProcessor(QueryContext queryContext){
        super(queryContext);
    }

    @Override
    public ReportResultForEs process(List<?> input) {
        ReportResultForEs reportResult = new ReportResultForEs();

        reportResult.setFlag("success");
        reportResult.setMsg("success");

        List<EsResultSetForSource> rows = (List<EsResultSetForSource>) input;
        reportResult.setPage(new ReportPage(0, 0));

        JsonParser jsonParser = queryContext.getJsonParser();
        List<String> returnTradeFeatures = jsonParser.getFields();

        Map<Object, Object> tradeMap = new LinkedHashMap<Object, Object>();
        if(rows.size() > 0){
            for(Object trade : returnTradeFeatures){
                String returnResult = "-1";
                try {
                    if (rows.get(0).get_source().get(trade) != null) {
                        String result = rows.get(0).get_source().get(trade).toString();
                        switch (trade.toString()){
                            case "sum_buyer_obtain_point_fee_y1" : {
                                Double sumBuyerObtainPointFeeY = Double.valueOf(result);
                                if(sumBuyerObtainPointFeeY >= 0 && sumBuyerObtainPointFeeY < 150){
                                    returnResult = "0";
                                } else if(sumBuyerObtainPointFeeY >= 150 && sumBuyerObtainPointFeeY < 300){
                                    returnResult = "1";
                                }else if(sumBuyerObtainPointFeeY >= 300 && sumBuyerObtainPointFeeY < 500){
                                    returnResult = "2";
                                }else if(sumBuyerObtainPointFeeY >= 500 && sumBuyerObtainPointFeeY < 800){
                                    returnResult = "3";
                                }else if(sumBuyerObtainPointFeeY >= 800){
                                    returnResult = "4";
                                } else {
                                    returnResult = "-1";//结果异常
                                }
                            }
                            break;
                            case "count_created_y1" : {
                                Integer countCreatedY = Integer.parseInt(result);
                                if(countCreatedY >= Integer.valueOf(1) && countCreatedY < Integer.valueOf(5)){
                                    returnResult = "0";
                                } else if(countCreatedY >= Integer.valueOf(5) && countCreatedY < Integer.valueOf(10)){
                                    returnResult = "1";
                                }else if(countCreatedY >= Integer.valueOf(10) && countCreatedY < Integer.valueOf(15)){
                                    returnResult = "2";
                                }else if(countCreatedY >= Integer.valueOf(15) && countCreatedY < Integer.valueOf(20)){
                                    returnResult = "3";
                                }else if(countCreatedY >= Integer.valueOf(20)){
                                    returnResult = "4";
                                } else {
                                    returnResult = "-1";//结果异常
                                }
                            }
                            break;
                            case "sum_payment_y1" : {
                                Double sumPaymentY = Double.parseDouble(result);
                                if(sumPaymentY >= 0 && sumPaymentY < 300){
                                    returnResult = "0";
                                } else if(sumPaymentY >= 300 && sumPaymentY < 600){
                                    returnResult = "1";
                                }else if(sumPaymentY >= 600 && sumPaymentY < 1000){
                                    returnResult = "2";
                                }else if(sumPaymentY >= 1000 && sumPaymentY < 2000){
                                    returnResult = "3";
                                }else if(sumPaymentY >= 2000){
                                    returnResult = "4";
                                } else {
                                    returnResult = "-1";//结果异常
                                }
                            }
                            break;
                            case "sum_buyer_obtain_point_fee_m6" : {
                                Double sumBuyerObtion = Double.valueOf(result);
                                if(sumBuyerObtion >= 0 && sumBuyerObtion < 50){
                                    returnResult = "0";
                                } else if(sumBuyerObtion >= 50 && sumBuyerObtion < 100){
                                    returnResult = "1";
                                }else if(sumBuyerObtion >= 100 && sumBuyerObtion < 250){
                                    returnResult = "2";
                                }else if(sumBuyerObtion >= 250 && sumBuyerObtion < 550){
                                    returnResult = "3";
                                }else if(sumBuyerObtion >= 550){
                                    returnResult = "4";
                                } else {
                                    returnResult = "-1";//结果异常
                                }
                            }
                            break;
                            case "count_created_m6" : {
                                Integer countCreatedM = Integer.parseInt(result);
                                if(countCreatedM >= Integer.valueOf(1) && countCreatedM < Integer.valueOf(3)){
                                    returnResult = "0";
                                } else if(countCreatedM >= Integer.valueOf(3) && countCreatedM < Integer.valueOf(5)){
                                    returnResult = "1";
                                }else if(countCreatedM >= Integer.valueOf(5) && countCreatedM < Integer.valueOf(7)){
                                    returnResult = "2";
                                }else if(countCreatedM >= Integer.valueOf(7) && countCreatedM < Integer.valueOf(10)){
                                    returnResult = "3";
                                }else if(countCreatedM >= Integer.valueOf(10)){
                                    returnResult = "4";
                                } else {
                                    returnResult = "-1";//结果异常
                                }
                            }
                            break;
                            case "sum_payment_m6" : {
                                Double sumPaymentM = Double.valueOf(result);
                                if(sumPaymentM >= 0 && sumPaymentM < 100){
                                    returnResult = "0";
                                } else if(sumPaymentM >= 100 && sumPaymentM < 300){
                                    returnResult = "1";
                                }else if(sumPaymentM >= 300 && sumPaymentM < 500){
                                    returnResult = "2";
                                }else if(sumPaymentM >= 500 && sumPaymentM < 700){
                                    returnResult = "3";
                                }else if(sumPaymentM >= 700){
                                    returnResult = "4";
                                } else {
                                    returnResult = "-1";//结果异常
                                }
                            }
                            break;
                            case "sum_buyer_obtain_point_fee_m3" : {
                                Double sumBuyerObtainPointFeeM = Double.valueOf(result);
                                if(sumBuyerObtainPointFeeM >= 0 && sumBuyerObtainPointFeeM < 50){
                                    returnResult = "0";
                                } else if(sumBuyerObtainPointFeeM >= 50 && sumBuyerObtainPointFeeM < 100){
                                    returnResult = "1";
                                }else if(sumBuyerObtainPointFeeM >= 100 && sumBuyerObtainPointFeeM < 200){
                                    returnResult = "2";
                                }else if(sumBuyerObtainPointFeeM >= 200 && sumBuyerObtainPointFeeM < 350){
                                    returnResult = "3";
                                }else if(sumBuyerObtainPointFeeM >= 350){
                                    returnResult = "4";
                                } else {
                                    returnResult = "-1";//结果异常
                                }
                            }
                            break;
                            case "count_created_m3" : {
                                Integer countCreated = Integer.parseInt(result);
                                if(countCreated >= Integer.valueOf(1) && countCreated < Integer.valueOf(2)){
                                    returnResult = "0";
                                } else if(countCreated >= Integer.valueOf(2) && countCreated < Integer.valueOf(4)){
                                    returnResult = "1";
                                }else if(countCreated >= Integer.valueOf(4) && countCreated < Integer.valueOf(6)){
                                    returnResult = "2";
                                }else if(countCreated >= Integer.valueOf(6) && countCreated < Integer.valueOf(8)){
                                    returnResult = "3";
                                }else if(countCreated >= Integer.valueOf(8)){
                                    returnResult = "4";
                                } else {
                                    returnResult = "-1";//结果异常
                                }
                            }
                            break;
                            case "sum_payment_m3" : {
                                Double sumPaymentM = Double.valueOf(result);
                                if(sumPaymentM >= 0 && sumPaymentM < 100){
                                    returnResult = "0";
                                } else if(sumPaymentM >= 100 && sumPaymentM < 200){
                                    returnResult = "1";
                                }else if(sumPaymentM >= 200 && sumPaymentM < 350){
                                    returnResult = "2";
                                }else if(sumPaymentM >= 350 && sumPaymentM < 500){
                                    returnResult = "3";
                                }else if(sumPaymentM >= 500){
                                    returnResult = "4";
                                } else {
                                    returnResult = "-1";//结果异常
                                }
                            }
                            break;
                            case "last_trade_dt" : {
                                //yyy-MM-dd HH:mm:ss||yyyy-MM-dd
                                try {
                                    if(!Strings.isNullOrEmpty(result)){
                                        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");//设置解析格式
                                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");//设置输出格式
                                        Date date = sdf1.parse(result);
                                        returnResult = sdf2.format(date);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                            default: {
                                String errorMsg = String.format("Sorry, the %s type is not supported", trade);
                                throw new RuntimeException(errorMsg);
                            }
                        }
                    }
                }catch (NullPointerException nu){
                    //result is null from es
                    returnResult = "-1";
                }
                tradeMap.put(trade, returnResult);
            }
        } else {
            throw new RuntimeException("Sorry, the user is not exist from es.");
        }
        reportResult.append(tradeMap);
        return reportResult;
    }
}
