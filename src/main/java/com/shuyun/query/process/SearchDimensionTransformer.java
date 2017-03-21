package com.shuyun.query.process;


import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.shuyun.query.meta.EsQueryConf;
import com.shuyun.query.result.EsResultSetForSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchDimensionTransformer implements Function<String, Object> {

	public SearchDimensionTransformer(EsResultSetForSource row, String dataSource) {
		this.row = row;
		this.dataSource = dataSource;
	}

	final EsResultSetForSource row;
	final String dataSource;

	@Override
	public Object apply( String input) {
		Object res = null;
		if(EsQueryConf.getDataSourceInstance(dataSource).getNeedSource().size() != 0 && EsQueryConf.getDataSourceInstance(dataSource).getNeedSource().contains(input)){
			String[] str = input.split("\\.");
			if(row.get_source().get(str[0]) == null){
				if(EsQueryConf.getDataSourceInstance(dataSource).getStringDimensions().contains(input)){
					res = "";
					return  res;
				}
				else if(EsQueryConf.getDataSourceInstance(dataSource).getNumericalDimensions().contains(input)){
					res = "0";
					return  res;
				}
				else if(EsQueryConf.getDataSourceInstance(dataSource).getDecimalDimensions().contains(input)){
					res = "0.00";
					return  res;
				}
				else if(EsQueryConf.getDataSourceInstance(dataSource).getDate().contains(input)){
					res = "0000-00-00";
					return  res;
				}
			}
			List rows = str.length == 2 ? (List)row.get_source().get(str[0]) : (List)((Map)row.get_source().get(str[0])).get(str[1]);
			List results = new ArrayList();
			for(Object object: rows){
				results.add(((Map)object).get(str[str.length-1]));
			}

			res = Joiner.on(" && ").join(results);
		}else{
			res = row.get_source().get(input);
			if(res == null){
				if(EsQueryConf.getDataSourceInstance(dataSource).getStringDimensions().size() != 0 && EsQueryConf.getDataSourceInstance(dataSource).getStringDimensions().contains(input)){
					res = "null";
					return  res;
				}
				else if(EsQueryConf.getDataSourceInstance(dataSource).getNumericalDimensions().size() != 0 && EsQueryConf.getDataSourceInstance(dataSource).getNumericalDimensions().contains(input)){
					res = "null";
					return  res;
				}
				else if(EsQueryConf.getDataSourceInstance(dataSource).getDecimalDimensions().size() != 0 && EsQueryConf.getDataSourceInstance(dataSource).getDecimalDimensions().contains(input)){
					res = "null";
					return  res;
				}
				else if(EsQueryConf.getDataSourceInstance(dataSource).getDate().size() != 0 && EsQueryConf.getDataSourceInstance(dataSource).getDate().contains(input)){
					res = "null";
					return  res;
				}
			}
		}
		return  res;
	}
}
