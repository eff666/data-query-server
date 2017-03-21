package com.shuyun.query.process;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuyun.query.meta.EsQueryConf;
import com.shuyun.query.parser.JsonParser;
import com.shuyun.query.parser.filter.*;
import com.shuyun.query.parser.search.*;
import com.shuyun.query.result.EsResultSet;
import com.shuyun.query.result.EsResultSetForSource;
import org.apache.log4j.Logger;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.bucket.missing.MissingBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryFactory {
	private static final String REGEXPSTR = "(.){%s}%s.*";
	private static Logger logger = Logger.getLogger(QueryFactory.class);

	/**
	 * elastic的请求类型目前共有3个：
	 * <p>
	 * groupby、search、filter
	 * <p>
	 * 目前实现了search、filter
	 * 目前的分类先根据结果集的解析按datasource还是fields分类
	 * @param parser
	 * @return
	 */
	public static QueryContext create(JsonParser parser,List shop_ids) {
		if (null == parser)
			return null;

		QueryContext queryContext = new QueryContext();
		queryContext.setJsonParser(parser);
		/*if(parser.getQuerys() != null){
			List fields = new ArrayList(parser.getFields());
			fields.retainAll(EsQueryConf.getDataSourceInstance(parser.getSettings().getData_source()).getNeedSource());
			boolean isSource =  fields.size() != 0;
			queryContext.setQuery(getEsRequest(parser, shop_ids, isSource));
			queryContext.setQueryType(isSource ? QueryType.SEARCH : QueryType.FILTER);
			queryContext.setTypeRef(isSource ? new TypeReference<EsResultSetForSource>() {} : new TypeReference<EsResultSet>() {});
		} else{}*/

		List fields = parser.getFields() == null? new ArrayList() : new ArrayList(parser.getFields());
		boolean isSource = true;
		if(fields.size() != 0){
			fields.retainAll(EsQueryConf.getDataSourceInstance(parser.getSettings().getData_source()).getNeedSource());
			isSource = fields.size() != 0;
		}

		queryContext.setQuery(getEsRequest(parser, shop_ids, isSource));
//		queryContext.setQueryType(isSource ? QueryType.SEARCH : QueryType.FILTER);
        if(parser.getSettings().getData_source().equalsIgnoreCase(EsQueryConf.getInstance().getTradeFeature())){
            queryContext.setQueryType(QueryType.TRADE);
        } else {
            queryContext.setQueryType(QueryType.SEARCH);
        }
//		queryContext.setTypeRef(isSource ? new TypeReference<EsResultSetForSource>() {
//		} : new TypeReference<EsResultSet>() {
//		});
		queryContext.setTypeRef(new TypeReference<EsResultSetForSource>() {});

		return queryContext;
	}


	private static SearchSourceBuilder getEsRequest(JsonParser jsonParser, List<String> shop_ids, boolean isSource){
		SearchSourceBuilder sb = SearchSourceBuilder.searchSource();

		if(jsonParser.getFields() != null && jsonParser.getFields().size()>0){
			if(!isSource){
//				sb.fields(jsonParser.getFields());
				sb.fetchSource(jsonParser.getFields().toArray(new String[jsonParser.getFields().size()]), new String[0]);
			} else{
				sb.fetchSource(jsonParser.getFields().toArray(new String[jsonParser.getFields().size()]), new String[0]);
			}
		}

//        //此处为查询null字段,针对Index_rfm_new,如果其他index也可以支持
//        if(jsonParser.getMissing() != null && jsonParser.getMissing().size() > 0){
//            //for(String str : missingFileds){
//                //sb.postFilter(QueryBuilders.missingQuery());
//            //}
//            QueryBuilder queryBuilder = QueryBuilders.boolQuery();
//            for(String filed : jsonParser.getMissing()){
//                ((BoolQueryBuilder)queryBuilder).must(QueryBuilders.existsQuery(filed));
//            }
//
//            sb.postFilter(QueryBuilders.notQuery(queryBuilder));
//        }

		// 此处为过滤，目前实现为filter，还有search需要适配
		sb.query(new FilteredQueryBuilder(getSearchDimentions(jsonParser.getQuerys(), shop_ids), getFilterDimentions(jsonParser.getFilters(), shop_ids, EsQueryConf.getDataSourceInstance(jsonParser.getSettings().getData_source()).getRouting())));
		if(jsonParser.getSettings().getData_source().equalsIgnoreCase(EsQueryConf.getInstance().getTradeFeature())){
            sb.from(0);
            sb.size(EsQueryConf.getDataSourceInstance(jsonParser.getSettings().getData_source()).getLimit());
        }else {
            sb.from(jsonParser.getSettings().getPagination().getOffset());
            sb.size(jsonParser.getSettings().getPagination().getLimit() > EsQueryConf.getDataSourceInstance(jsonParser.getSettings().getData_source()).getLimit() ? EsQueryConf.getDataSourceInstance(jsonParser.getSettings().getData_source()).getLimit() : jsonParser.getSettings().getPagination().getLimit());
        }

		if(jsonParser.getSort() != null && jsonParser.getSort().size()>0){
			HashMap<String, String> map = new HashMap<String, String>();
			for(Map<String, String> maps : jsonParser.getSort()){
				map.put(maps.get("orderBy"), maps.get("order"));
			}
			for(Map.Entry<String, String> map1 : map.entrySet()){
				if(SortOrder.valueOf(map1.getValue().toUpperCase()) == SortOrder.ASC){
					sb.sort(SortBuilders.fieldSort(map1.getKey()).order(SortOrder.ASC));
				} else{
					sb.sort(SortBuilders.fieldSort(map1.getKey()).order(SortOrder.DESC));
				}
			}
		}
		logger.debug("elasticsearch query is " + sb.toString());
		return sb;
	}

	private static QueryBuilder getSearchDimentions(DimSearch filter, List<String> shop_ids) {
		if(null == filter){
			return QueryBuilders.matchAllQuery();
		}

		switch(filter.getClass().getSimpleName()){
			case "StrMatch":{
				StrMatch strSelector = (StrMatch)filter;
				return QueryBuilders.matchPhraseQuery(strSelector.getDimension(), strSelector.getValue());
			}
			case "RegexpFilter":{
				RegexpFilter regexpFilter = (RegexpFilter)filter;
				return QueryBuilders.regexpQuery(regexpFilter.getDimension(), regexpFilter.getPattern());
			}
			case "StrPrifix":{
				StrPrifix strPrifix = (StrPrifix)filter;
				return QueryBuilders.prefixQuery(strPrifix.getDimension(), strPrifix.getValue());
			}
			case "StrSearch":{
				StrSearch strPrifix = (StrSearch)filter;
				return QueryBuilders.matchQuery(strPrifix.getDimension(), strPrifix.getValue());
			}
			case "AndDimSearch":{
				AndDimSearch andFilter = (AndDimSearch)filter;
				QueryBuilder queryBuilder = QueryBuilders.boolQuery();
				for(DimSearch filter1 : andFilter.getFields()){
					((BoolQueryBuilder)queryBuilder).must(getSearchDimentions(filter1, shop_ids));
				}
				return queryBuilder;
			}
			case "OrDimSearch":{
				OrDimSearch orFilter = (OrDimSearch)filter;
				QueryBuilder queryBuilder = QueryBuilders.boolQuery();
				for(DimSearch filter2 : orFilter.getFields()){
					((BoolQueryBuilder)queryBuilder).should(getSearchDimentions(filter2, shop_ids));
				}
				return queryBuilder;
			}
			default:{
				String errorMsg = String.format("search %s type %s is not supported", filter, filter.getClass());
				throw new RuntimeException(errorMsg);
			}
		}
	}

    private static QueryBuilder getFilterDimentions(DimFilter filter, List<String> shop_ids, String routing) {
        if(null == filter){
            return QueryBuilders.matchAllQuery();
        }

        switch(filter.getClass().getSimpleName()){
            case "StrMatchForFilter":{
                StrMatchForFilter strSelector = (StrMatchForFilter)filter;
                return QueryBuilders.matchPhraseQuery(strSelector.getDimension(), strSelector.getValue());
            }
            case "Equal":{
                Equal equal = (Equal)filter;
                if(!routing.equalsIgnoreCase(equal.getDimension()))
                    return QueryBuilders.termQuery(equal.getDimension(), equal.getValue());
                else {
                    shop_ids.add("" + equal.getValue());
                    return QueryBuilders.termQuery(equal.getDimension(), equal.getValue());
                }
            }
            case "NotEqual":{
                NotEqual notEqual = (NotEqual)filter;
                if(!routing.equalsIgnoreCase(notEqual.getDimension()))
                    return QueryBuilders.boolQuery().mustNot(QueryBuilders.termQuery(notEqual.getDimension(), notEqual.getValue()));
                else {
                    shop_ids.add("" + notEqual.getValue());
                    return QueryBuilders.boolQuery().mustNot(QueryBuilders.termQuery(notEqual.getDimension(), notEqual.getValue()));
                }
            }
            case "StrSelector":{
                StrSelector strSelector = (StrSelector)filter;
                if(!routing.equalsIgnoreCase(strSelector.getDimension()))
                    return QueryBuilders.termQuery(strSelector.getDimension(), strSelector.getValue());
                else {
                    shop_ids.add(strSelector.getValue());
                    return QueryBuilders.termQuery(strSelector.getDimension(), strSelector.getValue());
                }
            }
            case "StrNotSelector":{
                StrNotSelector strSelector = (StrNotSelector)filter;
                if(!routing.equalsIgnoreCase(strSelector.getDimension()))
                    return QueryBuilders.boolQuery().mustNot(QueryBuilders.termQuery(strSelector.getDimension(), strSelector.getValue()));
                else {
                    shop_ids.add("" + strSelector.getValue());
                    return QueryBuilders.boolQuery().mustNot(QueryBuilders.termQuery(strSelector.getDimension(), strSelector.getValue()));
                }
            }
            case "GreaterOrEqual":{
                GreaterOrEqual gEqual = (GreaterOrEqual)filter;
                if(!routing.equalsIgnoreCase(gEqual.getDimension()))
                    return QueryBuilders.rangeQuery(gEqual.getDimension()).gte(gEqual.getValue());
                else {
                    shop_ids.add("" + gEqual.getValue());
                    return QueryBuilders.rangeQuery(gEqual.getDimension()).gte(gEqual.getValue());
                }
            }
            case "GreaterThan":{
                GreaterThan gt = (GreaterThan)filter;
                if(!routing.equalsIgnoreCase(gt.getDimension()))
                    return QueryBuilders.rangeQuery(gt.getDimension()).gt(gt.getValue());
                else {
                    shop_ids.add("" + gt.getValue());
                    return QueryBuilders.rangeQuery(gt.getDimension()).gt(gt.getValue());
                }
            }
            case "TimeGreaterThan":{
                TimeGreaterThan gEqual = (TimeGreaterThan)filter;
                return QueryBuilders.rangeQuery(gEqual.getDimension()).gt(gEqual.getValue());
            }
            case "TimeGreaterOrEqual":{
                TimeGreaterOrEqual gEqual = (TimeGreaterOrEqual)filter;
                return QueryBuilders.rangeQuery(gEqual.getDimension()).gte(gEqual.getValue());
            }
            case "LittleOrEqual":{
                LittleOrEqual tEqual = (LittleOrEqual)filter;
                if(!routing.equalsIgnoreCase(tEqual.getDimension()))
                    return QueryBuilders.rangeQuery(tEqual.getDimension()).lte(tEqual.getValue());
                else {
                    shop_ids.add("" + tEqual.getValue());
                    return QueryBuilders.rangeQuery(tEqual.getDimension()).lte(tEqual.getValue());
                }
            }
            case "LittleThan":{
                LittleThan tthan = (LittleThan)filter;
                if(!routing.equalsIgnoreCase(tthan.getDimension()))
                    return QueryBuilders.rangeQuery(tthan.getDimension()).lt(tthan.getValue());
                else {
                    shop_ids.add("" + tthan.getValue());
                    return QueryBuilders.rangeQuery(tthan.getDimension()).lt(tthan.getValue());
                }
            }
            case "TimeLittleOrEqual":{
                TimeLittleOrEqual tEqual = (TimeLittleOrEqual)filter;
                return QueryBuilders.rangeQuery(tEqual.getDimension()).lte(tEqual.getValue());
            }
            case "TimeLittleThan":{
                TimeLittleThan tthan = (TimeLittleThan)filter;
                return QueryBuilders.rangeQuery(tthan.getDimension()).lt(tthan.getValue());
            }
            case "NumIn":{
                NumIn numIn = (NumIn)filter;
                if(!routing.equalsIgnoreCase(numIn.getDimension()))
                    return QueryBuilders.termsQuery(numIn.getDimension(), numIn.getValue());
                else {
                    shop_ids.add("" + numIn.getValue());
                    return QueryBuilders.termsQuery(numIn.getDimension(), numIn.getValue());
                }

            }
            case "NumNotIn":{
                NumNotIn numIn = (NumNotIn)filter;
                if(!routing.equalsIgnoreCase(numIn.getDimension()))
                    return QueryBuilders.boolQuery().mustNot(QueryBuilders.termsQuery(numIn.getDimension(), numIn.getValue()));
                else {
                    shop_ids.add("" + numIn.getValue());
                    return QueryBuilders.boolQuery().mustNot(QueryBuilders.termsQuery(numIn.getDimension(), numIn.getValue()));
                }
            }
            case "StrIn":{
                StrIn strIn = (StrIn)filter;
                if(!routing.equalsIgnoreCase(strIn.getDimension()))
                    return QueryBuilders.boolQuery().must(QueryBuilders.termsQuery(strIn.getDimension(), strIn.getValue()));
                else {
                    shop_ids.add("" + strIn.getValue());
                    return QueryBuilders.boolQuery().must(QueryBuilders.termsQuery(strIn.getDimension(), strIn.getValue()));
                }
            }
            case "StrNotIn":{
                StrNotIn strNotIn = (StrNotIn)filter;
                if(!routing.equalsIgnoreCase(strNotIn.getDimension()))
                    return QueryBuilders.boolQuery().mustNot(QueryBuilders.termsQuery(strNotIn.getDimension(), strNotIn.getValue()));
                else {
                    shop_ids.add("" + strNotIn.getValue());
                    return QueryBuilders.boolQuery().mustNot(QueryBuilders.termsQuery(strNotIn.getDimension(), strNotIn.getValue()));
                }
            }
            case "AndDimFilter":{
                AndDimFilter andFilter = (AndDimFilter)filter;
                QueryBuilder queryBuilder = QueryBuilders.boolQuery();
                for(DimFilter filter1 : andFilter.getFields()){
                    ((BoolQueryBuilder)queryBuilder).must(getFilterDimentions(filter1,shop_ids, routing));
                }
                return queryBuilder;
            }
            case "OrDimFilter":{
                OrDimFilter orFilter = (OrDimFilter)filter;
                QueryBuilder queryBuilder = QueryBuilders.boolQuery();
                for(DimFilter filter2 : orFilter.getFields()){
                    ((BoolQueryBuilder)queryBuilder).should(getFilterDimentions(filter2,shop_ids, routing));
                }
                return queryBuilder;
            }
            default:{
                String errorMsg = String.format("filter %s type %s is not supported", filter, filter.getClass());
                throw new RuntimeException(errorMsg);
            }
        }


    }

}
