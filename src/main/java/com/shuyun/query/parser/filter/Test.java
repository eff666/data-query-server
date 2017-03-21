/*
package com.shuyun.query.parser.filter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuyun.query.parser.*;
import com.shuyun.query.parser.search.RegexpFilter;
import com.shuyun.query.parser.search.StrMatch;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.util.*;

public class Test {
	private static final String REGEXPSTR = "(.){%s}%s.*";


	public static void main(String[] args) throws Exception {

		ObjectMapper mapper = new ObjectMapper();

		*/
/*Object obj = mapper.readValue(
				"{\"type\":\"and\",\"fields\":[{\"type\":\"or\",\"fields\":[{\"type\":\"num_selector\",\"dimension\":\"col\",\"value\":3},{\"type\":\"str_selector\",\"dimension\":\"col\",\"value\":\"3\"},{\"type\":\"regexp\",\"dimension\":\"col\",\"pattern\":\"^hello\"},{\"type\":\"gt\",\"dimension\":\"col\",\"value\":3},{\"type\":\"gte\",\"dimension\":\"col\",\"value\":3},{\"type\":\"lt\",\"dimension\":\"col\",\"value\":3},{\"type\":\"lte\",\"dimension\":\"col\",\"value\":3},{\"type\":\"eq\",\"dimension\":\"col\",\"value\":3},{\"type\":\"neq\",\"dimension\":\"col\",\"value\":3},{\"type\":\"num_in\",\"dimension\":\"col\",\"value\":[1,2,3]},{\"type\":\"num_not_in\",\"dimension\":\"col\",\"value\":[1,2,3]},{\"type\":\"str_in\",\"dimension\":\"col\",\"value\":[\"1\",\"2\",\"3\"]},{\"type\":\"str_not_in\",\"dimension\":\"col\",\"value\":[\"1\",\"2\",\"3\"]},{\"type\":\"function\",\"dimension\":\"col\",\"function\":\"substr\",\"args\":[1,4],\"value\":1234}]},{\"type\":\"not\",\"field\":{\"type\":\"str_selector\",\"dimension\":\"col\",\"value\":\"3\"}}]}",
				new TypeReference<DimFilter>() {
				});*//*


		String query = "{\"settings\":{\"query_id\":\"af65bc83-f8b4-4cba-b322-3dcce18cd042\",\"data_source\":\"item\",\"pagination\":{\"offset\":0,\"limit\":10},\"return_format\":\"json\"},\"filters\":{\"type\":\"and\",\"fields\":[{\"dimension\":\"shop_id\",\"type\":\"str_selector\",\"value\":\"59568783\"},{\"type\":\"or\",\"fields\":[{\"dimension\":\"title\",\"type\":\"str_selector\",\"value\":\"韩国\"}]}]},\"sort\":[{\"orderBy\":\"created\",\"order\":\"desc\"}]}";


		*/
