package com.shuyun.query.process;


import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.shuyun.query.meta.EsQueryConf;
import com.shuyun.query.result.EsResultSet;

import java.util.List;

public class FilterDimensionTransformer implements Function<String, Object> {

	public FilterDimensionTransformer(EsResultSet row, String dataSource) {
		this.row = row;
		this.dataSource = dataSource;
	}

	final EsResultSet row;
	final String dataSource;

	@Override
	public Object apply( String input) {
		if(!row.getFields().keySet().contains(input)){
			return "null";
		}
		Object res = row.getFields().get(input).get(0);

		// 如果没有值，则设置为默认值
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
		}/*else{
		List ress = (List)res;
		res = Joiner.on(" && ").join(ress);
		}*/
		return  res;
	}
}
