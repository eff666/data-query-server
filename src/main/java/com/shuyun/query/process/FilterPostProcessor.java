package com.shuyun.query.process;


import com.google.common.collect.Iterables;
import com.shuyun.query.meta.ReportPage;
import com.shuyun.query.meta.ReportResultForEs;
import com.shuyun.query.result.EsResultSet;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;


public class FilterPostProcessor extends PostProcessor {

	public FilterPostProcessor(QueryContext queryContext) {
		super(queryContext);
	}

	@Override
	public ReportResultForEs process(List<?> input) {

		ReportResultForEs reportResult = new ReportResultForEs();

		reportResult.setFlag("success");
		reportResult.setMsg("ok");

		List<EsResultSet> rows = (List<EsResultSet>) input;
		
		reportResult.setPage(new ReportPage(0, 0));

		HashMap<String, String> map = new HashMap<String, String>();
		/*if(parser.getSort() != null && parser.getSort().size()>0){
			for(Map<String, String> maps : parser.getSort()){
				map.put(maps.get("orderBy"), maps.get("order"));
			}
		}*/

//		LinkedHashSet<String> sortFields = new LinkedHashSet(map.keySet());
		reportResult.append(Iterables.toArray(Iterables.concat(queryContext.getJsonParser().getFields()), String.class));

		for (final EsResultSet r : rows) {
			reportResult.append(Iterables.toArray(
					Iterables.concat(
							Iterables.transform(queryContext.getJsonParser().getFields(), new FilterDimensionTransformer(r, queryContext.getJsonParser().getSettings().getData_source()))), Object.class));
		}
		return reportResult;
	}
}