/*String query = "{\"settings\":{\"data_source\":\"shuyun\",\"pagination\":{\"limit\":6,\"offset\":3},\"return_format\":\"json\"},\"fields\":[\"title\",\"shopId\"],\"filters\":{\"type\":\"and\",\"fields\":[{\"type\":\"or\",\"fields\":[{\"type\":\"eq\",\"dimension\":\"col\",\"value\":3},{\"type\":\"neq\",\"dimension\":\"col\",\"value\":3}]}]},\"sort\":[{\"orderBy\":\"itemOuterId\",\"order\":\"asc\"},{\"orderBy\":\"price\",\"order\":\"desc\"}]}";*//*

		JsonParser obj = mapper.readValue(query, new TypeReference<JsonParser>() {
		});

		Set<String> shop_ids = new LinkedHashSet<>();
		String str = getEsRequest(obj, shop_ids);
		System.out.println(str);
	}

	private static FilterBuilder getFilterDimentions(DimFilter filter, Set<String> shop_ids) {
		if(null == filter){
			return FilterBuilders.matchAllFilter();
		}

		System.out.println(filter.getClass().getSimpleName());
		if (filter instanceof Equal){
			Equal equal = (Equal)filter;
			if(!"shop_id".equalsIgnoreCase(equal.getDimension()))
				return FilterBuilders.termFilter(equal.getDimension(), equal.getValue());
			else{
				shop_ids.add("" + equal.getValue());
				return FilterBuilders.termFilter(equal.getDimension(), equal.getValue());
			}
		} else if (filter instanceof NotEqual){
			NotEqual notEqual = (NotEqual)filter;
			if(!"shop_id".equalsIgnoreCase(notEqual.getDimension()))
				return FilterBuilders.notFilter(FilterBuilders.termFilter(notEqual.getDimension(), notEqual.getValue()));
			else {
				shop_ids.add("" + notEqual.getValue());
				return FilterBuilders.notFilter(FilterBuilders.termFilter(notEqual.getDimension(), notEqual.getValue()));
			}
		} else if (filter instanceof StrSelector){
			StrSelector strSelector = (StrSelector)filter;
			if(!"shop_id".equalsIgnoreCase(strSelector.getDimension()))
				return FilterBuilders.queryFilter(QueryBuilders.matchPhraseQuery(strSelector.getDimension(), strSelector.getValue()));
			else {
				shop_ids.add(strSelector.getValue());
				return FilterBuilders.queryFilter(QueryBuilders.matchPhraseQuery(strSelector.getDimension(), strSelector.getValue()));
			}
		} else if (filter instanceof StrMatch){
			StrMatch strSelector = (StrMatch)filter;
			if(!"shop_id".equalsIgnoreCase(strSelector.getDimension()))
				return FilterBuilders.queryFilter(QueryBuilders.matchQuery(strSelector.getDimension(), strSelector.getValue()));
			else {
				shop_ids.add(strSelector.getValue());
				return FilterBuilders.queryFilter(QueryBuilders.matchQuery(strSelector.getDimension(), strSelector.getValue()));
			}
		} else if (filter instanceof GreaterOrEqual){
			GreaterOrEqual gEqual = (GreaterOrEqual)filter;
			return FilterBuilders.rangeFilter(gEqual.getDimension()).gte(gEqual.getValue());
		} else if (filter instanceof GreaterThan){
			GreaterThan gt = (GreaterThan)filter;
			return FilterBuilders.rangeFilter(gt.getDimension()).gt(gt.getValue());
		} else if (filter instanceof LittleOrEqual){
			LittleOrEqual tEqual = (LittleOrEqual)filter;
			return FilterBuilders.rangeFilter(tEqual.getDimension()).lte(tEqual.getValue());
		} else if (filter instanceof LittleThan){
			LittleThan tthan = (LittleThan)filter;
			return FilterBuilders.rangeFilter(tthan.getDimension()).lt(tthan.getValue());
		} else if (filter instanceof NumIn){
			NumIn numIn = (NumIn)filter;
			return FilterBuilders.termsFilter(numIn.getDimension(), numIn.getValue());
		} else if (filter instanceof NumNotIn){
			NumNotIn numIn = (NumNotIn)filter;
			return FilterBuilders.notFilter(FilterBuilders.termsFilter(numIn.getDimension(), numIn.getValue()));
		} else if (filter instanceof StrIn){
			StrIn StrIn = (StrIn)filter;
			FilterBuilder[] filters = new FilterBuilder[StrIn.getValue().size()];
			int i = 0;
			for(String str : StrIn.getValue()) {
				filters[i] = FilterBuilders.queryFilter(QueryBuilders.matchPhraseQuery(StrIn.getDimension(), str));
				i++;
			}
			return FilterBuilders.boolFilter().should(filters);
		} else if (filter instanceof StrNotIn){
			StrNotIn strNotIn = (StrNotIn)filter;
			FilterBuilder[] filters = new FilterBuilder[strNotIn.getValue().size()];
			int i = 0;
			for(String str : strNotIn.getValue()) {
				filters[i] = FilterBuilders.queryFilter(QueryBuilders.matchPhraseQuery(strNotIn.getDimension(), str));
				i++;
			}
			return FilterBuilders.boolFilter().mustNot(filters);
		} else if (filter instanceof RegexpFilter){
			RegexpFilter regexpFilter = (RegexpFilter)filter;
			return FilterBuilders.regexpFilter(regexpFilter.getDimension(), regexpFilter.getPattern());
		} else if (filter instanceof NotDimFilter){
			NotDimFilter notDimFilter = (NotDimFilter)filter;
			return FilterBuilders.notFilter(getFilterDimentions(notDimFilter.getField(), shop_ids));
		}else if (filter instanceof AndDimFilter){
			AndDimFilter andFilter = (AndDimFilter)filter;
			List<FilterBuilder> fbuilds = new ArrayList<FilterBuilder>();
			for(DimFilter filter1 : andFilter.getFields()){
				fbuilds.add(getFilterDimentions(filter1, shop_ids));
			}
			return FilterBuilders.andFilter(fbuilds.toArray(new FilterBuilder[fbuilds.size()]));
		} else if (filter instanceof OrDimFilter){
			OrDimFilter orFilter = (OrDimFilter)filter;
			List<FilterBuilder> fbuilds = new ArrayList<FilterBuilder>();
			for(DimFilter filter2 : orFilter.getFields()){
				fbuilds.add(getFilterDimentions(filter2, shop_ids));
			}
			return FilterBuilders.orFilter(fbuilds.toArray(new FilterBuilder[fbuilds.size()]));
		} else{
			String errorMsg = String.format("filter %s type %s is not supported", filter, filter.getClass());
			throw new RuntimeException(errorMsg);
		}

	}

	private static String getEsRequest(JsonParser jsonParser, Set<String> shop_ids){
		SearchSourceBuilder sb = SearchSourceBuilder.searchSource();


		if(jsonParser.getFields() != null && jsonParser.getFields().size()>0){
			for(String field : jsonParser.getFields()){
				sb.field(field);
			}
		}

		// 此处为过滤，目前实现为filter，还有search需要适配
		FilterBuilder fbuild = getFilterDimentions(jsonParser.getFilters(), shop_ids);

		sb.query(new FilteredQueryBuilder(QueryBuilders.matchAllQuery(), fbuild));
		sb.from(jsonParser.getSettings().getPagination().getOffset());
		sb.size(jsonParser.getSettings().getPagination().getLimit() > 200 ? 200 : jsonParser.getSettings().getPagination().getLimit());

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
		return sb.toString();
	}
}
*/
